/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.utm;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;

/**
 * @author garakl
 * @version $Id$
 */
public class UTMSector
{
    private int zone = 0;
    private double minEasting =0d, maxEasting = 0d, minNorthing = 0d, maxNorthing = 0d;

    public int getZone()
    {
        return zone;
    }

    public double getMinEasting()
    {
        return minEasting;
    }

    public double getMaxEasting()
    {
        return maxEasting;
    }

    public double getMinNorthing()
    {
        return minNorthing;
    }

    public double getMaxNorthing()
    {
        return maxNorthing;
    }

    public UTMSector(double minEasting, double maxEasting, double minNorthing, double maxNorthing, int zone )
    {
        this.minEasting = minEasting;
        this.maxEasting = maxEasting;
        this.minNorthing = minNorthing;
        this.maxNorthing = maxNorthing;
        this.zone = zone;
    }

//    public void adjust (double offset_widht, double offset_height )
//    {
//        this.minEasting  += offset_widht  ;
//        this.maxEasting  += offset_widht  ;
//        this.minNorthing += offset_height ;
//        this.maxNorthing += offset_height ;
//    }

    public static UTMSector fromSector( Sector s) throws IllegalArgumentException
    {
        if( null == s )
            return null;

        double minLat = s.getMinLatitude().degrees;
        double minLon = s.getMinLongitude().degrees;
        double maxLat = s.getMaxLatitude().degrees;
        double maxLon = s.getMaxLongitude().degrees;

        UTMCoords utm_ul = UTMCoords.fromLatLon( LatLon.fromDegrees( maxLat, minLon ) );
        UTMCoords utm_ll = UTMCoords.fromLatLon( LatLon.fromDegrees( minLat, minLon ) );
        UTMCoords utm_ur = UTMCoords.fromLatLon( LatLon.fromDegrees( maxLat, maxLon ) );
        UTMCoords utm_lr = UTMCoords.fromLatLon( LatLon.fromDegrees( minLat, maxLon ) );

        int zone = utm_ul.getZone();
        if(zone != utm_ll.getZone() || zone != utm_ur.getZone() || zone != utm_lr.getZone() )
        {
            String msg = Logging.getMessage("UTM.MixedZonesDetected");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);

        }
        return new UTMSector( Math.min( utm_ul.getEasting(), utm_ll.getEasting()),
                              Math.max( utm_ur.getEasting(), utm_lr.getEasting()),
                              Math.min( utm_ll.getNorthing(), utm_lr.getNorthing()),
                              Math.max( utm_ul.getNorthing(), utm_ur.getNorthing()),
                              zone );
    }


//    public LatLon getUpperLeftCoordinate()
//    {
//        // TODO calc hemisphere
//        return Utm2LatLon.convert( UTMCoords.Hemisphere.NORTH, (short)this.getZone(),
//                this.getMinEasting() , this.getMaxNorthing() );
//    }
//
//    public LatLon getUpperRightCoordinate()
//    {
//        // TODO calc hemisphere
//        return Utm2LatLon.convert( UTMCoords.Hemisphere.NORTH, (short)this.getZone(),
//                this.getMaxEasting() , this.getMaxNorthing() );
//    }
//
//    public LatLon getLowerRightCoordinate()
//    {
//        // TODO calc hemisphere
//        return Utm2LatLon.convert( UTMCoords.Hemisphere.NORTH, (short)this.getZone(),
//                this.getMaxEasting() , this.getMinNorthing() );
//    }
//
//    public LatLon getLowerLeftCoordinate()
//    {
//        // TODO calc hemisphere
//        return Utm2LatLon.convert( UTMCoords.Hemisphere.NORTH, (short)this.getZone(),
//                this.getMinEasting() , this.getMinNorthing() );
//    }

    public UTMSector clone()
    {
        return new UTMSector( this.getMinEasting(), this.getMaxEasting(),
                this.getMinNorthing(),
                this.getMaxNorthing(), this.getZone());

    }

    public final UTMSector intersection( UTMSector that)
    {
        if (that == null)
            return this;

        if( this.getZone() != that.getZone() )
            return null;

        double miny = (this.getMinNorthing() > that.getMinNorthing()) ? this.getMinNorthing() : that.getMinNorthing();
        double maxy = (this.getMaxNorthing() < that.getMaxNorthing()) ? this.getMaxNorthing() : that.getMaxNorthing();
        if( miny > maxy )
            return null;

        double minx = (this.getMinEasting() > that.getMinEasting()) ? this.getMinEasting(): that.getMinEasting();
        double maxx = (this.getMaxEasting() < that.getMaxEasting()) ? this.getMaxEasting(): that.getMaxEasting();
        if (minx > maxx )
            return null;

        return new UTMSector(minx, maxx, miny, maxy, this.getZone() );
    }
}
