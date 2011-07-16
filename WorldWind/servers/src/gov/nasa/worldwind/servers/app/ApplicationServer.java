/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.app;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class ApplicationServer
{
    private static final ApplicationServer instance = new ApplicationServer();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private static final ExecutorService clientSocketPool = Executors.newCachedThreadPool();

    private static final Hashtable<Integer, SocketListener> socketlisteners =
            new Hashtable<Integer, SocketListener>();

    private static final Hashtable<Integer, ServerApplication> registeredListeners =
            new Hashtable<Integer, ServerApplication>();

    private static final Hashtable<ServerApplication, ApplicationPool> applicationPools =
            new Hashtable<ServerApplication, ApplicationPool>();

    private static AVList configuration = new AVListImpl();



    private ApplicationServer()
    {
    }

    public static AVList getConfiguration()
    {
        return configuration.copy();
    }

    public static ApplicationServer getInstance()
    {
        return instance;
    }

    public static String getTempDirectory()
    {
        String defaultTempFolder = System.getProperty("java.io.tmpdir");
        return AVListImpl.getStringValue(configuration, AVKey.SERVER_TEMP_DIRECTORY, defaultTempFolder );
    }

    public void stop()
    {
        this.isRunning.set(false);
    }

    public void waitToFinish()
    {
        try
        {
            this.isRunning.set(true);

            while (!Thread.currentThread().isInterrupted() && this.isRunning.get())
            {
                if (socketlisteners.size() == 0 || registeredListeners.size() == 0)
                {
                    String reason = Logging.getMessage("WMS.Server.NoActiveApplicationsDetected");
                    String message = Logging.getMessage("WMS.Server.ShuttingDown", reason);
                    Logging.logger().severe(message);
                    break;
                }

                Thread.sleep(1000);
            }
        }
        catch (InterruptedException ie)
        {
        }
        finally
        {
            this.isRunning.set(false);

            if (Thread.currentThread().isInterrupted())
            {
                Thread.interrupted();
            }
        }
    }

    public synchronized void loadApplications(File webAppConfigFile) throws IllegalArgumentException, IOException
    {
        if (null == webAppConfigFile)
        {
            String message = Logging.getMessage("nullValue.FileIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!webAppConfigFile.exists())
        {
            String message = Logging.getMessage("generic.FileNotFound", webAppConfigFile.getAbsolutePath());
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!webAppConfigFile.canRead())
        {
            String message = Logging.getMessage("generic.FileNoReadPermission", webAppConfigFile.getAbsolutePath());
            Logging.logger().severe(message);
            throw new IOException(message);
        }

        parseWebAppConfigFile(webAppConfigFile);
    }

    protected static void parseWebAppConfigFile(File webAppConfigFile)
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream(webAppConfigFile);

            DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docfac.newDocumentBuilder();

            Document doc = builder.parse(is);

            XPathFactory xpfac = XPathFactory.newInstance();
            XPath xpath = xpfac.newXPath();

            Node root = (Node) xpath.evaluate("/ApplicationServer", doc, XPathConstants.NODE);
            if (null == root)
            {
                String msg = Logging.getMessage("WMS.Server.InvalidConfigFile", webAppConfigFile.getAbsolutePath());
                Logging.logger().severe(msg);
                throw new WWRuntimeException(msg);
            }

            NodeList config = (NodeList) xpath.evaluate("/ApplicationServer/Configuration", root, XPathConstants.NODESET);
            if (null != config && config.getLength() > 0)
            {
//                String msg = Logging.getMessage("WMS.Server.NoConfigurationDefined", webAppConfigFile.getAbsolutePath());
//                Logging.logger().severe(msg);
//                throw new IOException(msg);
                for (int i = 0; i < config.getLength(); i++)
                {
                    try
                    {
                        configuration.setValues(readProperties(xpath, config.item(i)));
                    }
                    catch (Exception ex)
                    {
                        Logging.logger().severe(ex.getMessage());
                    }
                }

                verifyConfigurationParameters();
            }

            NodeList apps = (NodeList) xpath.evaluate("/ApplicationServer/Application", root, XPathConstants.NODESET);
            if (null == apps || 0 == apps.getLength())
            {
                String msg = Logging.getMessage("WMS.Server.NoApplicationDefined", webAppConfigFile.getAbsolutePath());
                Logging.logger().severe(msg);
                throw new WWRuntimeException(msg);
            }

            for (int i = 0; i < apps.getLength(); i++)
            {
                try
                {
                    Node app = apps.item(i);

                    AVList params = readProperties(xpath, app);

                    String className = params.hasKey(AVKey.SERVER_APP_CLASS_NAME)
                                       ? params.getStringValue(AVKey.SERVER_APP_CLASS_NAME) : null;

                    if (WWUtil.isEmpty(className))
                    {
                        String msg = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SERVER_APP_CLASS_NAME);
                        Logging.logger().severe(msg);
                        throw new WWRuntimeException(msg);
                    }

                    Object o = WorldWind.createComponent(className);
                    if (WWUtil.isEmpty(o))
                    {
                        String msg = Logging.getMessage("WMS.Server.CannotStartApplication", className);
                        Logging.logger().severe(msg);
                        throw new WWRuntimeException(msg);
                    }

                    if (!(o instanceof ServerApplication))
                    {
                        String msg = Logging.getMessage("WMS.Server.CannotStartApplication", className);
                        Logging.logger().severe(msg);
                        throw new WWRuntimeException(msg);
                    }

                    ServerApplication srvApp = (ServerApplication) o;
                    srvApp.setValues(params);

                    String msg = Logging.getMessage("WMS.Server.ApplicationIsStartingUp", srvApp.getName());
                    Logging.logger().info(msg);

                    srvApp.start();

                    ApplicationServer.register(srvApp);

                    if (o instanceof BasicServerApplication)
                    {
                        BasicServerApplication.ApplicationState state =
                                ((BasicServerApplication) o).getApplicationState();

                        msg = Logging.getMessage("WMS.Server.ApplicationState", srvApp.getName(), state.name());
                        Logging.logger().info(msg);
                    }
                    else
                    {
                        msg = Logging.getMessage("WMS.Server.ApplicationIsActive", srvApp.getName());
                        Logging.logger().info(msg);
                    }
                }
                catch (WWRuntimeException wwe)
                {
                    // noting to do here because WWRuntimeException is always logged before it is thrown
                }
                catch (Exception ex)
                {
                    Logging.logger().severe(WWUtil.extractExceptionReason(ex));
                }
            }
        }
        catch (WWRuntimeException wwe)
        {
            // noting to do here because WWRuntimeException is always logged before it is thrown
        }
        catch (Exception e)
        {
            String message = Logging.getMessage("generic.ExceptionWhileReading", e.getMessage());
            Logging.logger().severe(message + " [" + webAppConfigFile.getAbsolutePath() + "]");
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                Logging.logger().finest(e.getMessage());
            }
        }
    }

    protected static AVList readProperties(XPath xpath, Node node) throws Exception
    {
        AVList params = new AVListImpl();

        NodeList props = (NodeList) xpath.evaluate("./property", node, XPathConstants.NODESET);
        if (props.getLength() > 0)
        {
            for (int j = 0; j < props.getLength(); j++)
            {
                Node prop = props.item(j);
                String name = xpath.evaluate("@name", prop);
                String value = xpath.evaluate("@value", prop);

                if (WWUtil.isEmpty(name))
                {
                    String message = Logging.getMessage("WMS.Server.InvalidNameValueProperty", name, value, "web.xml");
                    Logging.logger().severe(message);
                }
                else
                {
                    params.setValue(name, value);
                }
            }
        }

        return params;
    }

    public synchronized static void register(ServerApplication app) throws IllegalArgumentException, WWRuntimeException
    {
        if (null == app)
        {
            String message = Logging.getMessage("nullValue.ServerApplicationIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            SocketListener listener = null;

            int port = app.getPort();

            for (Integer sockPort : socketlisteners.keySet())
            {
                if (null != sockPort && sockPort == port)
                {
                    // existing port listener found
                    listener = socketlisteners.get(sockPort);
                    break;
                }
            }

            if (null == listener)
            {
                socketlisteners.put(port, SocketListener.listen(port, app.getProtocol()));
            }

            if (registeredListeners.containsKey(port))
            {
                String message = Logging.getMessage("WMS.Server.DuplicateServerApplicationFound", port);
                Logging.logger().fine(message);
                throw new IllegalArgumentException(message);
            }

            registeredListeners.put(port, app);

            if (!applicationPools.containsKey(app))
            {
                ApplicationPool pool = null;

                if (app.hasKey(AVKey.SERVER_APPLICATION_POOL_SIZE))
                {
                    try
                    {
                        int size = Integer.parseInt("" + app.getValue(AVKey.SERVER_APPLICATION_POOL_SIZE));
                        pool = new ApplicationPool(size);
                    }
                    catch (Exception e)
                    {
                        Logging.logger().finest(e.getMessage());
                        pool = null;
                    }
                }

                if (null == pool)
                {
                    // Create an application pool with default size
                    pool = new ApplicationPool();
                }

                String message = Logging.getMessage("WMS.Server.ApplicationRegistered", app.getName(), port,
                        app.getVirtualDirectory());
                Logging.logger().info(message);

                applicationPools.put(app, pool);
            }
        }
        catch (BindException be)
        {
            Logging.logger().fine(WWUtil.extractExceptionReason(be));

            String reason = Logging.getMessage("WMS.Server.CannotCreateSocket", app.getPort());
            String message = Logging.getMessage("WMS.Server.CannotRegisterApplication", app.getName());
            Logging.logger().severe(message + ": " + reason);

            throw new WWRuntimeException(message, be);
        }
        catch (WWRuntimeException wwe)
        {
            String reason = WWUtil.extractExceptionReason(wwe);
            String message = Logging.getMessage("WMS.Server.CannotRegisterApplication", app.getName());
            Logging.logger().severe(message + ": " + reason);

            throw new WWRuntimeException(message, wwe);
        }
        catch (Throwable t)
        {
            String reason = WWUtil.extractExceptionReason(t);
            String message = Logging.getMessage("WMS.Server.CannotRegisterApplication", app.getName());
            Logging.logger().severe(message + ": " + reason);

            throw new WWRuntimeException(message, t);
        }
    }

    public static void dispatch(Socket socket)
    {
        clientSocketPool.execute(new ClientSocketTask(socket));
    }


    protected static void verifyConfigurationParameters() throws IllegalArgumentException, IOException
    {
        // make sure working directory exists and we can write to it...
        Object o = configuration.getValue(AVKey.SERVER_TEMP_DIRECTORY);
        if (null != o)
        {
            File workDir = (o instanceof String) ? new File((String)o) : WWIO.getFileForLocalAddress(o);
            if (null == workDir)
            {
                Logging.logger().warning(Logging.getMessage("WMS.Config.InvalidParameter", AVKey.SERVER_TEMP_DIRECTORY));
            }
            else if ((workDir.exists() && !workDir.canWrite()) || (!workDir.exists()) && !workDir.mkdirs())
            {
                Logging.logger().warning(Logging.getMessage("generic.FolderNoWritePermission", workDir.getAbsolutePath()));
            }
            else if (!workDir.exists())
            {
                Logging.logger().warning(Logging.getMessage("generic.FolderDoesNotExist", workDir.getAbsolutePath()));
            }
            else
            {
                configuration.setValue( AVKey.SERVER_TEMP_DIRECTORY, workDir.getAbsolutePath() );
            }
        }

        try
        {
            /* Check GDAL path */
            GDALUtils.getGDAL();
        }
        catch (Exception e)
        {
            String message = Logging.getMessage("gdal.GDALUtilitiesPathNotConfigured") + ": (" + AVKey.GDAL_PATH + ")";
            Logging.logger().warning(message);
        }
    }

    static final class ClientSocketTask implements Runnable
    {
        protected Socket socket;

        ClientSocketTask(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            try
            {
                if (this.socket != null && this.socket.isConnected())
                {
                    int port = this.socket.getLocalPort();
                    // check if any app is listening on the port
                    if (!registeredListeners.containsKey(port))
                    {
                        String message = Logging.getMessage("WMS.Server.ApplicationNotFound", port);
                        Logging.logger().info(message);
                        throw new RuntimeException(message);
                    }

                    ServerApplication app = registeredListeners.get(port);

                    ApplicationPool pool = null;

                    if (applicationPools.containsKey(app))
                    {
                        pool = applicationPools.get(app);
                    }

                    if (null == pool)
                    {
                        String message = Logging.getMessage("WMS.Server.ApplicationPoolNotFound", app.getName());
                        Logging.logger().info(message);
                        throw new RuntimeException(message);
                    }

                    pool.execute(this.socket, app);
                }
                else
                {
                    String details = (null != this.socket) ? this.socket.toString() : "ClientSocketTask.run()";
                    String message = Logging.getMessage("WMS.Server.ClientClosedConnection", details);
                    Logging.logger().info(message);
                }
            }
            catch (Exception e)
            {
                String message = Logging.getMessage("WMS.Server.InternalError", e.getMessage());
                Logging.logger().finest(message);
            }
        }
    }
}
