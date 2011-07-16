/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.geom.*;

import java.util.List;

/**
 * @author tag
 * @version $Id$
 */
public interface SurfaceTile
{
    boolean bind(DrawContext dc);
    void applyInternalTransform(DrawContext dc, boolean textureIdentityActive);
    Sector getSector();
    Extent getExtent(DrawContext dc);
    List<? extends LatLon> getCorners();
}
