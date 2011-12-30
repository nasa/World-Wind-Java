/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.combatsupport;

import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.BasicArea;

/**
 * Implementation of combat support area graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Detainee Holding Area (2.X.5.3.1)</li> <li>Enemy Prisoner of War Holding Area (2.X.5.3.2)</li> <li>Forward
 * Arming and Refueling Area (2.X.5.3.3)</li> <li>Refugee Holding Area (2.X.5.3.4)</li> <li>Support Areas Brigade (BSA)
 * (2.X.5.3.5.1)</li> <li>Support Areas Division (DSA) (2.X.5.3.5.2)</li> <li>Support Areas Regimental (RSA)
 * (2.X.5.3.5.3)</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class CombatSupportArea extends BasicArea
{
    /** Function ID for Detainee Holding Area (2.X.5.3.1). */
    public final static String FUNCTION_ID_DETAINEE = "AD----";
    /** Function ID for Enemy Prisoner of War Holding Area (2.X.5.3.2). */
    public final static String FUNCTION_ID_EPW = "AE----";
    /** Function ID for Forward Arming and Refueling Area (2.X.5.3.3). */
    public final static String FUNCTION_ID_FARP = "AR----";
    /** Function ID for Refugee Holding Area (2.X.5.3.4). */
    public final static String FUNCTION_ID_REFUGEE = "AH----";
    /** Function ID for Support Areas Brigade (2.X.5.3.5.1). */
    public final static String FUNCTION_ID_SUPPORT_BRIGADE = "ASB---";
    /** Function ID for Support Areas Division (DSA) (2.X.5.3.5.2). */
    public final static String FUNCTION_ID_SUPPORT_DIVISION = "ASD---";
    /** Function ID for Support Areas Regimental (RSA) (2.X.5.3.5.3). */
    public final static String FUNCTION_ID_SUPPORT_REGIMENTAL = "ASR---";

    /** Create a new area. */
    public CombatSupportArea()
    {
        // Do not draw "ENY" labels for hostile entities
        this.setShowHostileIndicator(false);
    }

    /** {@inheritDoc} */
    @Override
    protected String getGraphicLabel()
    {
        String functionId = this.getFunctionId();

        if (FUNCTION_ID_DETAINEE.equals(functionId))
            return "DETAINEE\nHOLDING\nAREA";
        else if (FUNCTION_ID_EPW.equals(functionId))
            return "EPW\nHOLDING\nAREA";
        else if (FUNCTION_ID_FARP.equals(functionId))
            return "FARP";
        else if (FUNCTION_ID_REFUGEE.equals(functionId))
            return "REFUGEE\nHOLDING\nAREA";
        else if (FUNCTION_ID_SUPPORT_BRIGADE.equals(functionId))
            return "BSA";
        else if (FUNCTION_ID_SUPPORT_DIVISION.equals(functionId))
            return "DSA";
        else if (FUNCTION_ID_SUPPORT_REGIMENTAL.equals(functionId))
            return "RSA";

        return "";
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_COMBAT_SERVICE_SUPPORT;
    }
}
