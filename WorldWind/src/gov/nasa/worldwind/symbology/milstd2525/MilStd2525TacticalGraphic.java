/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;

/**
 * Base class for tactical graphics defined by <a href="http://www.assistdocs.com/search/document_details.cfm?ident_number=114934">MIL-STD-2525</a>.
 * See the TacticalGraphic <a title="Tactical Graphic Usage Guide" href="http://goworldwind.org/developers-guide/symbology/tactical-graphics/"
 * target="_blank">Usage Guide</a> for instructions on using TacticalGraphic in an application.
 * <p/>
 * The following table lists the modifiers supported by 2525 graphics. Note that not all graphics support all modifiers.
 * <table width="100%"> <tr><th>Field</th><th>Modifier key</th><th>Data type</th><th>Description</th></tr>
 * <tr><td>A</td><td>SymbologyConstants.SYMBOL</td><td>String</td><td>SIDC for a MIL-STD-2525 Tactical Symbol</td></tr>
 * <tr><td>B</td><td>SymbologyConstants.ECHELON</td><td>String</td><td>Echelon</td></tr>
 * <tr><td>C</td><td>SymbologyConstants.QUANTITY</td><td>String</td><td>Quantity</td></tr>
 * <tr><td>H</td><td>SymbologyConstants.ADDITIONAL_INFO</td><td>String</td><td>Additional information</td></tr>
 * <tr><td>Q</td><td>SymbologyConstants.DIRECTION_OF_MOVEMENT</td><td>{@link gov.nasa.worldwind.geom.Angle}</td><td>Direction
 * indicator</td></tr> <tr><td>S</td><td>SymbologyConstants.OFFSET</td><td>{@link gov.nasa.worldwind.render.Offset}</td><td>Offset
 * location indicator</td></tr> <tr><td>T</td><td>SymbologyConstants.UNIQUE_DESIGNATION</td><td>String</td><td>Unique
 * designation</td></tr> <tr><td>V</td><td>SymbologyConstants.TYPE</td><td>String</td><td>Type</td></tr>
 * <tr><td>W</td><td>SymbologyConstants.DATE_TIME_GROUP</td><td>String</td><td>Date/time</td></tr>
 * <tr><td>X</td><td>SymbologyConstants.ALTITUDE_DEPTH</td><td>Double</td><td>Altitude/depth</td></tr>
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
public abstract class MilStd2525TacticalGraphic extends AbstractTacticalGraphic implements TacticalGraphic, Renderable
{
    public final static Material MATERIAL_FRIEND = Material.BLACK;
    public final static Material MATERIAL_HOSTILE = Material.RED;

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

    protected MilStd2525TacticalGraphic(String symbolCode)
    {
        this.symbolCode = new SymbolCode(symbolCode);
        this.maskedSymbolCode = this.symbolCode.toMaskedString();

        // Use the same default units format as 2525 tactical symbols.
        this.setUnitsFormat(MilStd2525TacticalSymbol.DEFAULT_UNITS_FORMAT);
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.symbolCode.toString();
    }

    /** {@inheritDoc} */
    @Override
    public Object getModifier(String modifier)
    {
        if (SymbologyConstants.UNIQUE_DESIGNATION.equals(modifier) && this.text != null)
        {
            return this.text;
        }
        return super.getModifier(modifier);
    }

    /** {@inheritDoc} */
    @Override
    public void setModifier(String modifier, Object value)
    {
        if (SymbologyConstants.UNIQUE_DESIGNATION.equals(modifier) && (value instanceof String))
        {
            this.setText((String) value);
        }
        else
        {
            super.setModifier(modifier, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getText()
    {
        return this.text;
    }

    /**
     * Indicates whether or not the graphic must display the hostile/enemy indicator, if the graphic supports the
     * indicator.
     *
     * @return true if {@link #isShowHostileIndicator()} is true, and the graphic represents a hostile entity.
     */
    protected boolean mustShowHostileIndicator()
    {
        String id = this.symbolCode.getStandardIdentity();
        boolean isHostile = SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equalsIgnoreCase(id)
            || SymbologyConstants.STANDARD_IDENTITY_SUSPECT.equalsIgnoreCase(id)
            || SymbologyConstants.STANDARD_IDENTITY_FAKER.equalsIgnoreCase(id)
            || SymbologyConstants.STANDARD_IDENTITY_JOKER.equalsIgnoreCase(id);

        return this.isShowHostileIndicator() && isHostile;
    }

    /**
     * Apply defaults to the active attributes bundle. The default attributes are determined by the type of graphic.
     * This method is called each frame to reset the active shape attributes to the appropriate default state. Override
     * attributes specified by the application may be applied after the defaults have been set.
     *
     * @param attributes Attributes bundle to receive defaults.
     */
    @Override
    protected void applyDefaultAttributes(ShapeAttributes attributes)
    {
        Material material = this.getDefaultMaterial();
        attributes.setOutlineMaterial(material);
        attributes.setInteriorMaterial(material);

        // MIL-STD-2525C section 5.5.1.2 (pg. 37) states that graphics (in general) must be drawn with solid lines
        // when in the Present status, and dashed lines when the status is not Present. Note that the default is
        //  overridden by some graphics, which always draw with dashed lines.
        String status = this.symbolCode.getStatus();
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
     * Indicates the default Material for this graphic.
     *
     * @return The default material, determined by the graphic's standard identity.
     */
    protected Material getDefaultMaterial()
    {
        String identity = this.symbolCode.getStandardIdentity();
        if (SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equals(identity))
            return MATERIAL_HOSTILE;
        else
            return MATERIAL_FRIEND;
    }

    protected TacticalSymbol createSymbol(String sidc, Position position, TacticalSymbolAttributes attrs)
    {
        TacticalSymbol symbol = new MilStd2525TacticalSymbol(sidc,
            position != null ? position : Position.ZERO);
        symbol.setDelegateOwner(this);
        symbol.setAttributes(attrs);
        symbol.setShowTextModifiers(false);
        return symbol;
    }
}
