/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.servers.wms.formats.DataRasterFormatter;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.data.DataRaster;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author garakl
 * @version $Id$
 * @version $Id$
 */

/*
    USGS NED 10 meter elevation data was provided to the NASA by { TODO }

    Tile structure and file naming convention:

    Each tile is 1 degree by 1 degree in Geogdetic  coordinate system,
    projection is NAD83. Every tile is 10,812 x 10,812 and 32-bit floating point data.
    Tile is in the so called "ESRI BIL raster" format, technically it is a binary dump.
    Projection information is stored in the .PRJ file with the same name.
    Geo-referencing information is stored in the .HDR file with the same name.
    File name starts with "DEM"-prefix (Digital Elavation Model),
    two digits of longitude (for the USA assume negative longitude),
    and two digits latitude.

    Example: DEM7442, DEM7442.HDR, DEM7442.PRJ
    This is a tile with origin at uper left corner, covers sector -74W, 41N, -73W, 42N.

    Important 1: NODATA (MISSING DATA SIGNAL) is a default -9999.

    Important 2: each tile contain 0.0005 degree overlap.

 */
public class ElevationUSGSNED10m extends AbstractElevationGenerator
{
    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        this.setThreadId( "USGS_NED_10m" );
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

            success = true;
            
            Logging.logger().fine( this.getThreadId() + " layer started OK. Coverage: " + this.boundingSector.toString());
        }
        catch (Exception ex)
        {
            success = false;
            String msg = Logging.getMessage( "WMS.MapGenerator.CannotInstantiate", ex.getMessage() );
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
            // throw new WMSServiceException( msg );
        }

        return success;
    }

    public ServiceInstance getServiceInstance()
    {
        return new ElevationUSGSNED10mServiceInstance();
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
        return ( pixelSize != 0d ) ? pixelSize : (1d/10812d);
    }


    public String[] getCRS()
    {
        // TODO check projection for EPSG 4326 OR EPSG 4329
        // check with gdal if reprojection is requerd
        // this could slow down performance of the WMS server
        return CRS;
    }

    public class ElevationUSGSNED10mServiceInstance extends AbstractServiceInstance
    {
        private String threadID = "USGS_NED_10m";

        public String getThreadID()
        {
            return this.threadID;
        }

        public ElevationUSGSNED10mServiceInstance()
        {
            super();
            this.threadID = new StringBuffer("USGS_NED_10m").append(" (").append(Thread.currentThread().getId()).append("): ").toString();
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
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
            ElevationUSGSNED10m.this.setThreadId( "USGS NED 10m");

            ArrayList<File> tiles = new ArrayList<File>();

            StringBuilder source = new StringBuilder( 512 );

            Logging.logger().finest( ElevationUSGSNED10m.this.getThreadId() + "processing service request ...");

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

                if( !reqSector.intersects(ElevationUSGSNED10m.this.getBBox()))
                {
                    String msg = Logging.getMessage( "WMS.Layer.NoCoverage" );
                    Logging.logger().severe( this.getThreadID() + msg );
                    throw new WMSServiceException( msg );
                }

                double reqPixelSize = reqSector.getDeltaLatDegrees() / ((double)req.getHeight());
                if ( reqPixelSize > 1d/150d )
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

                            Sector tileSector = Sector.fromDegrees( lat - 1d, lat, lon, lon + 1d );
                            Sector overlap = reqSector.intersection(tileSector);

                            // Skip if tiles do nto intersect or intersect exactly at an edge (delta == 0)...
                            if (   null != overlap
                                && 0d < Math.abs(overlap.getDeltaLon().degrees)
                                && 0d < Math.abs(overlap.getDeltaLat().degrees)
                               )
                            {
                                // TODO: compute name of the USGS NED 10m tile and check its existance
                                source.setLength(0);
                                source.append(rootDir.getAbsolutePath()).append(File.separator).append("dem");
                                source.append(Math.abs(iLon)).append(Math.abs(iLat));

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
                            ElevationUSGSNED10m.NED_MISSING_DATA_FLAG,
                            req.getBGColorAsDouble().shortValue(),
                            ( "application/bil32".equals(req.getFormat()))  ? "Float32" : "Int16"
                    );
                }

                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report( "USGS.NED.10m", tiles.size(), ellapsed );
                Logging.logger().finest( this.getThreadID() + "DONE "
                        + tiles.size() + " tiles in "
                        + ellapsed + " msec. " + Stats.getStats("USGS.NED.10m"));
            }
            catch (Exception ex)
            {
                String s = this.getThreadID() + "request failed: " + ex.toString();
                Logging.logger().severe( s );
                throw new WMSServiceException( s );
            }
            return raster;
        }

        public void freeResources()
        {
            // NO-OP
        }
    }


    // TODO add USA coverage only
    private Sector boundingSector = Sector.fromDegrees( -14d, 66d, -171d, -63d );

    private static final String[] CRS = {"EPSG:4326"};
    private static final short NED_MISSING_DATA_FLAG = -9999;

    public static String DEFAULT_FILENAMING_FORMAT = "%s%sdem%02d%02d";
    protected String filenaming_format = DEFAULT_FILENAMING_FORMAT;

    protected File rootDir = null;
}
