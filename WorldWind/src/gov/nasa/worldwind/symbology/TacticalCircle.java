/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

/**
 * An interface for circular tactical graphics. This interface provides methods to access the radius of the circle. The
 * radius can also be set using the AVKey.RADIUS modifier.
 *
 * @author pabercrombie
 * @version $Id$
 */
public interface TacticalCircle extends TacticalPoint
{
    /**
     * Indicates the radius of this circle.
     *
     * @return The radius of this circle, in meters.
     */
    double getRadius();

    /**
     * Specifies the radius of this circle.
     *
     * @param radius New radius, in meters.
     */
    void setRadius(double radius);
}
