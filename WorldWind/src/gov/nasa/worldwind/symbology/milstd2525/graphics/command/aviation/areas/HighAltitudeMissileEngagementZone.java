/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.areas;

/**
 * Implementation of the High Altitude Missile Engagement Zone graphic (hierarchy 2.X.2.2.3.4.2, SIDC: G*GPAAMH--****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class HighAltitudeMissileEngagementZone extends AbstractAviationArea
{
    public final static String FUNCTION_ID = "AAMH--";

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    protected String getGraphicLabel()
    {
        return "HIMEZ";
    }
}
