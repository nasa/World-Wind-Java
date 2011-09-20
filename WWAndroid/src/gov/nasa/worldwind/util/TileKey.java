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
public class TileKey
{
    protected int levelNumber;
    protected String levelCacheKey;
    // The tile cache key is lazily created because it's only selectively needed and costly to create.
    protected String tileCacheKey;
    protected int row;
    protected int col;
    protected int hash;

    public TileKey(Level level, int row, int column)
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
        this.row = row;
        this.col = column;
        this.hash = this.computeHash();
    }

    public TileKey(int levelNumber, String levelCacheKey, int row, int column)
    {
        if (levelNumber < 0)
        {
            String msg = Logging.getMessage("generic.LevelNumberIsInvalid", levelNumber);
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
        this.row = row;
        this.col = column;
        this.hash = this.computeHash();
    }

    protected TileKey()
    {
    }

    protected int computeHash()
    {
        int result;
        result = this.levelNumber;
        result = 29 * result + (this.levelCacheKey != null ? this.levelCacheKey.hashCode() : 0);
        result = 29 * result + this.row;
        result = 29 * result + this.col;
        return result;
    }

    public int getLevelNumber()
    {
        return this.levelNumber;
    }

    public String getLevelCacheKey()
    {
        return this.levelCacheKey;
    }

    public String getTileCacheKey()
    {
        if (this.tileCacheKey == null)
            this.tileCacheKey = this.createTileCacheKey();

        return this.tileCacheKey;
    }

    protected String createTileCacheKey()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.levelCacheKey).append("/");
        sb.append(this.row).append("/");
        sb.append(this.row).append("_").append(this.col);

        return sb.toString();
    }

    public int getRow()
    {
        return this.row;
    }

    public int getColumn()
    {
        return this.col;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof TileKey)) // Accept subclasses to provide equality with MutableTileKey.
            return false;

        TileKey that = (TileKey) o;
        return this.levelNumber == that.levelNumber
            && this.row == that.row
            && this.col == that.col
            && (this.levelCacheKey != null ? this.levelCacheKey.equals(that.levelCacheKey)
            : that.levelCacheKey == null);
    }

    @Override
    public int hashCode()
    {
        return this.hash;
    }

    @Override
    public String toString()
    {
        return this.getTileCacheKey();
    }
}
