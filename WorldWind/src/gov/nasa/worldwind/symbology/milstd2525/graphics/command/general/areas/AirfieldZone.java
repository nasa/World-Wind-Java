/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class AirfieldZone extends GeneralArea
{
    public final static String FUNCTION_ID = "GAZ---";

    public AirfieldZone()
    {
    }

    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
