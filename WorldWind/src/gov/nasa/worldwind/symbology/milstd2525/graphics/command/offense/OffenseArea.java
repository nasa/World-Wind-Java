/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense;

import gov.nasa.worldwind.symbology.milstd2525.graphics.BasicArea;

/**
 * Implementation of offense area graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Assault Position (2.X.2.5.3.1)</li> <li>Attack Position (2.X.2.5.3.2)</li> <li>Objective (2.X.2.5.3.5)</li>
 * <li>Penetration Box (2.X.2.5.3.6)</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class OffenseArea extends BasicArea
{
    /** Function ID for Assault Position (2.X.2.5.3.1). */
    public final static String FUNCTION_ID_ASSAULT_POSITION = "OAA---";
    /** Function ID for Attack Position (2.X.2.5.3.2). */
    public final static String FUNCTION_ID_ATTACK_POSITION = "OAK---";
    /** Function ID for Objective (2.X.2.5.3.5). */
    public final static String FUNCTION_ID_OBJECTIVE = "OAO---";
    /** Function ID for Penetration Box (2.X.2.5.3.6). */
    public final static String FUNCTION_ID_PENETRATION_BOX = "OAP---";

    /** Create a new area graphic. */
    public OffenseArea()
    {
        super.setShowHostileIndicator(false);
    }

    /** {@inheritDoc} */
    @Override
    protected String createLabelText()
    {
        // Penetration box graphic does not support text modifiers.
        if (FUNCTION_ID_PENETRATION_BOX.equals(this.getFunctionId()))
            return null;

        return super.createLabelText();
    }

    @Override
    protected String getGraphicLabel()
    {
        String functionId = this.getFunctionId();

        if (FUNCTION_ID_ASSAULT_POSITION.equals(functionId))
            return "ASLT\nPSN";
        else if (FUNCTION_ID_ATTACK_POSITION.equals(functionId))
            return "ATK";
        else if (FUNCTION_ID_ATTACK_POSITION.equals(functionId))
            return "OBJ";

        return "";
    }
}
