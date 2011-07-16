/* Copyright (C) 2001, 2008 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.servers.wms.IMapRequest;
import gov.nasa.worldwind.servers.wms.MapSource;
import gov.nasa.worldwind.servers.wms.WMSServiceException;
import gov.nasa.worldwind.servers.wms.formats.DataRasterFormatter;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.util.Logging;

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

public class ElevationSrtm3V4 extends AbstractElevationGenerator
{
    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        this.setThreadId("Srtm3_Ver4");
        boolean success = false;

        try
        {
            this.rootDir = new File(mapSource.getRootDir());
            if (!this.rootDir.exists())
            {
                String msg = Logging.getMessage("generic.FolderDoesNotExist", mapSource.getRootDir());
                Logging.logger().severe(msg);
                throw new FileNotFoundException(msg);
            }
            Logging.logger().info(this.getThreadId() + "data directory set to " + this.rootDir.getAbsolutePath());

            Properties props = mapSource.getProperties();
            if (props == null)
            {
                throw new IllegalArgumentException("Missing properties in configuration for MapSource: "
                                                   + mapSource.getName());
            }

            this.filenaming_format = this.getProperty(props,
                    ElevationSrtm3V4.PROPERTY_FILENAMING_FORMAT,
                    ElevationSrtm3V4.DEFAULT_FILENAMING_FORMAT,
                    mapSource.getName());

            Logging.logger().info(this.getThreadId()
                                  + "Done! Status=OK; Tile file naming format string = " + this.filenaming_format);

            success = true;
        }
        catch (Exception ex)
        {
            StringBuffer sb = new StringBuffer(this.getThreadId());
            sb.append(" - initialization failed; reason: ").append(ex.getMessage());
            if (null != ex.getCause())
            {
                sb.append(ex.getCause().getMessage());
            }
            Logging.logger().severe(sb.toString());
            success = false;
        }
        return success;
    }

    public ServiceInstance getServiceInstance()
    {
        return new Srtm3ServiceInstance();
    }

    public Sector getBBox()
    {
        return BBOX;
    }

    public double getPixelSize()
    {
//        return (1d/1200d); // Original pixel size 0.000833333333
        double pixelSize = this.mapSource.getScaleHintMax();
        return (pixelSize != 0d) ? pixelSize : (1d / 1200d);
    }

    public String[] getCRS()
    {
        return CRS;
    }

    public class Srtm3ServiceInstance extends AbstractServiceInstance
    {
        private String threadID = "SRTM_3_V4";

        public String getThreadID()
        {
            return this.threadID;
        }

        public Srtm3ServiceInstance()
        {
            super();
            this.threadID = new StringBuffer("SRTM_3_V4").append(" (").append(Thread.currentThread().getId()).append(
                    "): ").toString();
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster raster = this.doServiceRequest(req);
            if (null == raster)
            {
                raster = Mosaicer.createCompatibleDataRaster(req);
            }
            return new DataRasterFormatter(raster);
        }

        public DataRaster doServiceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster raster = null;
//            BILImageFormatter targetImage = new BILImageFormatter(req.getWidth(), req.getHeight());
//            targetImage.setNoDataValue( (short)0 );

            long begTime = System.currentTimeMillis();

            Logging.logger().finest(this.getThreadID() + "processing service request ...");

            try
            {
                // include neighboor elevation pixels
                Sector reqSector = req.getExtentForElevationRequest();

                int iLonBeg = ElevationSrtm3V4.getLongitudeIndex(reqSector.getMinLongitude());
                int iLonEnd = ElevationSrtm3V4.getLongitudeIndex(reqSector.getMaxLongitude());
                int iLatBeg = ElevationSrtm3V4.getLatitudeIndex(reqSector.getMinLatitude());
                int iLatEnd = ElevationSrtm3V4.getLatitudeIndex(reqSector.getMaxLatitude());

                Logging.logger().finest("DEBUG: "
                                        + "longitude blocks { " + iLonBeg + " ~ " + iLonEnd + " }, "
                                        + "latitude blocks { " + iLatBeg + " ~ " + iLatEnd + " }."
                );

                int targetWidth = req.getWidth();
                int targetHeight = req.getHeight();
                StringBuilder source = new StringBuilder(200);
                Formatter formatter = new Formatter(source, Locale.US);

                // Our convention for dealing with missing data is that we'll use what ever might be
                // specified in the BGCOLOR parameter of the request, or a default of zero otherwise.

                Double bgColor = req.getBGColorAsDouble();
                short missingColor = (short) ((double) bgColor);

                ArrayList<File> tiles = new ArrayList<File>();
                for (int iLon = iLonBeg; iLon <= iLonEnd; iLon++)
                {
                    for (int iLat = iLatBeg; iLat <= iLatEnd; iLat++)
                    {
                        // SRTM V4.1 tiles are 5 x 5 degress
                        // calculate tile's origin (upper left)
                        double lon = (5d * (iLon - 1d)) - 180d;
                        double lat = 60d - (5d * (iLat - 1d));

                        Sector tileSector = Sector.fromDegrees(lat - 5d, lat, lon, lon + 5);
                        Sector overlap = reqSector.intersection(tileSector);

                        if (overlap == null)
                        {
                            Logging.logger().finest(this.getThreadID()
                                                    + " Block " + iLon + "_" + iLat
                                                    + ": No overlap between " + reqSector.toString()
                                                    + " and tile " + tileSector.toString());
                            continue;
                        }

                        // Skip if we intersect exactly at an edge...
                        if (overlap.getDeltaLon().degrees <= 0. || overlap.getDeltaLat().degrees <= 0.)
                        {
                            Logging.logger().finest(this.getThreadID()
                                                    + ": skipping - intersecting at an edge: " + overlap.toString());
                            continue;
                        }

                        // compute name of SRTM3 tile...
                        source.setLength(0);
                        formatter.format(ElevationSrtm3V4.this.filenaming_format, // "%s%ssrtm_%02d_%02d.tif"
                                rootDir.getAbsolutePath(), File.separator, iLon, iLat);

                        File sourceFile = new File(source.toString());
                        if (!sourceFile.exists())
                        {
                            Logging.logger().severe(this.getThreadID()
                                                    + source.toString() + " does NOT exists in " + rootDir.getAbsolutePath());
                            continue;
                        }

                        tiles.add(sourceFile);
                    }
                }

                if (tiles.size() > 0)
                {
                    File[] sourceFiles = new File[tiles.size()];
                    sourceFiles = (File[]) (tiles.toArray(sourceFiles));

                    raster = Mosaicer.mosaicElevations(this.getThreadID(),
                            sourceFiles, reqSector,
                            req.getWidth(), req.getHeight(),
                            ElevationSrtm3V4.SRTM3_NODATA_FLAG,
                            missingColor,
                            ("application/bil32".equals(req.getFormat())) ? "Float32" : "Int16"
                    );

//                    targetImage.copyFrom( sourceImage, missingColor );
                }

                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report("SRTM3_V4", tiles.size(), ellapsed);
                Logging.logger().info(this.getThreadID()
                                      + "DONE " + tiles.size() + " tiles in " + ellapsed + " msec. " + Stats.getStats("SRTM3_V4"));
            }
            catch (Exception ex)
            {
                String s = this.getThreadID() + "request failed: " + ex.toString();
                Logging.logger().severe(s);
                throw new WMSServiceException(s);
            }

            return raster;
        }

        public void freeResources()
        {
            // NO-OP
        }
    }

    public static int getLongitudeIndex(Angle lon)
    {
        if (null == lon)
        {
            String message = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double deg = lon.getDegrees();
        deg = (deg < -180d) ? -180d : ((deg > 180d) ? 180d : deg);
//        if(d < -180d || d > 180d )
//        {
//            String message = Logging.getMessage("generic.LongitudeOutOfRange", d );
//            Logging.logger().severe(message);
//            throw new IllegalArgumentException(message);
//        }
        return (int) ((180d + deg) / 5d + 1d);
    }

    public static int getLatitudeIndex(Angle lat)
    {
        if (null == lat)
        {
            String message = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        double deg = lat.getDegrees();
        deg = (deg < -60d) ? -60d : ((deg > 60d) ? 60d : deg);
//        if(d < -60d || d > 60d )
//        {
//            String message = Logging.getMessage("generic.LatitudeOutOfRange", d );
//            Logging.logger().severe(message);
//            throw new IllegalArgumentException(message);
//        }
        return (int) ((60d - deg) / 5d + 1d);
    }

    private static final String[] CRS = {"EPSG:4326"};
    private static final Sector BBOX = Sector.fromDegrees(-60d, 60d, -180d, 180d);
    public static final short SRTM3_NODATA_FLAG = -32768;

    private File rootDir;

    private static final String PROPERTY_FILENAMING_FORMAT = "filenaming_format";
    public static String DEFAULT_FILENAMING_FORMAT = "%s%ssrtm_%02d_%02d.tif";
    private String filenaming_format = ElevationSrtm3V4.DEFAULT_FILENAMING_FORMAT;
}
