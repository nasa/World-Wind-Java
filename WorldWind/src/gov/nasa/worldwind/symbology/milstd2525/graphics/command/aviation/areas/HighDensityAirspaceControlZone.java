/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.areas;

/**
 * Implementation of the High Density Airspace Control Zone graphic (hierarchy 2.X.2.2.3.3, SIDC: G*GPAAH---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class HighDensityAirspaceControlZone extends AbstractAviationArea
{
    public final static String FUNCTION_ID = "AAH---";

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    protected String getGraphicLabel()
    {
        return "HIDACZ";
    }
}
