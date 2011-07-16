/* Copyright (C) 2001, 2008 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.tools.naip;

import gov.nasa.worldwind.geom.Sector;


/**
 * @author garakl
 * @version $Id$
 */
public class NAIP
{
    private NAIP() {}

    public static final short missing_data_replacement = -32768;

    public static final double NAIP_GRID_DELTA    = 1d;

    public static final double QUADRANGLE_DELTA    = NAIP_GRID_DELTA / 8d;
    public static final double QUARTER_QUADRANGLE_DELTA  = QUADRANGLE_DELTA / 2d;


    // if a quadrange is 8, 16, 24, 32, 40, 48, 56, 64 than it is aligned with the longitude
    // that is a begining of a next UTM zone, we should NOT allow to the quarter-quadrangle to
    // have a maximum longitude aligned with the UTM zone, 
    // for example, instead of -96.0 we must adjust to -96.0000000001
    public static final double UTM_BOUNDARY_FIX  = 0.0000000001d;

    public enum QuarterQuadrangle
    {
        NW, NE, SW, SE;

        public static QuarterQuadrangle fromString( String s )
        {
            for(QuarterQuadrangle qq : QuarterQuadrangle.values())
            {
                if(qq.toString().equalsIgnoreCase(s))
                    return qq;
            }
            throw new IllegalArgumentException(s);
        }
    }

    public static Sector getSectorOfGridCell( int iLat, int iLon )
    {
        return Sector.fromDegrees( iLat, iLat + NAIP_GRID_DELTA, iLon - NAIP_GRID_DELTA, iLon );
    }


    public static Sector getSectorOfQuarterQuadrangle( int iLat, int iLon, int quadrangleNum, QuarterQuadrangle qq )
    {
        double q_lat = ((double)iLat + NAIP_GRID_DELTA) - (double)(((quadrangleNum - 1 ) / 8)) * QUADRANGLE_DELTA;
        double q_lon = ((double)iLon - NAIP_GRID_DELTA) + (double)(((quadrangleNum - 1 ) % 8)) * QUADRANGLE_DELTA;

        // check each quarter-quadrangle now SE, SE, NE, NW
        // |NW|NE|
        // |SW|SE|
        if( qq == QuarterQuadrangle.NW )
        {
//            fromDegrees(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude)
            return Sector.fromDegrees( q_lat - QUARTER_QUADRANGLE_DELTA, q_lat,
                    q_lon, q_lon + QUARTER_QUADRANGLE_DELTA );
        }
        else if( qq == QuarterQuadrangle.SW )
        {
            return Sector.fromDegrees( q_lat - QUADRANGLE_DELTA, q_lat - QUARTER_QUADRANGLE_DELTA,
                    q_lon, q_lon + QUARTER_QUADRANGLE_DELTA );
        }
        else if( qq == QuarterQuadrangle.NE )
        {
            return Sector.fromDegrees( q_lat - QUARTER_QUADRANGLE_DELTA, q_lat,
                    q_lon + QUARTER_QUADRANGLE_DELTA, q_lon + QUADRANGLE_DELTA - UTM_BOUNDARY_FIX );
        }
        else if( qq == QuarterQuadrangle.SE )
        {
            return Sector.fromDegrees( q_lat - QUADRANGLE_DELTA, q_lat - QUARTER_QUADRANGLE_DELTA,
                    q_lon + QUARTER_QUADRANGLE_DELTA, q_lon + QUADRANGLE_DELTA - UTM_BOUNDARY_FIX );
        }

        throw new IllegalArgumentException( qq.toString() );
    }

    public static Sector getSectorOfQuadrangle( int iLat, int iLon, int quadrangleNum )
    {
        double q_lat = ((double)iLat + NAIP_GRID_DELTA) - (double)(((quadrangleNum - 1 ) / 8)) * QUADRANGLE_DELTA;
        double q_lon = ((double)iLon - NAIP_GRID_DELTA) + (double)(((quadrangleNum - 1 ) % 8)) * QUADRANGLE_DELTA;

        return Sector.fromDegrees( q_lat - QUADRANGLE_DELTA, q_lat, q_lon, q_lon + QUADRANGLE_DELTA );
    }

}
