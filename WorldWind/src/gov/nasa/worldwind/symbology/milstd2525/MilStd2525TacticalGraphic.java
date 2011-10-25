/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.Logging;

/**
 * Base class for tactical graphics defined by <a href="http://www.assistdocs.com/search/document_details.cfm?ident_number=114934">MIL-STD-2525</a>.
 * See the TacticalGraphic <a title="Tactical Graphic Usage Guide" href="http://goworldwind.org/developers-guide/symbology/tactical-graphics/"
 * target="_blank">Usage Guide</a> for instructions on using TacticalGraphic in an application.
 * <p/>
 * The following table lists the modifiers supported by 2525 graphics. Note that not all graphics support all modifiers.
 * <table width="100%"> <tr><th>Field</th><th>Modifier key</th><th>Data type</th><th>Description</th></tr>
 * <tr><td>A</td><td>AVKey.SYMBOL</td><td>{@link TacticalSymbol}</td><td>Symbol icon</td></tr>
 * <tr><td>B</td><td>AVKey.ECHELON</td><td>String</td><td>Echelon</td></tr> <tr><td>C</td><td>AVKey.QUANTITY</td><td>String</td><td>Quantity</td></tr>
 * <tr><td>H</td><td>AVKey.DESCRIPTION</td><td>String</td><td>Additional information</td></tr>
 * <tr><td>N</td><td>AVKey.SHOW_HOSTILE</td><td>Boolean</td><td>Show/hide hostile entity indicator</td></tr>
 * <tr><td>Q</td><td>AVKey.HEADING</td><td>{@link gov.nasa.worldwind.geom.Angle}</td><td>Direction indicator</td></tr>
 * <tr><td>S</td><td>AVKey.OFFSET</td><td>{@link gov.nasa.worldwind.render.Offset}</td><td>Offset location
 * indicator</td></tr> <tr><td>T</td><td>AVKey.TEXT</td><td>String</td><td>Unique designation</td></tr>
 * <tr><td>V</td><td>AVKey.TYPE</td><td>String</td><td>Type</td></tr> <tr><td>W</td><td>AVKey.DATE</td><td>Date</td><td>Date/time</td></tr>
 * <tr><td>X</td><td>AVKey.ALTITUDE</td><td>Double</td><td>Altitude/depth</td></tr>
 * <tr><td>Y</td><td>AVKey.SHOW_POSITION</td><td>Boolean</td><td>Show/hide position field</td></tr>
 * <tr><td>AM</td><td>AVKey.DISTANCE</td><td>Double</td><td>Distance</td></tr> <tr><td>AN</td><td>AVKey.AZIMUTH</td><td>Angle</td><td>Azimuth</td></tr>
 * <tr><td>--</td><td>AVKey.GRAPHIC</td><td>{@link TacticalGraphic}</td><td>Child graphic</td></tr> </table>
 * <p/>
 * Here's an example of setting modifiers during construction of a graphic:
 * <pre>
 * AVList modifiers = new AVListImpl();
 * modifiers.setValue(AVKey.TEXT, "X469"); // Field T
 * modifiers.setValue(AVKey.DATE, new Date()); // Field W
 * modifiers.setValue(AVKey.ADDITIONAL_INFO, "Anthrax Suspected"); // Field H
 * modifiers.setValue(AVKey.HEADING, Angle.fromDegrees(30.0)); // Field Q
 *
 * Position position = Position.fromDegrees(35.1026, -118.348, 0);
 *
 * // Create the graphic with the modifier list
 * TacticalGraphic graphic = factory.createGraphic("GHMPNEB----AUSX", positions, modifiers);
 * </pre>
 * <p/>
 * Some graphics support multiple instances of a modifier. For example, 2525 uses the field code W for a date/time
 * modifier. Some graphics support multiple timestamps, in which case the fields are labeled W, W1, W2, etc. An
 * application can pass an {@link Iterable} to <code>setModifier</code> if multiple values are required to specify the
 * modifier. Here's an example of how to specify two timestamps:
 * <pre>
 * Date startDate = ...
 * Date endData = ...
 *
 * graphic.setModifier(AVKey.DATE, Arrays.asList(startDate, endDate));
 * </pre>
 * <p/>
 * <h1>Composite graphics</h1>
 * <p/>
 * Some tactical graphics can include other graphics. For example, a Minimum Risk Route (MRR) can include Air Control
 * Point or Communications Checkpoint graphics along the route. These child graphics can be specified using the {@code
 * AVKey.GRAPHIC} modifier. Here's an example of how to create a Minimum Risk Route:
 * <p/>
 * <pre>
 * List&lt;Position&gt; positions = ... // Positions that define the route
 *
 * // Create the Minimum Risk Route graphic
 * TacticalGraphic parent = factory.createGraphic("GFGPALM-------X", positions, null);
 *
 * // Create the child graphics that will appear at the control points of the route
 * List&lt;TacticalGraphic&gt; childGraphics = new ArrayList&lt;TacticalGraphic&gt;();
 * for (Position position : positions)
 * {
 *     // Create an Air Control Point graphic
 *     TacticalGraphic child = factory.createGraphic("GFGPAPP-------X", position, null); // Create
 *     childGraphics.add(child);
 * }
 *
 * // Set the child graphics
 * parent.setModifier(AVKey.GRAPHIC, childGraphics);
 * </pre>
 *
 * @author pabercrombie
 * @version $Id$
 */
public abstract class MilStd2525TacticalGraphic extends AVListImpl implements TacticalGraphic
{
    protected String text;

    protected boolean highlighted;
    protected boolean visible = true;
    protected TacticalGraphicAttributes normalAttributes;
    protected TacticalGraphicAttributes highlightAttributes;

    protected boolean showModifiers = true;

    protected String standardIdentity;
    protected String echelon;
    protected String category;
    protected String status;
    // TODO: add country code, etc.

    public abstract String getFunctionId();

    public abstract void doRenderGraphic(DrawContext dc);

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        SymbolCode symCode = new SymbolCode();
        symCode.setValue(SymbolCode.STANDARD_IDENTITY, this.standardIdentity);
        symCode.setValue(SymbolCode.ECHELON, this.echelon);
        symCode.setValue(SymbolCode.CATEGORY, this.category);
        symCode.setValue(SymbolCode.FUNCTION_ID, this.getFunctionId());

        return symCode.toString();
    }

    /** {@inheritDoc} */
    public boolean isVisible()
    {
        return this.visible;
    }

    /** {@inheritDoc} */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /** {@inheritDoc} */
    public TacticalGraphicAttributes getAttributes()
    {
        return this.normalAttributes;
    }

    /** {@inheritDoc} */
    public void setAttributes(TacticalGraphicAttributes attributes)
    {
        this.normalAttributes = attributes;
    }

    /** {@inheritDoc} */
    public TacticalGraphicAttributes getHighlightAttributes()
    {
        return this.highlightAttributes;
    }

    /** {@inheritDoc} */
    public void setHighlightAttributes(TacticalGraphicAttributes attributes)
    {
        this.highlightAttributes = attributes;
    }

    /** {@inheritDoc} */
    public boolean isHighlighted()
    {
        return this.highlighted;
    }

    /** {@inheritDoc} */
    public void setHighlighted(boolean highlighted)
    {
        this.highlighted = highlighted;
    }

    /** {@inheritDoc} */
    public Object getModifier(String modifier)
    {
        if (AVKey.TEXT.equals(modifier))
        {
            return this.text;
        }
        return null;
    }

    /** {@inheritDoc} */
    public void setModifier(String modifier, Object value)
    {
        if (AVKey.TEXT.equals(modifier) && (value instanceof String))
        {
            this.setText((String) value);
        }
    }

    /** {@inheritDoc} */
    public boolean isShowModifiers()
    {
        return this.showModifiers;
    }

    /** {@inheritDoc} */
    public void setShowModifiers(boolean showModifiers)
    {
        this.showModifiers = showModifiers;
    }

    public String getStandardIdentity()
    {
        return this.standardIdentity;
    }

    public void setStandardIdentity(String standardIdentity)
    {
        if (standardIdentity == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.standardIdentity = standardIdentity;
    }

    public String getCategory()
    {
        return this.category;
    }

    public void setCategory(String category)
    {
        if (category == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.category = category;
    }

    public String getEchelon()
    {
        return this.echelon;
    }

    public void setEchelon(String echelon)
    {
        if (echelon == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.echelon = echelon;
    }

    public String getStatus()
    {
        return this.status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * Indicates a string of descriptive text for this graphic.
     *
     * @return Descriptive text for this graphic.
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * Specifies a string of descriptive text for this graphic.
     *
     * @param text Descriptive text for this graphic.
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /** {@inheritDoc} */
    public void render(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.doRenderGraphic(dc);

        if (!this.isShowModifiers())
        {
            this.doRenderModifiers(dc);
        }
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected void doRenderModifiers(DrawContext dc)
    {
        // Do nothing
    }

    protected void applyDefaultAttributes(ShapeAttributes attributes)
    {
        String identity = this.getStandardIdentity();
        if (SymbolCode.IDENTITY_FRIEND.equals(identity))
        {
            attributes.setOutlineMaterial(Material.BLACK);
        }
        else if (SymbolCode.IDENTITY_HOSTILE.equals(identity))
        {
            attributes.setOutlineMaterial(Material.RED);
        }

        String status = this.getStatus();
        if (SymbolCode.STATUS_ANTICIPATED.equals(status))
        {
            attributes.setOutlineStippleFactor(6);
            attributes.setOutlineStipplePattern((short) 0xAAAA);
        }
    }

    protected void applyOverrideAttributes(TacticalGraphicAttributes graphicAttributes, ShapeAttributes shapeAttributes)
    {
        Material material = graphicAttributes.getInteriorMaterial();
        if (material != null)
        {
            shapeAttributes.setInteriorMaterial(material);
        }

        material = graphicAttributes.getOutlineMaterial();
        if (material != null)
        {
            shapeAttributes.setInteriorMaterial(material);
        }

        Double value = graphicAttributes.getInteriorOpacity();
        if (value != null)
        {
            shapeAttributes.setInteriorOpacity(value);
        }

        value = graphicAttributes.getOutlineOpacity();
        if (value != null)
        {
            shapeAttributes.setOutlineOpacity(value);
        }

        value = graphicAttributes.getOutlineWidth();
        if (value != null)
        {
            shapeAttributes.setOutlineWidth(value);
        }
    }
}
