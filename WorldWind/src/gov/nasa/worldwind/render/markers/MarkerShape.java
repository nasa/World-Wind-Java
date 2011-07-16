/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render.markers;

import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.geom.Vec4;

/**
 * @author tag
 * @version $Id$
 */
public interface MarkerShape
{
    String getShapeType();
    
    void render(DrawContext dc, Marker marker, Vec4 point, double radius);

    void render(DrawContext dc, Marker marker, Vec4 point, double radius, boolean isRelative);
}
