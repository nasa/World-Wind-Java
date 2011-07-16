/* Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.applications.wss;

import java.net.URL;

/**
 * @author dcollins
 * @version $Id$
 */
public interface WSSFeatureType
{
    String getName();

    Iterable<String> getOutputFormats();

    URL getFeatureResource(String resourceID);

    Iterable<String> getFeatureResourceIDs();
}
