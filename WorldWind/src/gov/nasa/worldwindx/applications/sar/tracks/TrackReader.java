/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.applications.sar.tracks;

import gov.nasa.worldwind.tracks.Track;

/**
 * @author dcollins
 * @version $Id$
 */
public interface TrackReader
{
    String getDescription();

    boolean canRead(Object source);

    Track[] read(Object source);
}
