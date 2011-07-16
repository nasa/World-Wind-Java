/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.ogc;

import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;

/**
 * @author dcollins
 * @version $Id$
 */
public interface OGCWebServiceOperation
{
    void service(HTTPRequest request, HTTPResponse response);
}
