/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.tracks;

/**
 * @author tag
 * @version $Id$
 */
public interface Track
{
    java.util.List<TrackSegment> getSegments();

    String getName();

    int getNumPoints();
}
