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

import java.util.*;

/**
 * Base class for Aviation Area graphics.
 *
 * @author pabercrombie
 * @version $Id$
 */
abstract public class AbstractAviationArea extends GeneralArea
{
    protected abstract String getGraphicLabel();

    /** Create a new aviation area. */
    public AbstractAviationArea()
    {
        // Do not draw "ENY" labels on hostile entities.
        this.setShowIdentityLabels(false);
    }

    @Override
    protected Offset getLabelOffset()
    {
        return new Offset(0d, 0d, AVKey.FRACTION, AVKey.FRACTION); // Left align
    }

    @Override
    protected String createLabelText()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getGraphicLabel()); // TODO consider renaming
        sb.append("\n");

        Object o = this.getModifier(AVKey.TEXT);
        if (o != null)
        {
            sb.append(o);
            sb.append("\n");
        }

        Object[] altitudes = this.getAltitudeRange();
        if (altitudes[0] != null)
        {
            sb.append("MIN ALT: ");
            sb.append(altitudes[0]);
            sb.append("\n");
        }

        if (altitudes[1] != null)
        {
            sb.append("MAX ALT: ");
            sb.append(altitudes[1]);
            sb.append("\n");
        }

        Object[] dates = this.getDateRange();
        if (dates[0] != null)
        {
            sb.append("TIME FROM: ");
            sb.append(dates[0]);
            sb.append("\n");
        }

        if (dates[1] != null)
        {
            sb.append("TIME TO: ");
            sb.append(dates[1]);
        }

        return sb.toString();
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
    protected LatLon computeLabelLocation(DrawContext dc)
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

        // Shift south east, away from the western border
        LatLon ll = Position.greatCircleEndPosition(northWest, Angle.POS90, textHeight);

        // Shift south, away from the northern border
        return Position.greatCircleEndPosition(ll, Angle.POS180, textHeight);
    }
}
