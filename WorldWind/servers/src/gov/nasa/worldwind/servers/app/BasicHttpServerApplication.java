/* Copyright (C) 2001, 2010 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */

package gov.nasa.worldwind.servers.app;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.servers.wms.security.AccessDeniedException;
import gov.nasa.worldwind.servers.wms.security.WMSSecurity;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Lado Garakanidze
 * @version $
 */

public class BasicHttpServerApplication extends BasicServerApplication
{
    public BasicHttpServerApplication()
    {
        super();
    }

    public BasicHttpServerApplication(AVList config)
    {
        super(config);
    }

    protected void doService(Socket socket)
    {
        HTTPRequest req = null;
        HTTPResponse resp = null;

        try
        {
            req = new HTTPRequest(socket, this);
            resp = new HTTPResponse(socket);

            WMSSecurity.getSecurityManager().allow(req);

            // TODO garakl
            // this breaks old hack with "/wms" (for imagery requests) and "/elev" (for elevation requests)
//
//            String urlPath = this.getVirtualDirectory();
//            if (!WWUtil.isEmpty(urlPath) && !urlPath.equalsIgnoreCase(req.getUrl()))
//            {
//                String msg = Logging.getMessage("WMS.Server.UnexpectedPath", req.getUrl(), urlPath);
//                throw new SecurityException(msg);
//            }

            String method = req.getMethod();
            if (HTTP.isGET(method))
            {
                this.doGet(req, resp);
            }
            else if (HTTP.isPOST(method))
            {
                this.doPost(req, resp);
            }
            else
            {
                String message = Logging.getMessage("WMS.Server.BadRequest", method);
                Logging.logger().finest(message);
            }

            if (socket.isConnected())
            {
                resp.flushBuffer();
            }
        }
        catch (AccessDeniedException denied)
        {
            String reason = denied.getMessage();
            Logging.logger().finest(reason);
            this.sendRedirectToClient(resp, "http://" + denied.getAddress() + "/", reason);
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
        catch (WWRuntimeException wwe)
        {
            this.sendExceptionToClient(resp, wwe.getMessage());
        }
        catch (IOException ioe)
        {
            Logging.logger().finest(WWUtil.extractExceptionReason(ioe));
        }
        catch (Throwable t)
        {
            String reason = WWUtil.extractExceptionReason(t);
            Logging.logger().log(java.util.logging.Level.FINEST, reason, t);
            this.sendExceptionToClient(resp, reason);
        }
    }

    @SuppressWarnings( {"UnusedDeclaration"})
    protected void doPost(HTTPRequest req, HTTPResponse resp)
    {
        String message = Logging.getMessage("generic.FeatureNotImplemented", "POST");
        Logging.logger().finest(message);
        throw new WWRuntimeException(message);
    }

    @SuppressWarnings( {"UnusedDeclaration"})
    protected void doGet(HTTPRequest req, HTTPResponse resp)
    {
        String message = Logging.getMessage("generic.FeatureNotImplemented", "GET");
        Logging.logger().finest(message);
        throw new WWRuntimeException(message);
    }

    protected void sendExceptionToClient(HTTPResponse resp, String msg)
    {
        this.sendHttpErrorToClient(HTTPResponse.SERVER_ERROR, resp, msg);
    }

    protected void sendHttpErrorToClient(int httpErrorCode, HTTPResponse resp, String msg)
    {
        if (null == resp)
        {
            String message = Logging.getMessage("nullValue.ResponseIsNull");
            Logging.logger().severe(message);
            return;
        }

        try
        {
            byte[] msgBytes = ((null != msg) ? msg : "Unknown error").getBytes();

            resp.setStatus(httpErrorCode);
            resp.setContentLength(msgBytes.length);
            resp.setContentType("text");

            OutputStream out = resp.getOutputStream();
            out.write(msgBytes);
        }
        catch (Exception e)
        {
            Logging.logger().severe(e.getMessage());
        }
    }

    protected void sendRedirectToClient(HTTPResponse resp, String dest, String msg)
    {
        if (null == resp)
        {
            String message = Logging.getMessage("nullValue.ResponseIsNull");
            Logging.logger().severe(message);
            return;
        }

        try
        {
            resp.setContentType("text/plain");
            resp.setStatus(HTTPResponse.TEMP_REDIRECT);
            resp.addHeader("Location", dest);
            resp.flushBuffer();
            Logging.logger().info("Redirecting to " + dest + ", reason: " + msg);
        }
        catch (Exception ex)
        {
            Logging.logger().severe(ex.getMessage());
        }
    }
}
