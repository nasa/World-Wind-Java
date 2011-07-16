/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class WMS
{
    public static final String VER_1_1_1 = "1.1.1";
    public static final String VER_1_3_0 = "1.3.0";

    public static class BBOX
    {
        public static final int XMIN = 0;
        public static final int YMIN = 1;
        public static final int XMAX = 2;
        public static final int YMAX = 3;
    }

    public static enum Request
    {
        GetCapabilities,
        GetMap,
        GetFeatureInfo,
        GetImageryList,
        GetElevations;

        public boolean equals(String s)
        {
            return this.name().equalsIgnoreCase(s);
        }
    }

    public static enum Param
    {
        //      NASA World Wind extensions
        WW_MAX_THREADS,

        //      WMS parameters
        BBOX,
        BGCOLOR,
        CRS,
        ELEVATION,
        EXCEPTIONS,
        FORMAT,
        HEIGHT,
        LAYERS,
        LOCATIONS,
        REQUEST,
        SERVICE,
        SRS,
        STYLES,
        TIME,
        TRANSPARENT,
        UPDATESEQUENCE,
        VERSION,
        WIDTH;

        public boolean equals(String s)
        {
            return this.name().equalsIgnoreCase(s);
        }
    }
}
