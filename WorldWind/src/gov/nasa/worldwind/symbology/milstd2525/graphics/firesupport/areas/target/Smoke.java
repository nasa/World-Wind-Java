/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.target;

import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.GeneralArea;

/**
 * Implementation of the Smoke graphic (hierarchy 2.X.4.3.1.4, SIDC: G*FPATS---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class Smoke extends GeneralArea
{
    /** Function ID of this graphic. */
    public final static String FUNCTION_ID = "ATS---";

    public Smoke()
    {
        // Do not draw "ENY" labels for hostile entities
        this.setShowIdentityLabels(false);
    }

    /** {@inheritDoc} */
    @Override
    protected String createLabelText()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SMOKE\n");

        Object[] dates = this.getDateRange();
        if (dates[0] != null)
        {
            sb.append(dates[0]);
            sb.append(" - \n");
        }

        if (dates[1] != null)
        {
            sb.append(dates[1]);
        }

        return sb.toString();
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
