/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.layers.Earth;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;

/**
 * @author dcollins
 * @version $Id$
 */
public class BMNGOneImage extends AbstractLayer
{
    protected static final String IMAGE_PATH = "images/world.topo.bathy.200405.3x2048x1024.dds";

    protected SurfaceImage surfaceImage;

    public BMNGOneImage()
    {
        this.surfaceImage = new SurfaceImage(IMAGE_PATH, Sector.fromFullSphere());
    }

    @Override
    protected void doRender(DrawContext dc)
    {
        this.surfaceImage.render(dc);
    }

    @Override
    public String toString()
    {
        return Logging.getMessage("layers.Earth.BlueMarbleOneImageLayer.Name");
    }
}
