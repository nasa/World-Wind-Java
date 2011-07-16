/* Copyright (C) 2001, 2010 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */

package gov.nasa.worldwind.servers.applications.ve;

import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Lado Garakanidze
 * @version $
 */

public class VirtualEarthTileSystem
{
    private static final double MinLatitude = -85.05112878;
    private static final double MaxLatitude = 85.05112878;
    private static final double MinLongitude = -180;
    private static final double MaxLongitude = 180;
    private static final int    VE_MAX_TILE_SIZE = 256;

//    private static final double EARTH_CIRCUM = Earth.WGS84_EQUATORIAL_RADIUS * 2d * Math.PI;

    private VirtualEarthTileSystem()
    {
    }

    /**
     * Clips a number to the specified minimum and maximum values.
     *
     * @param n        The number to clip
     * @param minValue Minimum allowable value
     * @param maxValue Maximum allowable value
     *
     * @return The clipped value
     */
    private static double clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    /**
     * Determines the map width and height (in pixels) at a specified level of detail.
     *
     * @param levelOfDetail Level of detail, from 1 (lowest) to 23 (highest)
     *
     * @return The map width and height in pixels
     */
    public static long calcMapSize(int levelOfDetail)
    {
        return (256L << levelOfDetail);
    }

    /**
     * Determines the ground resolution (in meters per pixel) at a specified latitude and level of detail.
     *
     * @param latitude      Latitude (in degrees) at which to measure the ground resolution
     * @param levelOfDetail Level of detail, from 1 (lowest) to 23 (highest)
     *
     * @return The ground resolution, in meters per pixel
     */
//    public static double calcGroundResolution(double latitude, int levelOfDetail)
//    {
//        latitude = clip(latitude, MinLatitude, MaxLatitude);
//        return Math.cos(latitude * Math.PI / 180) * EARTH_CIRCUM / calcMapSize(levelOfDetail);
//    }

    /**
     * Determines the map scale at a specified latitude, level of detail, and screen resolution.
     *
     * @param latitude      Latitude (in degrees) at which to measure the map scale
     * @param levelOfDetail Level of detail, from 1 (lowest) to 23 (highest)
     * @param screenDpi     Resolution of the screen, in dots per inch.
     *
     * @return The map scale, expressed as the denominator N of the ratio 1 : N
     */
//    public static double calcMapScale(double latitude, int levelOfDetail, int screenDpi)
//    {
//        return calcGroundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
//    }

    /**
     * Converts a point from latitude/longitude WGS-84 coordinates (in degrees) into pixel XY coordinates at a specified
     * level of detail.
     *
     * @param latitude      Latitude of the point, in degrees
     * @param longitude     Longitude of the point, in degrees
     * @param levelOfDetail Level of detail, from 1 (lowest detail) to 23 (highest detail)
     *
     * @return Point X and Y coordinates of the point
     */
    public static Point LatLongToPixelXY(double latitude, double longitude, int levelOfDetail)
    {
        latitude = clip(latitude, MinLatitude, MaxLatitude);
        longitude = clip(longitude, MinLongitude, MaxLongitude);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        long mapSize = calcMapSize(levelOfDetail);

        int px = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
        int py = (int) clip(y * mapSize + 0.5, 0, mapSize - 1);

        return new Point(px, py);
    }

    /**
     * Converts a point from pixel XY coordinates at a specified level of detail into latitude/longitude WGS-84
     * coordinates (in degrees)
     *
     * @param pixelX        X coordinate in pixels
     * @param pixelY        Y coordinate in pixels
     * @param levelOfDetail Level of detail, from 1 (lowest detail) to 23 (highest detail)
     *
     * @return LatLon   Geodetic coordinates
     */
    public static LatLon PixelXYToLatLong(int pixelX, int pixelY, int levelOfDetail)
    {
        double mapSize = (double) calcMapSize(levelOfDetail);

        double x = (pixelX - 0.5) / mapSize;
        double y = (pixelY - 0.5) / mapSize;

        double lon = 360 * x - 180;

        double b = Math.pow(Math.E, -4 * Math.PI * (y - 0.5));
        double sinLatitude = (b - 1) / (1 + b);

        double lat = Math.asin(sinLatitude) * 180 / Math.PI;

        return LatLon.fromDegrees(lat, lon);
    }

    /**
     * Converts pixel XY coordinates into tile XY coordinates
     *
     * @param pixelX Pixel X coordinate
     * @param pixelY Pixel Y coordinate
     *
     * @return Tile coordinates as Point object
     */
    public static Point PixelXYToTileXY(int pixelX, int pixelY)
    {
        return new Point(pixelX / 256, pixelY / 256);
    }

    /**
     * Converts tile XY coordinates into a QuadKey at a specified level of detail.
     *
     * @param tileX         Tile X coordinate
     * @param tileY         Tile X coordinate
     * @param levelOfDetail Level of detail, from 1 (lowest detail) to 23 (highest detail)
     *
     * @return A string containing the QuadKey
     */
    public static String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail)
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

//    public static int getZoomLevelByArcDistance(double arcDistance, int pixelsPerTile)
//    {
//        //arcDistance in meters
//        int totalLevels = 24;
//        int level = 0;
//        for (level = 1; level <= totalLevels; level++)
//        {
//            double metersPerPixel = calcMetersPerPixel(level, pixelsPerTile);
//            double totalDistance = metersPerPixel * pixelsPerTile;
//            if (arcDistance > totalDistance)
//            {
//                break;
//            }
//        }
//        return level - 1;
//    }

//    public static double MetersPerTile(int zoom, int pixelsPerTile)
//    {
//        return calcMetersPerPixel(zoom, pixelsPerTile) * pixelsPerTile;
//    }

//    public static double calcMetersPerPixel(int zoom, int pixelsPerTile)
//    {
//        double arc;
//        arc = EARTH_CIRCUM / ((1 << zoom) * pixelsPerTile);
//        return arc;
//    }

    public static int getZoomLevelByTrueViewRange(double trueViewRange)
    {
        int maxLevel = 1;
        int minLevel = 19;
        int numLevels = minLevel - maxLevel + 1;
        int retLevel = maxLevel;
        for (int i = 0; i < numLevels; i++)
        {
            retLevel = i + maxLevel;

            double viewAngle = 180;
            for (int j = 0; j < i; j++)
            {
                viewAngle = viewAngle / 2.0;
            }
            if (trueViewRange >= viewAngle)
            {
                break;
            }
        }
        return retLevel;
    }

    public static VirtualEarthTile[] createTiles(Sector bbox /*int wwLevel, int wwRow, int wwCol*/,  VirtualEarthLayer layer )
        throws WWRuntimeException
    {
        if( null == bbox )
        {
            String message = Logging.getMessage("nullValue.SectorIsNull" );
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        if( null == layer )
        {
            String message = Logging.getMessage("nullValue.LayerIsNull" );
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        int level = getZoomLevelByTrueViewRange( bbox.getDeltaLatDegrees() );

        Point startPixel = LatLongToPixelXY( bbox.getMaxLatitude().degrees, bbox.getMinLongitude().degrees, level );
        Point endPixel = LatLongToPixelXY( bbox.getMinLatitude().degrees, bbox.getMaxLongitude().degrees, level );

        Point startTile = PixelXYToTileXY( startPixel.x, startPixel.y );
        Point endTile = PixelXYToTileXY( endPixel.x, endPixel.y );

        ArrayList<VirtualEarthTile> tileList = new ArrayList<VirtualEarthTile>();

        for (int y = startTile.y ; y <= endTile.y; y++ )
        {
            for (int x = startTile.x; x <= endTile.x; x++ )
            {
                try
                {
                    int ulPixelX = x * VE_MAX_TILE_SIZE;
                    int ulPixelY = y * VE_MAX_TILE_SIZE;
                    LatLon ul = PixelXYToLatLong( ulPixelX, ulPixelY, level );

                    int lrPixelX = ulPixelX + VE_MAX_TILE_SIZE;
                    int lrPixelY = ulPixelY + VE_MAX_TILE_SIZE;
                    LatLon lr = PixelXYToLatLong( lrPixelX, lrPixelY, level );

                    Sector tileSector = Sector.boundingSector( ul, lr );

                    tileList.add( new VirtualEarthTile( x, y, level, layer, tileSector ) );
                }
                catch (Exception ex)
                {
                    Logging.logger().log( Level.SEVERE, ex.getMessage(), ex );
                }
            }
        }

        VirtualEarthTile[] tiles = new VirtualEarthTile[ tileList.size() ];
        return tileList.toArray( tiles );
    }
}
