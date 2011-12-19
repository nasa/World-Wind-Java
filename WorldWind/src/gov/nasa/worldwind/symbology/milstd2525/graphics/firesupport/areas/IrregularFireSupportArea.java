/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas.BasicArea;

/**
 * Implementation of the irregular Fire Support area graphics. This class implements the following graphics:
 *
 * <ul>
 *    <li>Area Target (2.X.4.3.1)</li>
 *    <li>Free Fire Area (FFA), Irregular (2.X.4.3.2.3.1)</li>
 *    <li>Restrictive Fire Area (RFA), Irregular (2.X.4.3.2.5.1)</li>
 * </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class IrregularFireSupportArea extends BasicArea
{
    /** Function ID of the Area Target graphic (2.X.4.3.1). */
    public final static String FUNCTION_ID_TARGET = "AT----";
    /** Function ID for the Free Fire Area graphic (2.X.4.3.2.3.1). */
    public final static String FUNCTION_ID_FFA = "ACFI--";
    /** Function ID for the Restrictive Fire Area graphic (2.X.4.3.2.5.1). */
    public final static String FUNCTION_ID_RFA = "ACRI--";

    /** Create the area graphic. */
    public IrregularFireSupportArea()
    {
        this.setShowHostileIndicator(false);
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
    }

    @Override
    protected String createLabelText()
    {
        FireSupportTextBuilder textBuilder = new FireSupportTextBuilder();
        return textBuilder.createText(this);
    }
}
