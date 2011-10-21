/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.areas;

import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.GeneralArea;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class PenetrationBox extends GeneralArea
{
    public final static String FUNCTION_ID = "OAP---";

    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
