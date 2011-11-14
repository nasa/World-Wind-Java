/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.areas;

/**
 * Implementation of the Restricted Operations Zone graphic (hierarchy 2.X.2.2.3.1, SIDC: G*GPAAR---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
// TODO better logic for determining the default label position
public class RestrictedOperationsZone extends AbstractAviationArea
{
    public final static String FUNCTION_ID = "AAR---";

    /** {@inheritDoc} */
    @Override
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    @Override
    protected String createText(String text)
    {
        return "ROZ";
    }
}
