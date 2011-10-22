/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.Logging;

/**
 * Base class for tactical graphics defined by <a href="http://www.assistdocs.com/search/document_details.cfm?ident_number=114934">MIL-STD-2525</a>
 * <p/>
 * The following table lists the modifiers supported by 2525 graphics. Note that not all graphics support all modifiers.
 * <table> <tr><th>Field</th><th>Modifier key</th><th>Data type</th><th>Description</th></tr>
 * <tr><td>T</td><td>AVKey.TEXT</td><td>String</td><td>Text label</td></tr> <tr><td>A</td><td>AVKey.SYMBOL</td><td>TacticalSymbol</td><td>Symbol
 * icon</td></tr> <tr><td>W</td><td>AVKey.DATE</td><td>Date</td><td>Date/time</td></tr>
 * <tr><td>H</td><td>AVKey.DESCRIPTION</td><td>String</td><td>Additional information</td></tr>
 * <tr><td>Q</td><td>AVKey.HEADING</td><td>Angle</td><td>Direction indicator</td></tr> </table>
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
 *
 * @author pabercrombie
 * @version $Id$
 */
public abstract class MilStd2525TacticalGraphic extends AVListImpl implements TacticalGraphic
{
    protected String text;

    protected boolean highlighted;
    protected boolean visible;
    protected TacticalGraphicAttributes normalAttributes;
    protected TacticalGraphicAttributes highlightAttributes;

    protected boolean showText;

    protected String standardIdentity;
    protected String echelon;
    protected String category;
    protected String status;
    // TODO: add country code, etc.

    public abstract String getFunctionId();

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
    public boolean isShowModifier(String modifier)
    {
        //noinspection SimplifiableIfStatement
        if (AVKey.TEXT.equals(modifier))
            return this.showText;
        else
            return false;
    }

    /** {@inheritDoc} */
    public void setShowModifier(String modifier, boolean visible)
    {
        if (AVKey.TEXT.equals(modifier))
        {
            this.showText = visible;
        }
    }

    /** {@inheritDoc} */
    public void setShowAllModifiers(boolean showModifiers)
    {
        this.showText = showModifiers;
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
}
