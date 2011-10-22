/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.render.Material;

import java.awt.*;

/**
 * @author dcollins
 * @version $Id$
 */
public interface TacticalSymbolAttributes
{
    Double getScale();

    void setScale(Double scale);

    Double getOpacity();

    void setOpacity(Double opacity);

    Font getTextModifierFont();

    void setTextModifierFont(Font font);

    Material getTextModifierMaterial();

    void setTextModifierMaterial(Material material);
}
