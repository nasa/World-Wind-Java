/* Copyright (C) 2001, 2009 United States Government as represented by

   the Administrator of the National Aeronautics and Space Administration.

   All Rights Reserved.

 */

package gov.nasa.worldwind.servers.tools.utm;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.util.Logging;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


/**

 * @author garakl

 * @version $Id$

 */

class UTMConverter
{
    protected UTMConverter() {}

    protected static double POW(double a, double b)
    {
        return Math.pow(a, b);
    }

    protected static double SIN(double value)
    {
        return Math.sin(value);
    }

    protected static double COS(double value)
    {
        return Math.cos(value);
    }

    protected static double TAN(double value)
    {
        return Math.tan(value);
    }


    public static UTMCoords.Hemisphere getHemisphere( char latBand )
    {
        if( "ACDEFGHJKLM".indexOf(latBand) > -1 )
            return UTMCoords.Hemisphere.SOUTH;
        else if( "NPQRSTUVWXZ".indexOf(latBand) > -1 )
            return UTMCoords.Hemisphere.NORTH;

        String msg = Logging.getMessage("UTM.InvalidLatitudeBand", latBand );
        Logging.logger().severe(msg);
        throw new IllegalArgumentException(msg);
    }

    public static char getLatitudeZone(LatLon ll)
    {
        if (isNorthPolar(ll))
            return (ll.getLongitude().radians < 0) ? 'Y' : 'Z';

        if (isSouthPolar(ll))
            return (ll.getLongitude().radians < 0) ? 'A' : 'B';

        final int degreesLatitude = (int) ll.getLatitude().degrees;
        char zone = (char) ((degreesLatitude + 80) / 8 + 'C');
        if (zone > 'H') {
            zone++;
        }
        if (zone > 'N') {
            zone++;
        }
        if (zone > 'X') {
            zone = 'X';
        }
        return zone;
    }

    public static boolean isNorthPolar(LatLon ll)
    {
        return ll.getLatitude().degrees > 84d;
    }

    public static boolean isSouthPolar(LatLon ll)
    {
        return ll.getLatitude().degrees < -80d;
    }


    protected static int original_getLongitudeZone(LatLon ll)
    {
        final double degreesLongitude = ll.getLongitude().degrees;

        // UPS longitude zones
        if (isNorthPolar(ll) || isSouthPolar(ll)) {
            return (degreesLongitude < 0.0) ? 30 : 31 ;
        }

        final char latitudeZone = getLatitudeZone(ll);
        // X latitude exceptions
        if (latitudeZone == 'X' && degreesLongitude > 0d && degreesLongitude < 42d)
        {
            if (degreesLongitude < 9.0)
                return 31;
            else if (degreesLongitude < 21d)
                return 33;
            else if (degreesLongitude < 33d)
                return 35;
            else
                return 37;
        }
        // V latitude exceptions
        if ( latitudeZone == 'V' && degreesLongitude > 0d && degreesLongitude < 12d)
        {
            return (degreesLongitude < 3d) ? 31 : 32 ;
        }

        return (int) ((degreesLongitude + 180) / 6) + 1;
    }


    public static int getLongitudeZone(LatLon ll) 
    {
        int zone = original_getLongitudeZone( ll );

        if( zone == 33 )
        {
            char band = UTMConverter.getLatitudeBandFromDegrees( ll.getLatitude().degrees );
            if( band == 'U' || band == 'V' )
                zone = 32;
        }

        return zone;
    }


    public static String getLongitudeZoneAsString(LatLon ll)
    {
        String val = String.valueOf( getLongitudeZone( ll ) );
        if (val.length() == 1)
        {
            val = "0" + val;
        }
        return val;
    }


    public static double getLatitudeInDegrees(char utmLatitudeBand)
    {
        if( "ACDEFGHJKLMNPQRSTUVWXZ".indexOf(utmLatitudeBand) > -1 )
        {
            switch( utmLatitudeBand )
            {
                case 'A': return -90d;
                case 'C': return -84d;
                case 'D': return -72d;
                case 'E': return -64d;
                case 'F': return -56d;
                case 'G': return -48d;
                case 'H': return -40d;
                case 'J': return -32d;
                case 'K': return -24d;
                case 'L': return -16d;
                case 'M': return  -8d;
                case 'N': return   0d;
                case 'P': return   8d;
                case 'Q': return  16d;
                case 'R': return  24d;
                case 'S': return  32d;
                case 'T': return  40d;
                case 'U': return  48d;
                case 'V': return  56d;
                case 'W': return  64d;
                case 'X': return  72d;
                case 'Z': return  84d;
            }
        }

       String msg = Logging.getMessage("UTM.InvalidLatitudeBand", utmLatitudeBand );
       Logging.logger().severe(msg);
       throw new IllegalArgumentException(msg);
    }

    public static double getLatitudeInDegrees(String latBand)
    {
        if( null == latBand || latBand.length() != 1 )
        {
            String msg = Logging.getMessage("UTM.InvalidLatitudeBand", latBand );
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return UTMConverter.getLatitudeInDegrees( latBand.charAt(0));
    }


    public static char getLatitudeBandFromDegrees(double latitude)
    {
        CharacterIterator bands = new StringCharacterIterator("ACDEFGHJKLMNPQRSTUVWXZ");

        char curr_band = bands.first();

        char saved_band = curr_band;

        double minLat = UTMConverter.getLatitudeInDegrees( curr_band );

        while( (curr_band = bands.next()) != CharacterIterator.DONE )
        {
            double maxLat = UTMConverter.getLatitudeInDegrees( curr_band );
            if( latitude >= minLat && latitude < maxLat )
            {
                break;
            }
            minLat = maxLat;
            saved_band = curr_band;
        }
        
        return saved_band;
    }

    public static double getCentralMeridian( int longitudeZone, char latitudeZone)
    {
        // polar zones
        if (latitudeZone < 'C' || latitudeZone > 'X')
        {
            return 0.0;
        }
        // X latitude zone exceptions
        if (latitudeZone == 'X' && longitudeZone > 31 && longitudeZone <= 37)
        {
            return Math.toRadians((longitudeZone - 1) * 6 - 180 + 4.5);
        }
        // V latitude zone exceptions
        if (longitudeZone == 'V')
        {
            if (latitudeZone == 31)
                return Math.toRadians(1.5);
            else if (latitudeZone == 32)
                return Math.toRadians(7.5);
        }
        return Math.toRadians((longitudeZone - 1) * 6 - 180 + 3);
    }


}
