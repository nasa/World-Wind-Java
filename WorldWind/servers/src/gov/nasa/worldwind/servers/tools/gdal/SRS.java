/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.gdal;

/**
 * @author garakl
 * @version $Id$
 */

public class SRS extends Option
{
    // These common geographic (lat/long) coordinate systems can be used directly by these names
    public static final SRS NAD_27 = new SRS( "NAD27" );
    public static final SRS NAD_83 = new SRS( "NAD83" );
    public static final SRS WGS_84 = new SRS( "WGS84" );
    public static final SRS WGS_72 = new SRS( "WGS72" );

    // Coordinate systems (projected or geographic) can be selected based on their EPSG codes,
    // for instance EPSG:27700 is the British National Grid.
    // A list of EPSG coordinate systems can be found in the GDAL data files gcs.csv and pcs.csv

    public static final SRS EPSG_4326 = new SRS( "EPSG:4326" );
    public static final SRS EPSG_7030 = new SRS( "EPSG:7030" );
    public static final SRS EPSG_6326 = new SRS( "EPSG:6326" );

    public SRS( String value )
    {
        super( null, value );
    }

    public SRS( String proj, int zone, String datum )
    {
        super( null, "+proj=" + proj + " +zone=" + zone + " +datum=" + datum );
    }

}