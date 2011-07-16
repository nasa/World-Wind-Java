/* Copyright (C) 2001, 2010 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */

package gov.nasa.worldwind.servers.applications.ve;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * @author Lado Garakanidze
 * @version $
 */
public class VirtualEarthTile
{
    public static final int SIZE = 256;

    protected String    path = null;
    protected URL       url = null;
    protected String    quadKey = null;
    protected int       level = 0;
    protected Sector    sector = null;
    protected Point     tile = null;

    protected VirtualEarthLayer layer = null;

    /**
     *
     * @param tileX         Tile X coordinate
     * @param tileY         Tile Y coordinate
     * @param levelOfDetail Level of detail, from 1 (lowest detail) to 23 (highest detail)
     * @param layer         A Virtual Earth Layer (AERIAL, HYBRID, or ROAD)
     * @param sector        A Sector
     * @throws IOException  If the string specifies an unknown protocol
     * @throws IllegalArgumentException if any of required parameters is null
     */
    public VirtualEarthTile( int tileX, int tileY, int levelOfDetail, VirtualEarthLayer layer, Sector sector )
        throws IOException, IllegalArgumentException
    {
        if( null == sector )
        {
            String message = Logging.getMessage("nullValue.SectorIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if( null == layer )
        {
            String message = Logging.getMessage("nullValue.LayerIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.layer = layer;
        this.tile = new Point( tileX, tileY );
        this.level = levelOfDetail;
        this.sector = sector;
        this.quadKey = calcQuadKey( tileX, tileY, levelOfDetail );
        this.path = calcTilePath( tileX, tileY, levelOfDetail, layer, this.quadKey );
        this.url = calcTileURL( layer, this.quadKey );
    }

    public VirtualEarthLayer getLayer()
    {
        return this.layer;
    }

    /**
     * Retrieves a level of detail
     * @return lLevel of detail, from 1 (lowest detail) to 23 (highest detail)
     */
    public int getLevel()
    {
        return this.level;
    }

    public int getSize()
    {
        return SIZE;
    }

    public int getHeight()
    {
        return SIZE;
    }

    public int getWidth()
    {
        return SIZE;
    }

    public Sector getSector()
    {
        return this.sector;
    }

    public void setPath(String tilePath)
    {
        this.path = tilePath;
    }
    
    public String getPath()
    {
        return this.path;
    }

    public URL getURL()
    {
        return this.url;
    }

    public String getQuadKey()
    {
        return this.quadKey;
    }

    /**
     * Converts tile XY coordinates into a QuadKey at a specified level of detail.
     *
     * @param tileX         Tile X coordinate
     * @param tileY         Tile Y coordinate
     * @param levelOfDetail Level of detail, from 1 (lowest detail) to 23 (highest detail)
     *
     * @return A string containing the QuadKey
     */
    protected static String calcQuadKey(int tileX, int tileY, int levelOfDetail)
    {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--)
        {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0)
            {
                digit++;
            }
            if ((tileY & mask) != 0)
            {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    /**
     * Calculates a path to a cached file that may contain a tile
     *
     * @param tileX         Tile X coordinate
     * @param tileY         Tile Y coordinate
     * @param levelOfDetail Level of detail, from 1 (lowest detail) to 23 (highest detail)
     * @param layer         Virtual Earth Layer (AERIAL, HYBRID, or ROAD)
     * @param quadKey       A string containing the QuadKey
     * @return String       A tile path that could be used to retrieve/save a tile
     */
    protected static String calcTilePath(int tileX, int tileY, int levelOfDetail, VirtualEarthLayer layer, String quadKey )
    {
        if( null == layer )
        {
            String message = Logging.getMessage("nullValue.LayerIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if( null == quadKey || 0 == quadKey.length())
            quadKey = calcQuadKey( tileX, tileY, levelOfDetail );

        StringBuffer filePath = new StringBuffer();
        filePath.append( layer.getLetter() ).append( File.separator );
        filePath.append( levelOfDetail ).append( File.separator );
        filePath.append( tileY ).append( File.separator );
        filePath.append( tileX ).append( File.separator );
        filePath.append( quadKey ).append( layer.getExt() );

        return filePath.toString();
    }

    /**
     * Calculates a URL path to the tile
     *
     * @param layer     A Virtual Earth Layer (AERIAL, HYBRID, or ROAD)
     * @param quadKey   A string containing the QuadKey
     * @return URL      A URL path to the tile
     * @throws MalformedURLException  If the string specifies an unknown protocol
     * @throws IllegalArgumentException if any of required parameters is null
     */
    protected static URL calcTileURL(VirtualEarthLayer layer, String quadKey )
        throws MalformedURLException, IllegalArgumentException
    {
        if( null == layer )
        {
            String message = Logging.getMessage("nullValue.LayerIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if( null == quadKey || 0 == quadKey.length())
        {
            String message = Logging.getMessage("nullValue.KeyIsNull" );
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        StringBuffer urlPath = new StringBuffer();
        urlPath.append("http://").append( layer.getLetter() );
        urlPath.append( quadKey.charAt( quadKey.length()-1 ) );
        urlPath.append( ".ortho.tiles.virtualearth.net/tiles/" );
        urlPath.append( layer.getLetter() );
        urlPath.append( quadKey ).append( layer.getExt() );
        //TODO no clue what "g=15" parameter is
        urlPath.append( "?g=15" );
        return new URL( urlPath.toString() );
    }
}
