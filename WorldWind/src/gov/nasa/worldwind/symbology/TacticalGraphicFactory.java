/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Position;

/**
 * @author pabercrombie
 * @version $Id$
 */
public interface TacticalGraphicFactory
{
    TacticalGraphic createGraphic(String symbolIdentifier, Iterable<Position> positions, AVList params);
}
