/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util;

import java.net.*;

/**
 * @version $Id$
 */
public interface TileUrlBuilder
{
    URL getURL(Tile tile, String imageFormat) throws MalformedURLException;
}
