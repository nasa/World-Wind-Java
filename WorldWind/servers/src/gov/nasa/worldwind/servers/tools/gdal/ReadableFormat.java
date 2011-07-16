/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.gdal;

/**
 * @author garakl
 * @version $Id$
 */

public class ReadableFormat extends Format
{
    public static final ReadableFormat RPFTOC       = new ReadableFormat( "RPFTOC"      ); // (ro): Raster Product Format TOC format
    public static final ReadableFormat SAR_CEOS     = new ReadableFormat( "SAR_CEOS"    ); // (ro): CEOS SAR Image
    public static final ReadableFormat CEOS         = new ReadableFormat( "CEOS"        ); // (ro): CEOS Image
    public static final ReadableFormat JAXAPALSAR   = new ReadableFormat( "JAXAPALSAR"  ); // (ro): JAXA PALSAR Product Reader (Level 1.1/1.5)
    public static final ReadableFormat GFF          = new ReadableFormat( "GFF"         ); // (ro): Ground-based SAR Applications Testbed File Format (.gff)
    public static final ReadableFormat AIG          = new ReadableFormat( "AIG"         ); // (ro): Arc/Info Binary Grid
    public static final ReadableFormat SDTS         = new ReadableFormat( "SDTS"        ); // (ro): SDTS Raster
    public static final ReadableFormat JDEM         = new ReadableFormat( "JDEM"        ); // (ro): Japanese DEM (.mem)
    public static final ReadableFormat ESAT         = new ReadableFormat( "ESAT"        ); // (ro): Envisat Image Format
    public static final ReadableFormat BSB          = new ReadableFormat( "BSB"         ); // (ro): Maptech BSB Nautical Charts
    public static final ReadableFormat DIMAP        = new ReadableFormat( "DIMAP"       ); // (ro): SPOT DIMAP
    public static final ReadableFormat AirSAR       = new ReadableFormat( "AirSAR"      ); // (ro): AirSAR Polarimetric Image
    public static final ReadableFormat RS2          = new ReadableFormat( "RS2"         ); // (ro): RadarSat 2 XML Product
    public static final ReadableFormat ISIS3        = new ReadableFormat( "ISIS3"       ); // (ro): USGS Astrogeology ISIS cube (Version 3)
    public static final ReadableFormat ISIS2        = new ReadableFormat( "ISIS2"       ); // (ro): USGS Astrogeology ISIS cube (Version 2)
    public static final ReadableFormat PDS          = new ReadableFormat( "PDS"         ); // (ro): NASA Planetary Data System
    public static final ReadableFormat L1B          = new ReadableFormat( "L1B"         ); // (ro): NOAA Polar Orbiter Level 1b Data Set
    public static final ReadableFormat GRIB         = new ReadableFormat( "GRIB"        ); // (ro): GRIdded Binary (.grb)
    public static final ReadableFormat MSGN         = new ReadableFormat( "MSGN"        ); // (ro): EUMETSAT Archive native (.nat)
    public static final ReadableFormat GS7BG        = new ReadableFormat( "GS7BG"       ); // (ro): Golden Software 7 Binary Grid (.grd)
    public static final ReadableFormat COSAR        = new ReadableFormat( "COSAR"       ); // (ro): COSAR Annotated Binary Matrix (TerraSAR-X)
    public static final ReadableFormat TSX          = new ReadableFormat( "TSX"         ); // (ro): TerraSAR-X Product
    public static final ReadableFormat COASP        = new ReadableFormat( "COASP"       ); // (ro): DRDC COASP SAR Processor Raster
    public static final ReadableFormat DOQ1         = new ReadableFormat( "DOQ1"        ); // (ro): USGS DOQ (Old Style)
    public static final ReadableFormat DOQ2         = new ReadableFormat( "DOQ2"        ); // (ro): USGS DOQ (New Style)
    public static final ReadableFormat GenBin       = new ReadableFormat( "GenBin"      ); // (ro): Generic Binary (.hdr Labelled)
    public static final ReadableFormat FujiBAS      = new ReadableFormat( "FujiBAS"     ); // (ro): Fuji BAS Scanner Image
    public static final ReadableFormat GSC          = new ReadableFormat( "GSC"         ); // (ro): GSC Geogrid
    public static final ReadableFormat FAST         = new ReadableFormat( "FAST"        ); // (ro): EOSAT FAST Format
    public static final ReadableFormat LAN          = new ReadableFormat( "LAN"         ); // (ro): Erdas .LAN/.GIS
    public static final ReadableFormat CPG          = new ReadableFormat( "CPG"         ); // (ro): Convair PolGASP
    public static final ReadableFormat NDF          = new ReadableFormat( "NDF"         ); // (ro): NLAPS Data Format
    public static final ReadableFormat EIR          = new ReadableFormat( "EIR"         ); // (ro): Erdas Imagine Raw
    public static final ReadableFormat DIPEx        = new ReadableFormat( "DIPEx"       ); // (ro): DIPEx
    public static final ReadableFormat LCP          = new ReadableFormat( "LCP"         ); // (ro): FARSITE v.4 Landscape File (.lcp)
    public static final ReadableFormat RIK          = new ReadableFormat( "RIK"         ); // (ro): Swedish Grid RIK (.rik)
    public static final ReadableFormat GXF          = new ReadableFormat( "GXF"         ); // (ro): GeoSoft Grid Exchange Format

    public ReadableFormat( String value )
    {
        super( value );
    }
}