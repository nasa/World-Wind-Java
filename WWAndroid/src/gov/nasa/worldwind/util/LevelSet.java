/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import java.util.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class LevelSet
{
    protected Sector sector;
    protected LatLon lzTileDelta;
    protected List<Level> levels = new ArrayList<Level>();

    public LevelSet(AVList params)
    {
        if (params == null)
        {
            String msg = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        StringBuilder sb = new StringBuilder();
        this.validateParams(params, sb);
        if (sb.length() > 0)
        {
            String msg = Logging.getMessage("generic.ParamsAreInvalid", sb.toString());
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.sector = (Sector) params.getValue(AVKey.SECTOR);
        this.lzTileDelta = (LatLon) params.getValue(AVKey.LEVEL_ZERO_TILE_DELTA);

        int numLevels = (Integer) params.getValue(AVKey.NUM_LEVELS);
        AVList levelParams = params.copy();
        double latDelta = this.lzTileDelta.latitude.degrees;
        double lonDelta = this.lzTileDelta.longitude.degrees;

        int numEmptyLevels = 0;
        Object o = params.getValue(AVKey.NUM_EMPTY_LEVELS);
        if (o != null && o instanceof Number)
            numEmptyLevels = ((Number) o).intValue();

        for (int i = 0; i < numLevels; i++)
        {
            // Level numbers and empty levels: the level numbers are zero-based, so the first non empty level's number
            // is equal to the number of empty levels. For example, if there are 4 empty levels, level numbers
            // 0, 1, 2, 3 are empty. If there 0 empty levels (the default), then i is never less than numEmptyLevels and
            // all levels are marked as non-empty. Level ordinal numbers are assigned starting with 0, and ignore empty
            // levels. Level names assigned starting with 0 at the first non-empty level, and empty levels are assigned
            // the name "empty".
            boolean isEmpty = i < numEmptyLevels;

            levelParams.setValue(AVKey.LEVEL_NUMBER, i);
            levelParams.setValue(AVKey.LEVEL_NAME, isEmpty ? "empty" : Integer.toString(i - numEmptyLevels));
            levelParams.setValue(AVKey.TILE_DELTA, LatLon.fromDegrees(latDelta, lonDelta));

            Level level = this.createLevel(levelParams);
            level.setEmpty(isEmpty);
            this.levels.add(level);

            latDelta /= 2d;
            lonDelta /= 2d;
        }
    }

    /**
     * Determines whether the constructor arguments are valid.
     *
     * @param params the list of parameters to validate.
     * @param sb     the StringBuilder to append a description of why it's invalid.
     */
    protected void validateParams(AVList params, StringBuilder sb)
    {
        Object o = params.getValue(AVKey.SECTOR);
        if (o == null || !(o instanceof Sector))
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(Logging.getMessage("term.Sector"));
        }

        o = params.getValue(AVKey.LEVEL_ZERO_TILE_DELTA);
        if (o == null || !(o instanceof LatLon))
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(Logging.getMessage("term.TileDelta"));
        }

        o = params.getValue(AVKey.NUM_LEVELS);
        if (o == null || !(o instanceof Integer) || ((Integer) o) < 1)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(Logging.getMessage("term.NumLevels"));
        }
    }

    protected Level createLevel(AVList params)
    {
        return new Level(params);
    }

    public static AVList paramsFromConfigDoc(Element element)
    {
        if (element == null)
        {
            String msg = Logging.getMessage("nullValue.ElementIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        AVList params = new AVListImpl();
        XPath xpath = WWXML.makeXPath();

        params.setValue(AVKey.SECTOR, WWXML.getSector(element, "Sector", xpath));
        // TODO would this make more sense as AVKey.GET_MAP_URL?
        params.setValue(AVKey.SERVICE, WWXML.getText(element, "Service/GetMapURL", xpath));
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, WWXML.getLatLon(element, "LevelZeroTileDelta/LatLon", xpath));
        params.setValue(AVKey.TILE_WIDTH, WWXML.getInteger(element, "TileSize/Dimension/@width", xpath));
        params.setValue(AVKey.TILE_HEIGHT, WWXML.getInteger(element, "TileSize/Dimension/@height", xpath));
        params.setValue(AVKey.NUM_LEVELS, WWXML.getInteger(element, "NumLevels/@count", xpath));
        params.setValue(AVKey.NUM_EMPTY_LEVELS, WWXML.getInteger(element, "NumLevels/@numEmpty", xpath));
        params.setValue(AVKey.DATA_CACHE_NAME, WWXML.getText(element, "DataCacheName", xpath));
        params.setValue(AVKey.FORMAT_SUFFIX, WWXML.getText(element, "FormatSuffix", xpath));

        return params;
    }

    public Sector getSector()
    {
        return this.sector;
    }

    public LatLon getLevelZeroTileDelta()
    {
        return this.lzTileDelta;
    }

    public int getNumLevels()
    {
        return this.levels.size();
    }

    public List<Level> getLevels()
    {
        return this.levels;
    }

    public Level getLevel(int levelNumber)
    {
        return (levelNumber >= 0 && levelNumber < this.levels.size()) ? this.levels.get(levelNumber) : null;
    }

    public Level getFirstLevel()
    {
        return this.levels.size() > 0 ? this.levels.get(0) : null;
    }

    public Level getLastLevel()
    {
        return this.levels.size() > 0 ? this.levels.get(this.levels.size() - 1) : null;
    }

    public Level getLastLevel(Sector sector)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!this.sector.intersects(sector))
            return null;

        return this.getLastLevel();
    }

    public Level getLastLevel(Angle latitude, Angle longitude)
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

        if (!this.sector.contains(latitude, longitude))
            return null;

        return this.getLastLevel();
    }

    public boolean isFinalLevel(int levelNum)
    {
        return levelNum == this.levels.size() - 1;
    }

    public boolean isTileAbsent(TileKey key)
    {
        if (key == null)
        {
            String msg = Logging.getMessage("nullValue.KeyIsNull");
            throw new IllegalArgumentException(msg);
        }

        Level level = this.getLevel(key.getLevelNumber());
        return level == null || level.getAbsentTiles().isResourceAbsent(key.getTileCacheKey());
    }

    public void markTileAbsent(TileKey key)
    {
        if (key == null)
        {
            String msg = Logging.getMessage("nullValue.KeyIsNull");
            throw new IllegalArgumentException(msg);
        }

        Level level = this.getLevel(key.getLevelNumber());
        if (level != null)
            level.getAbsentTiles().markResourceAbsent(key.getTileCacheKey());
    }

    public void unmarkTileAbsent(TileKey key)
    {
        if (key == null)
        {
            String msg = Logging.getMessage("nullValue.KeyIsNull");
            throw new IllegalArgumentException(msg);
        }

        Level level = this.getLevel(key.getLevelNumber());
        if (level != null)
            level.getAbsentTiles().unmarkResourceAbsent(key.getTileCacheKey());
    }
}
