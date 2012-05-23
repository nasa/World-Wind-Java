/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render.airspaces.editor;

import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.geom.Vec4;

/**
 * @author dcollins
 * @version $Id$
 */
public interface AirspaceControlPoint
{
    AirspaceEditor getEditor();

    Airspace getAirspace();

    int getLocationIndex();

    int getAltitudeIndex();

    Vec4 getPoint();

    Object getKey();
}
