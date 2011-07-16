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
import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.data.DataRasterReader;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * BasicTileCacheMosaicer
 *
 * RasterServerBackedMosaicer is used to compose a destination raster using matching tiles in the tile cache,
 * or if tiles are missing, will first try to create tile on-the-fly from higher resolution (descendant) tiles,
 * or , if descendant tiles not found, from lower resolution (ancestor) tile
 *
 * @author Lado Garakanidze
 * @version $Id$
 */

public class BasicTileCacheMosaicer implements TileCacheMosaicer
{
    protected FileStore dataFileStore = null;
    protected LevelSet levels = null;
    protected AVList tileParams = null;
    protected DataRasterReader[] readers = new DataRasterReader[0];
    protected Set<Tile> tileSetToMosaic = Collections.synchronizedSet(new HashSet<Tile>());

    public BasicTileCacheMosaicer(FileStore dataFileStore, LevelSet levels, AVList tileParams,
        DataRasterReader[] readers)
    {
        this.dataFileStore = dataFileStore;
        this.levels = levels;
        this.tileParams = tileParams;
        this.readers = (null != readers) ? readers : this.readers;
    }

    public void mosaicTilesForLevel(int levelNumber, gov.nasa.worldwind.geom.Sector reqSector, DataRaster destRaster)
    {
//        Logging.logger().info( "DEBUG: mosaicTilesForLevel() , level=" + levelNumber );

        this.tileSetToMosaic.clear();

        Level level = this.levels.getLevel(levelNumber);
        this.assembleTiles(level, reqSector, this.tileSetToMosaic);
        this.drawTiles(destRaster);
    }

    public void mosaicBestAvailableTiles(int reqWidth, int reqHeight, Sector reqSector, DataRaster destRaster)
    {
//        Logging.logger().info( "DEBUG: mosaicBestAvailableTiles() , sector=" + reqSector.toString());

        this.tileSetToMosaic.clear();

        Level firstLevel = this.levels.getFirstLevel();
        Set<Tile> firstLevelTiles = new HashSet<Tile>();
        this.assembleTiles( firstLevel, reqSector, firstLevelTiles);

//        long start = System.currentTimeMillis();
        for (Tile tile : firstLevelTiles)
        {
            this.assembleTileOrDescendants(tile, reqWidth, reqHeight, reqSector);
        }
//        Logging.logger().info( "DEBUG: assembleTileOrDescendants() time=" + (System.currentTimeMillis()-start) + "ms" );

//        start = System.currentTimeMillis();
        this.drawTiles(destRaster);
//        Logging.logger().info( "DEBUG: drawTiles() time=" + (System.currentTimeMillis()-start) + "ms" );
    }

    protected void assembleTiles(Level level, Sector reqSector, Set<Tile> tileToMosaic)
    {
        Angle latOrigin = this.levels.getTileOrigin().getLatitude();
        Angle lonOrigin = this.levels.getTileOrigin().getLongitude();
        Angle dLat = level.getTileDelta().getLatitude();
        Angle dLon = level.getTileDelta().getLongitude();

        Sector overlapSector = this.levels.getSector().intersection(reqSector);
        int firstRow = Tile.computeRow(dLat, overlapSector.getMinLatitude(), latOrigin);
        int firstCol = Tile.computeColumn(dLon, overlapSector.getMinLongitude(), lonOrigin);
        int lastRow = Tile.computeRow(dLat, overlapSector.getMaxLatitude(), latOrigin);
        int lastCol = Tile.computeColumn(dLon, overlapSector.getMaxLongitude(), lonOrigin);

        Angle p1 = Tile.computeRowLatitude(firstRow, dLat, latOrigin);

        for (int row = firstRow; row <= lastRow; row++)
        {
            Angle p2;
            p2 = p1.add(dLat);

            Angle t1 = Tile.computeColumnLongitude(firstCol, dLon, lonOrigin);
            for (int col = firstCol; col <= lastCol; col++)
            {
                Angle t2;
                t2 = t1.add(dLon);

                ResourceTile tile = new ResourceTile(new Sector(p1, p2, t1, t2), level, row, col, null);
                if (this.intersects(tile.getSector(), reqSector))
                {
                    tileToMosaic.add(tile);
                }

                t1 = t2;
            }
            p1 = p2;
        }
    }

    protected boolean intersects(Sector a, Sector b)
    {
        Sector overlap;
        return (null != a && null != b
            && (null != (overlap = a.intersection(b)))
            && Math.abs(overlap.getDeltaLatDegrees()) > 0d
            && Math.abs(overlap.getDeltaLonDegrees()) > 0d
        );
    }

    protected URL locateTileInCache(Tile tile)
    {
        if (null == tile || null == this.dataFileStore)
        {
            return null;
        }

        return this.dataFileStore.findFile(tile.getPath(), false);
    }

    protected void addTile(Tile tile)
    {
//        Logging.logger().info( "DEBUG: !!!!!! Added tile for Level#" + tile.getLevelName() + ", tile path = " + tile.getPath() );
        this.tileSetToMosaic.add(tile);
    }

    protected void assembleTileOrDescendants(Tile tile, int reqWidth, int reqHeight, Sector reqSector)
    {
        if (this.tileMeetsRenderCriteria(tile, reqWidth, reqHeight, reqSector) )
        {
            if( this.dataFileStore.containsFile(tile.getPath()) )
            {
                this.addTile(tile);
                return;
            }
            // we are here, level's resolution does meet render criteria, but tile was not found in the cache
            // let's allow to go to lower levels with higher resolution and try to locate higher resolution
            // descendant tiles and use them to re-create the missing tile
            // but we do not want to allow to go too far, because next we will 4 tiles, than 16, then 64
            // we simply may not have time to assemble the missing tile

            double reqTexelSize = Math.abs(reqSector.getDeltaLatRadians() / reqHeight);
            double levelTexelSize = Math.abs(tile.getLevel().getTexelSize());

            if( reqTexelSize >= levelTexelSize && (reqTexelSize / levelTexelSize) >= 4 )
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

    protected boolean tileMeetsRenderCriteria(Tile tile, int reqWidth, int reqHeight, Sector reqSector)
    {
        if( null == tile )
            return false;

        Sector tileSector = tile.getSector();
        if( null == tileSector )
            return false;

        if ( !this.intersects( tileSector, this.levels.getSector()))
        {
            return false;
        }

        if (!this.intersects( tileSector, reqSector))
        {
            return false;
        }

        if (this.levels.isFinalLevel(tile.getLevelNumber()))
        {
            return true;
        }

        if (tile.getLevel().isEmpty())
        {
            return false;
        }

        double reqTexelSize = reqSector.getDeltaLatRadians() / reqHeight;
        if (tile.getLevel().getTexelSize() > reqTexelSize)
        {
            return false;
        }

        return true;
    }

    protected void drawTiles(DataRaster destRaster)
    {
        for (Tile tile : this.tileSetToMosaic)
        {
            this.drawTileOrAncestors(tile, destRaster);
        }
    }

    protected void drawTileOrAncestors(Tile tile, DataRaster destRaster)
    {
        // If drawing this tile to the destination raster was successful, then we're done.
        if (this.drawTileToRaster(tile, destRaster))
        {
            return;
        }

        // Otherwise, we must try to draw one of the tile's ancestors in it's place. Since the parent raster covers
        // a larger geographic region than the child, we clip the parent to the child's geographic region during
        // rendering.
        if (((ResourceTile) tile).getParent() != null)
        {
            this.drawTileOrAncestors(((ResourceTile) tile).getParent(), destRaster);
        }
    }

    protected boolean drawTileToRaster(Tile tile, DataRaster destRaster)
    {
        try
        {
            DataRaster[] rasters = this.readTileAsRaster(tile);
            if (null != rasters)
            {
                for (DataRaster raster : rasters)
                {
                    if (this.intersects(raster.getSector(), destRaster.getSector()))
                    {
                        raster.drawOnTo(destRaster);
                    }
                }

                return true;
            }
        }
        catch (Throwable t)
        {
            Logging.logger().log(java.util.logging.Level.SEVERE, WWUtil.extractExceptionReason(t), t );
        }

        return false;
    }

    protected DataRaster[] readTileAsRaster(Tile tile) throws IOException
    {
        URL url = this.locateTileInCache(tile);
        if (null == url)
        {
            return null;
        }

        AVList params = new AVListImpl();

        params.setValue(AVKey.WIDTH, tile.getWidth());
        params.setValue(AVKey.HEIGHT, tile.getHeight());
        params.setValue(AVKey.SECTOR, tile.getSector());

        if (null != this.tileParams)
        {
            params.setValues(this.tileParams);
        }

        DataRasterReader reader = this.findReaderFor(url, params);
        if (null == reader)
        {
            String message = Logging.getMessage("generic.UnknownFileFormatOrMatchingReaderNotFound", url.toString());
            Logging.logger().severe(message);
            return null;
        }

        DataRaster[] rasters = reader.read(url, params);
        if (null == rasters)
        {
            String message = Logging.getMessage("generic.CannotCreateRaster", url.toString());
            Logging.logger().severe(message);
            return null;
        }
        return rasters;
    }

    protected DataRasterReader findReaderFor(Object source, AVList params)
    {
        if (null != this.readers)
        {
            for (DataRasterReader reader : this.readers)
            {
                if (reader.canRead(source, params))
                {
                    return reader;
                }
            }
        }

        return null;
    }

    /** ResourceTile Used to define a tile in the World Wind cache. */
    protected static class ResourceTile extends Tile
    {
        protected final Tile parent;

        public ResourceTile(Sector sector, Level level, int row, int column, Tile parent)
        {
            super(sector, level, row, column);
            this.parent = parent;
        }

        public Tile getParent()
        {
            return this.parent;
        }

        public Tile[] subdivide(Level nextLevel)
        {
            Angle p0 = this.getSector().getMinLatitude();
            Angle p2 = this.getSector().getMaxLatitude();
            Angle p1 = Angle.midAngle(p0, p2);

            Angle t0 = this.getSector().getMinLongitude();
            Angle t2 = this.getSector().getMaxLongitude();
            Angle t1 = Angle.midAngle(t0, t2);

            int row = this.getRow();
            int col = this.getColumn();

            ResourceTile[] subTiles = new ResourceTile[4];
            subTiles[0] = new ResourceTile(new Sector(p0, p1, t0, t1), nextLevel, 2 * row, 2 * col, this);
            subTiles[1] = new ResourceTile(new Sector(p0, p1, t1, t2), nextLevel, 2 * row, 2 * col + 1, this);
            subTiles[2] = new ResourceTile(new Sector(p1, p2, t0, t1), nextLevel, 2 * row + 1, 2 * col, this);
            subTiles[3] = new ResourceTile(new Sector(p1, p2, t1, t2), nextLevel, 2 * row + 1, 2 * col + 1, this);

            return subTiles;
        }
    }
}
