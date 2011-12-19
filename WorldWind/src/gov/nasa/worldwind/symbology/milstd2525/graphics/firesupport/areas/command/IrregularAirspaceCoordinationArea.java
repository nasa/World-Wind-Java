/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.command;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.AviationZone;

/**
 * Implementation of the Irregular Airspace Coordination Area graphic (hierarchy 2.X.4.3.2.2.1, SIDC: G*FPACAI--****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class IrregularAirspaceCoordinationArea extends AviationZone
{
    /** Function ID of this graphic. */
    public final static String FUNCTION_ID = "ACAI--";

    public IrregularAirspaceCoordinationArea()
    {
        // Do not draw "ENY" labels for hostile entities
        this.setShowHostileIndicator(false);
    }

    @Override
    protected String getGraphicLabel()
    {
        return "ACA";
    }

    @Override
    protected String createLabelText()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getGraphicLabel()); // TODO consider renaming
        sb.append("\n");

        Object o = this.getModifier(AVKey.TEXT);
        if (o != null)
        {
            sb.append(o);
            sb.append("\n");
        }

        Object[] altitudes = TacticalGraphicUtil.getAltitudeRange(this);
        if (altitudes[0] != null)
        {
            sb.append("MIN ALT: ");
            sb.append(altitudes[0]);
            sb.append("\n");
        }

        if (altitudes[1] != null)
        {
            sb.append("MAX ALT: ");
            sb.append(altitudes[1]);
            sb.append("\n");
        }

        o = this.getModifier(AVKey.DESCRIPTION);
        if (o != null)
        {
            sb.append("Grids: ");
            sb.append(o);
            sb.append("\n");
        }

        Object[] dates = TacticalGraphicUtil.getDateRange(this);
        if (dates[0] != null)
        {
            sb.append("EFF: ");
            sb.append(dates[0]);
            sb.append("\n");
        }

        if (dates[1] != null)
        {
            sb.append("     "); // TODO do a better job of vertically aligning the start and end time labels
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
