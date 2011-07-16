/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.servers.app.*;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.util.Logging;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class WMSRequestFactory
{
    public static WMSRequest create(HTTPRequest req) throws WMSServiceException
    {
        String request = req.getParameter( WMS.Param.REQUEST.toString() );
        request = (request == null ) ? "" : request.trim();

        if (request.equalsIgnoreCase( WMS.Request.GetCapabilities.toString() ))
            return new WMSGetCapabilitiesRequest( req );

        else if( request.equalsIgnoreCase( WMS.Request.GetMap.toString()) )
            return new WMSGetMapRequest(req);

        else if( request.equalsIgnoreCase( WMS.Request.GetFeatureInfo.toString()) )
            return new WMSGetFeatureInfoRequest(req);

        else if( request.equalsIgnoreCase( WMS.Request.GetImageryList.toString()) )
            return new WMSGetImageryListRequest(req);

        else if( request.equalsIgnoreCase( WMS.Request.GetElevations.toString()) )
            return new WMSGetElevationsRequest(req);

        else
        {
            // TODO zz: garakl: Do not redirect, just send a valid WMS error XML
            
            String requestUrl = req.getUrl();
            requestUrl = ( null == requestUrl ) ? "" : requestUrl.trim();

            ServerApplication app = req.getServerApplication();

            if( null == app || !(app instanceof WMSServerApplication) )
            {
                String msg = Logging.getMessage( "WMS.Server.ApplicationNotFound" );
                Logging.logger().severe(msg);
                throw new WMSServiceException( msg );
            }

            WMSServerApplication wmsApp = (WMSServerApplication)app;

            String redirectUrl = wmsApp.getRedirectUrl();

            redirectUrl = ( null == redirectUrl ) ? "" : redirectUrl.trim();

            String vdir = app.getVirtualDirectory();
            vdir = ( null == vdir ) ? "" : vdir.trim().toLowerCase();

            if( null == redirectUrl || null == vdir || null == requestUrl || requestUrl.startsWith(vdir) )
            {
                String msg = Logging.getMessage( "WMS.MissingRequestParameter" );
                Logging.logger().severe(msg);
                throw new WMSServiceException( msg );
            }

            return new HTTPRedirectRequest( req );
        }
    }
}
