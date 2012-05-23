/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.globes;

/**
 * Holds a globe's configuration state. The state can be used to compare a globe's current configuration with a previous
 * configuration.
 *
 * @author tag
 * @version $ID$
 */
public interface GlobeStateKey
{
    /**
     * Indicates the globe associated with this state key.
     *
     * @return the globe associated with this state key.
     */
    Globe getGlobe();
}