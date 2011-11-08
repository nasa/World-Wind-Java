/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.lines.axis.ground;

import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.lines.AbstractOffenseArrow;

/**
 * Implementation of the Supporting Attack graphic (hierarchy 2.X.2.5.2.1.4.2, SIDC: G*GPOLAGS-****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class SupportingAttack extends AbstractOffenseArrow
{
    /** Function ID for the Phase Line. */
    public final static String FUNCTION_ID = "OLAGS-";

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_COMMAND_CONTROL_GENERAL_MANEUVER;
    }

    /** {@inheritDoc} */
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
