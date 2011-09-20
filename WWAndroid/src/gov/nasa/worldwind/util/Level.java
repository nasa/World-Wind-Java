/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.LatLon;

/**
 * @author dcollins
 * @version $Id$
 */
public class Level
{
    protected boolean empty;
    protected int levelNumber;
    protected String levelName;
    protected String formatSuffix;
    protected LatLon tileDelta;
    protected int tileWidth;
    protected int tileHeight;
    protected double texelHeight;
    protected String cacheKey;
    protected String service;
    /**
     * The list of absent tiles. A tile is deemed absent if a specified maximum number of attempts have been made to
     * retrieve it. Retrieval attempts are governed by a minimum time interval between successive attempts. If an
     * attempt is made within this interval, the tile is still deemed to be absent until the interval expires. Initially
     * <code>null</code>, this is lazily created in <code>getAbsentTiles</code>.
     */
    protected AbsentResourceList absentTiles;

    public Level(AVList params)
    {
        if (params == null)
        {
            String msg = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        String msg = this.validate(params);
        if (msg != null)
        {
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.levelNumber = (Integer) params.getValue(AVKey.LEVEL_NUMBER);
        this.levelName = (String) params.getValue(AVKey.LEVEL_NAME);
        this.formatSuffix = (String) params.getValue(AVKey.FORMAT_SUFFIX);
        this.service = (String) params.getValue(AVKey.SERVICE);
        this.tileDelta = (LatLon) params.getValue(AVKey.TILE_DELTA);
        this.tileWidth = (Integer) params.getValue(AVKey.TILE_WIDTH);
        this.tileHeight = (Integer) params.getValue(AVKey.TILE_HEIGHT);
        this.texelHeight = this.computeTexelHeight(params);
        this.cacheKey = this.buildCacheKey(params);
    }

    /**
     * Determines whether the constructor arguments are valid.
     *
     * @param params the list of parameters to validate.
     *
     * @return null if valid, otherwise a <code>String</code> containing a description of why it's invalid.
     */
    protected String validate(AVList params)
    {
        StringBuilder sb = new StringBuilder();

        Object o = params.getValue(AVKey.LEVEL_NUMBER);
        if (o == null || !(o instanceof Integer) || ((Integer) o) < 0)
            sb.append(Logging.getMessage("term.LevelNumber"));

        o = params.getValue(AVKey.LEVEL_NAME);
        if (WWUtil.isEmpty(o))
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(Logging.getMessage("term.LevelName"));
        }

        o = params.getValue(AVKey.TILE_DELTA);
        if (o == null || !(o instanceof LatLon))
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(Logging.getMessage("term.TileDelta"));
        }

        o = params.getValue(AVKey.TILE_WIDTH);
        if (o == null || !(o instanceof Integer) || ((Integer) o) < 0)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(Logging.getMessage("term.TileWidth"));
        }

        o = params.getValue(AVKey.TILE_HEIGHT);
        if (o == null || !(o instanceof Integer) || ((Integer) o) < 0)
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(Logging.getMessage("term.TileHeight"));
        }

        o = params.getValue(AVKey.DATA_CACHE_NAME);
        if (WWUtil.isEmpty(o))
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(Logging.getMessage("term.CacheKey"));
        }

        if (sb.length() == 0)
            return null;

        return Logging.getMessage("generic.ParamsAreInvalid", sb.toString());
    }

    protected double computeTexelHeight(AVList params)
    {
        return ((LatLon) params.getValue(AVKey.TILE_DELTA)).latitude.radians /
            ((Integer) params.getValue(AVKey.TILE_HEIGHT)).doubleValue();
    }

    protected String buildCacheKey(AVList params)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(params.getValue(AVKey.DATA_CACHE_NAME)).append("/");
        sb.append(params.getValue(AVKey.LEVEL_NAME));

        return sb.toString();
    }

    public boolean isEmpty()
    {
        return this.empty;
    }

    public void setEmpty(boolean empty)
    {
        this.empty = empty;
    }

    public int getLevelNumber()
    {
        return this.levelNumber;
    }

    public String getLevelName()
    {
        return this.levelName;
    }

    public LatLon getTileDelta()
    {
        return this.tileDelta;
    }

    public int getTileWidth()
    {
        return this.tileWidth;
    }

    public int getTileHeight()
    {
        return this.tileHeight;
    }

    public double getTexelHeightInRadians()
    {
        return this.texelHeight;
    }

    public String getCacheKey()
    {
        return this.cacheKey;
    }

    public AbsentResourceList getAbsentTiles()
    {
        if (this.absentTiles == null)
            this.absentTiles = this.createAbsentTileList();

        return this.absentTiles;
    }

    public String getFormatSuffix()
    {
        return this.formatSuffix;
    }

    public String getService()
    {
        return this.service;
    }

    /**
     * Returns an absent-resource list with default values for max tries (3), check interval (10 seconds) and try-again
     * interval (60 seconds).
     *
     * @return the absent-resource list that tracks which tiles are missing for this level.
     */
    protected AbsentResourceList createAbsentTileList()
    {
        return new AbsentResourceList();
    }
}
