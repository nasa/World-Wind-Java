/* Copyright (C) 2001, 2008 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.formats.tiff.GeotiffImageReaderSpi;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.tools.gdal.ReadWriteFormat;
import gov.nasa.worldwind.servers.tools.naip.NAIP;
import gov.nasa.worldwind.servers.tools.naip.NAIP.QuarterQuadrangle;
import gov.nasa.worldwind.servers.tools.utm.UTMSector;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.BufferedImageFormatter;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.servers.wms.utilities.FileUtil;
import gov.nasa.worldwind.util.DataConfigurationUtils;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWXML;
import org.w3c.dom.Document;

import javax.imageio.spi.IIORegistry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.*;

/**
 * @author garakl
 * @version $Id$
 */ 
public class NAIPGenerator extends AbstractMapGenerator
{
    private String dataFileStore = "NaipDataFileStore.xml";
    private String dataConfigurationFile = "/data-47/Cache/Tiled_Cache/NAIP/naip.xml";

    protected WorldWindTiledLayer naipTiledLayer = null;

    
    public String getDataType()
    {
        return "imagery";
    }

    public NAIPGenerator()
    {
        super();
        this.naipTiledLayer = new WorldWindTiledLayer();
    }

    public ServiceInstance getServiceInstance()
    {
        return new NAIPServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = false;
        
        try
        {
            this.mapSource = mapSource;
            String ms_name = mapSource.getName();
            

            Logging.logger().fine("NAIPGenerator: initializing with mapSource: " + mapSource.getName());

            // Extract expected properties that should have been set in our MapSource
            // configuration...
            Properties myProps = mapSource.getProperties();
            if (myProps == null)
                throw new IllegalArgumentException("Missing properties in configuration for MapSource: "
                    + mapSource.getServiceClass().getName());

            this.rootDir = new File(mapSource.getRootDir());
            if( !this.rootDir.exists())
            {
                String msg = Logging.getMessage( "generic.FolderDoesNotExist", mapSource.getRootDir() );
                Logging.logger().severe(msg);
                throw new FileNotFoundException( msg );
            }
            Logging.logger().fine("NAIPGenerator: data directory set to " + this.rootDir.getAbsolutePath());

            this.indicesBaseDir = new File( myProps.getProperty(INDICES_BASE_DIR) );
            if( !this.indicesBaseDir.exists() )
            {
                String msg = Logging.getMessage( "generic.FolderDoesNotExist", this.indicesBaseDir.getAbsolutePath() );
                Logging.logger().severe(msg);
                throw new FileNotFoundException( msg );
            }
            Logging.logger().fine("NAIPGenerator: index directory set to " + this.indicesBaseDir.getAbsolutePath());

            this.readCoverageXml();
            Logging.logger().fine("NAIPGenerator: coverage = " + this.BBOX.toString() );

            try
            {
                this.default_missing_data_signal = (short)Double.parseDouble( this.getMapSource().getMissingDataSignal() );
            }
            catch(Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e );
            }
            Logging.logger().finest("NAIPGenerator: default_missing_data_signal = " + this.default_missing_data_signal );

            try
            {
                this.naip_missing_data_replacement = (short)Double.parseDouble( this.getMapSource().getMissingDataReplacement() );
            }
            catch(Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e );
            }
            Logging.logger().finest("NAIPGenerator: naip_missing_data_replacement = " + this.naip_missing_data_replacement );


            this.contrast = this.getProperty( myProps, "contrast", this.contrast, mapSource.getName() );
            Logging.logger().finest("NAIPGenerator: contrast= " + this.contrast );

            this.utm_tile_gap = this.getProperty( myProps, "utm_tile_gap", this.utm_tile_gap, mapSource.getName() );
            Logging.logger().finest("NAIPGenerator: utm_tile_gap= " + this.utm_tile_gap );


            // make sure our home-grown TIFF reader is registered with the ImageIO API...
            IIORegistry r = IIORegistry.getDefaultInstance();
            r.registerServiceProvider(GeotiffImageReaderSpi.inst());

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

            this.initializeNaipTiledLayer();

            success = true;
        }
        catch (Exception severe)
        {
            StringBuffer sb = new StringBuffer( "NAIPGenerator: initialization failed - " );
            sb.append(severe.getMessage());
            if( null != severe.getCause() )
                sb.append(severe.getCause().getMessage());
            Logging.logger().severe( sb.toString());
        }

        return success;
    }

    protected void initializeNaipTiledLayer() throws IOException, WMSServiceException
    {
        AVList params = new AVListImpl();

        Document dataConfigDoc = WWXML.openDocument( this.dataConfigurationFile );
        dataConfigDoc = DataConfigurationUtils.convertToStandardDataConfigDocument(dataConfigDoc);

        DataConfigurationUtils.getLevelSetConfigParams(dataConfigDoc.getDocumentElement(), params);
        WMSDataFileStore fs = new WMSDataFileStore("WEB-INF" + File.separator + dataFileStore );

        DataConfigurationMapSource tiled_ms = new DataConfigurationMapSource(fs, dataConfigDoc.getDocumentElement(),
                params, WorldWindTiledLayer.class);
        this.naipTiledLayer.initialize( tiled_ms );
    }


    private void readCoverageXml()
    {
        InputStream is = null;

        File f = new File( this.indicesBaseDir.getAbsolutePath() + File.separator + "ww.coverage.xml");

        try
        {
            is = new FileInputStream( f );

            DocumentBuilderFactory docfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docfac.newDocumentBuilder();
            Document doc = builder.parse( is );

            XPathFactory xpfac = XPathFactory.newInstance();
            XPath xpath = xpfac.newXPath();

            double minlon, maxlon, minlat, maxlat ;
            String s = xpath.evaluate("/coverage/@minlon", doc );
            minlon = Double.parseDouble( s );
            
            s = xpath.evaluate("/coverage/@maxlon", doc );
            maxlon = Double.parseDouble( s );

            s = xpath.evaluate("/coverage/@minlat", doc );
            minlat = Double.parseDouble( s );

            s = xpath.evaluate("/coverage/@maxlat", doc );
            maxlat = Double.parseDouble( s );

            this.BBOX = Sector.fromDegrees(minlat, maxlat, minlon, maxlon);
        }
        catch (Exception ex)
        {
            Logging.logger().log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
        }
        finally
        {
            if( null != is ) try { is.close(); } catch(Exception ignore) {}
        }
    }

    public Sector getBBox()
    {
        return this.BBOX;
    }

    public boolean hasCoverage(Sector sector)
    {
        // check first bigger bounding box
        if(null != sector && sector.intersects( this.getBBox() ))
        {
            // TODO
//
//            // more fine check, becasue USGS NED does not have continuous coverage within its bounding box
//            for (NEDFile ned : ElevationUSGSNED.this.spatialIndex)
//            {
//                if (sector.intersects(ned.sector))
//                    return true;
//            }
        }
        return true;
    }


    public double getPixelSize()
    {
        return this.mapSource.getScaleHintMax();
    }

    public String[] getCRS()
    {
        return new String[]{crsStr};
    }

    protected static File makeLink(File base_path, int iLat, int iLon, int quad, String qq)
    {
        StringBuilder source = new StringBuilder(512);
        Formatter formatter = new Formatter(source, Locale.US);

        // build a relative path to the quarter-quad
        // example, W120/N49/W120.N49.57SW.tif.link
        formatter.format("%s%02d.%s%03d.%02d%s.tiff.link",
            ((iLat >= 0) ? "N" : "S"), Math.abs(iLat),
            ((iLon <= 0) ? "W" : "E"), Math.abs(iLon),
            quad, qq.toUpperCase()
        );
        String filename = source.toString();

        source.setLength(0);
        formatter.format("%s%s%s%02d%s%s%03d",
            base_path.getAbsolutePath(),
            File.separator,
            ((iLat >= 0) ? "N" : "S"), Math.abs(iLat),
            File.separator,
            ((iLon <= 0) ? "W" : "E"), Math.abs(iLon)
        );
        String filepath = source.toString();

        return new File(filepath, filename);
    }

    public class NAIPServiceInstance extends AbstractServiceInstance
    {
        private Sector intersects( Sector a, Sector b )
        {
            if( null != a && null != b)
            {
                Sector overlap = a.intersection(b);
                if(    overlap != null
                    && overlap.getDeltaLon().degrees > 0d
                    && overlap.getDeltaLat().degrees > 0d
                  )
                {
                    return overlap;
                }
            }
            return null;
        }

        protected double calcPixelSizeOfRequestArea( IMapRequest req )
        {
            Sector reqSector = req.getExtent();
            double reqHeight = (double)((req.getHeight() > 0) ? req.getHeight() : 512 );
            return  (reqSector.getDeltaLatDegrees() / reqHeight);
        }


        private File locateTile (int iLat, int iLon, int quad, QuarterQuadrangle qq )
        {
            File qqLink = makeLink(NAIPGenerator.this.indicesBaseDir, iLat, iLon, quad, qq.toString());
            if( null != qqLink )
            {
                if( qqLink.exists())
                {
                    BufferedReader reader = null;
                    try
                    {
                        reader = new BufferedReader(new FileReader( qqLink ));
                        String linked_file = reader.readLine();
                        reader.close();
                        reader = null;

                        File qqFile = new File( linked_file );
                        if( qqFile.exists() )
                        {
                            Logging.logger().finest("Added " + qqFile.getAbsolutePath());
                            return qqFile;
                        }
                        else
                        {
                            String msg = Logging.getMessage( "NAIP.LinkRefersToNonExistingQuarterQuad", qqLink.getAbsolutePath() );
                            Logging.logger().severe(msg);
                        }

                    }
                    catch (Exception e)
                    {
                        Logging.logger().severe( "NAIP: locateAndAdd: " + e.getMessage() );
                    }
                    finally
                    {
                        try {
                            if (reader != null)
                                reader.close();
                        } catch (IOException ignore) {}
                    }
                }
//                else
//                    Logging.logger().info( qqLink.getAbsolutePath() + " cannot be found. Maybe no coverage." );
            }
            return null;
        }


        public ImageFormatter serviceRequest(IMapRequest req)
                throws IOException, WMSServiceException
        {

            long begTime = System.currentTimeMillis();

            NAIPGenerator.this.setThreadId( "NAIP" );

            Logging.logger().finest( NAIPGenerator.this.getThreadId() + "processing service request ...");

            BufferedImageFormatter formatter = null;
            try
            {
                if( this.calcPixelSizeOfRequestArea( req ) >= 0.00013733d /* zoom level 9 and lower up to 0 */ )
                {
                   formatter = (BufferedImageFormatter)
                           NAIPGenerator.this.naipTiledLayer.getServiceInstance().serviceRequest( req );
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
                String s = NAIPGenerator.this.getThreadId() + "request failed: " + ex.toString();
                Logging.logger().severe( s );
                // throw new WMSServiceException( s );
            }
            finally
            {
                long ellapsed = System.currentTimeMillis() - begTime;
                Logging.logger().fine( NAIPGenerator.this.getThreadId()
                        + "Request with pixel size = " + this.calcPixelSizeOfRequestArea( req )
                        + " completed in " + ellapsed + " msec. " );
            }

            if(null == formatter)
            {
                int reqWidth = (req.getWidth() > 0) ? req.getWidth() : 512;
                int reqHeight = (req.getHeight() > 0) ? req.getHeight() : 512;

                short missingColor = NAIPGenerator.this.default_missing_data_signal;

                try
                {
                    String bgColorStr = req.getBGColor();
                    if (bgColorStr != null)
                        missingColor = Short.parseShort(req.getBGColor());
                }
                catch (Exception ignore) {}

                if(    (512 == reqWidth) && (512 == reqHeight)
                    && missingColor == NAIPGenerator.this.default_missing_data_signal )
                {
                    if( null == NAIPGenerator.defaultEmtpy512x512 )
                    {
                        // lazy initialization of NAIPGenerator.defaultEmtpy512x512
                        BufferedImage tmpImage = new BufferedImage( 512, 512, BufferedImage.TYPE_4BYTE_ABGR );
                        this.makeNoDataTransparentAndAutoContrast( tmpImage, NAIPGenerator.this.default_missing_data_signal );
                        NAIPGenerator.defaultEmtpy512x512 = new BufferedImageFormatter(tmpImage);
                    }

                    formatter = NAIPGenerator.defaultEmtpy512x512;
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

        public BufferedImage buildBufferedImage(IMapRequest req)
                throws IOException, WMSServiceException
        {
            BufferedImage reqImage = null;
            long begTime = System.currentTimeMillis();

            NAIPGenerator.this.setThreadId( "NAIP" );

            Logging.logger().finest( NAIPGenerator.this.getThreadId() + "processing service request ...");

            int reqWidth = 512, reqHeight = 512;
            short missingColor = NAIPGenerator.this.default_missing_data_signal;

            ArrayList<File> qquads = new ArrayList<File>();

            try
            {
                // Our convention for dealing with missing data is that we'll use what ever might be
                // specified in the BGCOLOR parameter of the request, or a default of zero otherwise.
                try {
                    String bgColorStr = req.getBGColor();
                    if (bgColorStr != null)
                        missingColor = Short.parseShort(req.getBGColor());
                }
                catch (Exception ex) {
                    Logging.logger().finest("Unable to parse BGCOLOR in NAIP request: " + req.getBGColor());
                }

                // Figure out what parts of the SRTM30 grid the request overlaps...
                Sector reqSector = req.getExtent();

                reqWidth = (req.getWidth() > 0) ? req.getWidth() : reqWidth;
                reqHeight = (req.getHeight() > 0) ? req.getHeight() : reqHeight;

                reqImage = new BufferedImage(reqWidth, reqHeight, BufferedImage.TYPE_4BYTE_ABGR);


                double texelSize = (reqSector.getDeltaLatDegrees() / (double)reqHeight);
                Logging.logger().fine( "NAIP: req.sector = " + reqSector.toString()
                        + ", latitude delta = " + reqSector.getDeltaLatDegrees() 
                        + ", req pixel size = " + texelSize
                        + ", missing color = " + missingColor );

                if( texelSize > NAIPGenerator.this.getMapSource().getScaleHintMin() )
                    throw new WMSServiceException( "NAIP: req.sector is too big. Skipping. " + texelSize );

                int minlon = (int)(reqSector.getMinLongitude().getDegrees());
                minlon = (minlon < -179) ? -179 : minlon;

                int maxlon = (int)(reqSector.getMaxLongitude().getDegrees() + 1d);
                maxlon = (maxlon > 180) ? 180 : maxlon;

                int minlat = (int)(reqSector.getMinLatitude().getDegrees());
                minlat = (minlat < -90) ? -90 : minlat;

                int maxlat = (int)(reqSector.getMaxLatitude().getDegrees() + 1d);
                maxlat = (maxlat > 89 ) ? 89 : maxlat;

                // TODO re-check agains coverage
                Logging.logger().finest("DEBUG: minlon=" + minlon + ", maxlon=" + maxlon + ", minlat=" + minlat + ". maxlat=" + maxlat );

//                BufferedImage reqImage = new BufferedImage(reqWidth, reqHeight, BufferedImage.TYPE_4BYTE_ABGR);

                for(int iLon = minlon; iLon < maxlon; iLon++ )
                {
                    for(int iLat = minlat; iLat < maxlat; iLat++ )
                    {
                        Sector naipBlock = NAIP.getSectorOfGridCell( iLat, iLon );
                        if( null == this.intersects( reqSector, naipBlock ) )
                        {
                            Logging.logger().finest("DEBUG: no intersection with " + naipBlock.toString() );
                            continue;
                        }

                        // every NAIP tile 1" (60')  degree x 1" (60') degree is subdivied
                        // into 64 quadrangles size of 0.125 (7.5') x 0.125 (7.5') degress
                        // and every quadrangle is subdivided into 4 quarter-quadrangles (QQ)

                        for(int quad = 1; quad <= 64 ; quad++ )
                        {
                            Sector quadSector = NAIP.getSectorOfQuadrangle(iLat, iLon, quad );
                            if( null == this.intersects( reqSector, quadSector ))
                                continue;

                            // check each quarter-quadrangle now SE, SE, NE, NW
                            for(QuarterQuadrangle qq : QuarterQuadrangle.values())
                            {
                                Sector secQQ = NAIP.getSectorOfQuarterQuadrangle( iLat, iLon, quad, qq );
                                Sector overlap = this.intersects( reqSector, secQQ );
                                if( null != overlap )
                                {
                                    File qqTile = this.locateTile(iLat, iLon, quad, qq );
                                    if(null != qqTile )
                                    {
                                        File cropped = this.crop( qqTile, overlap, reqSector, reqWidth, reqHeight );
                                        if( null != cropped )
                                            qquads.add( cropped );
                                    }
                                }
                            }
                        }
                    }
                }

//                Logging.logger().finest("DEBUG: found intesection with " + qquads.size() + " tiles" );

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


                    long startTime = System.currentTimeMillis();

                    BufferedImage sourceImage = this.mosaic(
                            sourceFiles,
                            reqSector,
                            req.getWidth(),
                            req.getHeight(),
                            NAIPGenerator.this.naip_missing_data_replacement,
                            missingColor
                    );

                    long ellapsed = System.currentTimeMillis() - startTime;
                    Stats.report("GDAL_WARP", qquads.size(), ellapsed );
                    Logging.logger().finest( Stats.getStats("GDAL_WARP") );

                    for(File f: sourceFiles )
                        FileUtil.delete(f);

                    if( null != sourceImage && null != reqImage )
                    {
                        Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
                        g2d.drawImage( sourceImage,0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
                        g2d.dispose();

                        this.makeNoDataTransparentAndAutoContrast( reqImage, missingColor );
                    }
                }
            }
            catch (Exception ex)
            {
                String s = NAIPGenerator.this.getThreadId() + "request failed: " + ex.toString();
                Logging.logger().severe( s );
                // throw new WMSServiceException( s );
            }
            finally
            {
                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report("NAIP", qquads.size(), ellapsed );
                Logging.logger().fine( NAIPGenerator.this.getThreadId() + "DONE " + qquads.size() + " tiles in " + ellapsed + " msec. " + Stats.getStats("NAIP"));
            }

            return reqImage;
        }



//        private void mosaic( File f, BufferedImage reqImage, Sector reqSector, Sector tile, short srcNoData, short destNoData )
//        {
//            if( null != f && null != reqImage && null != reqSector &&  null != tile )
//            {
//                Sector overlap = tile.intersection( reqSector );
//
//                int dx1 = (int) ((overlap.getMinLongitude().degrees - reqSector.getMinLongitude().degrees)
//                    * reqImage.getWidth() / reqSector.getDeltaLonDegrees());
//                int dx2 = (int) ((overlap.getMaxLongitude().degrees - reqSector.getMinLongitude().degrees)
//                    * reqImage.getWidth() / reqSector.getDeltaLonDegrees());
//                int dy1 = (int) ((reqSector.getMaxLatitude().degrees - overlap.getMaxLatitude().degrees)
//                    * reqImage.getHeight() / reqSector.getDeltaLatDegrees());
//                int dy2 = (int) ((reqSector.getMaxLatitude().degrees - overlap.getMinLatitude().degrees)
//                    * reqImage.getHeight() / reqSector.getDeltaLatDegrees());
//
//                BufferedImage sourceImage = this.mosaic( new File[] { f }, overlap, (dx2 - dx1), (dy2 - dy1), srcNoData, destNoData );
//
//                if (null != sourceImage)
//                {
//                    int sx1, sx2, sy1, sy2;
//
//                    sx1 = sy1 = 0;
//                    sx2 = sourceImage.getWidth();
//                    sy2 = sourceImage.getHeight();
//
//                    Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
//                    g2d.drawImage(sourceImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
//                    g2d.dispose();
//                }
//            }
//        }

        private final int filterContrast(int c)
        {
            float d = c / 255f;
            d = 255f * (d * (3 * d - 2 * d * d ));
//            d = 0.5f + ((d - 0.5f) * NAIPGenerator.this.contrast );
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


        private File crop( File sourceFile, Sector overlap, Sector reqSector, int reqWidth, int reqHeight )
        {
            long begTime = System.currentTimeMillis();
            File tmpFile = TempFile.getTempFile();
            try
            {
                UTMSector utm_overlap = UTMSector.fromSector( overlap );

                int minx = (int)utm_overlap.getMinEasting();
                int maxx = (int)utm_overlap.getMaxEasting();

                int miny = (int)utm_overlap.getMinNorthing();
                int maxy = (int)utm_overlap.getMaxNorthing();

                int tileWidth = reqWidth, tileHeight = reqHeight;

                if( reqWidth != 0 && reqHeight != 0 )
                {
                    int dx1 = (int) ((overlap.getMinLongitude().degrees - reqSector.getMinLongitude().degrees)
                        * ((double)reqWidth) / reqSector.getDeltaLonDegrees());

                    int dx2 = (int) ((overlap.getMaxLongitude().degrees - reqSector.getMinLongitude().degrees)
                        * ((double)reqWidth) / reqSector.getDeltaLonDegrees());

                    int dy1 = (int) ((reqSector.getMaxLatitude().degrees - overlap.getMaxLatitude().degrees)
                        * ((double)reqHeight) / reqSector.getDeltaLatDegrees());

                    int dy2 = (int) ((reqSector.getMaxLatitude().degrees - overlap.getMinLatitude().degrees)
                        * ((double)reqHeight) / reqSector.getDeltaLatDegrees());

                    tileWidth = (dx2 - dx1);
                    tileHeight = (dy2 - dy1);
                }

                int gap = NAIPGenerator.this.utm_tile_gap;
                if( gap != 0 )
                {
                    double orig_delta_x = maxx - minx + 1;
                    double orig_delta_y = maxy - miny + 1;

                    minx -= gap;
                    maxx += gap;
                    maxy += gap;
                    miny -= gap;

                    double new_delta_x = maxx - minx + 1;
                    double new_delta_y = maxy - miny + 1;

                    tileWidth = (int)((new_delta_x * ((double)tileWidth)) / orig_delta_x );
                    tileHeight = (int)((new_delta_y * ((double)tileHeight)) / orig_delta_y );
                }

                GDALUtils gdal = GDALUtils.getGDAL();
                gdal.translate(
                        NAIPGenerator.this.getThreadId(),
                        new String[] {
//                            "--config", "GDAL_CACHEMAX", "1024",
//                            "--debug", "ON",
//                            "-quiet",
                            "-of", "GTiff",
                            "-projwin",
                            Integer.toString(minx), Integer.toString(maxy),
                            Integer.toString(maxx), Integer.toString(miny),
                            "-outsize",
                            Integer.toString( tileWidth ), Integer.toString( tileHeight )
                        },
                        sourceFile,
                        tmpFile
                );
            }
            catch (Exception ex)
            {
                String msg = NAIPGenerator.this.getThreadId() +  ex.toString();
                Logging.logger().severe( msg );
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
            File tmpFile = TempFile.getTempFile();
            try
            {
                GDALUtils gdal = GDALUtils.getGDAL();
                gdal.warp( NAIPGenerator.this.getThreadId(),
                        Option.Warp.Resampling.NearNeighbour,
                        new String[] {
//                                "--debug", "ON",
//                                "-q",
//                                "--config", "GDAL_CACHEMAX", "1024",
//                                "-wm", "1024",
                                "-srcnodata", String.valueOf(srcNoData) ,
                                "-dstnodata", String.valueOf(destNoData),
                                "-t_srs", "EPSG:4326"
//                                "-t_srs", "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs"
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
                String msg = NAIPGenerator.this.getThreadId() +  ex.toString();
                Logging.logger().severe( msg );
            }
            finally
            {
                FileUtil.delete(tmpFile);
            }
            return sourceImage;
        }


        public void freeResources()
        {
        }
    }

//    private static final String[] CRS = {"EPSG:4326"};
//    private static final short MAX_QUADS_PER_REQUEST = 16;
    private static final String INDICES_BASE_DIR = "indices_base_dir";

    private static BufferedImageFormatter defaultEmtpy512x512 = null;

    private short default_missing_data_signal = 0;
    private short naip_missing_data_replacement = -32768;

    private int utm_tile_gap = 50;

    private File rootDir = null;
    private File indicesBaseDir = null;
    private Sector BBOX = Sector.FULL_SPHERE; // Sector.EMPTY_SECTOR;
    private double contrast = 0.9d; // 0.9d - to darken, 1.1d - to lighten

    private static final String crsStr = "EPSG:4326";
}

