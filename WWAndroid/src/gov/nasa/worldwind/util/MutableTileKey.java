/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

/**
 * @author dcollins
 * @version $Id$
 */
public class MutableTileKey extends TileKey
{
    public MutableTileKey()
    {
    }

    public MutableTileKey(Level level, int row, int column)
    {
        super(level, row, column);
    }

    public MutableTileKey(int levelNumber, String levelCacheKey, int row, int column)
    {
        super(levelNumber, levelCacheKey, row, column);
    }

    public MutableTileKey set(Level level, int row, int column)
    {
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

        this.levelNumber = level.getLevelNumber();
        this.levelCacheKey = level.getCacheKey();
        this.tileCacheKey = null;
        this.row = row;
        this.col = column;
        this.hash = this.computeHash();

        return this;
    }

    public MutableTileKey set(int levelNumber, String levelCacheKey, int row, int column)
    {
        if (levelNumber < 0)
        {
            String msg = Logging.getMessage("TileKey.levelIsLessThanZero");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (WWUtil.isEmpty(levelCacheKey))
        {
            String msg = Logging.getMessage("nullValue.CacheKeyIsNull");
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

        this.levelNumber = levelNumber;
        this.levelCacheKey = levelCacheKey;
        this.tileCacheKey = null;
        this.row = row;
        this.col = column;
        this.hash = this.computeHash();

        return this;
    }
}
