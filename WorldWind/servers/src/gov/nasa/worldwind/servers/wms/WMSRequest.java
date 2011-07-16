/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.servers.app.*;
import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.util.Logging;

import java.io.IOException;

/**
 * @author brownrigg
 * @version $Id$
 */
public abstract class WMSRequest
{
    protected HTTPRequest request;

    protected WMSRequest(HTTPRequest request)
    {
        this.request = request;
    }

    public HTTPRequest getHttpRequest()
    {
        return this.request;
    }

    abstract public void service(HTTPRequest req, HTTPResponse resp) throws IOException, WMSServiceException;

    public WMSServerApplication getApplicationContext() throws WMSServiceException
    {
        ServerApplication app = this.request.getServerApplication();
        if( null == app || !(app instanceof WMSServerApplication) )
        {
            String msg = Logging.getMessage( "WMS.Server.ApplicationNotFound" );
            Logging.logger().severe(msg);
            throw new WMSServiceException( msg );
        }
        return (WMSServerApplication)app;
    }

    public Configuration getConfigurationContext() throws WMSServiceException
    {
        WMSServerApplication app = this.getApplicationContext();
        Configuration config = app.getConfiguration();
        if(null == config)
        {
            String msg = Logging.getMessage( "nullValue.ConfigurationIsNull" );
            Logging.logger().severe(msg);
            throw new WMSServiceException( msg );
        }
        return  config;
    }
}
