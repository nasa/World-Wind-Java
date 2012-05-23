/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.geom.Position;

/**
 * An interface for tactical graphics that are positioned by a single point.
 *
 * @author pabercrombie
 * @version $Id$
 * @see TacticalGraphicFactory#createPoint(String, gov.nasa.worldwind.geom.Position, gov.nasa.worldwind.avlist.AVList)
 */
public interface TacticalPoint extends TacticalGraphic
{
    /**
     * Indicates the position of the graphic.
     *
     * @return The position of the graphic.
     */
    public Position getPosition();

    /**
     * Specifies the position of the graphic.
     *
     * @param position New position.
     */
    void setPosition(Position position);
}
