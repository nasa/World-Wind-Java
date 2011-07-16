/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.util.Logging;

import java.io.IOException;

/**
 * @author brownrigg
 * @version $Id$
 */
public class WMSGetFeatureInfoRequest extends WMSRequest 
{
    public WMSGetFeatureInfoRequest(HTTPRequest req) throws WMSServiceException
    {
        super(req);
        String message = Logging.getMessage("generic.FeatureNotImplemented", "GetFeatureInfo" );
        Logging.logger().severe(message);
        throw new WMSServiceException(message);
    }

    public String getFormat()
    {
        return null;
    }

    public void service(HTTPRequest req, HTTPResponse resp) throws IOException, WMSServiceException
    {
        String message = Logging.getMessage("generic.FeatureNotImplemented", "GetFeatureInfo" );
        Logging.logger().severe(message);
        throw new WMSServiceException(message);
    }
}
