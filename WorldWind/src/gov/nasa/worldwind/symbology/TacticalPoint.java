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

    /**
     * Returns the delegate owner of the graphic. If non-null, the returned object replaces the graphic as the pickable
     * object returned during picking. If null, the graphic itself is the pickable object returned during picking.
     *
     * @return the object used as the pickable object returned during picking, or null to indicate the the graphic is
     *         returned during picking.
     */
    Object getDelegateOwner();

    /**
     * Specifies the delegate owner of the graphic. If non-null, the delegate owner replaces the graphic as the
     * pickable object returned during picking. If null, the graphic itself is the pickable object returned during
     * picking.
     *
     * @param owner the object to use as the pickable object returned during picking, or null to return the graphic.
     */
    void setDelegateOwner(Object owner);
}
