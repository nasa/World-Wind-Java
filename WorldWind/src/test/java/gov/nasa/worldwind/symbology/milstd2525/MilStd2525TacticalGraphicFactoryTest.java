/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link MilStd2525GraphicFactory}.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class MilStd2525TacticalGraphicFactoryTest
{
    @Test
    public void testGraphicSupported() throws IllegalAccessException
    {
        MilStd2525GraphicFactory factory = new MilStd2525GraphicFactory();
        assertTrue(factory.isSupported("GFGPGLP----AUSX"));
    }

    @Test
    public void testGraphicNotSupported() throws IllegalAccessException
    {
        MilStd2525GraphicFactory factory = new MilStd2525GraphicFactory();
        assertFalse(factory.isSupported("GFGPXXX----AUSX")); // Non-existent function ID.
    }
}
