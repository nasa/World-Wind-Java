/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.DrawContext;

import java.util.List;

/**
 * Large images and most imagery and elevation-data sets are subdivided in order to display visible portions quickly and
 * without excessive memory usage. Each subdivision is called a tile, and a collections of adjacent tiles corresponding
 * to a common spatial resolution is typically maintained in a {@link Level}. A collection of levels of progressive
 * resolutions are maintained in a {@link LevelSet}. The <code>Tile</code> class represents a single tile of a
 * subdivided image or elevation raster.
 * <p/>
 * Individual tiles are identified by the level, row and column of the tile within its containing level set.
 *
 * @author dcollins
 * @version $Id$
 */
public class Tile implements Cacheable
{
    public interface TileFactory
    {
        Tile createTile(Sector sector, Level level, int row, int column);
    }

    protected Sector sector;
    protected Level level;
    protected int row;
    protected int col;
    protected TileKey tileKey;
    protected Vec4[] referencePoints;
    // The following is late bound because it's only selectively needed and costly to create
    protected String path;
    // Temporary properties used to avoid constant reallocation of data used during tile subdivision.
    protected static MutableTileKey mutableTileKey = new MutableTileKey();

    /**
     * Constructs a tile for a given sector, level, row and column of the tile's containing tile set.
     *
     * @param sector the sector corresponding with the tile.
     * @param level  the tile's level within a containing level set.
     * @param row    the row index (0 origin) of the tile within the indicated level.
     * @param column the column index (0 origin) of the tile within the indicated level.
     *
     * @throws IllegalArgumentException if <code>sector</code> or <code>level</code> is null.
     */
    public Tile(Sector sector, Level level, int row, int column)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (level == null)
        {
            String msg = Logging.getMessage("nullValue.LevelIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (row < 0)
        {
            String msg = Logging.getMessage("generic.RowIndexOutOfRange", row);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (column < 0)
        {
            String msg = Logging.getMessage("generic.ColumnIndexOutOfRange", column);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.sector = sector;
        this.level = level;
        this.row = row;
        this.col = column;
        this.tileKey = this.createTileKey();
    }

    protected TileKey createTileKey()
    {
        return new TileKey(this.level, this.row, this.col);
    }

    public Sector getSector()
    {
        return this.sector;
    }

    public Level getLevel()
    {
        return this.level;
    }

    public int getLevelNumber()
    {
        return this.level.getLevelNumber();
    }

    public int getRow()
    {
        return this.row;
    }

    public int getColumn()
    {
        return this.col;
    }

    public int getWidth()
    {
        return this.getLevel().getTileWidth();
    }

    public int getHeight()
    {
        return this.getLevel().getTileHeight();
    }

    public TileKey getTileKey()
    {
        return this.tileKey;
    }

    public long getSizeInBytes()
    {
        // This tile's size in bytes is computed as follows:
        // self: 4 bytes (1 32-bit reference)
        // sector: 84 bytes (8 64-bit doubles + 4 32-bit internal references + 1 32-bit reference)
        // level: 4 bytes (1 32-bit reference)
        // row, col: 8 bytes (2 32-bit integers)
        // tileKey: 24 bytes + variable (4 32-bit integers + 2 32-bit references + variable num of 16-bit characters)
        // points 180 bytes (5 32-bit references + 20 64-bit floats)
        // total: 304 bytes + variable num of 16-bit characters

        long size = 304;

        if (this.tileKey.getLevelCacheKey() != null)
            size += 2 * this.tileKey.getLevelCacheKey().length();
        if (this.tileKey.getTileCacheKey() != null)
            size += 2 * this.tileKey.getTileCacheKey().length();

        return size;
    }

    public Vec4[] getReferencePoints()
    {
        return this.referencePoints;
    }

    public void setReferencePoints(Vec4[] points)
    {
        this.referencePoints = points;
    }

    public String getPath()
    {
        if (this.path == null)
        {
            StringBuilder sb = new StringBuilder();

            sb.append(this.level.getCacheKey()).append("/")
                .append(this.row).append("/")
                .append(this.row).append("_").append(this.col);

            if (!this.level.isEmpty())
                sb.append(this.level.getFormatSuffix());

            this.path = sb.toString();
        }

        return this.path;
    }

    public boolean mustSubdivide(DrawContext dc, double detailFactor)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        Vec4[] points = this.referencePoints;
        if (points == null)
        {
            points = new Vec4[] {new Vec4(), new Vec4(), new Vec4(), new Vec4(), new Vec4()};
            this.sector.computeCornerPoints(dc.getGlobe(), dc.getVerticalExaggeration(), points);
            this.sector.computeCentroidPoint(dc.getGlobe(), dc.getVerticalExaggeration(), points[4]);
        }

        // Get the eye distance squared for each of the sector's corners and its center. We use the distance squared
        // because we're only concerned about magnitude here, and using distance squared enables us to reduce the number
        // of sqrt calls from 5 to 1.
        View view = dc.getView();
        double d1 = view.getEyePoint().distanceToSquared3(points[0]);
        double d2 = view.getEyePoint().distanceToSquared3(points[1]);
        double d3 = view.getEyePoint().distanceToSquared3(points[2]);
        double d4 = view.getEyePoint().distanceToSquared3(points[3]);
        double d5 = view.getEyePoint().distanceToSquared3(points[4]);

        // Find the minimum eye distance squared. Compute cell height at the corresponding point. Cell height is
        // radius * radian texel size.
        double minDistanceSq = d1;
        double cellHeight = points[0].getLength3() * this.level.getTexelHeightInRadians();
        double texelSize = level.getTexelHeightInRadians();

        if (d2 < minDistanceSq)
        {
            minDistanceSq = d2;
            cellHeight = points[1].getLength3() * texelSize;
        }
        if (d3 < minDistanceSq)
        {
            minDistanceSq = d3;
            cellHeight = points[2].getLength3() * texelSize;
        }
        if (d4 < minDistanceSq)
        {
            minDistanceSq = d4;
            cellHeight = points[3].getLength3() * texelSize;
        }
        if (d5 < minDistanceSq)
        {
            minDistanceSq = d5;
            cellHeight = points[4].getLength3() * texelSize;
        }

        // Split when the cell height (length of a texel) becomes greater than the specified fraction of the eye
        // distance. The fraction is specified as a power of 10. For example, a detail factor of 3 means split when the
        // cell height becomes more than one thousandth of the eye distance. Another way to say it is, use the current
        // tile if its cell height is less than the specified fraction of the eye distance.
        //
        // NOTE: It's tempting to instead compare a screen pixel size to the texel size, but that calculation is
        // window-size dependent and results in selecting an excessive number of tiles when the window is large.
        return cellHeight > Math.sqrt(minDistanceSq) * Math.pow(10, -detailFactor);
    }

    /**
     * Splits this tile into four tiles; one for each sub quadrant of this tile. This attempts to retrieve each sub tile
     * from the tile cache.
     *
     * @param nextLevel the level for the sub tiles.
     *
     * @return a four-element array that contains this tile's sub tiles.
     *
     * @throws IllegalArgumentException if <code>nextLevel</code> is <code>null</code>.
     */
    public Tile[] subdivide(Level nextLevel, MemoryCache cache, TileFactory factory)
    {
        if (nextLevel == null)
        {
            String msg = Logging.getMessage("nullValue.LevelIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (cache == null)
        {
            String msg = Logging.getMessage("nullValue.CacheIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (factory == null)
        {
            String msg = Logging.getMessage("nullValue.FactoryIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        Tile[] result = new Tile[4];

        double p0 = this.sector.minLatitude.degrees;
        double p2 = this.sector.maxLatitude.degrees;
        double p1 = 0.5 * (p0 + p2);

        double t0 = this.sector.minLongitude.degrees;
        double t2 = this.sector.maxLongitude.degrees;
        double t1 = 0.5 * (t0 + t2);

        int subRow = 2 * this.row;
        int subCol = 2 * this.col;
        mutableTileKey.set(nextLevel, subRow, subCol);
        Tile subTile = (Tile) cache.get(mutableTileKey);
        if (subTile != null)
            result[0] = subTile;
        else
            result[0] = factory.createTile(Sector.fromDegrees(p0, p1, t0, t1), nextLevel, subRow, subCol);

        subRow = 2 * this.row;
        subCol = 2 * this.col + 1;
        mutableTileKey.set(nextLevel, subRow, subCol);
        subTile = (Tile) cache.get(mutableTileKey);
        if (subTile != null)
            result[1] = subTile;
        else
            result[1] = factory.createTile(Sector.fromDegrees(p0, p1, t1, t2), nextLevel, subRow, subCol);

        subRow = 2 * this.row + 1;
        subCol = 2 * this.col;
        mutableTileKey.set(nextLevel, subRow, subCol);
        subTile = (Tile) cache.get(mutableTileKey);
        if (subTile != null)
            result[2] = subTile;
        else
            result[2] = factory.createTile(Sector.fromDegrees(p1, p2, t0, t1), nextLevel, subRow, subCol);

        subRow = 2 * this.row + 1;
        subCol = 2 * this.col + 1;
        mutableTileKey.set(nextLevel, subRow, subCol);
        subTile = (Tile) cache.get(mutableTileKey);
        if (subTile != null)
            result[3] = subTile;
        else
            result[3] = factory.createTile(Sector.fromDegrees(p1, p2, t1, t2), nextLevel, subRow, subCol);

        return result;
    }

    public static void createTilesForLevel(Level level, Sector sector, TileFactory factory, List<Tile> result)
    {
        if (level == null)
        {
            String msg = Logging.getMessage("nullValue.LevelIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (factory == null)
        {
            String msg = Logging.getMessage("nullValue.FactoryIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (result == null)
        {
            String msg = Logging.getMessage("nullValue.ResultIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        Angle deltaLat = level.tileDelta.latitude;
        Angle deltaLon = level.tileDelta.longitude;

        int firstRow = computeRow(deltaLat, sector.minLatitude);
        int lastRow = computeRow(deltaLat, sector.maxLatitude);
        int firstCol = computeColumn(deltaLon, sector.minLongitude);
        int lastCol = computeColumn(deltaLon, sector.maxLongitude);

        Angle firstRowLat = new Angle();
        Angle firstRowLon = new Angle();
        computeRowLatitude(deltaLat, firstRow, firstRowLat);
        computeColumnLongitude(deltaLon, firstCol, firstRowLon);

        double minLat = firstRowLat.degrees;
        double minLon;
        double maxLat;
        double maxLon;

        for (int row = firstRow; row <= lastRow; row++)
        {
            maxLat = minLat + deltaLat.degrees;
            minLon = firstRowLon.degrees;

            for (int col = firstCol; col <= lastCol; col++)
            {
                maxLon = minLon + deltaLon.degrees;

                result.add(factory.createTile(Sector.fromDegrees(minLat, maxLat, minLon, maxLon), level, row, col));

                minLon = maxLon;
            }

            minLat = maxLat;
        }
    }

    /**
     * Computes the row index of a latitude in the global tile grid corresponding to a specified grid interval.
     *
     * @param tileDelta the grid interval
     * @param latitude  the latitude for which to compute the row index
     *
     * @return the row index of the row containing the specified latitude
     *
     * @throws IllegalArgumentException if <code>delta</code> is null or non-positive, or <code>latitude</code> is null,
     *                                  greater than positive 90 degrees, or less than  negative 90 degrees
     */
    public static int computeRow(Angle tileDelta, Angle latitude)
    {
        if (tileDelta == null)
        {
            String msg = Logging.getMessage("nullValue.TileDeltaIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (latitude == null)
        {
            String msg = Logging.getMessage("nullValue.LatitudeIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (latitude.degrees < -90d || latitude.degrees > 90d)
        {
            String msg = Logging.getMessage("generic.LatitudeOutOfRange", latitude);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        int row = (int) ((latitude.degrees + 90d) / tileDelta.degrees);

        // Latitude is at the end of the grid. Subtract 1 from the computed row to return the last row.
        if (latitude.degrees == 90d)
            row = row - 1;

        return row;
    }

    /**
     * Computes the column index of a longitude in the global tile grid corresponding to a specified grid interval.
     *
     * @param tileDelta the grid interval
     * @param longitude the longitude for which to compute the column index
     *
     * @return the column index of the column containing the specified latitude
     *
     * @throws IllegalArgumentException if <code>delta</code> is null or non-positive, or <code>longitude</code> is
     *                                  null, greater than positive 180 degrees, or less than  negative 180 degrees
     */
    public static int computeColumn(Angle tileDelta, Angle longitude)
    {
        if (tileDelta == null)
        {
            String msg = Logging.getMessage("nullValue.TileDeltaIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (longitude == null)
        {
            String msg = Logging.getMessage("nullValue.LongitudeIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (longitude.degrees < -180d || longitude.degrees > 180d)
        {
            String msg = Logging.getMessage("generic.LongitudeOutOfRange", longitude);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        int col = (int) ((longitude.degrees + 180d) / tileDelta.degrees);

        // Longitude is at the end of the grid. Subtract 1 from the computed column to return the last column.
        if (longitude.degrees == 180d)
            col = col - 1;

        return col;
    }

    /**
     * Determines the minimum latitude of a row in the global tile grid corresponding to a specified grid interval.
     *
     * @param tileDelta the grid interval
     * @param row       the row index of the row in question
     * @param result    contains the minimum latitude of the tile corresponding to the specified row after this method
     *                  returns.
     *
     * @throws IllegalArgumentException if the grid interval (<code>delta</code>) is null or zero or the row index is
     *                                  negative.
     */
    public static void computeRowLatitude(Angle tileDelta, int row, Angle result)
    {
        if (tileDelta == null)
        {
            String msg = Logging.getMessage("nullValue.TileDeltaIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (row < 0)
        {
            String msg = Logging.getMessage("generic.RowIndexOutOfRange", row);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (result == null)
        {
            String msg = Logging.getMessage("nullValue.ResultIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        result.setDegrees(row * tileDelta.degrees - 90d);
    }

    /**
     * Determines the minimum longitude of a column in the global tile grid corresponding to a specified grid interval.
     *
     * @param tileDelta the grid interval
     * @param column    the row index of the row in question
     * @param result    contains the minimum longitude of the tile corresponding to the specified column after this
     *                  method returns.
     *
     * @throws IllegalArgumentException if the grid interval (<code>delta</code>) is null or zero or the column index is
     *                                  negative.
     */
    public static void computeColumnLongitude(Angle tileDelta, int column, Angle result)
    {
        if (tileDelta == null)
        {
            String msg = Logging.getMessage("nullValue.TileDeltaIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (column < 0)
        {
            String msg = Logging.getMessage("generic.ColumnIndexOutOfRange", column);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (result == null)
        {
            String msg = Logging.getMessage("nullValue.ResultIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        result.setDegrees(column * tileDelta.degrees - 180d);
    }

    @Override
    public boolean equals(Object o)
    {
        // Equality based only on the tile key
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Tile that = (Tile) o;
        return this.tileKey.equals(that.tileKey);
    }

    @Override
    public int hashCode()
    {
        return this.tileKey.hashCode();
    }

    @Override
    public String toString()
    {
        return this.tileKey.getTileCacheKey();
    }
}
