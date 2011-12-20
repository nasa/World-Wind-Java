/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;

/**
 * Implementation of the Position Area for Artillery, Circular graphic (2.X.4.3.2.6.2).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class CircularPositionArea extends AbstractCircularGraphic
{
    /** Function ID for the Position Area for Artillery, Circular graphic (2.X.4.3.2.6.2). */
    public final static String FUNCTION_ID = "ACPC--";

    /** Create a new circular area. */
    public CircularPositionArea()
    {
        super();
    }

    /** Create labels for the start and end of the path. */
    @Override
    protected void createLabels()
    {
        // This graphic has labels at the top, bottom, left, and right of the circle.
        this.addLabel("PAA");
        this.addLabel("PAA");
        this.addLabel("PAA");
        this.addLabel("PAA");
    }

    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        Position center = new Position(this.circle.getCenter(), 0);
        Angle radius = Angle.fromRadians(this.circle.getRadius() / dc.getGlobe().getRadius());

        Angle[] cardinalDirections = new Angle[] {
            Angle.NEG90, // Due West
            Angle.POS90, // Due East
            Angle.ZERO, // Due North
            Angle.POS180 // Due South
        };

        int i = 0;
        for (Angle dir : cardinalDirections)
        {
            LatLon loc = LatLon.greatCircleEndPosition(center, dir, radius);
            this.labels.get(i).setPosition(new Position(loc, 0));
            i += 1;
        }
    }
}