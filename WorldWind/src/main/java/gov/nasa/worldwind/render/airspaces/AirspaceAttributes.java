/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.RestorableSupport;

/**
 * @author dcollins
 * @version $Id$
 */
public interface AirspaceAttributes
{
    boolean isDrawInterior();

    void setDrawInterior(boolean state);

    boolean isDrawOutline();

    void setDrawOutline(boolean state);

    Material getMaterial();

    void setMaterial(Material material);

    Material getOutlineMaterial();

    void setOutlineMaterial(Material material);

    double getOpacity();

    void setOpacity(double opacity);

    double getOutlineOpacity();

    void setOutlineOpacity(double opacity);

    double getOutlineWidth();

    void setOutlineWidth(double width);

    void applyInterior(DrawContext dc, boolean enableMaterial);
    
    void applyOutline(DrawContext dc, boolean enableMaterial);

    void getRestorableState(RestorableSupport rs, RestorableSupport.StateObject so);

    void restoreState(RestorableSupport rs, RestorableSupport.StateObject so);
}
