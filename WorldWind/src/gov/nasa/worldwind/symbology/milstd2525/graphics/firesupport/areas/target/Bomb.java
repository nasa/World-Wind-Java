/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.target;

import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.GeneralArea;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class Bomb extends GeneralArea
{
    /** Function ID of this graphic. */
    public final static String FUNCTION_ID = "ATB---";

    /** {@inheritDoc} */
    @Override
    protected String createText(String text)
    {
        return "BOMB";
    }

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
