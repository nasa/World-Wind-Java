/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.BasicArea;

/**
 * Implementation of aviation area graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Restricted Operations Zone (2.X.2.2.3.1)</li> <li>Short Range Air Defense Engagement Zone (2.X.2.2.3.2)</li>
 * <li>High Density Airspace Control Zone (2.X.2.2.3.3)</li> <li>Missile Engagement Zone (2.X.2.2.3.4)</li> <li>Low
 * Altitude Missile Engagement Zone (2.X.2.2.3.4.1)</li> <li>High Altitude Missile Engagement Zone (2.X.2.2.3.4.2)</li>
 * </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class AviationZone extends BasicArea
{
    /** Function ID for Restricted Operations Zone (2.X.2.2.3.1). */
    public final static String FUNCTION_ID_RESTRICTED_OPERATIONS_ZONE = "AAR---";
    /** Function ID for Short Range Air Defense Engagement Zone (2.X.2.2.3.2). */
    public final static String FUNCTION_ID_SHORT_RANGE_AIR_DEFENSE = "AAF---";
    /** Function ID for High Altitude Missile Engagement Zone (2.X.2.2.3.4.2). */
    public final static String FUNCTION_ID_HI_ALT_MISSILE_ZONE = "AAMH--";
    /** Function ID for High Density Airspace Control Zone (2.X.2.2.3.3). */
    public final static String FUNCTION_ID_HI_DENSITY_AIRSPACE = "AAH---";
    /** Function ID for Missile Engagement Zone (2.X.2.2.3.4). */
    public final static String FUNCTION_ID_MISSILE_ZONE = "AAML--";
    /** Function ID for Low Altitude Missile Engagement Zone (2.X.2.2.3.4.1). */
    public final static String FUNCTION_ID_LO_ALT_MISSILE_ZONE = "AAM---";

    /** Create a new aviation area. */
    public AviationZone()
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
    protected String getLabelAlignment()
    {
        return AVKey.LEFT;
    }

    @Override
    protected String createLabelText()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getGraphicLabel());
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

    protected String getGraphicLabel()
    {
        String functionId = this.getFunctionId();

        if (FUNCTION_ID_RESTRICTED_OPERATIONS_ZONE.equals(functionId))
            return "ROZ";
        else if (FUNCTION_ID_SHORT_RANGE_AIR_DEFENSE.equals(functionId))
            return "SHORADEZ";
        else if (FUNCTION_ID_HI_ALT_MISSILE_ZONE.equals(functionId))
            return "HIMEZ";
        else if (FUNCTION_ID_HI_DENSITY_AIRSPACE.equals(functionId))
            return "HIDACZ";
        else if (FUNCTION_ID_MISSILE_ZONE.equals(functionId))
            return "MEZ";
        else if (FUNCTION_ID_LO_ALT_MISSILE_ZONE.equals(functionId))
            return "LOMEZ";

        return "";
    }

    @Override
    protected void determineMainLabelPosition(DrawContext dc)
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
        LatLon latLon = Position.greatCircleEndPosition(ll, Angle.POS180, textHeight);
        this.labels.get(0).setPosition(new Position(latLon, 0));
    }
}
