/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.render.*;

/**
 * @author dcollins
 * @version $Id$
 */
public interface DetailLevel extends Comparable<DetailLevel>, AVList
{
    boolean meetsCriteria(DrawContext dc, Airspace airspace);

    int compareTo(DetailLevel level);
}
