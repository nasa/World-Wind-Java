/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

/**
 * Implementation of the Airfield Zone graphic (hierarchy 2.X.2.1.3.11, SIDC: G*GPGAZ---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class AirfieldZone extends GeneralArea
{
    /** Function ID for this graphic. */
    public final static String FUNCTION_ID = "GAZ---";

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
