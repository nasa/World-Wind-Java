/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

/**
 * Implementation of a Assembly Area (hierarchy 2.X.2.1.3.3, SIDC: G*GPGAE---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class EngagementArea extends GeneralArea
{
    public final static String FUNCTION_ID = "GAE---";

    @Override
    protected String createText(String text)
    {
        return "EA " + text;
    }

    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
