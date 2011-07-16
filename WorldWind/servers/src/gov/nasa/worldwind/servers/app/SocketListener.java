/*
Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.app;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.util.Logging;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class SocketListener extends Thread
{
    protected int port = 0;
    protected ServerSocket socket = null;
    protected String protocol = AVKey.PROTOCOL_HTTP;

    protected SocketListener(int port, String protocol) throws IOException, IllegalArgumentException
    {
        if (port <= 0)
        {
            String message = Logging.getMessage("WMS.Server.CannotCreateSocket", port);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.port = port;

        if (null == protocol)
        {
            String msg = Logging.getMessage("nullValue.ProtocolIsNull", protocol);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!protocol.equalsIgnoreCase(AVKey.PROTOCOL_HTTP) && !protocol.equalsIgnoreCase(AVKey.PROTOCOL_HTTPS))
        {
            String msg = Logging.getMessage("WMS.Server.UnknownProtocol", protocol);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.protocol = protocol;

        this.connect();

        this.start();
    }

    protected void connect() throws IOException, IllegalArgumentException
    {
        if (this.protocol.equalsIgnoreCase(AVKey.PROTOCOL_HTTP))
        {
            this.socket = new ServerSocket(this.port);
        }
        else if (this.protocol.equalsIgnoreCase(AVKey.PROTOCOL_HTTPS))
        {
            this.socket = this.createSSLSocket(ApplicationServer.getConfiguration());
        }
        else
        {
            String msg = Logging.getMessage("WMS.Server.UnknownProtocol", protocol);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (null != this.socket && this.socket.isBound())
        {
            String s = this.socket.getInetAddress() + ":" + this.socket.getLocalPort();
            String msg = Logging.getMessage("WMS.Server.SocketBound", s);
            Logging.logger().info(msg);
        }
        else
        {
            String msg = Logging.getMessage("WMS.Server.CannotCreateSocket", this.port);
            throw new IOException(msg);
        }
    }

    protected void reconnect()
    {
        try
        {
            this.closeSocket();
            this.connect();
        }
        catch (Throwable t)
        {
            Logging.logger().severe(t.getMessage());
        }
    }

    protected void closeSocket()
    {
        try
        {
            if (null != this.socket)
            {
                if (!this.socket.isClosed())
                    this.socket.close();

                this.socket = null;
            }
        }
        catch (Throwable t)
        {
            Logging.logger().severe(t.getMessage());
        }
    }

    protected ServerSocket createSSLSocket(AVList config) throws IllegalArgumentException, IOException
    {
        this.verifyKeystoreParameters(config);
        try
        {
            // TODO zz: garakl:
            SSLContext sslCtx = SSLContext.getDefault();
            return sslCtx.getServerSocketFactory().createServerSocket(this.port);
        }
        catch (Exception e)
        {
            String message = Logging.getMessage("WMS.Server.CannotCreateSocket", this.port);
            Logging.logger().severe(message);
            throw new IOException(message);
        }
    }

    protected void verifyKeystoreParameters(AVList config) throws IllegalArgumentException
    {
        if (null == config)
        {
            String message = Logging.getMessage("nullValue.AVListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!config.hasKey(AVKey.KEYSTORE_PATH))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.KEYSTORE_PATH);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!config.hasKey(AVKey.KEYSTORE_USER))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.KEYSTORE_USER);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!config.hasKey(AVKey.KEYSTORE_PASSWORD))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.KEYSTORE_PASSWORD);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!config.hasKey(AVKey.SERVER_CERTIFICATE))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SERVER_CERTIFICATE);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // TODO zz: garakl: check
        // if JKS exists, if valid JKS user and password, if JKS has the certificate
    }

    public static SocketListener listen(int port, String protocol) throws IOException
    {
        return new SocketListener(port, protocol);
    }

    @Override
    public void run()
    {
        try
        {
            // Open a port and listen indefinitely for requests...
            for (; ;)
            {
                try
                {
                    Socket clientSocket = this.socket.accept();
                    ApplicationServer.dispatch(clientSocket);
                }
                catch (Exception e)
                {
                    if (this.isInterrupted())
                    {
                        String msg = Logging.getMessage("WMS.Server.SocketListenerInterrupted", this.port);
                        Logging.logger().log(Level.INFO, msg, e);
                        this.interrupt();
                        break;
                    }
                    else
                    {
                        String msg = Logging.getMessage("WMS.Server.SocketError", e.toString());
                        Logging.logger().log(Level.SEVERE, msg, e);

                        this.reconnect();
                    }
                }
            }
        }
        finally
        {
            this.closeSocket();
        }
    }
}
