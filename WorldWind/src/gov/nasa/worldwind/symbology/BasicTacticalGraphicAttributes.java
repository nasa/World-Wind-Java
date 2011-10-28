/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;

/**
 * Basic implementation of {@link TacticalGraphicAttributes}.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class BasicTacticalGraphicAttributes implements TacticalGraphicAttributes
{
    /** Indicates the material properties of the shape's interior. Initially <code>null</code>. */
    protected Material interiorMaterial;
    /** Indicates the material properties of the shape's outline. Initially <code>null</code>. */
    protected Material outlineMaterial;
    /** Indicates the opacity of the shape's interior as a floating-point value in the range 0.0 to 1.0. Initially 0.0. */
    protected Double interiorOpacity;
    /** Indicates the opacity of the shape's outline as a floating-point value in the range 0.0 to 1.0. Initially 0.0. */
    protected Double outlineOpacity;
    /** Indicates the line width (in pixels) used when rendering the shape's outline. Initially 0.0. */
    protected double outlineWidth;
    protected Offset labelOffset;
    protected Font font;
    protected Material textMaterial;

    /**
     * Creates a new <code>BasicShapeAttributes</code>.
     */
    public BasicTacticalGraphicAttributes()
    {
    }

    /**
     * Creates a new <code>BasicShapeAttributes</code> configured with the specified <code>attributes</code>.
     *
     * @param attributes the attributes to configure the new <code>BasicShapeAttributes</code> with.
     *
     * @throws IllegalArgumentException if <code>attributes</code> is <code>null</code>.
     */
    public BasicTacticalGraphicAttributes(TacticalGraphicAttributes attributes)
    {
        if (attributes == null)
        {
            String message = Logging.getMessage("nullValue.AttributesIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.labelOffset = attributes.getLabelOffset();
        this.font = attributes.getTextModifierFont();
        this.textMaterial = attributes.getTextModifierMaterial();
        this.interiorMaterial = attributes.getInteriorMaterial();
        this.outlineMaterial = attributes.getOutlineMaterial();
        this.interiorOpacity = attributes.getInteriorOpacity();
        this.outlineOpacity = attributes.getOutlineOpacity();
        this.outlineWidth = attributes.getOutlineWidth();
    }

    /** {@inheritDoc} */
    public TacticalGraphicAttributes copy()
    {
        return new BasicTacticalGraphicAttributes(this);
    }

    /** {@inheritDoc} */
    public void copy(TacticalGraphicAttributes attributes)
    {
        if (attributes != null)
        {
            this.labelOffset = attributes.getLabelOffset();
            this.font = attributes.getTextModifierFont();
            this.textMaterial = attributes.getTextModifierMaterial();
            this.interiorMaterial = attributes.getInteriorMaterial();
            this.outlineMaterial = attributes.getOutlineMaterial();
            this.interiorOpacity = attributes.getInteriorOpacity();
            this.outlineOpacity = attributes.getOutlineOpacity();
            this.outlineWidth = attributes.getOutlineWidth();
        }
    }

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

    /** {@inheritDoc} */
    public Material getInteriorMaterial()
    {
        return this.interiorMaterial;
    }

    /** {@inheritDoc} */
    public void setInteriorMaterial(Material material)
    {
        if (material == null)
        {
            String message = Logging.getMessage("nullValue.MaterialIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.interiorMaterial = material;
    }

    /** {@inheritDoc} */
    public Material getOutlineMaterial()
    {
        return this.outlineMaterial;
    }

    /** {@inheritDoc} */
    public void setOutlineMaterial(Material material)
    {
        if (material == null)
        {
            String message = Logging.getMessage("nullValue.MaterialIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.outlineMaterial = material;
    }

    /** {@inheritDoc} */
    public Double getInteriorOpacity()
    {
        return this.interiorOpacity;
    }

    /** {@inheritDoc} */
    public void setInteriorOpacity(Double opacity)
    {
        if (opacity < 0 || opacity > 1)
        {
            String message = Logging.getMessage("generic.OpacityOutOfRange", opacity);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.interiorOpacity = opacity;
    }

    /** {@inheritDoc} */
    public Double getOutlineOpacity()
    {
        return this.outlineOpacity;
    }

    /** {@inheritDoc} */
    public void setOutlineOpacity(Double opacity)
    {
        if (opacity < 0 || opacity > 1)
        {
            String message = Logging.getMessage("generic.OpacityOutOfRange", opacity);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.outlineOpacity = opacity;
    }

    /** {@inheritDoc} */
    public Double getOutlineWidth()
    {
        return this.outlineWidth;
    }

    /** {@inheritDoc} */
    public void setOutlineWidth(Double width)
    {
        if (width < 0)
        {
            String message = Logging.getMessage("Geom.WidthIsNegative", width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.outlineWidth = width;
    }
}
