/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.render.*;

import java.awt.*;

/**
 * Basic implementation of {@link TacticalGraphicAttributes}.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class BasicTacticalGraphicAttributes extends BasicShapeAttributes implements TacticalGraphicAttributes
{
    protected Offset labelOffset;
    protected Font font;
    protected Material textMaterial;

    /** {@inheritDoc} */
    public Offset getLabelOffset()
    {
        return this.labelOffset;
    }

    /** {@inheritDoc} */
    public void setLabelOffset(Offset offset)
    {
        this.labelOffset = offset;
    }

    /** {@inheritDoc} */
    public Font getTextModifierFont()
    {
        return this.font;
    }

    /** {@inheritDoc} */
    public void setTextModifierFont(Font font)
    {
        this.font = font;
    }

    /** {@inheritDoc} */
    public Material getTextModifierMaterial()
    {
        return this.textMaterial;
    }

    /** {@inheritDoc} */
    public void setTextModifierMaterial(Material material)
    {
        this.textMaterial = material;
    }
}
