/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.ShapeAttributes;

/**
 * @author pabercrombie
 * @version $Id$
 */
public interface TacticalShape extends TacticalGraphic
{
    void setPositions(Iterable<? extends Position> positions);

    Iterable<? extends Position> getPositions();

    void setAttributes(ShapeAttributes attributes);

    ShapeAttributes getAttributes();
}
