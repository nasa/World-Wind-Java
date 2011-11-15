/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

/**
 * Implementation of the Landing Zone graphic (hierarchy 2.X.2.1.3.7, SIDC: G*GPGAL---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class LandingZone extends GeneralArea
{
    public final static String FUNCTION_ID = "GAL---";

    @Override
    protected String createLabelText()
    {
        return "LZ\n" + this.getText();
    }

    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
