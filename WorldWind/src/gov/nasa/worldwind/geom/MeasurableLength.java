/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.geom;

import gov.nasa.worldwind.globes.*;

/**
 * @author tag
 * @version $Id$
 */
public interface MeasurableLength
{
    /**
     * Returns the object's length in meters. If the object conforms to terrain, the length is that along the terrain,
     * including its hillsides and other undulations.
     *
     * @param globe The globe the object is related to.
     * @return the object's length in meters.
     * @throws IllegalArgumentException if the <code>globe</code> is null.
     */
    double getLength(Globe globe);
}
