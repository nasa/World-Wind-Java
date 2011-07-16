/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.servers.app.WMSServerApplication;
import gov.nasa.worldwind.servers.http.*;

import java.io.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class HTTPRedirectRequest extends WMSRequest 
{
    public HTTPRedirectRequest(HTTPRequest req) throws WMSServiceException
    {
        super( req );
    }

    public void service(HTTPRequest req, HTTPResponse resp) throws IOException, WMSServiceException
    {
        String redirect = ((WMSServerApplication)req.getServerApplication()).getRedirectUrl();

        resp.setContentType("text/plain");
        resp.setStatus( HTTPResponse.TEMP_REDIRECT );
        resp.addHeader( "Location", redirect );
        resp.flushBuffer();
    }
}

