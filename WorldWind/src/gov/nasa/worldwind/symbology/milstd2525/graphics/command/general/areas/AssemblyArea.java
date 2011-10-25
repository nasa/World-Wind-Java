/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

/**
 * Implementation of the Assembly Area graphic (hierarchy 2.X.2.1.3.2, SIDC: G*GPGAA---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class AssemblyArea extends GeneralArea
{
    /** Function ID of this graphic. */
    public final static String FUNCTION_ID = "GAA---";

    /** {@inheritDoc} */
    @Override
    protected String createText(String text)
    {
        return "AA " + text;
    }

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
