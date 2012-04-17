/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.*;
import junit.framework.TestCase;

import java.util.*;

/**
 * Unit tests for {@link Polygon}.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class PolygonTest
{
    @org.junit.Test
    public void testGetExtent()
    {
        Globe globe = new Earth();
        double verticalExaggeration = 1.0;

        List<Position> positions = Arrays.asList(
            Position.fromDegrees(28, -106),
            Position.fromDegrees(35, -104),
            Position.fromDegrees(28, -107),
            Position.fromDegrees(28, -106));

        Sector sector = Sector.boundingSector(positions);
        double[] minAndMaxElevations = globe.getMinAndMaxElevations(sector);

        Extent expected = Sector.computeBoundingBox(globe, verticalExaggeration, sector, minAndMaxElevations[0],
            minAndMaxElevations[1]);

        Polygon pgon = new Polygon(positions);
        pgon.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);

        Extent actual = pgon.getExtent(globe, verticalExaggeration);

        TestCase.assertEquals(expected, actual);
    }
}
