/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

/**
 * An interface for tactical graphics that depict routes. A route is composed of a series of point graphics connected by
 * lines. For example, the MIL-STD-2525 symbology set defines an Air Control Route that is composed of Air Control
 * Points.
 *
 * @author pabercrombie
 * @version $Id$
 */
public interface TacticalRoute extends TacticalGraphic
{
    /**
     * Indicates the control points along this route.
     *
     * @return This route's control points.
     */
    Iterable<? extends TacticalPoint> getControlPoints();

    /**
     * Specifies the control points along this route.
     *
     * @param points New control points.
     */
    void setControlPoints(Iterable<? extends TacticalPoint> points);
}
