/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.utm;
import gov.nasa.worldwind.geom.LatLon;
/**
 * @author garakl
 * @version $Id$
 */
public class UTMCoords {

    public static enum Hemisphere { NORTH, SOUTH }

    private Short lonZone = 0;
    private double easting = 0d;
    private double northing = 0d;
    private Hemisphere hemisphere = Hemisphere.NORTH ;

    public UTMCoords(char latBand, short lonZone, double easting, double northing)
    {
        this.hemisphere = UTMConverter.getHemisphere( latBand );
        this.easting = easting;
        this.northing = northing;
        this.lonZone = lonZone;
    }


    public UTMCoords( Hemisphere hemisphere, short lonZone, double easting, double northing)
    {
        this.easting = easting;
        this.northing = northing;
        this.lonZone = lonZone;
        this.hemisphere = hemisphere;
    }

    public static UTMCoords fromLatLon(LatLon latlon)
    {
        return LatLon2Utm.convert(latlon);
    }

    public int getZone()
    {
        return (int)this.lonZone;
    }

    public double getEasting()
    {
        return this.easting;
    }

    public double getNorthing()
    {
        return this.northing;
    }

    public LatLon getLatLon()
    {

        return Utm2LatLon.convert( this.hemisphere, this.lonZone, this.easting, this.northing );
    }

    public void shift( double easting, double northing )
    {
        this.easting += easting;
        this.northing += northing;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer("UTM Coords { ");
        sb.append( "hemisphere =").append(this.hemisphere);
        sb.append( ", longitudeZone=").append(this.lonZone);
        sb.append( ", easting=").append(this.easting);
        sb.append( ", northing=").append(this.northing).append(" }");
        return sb.toString();
    }

    public static void main(String[] args)
    {
        // -122.9677734375 46.0458984375 -122.958984375 46.0546875

        // -11.6670083888889,55.9195647222222,-11.6621791666667,55.9229644444444

        LatLon ll_upl = LatLon.fromDegrees( 55.9229644444444, 11.6670083888889 );
        LatLon ll_lol = LatLon.fromDegrees( 55.9195647222222, 11.6670083888889 );

        LatLon ll_upr = LatLon.fromDegrees( 55.9229644444444, 11.6621791666667 );
        LatLon ll_lor = LatLon.fromDegrees( 55.9195647222222, 11.6621791666667 );

        UTMCoords utm_upl = UTMCoords.fromLatLon( ll_upl );
        UTMCoords utm_lol = UTMCoords.fromLatLon( ll_lol );
        UTMCoords utm_upr = UTMCoords.fromLatLon( ll_upr );
        UTMCoords utm_lor = UTMCoords.fromLatLon( ll_lor );


        System.out.println( "upper left  =" + utm_upl.toString() );
        System.out.println( "lower left  =" + utm_lol.toString() );
        System.out.println( "upper right =" + utm_upr.toString() );
        System.out.println( "lower right =" + utm_lor.toString() );

        System.out.print( UTMCoords.Hemisphere.NORTH.toString());
    }
}
