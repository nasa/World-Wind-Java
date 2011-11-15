/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.areas;

/**
 * Implementation of the Short Range Air Defense Engagement Zone graphic (hierarchy 2.X.2.2.3.2, SIDC: G*GPAAF---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class ShortRangeAirDefenseEngagementZone extends AbstractAviationArea
{
    public final static String FUNCTION_ID = "AAF---";

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    @Override
    protected String getGraphicLabel()
    {
        return "SHORADEZ";
    }
}
