/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.tools.gdal.ReadWriteFormat;
import gov.nasa.worldwind.servers.tools.utm.UTMSector;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.BufferedImageFormatter;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.util.DataConfigurationUtils;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWXML;
import org.w3c.dom.Document;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;

/**
 * @author garakl
 * @version $Id$
 */
public class ScankortGenerator extends AbstractMapGenerator
{
    private static final String[] CRS = {"EPSG:4326"};

    private static final short MAX_QUADS_PER_REQUEST = 16;
    private static final String INDICES_BASE_DIR = "indices_base_dir";

    private static BufferedImageFormatter defaultEmtpy512x512 = null;

    private short default_missing_data_signal = 0;
    private short naip_missing_data_replacement = -32768;

    private int utm_tile_overlap = 50;

    private File rootDir = null;
    private Sector BBOX = Sector.FULL_SPHERE; // Sector.EMPTY_SECTOR;
    private double contrast = 1d; // 0.9d - to darken, 1.1d - to lighten, 1d = do nothing

    private static final String crsStr = "EPSG:4326";

    private static final short  TILE_WIDTH_IN_METERS  = 1000;
    private static final short  TILE_WIDTH_IN_PIXELS  = 5000;
    private static final short  TILE_HEIGHT_IN_METERS = 1000;
    private static final short  TILE_HEIGHT_IN_PIXELS = 5000;

    private String dataFileStore = "ScankortDataFileStore.xml";
    private String dataConfigurationFile = "/winddata/Cache/Tiled_Cache/Scankort_Denmark/ScankortTiled.xml";

    protected WorldWindTiledLayer scankortTiledLayer = null;

    public String getDataType()
    {
        return "imagery";
    }

    public ScankortGenerator()
    {
        super();

        this.scankortTiledLayer = new WorldWindTiledLayer();
    }

    public ServiceInstance getServiceInstance()
    {
        return new ScankortServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = false;

        try
        {
            this.mapSource = mapSource;

            Logging.logger().fine("ScankortGenerator: initializing with mapSource: " + mapSource.getName());

            // Extract expected properties that should have been set in our MapSource
            // configuration...
            Properties myProps = mapSource.getProperties();
            if (myProps == null)
                throw new IllegalArgumentException("Missing properties in configuration for MapSource: "
                    + mapSource.getServiceClass().getName());

            this.rootDir = new File(mapSource.getRootDir());
            if( !this.rootDir.exists())
            {
                success = false;
                String msg = Logging.getMessage( "generic.FolderDoesNotExist", mapSource.getRootDir() );
                Logging.logger().severe(msg);
                throw new FileNotFoundException( msg );
            }
            Logging.logger().fine("ScankortGenerator: data directory set to " + this.rootDir.getAbsolutePath());

            try
            {
                this.default_missing_data_signal = (short)Double.parseDouble( this.getMapSource().getMissingDataSignal() );
            }
            catch(Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e );
            }
            Logging.logger().finest("ScankortGenerator: default_missing_data_signal = " + this.default_missing_data_signal );

            try
            {
                this.naip_missing_data_replacement = (short)Double.parseDouble( this.getMapSource().getMissingDataReplacement() );
            }
            catch(Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }
            Logging.logger().finest("ScankortGenerator: naip_missing_data_replacement = " + this.naip_missing_data_replacement );

            String ms_name = mapSource.getName();

            this.contrast = this.getProperty( myProps, "contrast", this.contrast, ms_name );
            Logging.logger().finest("ScankortGenerator: contrast= " + this.contrast );

            this.utm_tile_overlap = this.getProperty( myProps, "utm_tile_overlap", this.utm_tile_overlap, ms_name );
            Logging.logger().finest("ScankortGenerator: utm_tile_overlap= " + this.utm_tile_overlap );

            double min_lon, max_lon, min_lat, max_lat;
            min_lat = this.getProperty( myProps, "coverage_min_latitude", 0d, ms_name );
            max_lat = this.getProperty( myProps, "coverage_max_latitude", 0d, ms_name );
            min_lon = this.getProperty( myProps, "coverage_min_longitude", 0d, ms_name );
            max_lon = this.getProperty( myProps, "coverage_max_longitude", 0d, ms_name );

            this.BBOX = Sector.fromDegrees( min_lat, max_lat, min_lon, max_lon);
            Logging.logger().fine("ScankortGenerator: coverage = " + this.BBOX.toString() );

            this.dataFileStore = this.getProperty( myProps, "DataFileStore", this.dataFileStore, mapSource.getName());
//            if( !(new File("WEB-INF" + File.separator + this.dataFileStore).exists()) )
//            {
//                String msg = Logging.getMessage("FileStore.LocationInvalid", this.dataFileStore );
//                Logging.logger().severe(msg);
//                throw new IllegalArgumentException(msg);
//            }

            this.dataConfigurationFile = this.getProperty( myProps, "DataConfigurationFile", dataConfigurationFile, ms_name );
            if( !(new File(this.dataConfigurationFile).exists()) )
            {
                String msg = Logging.getMessage("FileStore.ConfigurationNotFound", this.dataConfigurationFile );
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.initializeScankortTiledLayer();

            success = true;
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage( "WMS.MapGenerator.CannotInstantiate", ex.getMessage() );
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
            // throw new WMSServiceException( msg );
            success = false;
        }

        return success;
    }

    protected void initializeScankortTiledLayer() throws IOException, WMSServiceException
    {
        AVList params = new AVListImpl();

        Document dataConfigDoc = WWXML.openDocument( this.dataConfigurationFile );
        dataConfigDoc = DataConfigurationUtils.convertToStandardDataConfigDocument(dataConfigDoc);

        DataConfigurationUtils.getLevelSetConfigParams(dataConfigDoc.getDocumentElement(), params);
        WMSDataFileStore fs = new WMSDataFileStore("WEB-INF" + File.separator + dataFileStore );

        DataConfigurationMapSource tiled_ms = new DataConfigurationMapSource(fs, dataConfigDoc.getDocumentElement(),
                params, WorldWindTiledLayer.class);
        this.scankortTiledLayer.initialize( tiled_ms );
    }

    public Sector getBBox()
    {
        return this.BBOX;
    }

    public boolean hasCoverage(Sector sector)
    {
        // check first bigger bounding box
        return (null != sector && sector.intersects( this.getBBox() ));
    }


    public double getPixelSize()
    {
        return this.mapSource.getScaleHintMax();
    }

    public String[] getCRS()
    {
        return new String[]{crsStr};
    }

    public class ScankortServiceInstance extends AbstractServiceInstance
    {
        private boolean intersects( Sector a, Sector b )
        {
            if( null != a && null != b)
            {
                Sector overlap = a.intersection(b);
                if(    overlap != null
                    && overlap.getDeltaLon().degrees > 0d
                    && overlap.getDeltaLat().degrees > 0d
                  )
                {
                    return true;
                }
            }
            return false;
        }


        protected double calcPixelSizeOfRequestArea( IMapRequest req )
        {
            Sector reqSector = req.getExtent();
            double reqHeight = (double)((req.getHeight() > 0) ? req.getHeight() : 512 );
            return  (reqSector.getDeltaLatDegrees() / reqHeight);
        }

        public ImageFormatter serviceRequest( IMapRequest req) throws IOException, WMSServiceException
        {
            long begTime = System.currentTimeMillis();

            ScankortGenerator.this.setThreadId( "Scankort" );

            Logging.logger().finest( ScankortGenerator.this.getThreadId() + "processing service request ...");

            BufferedImageFormatter formatter = null;
            try
            {
                if( this.calcPixelSizeOfRequestArea( req ) > 0.00001d /* zoom level 12 and lower up to 0 */ )
                {
                   formatter = (BufferedImageFormatter)
                           ScankortGenerator.this.scankortTiledLayer.getServiceInstance().serviceRequest( req );
                }
                else
                {
                    BufferedImage image = this.buildBufferedImage( req );
                    if( null != image )
                        formatter = new BufferedImageFormatter( image );
                }
            }
            catch (Exception ex)
            {
                String s = ScankortGenerator.this.getThreadId() + "request failed: " + ex.toString();
                Logging.logger().severe( s );
                // throw new WMSServiceException( s );
            }
            finally
            {
                long ellapsed = System.currentTimeMillis() - begTime;
                Logging.logger().fine( ScankortGenerator.this.getThreadId()
                        + "Request with pixel size = " + this.calcPixelSizeOfRequestArea( req )
                        + " completed in " + ellapsed + " msec. " );
            }

            if(null == formatter)
            {
                int reqWidth = (req.getWidth() > 0) ? req.getWidth() : 512;
                int reqHeight = (req.getHeight() > 0) ? req.getHeight() : 512;

                short missingColor = ScankortGenerator.this.default_missing_data_signal;

                try
                {
                    String bgColorStr = req.getBGColor();
                    if (bgColorStr != null)
                        missingColor = Short.parseShort(req.getBGColor());
                }
                catch (Exception ignore) {}

                if(    (512 == reqWidth) && (512 == reqHeight)
                    && missingColor == ScankortGenerator.this.default_missing_data_signal )
                {
                    if( null == ScankortGenerator.defaultEmtpy512x512 )
                    {
                        // lazy initialization of ScankortGenerator.defaultEmtpy512x512
                        BufferedImage tmpImage = new BufferedImage( 512, 512, BufferedImage.TYPE_4BYTE_ABGR );
                        this.makeNoDataTransparentAndAutoContrast( tmpImage, ScankortGenerator.this.default_missing_data_signal );
                        ScankortGenerator.defaultEmtpy512x512 = new BufferedImageFormatter(tmpImage);
                    }

                    formatter = ScankortGenerator.defaultEmtpy512x512;
                }
                else
                {
                    BufferedImage tmpImage = new BufferedImage(reqWidth, reqHeight, BufferedImage.TYPE_4BYTE_ABGR);
                    this.makeNoDataTransparentAndAutoContrast( tmpImage, missingColor );
                    formatter = new BufferedImageFormatter(tmpImage);
                }
            }
            return formatter;
        }

        public BufferedImage buildBufferedImage(IMapRequest req) throws IOException, WMSServiceException
        {
            int reqWidth = 512, reqHeight = 512;

            short missingColor = ScankortGenerator.this.default_missing_data_signal;
            try
            {
                String bgColorStr = req.getBGColor();
                if (bgColorStr != null)
                    missingColor = Short.parseShort(req.getBGColor());
            }
            catch (Exception ignore) {}

            ArrayList<File> qquads = new ArrayList<File>();

            Sector reqSector = req.getExtent();
            reqWidth = (req.getWidth() > 0) ? req.getWidth() : reqWidth;
            reqHeight = (req.getHeight() > 0) ? req.getHeight() : reqHeight;

            if( !ScankortGenerator.this.hasCoverage( reqSector ) )
                throw new WMSServiceException( "Scankort: Out of coverage! Skipping." );

            double texelSize = (reqSector.getDeltaLatDegrees() / (double)reqHeight);
            Logging.logger().fine( "Scankort: req.sector = " + reqSector.toString()
                    + ", latitude delta = " + reqSector.getDeltaLatDegrees()
                    + ", req pixel size = " + texelSize
                    + ", missing color = " + missingColor );

            if( texelSize > ScankortGenerator.this.getMapSource().getScaleHintMin() )
                throw new WMSServiceException( "Scankort: req.sector is too big. Skipping. " + texelSize );

            UTMSector reqSectorUTM = UTMSector.fromSector( reqSector );

            // round to the nearest 1000m
            int minx = TILE_WIDTH_IN_METERS  * ((int)(reqSectorUTM.getMinEasting()  / (double)TILE_WIDTH_IN_METERS));
            int maxx = TILE_WIDTH_IN_METERS  * ((int)(reqSectorUTM.getMaxEasting()  / (double)TILE_WIDTH_IN_METERS));
            int miny = TILE_HEIGHT_IN_METERS * ((int)(reqSectorUTM.getMinNorthing() / (double)TILE_HEIGHT_IN_METERS));
            int maxy = TILE_HEIGHT_IN_METERS * ((int)(reqSectorUTM.getMaxNorthing() / (double)TILE_HEIGHT_IN_METERS));

            for(int x = minx; x <= maxx; x += TILE_WIDTH_IN_METERS )
            {
                for(int y = miny; y <= maxy; y += TILE_HEIGHT_IN_METERS )
                {
                    try
                    {
                        File file = this.crop( reqSectorUTM, x, y, reqWidth, reqHeight );
                        if( null != file )
                            qquads.add( file );
                    }
                    catch(Exception ex)
                    {
                        Logging.logger().log(java.util.logging.Level.SEVERE,
                            ScankortGenerator.this.getThreadId() + ex.getMessage(), ex);
                    }
                }
            }

            Logging.logger().finest("found intesection with " + qquads.size() + " tiles" );

//                this.makeNoDataTransparentAndAutoContrast( reqImage, missingColor );
//                formatter = new BufferedImageFormatter(reqImage);

//              if( qquads.size() > MAX_QUADS_PER_REQUEST )
//              {
//                  throw new WMSServiceException( "Too many tiles requested." );
//              }

            if( qquads.size() > 0 )
            {
                File[] sourceFiles = new File[ qquads.size() ];
                sourceFiles = (File[])(qquads.toArray( sourceFiles ));

                BufferedImage sourceImage = this.mosaic(
                        sourceFiles,
                        reqSector,
                        req.getWidth(),
                        req.getHeight(),
                        ScankortGenerator.this.naip_missing_data_replacement,
                        missingColor
                );

                for(File f: sourceFiles )
                {
                    try
                    {
                        f.delete();
                    }
                    catch (Exception ignore){}
                }

                if( null != sourceImage )
                {
                    BufferedImage reqImage = new BufferedImage(reqWidth, reqHeight, BufferedImage.TYPE_4BYTE_ABGR);
                    Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
                    g2d.drawImage( sourceImage,0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
                    g2d.dispose();

                    this.makeNoDataTransparentAndAutoContrast( reqImage, missingColor );

                    return reqImage;
                }
            }
            return null;
        }

        private void mosaic( File f, BufferedImage reqImage, Sector reqSector, Sector tile, short srcNoData, short destNoData )
        {
            if( null != f && null != reqImage && null != reqSector &&  null != tile )
            {
                Sector overlap = tile.intersection( reqSector );

                int dx1 = (int) ((overlap.getMinLongitude().degrees - reqSector.getMinLongitude().degrees)
                    * reqImage.getWidth() / reqSector.getDeltaLonDegrees());
                int dx2 = (int) ((overlap.getMaxLongitude().degrees - reqSector.getMinLongitude().degrees)
                    * reqImage.getWidth() / reqSector.getDeltaLonDegrees());
                int dy1 = (int) ((reqSector.getMaxLatitude().degrees - overlap.getMaxLatitude().degrees)
                    * reqImage.getHeight() / reqSector.getDeltaLatDegrees());
                int dy2 = (int) ((reqSector.getMaxLatitude().degrees - overlap.getMinLatitude().degrees)
                    * reqImage.getHeight() / reqSector.getDeltaLatDegrees());

                BufferedImage sourceImage = this.mosaic( new File[] { f }, overlap, (dx2 - dx1), (dy2 - dy1), srcNoData, destNoData );

                if (null != sourceImage)
                {
                    int sx1, sx2, sy1, sy2;

                    sx1 = sy1 = 0;
                    sx2 = sourceImage.getWidth();
                    sy2 = sourceImage.getHeight();

                    Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
                    g2d.drawImage(sourceImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
                    g2d.dispose();
                }
            }
        }

        private final int filterContrast(int c)
        {
            float d = c / 255f;
            d = 255f * (d * (3 * d - 2 * d * d ));
//            d = 0.5f + ((d - 0.5f) * ScankortGenerator.this.contrast );
            return ((d < 0f ) ? 0 : ((d > 255f) ? 255: (int)d));
        }

        private void makeNoDataTransparentAndAutoContrast(BufferedImage image, int missingColor )
        {
            WritableRaster raster = null;

            if(    null != image
                && image.getType() == BufferedImage.TYPE_4BYTE_ABGR
                && null != (raster = image.getRaster())
              )
            {
                int nodata_r = ((missingColor >> 16) & 0xff);
                int nodata_g = ((missingColor >> 8) & 0xff);
                int nodata_b = ((missingColor ) & 0xff);

                int[] pixel = new int[4];
                int width = image.getWidth();
                int height = image.getHeight();
                for (int j = 0; j < height; j++)
                {
                    for (int i = 0; i < width; i++)
                    {
                        // We know, by the nature of this source, that we are dealing with RGBA rasters...
                        raster.getPixel( i, j, pixel );
                        if ( pixel[0] == nodata_r && pixel[1] == nodata_g && pixel[2] == nodata_b )
                        {
//                            pixel[0] = 255;
                            pixel[3] = 0;
                            raster.setPixel( i, j, pixel );
                        }
                        else if( contrast != 1d )
                        {
                            pixel[0] = filterContrast( pixel[0] );
                            pixel[1] = filterContrast( pixel[1] );
                            pixel[2] = filterContrast( pixel[2] );
                            pixel[3] = 0xff;
                            raster.setPixel( i, j, pixel );
                        }
                    }
                }
            }
        }


        private File crop( UTMSector reqSector, int tile_x, int tile_y, int reqWidth_px, int reqHeight_px )
        {
            long begTime = System.currentTimeMillis();
            BufferedImage sourceImage = null;
            File tmpFile = TempFile.getTempFile();
            try
            {
                UTMSector utm_tile = new UTMSector( tile_x, tile_x + TILE_WIDTH_IN_METERS,
                        tile_y, tile_y + TILE_HEIGHT_IN_METERS, reqSector.getZone() );

                UTMSector overlap = reqSector.intersection( utm_tile );
                int minx = (int)overlap.getMinEasting();
                int maxx = (int)overlap.getMaxEasting();
                int miny = (int)overlap.getMinNorthing();
                int maxy = (int)overlap.getMaxNorthing();

                // ignore if overlap only on edges
                if ( minx == maxx || miny == maxy )
                    return null;

                StringBuilder source = new StringBuilder( 1024 );
                Formatter formatter = new Formatter(source, Locale.US);
                formatter.format( "%s%s%02d_%01d%s%03d_%02d%s1km_%04d_%03d.tif",
                        rootDir.getAbsolutePath(),
                        File.separator,
                        (int)(tile_y/100000), (int)(tile_x/100000),
                        File.separator,
                        (int)(tile_y/10000), (int)(tile_x/10000),
                        File.separator,
                        (int)(tile_y/1000), (int)(tile_x/1000)
                        );

                File sourceFile = new File(source.toString());
                if (!sourceFile.exists())
                {
                    Logging.logger().severe( ScankortGenerator.this.getThreadId()
                            + source.toString() + " does NOT exists in " + rootDir.getAbsolutePath());
                    return null;
                }

                // Scankort data tiles are 1,000m x 1,000m = 5000 x 5000 pixels
                double width_ratio = (double)TILE_WIDTH_IN_PIXELS / (double )TILE_WIDTH_IN_METERS;
                double height_ratio = (double)TILE_HEIGHT_IN_PIXELS / (double )TILE_HEIGHT_IN_METERS;

                int tileWidth_px = (int)(width_ratio * (maxx - minx + 1));
                tileWidth_px = ( tileWidth_px > reqWidth_px ) ? reqWidth_px : tileWidth_px;

                int tileHeight_px = (int)(height_ratio * (maxy - miny + 1));
                tileHeight_px = ( tileHeight_px > reqHeight_px ) ? reqHeight_px : tileHeight_px;

                int gap = ScankortGenerator.this.utm_tile_overlap;
                if( gap != 0 )
                {
                    double orig_delta_x = maxx - minx + 1;
                    double orig_delta_y = maxy - miny + 1;

                    minx -= gap;
                    minx = ( minx < tile_x ) ? tile_x : minx;
                    maxx += gap;
                    maxx = ( maxx > (tile_x + TILE_WIDTH_IN_METERS) ) ? (tile_x + TILE_WIDTH_IN_METERS) : maxx;
                    maxy += gap;
                    maxy = ( maxy > (tile_y + TILE_HEIGHT_IN_METERS) ) ? (tile_y + TILE_HEIGHT_IN_METERS) : maxy;
                    miny -= gap;
                    miny = ( miny < tile_y ) ? tile_y : miny;

                    double new_delta_x = maxx - minx + 1;
                    double new_delta_y = maxy - miny + 1;

                    tileWidth_px = (int)((new_delta_x * ((double)tileWidth_px)) / orig_delta_x );
                    tileHeight_px = (int)((new_delta_y * ((double)tileHeight_px)) / orig_delta_y );
                }

                GDALUtils gdal = GDALUtils.getGDAL();

                gdal.translate(
                        ScankortGenerator.this.getThreadId(),
                        new String[] {
//                            "--config", "GDAL_CACHEMAX", "1024",
//                            "--debug", "ON",
//                            "-quiet",
                            "-strict",
                            "-of", "GTiff",
                            "-projwin",
                            Integer.toString(minx), Integer.toString(maxy),
                            Integer.toString(maxx), Integer.toString(miny),
                            "-outsize",
                            Integer.toString( tileWidth_px ), Integer.toString( tileHeight_px )
                        },
                        sourceFile,
                        tmpFile
                );
            }
            catch (Exception ex)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE,
                    ScankortGenerator.this.getThreadId() + ex.getMessage(), ex);
            }
            finally
            {
                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report("GDAL_TRANS", 1, ellapsed );
                Logging.logger().finest( Stats.getStats("GDAL_TRANS") );

            }
            return ( tmpFile.exists() ) ? tmpFile : null ;
        }

        private BufferedImage mosaic(File[] sourceFiles, Sector extent, int width, int height, short srcNoData, short destNoData )
        {
            BufferedImage sourceImage = null;
            File tmpFile = new File( TempFile.getTempFile().getAbsolutePath() + ".tif" );
            try
            {
                GDALUtils gdal = GDALUtils.getGDAL();
                gdal.warp( ScankortGenerator.this.getThreadId(),
                        Option.Warp.Resampling.Cubic,
                        new String[] {
//                            "--config", "GDAL_CACHEMAX", "1024",
//                            "-wm", "1024",
//                            "--debug", "ON",
//                            "-q",
                            "-srcnodata", String.valueOf(srcNoData),
                            "-dstnodata", String.valueOf(destNoData),
                            "-s_srs", "+proj=utm +zone=32 +datum=WGS84 +no_defs",
//                             "-t_srs", "EPSG:4326",
                            "-t_srs", "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs"
                        },
                        sourceFiles,
                        extent, width, height,
                        ReadWriteFormat.GTiff,
                        tmpFile
                );

                GeotiffReader reader = new GeotiffReader(tmpFile);
                sourceImage = reader.read();
            }
            catch (Exception ex)
            {
                String msg = ScankortGenerator.this.getThreadId() +  ex.toString();
                Logging.logger().severe( msg );
            }
            finally
            {
                try {
                  tmpFile.delete();
                }
                catch(Exception ignore) {}
            }
            return sourceImage;
        }


        public void freeResources()
        {
        }
    }
}

