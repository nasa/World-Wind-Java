/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.app;

import gov.nasa.worldwind.Factory;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.cache.BasicDataFileStore;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.formats.tiff.GeotiffImageReaderSpi;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;
import gov.nasa.worldwind.servers.tools.xml.XMLWriter;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;

import javax.imageio.spi.IIORegistry;
import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class WMSServerApplication extends BasicHttpServerApplication
{
    private MapRegistry mapRegistry = null;
    private gov.nasa.worldwind.servers.wms.Configuration configuration = null;
    private FileStore dataFileStore = null;
    private Factory mapSourceFactory = null;

    @SuppressWarnings( {"UnusedDeclaration"})
    public WMSServerApplication()
    {
        super();
        this.init();
    }

    @SuppressWarnings( {"UnusedDeclaration"})
    public WMSServerApplication(AVList config)
    {
        super(config);
        this.init();
    }

    private void init()
    {
        this.mapRegistry = new BasicMapRegistry(this);
        this.mapSourceFactory = new BasicMapSourceFactory(this);
    }

    @Override
    protected void doStart()
    {
        try
        {
            // make sure our home-grown TIFF reader is registered with the ImageIO API...
            IIORegistry r = IIORegistry.getDefaultInstance();
            r.registerServiceProvider(GeotiffImageReaderSpi.inst());

            this.readConfigurationFile();

            this.readMapSources();

            if (this.getConfiguration().runAutoDiscoveryTask())
            {
                // Create the WMS data file store.
                this.dataFileStore = this.locateDataFileStore();

                WMSDataDiscoveryThread discovery =
                    new WMSDataDiscoveryThread(this, dataFileStore, configuration.getDataFileStoreFilter());

                discovery.addWMSLayerEventListener((WMSLayerEventListener) mapRegistry);
                discovery.start();
            }
        }
        finally
        {
            this.setApplicationState(ApplicationState.Started);
        }
    }

    protected FileStore locateDataFileStore()
    {
        ArrayList<String> locations = new ArrayList<String>();

        FileStore fs = null;

        if (this.hasKey(AVKey.FILE_STORE))
        {
            Object o = this.getValue(AVKey.FILE_STORE);
            if (null != o)
            {
                if (o instanceof FileStore)
                {
                    return (FileStore) o;
                }
                else if (o instanceof String)
                {
                    locations.add((String) o);
                }
            }
        }

        if (this.hasKey(AVKey.FILE_STORE_LOCATION))
        {
            Object o = this.getValue(AVKey.FILE_STORE_LOCATION);
            if (null != o && o instanceof String)
            {
                locations.add((String) o);
            }
        }

        if (this.hasKey(AVKey.DATA_FILE_STORE_CONFIGURATION_FILE_NAME))
        {
            Object o = this.getValue(AVKey.DATA_FILE_STORE_CONFIGURATION_FILE_NAME);
            if (null != o && o instanceof String)
            {
                locations.add((String) o);
            }
        }

        String configFile = configuration.getDataFileStoreConfigurationFile();
        if (!WWUtil.isEmpty(configFile))
        {
            locations.add(configFile);
        }

        for (String location : locations)
        {
            try
            {
                if (WWUtil.isEmpty(location))
                {
                    continue;
                }

                File path = new File(location);
                if (path.isDirectory())
                {
                    fs = new BasicDataFileStore(path);
                }
                else
                {
                    path = (!path.exists()) ? FileUtil.locateConfigurationFile(location) : path;
                    fs = new WMSDataFileStore(path.getAbsolutePath());
                }

                if (null != fs)
                {
                    this.setValue(AVKey.FILE_STORE, fs);
                    break;
                }
            }
            catch (Throwable t)
            {
                String message = WWUtil.extractExceptionReason(t);
                Logging.logger().severe(message);
                fs = null;
            }
        }

        return fs;
    }

    @Override
    protected void doGet(HTTPRequest req, HTTPResponse resp)
    {
        try
        {
            if (this.getApplicationState().equals(ApplicationState.Started))
            {
                WMSRequest wmsReq = WMSRequestFactory.create(req);
                wmsReq.service(req, resp);

                Logging.logger().finest(req.toString());
            }
            else
            {
                String message = Logging.getMessage("generic.ServiceNotReady", this.getName());
                Logging.logger().finest(message);
                throw new WMSServiceException(message);
            }
        }
        catch (WMSServiceException wmse)
        {
            String reason = WWUtil.extractExceptionReason(wmse);
            this.sendExceptionToClient(resp, reason);
        }
        catch (WWRuntimeException wwe)
        {
            String reason = WWUtil.extractExceptionReason(wwe);
            this.sendExceptionToClient(resp, reason);
        }
        catch (SecurityException se)
        {
            String reason = WWUtil.extractExceptionReason(se);
            Logging.logger().finest(reason);
            this.sendExceptionToClient(resp, reason);
        }
        catch (SocketException se)
        {
            String reason = WWUtil.extractExceptionReason(se);
            String message = Logging.getMessage("WMS.Server.ClientClosedConnection", reason);
            Logging.logger().finest(message);
        }
        catch (Throwable t)
        {
            String reason = WWUtil.extractExceptionReason(t);
            Logging.logger().log(java.util.logging.Level.FINEST, reason, t);
            this.sendExceptionToClient(resp, reason);
        }
    }

    @Override
    protected void doStop()
    {
        super.doStop();
    }

    @Override
    protected void sendExceptionToClient(HTTPResponse resp, String msg)
    {
        this.writeWMSServiceException(resp, msg);
    }

    protected void writeWMSServiceException(HTTPResponse resp, String msg)
    {
        if (null == resp)
        {
            String message = Logging.getMessage("nullValue.ResponseIsNull");
            Logging.logger().severe(message);
            return;
        }

        try
        {
            Writer writer = new java.io.StringWriter();
            XMLWriter xmlwriter = new XMLWriter(writer);
            xmlwriter.addXmlHeader();
            xmlwriter.openElement("ServiceExceptionReport");
            xmlwriter.addAttribute("xmlns", "http://www.opengis.net/ogc");
            xmlwriter.addAttribute("xmlns:ns2", "http://www.opengis.net/wms");
            xmlwriter.addAttribute("xmlns:ns3", "http://www.w3.org/1999/xlink");
            xmlwriter.addElement("ServiceException", msg);
            xmlwriter.closeElement("ServiceExceptionReport");
            byte[] bytes = writer.toString().getBytes();

            resp.setStatus(HTTPResponse.BAD_REQUEST);
            resp.setContentType("application/vnd.ogc.se_xml");
            resp.setContentLength(bytes.length);

            OutputStream out = resp.getOutputStream();
            out.write(bytes);
        }
        catch (Exception ex)
        {
            Logging.logger().severe(ex.getMessage());
        }
    }

    public Configuration getConfiguration()
    {
        if (null == this.configuration)
        {
            this.readConfigurationFile();
        }

        return configuration;
    }

    public MapRegistry getMapSourceRegistry()
    {
        return mapRegistry;
    }

    public FileStore getDataFileStore()
    {
        return dataFileStore;
    }

    public Factory getMapSourceFactory()
    {
        return this.mapSourceFactory;
    }

    public String getOnlineResource()
    {
        if (!this.hasKey(AVKey.SERVER_CAPABILITIES_ONLINE_RESOURCE))
        {
            String message = Logging.getMessage("WMS.Server.Application.MissingRequiredParameter",
                AVKey.SERVER_CAPABILITIES_ONLINE_RESOURCE);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        return this.getStringValue(AVKey.SERVER_CAPABILITIES_ONLINE_RESOURCE);
    }

    public String getRedirectUrl()
    {
        if (!this.hasKey(AVKey.SERVER_REDIRECT_TO))
        {
            String message = Logging.getMessage("WMS.Server.Application.MissingRequiredParameter",
                AVKey.SERVER_REDIRECT_TO);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        return this.getStringValue(AVKey.SERVER_REDIRECT_TO);
    }

    protected void readMapSources()
    {
        for (MapSource mapSource : getConfiguration().getMapSources())
        {
            // initialize each MapSource's MapGenerator; add to registry only if successful...
            try
            {
                mapRegistry.add(mapSource);
            }
            catch (Exception ex)
            {
                Logging.logger().severe("Could not instantiate or initialize MapSource: " + ex.toString());
            }
        }
    }

    protected void readConfigurationFile()
    {
        // could return just a file name, or relative WEB-INF/config.xml,
        // or absolute path to the config file
        String s = this.getStringValue(AVKey.SERVER_APP_CONFIG_FILE);
        if (WWUtil.isEmpty(s))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SERVER_APP_CONFIG_FILE);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        File configFile = new File(s);
        if (!configFile.exists())
        {
            String userHomeFolder = System.getProperties().getProperty("user.dir");
            configFile = new File(userHomeFolder + File.separator + "WEB-INF" + File.separator + s);

            if (!configFile.exists())
            {
                configFile = new File(userHomeFolder + File.separator + s);
            }
        }

        if (!configFile.exists())
        {
            String message = Logging.getMessage("generic.FileNotFound", configFile.getAbsolutePath());
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!configFile.canRead())
        {
            String message = Logging.getMessage("generic.FileNoReadPermission", configFile.getAbsolutePath());
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        InputStream is = null;
        try
        {
            is = new FileInputStream(configFile);
            configuration = new Configuration(is);
        }
        catch (Exception e)
        {
            String message = Logging.getMessage("generic.ExceptionWhileReading", configFile.getAbsolutePath());
            Logging.logger().severe(message);
            Logging.logger().severe(e.getMessage());
            throw new RuntimeException(message);
        }
        finally
        {
            try
            {
                if (null != is)
                {
                    is.close();
                }
            }
            catch (Exception e)
            {
                Logging.logger().finest(e.getMessage());
            }
        }
    }
}

