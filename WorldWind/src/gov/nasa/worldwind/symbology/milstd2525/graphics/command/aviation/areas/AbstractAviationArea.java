/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.areas;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.GeneralArea;

import java.awt.*;
import java.util.*;

/**
 * Base class for Aviation Area graphics.
 *
 * @author pabercrombie
 * @version $Id$
 */
abstract public class AbstractAviationArea extends GeneralArea
{
    protected SurfaceText textLabel;
    protected SurfaceText timeFromLabel;
    protected SurfaceText timeToLabel;
    protected SurfaceText minAltLabel;
    protected SurfaceText maxAltLabel;

    /** Create a new aviation area. */
    public AbstractAviationArea()
    {
        // Do not draw "ENY" labels on hostile entities.
        this.setShowIdentityLabels(false);
    }

    /** {@inheritDoc} */
    @Override
    public void preRender(DrawContext dc)
    {
        if (this.textLabel == null) // TODO text modifier may not be set, need to look at other modifiers too
            this.createLabels();

        super.preRender(dc);

        if (this.textLabel != null)
            this.textLabel.preRender(dc);

        if (this.minAltLabel != null)
            this.minAltLabel.preRender(dc);

        if (this.maxAltLabel != null)
            this.maxAltLabel.preRender(dc);

        if (this.timeFromLabel != null)
            this.timeFromLabel.preRender(dc);

        if (this.timeToLabel != null)
            this.timeToLabel.preRender(dc);
    }

    protected void createLabels()
    {
        Offset offset = new Offset(0.0, 0.0, AVKey.PIXELS, AVKey.PIXELS);

        if (this.label != null)
        {
            this.label.setOffset(offset);
        }

        Object o = this.getModifier(AVKey.TEXT);
        if (o instanceof String)
        {
            this.textLabel = new SurfaceText((String) o, Position.ZERO);
            this.textLabel.setOffset(offset);
        }

        Object[] altitudes = this.getAltitudeRange();
        if (altitudes[0] != null)
        {
            this.minAltLabel = new SurfaceText("MIN ALT: " + o, Position.ZERO);
            this.minAltLabel.setOffset(offset);
        }

        if (altitudes[1] != null)
        {
            this.maxAltLabel = new SurfaceText("MAX ALT: " + o, Position.ZERO);
            this.maxAltLabel.setOffset(offset);
        }

        Object[] dates = this.getDateRange();
        if (dates[0] != null)
        {
            this.timeFromLabel = new SurfaceText("TIME FROM: " + dates[0], Position.ZERO);
            this.timeFromLabel.setOffset(offset);
        }

        if (dates[1] != null)
        {
            this.timeToLabel = new SurfaceText("TIME TO: " + dates[1], Position.ZERO);
            this.timeToLabel.setOffset(offset);
        }
    }

    /**
     * Get the altitude range from the graphic's modifiers. This method looks at the value of the
     * <code>AVKey.ALTITUDE</code> modifier, and returns the results as a two element array. If the value of the
     * modifier is an <code>Iterable</code>, then this method returns the first two values of the iteration. If the
     * value of the modifier is a single object, this method returns an array containing that object and
     * <code>null</code>.
     *
     * @return A two element array containing the altitude modifiers. One or both elements may be null.
     */
    protected Object[] getAltitudeRange()
    {
        Object alt1 = null;
        Object alt2 = null;

        Object o = this.getModifier(AVKey.ALTITUDE);
        if (o instanceof Iterable)
        {
            Iterator iterator = ((Iterable) o).iterator();
            if (iterator.hasNext())
            {
                alt1 = iterator.next();
            }

            if (iterator.hasNext())
            {
                alt2 = iterator.next();
            }
        }
        else
        {
            alt1 = o;
        }

        return new Object[] {alt1, alt2};
    }

    /**
     * Get the date range from the graphic's modifiers. This method looks at the value of the
     * <code>AVKey.DATE_TIME</code> modifier, and returns the results as a two element array. If either value is an
     * instance of {@link Date}, the date will be formatted to a String using the active date format. If the value of
     * the modifier is an <code>Iterable</code>, then this method returns the first two values of the iteration. If the
     * value of the modifier is a single object, this method returns an array containing that object and
     * <code>null</code>.
     *
     * @return A two element array containing the altitude modifiers. One or both elements may be null.
     */
    protected Object[] getDateRange()
    {
        Object date1 = null;
        Object date2 = null;

        Object o = this.getModifier(AVKey.DATE_TIME);
        if (o instanceof Iterable)
        {
            Iterator iterator = ((Iterable) o).iterator();
            if (iterator.hasNext())
            {
                o = iterator.next();
                if (o instanceof Date)
                    date1 = this.formatDate((Date) o);
                else
                    date1 = o;
            }

            if (iterator.hasNext())
            {
                o = iterator.next();
                if (o instanceof Date)
                    date2 = this.formatDate((Date) o);
                else
                    date2 = o;
            }
        }
        else
        {
            date1 = o;
        }

        return new Object[] {date1, date2};
    }

    @Override
    protected void determineLabelPosition(DrawContext dc)
    {
        Position northWest = Position.fromDegrees(-90.0, 180.0);

        // TODO this algorithm works ok for a rectangular-ish area, but it's won't work for a circular area
        Iterable<? extends Position> positions = this.getPositions();
        for (Position p : positions)
        {
            if (p.latitude.compareTo(northWest.latitude) > 0 && p.longitude.compareTo(northWest.longitude) < 0)
            {
                northWest = p;
            }
        }

        Angle textHeight = Angle.fromRadians(
            SurfaceText.DEFAULT_TEXT_SIZE_IN_METERS * 1.25 / dc.getGlobe().getRadius());

        Position position = northWest;

        position = new Position(
            Position.greatCircleEndPosition(position, Angle.fromDegrees(135.0) /* South east */, textHeight), 0);
        position = new Position(Position.greatCircleEndPosition(position, Angle.POS180, textHeight), 0);

        if (this.label != null)
        {
            this.label.setPosition(position);
        }

        if (this.textLabel != null)
        {
            position = new Position(Position.greatCircleEndPosition(position, Angle.POS180, textHeight), 0);
            textLabel.setPosition(position);
        }

        if (this.minAltLabel != null)
        {
            position = new Position(Position.greatCircleEndPosition(position, Angle.POS180, textHeight), 0);
            minAltLabel.setPosition(position);
        }

        if (this.maxAltLabel != null)
        {
            position = new Position(Position.greatCircleEndPosition(position, Angle.POS180, textHeight), 0);
            maxAltLabel.setPosition(position);
        }

        if (this.timeFromLabel != null)
        {
            position = new Position(Position.greatCircleEndPosition(position, Angle.POS180, textHeight), 0);
            timeFromLabel.setPosition(position);
        }

        if (this.timeToLabel != null)
        {
            position = new Position(Position.greatCircleEndPosition(position, Angle.POS180, textHeight), 0);
            timeToLabel.setPosition(position);
        }
    }

    @Override
    protected void determineLabelAttributes()
    {
        super.determineLabelAttributes();

        Color color = this.getLabelMaterial().getDiffuse();

        Font font = this.getActiveOverrideAttributes().getTextModifierFont();
        if (font == null)
            font = DEFAULT_FONT;

        if (this.textLabel != null)
        {
            this.textLabel.setColor(color);
            this.textLabel.setFont(font);
        }

        if (this.minAltLabel != null)
        {
            this.minAltLabel.setColor(color);
            this.minAltLabel.setFont(font);
        }

        if (this.maxAltLabel != null)
        {
            this.maxAltLabel.setColor(color);
            this.maxAltLabel.setFont(font);
        }

        if (this.timeFromLabel != null)
        {
            this.timeFromLabel.setColor(color);
            this.timeFromLabel.setFont(font);
        }

        if (this.timeToLabel != null)
        {
            this.timeToLabel.setColor(color);
            this.timeToLabel.setFont(font);
        }
    }
}
