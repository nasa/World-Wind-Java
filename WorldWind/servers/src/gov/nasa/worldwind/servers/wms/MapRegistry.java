/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import java.util.*;

/**
 * @author brownrigg
 * @version $Id$
 */
public interface MapRegistry
{
    Iterable<String> getMapNames();

    MapSource get(String mapName);

    boolean contains(String mapName);

    void add(MapSource mapSource);

    void remove(MapSource mapSource);
}
