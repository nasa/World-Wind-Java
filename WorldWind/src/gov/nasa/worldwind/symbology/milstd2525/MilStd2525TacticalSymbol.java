/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Position;

/**
 * Implementation of {@link gov.nasa.worldwind.symbology.TacticalSymbol} that provides support for tactical symbols from
 * the <a href="http://www.assistdocs.com/search/document_details.cfm?ident_number=114934">MIL-STD-2525</a> symbology
 * set. See the TacticalSymbol <a title="Tactical Symbol Usage Guide" href="http://goworldwind.org/developers-guide/symbology/tactical-symbols/"
 * target="_blank">Usage Guide</a> for instructions on using TacticalSymbol in an application.
 * <p/>
 * <strong>Note</strong>: MilStd2525TacticalSymbol is currently an in-development stub class, and does not yet implement
 * the TacticalSymbol interface or any of its functionality.
 *
 * @author dcollins
 * @version $Id$
 */
public class MilStd2525TacticalSymbol
{
    /**
     * Constructs a tactical symbol for the MIL-STD-2525 symbology set with the specified symbol identifier and
     * position. This constructor does not accept any supplemental modifiers, so the symbol contains only the attributes
     * specified by its symbol identifier.
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
    }

    /**
     * Constructs a tactical symbol for the MIL-STD-2525 symbology set with the specified symbol identifier, position,
     * and list of modifiers.
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
    }

    /**
     * Indicates whether this symbol draws its frame and icon. See {@link #setShowFrameAndIcon(boolean)} for a
     * description of how this property is used.
     *
     * @return true if this symbol draws its frame and icon, otherwise false.
     */
    public boolean isShowFrameAndIcon()
    {
        return false;
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
    }
}
