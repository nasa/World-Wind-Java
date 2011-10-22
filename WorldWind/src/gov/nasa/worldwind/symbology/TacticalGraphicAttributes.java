/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.render.*;

import java.awt.*;

/**
 * Holds attributes for a {@link TacticalGraphic}. Changes made to the attributes are applied to the graphic when the
 * <code>WorldWindow</code> renders the next frame. Instances of <code>TacticalGraphicAttributes</code> may be shared by
 * many graphics, thereby reducing the memory normally required to store attributes for each graphic.
 *
 * @author pabercrombie
 * @version $Id$
 */
public interface TacticalGraphicAttributes extends ShapeAttributes
{
    /**
     * Indicates an offset used to position the graphic's label relative to the graphic's reference point.
     *
     * @return The offset that determines how the graphic's label is placed relative to the graphic.
     */
    Offset getLabelOffset();

    /**
     * Specifies an offset used to position the graphic's label relative to the graphic's reference point.
     *
     * @param offset The offset that determines how the graphic's label is placed relative to the graphic.
     */
    void setLabelOffset(Offset offset);

    /**
     * Indicates the font used to render text modifiers.
     *
     * @return The font used to render text modifiers.
     */
    Font getTextModifierFont();

    /**
     * Specifies the font used to render text modifiers.
     *
     * @param font New font.
     */
    void setTextModifierFont(Font font);

    /**
     * Indicates the material used to render text modifiers.
     *
     * @return The material used to render text modifiers.
     */
    Material getTextModifierMaterial();

    /**
     * Specifies the material used to render text modifiers.
     *
     * @param material The new material.
     */
    void setTextModifierMaterial(Material material);
}
