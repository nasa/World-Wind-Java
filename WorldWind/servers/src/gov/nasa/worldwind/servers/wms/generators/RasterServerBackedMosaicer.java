/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.data.BasicRasterServer;
import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.data.DataRasterReader;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.*;

/**
 * RasterServerBackedMosaicer is used to compose a destination raster using matching tiles in the tile cache,
 * or if tiles are missing, build tile raster on-the-fly from sources and draw it on to the destination raster.
 *
 * @author Lado Garakanidze
 * @version $Id$
 */

public class RasterServerBackedMosaicer extends BasicTileCacheMosaicer
{
    protected BasicRasterServer rasterServer = null;

    public RasterServerBackedMosaicer(BasicRasterServer rasterServer, FileStore dataFileStore, LevelSet levels, AVList tileParams, DataRasterReader[] readers)
    {
        super(dataFileStore, levels, tileParams, readers);
        this.rasterServer = rasterServer;
    }

    private boolean drawTileFromSource(Tile tile, DataRaster destRaster)
    {
        if (null != this.rasterServer && this.rasterServer.hasDataRasters() && null != destRaster)
        {
            AVList params = new AVListImpl();
            params.setValue(AVKey.WIDTH, tile.getWidth());
            params.setValue(AVKey.HEIGHT, tile.getHeight());
            params.setValue(AVKey.SECTOR, tile.getSector());

            try
            {
                DataRaster tileRaster = this.rasterServer.composeRaster(params);
                if (null != tileRaster)
                {
                    // TODO garakl save (install tile)

                    tileRaster.drawOnTo(destRaster);

                    return true;
                }
            }
            catch (Exception e)
            {
                String message = Logging.getMessage("generic.CannotCreateRaster", tile.getPath());
                Logging.logger().severe(message);
            }
        }

        return false;
    }

    @Override
    protected void assembleTileOrDescendants(Tile tile, int reqWidth, int reqHeight, Sector reqSector)
    {
        if (this.tileMeetsRenderCriteria(tile, reqWidth, reqHeight, reqSector) )
        {
            // we do not care if tile does not exists,
            // in this case we will get it from source rasters
            this.addTile(tile);
            return;
        }

        if( this.levels.isFinalLevel(tile.getLevelNumber()) )
            return;

        Level nextLevel = this.levels.getLevel(tile.getLevelNumber() + 1);
        for (Tile subTile : ((ResourceTile) tile).subdivide(nextLevel))
        {
            this.assembleTileOrDescendants(subTile, reqWidth, reqHeight, reqSector);
        }
    }

    @Override
    protected void drawTiles(DataRaster destRaster)
    {
        boolean destRasterIsBlank = true;

        if( null != this.tileSetToMosaic &&  !this.tileSetToMosaic.isEmpty() )
        {
            for (Tile tile : this.tileSetToMosaic)
            {
                if( this.doDrawTile(tile, destRaster) )
                {   // something was painted on to the destination raster, therefore it is not blank anymore
                    destRasterIsBlank = false;
                }
            }
        }

        if( destRasterIsBlank )
        {
            // We are here because there were no intersecting tiles
            String message = Logging.getMessage("WMS.Layer.OutOfCoverage", destRaster.getSector());
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }
    }

    /**
     * Draws tile to the destination raster.
     * First, tries to locate the tile in the tile cache, if tile is not found in the cache,
     * uses sources to produce the tile and than draws it to the destination raster.
     *
     * @param tile Tile
     * @param destRaster destination raster
     * @return true, if a tile was drawn on to the destination raster
     *
     */
    private boolean doDrawTile(Tile tile, DataRaster destRaster)
    {
        // If drawing this tile to the destination raster was successful, then we're done.
        if (this.drawTileToRaster(tile, destRaster))
        {
            return true;
        }
        // Let's try to get the tile from sources
        if (this.drawTileFromSource(tile, destRaster))
        {
            return true;
        }

        return false;
    }
}
