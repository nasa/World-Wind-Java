/* Copyright (C) 2001, 2008 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.servers.wms.IMapRequest;
import gov.nasa.worldwind.servers.wms.MapSource;
import gov.nasa.worldwind.servers.wms.WMSServiceException;
import gov.nasa.worldwind.servers.wms.formats.DataRasterFormatter;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author brownrigg
 * @version $Id$
 */

public class ElevationSrtm30 extends AbstractElevationGenerator
{
    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success = true;  // Assume the best...

        this.setThreadId("SRTM30: ");

        try
        {
            this.mapSource = mapSource;
            this.rootDir = new File(mapSource.getRootDir());
        }
        catch (Exception ex)
        {
            success = false;
            Logging.logger().severe(this.getThreadId() + ex.getMessage());
        }

        return success;
    }

    public ServiceInstance getServiceInstance()
    {
        return new Srtm30ServiceInstance();
    }

    public Sector getBBox()
    {
        return BBOX;
    }

    public double getPixelSize()
    {
        //  Original pixel size 0.0083333333333
        double pixelSize = this.mapSource.getScaleHintMax();
        return (pixelSize != 0d) ? pixelSize : (1d / 120d);
    }


    public String[] getCRS()
    {
        return CRS;
    }

    public class Srtm30ServiceInstance extends AbstractServiceInstance
    {
        private String threadID = "SRTM_30";

        public String getThreadID()
        {
            return this.threadID;
        }

        public Srtm30ServiceInstance()
        {
            super();
            this.threadID = new StringBuffer("SRTM_30").append(" (").append(Thread.currentThread().getId()).append("): ").toString();
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster raster = this.doServiceRequest(req);
            if (null == raster)
            {
                String reason = Logging.getMessage("generic.CannotCreateRaster", req.getExtent());
                String message = Logging.getMessage("WMS.Server.InternalError", reason);
                Logging.logger().severe(message);
                throw new WMSServiceException(message);
            }

            return new DataRasterFormatter(raster);
        }

        private DataRaster doServiceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster raster = null;

            long begTime = System.currentTimeMillis();

            Logging.logger().finest(ElevationSrtm30.this.getThreadId() + "processing service request ...");

            try
            {
                ArrayList<File> tiles = new ArrayList<File>();

                // Our convention for dealing with missing data is that we'll use what ever might be
                // specified in the BGCOLOR parameter of the request, or a default of zero otherwise.
                Double bgColor = req.getBGColorAsDouble();
                short missingColor = (short) ((double) bgColor);

                // include neighboor elevation pixels
                Sector reqSector = req.getExtentForElevationRequest();

                for (String tileName : ElevationSrtm30.srtm30_tiles.keySet())
                {
                    Sector tileSector = ElevationSrtm30.srtm30_tiles.get(tileName);
                    Sector overlap = reqSector.intersection(tileSector);
                    if (null == overlap
                        || Math.abs(overlap.getDeltaLon().degrees) <= 0d
                        || Math.abs(overlap.getDeltaLat().degrees) <= 0d
                            )
                    {
                        continue;
                    }

                    String source = rootDir.getAbsolutePath() + File.separator + tileName + ".Bathmetry.tif";
                    File sourceFile = new File(source);
                    if (!sourceFile.exists())
                    {
                        Logging.logger().severe(this.getThreadID() + source + "does NOT exists in " + rootDir.getAbsolutePath());
                        continue;
                    }

                    tiles.add(sourceFile);
                }

                if (tiles.size() > 0)
                {
                    File[] sourceFiles = new File[tiles.size()];
                    sourceFiles = (File[]) (tiles.toArray(sourceFiles));

                    raster = Mosaicer.mosaicElevations(this.getThreadID(),
                            sourceFiles, reqSector,
                            req.getWidth(), req.getHeight(),
                            ElevationSrtm30.SRTM30_NODATA_FLAG,
                            missingColor,
                            ("application/bil32".equals(req.getFormat())) ? "Float32" : "Int16"
                    );
                }
                // targetImage.treatMissingData( NED_MISSING_DATA_FLAG, missingColor );

                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report("SRTM30", tiles.size(), ellapsed);
                Logging.logger().info(this.getThreadID() + "DONE " + tiles.size() + " tiles in " + ellapsed + " msec. " + Stats.getStats("SRTM30"));
            }
            catch (Exception ex)
            {
                String s = this.getThreadID() + "request failed: " + WWUtil.extractExceptionReason(ex);
                ex.printStackTrace();
                Logging.logger().severe(s);
                throw new WMSServiceException(s);
            }

            return raster;
        }

        //
        // Attempts to return the specified image as a BufferedImage. Returns null on failure.
        //

        public void freeResources()
        {
            // NO-OP
        }
    }

    private MapSource mapSource;
    private File rootDir;

    private static final String[] CRS = {"EPSG:4326"};
    private static final Sector BBOX = Sector.FULL_SPHERE;

    private static final short DEFAULT_MISSING_DATA_COLOR = 0;
    private static final short SRTM30_NODATA_FLAG = -32768;


    private static final TilesMap srtm30_tiles = new TilesMap();

    private static class TilesMap extends HashMap<String, Sector>
    {
        public TilesMap()
        {
            put("w180n90", Sector.fromDegrees(40, 90, -180, -140));
            put("w140n90", Sector.fromDegrees(40, 90, -140, -100));
            put("w100n90", Sector.fromDegrees(40, 90, -100, -60));
            put("w060n90", Sector.fromDegrees(40, 90, -60, -20));
            put("w020n90", Sector.fromDegrees(40, 90, -20, 20));
            put("e020n90", Sector.fromDegrees(40, 90, 20, 60));
            put("e060n90", Sector.fromDegrees(40, 90, 60, 100));
            put("e100n90", Sector.fromDegrees(40, 90, 100, 140));
            put("e140n90", Sector.fromDegrees(40, 90, 140, 180));
            put("w180n40", Sector.fromDegrees(-10, 40, -180, -140));
            put("w140n40", Sector.fromDegrees(-10, 40, -140, -100));
            put("w100n40", Sector.fromDegrees(-10, 40, -100, -60));
            put("w060n40", Sector.fromDegrees(-10, 40, -60, -20));
            put("w020n40", Sector.fromDegrees(-10, 40, -20, 20));
            put("e020n40", Sector.fromDegrees(-10, 40, 20, 60));
            put("e060n40", Sector.fromDegrees(-10, 40, 60, 100));
            put("e100n40", Sector.fromDegrees(-10, 40, 100, 140));
            put("e140n40", Sector.fromDegrees(-10, 40, 140, 180));
            put("w180s10", Sector.fromDegrees(-60, -10, -180, -140));
            put("w140s10", Sector.fromDegrees(-60, -10, -140, -100));
            put("w100s10", Sector.fromDegrees(-60, -10, -100, -60));
            put("w060s10", Sector.fromDegrees(-60, -10, -60, -20));
            put("w020s10", Sector.fromDegrees(-60, -10, -20, 20));
            put("e020s10", Sector.fromDegrees(-60, -10, 20, 60));
            put("e060s10", Sector.fromDegrees(-60, -10, 60, 100));
            put("e100s10", Sector.fromDegrees(-60, -10, 100, 140));
            put("e140s10", Sector.fromDegrees(-60, -10, 140, 180));
            put("w180s60", Sector.fromDegrees(-90, -60, -180, -120));
            put("w120s60", Sector.fromDegrees(-90, -60, -120, -60));
            put("w060s60", Sector.fromDegrees(-90, -60, -60, 0));
            put("w000s60", Sector.fromDegrees(-90, -60, 0, 60));
            put("e060s60", Sector.fromDegrees(-90, -60, 60, 120));
            put("e120s60", Sector.fromDegrees(-90, -60, 120, 180));
        }
    }
}