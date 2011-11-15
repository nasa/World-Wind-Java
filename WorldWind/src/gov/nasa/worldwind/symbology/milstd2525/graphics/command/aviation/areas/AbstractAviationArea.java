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
