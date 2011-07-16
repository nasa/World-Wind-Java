/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util;

import java.net.URL;

/**
 * @author lado
 * @version $Id: TileUrlBuilder   Jun 19, 2007  12:47:51 AM
 */
public interface TileUrlBuilder
{
        public URL getURL(Tile tile, String imageFormat) throws java.net.MalformedURLException;
}
