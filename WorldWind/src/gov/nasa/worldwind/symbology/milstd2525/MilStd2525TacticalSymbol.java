/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;

/**
 * Implementation of {@link gov.nasa.worldwind.symbology.TacticalSymbol} that provides support for tactical symbols from
 * the <a href="http://www.assistdocs.com/search/document_details.cfm?ident_number=114934">MIL-STD-2525</a> symbology
 * set. See the <a title="Tactical Symbol Usage Guide" href="http://goworldwind.org/developers-guide/symbology/tactical-symbols/"
 * target="_blank">Tactical Symbol Usage Guide</a> for instructions on using TacticalSymbol in an application.
 *
 * @author dcollins
 * @version $Id$
 */
public class MilStd2525TacticalSymbol extends AbstractTacticalSymbol
{
    protected static final Offset CENTER_OFFSET = Offset.fromFraction(0.5, 0.5);
    protected static final Offset BOTTOM_CENTER_OFFSET = Offset.fromFraction(0.5, 0.0);
    protected static final Offset TOP_CENTER_OFFSET = Offset.fromFraction(0.5, 1.0);
    protected static final Offset LEFT_CENTER_OFFSET = Offset.fromFraction(0.0, 0.5);
    protected static final Offset RIGHT_CENTER_OFFSET = Offset.fromFraction(1.0, 0.5);

    /**
     * Indicates a string identifier for this symbol. The format of the identifier depends on the symbol set to which
     * this symbol belongs. For symbols belonging to the MIL-STD-2525 symbol set, this returns a 15-character
     * alphanumeric symbol identification code (SIDC). Calculated from the current modifiers at construction and during
     * each call to {@link #setModifier(String, Object)}. Initially <code>null</code>.
     */
    protected SymbolCode symbolCode;
    protected boolean isGroundSymbol;
    protected boolean useGroundHeadingIndicator;

    /**
     * Constructs a tactical symbol for the MIL-STD-2525 symbology set with the specified symbol identifier and
     * position. This constructor does not accept any supplemental modifiers, so the symbol contains only the attributes
     * specified by its symbol identifier. This constructor does not accept any icon retrieval path, so the created
     * symbol retrieves its icons from the default location.
     * <p/>
     * The symbolId specifies the tactical symbol's appearance. The symbolId must be a 15-character alphanumeric symbol
     * identification code (SIDC). The symbol's shape, fill color, outline color, and icon are all defined by the symbol
     * identifier. Use the '-' character to specify null entries in the symbol identifier.
     * <p/>
     * The position specifies the latitude, longitude, and altitude where the symbol is drawn on the globe. The
     * position's altitude component is interpreted according to the altitudeMode.
     *
     * @param symbolId a 15-character alphanumeric symbol identification code (SIDC).
     * @param position the latitude, longitude, and altitude where the symbol is drawn.
     *
     * @throws IllegalArgumentException if either the symbolId or the position are <code>null</code>, or if the symbolId
     *                                  is not a valid 15-character alphanumeric symbol identification code (SIDC).
     */
    public MilStd2525TacticalSymbol(String symbolId, Position position)
    {
        super(position);

        this.init(symbolId, null);
    }

    /**
     * Constructs a tactical symbol for the MIL-STD-2525 symbology set with the specified symbol identifier, position,
     * and list of modifiers. This constructor does not accept any icon retrieval path, so the created symbol retrieves
     * its icons from the default location.
     * <p/>
     * The symbolId specifies the tactical symbol's appearance. The symbolId must be a 15-character alphanumeric symbol
     * identification code (SIDC). The symbol's shape, fill color, outline color, and icon are all defined by the symbol
     * identifier. Use the '-' character to specify null entries in the symbol identifier.
     * <p/>
     * The position specifies the latitude, longitude, and altitude where the symbol is drawn on the globe. The
     * position's altitude component is interpreted according to this symbol's altitudeMode.
     * <p/>
     * The modifiers specify supplemental graphic and text attributes as key-value pairs. See the
     * MilStd2525TacticalSymbol class documentation for the list of recognized modifiers. In the case where both the
     * symbol identifier and the modifiers list specify the same attribute, the modifiers list has priority.
     *
     * @param symbolId  a 15-character alphanumeric symbol identification code (SIDC).
     * @param position  the latitude, longitude, and altitude where the symbol is drawn.
     * @param modifiers an optional list of key-value pairs specifying the symbol's modifiers. May be <code>null</code>
     *                  to specify that the symbol contains only the attributes in its symbol identifier.
     *
     * @throws IllegalArgumentException if either the symbolId or the position are <code>null</code>, or if the symbolId
     *                                  is not a valid 15-character alphanumeric symbol identification code (SIDC).
     */
    public MilStd2525TacticalSymbol(String symbolId, Position position, AVList modifiers)
    {
        super(position);

        this.init(symbolId, modifiers);
    }

    protected void init(String symbolId, AVList modifiers)
    {
        // Initialize the symbol code from the symbol identifier specified at construction.
        this.symbolCode = new SymbolCode(symbolId);
        // Parse the symbol code's 2-character modifier code and store the resulting pairs in the modifiers list.
        SymbolCode.parseSymbolModifierCode(this.symbolCode.getSymbolModifier(), this.modifiers);
        // Apply any caller-specified key-value pairs to the modifiers list. We apply these pairs last to give them
        // precedence.
        if (modifiers != null)
            this.modifiers.setValues(modifiers);

        // Configure this tactical symbol's icon retriever and modifier retriever with either the configuration value or
        // the default value (in that order of precedence). Note that the empty string is valid and indicates that icons
        // are retrieved from the class path.
        String iconRetrieverPath = Configuration.getStringValue(AVKey.MIL_STD_2525_ICON_RETRIEVER_PATH,
            MilStd2525Constants.DEFAULT_ICON_RETRIEVER_PATH);
        this.setIconRetriever(new MilStd2525IconRetriever(iconRetrieverPath));
        this.setModifierRetriever(new MilStd2525ModifierRetriever(iconRetrieverPath));

        // Initialize this tactical symbol's icon offset, icon size, and altitude mode from its symbol code.
        this.initIconLayout();
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.symbolCode.toString();
    }

    /**
     * Indicates whether this symbol draws its frame and icon. See {@link #setShowFrameAndIcon(boolean)} for a
     * description of how this property is used.
     *
     * @return true if this symbol draws its frame and icon, otherwise false.
     */
    public boolean isShowFrameAndIcon()
    {
        return false; // TODO: replace with separate controls: isShowFrame, isShowFill, isShowIcon
    }

    /**
     * Specifies whether to draw this symbol's frame and icon. The showFrameAndIcon property provides control over this
     * tactical symbol's display option hierarchy as defined by MIL-STD-2525C, section 5.4.5 and table III.
     * <p/>
     * When true, this symbol's frame, icon, and fill are drawn, and any enabled modifiers are drawn on and around the
     * frame. This state corresponds to MIL-STD-2525C, table III, row 1.
     * <p/>
     * When false, this symbol's frame, icon, and modifiers are not drawn. Instead, a filled dot is drawn at this
     * symbol's position, and is colored according to this symbol's normal fill color. The TacticalSymbolAttributes'
     * scale property specifies the dot's diameter in screen pixels. This state corresponds to MIL-STD-2525C, table III,
     * row 7.
     *
     * @param showFrameAndIcon true to draw this symbol's frame and icon, otherwise false.
     */
    public void setShowFrameAndIcon(boolean showFrameAndIcon)
    {
        // TODO: replace with separate controls: setShowFrame, setShowFill, setShowIcon
    }

    protected void initIconLayout()
    {
        MilStd2525Util.SymbolInfo info = MilStd2525Util.computeTacticalSymbolInfo(this.getIdentifier());
        if (info == null)
            return;

        this.iconOffset = info.iconOffset;
        this.iconSize = info.iconSize;

        if (info.offset != null)
            this.setOffset(info.offset);

        if (info.isGroundSymbol)
        {
            this.isGroundSymbol = true;
            this.useGroundHeadingIndicator = info.offset == null;
            this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        }
    }

    @Override
    protected void layoutModifiers(DrawContext dc)
    {
        if (this.iconRect == null)
            return;

        // Layout all of the graphic and text modifiers around the symbol's frame bounds. The location of each modifier
        // is the same regardless of whether the symbol is framed or unframed. See MIL-STD-2525C section 5.4.4, page 34.

        if (this.mustDrawGraphicModifiers(dc))
        {
            this.currentGlyphs.clear();
            this.currentLines.clear();
            this.layoutGraphicModifiers(dc);
        }

        if (this.mustDrawTextModifiers(dc))
        {
            this.currentLabels.clear();
            this.layoutTextModifiers(dc);
        }
    }

    protected void layoutGraphicModifiers(DrawContext dc)
    {
        AVList retrieverParams = new AVListImpl();
        retrieverParams.setValue(AVKey.WIDTH, this.iconRect.width);

        // Feint/Dummy Indicator modifier. Placed above the icon.
        String modifierCode = this.getModifierCode(SymbologyConstants.FEINT_DUMMY);
        if (modifierCode != null)
        {
            this.addGlyph(dc, TOP_CENTER_OFFSET, BOTTOM_CENTER_OFFSET, modifierCode, retrieverParams, null);
        }

        // Installation modifier. Placed at the top of the symbol layout.
        modifierCode = this.getModifierCode(SymbologyConstants.INSTALLATION);
        if (modifierCode != null)
        {
            this.addGlyph(dc, TOP_CENTER_OFFSET, BOTTOM_CENTER_OFFSET, modifierCode, null, LAYOUT_RELATIVE);
        }

        // Echelon / Task Force Indicator modifier. Placed at the top of the symbol layout.
        modifierCode = this.getModifierCode(SymbologyConstants.TASK_FORCE);
        if (modifierCode != null)
        {
            this.addGlyph(dc, TOP_CENTER_OFFSET, BOTTOM_CENTER_OFFSET, modifierCode, null, LAYOUT_RELATIVE);
        }
        // Echelon modifier. Placed at the top of the symbol layout.
        else if ((modifierCode = this.getModifierCode(SymbologyConstants.ECHELON)) != null)
        {
            this.addGlyph(dc, TOP_CENTER_OFFSET, BOTTOM_CENTER_OFFSET, modifierCode, null, LAYOUT_RELATIVE);
        }

        // Mobility Indicator modifier. Placed at the bottom of the symbol layout.
        modifierCode = this.getModifierCode(SymbologyConstants.MOBILITY);
        if (modifierCode != null)
        {
            this.addGlyph(dc, BOTTOM_CENTER_OFFSET, TOP_CENTER_OFFSET, modifierCode, null, LAYOUT_RELATIVE);
        }

        // Auxiliary Equipment Indicator modifier. Placed at the bottom of the symbol layout.
        modifierCode = this.getModifierCode(SymbologyConstants.AUXILIARY_EQUIPMENT);
        if (modifierCode != null)
        {
            this.addGlyph(dc, BOTTOM_CENTER_OFFSET, TOP_CENTER_OFFSET, modifierCode, null, LAYOUT_RELATIVE);
        }

        if (SymbologyConstants.SCHEME_EMERGENCY_MANAGEMENT.equalsIgnoreCase(this.symbolCode.getScheme()))
        {
            // Alternate Status/Operational Condition. Used by the Emergency Management scheme. Placed at the bottom of
            // the symbol layout.
            modifierCode = this.getModifierCode(SymbologyConstants.OPERATIONAL_CONDITION_ALTERNATE);
            if (modifierCode != null)
            {
                this.addGlyph(dc, BOTTOM_CENTER_OFFSET, TOP_CENTER_OFFSET, modifierCode, retrieverParams,
                    LAYOUT_RELATIVE);
            }
        }
        else
        {
            // Status/Operational Condition. Used by all schemes except the Emergency Management scheme. Centered on
            // the icon.
            modifierCode = this.getModifierCode(SymbologyConstants.OPERATIONAL_CONDITION);
            if (modifierCode != null)
            {
                this.addGlyph(dc, CENTER_OFFSET, CENTER_OFFSET, modifierCode, null, null);
            }
        }

        // Direction of Movement indicator. Placed either at the center of the icon or at the bottom of the symbol
        // layout.
        Object o = this.getModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT);
        if (o != null && o instanceof Angle)
        {
            // The length of the direction of movement line is equal to the height of the symbol frame. See
            // MIL-STD-2525C section 5.3.4.1.c, page 33.
            double length = this.iconRect.getHeight();
            Object d = this.getModifier(SymbologyConstants.SPEED_LEADER_SCALE);
            if (d != null && d instanceof Number)
                length *= ((Number) d).doubleValue();

            if (this.useGroundHeadingIndicator)
            {
                List<? extends Point2D> points = MilStd2525Util.computeGroundHeadingIndicatorPoints(dc, (Angle) o,
                    length, this.iconRect.getHeight());
                this.addLine(dc, BOTTOM_CENTER_OFFSET, points, LAYOUT_RELATIVE, points.size() - 1);
            }
            else
            {
                List<? extends Point2D> points = MilStd2525Util.computeCenterHeadingIndicatorPoints(dc, (Angle) o,
                    length);
                this.addLine(dc, CENTER_OFFSET, points, null, 0);
            }
        }
    }

    protected void layoutTextModifiers(DrawContext dc)
    {
        StringBuilder sb = new StringBuilder();

        // We compute a default font rather than using a static default in order to choose a font size that is
        // appropriate for the symbol's frame height. According to the MIL-STD-2525C specification, the text modifier
        // height must be 0.3x the symbol's frame height.
        Font font = this.getActiveAttributes().getTextModifierFont();
        if (font == null)
            font = MilStd2525Util.computeTextModifierFont(this.iconRect.getHeight());

        // Quantity modifier layout. Placed at the top of the symbol layout.
        this.appendTextModifier(sb, SymbologyConstants.QUANTITY, 9);
        if (sb.length() > 0)
        {
            this.addLabel(dc, TOP_CENTER_OFFSET, BOTTOM_CENTER_OFFSET, sb.toString(), font, null, LAYOUT_RELATIVE);
            sb.delete(0, sb.length());
        }

        // Special C2 Headquarters modifier layout. Centered on the icon.
        this.appendTextModifier(sb, SymbologyConstants.SPECIAL_C2_HEADQUARTERS, 9);
        if (sb.length() > 0)
        {
            this.addLabel(dc, CENTER_OFFSET, CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Reinforced/Reduced modifier layout (Frame Shape modifier is handled by IconRetriever).
        Object o = this.getModifier(SymbologyConstants.REINFORCED_REDUCED);
        if (o != null && o.toString().equalsIgnoreCase(SymbologyConstants.REINFORCED))
            sb.append("+");
        else if (o != null && o.toString().equalsIgnoreCase(SymbologyConstants.REDUCED))
            sb.append("-");
        else if (o != null && o.toString().equalsIgnoreCase(SymbologyConstants.REINFORCED_AND_REDUCED))
            sb.append("+-"); // TODO: get the string for + over -
        if (sb.length() > 0)
        {
            // TODO: adjust location to edge of frame shape when present.
            Offset offset = Offset.fromFraction(1.1, 1.1);
            this.addLabel(dc, offset, LEFT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Staff Comments modifier layout.
        this.appendTextModifier(sb, SymbologyConstants.STAFF_COMMENTS, 20);
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(1.1, 0.8), LEFT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Additional Information modifier layout.
        this.appendTextModifier(sb, SymbologyConstants.ADDITIONAL_INFORMATION, 20);
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(1.1, 0.5), LEFT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Higher Formation modifier layout.
        this.appendTextModifier(sb, SymbologyConstants.HIGHER_FORMATION, 21);
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(1.1, 0.2), LEFT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Evaluation Rating, Combat Effectiveness, Signature Equipment, Hostile (Enemy), and IFF/SIF modifier
        // layout.
        this.appendTextModifier(sb, SymbologyConstants.EVALUATION_RATING, 2); // TODO: validate value
        this.appendTextModifier(sb, SymbologyConstants.COMBAT_EFFECTIVENESS, 3);
        this.appendTextModifier(sb, SymbologyConstants.SIGNATURE_EQUIPMENT, 1); // TODO: validate value
        this.appendTextModifier(sb, SymbologyConstants.HOSTILE_ENEMY, 3); // TODO: compute value from standard identity
        this.appendTextModifier(sb, SymbologyConstants.IFF_SIF, 5);
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(1.1, -0.1), LEFT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Date-Time-Group (DTG) modifier layout.
        this.appendTextModifier(sb, SymbologyConstants.DATE_TIME_GROUP, 16); // TODO: compute value from modifier
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(-0.1, 1.1), RIGHT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Altitude/Depth and Location modifier layout.
        this.appendTextModifier(sb, SymbologyConstants.ALTITUDE_DEPTH, 14); // TODO: compute value from position
        this.appendTextModifier(sb, SymbologyConstants.LOCATION, 19); // TODO: compute value from position
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(-0.1, 0.8), RIGHT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Type modifier layout.
        this.appendTextModifier(sb, SymbologyConstants.TYPE, 24);
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(-0.1, 0.5), RIGHT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Unique Designation modifier layout.
        this.appendTextModifier(sb, SymbologyConstants.UNIQUE_DESIGNATION, 21);
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(-0.1, 0.2), RIGHT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }

        // Speed modifier layout.
        this.appendTextModifier(sb, SymbologyConstants.SPEED, 8);
        if (sb.length() > 0)
        {
            this.addLabel(dc, Offset.fromFraction(-0.1, -0.1), RIGHT_CENTER_OFFSET, sb.toString(), font, null, null);
            sb.delete(0, sb.length());
        }
    }

    protected String getModifierCode(String modifier)
    {
        return SymbolCode.composeSymbolModifierCode(this.symbolCode, this.modifiers, modifier);
    }

    protected void appendTextModifier(StringBuilder sb, String modifierKey, Integer maxLength)
    {
        Object modifierValue = this.getModifier(modifierKey);
        if (WWUtil.isEmpty(modifierValue))
            return;

        String modifierText = modifierValue.toString();
        int len = maxLength != null && maxLength < modifierText.length() ? maxLength : modifierText.length();

        if (sb.length() > 0)
            sb.append(" ");

        sb.append(modifierText, 0, len);
    }

    @Override
    protected void computeTransform(DrawContext dc)
    {
        super.computeTransform(dc);

        // Compute an appropriate default offset if the application has not specified an offset and this symbol has no
        // default offset.
        if (this.getOffset() == null && this.iconRect != null && this.layoutRect != null && this.isGroundSymbol)
        {
            this.dx = -this.iconRect.getCenterX();
            this.dy = -this.layoutRect.getMinY();
        }
        else if (this.getOffset() == null && this.iconRect != null)
        {
            this.dx = -this.iconRect.getCenterX();
            this.dy = -this.iconRect.getCenterY();
        }
    }
}
