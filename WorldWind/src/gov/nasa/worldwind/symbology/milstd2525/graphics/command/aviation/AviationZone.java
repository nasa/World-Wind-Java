/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation;

import gov.nasa.worldwind.avlist.AVKey;
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
        return new Offset(-0.5d, -0.5d, AVKey.FRACTION, AVKey.FRACTION); // Center text block on label position.
    }

    @Override
    protected String getLabelAlignment()
    {
        return AVKey.LEFT;
    }

    @Override
    protected String createLabelText()
    {
        return doCreateLabelText(true);
    }

    /**
     * Create text for the area's label.
     *
     * @param includeAltitude Indicates whether to include altitude information in the label (if the AVKey.ALTITUDE
     *                        modifier is set). Not all aviation area graphics support the altitude modifier.
     *
     * @return Text for the label, based on the active modifiers.
     */
    protected String doCreateLabelText(boolean includeAltitude)
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

        if (includeAltitude)
        {
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
}
