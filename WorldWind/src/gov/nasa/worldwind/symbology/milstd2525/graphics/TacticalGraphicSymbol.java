/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * Implementation of TacticalSymbol to render point graphics defined by MIL-STD-2525C Appendix B (Tactical Graphics).
 * This class implements the logic for rendering tactical point graphics, but actually implements the TacticalSymbol
 * interface.
 * <p/>
 * This class is not meant to be used directly by applications. Instead, apps should use {@link MilStd2525PointGraphic},
 * which implements the {@link TacticalGraphic} interface. (MilStd2525PointGraphic uses TacticalGraphicSymbol internally
 * to render the point graphic.)
 *
 * @author pabercrombie
 * @version $Id$
 * @see MilStd2525PointGraphic
 */
public class TacticalGraphicSymbol extends AbstractTacticalSymbol
{
    /**
     * Object that provides the default offset for each point graphic. Most graphics are centered on their position, but
     * some require a different offset.
     */
    protected static DefaultOffsets defaultOffsets = new DefaultOffsets();

    /** Object that provides the default label layouts for each point graphic. */
    protected static DefaultLabelLayouts defaultLayouts = new DefaultLabelLayouts();

    protected static final Offset CENTER_OFFSET = Offset.fromFraction(0.5, 0.5);
    protected static final Offset BELOW_BOTTOM_CENTER_OFFSET = Offset.fromFraction(0.5, -0.1);

    public static class LabelLayout
    {
        public Offset offset;
        public Offset hotSpot;

        public LabelLayout(Offset offset, Offset hotSpot)
        {
            this.offset = offset;
            this.hotSpot = hotSpot;
        }
    }

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

    /** Indicates whether or not to render the location modifier. */
    protected boolean showLocation = true;
    /** Indicates whether or not to render the hostile/enemy modifier. */
    protected boolean showHostileIndicator = true;

    /**
     * Constructs a new symbol with the specified position. The position specifies the latitude, longitude, and altitude
     * where this symbol is drawn on the globe. The position's altitude component is interpreted according to the
     * altitudeMode.
     *
     * @param sidc     Code that identifies the graphic.
     * @param position The latitude, longitude, and altitude where the symbol is drawn.
     *
     * @throws IllegalArgumentException if the position is <code>null</code>.
     */
    public TacticalGraphicSymbol(String sidc, Position position)
    {
        super(position);

        this.symbolCode = new SymbolCode(sidc);
        this.maskedSymbolCode = this.symbolCode.toMaskedString();

        // Initialize the symbol code from the symbol identifier specified at construction.
        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);

        // Configure this tactical point graphic's icon retriever and modifier retriever with either the
        // configuration value or the default value (in that order of precedence).
        String iconRetrieverPath = Configuration.getStringValue(AVKey.MIL_STD_2525_ICON_RETRIEVER_PATH,
            MilStd2525Constants.DEFAULT_ICON_RETRIEVER_PATH);
        this.setIconRetriever(new MilStd2525PointGraphicRetriever(iconRetrieverPath));

        Offset offset = defaultOffsets.get(this.symbolCode.toMaskedString());
        this.setOffset(offset);
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.symbolCode.toString();
    }

    /**
     * Indicates whether or not this graphic will display a text indicator when the graphic represents a hostile entity.
     * See comments on {@link #setShowHostileIndicator(boolean) setShowHostileIndicator} for more information.
     *
     * @return true if an indicator may be drawn when this graphic represents a hostile entity, if supported by the
     *         graphic implementation. Note that some graphics may not display an indicator, even when representing a
     *         hostile entity.
     */
    public boolean isShowHostileIndicator()
    {
        return this.showHostileIndicator;
    }

    /**
     * Specifies whether or not to display a text indicator when the symbol or graphic represents a hostile entity. In
     * the case of MIL-STD-2525C, the indicator is the letters "ENY". The indicator is determined by the symbology set,
     * and may not apply to all graphics in the symbol set.
     *
     * @param show true if this graphic should display an indicator when this graphic represents a hostile entity and
     *             the graphic implementation supports such an indicator. Note that some graphics may not display an
     *             indicator, even when representing a hostile entity.
     */
    public void setShowHostileIndicator(boolean show)
    {
        this.showHostileIndicator = show;
    }

    /**
     * Indicates whether or not the graphic should display its location as a text modifier. Not all graphics support the
     * location modifier.
     *
     * @return true if the graphic will display the location modifier. Note that not all graphics support this
     *         modifier.
     */
    public boolean isShowLocation()
    {
        return this.showLocation;
    }

    /**
     * Specifies whether or not the graphic should display its location as a text modifier. Not all graphics support the
     * location modifier. Setting showLocation on a graphic that does not support the modifier will have no effect.
     *
     * @param show true if the graphic will display the location modifier. Note that not all graphics support this
     *             modifier.
     */
    public void setShowLocation(boolean show)
    {
        this.showLocation = show;
    }

    @Override
    protected void layoutModifiers(DrawContext dc)
    {
        if (this.iconRect == null)
            return;

        if (this.mustDrawTextModifiers(dc))
        {
            this.currentLabels.clear();
            this.doLayoutModifiers(dc, this.iconRect);
        }

        if (this.mustDrawGraphicModifiers(dc))
        {
            this.currentGlyphs.clear();
            this.currentLines.clear();
            this.layoutGraphicModifiers(dc);
        }
    }

    /**
     * Layout text and graphic modifiers around the symbol.
     *
     * @param dc       Current draw context.
     * @param iconRect Symbol's screen rectangle.
     */
    protected void doLayoutModifiers(DrawContext dc, Rectangle iconRect)
    {
        // We compute a default font rather than using a static default in order to choose a font size that is
        // appropriate for the symbol's frame height. According to the MIL-STD-2525C specification, the text modifier
        // height must be 0.3x the symbol's frame height.
        Font font = this.getActiveAttributes().getTextModifierFont();
        if (font == null)
            font = MilStd2525Util.computeTextModifierFont(iconRect.getHeight());

        Map<String, java.util.List<LabelLayout>> allLayouts = defaultLayouts.get(this.symbolCode.toMaskedString());

        for (Map.Entry<String, java.util.List<LabelLayout>> entry : allLayouts.entrySet())
        {
            String key = entry.getKey();
            java.util.List<LabelLayout> layouts = entry.getValue();

            if (WWUtil.isEmpty(layouts))
                continue;

            Object value = this.getLabelValue(key);

            // If we're retrieving the date modifier, maybe add a hyphen to the first value to indicate a date range.
            if (SymbologyConstants.DATE_TIME_GROUP.equals(key) && (value instanceof Iterable))
            {
                value = this.addHyphenToDateRange((Iterable) value, layouts);
            }

            String mode = SymbologyConstants.LOCATION.equals(key) ? LAYOUT_RELATIVE : LAYOUT_NONE;

            // Some graphics support multiple instances of the same modifier. Handle this case differently than the
            // single instance case.
            if (value instanceof Iterable)
            {
                this.layoutMultiLabel(dc, font, layouts, (Iterable) value, mode);
            }
            else if (value != null)
            {
                this.layoutLabel(dc, font, layouts.get(0), value.toString(), mode);
            }
        }
    }

    protected void layoutGraphicModifiers(DrawContext dc)
    {
        AVList retrieverParams = new AVListImpl();
        retrieverParams.setValue(AVKey.WIDTH, this.iconRect.width);

        // Direction of Movement indicator. Placed at the bottom of the symbol layout. Direction of Movement applies
        // only to CBRN graphics (see MIL-STD-2525C table XI, pg. 38).
        Object o = this.getModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT);
        if (this.isShowDirectionOfMovement() && o instanceof Angle)
        {
            // The length of the direction of movement line is equal to the height of the symbol frame. See
            // MIL-STD-2525C section 5.3.4.1.c, page 33.
            double length = this.iconRect.getHeight();

            java.util.List<? extends Point2D> points = MilStd2525Util.computeGroundHeadingIndicatorPoints(dc, (Angle) o,
                length, this.iconRect.getHeight());
            this.addLine(dc, BELOW_BOTTOM_CENTER_OFFSET, points, LAYOUT_RELATIVE, points.size() - 1);
        }
    }

    //////////////////////////////////////////////
    // Modifier layout
    //////////////////////////////////////////////

    /**
     * Add a hyphen to the first element in a list of dates to indicate a date range. This method only modifiers the
     * date list if exactly two dates are displayed in the graphic.
     *
     * @param value   Iterable of date modifiers.
     * @param layouts Layouts for the date modifiers.
     *
     * @return Iterable of modified dates. This may be a new, modified list, or the same list as {@code value} if no
     *         modification was required.
     */
    protected Iterable addHyphenToDateRange(Iterable value, java.util.List<LabelLayout> layouts)
    {
        // Only add a hyphen if exactly two dates are displayed in the graphic.
        if (layouts.size() != 2)
            return value;

        // Make sure that two date values are provided.
        Iterator iterator = value.iterator();
        Object date1 = iterator.hasNext() ? iterator.next() : null;
        Object date2 = iterator.hasNext() ? iterator.next() : null;

        // If only two dates were provided, add a hyphen to indicate a date range. If more or less
        // date were provided it's not a date range, so don't change anything.
        if (date1 != null && date2 != null)
        {
            return Arrays.asList(date1 + "-", date2);
        }
        return value;
    }

    protected void layoutLabel(DrawContext dc, Font font, LabelLayout layout, String value, String mode)
    {
        if (!WWUtil.isEmpty(value))
        {
            this.addLabel(dc, layout.offset, layout.hotSpot, value, font, null, mode);
        }
    }

    protected void layoutMultiLabel(DrawContext dc, Font font, java.util.List<LabelLayout> layouts, Iterable values,
        String mode)
    {
        Iterator valueIterator = values.iterator();
        Iterator<LabelLayout> layoutIterator = layouts.iterator();

        while (layoutIterator.hasNext() && valueIterator.hasNext())
        {
            LabelLayout layout = layoutIterator.next();
            Object value = valueIterator.next();
            if (value != null)
            {
                this.layoutLabel(dc, font, layout, value.toString(), mode);
            }
        }
    }

    protected Object getLabelValue(String key)
    {
        Object value = null;
        if (SymbologyConstants.HOSTILE_ENEMY.equals(key) && this.isShowHostileIndicator())
        {
            if (SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equals(this.symbolCode.getStandardIdentity()))
            {
                value = SymbologyConstants.HOSTILE_ENEMY;
            }
        }
        else if (SymbologyConstants.TYPE.equals(key))
        {
            value = this.getType();
        }
        else if (SymbologyConstants.LOCATION.equals(key) && this.isShowLocation())
        {
            value = this.getModifier(key); // TODO compute from actual location
        }
        else
        {
            value = this.getModifier(key);
        }
        return value;
    }

    /**
     * Indicates the Type modifier. This modifier is only used by Nuclear/Chemical/Biological graphics. In the case of
     * Nuclear graphics the modifier is specified by the application. In the case of chemical or biological this method
     * returns the string "CML" or "BIO".
     *
     * @return The value of the type modifier. Returns null if no type modifier has been set, and the graphics is not
     *         Chemical or Biological.
     */
    protected String getType()
    {
        if (TacGrpSidc.MOBSU_CBRN_REEVNT_BIO.equals(this.maskedSymbolCode))
            return "BIO";
        else if (TacGrpSidc.MOBSU_CBRN_REEVNT_CML.equals(this.maskedSymbolCode))
            return "CML";
        else
            return (String) this.getModifier(SymbologyConstants.TYPE);
    }

    /**
     * Indicates whether or not this graphic supports the direction of movement indicator. Only chemical, biological,
     * radiological, and nuclear point graphics support this modifier  (see MIL-STD-2525C, table XI, pg. 38).
     *
     * @return True if the graphic is chemical, biological, radiological, or nuclear.
     */
    protected boolean isShowDirectionOfMovement()
    {
        String code = this.maskedSymbolCode;

        return TacGrpSidc.MOBSU_CBRN_NDGZ.equals(code)
            || TacGrpSidc.MOBSU_CBRN_FAOTP.equals(code) // TODO: does this graphic support direction of movement?
            || TacGrpSidc.MOBSU_CBRN_REEVNT_BIO.equals(code)
            || TacGrpSidc.MOBSU_CBRN_REEVNT_CML.equals(code);
    }
}
