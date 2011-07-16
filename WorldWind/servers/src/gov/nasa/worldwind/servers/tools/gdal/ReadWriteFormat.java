/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.gdal;

/**
 * @author garakl
 * @version $Id$
 */

public class ReadWriteFormat extends ReadableFormat
{
    public static final ReadWriteFormat VRT      = new ReadWriteFormat( "VRT" );        // (rw+): Virtual Raster
    public static final ReadWriteFormat GTiff    = new ReadWriteFormat( "GTiff" );      // (rw+): GeoTIFF
    public static final ReadWriteFormat NITF     = new ReadWriteFormat( "NITF" );       // (rw+): National Imagery Transmission Format
    public static final ReadWriteFormat HFA      = new ReadWriteFormat( "HFA" );        // (rw+): Erdas Imagine Images (.img)
    public static final ReadWriteFormat ELAS     = new ReadWriteFormat( "ELAS" );       // (rw+): ELAS
    public static final ReadWriteFormat AAIGrid  = new ReadWriteFormat( "AAIGrid" );    // (rw): Arc/Info ASCII Grid
    public static final ReadWriteFormat DTED     = new ReadWriteFormat( "DTED" );       // (rw): DTED Elevation Raster
    public static final ReadWriteFormat PNG      = new ReadWriteFormat( "PNG" );        // (rw): Portable Network Graphics
    public static final ReadWriteFormat JPEG     = new ReadWriteFormat( "JPEG" );       // (rw): JPEG JFIF
    public static final ReadWriteFormat MEM      = new ReadWriteFormat( "MEM" );        // (rw+): In Memory Raster
    public static final ReadWriteFormat PCIDSK   = new ReadWriteFormat( "PCIDSK" );     // (rw+): PCIDSK Database File
    public static final ReadWriteFormat PCRaster = new ReadWriteFormat( "PCRaster" );   // (rw): PCRaster Raster File
    public static final ReadWriteFormat ILWIS    = new ReadWriteFormat( "ILWIS" );      // (rw+): ILWIS Raster Map
    public static final ReadWriteFormat SGI      = new ReadWriteFormat( "SGI" );        // (rw+): SGI Image File Format 1.0
    public static final ReadWriteFormat SRTMHGT  = new ReadWriteFormat( "SRTMHGT" );    // (rw): SRTMHGT File Format
    public static final ReadWriteFormat Leveller = new ReadWriteFormat( "Leveller" );   // (rw+): Leveller heightfield
    public static final ReadWriteFormat Terragen = new ReadWriteFormat( "Terragen" );   // (rw+): Terragen heightfield
    public static final ReadWriteFormat RST      = new ReadWriteFormat( "RST" );        // (rw+): Idrisi Raster A.1
    public static final ReadWriteFormat INGR     = new ReadWriteFormat( "INGR" );       // (rw+): Intergraph Raster
    public static final ReadWriteFormat GSAG     = new ReadWriteFormat( "GSAG" );       // (rw): Golden Software ASCII Grid (.grd)
    public static final ReadWriteFormat GSBG     = new ReadWriteFormat( "GSBG" );       // (rw+): Golden Software Binary Grid (.grd)
    public static final ReadWriteFormat PAux     = new ReadWriteFormat( "PAux" );       // (rw+): PCI .aux Labelled
    public static final ReadWriteFormat MFF      = new ReadWriteFormat( "MFF" );        // (rw+): Vexcel MFF Raster
    public static final ReadWriteFormat MFF2     = new ReadWriteFormat( "MFF2" );       // (rw+): Vexcel MFF2 (HKV) Raster
    public static final ReadWriteFormat ENVI     = new ReadWriteFormat( "ENVI" );       // (rw+): ENVI .hdr Labelled
    public static final ReadWriteFormat EHdr     = new ReadWriteFormat( "EHdr" );       // (rw+): ESRI .hdr Labelled
    public static final ReadWriteFormat GIF      = new ReadWriteFormat( "GIF" );        // (rw): Graphics Interchange Format (.gif)
    public static final ReadWriteFormat XPM      = new ReadWriteFormat( "XPM" );        // (rw): X11 PixMap Format
    public static final ReadWriteFormat BMP      = new ReadWriteFormat( "BMP" );        // (rw+): MS Windows Device Independent Bitmap
    public static final ReadWriteFormat ERS      = new ReadWriteFormat( "ERS" );        // (rw+): ERMapper .ers Labelled
    public static final ReadWriteFormat FIT      = new ReadWriteFormat( "FIT" );        // (rw): FIT Image
    public static final ReadWriteFormat RMF      = new ReadWriteFormat( "RMF" );        // (rw+): Raster Matrix Format
    public static final ReadWriteFormat PNM      = new ReadWriteFormat( "PNM" );        // (rw+): Portable Pixmap Format (netpbm)
    public static final ReadWriteFormat BT       = new ReadWriteFormat( "BT" );         // (rw+): VTP .bt (Binary Terrain) 1.3 Format
    public static final ReadWriteFormat IDA      = new ReadWriteFormat( "IDA" );        // (rw+): Image Data and Analysis
    public static final ReadWriteFormat USGSDEM  = new ReadWriteFormat( "USGSDEM" );    // (rw): USGS Optional ASCII DEM (and CDED)
    public static final ReadWriteFormat ADRG     = new ReadWriteFormat( "ADRG" );       // (rw+): ARC Digitized Raster Graphics
    public static final ReadWriteFormat BLX      = new ReadWriteFormat( "BLX" );        // (rw): Magellan topo (.blx)

    public ReadWriteFormat( String value )
    {
        super( value );
    }
}