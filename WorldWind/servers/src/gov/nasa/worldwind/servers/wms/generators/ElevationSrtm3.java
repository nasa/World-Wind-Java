/* Copyright (C) 2001, 2008 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.formats.tiff.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.tools.gdal.ReadWriteFormat;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.data.DataRaster;

import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * @author brownrigg
 * @version $Id$
 */

public class ElevationSrtm3 extends AbstractElevationGenerator
{
    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        this.setThreadId("Srtm3: ");
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
            Logging.logger().fine(this.getThreadId() + "data directory set to " + this.rootDir.getAbsolutePath());

            Properties props = mapSource.getProperties();
            if (props == null)
                throw new IllegalArgumentException("Missing properties in configuration for MapSource: "
                    + mapSource.getName());

            this.filenaming_format = this.getProperty(props,
                ElevationSrtm3.PROPERTY_FILENAMING_FORMAT,
                ElevationSrtm3.DEFAULT_FILENAMING_FORMAT,
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
                sb.append(ex.getCause().getMessage());
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
        // Original pixel size 0.000833333333 (1/120)
        double pixelSize = this.mapSource.getScaleHintMax();
        return (pixelSize != 0d) ? pixelSize : (1d / 1200d);
    }

    public String[] getCRS()
    {
        return CRS;
    }

    public class Srtm3ServiceInstance extends AbstractServiceInstance
    {
        private String threadID = "SRTM_3_V2";

        public String getThreadID()
        {
            return this.threadID;
        }

        public Srtm3ServiceInstance()
        {
            super();
            this.threadID = new StringBuffer("SRTM_3_V2").append(" (").append(Thread.currentThread().getId()).append(
                "): ").toString();
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster raster = this.doServiceRequest(req);
            if (null == raster)
                raster = Mosaicer.createCompatibleDataRaster(req);
            return new DataRasterFormatter(raster);
        }

        private DataRaster doServiceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster raster = null;
            long begTime = System.currentTimeMillis();

            Logging.logger().finest(ElevationSrtm3.this.getThreadId() + "processing service request ...");

            try
            {
                // include neighboor elevation pixels
                Sector reqSector = req.getExtentForElevationRequest();

                int iLonBeg = (int) (reqSector.getMinLongitude().degrees - 1);
                int iLonEnd = (int) (reqSector.getMaxLongitude().degrees + 1);
                int iLatBeg = (int) (reqSector.getMinLatitude().degrees - 1);
                int iLatEnd = (int) (reqSector.getMaxLatitude().degrees + 1);

                int targetWidth = req.getWidth();
                int targetHeight = req.getHeight();
                StringBuilder source = new StringBuilder(200);
                Formatter formatter = new Formatter(source, Locale.US);

                // Our convention for dealing with missing data is that we'll use what ever might be
                // specified in the BGCOLOR parameter of the request, or a default of zero otherwise.
                short missingColor = DEFAULT_MISSING_DATA_COLOR;
                try
                {
                    String bgColorStr = req.getBGColor();
                    if (bgColorStr != null)
                        missingColor = Short.parseShort(req.getBGColor());
                }
                catch (Exception ex)
                {
                    Logging.logger().finest("Unable to parse BGCOLOR in SRTM3 request: " + req.getBGColor());
                }

                ArrayList<File> tiles = new ArrayList<File>();
                for (int iLon = iLonBeg; iLon <= iLonEnd; iLon++)
                {
                    for (int iLat = iLatBeg; iLat <= iLatEnd; iLat++)
                    {

                        Sector tileSector = Sector.fromDegrees(iLat, iLat + 1, iLon, iLon + 1);
                        Sector overlap = reqSector.intersection(tileSector);
                        if (overlap == null)
                        {
//                            Logging.logger().info( threadId + "No overlap between " + reqSector.toString() + " and tile " + tileSector.toString() );
                            continue;
                        }

                        // Skip if we intersect exactly at an edge...
                        if (overlap.getDeltaLon().degrees <= 0. || overlap.getDeltaLat().degrees <= 0.)
                        {
//                            Logging.logger().info( threadId + ": skipping - intersecting at an edge: " + overlap.toString() );
                            continue;
                        }

                        // compute name of SRTM3 tile...
                        source.setLength(0);
                        formatter.format(ElevationSrtm3.this.filenaming_format, // "%s%s%s%02d%s%03d_fill.tif"
                            rootDir.getAbsolutePath(), File.separator,
                            ((iLat >= 0) ? "N" : "S"), Math.abs(iLat),
                            ((iLon <= 0.) ? "W" : "E"), Math.abs(iLon));

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
                        ElevationSrtm3.SRTM3_NODATA_FLAG,
                        missingColor,
                        ("application/bil32".equals(req.getFormat())) ? "Float32" : "Int16"
                    );
//                    targetImage.copyFrom( sourceImage, missingColor );
                }
                // targetImage.treatMissingData( NED_MISSING_DATA_FLAG, missingColor );

                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report("SRTM3", tiles.size(), ellapsed);
                Logging.logger().info(
                    this.getThreadID() + "DONE " + tiles.size() + " tiles in " + ellapsed + " msec. " + Stats.getStats(
                        "SRTM3"));
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

    private static final String[] CRS = {"EPSG:4326"};
    private static final Sector BBOX = Sector.fromDegrees(-56d, 60d, -180d, 180d);
    public static final short DEFAULT_MISSING_DATA_COLOR = 0;
    public static final short SRTM3_NODATA_FLAG = -32768;

    private File rootDir;

    private static final String PROPERTY_FILENAMING_FORMAT = "filenaming_format";
    public static String DEFAULT_FILENAMING_FORMAT = "%s%s%s%02d%s%03d.tif";
    private String filenaming_format = ElevationSrtm3.DEFAULT_FILENAMING_FORMAT;
}