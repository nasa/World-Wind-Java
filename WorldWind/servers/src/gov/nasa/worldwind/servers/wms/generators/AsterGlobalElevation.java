/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.servers.app.WMSServerApplication;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.servers.wms.formats.DataRasterFormatter;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.data.ByteBufferRaster;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;

/**
 * @author garakl
 * @version $Id$
 * @version $Id$
 */

/*
    NASA and MITI (Japan) ASTER 30 meter elevation data

    Tile structure and file naming convention:

    Each tile is 1 degree by 1 degree in Geogdetic  coordinate system,
    projection is WGS84. Every tile is 3,601 x 3,601 and 16-bit integer data.
    NODATA is 0, at least in the ocean areas

    Tiles are in the Geo-Tiff format, Int16 gray.
    Total tiles =  22,602,
    tile size is 3,601 x 3,6001 x 2 = 25MB

    File naming : ASTGTM_{S|N}YY{W|E}XXX_dem.tif

    Example: ASTGTM_S40W095_dem.tif, ASTGTM_N05E121_dem.tif

    Tile origin is a lower left corner

 */
public class AsterGlobalElevation extends AbstractElevationGenerator
{
    private Sector boundingSector = Sector.FULL_SPHERE;

    private static final String[] CRS = {"EPSG:4326"};
    private static final short ASTER_MISSING_DATA_FLAG = -9999;

    protected static final double ASTER_MAX_RESOLUTION = 1d / 3600d;

    private static final String PROPERTY_FILENAMING_FORMAT = "filenaming_format";
    public  static String DEFAULT_FILENAMING_FORMAT = "%s%sASTGTM_%c%02d%c%03d_dem.tif";
    private String filenaming_format = DEFAULT_FILENAMING_FORMAT;

    protected File rootDir = null;

    private static final String PROPERTY_FALLBACK_LAYER_NAME = "gov.nasa.worldwind.avkey.FallbackLayerName";
    private static final String PROPERTY_FALLBACK_CONDITION = "gov.nasa.worldwind.avkey.FallbackCondition";

    protected static String fallbackLayerName = "srtm3";
    protected static double fallbackCondition = 2d;

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        this.setThreadId( "ASTER_30m" );
        boolean success = false;

        try
        {
            if( null == mapSource )
            {
                String msg = Logging.getMessage( "nullValue.SourceIsNull" );
                Logging.logger().severe(msg);
                throw new IllegalArgumentException( msg );
            }

            this.rootDir = new File(mapSource.getRootDir());
            if( !this.rootDir.exists())
            {
                String msg = Logging.getMessage( "generic.FolderDoesNotExist", mapSource.getRootDir() );
                Logging.logger().severe(msg);
                throw new FileNotFoundException( msg );
            }
            else
                Logging.logger().finest( this.getThreadId() + "data directory set to " + this.rootDir.getAbsolutePath());

            Properties props = mapSource.getProperties();
            if (props == null)
            {
                String msg = Logging.getMessage( "nullValue.PropertyNameIsNull" );
                Logging.logger().severe(msg);
                throw new IllegalArgumentException( msg );

            }

            fallbackLayerName = props.getProperty(PROPERTY_FALLBACK_LAYER_NAME, fallbackLayerName );
            if( null != fallbackLayerName && 0 < fallbackLayerName.trim().length() )
            {
                String s = props.getProperty( PROPERTY_FALLBACK_CONDITION, Double.toString(fallbackCondition) );
                try
                {
                    fallbackCondition = Double.parseDouble(s);
                }
                catch(Exception ignore) {}
            }

            success = true;

            Logging.logger().fine( this.getThreadId() + " layer started OK. Coverage: " + this.boundingSector.toString());
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage( "WMS.MapGenerator.CannotInstantiate", ex.getMessage() );
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
            success = false;
        }

        return success;
    }

    public ServiceInstance getServiceInstance()
    {
        return new Aster30mServiceInstance();
    }

    public Sector getBBox()
    {
        return this.boundingSector;
    }

    public boolean hasCoverage(Sector sector)
    {
        return (null != sector && sector.intersects( this.getBBox() ));
    }

    public double getPixelSize()
    {
        double pixelSize = this.mapSource.getScaleHintMax();
        return ( pixelSize != 0d ) ? pixelSize : ASTER_MAX_RESOLUTION;
    }


    public String[] getCRS()
    {
        // TODO check projection for EPSG 4326 OR EPSG 4329
        // check with gdal if reprojection is requerd
        // this could slow down performance of the WMS server
        return CRS;
    }

    public class Aster30mServiceInstance extends AbstractServiceInstance
    {
        private String threadID = "ASTER_30m";

        public String getThreadID()
        {
            return this.threadID;
        }

        public Aster30mServiceInstance()
        {
            super();
            this.threadID = new StringBuffer("ASTER_30m").append(" (").append(Thread.currentThread().getId()).append("): ").toString();
        }

        public ImageFormatter serviceRequest( IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster raster = this.doServiceRequest( req );
            if( null == raster )
                raster = Mosaicer.createCompatibleDataRaster( req );
            return new DataRasterFormatter( raster );
        }

        private DataRaster doServiceRequest( IMapRequest req ) throws IOException, WMSServiceException
        {
            DataRaster raster = null;

            long begTime = System.currentTimeMillis();

            ArrayList<File> tiles = new ArrayList<File>();

            StringBuilder source = new StringBuilder( 2048 );
            Formatter formatter = new Formatter(source, Locale.US);

            Logging.logger().finest( this.getThreadID() + "processing service request ...");

            try
            {
                // Determine which NED files overlap the request...
                // Sector reqSector = Sector.fromDegrees(req.getBBoxYMin(), req.getBBoxYMax(), req.getBBoxXMin(), req.getBBoxXMax());
                Sector reqSector = req.getExtentForElevationRequest();

                if( null == reqSector )
                {
                    String msg = Logging.getMessage( "nullValue.SectorIsNull" );
                    Logging.logger().severe( this.getThreadID() + msg );
                    throw new WMSServiceException( msg );
                }

                double reqPixelSize = reqSector.getDeltaLatDegrees() / ((double)req.getHeight());
                if ( reqPixelSize > 2d/150d ) // performace protection, do not open more than 9 tiles
                {
                    String msg = Logging.getMessage( "WMS.Layer.TheRequestedAreaIsTooBig" );
                    Logging.logger().severe( this.getThreadID() + msg );
                    throw new WMSServiceException( msg );
                }

                int iLonBeg = (int)( reqSector.getMinLongitude().degrees - 1d );
                int iLonEnd = (int)( reqSector.getMaxLongitude().degrees + 1d );
                int iLatBeg = (int)( reqSector.getMinLatitude().degrees  - 1d );
                int iLatEnd = (int)( reqSector.getMaxLatitude().degrees  + 1d );

                search:
                {
                    for (int iLon = iLonBeg; iLon <= iLonEnd; iLon++)
                    {
                        for (int iLat = iLatBeg; iLat <= iLatEnd; iLat++)
                        {
                            double lat = (double)iLat;
                            double lon = (double)iLon;

                            Sector tileSector = Sector.fromDegrees( lat, lat+1, lon, lon + 1d );
                            Sector overlap = reqSector.intersection(tileSector);

                            // Skip if tiles do not intersect or intersect exactly at an edge (delta == 0)...
                            if (   null != overlap
                                && 0d < Math.abs(overlap.getDeltaLon().degrees)
                                && 0d < Math.abs(overlap.getDeltaLat().degrees)
                               )
                            {
                                // compute name of ASTER tile...
                                // "%s%sASTGTM_%c%02d%c%03d_dem.tif"
                                source.setLength(0);
                                formatter.format( AsterGlobalElevation.this.filenaming_format,
                                    rootDir.getAbsolutePath(),
                                    File.separator,
                                    (iLat >= 0 ) ? 'N' : 'S',
                                    Math.abs(iLat),
                                    (iLon >= 0 ) ? 'E' : 'W',
                                    Math.abs(iLon)
                                );


                                File sourceFile = new File(source.toString());
                                if (!sourceFile.exists())
                                {
                                    Logging.logger().severe( this.getThreadID()
                                            + source.toString() + " does NOT exists in " + rootDir.getAbsolutePath());
                                    continue;
                                }

                                // check if fully contains (remove all others and leave the loop)
                                if( tileSector.contains(reqSector))
                                {
                                    tiles.clear();
                                    tiles.add( sourceFile );
                                    break search;
                                }
                                else
                                {
                                    tiles.add( sourceFile );
                                }
                            }
                        }
                    }
                }

                if( tiles.size() > 0 )
                {
                    File[] sourceFiles = new File[ tiles.size() ];
                    sourceFiles = (File[])(tiles.toArray( sourceFiles ));

                    raster = Mosaicer.mosaicElevations( this.getThreadID(),
                            sourceFiles, reqSector,
                            req.getWidth(), req.getHeight(),
                            AsterGlobalElevation.ASTER_MISSING_DATA_FLAG,
                            req.getBGColorAsDouble().shortValue(),
                            ( "application/bil32".equals(req.getFormat()))  ? "Float32" : "Int16",
                            Option.Warp.Resampling.Cubic
                    );
                    this.checkAndFixAsterElevations( req, raster );
                }

                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report( "ASTER.30m", tiles.size(), ellapsed );
                Logging.logger().finest( this.getThreadID() + "DONE "
                        + tiles.size() + " tiles in "
                        + ellapsed + " msec. " + Stats.getStats("ASTER.30m"));
            }
            catch (Exception ex)
            {
                String s = this.getThreadID() + "request failed: " + ex.toString();
                Logging.logger().severe( s );
                throw new WMSServiceException( s );
            }
            return raster;
        }

        private void checkAndFixAsterElevations( IMapRequest req, DataRaster asterRaster )
        {
            // check if Aster configuration has a name of another elevation layer to fallback, usually "srtm3"
            if(    (null == AsterGlobalElevation.fallbackLayerName)
                || (0 == AsterGlobalElevation.fallbackLayerName.trim().length()) )
            {
                return;
            }

            try
            {
                MapGenerator genSRTM3 = null; 

                WMSServerApplication app = AsterGlobalElevation.this.getApplicationContext();
                if( null == app )
                {
                    String msg = Logging.getMessage( "WMS.Server.ApplicationIsNull" );
                    Logging.logger().severe(msg);
                }
                else
                {
                    // TODO zz: garakl: either read dependant MapSource by name/type
                    // or iterate through existing ones to find the instance of ElevationSrtm3V4 
                    genSRTM3 = app.getMapSourceRegistry().get("srtm3").getMapGenerator();
                }

                if (null != genSRTM3 && genSRTM3 instanceof ElevationSrtm3V4 )
                {
                    MinMax asterExtreme = this.findMinMaxValues( asterRaster );
                    if( asterExtreme.min < asterExtreme.max  && asterExtreme.max > 100d )
                    {
                        ServiceInstance svc = genSRTM3.getServiceInstance();
                        DataRaster rasterSRTM3 = ((ElevationSrtm3V4.Srtm3ServiceInstance) svc).doServiceRequest(req);

                        MinMax srtmExtreme = this.findMinMaxValues( rasterSRTM3 );

                        if(    srtmExtreme.max > asterExtreme.max
                            && (srtmExtreme.max - asterExtreme.max) > AsterGlobalElevation.fallbackCondition
                          )
                        {
                            this.doFixAsterElevations( asterRaster, rasterSRTM3 );
                            Logging.logger().fine( "*** Aster: " + asterExtreme + " ----> SRTM3: " + srtmExtreme );
                        }
                    }
                }
                else
                {
                    String msg = Logging.getMessage( "WMS.MapGeneratorIsNull" );
                    Logging.logger().severe(msg);
                }
            }
            catch (Exception unknownException)
            {
                Logging.logger().log(Level.SEVERE, unknownException.getMessage(), unknownException );
            }
        }

        private void doFixAsterElevations( DataRaster asterRaster, DataRaster srtmRaster )
        {
            if (null == asterRaster || null == srtmRaster)
            {
                String msg = Logging.getMessage("nullValue.RasterIsNull");
                Logging.logger().severe(msg);
                // throw new IOException( Logging.getMessage("WMS.Server.InternalError", msg ));
                return;
            }

            int width = asterRaster.getWidth();
            int height = asterRaster.getHeight();

            if (width != srtmRaster.getWidth() || height != srtmRaster.getHeight())
            {
                String msg =
                    Logging.getMessage("generic.InvalidImageSize", asterRaster.getWidth(), asterRaster.getHeight())
                  + "\n"
                  + Logging.getMessage("generic.InvalidImageSize", srtmRaster.getWidth(), srtmRaster.getHeight());
                Logging.logger().severe(msg);
                return;
            }

            if (!(asterRaster instanceof ByteBufferRaster) || !(srtmRaster instanceof ByteBufferRaster))
            {
                String msg = Logging.getMessage("generic.InvalidDataSource", srtmRaster.getClass().getName());
                Logging.logger().severe(msg);
                // throw new IOException( Logging.getMessage("WMS.Server.InternalError", msg ));
                return;
            }

            ByteBufferRaster bbA = (ByteBufferRaster) asterRaster;
            ByteBufferRaster bbB = (ByteBufferRaster) srtmRaster;

            double nodataA = bbA.getTransparentValue();
            double nodataB = bbB.getTransparentValue();

            for(int row = 0; row < height ; row++ )
            {
                for(int col = 0; col < width ; col++ )
                {
                    double a = bbA.getDoubleAtPosition(row, col);
                    double b = bbB.getDoubleAtPosition(row, col);
                    if( (a == nodataA && b != nodataB) || (b > a))
                        bbA.setDoubleAtPosition( row, col, b );
//                    else if( a != nodataA && b != nodataB && a != b )
//                        bbA.setDoubleAtPosition( row, col, (a + b)/2d );
                }
            }
        }

        public void freeResources()
        {
            // NO-OP
        }

        class MinMax
        {
            public double min = 0, max = 0;
            public MinMax()
            {
            }

            public String toString()
            {
                return "{ min=" + min + ", max=" + max + " }";
            }
        }

        private MinMax findMinMaxValues( DataRaster raster )
        {
            MinMax value = new MinMax();

            if( null == raster )
            {
                String msg = Logging.getMessage("nullValue.RasterIsNull");
                Logging.logger().severe(msg);
                return value;
            }

            if( !(raster instanceof ByteBufferRaster) )
            {
                String msg = Logging.getMessage("generic.InvalidDataSource", raster.getClass().getName());
                Logging.logger().severe(msg);
                return value;
            }

            // TODO check for elevation type

            ByteBufferRaster bb = (ByteBufferRaster) raster;
            double nodata = bb.getTransparentValue();

            int width = raster.getWidth();
            int height = raster.getHeight();

            value.min = Double.MAX_VALUE;
            value.max = -Double.MAX_VALUE;

            for (int row = 0; row < height; row++)
            {
                for (int col = 0; col < width; col++)
                {
                    double x = bb.getDoubleAtPosition(row, col);
                    // we want to overwrite nodata areas only
                    if (x != nodata)
                    {
                        if( x > value.max )
                            value.max = x;
                        if( x < value.min )
                            value.min = x;
                    }
                }
            }
            return value;
        }
    }



}
