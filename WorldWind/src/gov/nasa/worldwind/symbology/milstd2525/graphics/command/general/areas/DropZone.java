/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

/**
 * Implementation of a Drop Zone graphic (hierarchy 2.X.2.1.3.5, SIDC: G*GPGAD---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class DropZone extends GeneralArea
{
    public final static String FUNCTION_ID = "GAD---";

    @Override
    protected String createLabelText()
    {
        return "DZ\n" + this.getText();
    }

    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
