/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.layers.Earth;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.util.*;

/**
 * @author tag
 * @version $Id$
 */
public class BMNGOneImage extends RenderableLayer
{
    protected static final String IMAGE_PATH = "images/BMNG_world.topo.bathy.200405.3.2048x1024.dds";

    public BMNGOneImage()
    {
        this.setName(Logging.getMessage("layers.Earth.BlueMarbleOneImageLayer.Name"));
        this.addRenderable(new SurfaceImage(IMAGE_PATH, Sector.FULL_SPHERE));

        // Disable picking for the layer because it covers the full sphere and will override a terrain pick.
        this.setPickEnabled(false);
    }

    @Override
    public String toString()
    {
        return Logging.getMessage("layers.Earth.BlueMarbleOneImageLayer.Name");
    }
}
