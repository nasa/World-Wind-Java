/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.command;

import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.BasicArea;

/**
 * Implementation of the Terminally Guided Munition Footprint graphic (SIDC: G*FPACT---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class TerminallyGuidedMunitionFootprint extends BasicArea
{
    /** Function ID of this graphic. */
    public final static String FUNCTION_ID = "ACT---";

    public TerminallyGuidedMunitionFootprint()
    {
        // Do not draw "ENY" labels for hostile entities
        this.setShowHostileIndicator(false);
    }

    /** {@inheritDoc} */
    @Override
    protected String createLabelText()
    {
        return "TGMF";
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
