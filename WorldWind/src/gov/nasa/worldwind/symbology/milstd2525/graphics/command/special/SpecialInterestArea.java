/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.special;

import gov.nasa.worldwind.symbology.milstd2525.graphics.*;

import java.util.*;

/**
 * Implementation of General Command/Special area graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Area of Operations (2.X.2.6.2.1)</li> <li>Named Area of Interest (2.X.2.6.2.4)</li> <li>Targeted Area of
 * Interest (2.X.2.6.2.5)</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class SpecialInterestArea extends BasicArea
{
    /**
     * Indicates the graphics supported by this class.
     *
     * @return List of masked SIDC strings that identify graphics that this class supports.
     */
    public static List<String> getSupportedGraphics()
    {
        return Arrays.asList(
            TacGrpSidc.C2GM_SPL_ARA_AOO,
            TacGrpSidc.C2GM_SPL_ARA_NAI,
            TacGrpSidc.C2GM_SPL_ARA_TAI);
    }

    /**
     * Create a new area graphic.
     *
     * @param sidc Symbol code the identifies the graphic.
     */
    public SpecialInterestArea(String sidc)
    {
        super(sidc);
        this.setShowHostileIndicator(false);
    }

    @Override
    protected String getGraphicLabel()
    {
        String code = this.maskedSymbolCode;

        if (TacGrpSidc.C2GM_SPL_ARA_AOO.equals(code))
            return "AO";
        else if (TacGrpSidc.C2GM_SPL_ARA_NAI.equals(code))
            return "NAI";
        else if (TacGrpSidc.C2GM_SPL_ARA_TAI.equals(code))
            return "TAI";

        return "";
    }
}