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
 * <p/>
 * TacticalGraphicAttributes is used to override attributes determined by a graphic's symbol set. Any non-null
 * attributes will override the corresponding default attributes. Here's an example of overriding only the outline
 * material of a graphic without affecting other styling specified by the symbol set:
 * <p/>
 * <pre>
 *     TacticalGraphic graphic = ...
 *     TacticalGraphicAttributes attrs = new BasicTacticalGraphicAttributes();
 *
 *     // Set the outline to red. Leave all other fields null to retain the default values.
 *     attrs.setOutlineMaterial(Material.RED);
 *
 *     // Apply the overrides to the graphic
 *     graphic.setAttributes(attrs);
 * </pre>
 *
 * @author pabercrombie
 * @version $Id$
 */
public interface TacticalGraphicAttributes
{
    /**
     * Returns a new TacticalGraphicAttributes instance of the same type as this TacticalGraphicAttributes who's
     * properties are configured exactly as this TacticalGraphicAttributes.
     *
     * @return a copy of this TacticalGraphicAttributes.
     */
    TacticalGraphicAttributes copy();

    /**
     * Copies the specified TacticalGraphicAttributes' properties into this object's properties. This does nothing if
     * the specified attributes is <code>null</code>.
     *
     * @param attributes the attributes to copy.
     */
    void copy(TacticalGraphicAttributes attributes);

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

    /**
     * Indicates the material properties of the shape's interior. If lighting is applied to the shape, this indicates
     * the interior's ambient, diffuse, and specular colors, its shininess, and the color of any emitted light.
     * Otherwise, the material's diffuse color indicates the shape's constant interior color.
     *
     * @return the material applied to the balloon's interior.
     *
     * @see #setInteriorMaterial(Material)
     */
    Material getInteriorMaterial();

    /**
     * Specifies the material properties of the shape's interior. If lighting is applied to the shape, this specifies
     * the interior's ambient, diffuse, and specular colors, its shininess, and the color of any emitted light.
     * Otherwise, the material's diffuse color specifies the shape's constant interior color.
     *
     * @param material the material to apply to the balloon's interior.
     *
     * @throws IllegalArgumentException if <code>material</code> is <code>null</code>.
     * @see #getInteriorMaterial()
     */
    void setInteriorMaterial(Material material);

    /**
     * Indicates the material properties of the shape's outline. If lighting is applied to the shape, this indicates the
     * outline's ambient, diffuse, and specular colors, its shininess, and the color of any emitted light. Otherwise,
     * the material's diffuse color indicates the shape's constant outline color.
     *
     * @return the material applied to the balloon's outline.
     *
     * @see #setOutlineMaterial(Material)
     */
    Material getOutlineMaterial();

    /**
     * Specifies the material properties of the shape's outline. If lighting is applied to the shape, this specifies the
     * outline's ambient, diffuse, and specular colors, its shininess, and the color of any emitted light. Otherwise,
     * the material's diffuse color specifies as the shape's constant outline color.
     *
     * @param material the material to apply to the balloon's outline.
     *
     * @throws IllegalArgumentException if <code>material</code> is <code>null</code>.
     * @see #getOutlineMaterial()
     */
    void setOutlineMaterial(Material material);

    /**
     * Indicates the opacity of the shape's interior as a floating-point value in the range 0.0 to 1.0.
     *
     * @return the interior opacity as a floating-point value from 0.0 to 1.0.
     *
     * @see #setInteriorOpacity(Double)
     */
    Double getInteriorOpacity();

    /**
     * Specifies the opacity of the shape's interior as a floating-point value in the range 0.0 to 1.0. A value of 1.0
     * specifies a completely opaque interior, and 0.0 specifies a completely transparent interior. Values in between
     * specify a partially transparent interior.
     *
     * @param opacity the interior opacity as a floating-point value from 0.0 to 1.0.
     *
     * @throws IllegalArgumentException if <code>opacity</code> is less than 0.0 or greater than 1.0.
     * @see #getInteriorOpacity()
     */
    void setInteriorOpacity(Double opacity);

    /**
     * Indicates the opacity of the shape's outline as a floating-point value in the range 0.0 to 1.0.
     *
     * @return the outline opacity as a floating-point value from 0.0 to 1.0.
     *
     * @see #setOutlineOpacity(Double)
     */
    Double getOutlineOpacity();

    /**
     * Specifies the opacity of the shape's outline as a floating-point value in the range 0.0 to 1.0. A value of 1.0
     * specifies a completely opaque outline, and 0.0 specifies a completely transparent outline. Values in between
     * specify a partially transparent outline.
     *
     * @param opacity the outline opacity as a floating-point value from 0.0 to 1.0.
     *
     * @throws IllegalArgumentException if <code>opacity</code> is less than 0.0 or greater than 1.0.
     * @see #getOutlineOpacity()
     */
    void setOutlineOpacity(Double opacity);

    /**
     * Indicates the line width (in pixels) used when rendering the shape's outline. The returned value is either zero
     * or a positive floating-point value.
     *
     * @return the line width in pixels.
     *
     * @see #setOutlineWidth(Double)
     */
    Double getOutlineWidth();

    /**
     * Specifies the line width (in pixels) to use when rendering the shape's outline. The specified <code>width</code>
     * must be zero or a positive floating-point value. Specifying a line width of zero disables the shape's outline.
     * The <code>width</code> may be limited by an implementation-defined maximum during rendering. The maximum width is
     * typically 10 pixels.
     *
     * @param width the line width in pixels.
     *
     * @throws IllegalArgumentException if <code>width</code> is less than zero.
     * @see #getOutlineWidth()
     */
    void setOutlineWidth(Double width);
}
