/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.utm;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.globes.EllipsoidalGlobe;
import gov.nasa.worldwind.globes.Earth;
/**
 * @author garakl
 * @version $Id$
 */
class LatLon2Utm extends UTMConverter
{
    private static final double sin1 = 4.84814E-06;

    // scale factor
    private static final double k0 = 0.9996;

    // Meridional Arc

    //  public static final double S = 5103266.421;
    private static final double A0 = 6367449.146d;
    private static final double B0 = 16038.42955d;
    private static final double C0 = 16.83261333d;
    private static final double D0 = 0.021984404d;
    private static final double E0 = 0.000312705d;

    // eccentricity
    public static final double e = Math.sqrt(1 - POW(Earth.WGS84_POLAR_RADIUS / Earth.WGS84_EQUATORIAL_RADIUS, 2));
    public static final double e1sq = e * e / (1 - e * e);

    
    public static UTMCoords convert (LatLon latlon)
    {
        double latitude = latlon.getLatitude().radians;
        double longitude = latlon.getLongitude().degrees;

        // Lat Lon to UTM variables

        double nu = Earth.WGS84_EQUATORIAL_RADIUS / POW(1 - POW(e * SIN(latitude), 2), (1 / 2.0));
        Short lonZone =  (short)UTMConverter.getLongitudeZone( latlon );

        double var1 = (double)lonZone;
//
//        if (longitude < 0.0)
//        {
//            var1 = ((int) ((180 + longitude) / 6.0)) + 1;
//        }
//        else
//        {
//            var1 = ((int) (longitude / 6)) + 31;
//        }
//
//        if( var1 == 33 )
//        {
//            char band = UTMConverter.getLatitudeBandFromDegrees( latlon.getLatitude().degrees );
//            if( band == 'U' || band == 'V' )
//                var1 = 32;
//        }

        double var2 = (6 * var1) - 183;

        double var3 = longitude - var2;

        double p = var3 * 3600 / 10000;

        double S = A0 * latitude - B0 * SIN(2 * latitude) + C0 * SIN(4 * latitude) - D0
                * SIN(6 * latitude) + E0 * SIN(8 * latitude);

        double K1 = S * k0;

        double K2 = nu * SIN(latitude) * COS(latitude) * POW(sin1, 2) * k0 * (100000000) / 2;

        double K3 = ((POW(sin1, 4) * nu * SIN(latitude) * Math.pow(COS(latitude), 3)) / 24)
                * (5 - POW(TAN(latitude), 2) + 9 * e1sq * POW(COS(latitude), 2) + 4
                * POW(e1sq, 2) * POW(COS(latitude), 4))
                * k0
                * (10000000000000000L);

        double K4 = nu * COS(latitude) * sin1 * k0 * 10000;

        double K5 = POW(sin1 * COS(latitude), 3) * (nu / 6)
                * (1 - POW(TAN(latitude), 2) + e1sq * POW(COS(latitude), 2)) * k0
                * 1000000000000L;

        // Note! switching from radians to degrees
        latitude = latlon.getLatitude().degrees;

//        Short lonZone =  (short)UTMConverter.getLongitudeZone( latlon );

        double easting = 500000 + (K4 * p + K5 * POW(p, 3));

        double northing = K1 + K2 * p * p + K3 * POW(p, 4);

        northing += ( latitude < 0d  ) ? 10000000d : 0d ;

        return new UTMCoords( UTMConverter.getLatitudeBandFromDegrees( latitude ), lonZone, easting, northing );

    }
}
