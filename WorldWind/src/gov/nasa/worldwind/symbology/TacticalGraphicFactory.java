/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Position;

/**
 * A factory to create {@link TacticalGraphic}s. Each implementation of this interface handles the graphics for a
 * specific symbol set. Each graphic within that set is identified by a string identifier.
 *
 * @author pabercrombie
 * @version $Id$
 * @see TacticalGraphic
 */
public interface TacticalGraphicFactory
{
    /**
     * Create a tactical graphic positioned by a single control point.
     *
     * @param symbolIdentifier Identifier for the symbol within its symbol set.
     * @param position         Control point to use to place the graphic.
     * @param params           Modifiers to apply to the graphic.
     *
     * @return A new TacticalGraphic configured to render at the position indicated, or {@code null} if no graphic can
     *         be created for the given symbol identifier.
     */
    TacticalGraphic createGraphic(String symbolIdentifier, Position position, AVList params);

    /**
     * Create a tactical graphic positioned by more than one control point.
     *
     * @param symbolIdentifier Identifier for the symbol within its symbol set.
     * @param positions        Control points to use to place the graphic. How many points are required depends on the
     *                         type of graphic.
     * @param params           Modifiers to apply to the graphic.
     *
     * @return A new TacticalGraphic configured to render at the position indicated, or {@code null} if no graphic can
     *         be created for the given symbol identifier.
     */
    TacticalGraphic createGraphic(String symbolIdentifier, Iterable<Position> positions, AVList params);
}
