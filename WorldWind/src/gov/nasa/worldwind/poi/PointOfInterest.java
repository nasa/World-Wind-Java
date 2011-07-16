/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.poi;

import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.geom.*;

/**
 * @author tag
 * @version $Id$
 */
public interface PointOfInterest extends WWObject
{
    LatLon getLatlon();
}
