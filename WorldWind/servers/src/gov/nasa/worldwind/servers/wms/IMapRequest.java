/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public interface IMapRequest
{
    public String getFormat();

    public void     setWidth(int width);
    public int      getWidth();

    public void     setHeight(int height);
    public int      getHeight();

    public Sector   getExtent();
    public Sector   getExtentForElevationRequest();

    public void     setBGColor(String color);
    public String   getBGColor();
    public Double   getBGColorAsDouble();
    public Color    getBGColorAsRGB();

    public double   getBBoxXMin();
    public double   getBBoxXMax();
    public double   getBBoxYMin();
    public double   getBBoxYMax();
}
