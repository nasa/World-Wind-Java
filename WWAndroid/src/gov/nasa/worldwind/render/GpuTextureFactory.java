/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.avlist.AVList;

/**
 * @author dcollins
 * @version $Id$
 */
public interface GpuTextureFactory
{
    GpuTextureData createTextureData(Object source, AVList params);

    GpuTexture createTexture(DrawContext dc, GpuTextureData textureData, AVList params);
}
