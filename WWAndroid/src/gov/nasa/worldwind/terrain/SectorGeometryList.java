/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.terrain;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.render.DrawContext;

/**
 * @author dcollins
 * @version $Id$
 */
public interface SectorGeometryList extends Iterable<SectorGeometry>
{
    int size();

    Sector getSector();

    void beginRendering(DrawContext dc);

    void endRendering(DrawContext dc);
}
