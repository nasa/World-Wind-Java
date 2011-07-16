/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.app;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class ProxyServerWithAuthentication extends BasicHttpServerApplication
{
    protected String authToken = null;

    @SuppressWarnings({"UnusedDeclaration"})
    public ProxyServerWithAuthentication()
    {
        super();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public ProxyServerWithAuthentication(AVList config)
    {
        super(config);
    }

    @Override
    protected void doStart()
    {
        super.doStart();

        if (null == this.getProtocol())
            this.setValue(AVKey.PROTOCOL, AVKey.PROTOCOL_HTTPS);

        if (null == this.getVirtualDirectory())
        {

            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SERVER_VIRTUAL_DIRECTORY);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!this.hasKey(AVKey.SERVER_REDIRECT_TO))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SERVER_REDIRECT_TO);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.authToken = this.getStringValue(AVKey.AUTH_TOKEN);
        if (null == this.authToken || 0 == this.authToken.length())
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.AUTH_TOKEN);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    protected void doStop()
    {
        super.doStop();
    }

    @Override
    protected void doGet(HTTPRequest req, HTTPResponse resp)
    {
        String token = req.getParameter("authToken");
        if( !authToken.equals( token ) )
        {
            String message = Logging.getMessage("WMS.Security.AccessDenied",
                this.getVirtualDirectory(), AVKey.AUTH_TOKEN );
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        // TODO zz: garakl: implement proxy service
    }
}
