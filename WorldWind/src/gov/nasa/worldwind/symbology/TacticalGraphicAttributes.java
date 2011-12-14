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
 * TacticalGraphicAttributes is used to override default attributes determined by a graphic's symbol set. Any non-null
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
     * Indicates an offset used to position the graphic's main label relative to the label's geographic position. See
     * comments on {@link #setTextModifierOffset(gov.nasa.worldwind.render.Offset) setTextModifierOffset} for more
     * information.
     *
     * @return The offset that determines how the graphic's label is placed relative to the graphic.
     */
    Offset getTextModifierOffset();

    /**
     * Specifies an offset used to position the graphic's main label relative to the label's geographic position. The
     * geographic position is determined by the type of graphic. For example, the label for an area graphic is typically
     * placed at the center of the area polygon.
     * <p/>
     * The offset can specify an absolute pixel value, or a an offset relative to the size of the label. For example, an
     * offset of (-0.5, -0.5) in fraction units will center the label on its geographic position both horizontally and
     * vertically.
     *
     * @param offset The offset that determines how the graphic's label is placed relative to the graphic.
     */
    void setTextModifierOffset(Offset offset);

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
     * Indicates the material used to render text modifiers. See {@link #setTextModifierMaterial(gov.nasa.worldwind.render.Material)
     * setTextModifierMaterial} for a description of how the material is used.
     *
     * @return The material used to render text modifiers.
     */
    Material getTextModifierMaterial();

    /**
     * Specifies the material used to render text modifiers. How the material is used depends on the graphic
     * implementation. For example, graphics may draw 3D text that uses all of the specified material components, or
     * draw 2D text that uses only the diffuse component. MIL-STD-2525 tactical graphics use the diffuse component to
     * specify the color of 2D text.
     *
     * @param material The new material.
     */
    void setTextModifierMaterial(Material material);

    /**
     * Indicates the material properties of the graphic's interior. See {@link #setInteriorMaterial(gov.nasa.worldwind.render.Material)
     * setInteriorMaterial} for more information on how this material is interpreted.
     *
     * @return the material applied to the graphic's interior.
     *
     * @see #setInteriorMaterial(Material)
     */
    Material getInteriorMaterial();

    /**
     * Specifies the material properties of the graphic's interior. If lighting is applied to the graphic, this
     * indicates the interior's ambient, diffuse, and specular colors, its shininess, and the color of any emitted
     * light. Otherwise, the material's diffuse color indicates the graphic's constant interior color.
     *
     * @param material the material to apply to the graphic's interior.
     *
     * @throws IllegalArgumentException if <code>material</code> is <code>null</code>.
     * @see #getInteriorMaterial()
     */
    void setInteriorMaterial(Material material);

    /**
     * Indicates the material properties of the graphic's outline. See {@link #setOutlineMaterial(gov.nasa.worldwind.render.Material)
     * setOutlineMaterial} for more information on how this material is interpreted.
     *
     * @return the material applied to the graphic's outline.
     *
     * @see #setOutlineMaterial(Material)
     */
    Material getOutlineMaterial();

    /**
     * Specifies the material properties of the graphic's outline. If lighting is applied to the graphic, this indicates
     * the outline's ambient, diffuse, and specular colors, its shininess, and the color of any emitted light.
     * Otherwise, the material's diffuse color indicates the graphic's constant outline color.
     *
     * @param material the material to apply to the graphic's outline.
     *
     * @throws IllegalArgumentException if <code>material</code> is <code>null</code>.
     * @see #getOutlineMaterial()
     */
    void setOutlineMaterial(Material material);

    /**
     * Indicates the opacity of the graphic's interior as a floating-point value in the range 0.0 to 1.0.
     *
     * @return the interior opacity as a floating-point value from 0.0 to 1.0.
     *
     * @see #setInteriorOpacity(Double)
     */
    Double getInteriorOpacity();

    /**
     * Specifies the opacity of the graphic's interior as a floating-point value in the range 0.0 to 1.0. A value of 1.0
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
     * Indicates the opacity of the graphic's outline as a floating-point value in the range 0.0 to 1.0.
     *
     * @return the outline opacity as a floating-point value from 0.0 to 1.0.
     *
     * @see #setOutlineOpacity(Double)
     */
    Double getOutlineOpacity();

    /**
     * Specifies the opacity of the graphic's outline as a floating-point value in the range 0.0 to 1.0. A value of 1.0
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
     * Indicates the line width (in pixels) used when rendering the graphic's outline. The returned value is either zero
     * or a positive floating-point value.
     *
     * @return the line width in pixels.
     *
     * @see #setOutlineWidth(Double)
     */
    Double getOutlineWidth();

    /**
     * Specifies the line width (in pixels) to use when rendering the graphic's outline. The specified
     * <code>width</code> must be zero or a positive floating-point value. Specifying a line width of zero disables the
     * graphic's outline. The <code>width</code> may be limited by an implementation-defined maximum during rendering.
     * The maximum width is typically 10 pixels.
     *
     * @param width the line width in pixels.
     *
     * @throws IllegalArgumentException if <code>width</code> is less than zero.
     * @see #getOutlineWidth()
     */
    void setOutlineWidth(Double width);
}
