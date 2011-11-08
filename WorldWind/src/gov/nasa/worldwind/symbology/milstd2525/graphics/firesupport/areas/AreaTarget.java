/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.GeneralArea;

/**
 * Implementation of the Circular Target graphic (hierarchy 2.X.4.3.1, SIDC: G*FPAT----****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class AreaTarget extends GeneralArea
{
    /** Function ID of this graphic. */
    public final static String FUNCTION_ID = "AT----";

    /** {@inheritDoc} */
    @Override
    protected String createText(String text)
    {
        return text;
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
