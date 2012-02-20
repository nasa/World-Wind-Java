/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.symbology.milstd2525.SymbolCode;
import gov.nasa.worldwind.symbology.milstd2525.graphics.*;
import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 * Test parsing of all SIDC constants declared in {@link MetocSidc}.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class MetocSidcTest extends TestCase
{
    @org.junit.Test
    public void testParse() throws IllegalAccessException
    {
        // MetocSidc declares constants for each SIDC. Grab all of these fields and make sure that each one can be
        // parsed successfully.
        Field[] fields = MetocSidc.class.getDeclaredFields();

        for (Field f : fields)
        {
            StringBuilder sidc = new StringBuilder((String) f.get(null));

            SymbolCode code = new SymbolCode(sidc.toString());
            TestCase.assertEquals(sidc.toString(), code.toString());
        }
    }

    public static void main(String[] args)
    {
        new junit.textui.TestRunner().doRun(new junit.framework.TestSuite(MetocSidcTest.class));
    }
}
