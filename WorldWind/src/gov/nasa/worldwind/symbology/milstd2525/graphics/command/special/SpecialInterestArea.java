/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.special;

import gov.nasa.worldwind.symbology.milstd2525.graphics.BasicArea;

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
    /** Function ID for Area of Operations (2.X.2.6.2.1). */
    public final static String FUNCTION_ID_OPERATIONS = "SAO---";
    /** Function ID for Named Area of Interest (2.X.2.6.2.4). */
    public final static String FUNCTION_ID_NAMED = "SAN---";
    /** Function ID for Named Area of Interest (2.X.2.6.2.5). */
    public final static String FUNCTION_ID_TARGETED = "SAT---";

    /** Create a new area graphic. */
    public SpecialInterestArea()
    {
        super.setShowHostileIndicator(false);
    }

    @Override
    protected String getGraphicLabel()
    {
        String functionId = this.getFunctionId();

        if (FUNCTION_ID_OPERATIONS.equals(functionId))
            return "AO";
        else if (FUNCTION_ID_NAMED.equals(functionId))
            return "NAI";
        else if (FUNCTION_ID_TARGETED.equals(functionId))
            return "TAI";

        return "";
    }
}