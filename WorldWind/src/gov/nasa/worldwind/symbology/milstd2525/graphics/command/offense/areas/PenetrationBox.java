/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.areas;

import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.GeneralArea;

/**
 * Implementation of the Penetration Box graphic (hierarchy 2.X.2.5.3.6, SIDC: G*GPOAP---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class PenetrationBox extends GeneralArea
{
    /** Function ID for this graphic. */
    public final static String FUNCTION_ID = "OAP---";

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
