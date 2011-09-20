/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.terrain;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import java.util.*;

/**
 * @author dcollins
 * @version $Id$
 */
public abstract class TiledElevationModel extends AbstractElevationModel
{
    protected abstract void requestTile(TileKey key);

    protected static class ElevationTile extends Tile implements Cacheable
    {
        protected short[] elevations; // the elevations themselves

        protected ElevationTile(Sector sector, Level level, int row, int col, short[] elevations)
        {
            super(sector, level, row, col);

            this.elevations = elevations;
        }

        public short[] getElevations()
        {
            return this.elevations;
        }

        @Override
        public long getSizeInBytes()
        {
            // This tile's size in bytes is computed as follows:
            // superclass: variable
            // elevations: 4 bytes + variable (1 32-bit reference + 2 * elevations buffer capacity)
            // total: 4 bytes + superclass' size in bytes + 2 * elevations buffer capacity

            long size = 4 + super.getSizeInBytes();

            if (this.elevations != null)
                size += 2 * this.elevations.length;

            return size;
        }
    }

    /** Internal class to hold collections of elevation tiles that provide elevations for a specific sector. */
    protected static class ElevationTileSet
    {
        protected TiledElevationModel elevationModel;
        protected final double achievedResolution;
        protected ElevationTile[] tiles;

        protected ElevationTileSet(TiledElevationModel elevationModel, double achievedResolution,
            ElevationTile[] tiles)
        {
            this.elevationModel = elevationModel;
            this.achievedResolution = achievedResolution;
            this.tiles = tiles;
        }

        protected Double getElevation(double latitudeRadians, double longitudeRadians)
        {
            if (this.tiles == null)
                return null;

            for (ElevationTile tile : this.tiles)
            {
                if (tile.getSector().containsRadians(latitudeRadians, longitudeRadians))
                    return this.elevationModel.lookupElevation(tile, latitudeRadians, longitudeRadians);
            }

            // Location is not within this group of tiles, so is outside the coverage of this elevation model.
            return null;
        }
    }

    protected LevelSet levels;
    protected Sector currentSector = new Sector();
    protected TreeSet<ElevationTile> currentTiles = new TreeSet<ElevationTile>(new Comparator<ElevationTile>()
    {
        public int compare(ElevationTile t1, ElevationTile t2)
        {
            if (t2.getLevelNumber() == t1.getLevelNumber() && t2.getRow() == t1.getRow()
                && t2.getColumn() == t1.getColumn())
                return 0;

            // Higher-res levels compare lower than lower-res
            return t1.getLevelNumber() > t2.getLevelNumber() ? -1 : 1;
        }
    });
    protected List<TileKey> requestedTiles = new ArrayList<TileKey>();

    public TiledElevationModel(AVList params)
    {
        super(params);
    }

    public TiledElevationModel(Element element)
    {
        super(element);
    }

    @Override
    protected void initWithParams(AVList params)
    {
        super.initWithParams(params);

        this.levels = new LevelSet(params);
    }

    @Override
    protected void initWithConfigDoc(Element element)
    {
        super.initWithConfigDoc(element);

        this.levels = new LevelSet(LevelSet.paramsFromConfigDoc(element));
    }

    @Override
    protected void init()
    {
    }

    @Override
    protected void validateParams(AVList params, StringBuilder sb)
    {
        super.validateParams(params, sb);
    }

    @Override
    protected void validateConfigDoc(Element element, StringBuilder sb)
    {
        super.validateConfigDoc(element, sb);
    }

    /** {@inheritDoc} */
    public int intersects(Sector sector)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.levels.getSector().contains(sector))
            return 0;

        return this.levels.getSector().intersects(sector) ? 1 : -1;
    }

    /** {@inheritDoc} */
    public boolean contains(Angle latitude, Angle longitude)
    {
        if (latitude == null)
        {
            String msg = Logging.getMessage("nullValue.LatitudeIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (longitude == null)
        {
            String msg = Logging.getMessage("nullValue.LongitudeIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.levels.getSector().contains(latitude, longitude);
    }

    /** {@inheritDoc} */
    public double getBestResolution(Sector sector)
    {
        if (sector == null)
            return this.levels.getLastLevel().getTexelHeightInRadians();

        Level level = this.levels.getLastLevel(sector);
        return level != null ? level.getTexelHeightInRadians() : Double.MAX_VALUE;
    }

    // TODO: implement extreme elevations

    /** {@inheritDoc} */
    public double getElevation(Angle latitude, Angle longitude)
    {
        if (latitude == null)
        {
            String msg = Logging.getMessage("nullValue.LatitudeIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (longitude == null)
        {
            String msg = Logging.getMessage("nullValue.LongitudeIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!this.levels.getSector().contains(latitude, longitude))
            return 0;

        MemoryCache cache = this.getElevationCache();

        Level lastLevel = this.levels.getLastLevel(latitude, longitude);
        TileKey key = new TileKey(lastLevel, Tile.computeRow(lastLevel.getTileDelta().latitude, latitude),
            Tile.computeColumn(lastLevel.getTileDelta().longitude, longitude));
        ElevationTile tile = (ElevationTile) cache.get(key);

        if (tile == null)
        {
            int fallbackRow = key.getRow();
            int fallbackCol = key.getColumn();
            for (int fallbackLevel = key.getLevelNumber() - 1; fallbackLevel >= 0; fallbackLevel--)
            {
                fallbackRow /= 2;
                fallbackCol /= 2;

                Level level = this.levels.getLevel(fallbackLevel);
                if (level.isEmpty()) // everything lower res is empty
                    return this.getMinAndMaxElevations(latitude, longitude)[0];

                TileKey fallbackKey = new TileKey(level, fallbackRow, fallbackCol);
                tile = (ElevationTile) cache.get(fallbackKey);
                if (tile != null)
                    break;
            }
        }

        if (tile == null && !this.levels.getFirstLevel().isEmpty())
        {
            // Request the level-zero tile since it's not in memory
            Level firstLevel = this.levels.getFirstLevel();
            TileKey requestKey = new TileKey(firstLevel, Tile.computeRow(firstLevel.getTileDelta().latitude, latitude),
                Tile.computeColumn(firstLevel.getTileDelta().longitude, longitude));
            this.requestTile(requestKey);

            // Return the best we know about the location's elevation
            return this.getMinAndMaxElevations(latitude, longitude)[0];
        }

        // The containing tile is non-null, so look up the elevation and return.
        return this.lookupElevation(tile, latitude.radians, longitude.radians);
    }

    /** {@inheritDoc} */
    public double getElevations(Sector sector, List<? extends LatLon> locations, double targetResolution,
        double[] buffer)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (locations == null)
        {
            String msg = Logging.getMessage("nullValue.LocationsListIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (targetResolution <= 0)
        {
            String msg = Logging.getMessage("generic.ResolutionIsInvalid", targetResolution);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (buffer == null)
        {
            String msg = Logging.getMessage("nullValue.BufferIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (buffer.length < locations.size())
        {
            String msg = Logging.getMessage("generic.BufferInvalidLength", buffer.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.intersects(sector) == -1)
            return Double.MAX_VALUE;

        Level targetLevel = this.getTargetLevel(sector, targetResolution);
        if (targetLevel == null)
            return Double.MAX_VALUE;

        ElevationTileSet tileSet = this.getElevationTileSet(sector, targetLevel.getLevelNumber());
        if (tileSet == null)
            return Double.MAX_VALUE;

        int index = 0;
        for (LatLon ll : locations)
        {
            Double value = tileSet.getElevation(ll.latitude.radians, ll.longitude.radians);
            if (value != null)
                buffer[index++] = value;
        }

        return tileSet.achievedResolution;
    }

    /** {@inheritDoc} */
    public double getElevations(Sector sector, int numLat, int numLon, double targetResolution, double[] buffer)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (numLat <= 0)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", numLat);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (numLon <= 0)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", numLon);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (targetResolution <= 0)
        {
            String msg = Logging.getMessage("generic.ResolutionIsInvalid", targetResolution);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (buffer == null)
        {
            String msg = Logging.getMessage("nullValue.BufferIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (buffer.length < numLat * numLon)
        {
            String msg = Logging.getMessage("generic.BufferInvalidLength", buffer.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.intersects(sector) == -1)
            return Double.MAX_VALUE;

        Level targetLevel = this.getTargetLevel(sector, targetResolution);
        if (targetLevel == null)
            return Double.MAX_VALUE;

        ElevationTileSet tileSet = this.getElevationTileSet(sector, targetLevel.getLevelNumber());
        if (tileSet == null)
            return Double.MAX_VALUE;

        double minLat = sector.minLatitude.radians;
        double maxLat = sector.maxLatitude.radians;
        double minLon = sector.minLongitude.radians;
        double maxLon = sector.maxLongitude.radians;
        double deltaLat = sector.getDeltaLatRadians() / (numLat > 1 ? numLat - 1 : 1);
        double deltaLon = sector.getDeltaLonRadians() / (numLon > 1 ? numLon - 1 : 1);

        double lat = minLat;
        double lon = minLon;
        int index = 0;

        for (int j = 0; j < numLat; j++)
        {
            // Explicitly set the first and last row to minLat and maxLat, respectively, rather than using the
            // accumulated lat value. We do this to ensure that the Cartesian points of adjacent sectors are a
            // perfect match.
            if (j == 0)
                lat = minLat;
            else if (j == numLat - 1)
                lat = maxLat;
            else
                lat += deltaLat;

            for (int i = 0; i < numLon; i++)
            {
                // Explicitly set the first and last column to minLon and maxLon, respectively, rather than using the
                // accumulated lon value. We do this to ensure that the Cartesian points of adjacent sectors are a
                // perfect match.
                if (i == 0)
                    lon = minLon;
                else if (i == numLon - 1)
                    lon = maxLon;
                else
                    lon += deltaLon;

                Double value = tileSet.getElevation(lat, lon);
                if (value != null)
                    buffer[index++] = value;
            }
        }

        return tileSet.achievedResolution;
    }

    protected MemoryCache getElevationCache()
    {
        if (!WorldWind.getMemoryCacheSet().contains(ElevationTile.class.getName()))
        {
            long size = Configuration.getLongValue(AVKey.ELEVATION_TILE_CACHE_SIZE);
            MemoryCache cache = new BasicMemoryCache((long) (0.8 * size), size);
            cache.setName("Elevation Tiles");
            WorldWind.getMemoryCacheSet().put(ElevationTile.class.getName(), cache);
        }

        return WorldWind.getMemoryCacheSet().get(ElevationTile.class.getName());
    }

    protected double lookupElevation(ElevationTile tile, double latitudeRadians, double longitudeRadians)
    {
        short[] elevations = tile.getElevations();

        Sector sector = tile.getSector();
        int tileHeight = tile.getHeight();
        int tileWidth = tile.getWidth();
        double sectorDeltaLat = sector.getDeltaLatRadians();
        double sectorDeltaLon = sector.getDeltaLonRadians();
        double dLat = sector.maxLatitude.radians - latitudeRadians;
        double dLon = longitudeRadians - sector.minLongitude.radians;
        double sLat = dLat / sectorDeltaLat;
        double sLon = dLon / sectorDeltaLon;

        int j = (int) ((tileHeight - 1) * sLat);
        int i = (int) ((tileWidth - 1) * sLon);
        int k = j * tileWidth + i;

        double eLeft = elevations[k];
        double eRight = i < (tileWidth - 1) ? elevations[k + 1] : eLeft;

        double dw = sectorDeltaLon / (tileWidth - 1);
        double dh = sectorDeltaLat / (tileHeight - 1);
        double ssLon = (dLon - i * dw) / dw;
        double ssLat = (dLat - j * dh) / dh;

        double eTop = eLeft + ssLon * (eRight - eLeft);

        if (j < tileHeight - 1 && i < tileWidth - 1)
        {
            eLeft = elevations[k + tileWidth];
            eRight = elevations[k + tileWidth + 1];
        }

        double eBot = eLeft + ssLon * (eRight - eLeft);
        return eTop + ssLat * (eBot - eTop);
    }

    protected Level getTargetLevel(Sector sector, double targetResolution)
    {
        Level lastLevel = this.levels.getLastLevel(sector); // finest resolution available
        if (lastLevel == null)
            return null;

        if (lastLevel.getTexelHeightInRadians() >= targetResolution)
            return lastLevel; // can't do any better than this

        for (Level level : this.levels.getLevels())
        {
            if (level.getTexelHeightInRadians() <= targetResolution)
                return !level.isEmpty() ? level : null;

            if (level == lastLevel)
                break;
        }

        return lastLevel;
    }

    protected ElevationTileSet getElevationTileSet(Sector sector, int targetLevelNumber)
    {
        // Clear the lists of current tiles and requested tiles.
        this.currentTiles.clear();
        this.requestedTiles.clear();
        // Compute the intersection of the requested sector with the LevelSet's sector. The intersection is used to
        // determine which Tiles in the LevelSet are in the requested sector.
        this.currentSector.intersection(this.levels.getSector(), sector);

        Level targetLevel = this.levels.getLevel(targetLevelNumber);
        Angle deltaLat = targetLevel.getTileDelta().latitude;
        Angle deltaLon = targetLevel.getTileDelta().longitude;

        int firstRow = Tile.computeRow(deltaLat, this.currentSector.minLatitude);
        int lastRow = Tile.computeRow(deltaLat, this.currentSector.maxLatitude);
        int firstCol = Tile.computeColumn(deltaLon, this.currentSector.minLongitude);
        int lastCol = Tile.computeColumn(deltaLon, this.currentSector.maxLongitude);

        MemoryCache cache = this.getElevationCache();
        boolean missingTargetTiles = false;
        boolean missingLevelZeroTiles = false;

        for (int row = firstRow; row <= lastRow; row++)
        {
            for (int col = firstCol; col <= lastCol; col++)
            {
                TileKey key = new TileKey(targetLevel, row, col);
                ElevationTile tile = (ElevationTile) cache.get(key);
                if (tile != null)
                {
                    this.currentTiles.add(tile);
                    continue;
                }

                missingTargetTiles = true;
                this.requestTile(key);

                // Determine the fallback to use. Simultaneously determine a fallback to request that is the next
                // resolution higher than the fallback chosen, if any. This will progressively refine the display until
                // the desired resolution tile arrives.
                TileKey fallbackKey;
                TileKey fallbackToRequest = null;
                int fallbackRow = row;
                int fallbackCol = col;

                for (int fallbackLevelNum = targetLevelNumber - 1; fallbackLevelNum >= 0; fallbackLevelNum--)
                {
                    fallbackRow /= 2;
                    fallbackCol /= 2;

                    fallbackKey = new TileKey(this.levels.getLevel(fallbackLevelNum), fallbackRow, fallbackCol);
                    tile = (ElevationTile) cache.get(fallbackKey);
                    if (tile != null)
                    {
                        if (!this.currentTiles.contains(tile))
                            this.currentTiles.add(tile);

                        break;
                    }
                    else
                    {
                        if (fallbackLevelNum == 0)
                            missingLevelZeroTiles = true;

                        fallbackToRequest = fallbackKey; // keep track of lowest level to request
                    }
                }

                if (fallbackToRequest != null)
                {
                    if (!this.requestedTiles.contains(fallbackToRequest))
                    {
                        this.requestTile(fallbackToRequest);
                        this.requestedTiles.add(fallbackToRequest); // keep track to avoid duplicate requests
                    }
                }
            }
        }

        ElevationTile[] tiles = new ElevationTile[this.currentTiles.size()];
        this.currentTiles.toArray(tiles);

        if (missingLevelZeroTiles || this.currentTiles.isEmpty())
        {
            // Double.MAX_VALUE is a signal for no in-memory tile for a given region of the sector.
            return new ElevationTileSet(this, Double.MAX_VALUE, tiles);
        }
        else if (missingTargetTiles)
        {
            // Use the level of the the lowest resolution found to denote the resolution of this elevation set.
            // The list of tiles is sorted first by level, so use the level of the list's last entry.
            return new ElevationTileSet(this, this.currentTiles.last().getLevel().getTexelHeightInRadians(), tiles);
        }
        else
        {
            return new ElevationTileSet(this, this.currentTiles.last().getLevel().getTexelHeightInRadians(), tiles);

            // TODO
            // Compute the elevation extremes now that the sector is fully resolved
            //if (tiles != null && tiles.size() > 0)
            //{
            //    double[] extremes = elevations.getExtremes(requestedSector);
            //    if (extremes != null)
            //        this.getExtremesLookupCache().add(requestedSector, extremes, 16);
            //}
        }
    }
}
