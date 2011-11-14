/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.areas;

/**
 * Implementation of the Restricted Operations Zone graphic (hierarchy 2.X.2.2.3.4, SIDC: G*GPAAM---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class MissileEngagementZone extends AbstractAviationArea
{
    public final static String FUNCTION_ID = "AAM---";

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    @Override
    protected String createText(String text)
    {
        return "MEZ";
    }
}
