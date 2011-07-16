/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.geom.Sector;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public interface TileCacheMosaicer
{
    void mosaicTilesForLevel(int levelNumber, Sector reqSector, DataRaster destRaster);

    void mosaicBestAvailableTiles(int reqWidth, int reqHeight, Sector reqSector, DataRaster destRaster);
}
