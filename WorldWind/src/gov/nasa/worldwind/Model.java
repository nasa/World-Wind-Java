/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind;

import gov.nasa.worldwind.globes.*;
import gov.nasa.worldwind.layers.LayerList;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public interface Model extends WWObject
{
    gov.nasa.worldwind.geom.Extent getExtent();

    Globe getGlobe();

    LayerList getLayers();

    void setGlobe(Globe globe);

    void setLayers(LayerList layers);

    void setShowWireframeInterior(boolean show);

    void setShowWireframeExterior(boolean show);

    boolean isShowWireframeInterior();

    boolean isShowWireframeExterior();

    boolean isShowTessellationBoundingVolumes();

    void setShowTessellationBoundingVolumes(boolean showTileBoundingVolumes);
}
