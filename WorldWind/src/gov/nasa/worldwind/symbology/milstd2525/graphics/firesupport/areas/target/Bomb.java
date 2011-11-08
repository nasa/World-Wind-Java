/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.target;

import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.GeneralArea;

/**
 * Implementation of the Circular Target graphic (hierarchy 2.X.4.3.1.5, SIDC: G*FPATB---****X).
 *
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
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
    }

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
