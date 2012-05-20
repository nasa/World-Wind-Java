/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.tracks;

import gov.nasa.worldwind.geom.Position;

/**
 * @author tag
 * @version $Id$
 */
public interface TrackPoint
{
    double getLatitude();

    void setLatitude(double latitude);

    double getLongitude();

    void setLongitude(double longitude);

    double getElevation();

    void setElevation(double elevation);

    String getTime();

    void setTime(String time);

    Position getPosition();

    void setPosition(Position position);
}
