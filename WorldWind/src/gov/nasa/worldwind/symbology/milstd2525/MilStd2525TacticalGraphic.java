/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Base class for tactical graphics defined by <a href="http://www.assistdocs.com/search/document_details.cfm?ident_number=114934">MIL-STD-2525</a>.
 * See the TacticalGraphic <a title="Tactical Graphic Usage Guide" href="http://goworldwind.org/developers-guide/symbology/tactical-graphics/"
 * target="_blank">Usage Guide</a> for instructions on using TacticalGraphic in an application.
 * <p/>
 * The following table lists the modifiers supported by 2525 graphics. Note that not all graphics support all modifiers.
 * <table width="100%"> <tr><th>Field</th><th>Modifier key</th><th>Data type</th><th>Description</th></tr>
 * <tr><td>A</td><td>SymbologyConstants.SYMBOL</td><td>{@link TacticalSymbol}</td><td>Symbol icon</td></tr>
 * <tr><td>B</td><td>SymbologyConstants.ECHELON</td><td>String</td><td>Echelon</td></tr>
 * <tr><td>C</td><td>SymbologyConstants.QUANTITY</td><td>String</td><td>Quantity</td></tr>
 * <tr><td>H</td><td>SymbologyConstants.ADDITIONAL_INFO</td><td>String</td><td>Additional information</td></tr>
 * <tr><td>N</td><td>SymbologyConstants.SHOW_HOSTILE</td><td>Boolean</td><td>Show/hide hostile entity
 * indicator</td></tr> <tr><td>Q</td><td>SymbologyConstants.DIRECTION_OF_MOVEMENT</td><td>{@link
 * gov.nasa.worldwind.geom.Angle}</td><td>Direction indicator</td></tr> <tr><td>S</td><td>SymbologyConstants.OFFSET</td><td>{@link
 * gov.nasa.worldwind.render.Offset}</td><td>Offset location indicator</td></tr> <tr><td>T</td><td>SymbologyConstants.UNIQUE_DESIGNATION</td><td>String</td><td>Unique
 * designation</td></tr> <tr><td>V</td><td>SymbologyConstants.TYPE</td><td>String</td><td>Type</td></tr>
 * <tr><td>W</td><td>SymbologyConstants.DATE_TIME_GROUP</td><td>String</td><td>Date/time</td></tr>
 * <tr><td>X</td><td>SymbologyConstants.ALTITUDE_DEPTH</td><td>Double</td><td>Altitude/depth</td></tr>
 * <tr><td>Y</td><td>SymbologyConstants.SHOW_LOCATION</td><td>Boolean</td><td>Show/hide position field</td></tr>
 * <tr><td>AM</td><td>SymbologyConstants.DISTANCE</td><td>Double</td><td>Radius, length or width of rectangle.</td></tr>
 * <tr><td>AN</td><td>SymbologyConstants.AZIMUTH</td><td>Angle</td><td>Azimuth</td></tr> </table>
 * <p/>
 * Here's an example of setting modifiers during construction of a graphic:
 * <pre>
 * AVList modifiers = new AVListImpl();
 * modifiers.setValue(SymbologyConstants.UNIQUE_DESIGNATION, "X469"); // Field T
 * modifiers.setValue(SymbologyConstants.DATE_TIME_GROUP, new Date()); // Field W
 * modifiers.setValue(SymbologyConstants.ADDITIONAL_INFO, "Anthrax Suspected"); // Field H
 * modifiers.setValue(SymbologyConstants.DIRECTION_OF_MOVEMENT, Angle.fromDegrees(30.0)); // Field Q
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
 * String startDate = ...
 * String endData = ...
 *
 * graphic.setModifier(SymbologyConstants.DATE_TIME_GROUP, Arrays.asList(startDate, endDate));
 * </pre>
 *
 * @author pabercrombie
 * @version $Id$
 */
public abstract class MilStd2525TacticalGraphic extends AVListImpl implements TacticalGraphic, Renderable
{
    public final static Material MATERIAL_FRIEND = Material.BLACK;
    public final static Material MATERIAL_HOSTILE = Material.RED;

    /** The default highlight color. */
    protected static final Material DEFAULT_HIGHLIGHT_MATERIAL = Material.WHITE;

    /** Factor applied to the stipple pattern used to draw graphics in anticipated state. */
    protected static final int OUTLINE_STIPPLE_FACTOR = 6;
    /** Stipple pattern applied to graphics in the anticipated state. */
    protected static final short OUTLINE_STIPPLE_PATTERN = (short) 0xAAAA;

    /**
     * Indicates a string identifier for this symbol. The format of the identifier depends on the symbol set to which
     * this graphic belongs. For symbols belonging to the MIL-STD-2525 symbol set, this returns a 15-character
     * alphanumeric symbol identification code (SIDC). Calculated from the current modifiers at construction and during
     * each call to {@link #setModifier(String, Object)}. Initially <code>null</code>.
     */
    protected SymbolCode symbolCode;
    /**
     * Symbol identifier with fields that do not influence the type of graphic replaced with hyphens. See {@link
     * SymbolCode#toMaskedString}.
     */
    protected String maskedSymbolCode;

    /**
     * The graphic's text string. This field corresponds to the {@link SymbologyConstants#UNIQUE_DESIGNATION} modifier.
     * Note that this field is not used if an Iterable is specified as the unique designation.
     */
    protected String text;

    /** Indicates whether or not the graphic is highlighted. */
    protected boolean highlighted;
    /** Indicates whether or not to render the graphic. */
    protected boolean visible = true;
    /** Indicates whether or not to render text modifiers. */
    protected boolean showModifiers = true;
    /** Indicates whether or not to render the hostile/enemy modifier. */
    protected boolean showHostileIndicator = true;
    /** Object returned during picking to represent this graphic. */
    protected Object delegateOwner;

    /**
     * Attributes to apply when the graphic is not highlighted. These attributes override defaults determined by the
     * graphic's symbol code.
     */
    protected TacticalGraphicAttributes normalAttributes;
    /**
     * Attributes to apply when the graphic is highlighted. These attributes override defaults determined by the
     * graphic's symbol code.
     */
    protected TacticalGraphicAttributes highlightAttributes;

    /** Offset applied to the graphic's main label. */
    protected Offset labelOffset;
    /** Labels to render with the graphic. */
    protected List<Label> labels;

    /**
     * Map of modifiers applied to this graphic. Note that implementations may not store all modifiers in this map. Some
     * modifiers may be handled specially.
     */
    protected AVList modifiers;

    /** Current frame timestamp. */
    protected long frameTimestamp = -1L;

    /** Override attributes for the current frame. */
    protected TacticalGraphicAttributes activeOverrides = new BasicTacticalGraphicAttributes();
    /**
     * Shape attributes shared by all shapes that make up this graphic. The graphic's active attributes are copied into
     * this attribute bundle on each frame.
     */
    protected ShapeAttributes activeShapeAttributes = new BasicShapeAttributes();

    /** Flag to indicate that labels must be recreated before the graphic is rendered. */
    protected boolean mustCreateLabels = true;

    protected abstract void doRenderGraphic(DrawContext dc);

    /**
     * Invoked each frame to apply to the current delegate owner to all renderable objects used to draw the graphic.
     * This base class will apply the delegate owner to Label objects. Subclasses must implement this method to apply
     * the delegate owner to any Renderables that they will draw in order to render the graphic.
     *
     * @param owner Current delegate owner.
     */
    protected abstract void applyDelegateOwner(Object owner);

    protected MilStd2525TacticalGraphic(String symbolCode)
    {
        this.symbolCode = new SymbolCode(symbolCode);
        this.maskedSymbolCode = this.symbolCode.toMaskedString();
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.symbolCode.toString();
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
    public Offset getLabelOffset()
    {
        return this.labelOffset;
    }

    /** {@inheritDoc} */
    public void setLabelOffset(Offset labelOffset)
    {
        this.labelOffset = labelOffset;
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
        if (SymbologyConstants.UNIQUE_DESIGNATION.equals(modifier) && this.text != null)
        {
            return this.text;
        }
        else if (this.modifiers != null)
        {
            return this.modifiers.getValue(modifier);
        }
        return null;
    }

    /** {@inheritDoc} */
    public void setModifier(String modifier, Object value)
    {
        if (SymbologyConstants.UNIQUE_DESIGNATION.equals(modifier) && (value instanceof String))
        {
            this.setText((String) value);
        }
        else
        {
            if (this.modifiers == null)
                this.modifiers = new AVListImpl();

            this.modifiers.setValue(modifier, value);
            this.onModifierChanged();
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

    /** {@inheritDoc} */
    public boolean isShowHostileIndicator()
    {
        return this.showHostileIndicator;
    }

    /** {@inheritDoc} */
    public void setShowHostileIndicator(boolean showHostileIndicator)
    {
        this.showHostileIndicator = showHostileIndicator;
        this.onModifierChanged();
    }

    /** {@inheritDoc} */
    public boolean isShowLocation()
    {
        // Most 2525 graphics do not support the location modifier. Graphics that do support it can override this method.
        return false;
    }

    /** {@inheritDoc} */
    public void setShowLocation(boolean showLocation)
    {
        // Most 2525 graphics do not support the location modifier. Graphics that do support it can override this method.
    }

    /** {@inheritDoc} */
    public String getText()
    {
        if (this.text != null)
            return this.text;

        // If a simple string has not been set check for an iterable. Return the first value, if present.
        Object value = this.getModifier(SymbologyConstants.UNIQUE_DESIGNATION);
        if (value instanceof Iterable)
        {
            Iterator iterator = ((Iterable) value).iterator();
            Object o = iterator.hasNext() ? iterator.next() : null;
            if (o != null)
                return o.toString();
        }
        return null;
    }

    /** {@inheritDoc} */
    public void setText(String text)
    {
        this.text = text;
        this.onModifierChanged();
    }

    /** {@inheritDoc} */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /** {@inheritDoc} */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
    }

    /////////////////////////////
    // Movable interface
    /////////////////////////////

    /** {@inheritDoc} */
    public void move(Position delta)
    {
        if (delta == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position refPos = this.getReferencePosition();

        // The reference position is null if this shape has no positions. In this case moving the shape by a
        // relative delta is meaningless. Therefore we fail softly by exiting and doing nothing.
        if (refPos == null)
            return;

        this.moveTo(refPos.add(delta));
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position oldPosition = this.getReferencePosition();

        // The reference position is null if this shape has no positions. In this case moving the shape to a new
        // reference position is meaningless. Therefore we fail softly by exiting and doing nothing.
        if (oldPosition == null)
            return;

        List<Position> newPositions = Position.computeShiftedPositions(oldPosition, position, this.getPositions());

        if (newPositions != null)
            this.setPositions(newPositions);
    }

    /////////////
    // Rendering
    /////////////

    /** {@inheritDoc} */
    public void render(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.determinePerFrameAttributes(dc);

        this.doRenderGraphic(dc);

        if (this.isShowModifiers())
        {
            this.doRenderModifiers(dc);
        }
    }

    /**
     * Determine geometry and attributes for this frame. This method only determines attributes the first time that it
     * is called for each frame. Multiple calls in the same frame will have no effect.
     *
     * @param dc Current draw context.
     */
    protected void determinePerFrameAttributes(DrawContext dc)
    {
        long timeStamp = dc.getFrameTimeStamp();
        if (this.frameTimestamp != timeStamp)
        {
            // Allow the subclass to create labels, if necessary
            if (this.mustCreateLabels)
            {
                if (this.labels != null)
                {
                    this.labels.clear();
                }

                this.createLabels();
                this.mustCreateLabels = false;
            }

            this.determineActiveAttributes();
            this.determineDelegateOwner();

            this.computeGeometry(dc);
            this.frameTimestamp = timeStamp;
        }
    }

    /**
     * Render the text modifiers.
     *
     * @param dc Current draw context.
     */
    protected void doRenderModifiers(DrawContext dc)
    {
        if (this.labels != null)
        {
            for (Label label : this.labels)
            {
                label.render(dc);
            }
        }
    }

    protected String getStandardIdentity()
    {
        return this.symbolCode.getStandardIdentity();
    }

    /**
     * Indicates the status portion of the graphic identifier.
     *
     * @return The status associated with this graphic.
     */
    protected String getStatus()
    {
        return this.symbolCode.getStatus();
    }

    /**
     * Invoked when a modifier is changed. This implementation marks the label text as invalid causing it to be
     * recreated based on the new modifiers.
     */
    protected void onModifierChanged()
    {
        // Text may need to change to reflect new modifiers.
        this.mustCreateLabels = true;
    }

    /**
     * Determine positions for the start and end labels.
     *
     * @param dc Current draw context.
     */
    protected void determineLabelPositions(DrawContext dc)
    {
        // Do nothing, but allow subclasses to override
    }

    protected void createLabels()
    {
        // Do nothing, but allow subclasses to override
    }

    protected Label addLabel(String text)
    {
        if (this.labels == null)
            this.labels = new ArrayList<Label>();

        Label label = new Label();
        label.setText(text);
        label.setDelegateOwner(this.getActiveDelegateOwner());
        label.setTextAlign(AVKey.CENTER);
        this.labels.add(label);

        return label;
    }

    protected void computeGeometry(DrawContext dc)
    {
        // Allow the subclass to decide where to put the labels
        this.determineLabelPositions(dc);
    }

    /**
     * Determine the delegate owner for the current frame, and apply the owner to all renderable objects used to draw
     * the graphic.
     */
    protected void determineDelegateOwner()
    {
        Object owner = this.getActiveDelegateOwner();

        // Apply the delegate owner to all label objects.
        if (this.labels != null)
        {
            for (Label label : this.labels)
            {
                label.setDelegateOwner(owner);
            }
        }

        // Give subclasses a chance to apply the delegate owner to shapes they own.
        this.applyDelegateOwner(owner);
    }

    /**
     * Indicates the object attached to the pick list to represent this graphic.
     *
     * @return Delegate owner, if specified, or {@code this} if an owner is not specified.
     */
    protected Object getActiveDelegateOwner()
    {
        Object owner = this.getDelegateOwner();
        return owner != null ? owner : this;
    }

    /** Determine active attributes for this frame. */
    protected void determineActiveAttributes()
    {
        // Apply defaults for this graphic
        this.applyDefaultAttributes(this.activeShapeAttributes);

        if (this.isHighlighted())
        {
            TacticalGraphicAttributes highlightAttributes = this.getHighlightAttributes();

            // If the application specified overrides to the highlight attributes, then apply the overrides
            if (highlightAttributes != null)
            {
                this.activeOverrides.copy(highlightAttributes);

                // Apply overrides specified by application
                this.applyOverrideAttributes(highlightAttributes, this.activeShapeAttributes);
            }
            else
            {
                // If no highlight attributes have been specified we need to use the normal attributes but adjust them
                // to cause highlighting.
                this.activeShapeAttributes.setOutlineMaterial(DEFAULT_HIGHLIGHT_MATERIAL);
                this.activeShapeAttributes.setInteriorMaterial(DEFAULT_HIGHLIGHT_MATERIAL);
                this.activeShapeAttributes.setInteriorOpacity(1.0);
                this.activeShapeAttributes.setOutlineOpacity(1.0);
            }
        }
        else
        {
            // Apply overrides specified by application
            TacticalGraphicAttributes normalAttributes = this.getAttributes();
            if (normalAttributes != null)
            {
                this.activeOverrides.copy(normalAttributes);
                this.applyOverrideAttributes(normalAttributes, this.activeShapeAttributes);
            }
        }

        this.applyLabelAttributes();
    }

    /** Apply the active attributes to the graphic's labels. */
    protected void applyLabelAttributes()
    {
        if (WWUtil.isEmpty(this.labels))
            return;

        Material labelMaterial = this.getLabelMaterial();

        Font font = this.activeOverrides.getTextModifierFont();
        if (font == null)
            font = Label.DEFAULT_FONT;

        double opacity = this.getActiveShapeAttributes().getInteriorOpacity();

        for (Label label : this.labels)
        {
            label.setMaterial(labelMaterial);
            label.setFont(font);
            label.setOpacity(opacity);
        }

        // Apply the offset to the main label.
        Offset offset = this.getLabelOffset();
        if (offset == null)
            offset = this.getDefaultLabelOffset();
        this.labels.get(0).setOffset(offset);
    }

    /**
     * Indicates the default offset applied to the graphic's main label. This offset may be overridden by the graphic
     * attributes.
     *
     * @return Offset to apply to the main label.
     */
    protected Offset getDefaultLabelOffset()
    {
        return Label.DEFAULT_OFFSET;
    }

    /**
     * Get the override attributes that are active for this frame.
     *
     * @return Override attributes. Values set in this bundle override defaults specified by the symbol set.
     */
    protected TacticalGraphicAttributes getActiveOverrideAttributes()
    {
        return this.activeOverrides;
    }

    /**
     * Get the active shape attributes for this frame. The active attributes are created by applying application
     * specified overrides to the default attributes specified by the symbol set.
     *
     * @return Active shape attributes.
     */
    protected ShapeAttributes getActiveShapeAttributes()
    {
        return this.activeShapeAttributes;
    }

    /**
     * Get the Material that should be used to draw labels. If no override material has been specified, the graphic's
     * outline Material is used for the labels.
     *
     * @return The Material that should be used when drawing labels. May change each frame.
     */
    protected Material getLabelMaterial()
    {
        Material material = this.activeOverrides.getTextModifierMaterial();
        if (material != null)
            return material;
        else
            return this.activeShapeAttributes.getOutlineMaterial();
    }

    /**
     * Apply defaults to the active attributes bundle. The default attributes are determined by the type of graphic.
     * This method is called each frame to reset the active shape attributes to the appropriate default state. Override
     * attributes specified by the application may be applied after the defaults have been set.
     *
     * @param attributes Attributes bundle to receive defaults.
     */
    protected void applyDefaultAttributes(ShapeAttributes attributes)
    {
        Material material = this.getDefaultOutlineMaterial();
        attributes.setOutlineMaterial(material);

        // MIL-STD-2525C section 5.5.1.2 (pg. 37) states that graphics (in general) must be drawn with solid lines
        // when in the Present status, and dashed lines when the status is not Present. Note that the default is
        //  overridden by some graphics, which always draw with dashed lines.
        String status = this.getStatus();
        if (!SymbologyConstants.STATUS_PRESENT.equals(status))
        {
            attributes.setOutlineStippleFactor(this.getOutlineStippleFactor());
            attributes.setOutlineStipplePattern(this.getOutlineStipplePattern());
        }

        // Most 2525 area graphic do not have a fill.
        attributes.setDrawInterior(false);
    }

    /**
     * Indicates the factor applied to the stipple pattern used to draw dashed lines when the graphic is "anticipated".
     * This value is not used when the graphic is "present".
     *
     * @return Factor applied to the stipple pattern.
     *
     * @see gov.nasa.worldwind.render.ShapeAttributes#getOutlineStippleFactor()
     */
    protected int getOutlineStippleFactor()
    {
        return OUTLINE_STIPPLE_FACTOR;
    }

    /**
     * Indicates the stipple pattern used to draw dashed lines when the graphic is "anticipated".
     *
     * @return Factor applied to the stipple pattern.
     *
     * @see gov.nasa.worldwind.render.ShapeAttributes#getOutlineStipplePattern()
     */
    protected short getOutlineStipplePattern()
    {
        return OUTLINE_STIPPLE_PATTERN;
    }

    /**
     * Indicates the default outline Material for this graphic.
     *
     * @return The default outline material, determined by the graphic's standard identity.
     */
    protected Material getDefaultOutlineMaterial()
    {
        String identity = this.getStandardIdentity();
        if (SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equals(identity))
            return MATERIAL_HOSTILE;
        else
            return MATERIAL_FRIEND;
    }

    /**
     * Apply override attributes specified in a TacticalGraphicAttributes bundle to the active ShapeAttributes. Any
     * non-null properties of {@code graphicAttributes} will be applied to {@code shapeAttributes}.
     *
     * @param graphicAttributes Override attributes.
     * @param shapeAttributes   Shape attributes to receive overrides.
     */
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
            shapeAttributes.setOutlineMaterial(material);
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
