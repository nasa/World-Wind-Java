/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.servers.tools.gdal.*;
import gov.nasa.worldwind.servers.tools.utm.*;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.util.Logging;

import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * @author garakl
 * @version $Id$
 */
public class ScankortElevationsLayer extends AbstractElevationGenerator
{
    public ScankortElevationsLayer()
    {
        super();
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

            Logging.logger().fine("ScankortElevationsLayer: initializing with mapSource: " + mapSource.getName());

            // Extract expected properties that should have been set in our MapSource
            // configuration...
            Properties myProps = mapSource.getProperties();
            if (myProps == null)
                throw new IllegalArgumentException("Missing properties in configuration for MapSource: "
                    + mapSource.getServiceClass().getName());

            this.rootDir = new File(mapSource.getRootDir());
            if (!this.rootDir.exists())
            {
                success = false;
                String msg = Logging.getMessage("generic.FolderDoesNotExist", mapSource.getRootDir());
                Logging.logger().severe(msg);
                throw new FileNotFoundException(msg);
            }
            Logging.logger().fine("ScankortElevationsLayer: data directory set to " + this.rootDir.getAbsolutePath());

            try
            {
                this.default_missing_data_signal = (short) Double.parseDouble(
                    this.getMapSource().getMissingDataSignal());
            }
            catch (Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }
            Logging.logger().finest(
                "ScankortElevationsLayer: default_missing_data_signal = " + this.default_missing_data_signal);

            try
            {
                this.missing_data_replacement = (short) Double.parseDouble(
                    this.getMapSource().getMissingDataReplacement());
            }
            catch (Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }
            Logging.logger().finest(
                "ScankortElevationsLayer: missing_data_replacement = " + this.missing_data_replacement);

            this.utm_tile_overlap = this.getProperty(myProps, "utm_tile_overlap", this.utm_tile_overlap,
                mapSource.getName());
            Logging.logger().finest("ScankortElevationsLayer: utm_tile_overlap= " + this.utm_tile_overlap);

            this.filenaming_format = this.getProperty(myProps, PROPERTY_FILENAMING_FORMAT, DEFAULT_FILENAMING_FORMAT,
                mapSource.getName());
            Logging.logger().finest(
                "ScankortElevationsLayer: " + PROPERTY_FILENAMING_FORMAT + "=" + this.filenaming_format);

            double min_lon, max_lon, min_lat, max_lat;
            min_lat = this.getProperty(myProps, "coverage_min_latitude", 0d, mapSource.getName());
            max_lat = this.getProperty(myProps, "coverage_max_latitude", 0d, mapSource.getName());
            min_lon = this.getProperty(myProps, "coverage_min_longitude", 0d, mapSource.getName());
            max_lon = this.getProperty(myProps, "coverage_max_longitude", 0d, mapSource.getName());

            this.offset_width = this.getProperty(myProps, "offset_width", 0d, mapSource.getName());
            this.offset_height = this.getProperty(myProps, "offset_height", 0d, mapSource.getName());

            this.BBOX = Sector.fromDegrees(min_lat, max_lat, min_lon, max_lon);
            Logging.logger().fine("ScankortElevationsLayer: coverage = " + this.BBOX.toString());

            success = true;
        }
        catch (Exception ex)
        {
            String msg = Logging.getMessage("WMS.MapGenerator.CannotInstantiate", ex.getMessage());
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
            // throw new WMSServiceException( msg );
            success = false;
        }

        return success;
    }

    public Sector getBBox()
    {
        return this.BBOX;
    }

    public boolean hasCoverage(Sector sector)
    {
        // check first bigger bounding box
        return (null != sector && sector.intersects(this.getBBox()));
    }

    public double getPixelSize()
    {
        return this.mapSource.getScaleHintMax();
    }

    public String[] getCRS()
    {
        return new String[] {crsStr};
    }

    private class ScankortServiceInstance extends AbstractServiceInstance
    {
        private boolean intersects(Sector a, Sector b)
        {
            if (null != a && null != b)
            {
                Sector overlap = a.intersection(b);
                if (overlap != null
                    && overlap.getDeltaLon().degrees > 0d
                    && overlap.getDeltaLat().degrees > 0d
                    )
                {
                    return true;
                }
            }
            return false;
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            long begTime = System.currentTimeMillis();

            ScankortElevationsLayer.this.setThreadId("Scankort");

            Logging.logger().finest(ScankortElevationsLayer.this.getThreadId() + "processing service request ...");

            int reqWidth = 150, reqHeight = 150;
            BILImageFormatter formatter = null;

            AVList reqParams = new AVListImpl();
            reqParams.setValue(AVKey.BYTE_ORDER, AVKey.LITTLE_ENDIAN);
//            reqParams.setValue( AVKey.FILE_STORE_LOCATION, BILGenerator.this.descriptor.getValue(AVKey.FILE_STORE_LOCATION));
//            reqParams.setValue( AVKey.DATASET_NAME, BILGenerator.this.descriptor.getValue(AVKey.DATASET_NAME));
//            reqParams.setValue( AVKey.DATA_CACHE_NAME, BILGenerator.this.descriptor.getValue(AVKey.DATA_CACHE_NAME));

            short missingColor = ScankortElevationsLayer.this.default_missing_data_signal;
            try
            {
                String bgColorStr = req.getBGColor();
                if (bgColorStr != null)
                    missingColor = Short.parseShort(req.getBGColor());
            }
            catch (Exception ex)
            {
                //                    Logging.logger().finest("Unable to parse BGCOLOR in Scankort request: " + req.getBGColor());
            }
            reqParams.setValue(AVKey.MISSING_DATA_REPLACEMENT, (double) missingColor);

            ArrayList<File> qquads = new ArrayList<File>();
            try
            {
//                Sector reqSector = this.adjustElevationOffset( req.getExtent() );
//
//                if(    0d != ScankortElevationsLayer.this.offset_width
//                    || 0d != ScankortElevationsLayer.this.offset_height )
//                {
//                    reqSector = this.adjustElevationOffset( reqSector );
//                }

                Sector reqSector = this.adjustElevationOffset(req.getExtentForElevationRequest());

                reqParams.setValue(AVKey.SECTOR, reqSector);

                reqWidth = (req.getWidth() > 0) ? req.getWidth() : reqWidth;
                reqParams.setValue(AVKey.TILE_WIDTH, reqWidth);

                reqHeight = (req.getHeight() > 0) ? req.getHeight() : reqHeight;
                reqParams.setValue(AVKey.TILE_HEIGHT, reqHeight);

                String pixelType = req.getFormat();
                if (null == pixelType || pixelType.length() == 0)
                {
                    Logging.logger().finest(ScankortElevationsLayer.this.getThreadId()
                        + "default .BIL (Float32) type is used");
                    reqParams.setValue(AVKey.DATA_TYPE, AVKey.FLOAT32);
                }
                else
                {
                    if ("application/bil32".equals(pixelType))
                        reqParams.setValue(AVKey.DATA_TYPE, AVKey.FLOAT32);
                    else if ("application/bil16".equals(pixelType))
                        reqParams.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                    else if ("application/bil".equals(pixelType))
                        reqParams.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                    else if ("image/bil".equals(pixelType))
                        reqParams.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                    else
                        throw new WMSServiceException("Unknown or unsupported format - " + pixelType);
                }

                if (!ScankortElevationsLayer.this.hasCoverage(reqSector))
                    throw new WMSServiceException("Scankort: Out of coverage! Skipping.");

                double texelSize = (reqSector.getDeltaLatDegrees() / (double) reqHeight);
                Logging.logger().fine("Scankort: req.sector = " + reqSector.toString()
                    + ", latitude delta = " + reqSector.getDeltaLatDegrees()
                    + ", req pixel size = " + texelSize
                    + ", missing color = " + missingColor);

                if (texelSize > ScankortElevationsLayer.this.getMapSource().getScaleHintMin())
                    throw new WMSServiceException("Scankort: req.sector is too big. Skipping. " + texelSize);

                UTMSector reqSectorUTM = UTMSector.fromSector(reqSector);

                // round to the nearest 1000m
                int minx = TILE_WIDTH_IN_METERS * ((int) (reqSectorUTM.getMinEasting()
                    / (double) TILE_WIDTH_IN_METERS));
                int maxx = TILE_WIDTH_IN_METERS * ((int) (reqSectorUTM.getMaxEasting()
                    / (double) TILE_WIDTH_IN_METERS));
                int miny = TILE_HEIGHT_IN_METERS * ((int) (reqSectorUTM.getMinNorthing()
                    / (double) TILE_HEIGHT_IN_METERS));
                int maxy = TILE_HEIGHT_IN_METERS * ((int) (reqSectorUTM.getMaxNorthing()
                    / (double) TILE_HEIGHT_IN_METERS));

                for (int x = minx; x <= maxx; x += TILE_WIDTH_IN_METERS)
                {
                    for (int y = miny; y <= maxy; y += TILE_HEIGHT_IN_METERS)
                    {
                        try
                        {
                            File file = this.crop(reqSectorUTM, x, y, reqWidth, reqHeight);
                            if (null != file)
                                qquads.add(file);
                        }
                        catch (Exception ex)
                        {
                            Logging.logger().log(java.util.logging.Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }

                Logging.logger().finest("DEBUG: found intesection with " + qquads.size() + " tiles");

//                this.makeNoDataTransparentAndAutoContrast( reqImage, missingColor );
//                formatter = new BufferedImageFormatter(reqImage);

//              if( qquads.size() > MAX_QUADS_PER_REQUEST )
//              {
//                  throw new WMSServiceException( "Too many tiles requested." );
//              }

                if (qquads.size() > 0)
                {
                    File[] sourceFiles = new File[qquads.size()];
                    sourceFiles = (File[]) (qquads.toArray(sourceFiles));

                    long startTime = System.currentTimeMillis();

                    ByteBuffer bilImage = this.mosaic(
                        sourceFiles,
                        reqSector,
                        req.getWidth(),
                        req.getHeight(),
                        ScankortElevationsLayer.this.missing_data_replacement,
                        missingColor,
                        (0 == AVKey.FLOAT32.compareTo((String) reqParams.getValue(AVKey.DATA_TYPE)) ? "Float32"
                            : "Int16")
                    );

                    long ellapsed = System.currentTimeMillis() - startTime;
                    Stats.report("GDAL_WARP", qquads.size(), ellapsed);
                    Logging.logger().finest(Stats.getStats("GDAL_WARP"));

                    if (null != bilImage
                        && (0 == AVKey.FLOAT32.compareTo((String) reqParams.getValue(AVKey.DATA_TYPE)))
                        )
                    {
                        this.removeElevationSpikes(
                            ((ByteBuffer) bilImage.rewind()).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer(),
                            req.getWidth(), req.getHeight()
                        );

                        formatter = new BILImageFormatter(req.getWidth(), req.getHeight(), bilImage);
                    }
                }
            }
            catch (Exception ex)
            {
                String msg = Logging.getMessage("WMS.RequestFailed", ex.getMessage());
                Logging.logger().log(java.util.logging.Level.SEVERE, ScankortElevationsLayer.this.getThreadId(), ex);
//                throw new WMSServiceException( msg );
            }
            finally
            {
                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report("Scankort", qquads.size(), ellapsed);
                Logging.logger().fine(
                    ScankortElevationsLayer.this.getThreadId() + "DONE " + qquads.size() + " tiles in " + ellapsed
                        + " msec. " + Stats.getStats("Scankort"));
            }

            if (null == formatter)
            {
                formatter = new BILImageFormatter((ByteBufferRaster) this.createDataRaster(reqParams));
            }
            return formatter;
        }

        public Sector adjustElevationOffset(Sector orgReqSector)
        {
            LatLon[] org_corners = orgReqSector.getCorners();

            ArrayList<LatLon> new_corners = new ArrayList<LatLon>();
            for (LatLon ll : org_corners)
            {
                UTMCoords utm = UTMCoords.fromLatLon(ll);
                utm.shift(ScankortElevationsLayer.this.offset_width, ScankortElevationsLayer.this.offset_height);

                new_corners.add(utm.getLatLon());
            }

            return Sector.boundingSector(new_corners);
        }

        public void removeElevationSpikes(FloatBuffer buf, int width, int height)
            throws IllegalArgumentException
        {
            if (null == buf)
            {
                String message = Logging.getMessage("nullValue.BufferIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            int size = buf.capacity();
            if (width <= 0 || height <= 0 || size != width * height)
            {
                String message = Logging.getMessage("generic.InvalidImageSize", width, height);
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            buf.rewind();

            int idx = 0;

            for (int i = 0; i < size; i++)
            {
                float original = buf.get(i);

                if (Math.abs(original) <= 10f)
                    continue;

                double interpolated_sum = 0d;
                int interpolated_count = 0;

                idx = i - width - 1;
                if (idx >= 0 && idx < size)
                {
                    interpolated_sum += buf.get(idx);
                    interpolated_count++;
                }
                idx = i - width + 1;
                if (idx >= 0 && idx < size)
                {
                    interpolated_sum += buf.get(idx);
                    interpolated_count++;
                }
                idx = i + width - 1;
                if (idx >= 0 && idx < size)
                {
                    interpolated_sum += buf.get(idx);
                    interpolated_count++;
                }
                idx = i + width + 1;
                if (idx >= 0 && idx < size)
                {
                    interpolated_sum += buf.get(idx);
                    interpolated_count++;
                }
                if (interpolated_count > 0)
                {
                    float expected = (float) ((double) (interpolated_sum / (double) interpolated_count));
                    if (Math.abs(original) > Math.abs(10f * expected))
                    {
                        buf.put(i, expected);
                        Logging.logger().severe("[!!!] ScankortElevations: spike " + original
                            + " is replaced with " + expected);
                    }
                }
            }
            buf.rewind();
        }

        protected DataRaster createDataRaster(AVList params)
        {
            // Create a BIL elevation raster to hold the tile's data.
            AVList bufferParams = params.copy();
            ByteBufferRaster bufferRaster = new ByteBufferRaster(
                (Integer) params.getValue(AVKey.TILE_WIDTH),
                (Integer) params.getValue(AVKey.TILE_HEIGHT),
                (Sector) params.getValue(AVKey.SECTOR),
                bufferParams
            );

            // Clear the raster with the missing data replacment.
            // This code expects the string "gov.nasa.worldwind.avkey.MissingDataValue", which now corresponds to the key
            // MISSING_DATA_REPLACEMENT.
            Object o = params.getValue(AVKey.MISSING_DATA_REPLACEMENT);
            if (o != null && o instanceof Double)
            {
                Double missingDataValue = (Double) o;
                bufferRaster.fill(missingDataValue);
                bufferRaster.setTransparentValue(missingDataValue);
            }

            return bufferRaster;
        }

        private File crop(UTMSector reqSector, int tile_x, int tile_y, int reqWidth_px, int reqHeight_px)
        {
            File sourceFile = null;
            try
            {
                UTMSector utm_tile = new UTMSector(tile_x, tile_x + TILE_WIDTH_IN_METERS,
                    tile_y, tile_y + TILE_HEIGHT_IN_METERS, reqSector.getZone());

                UTMSector overlap = reqSector.intersection(utm_tile);
                if (null == overlap)
                    return null;

                int minx = (int) overlap.getMinEasting();
                int maxx = (int) overlap.getMaxEasting();
                int miny = (int) overlap.getMinNorthing();
                int maxy = (int) overlap.getMaxNorthing();

                // ignore if overlap only on edges
                if (minx == maxx || miny == maxy)
                    return null;

                StringBuilder source = new StringBuilder(1024);
                Formatter formatter = new Formatter(source, Locale.US);
                formatter.format(ScankortElevationsLayer.this.filenaming_format,
                    rootDir.getAbsolutePath(),
                    File.separator,
//                        (int)(tile_y/100000), (int)(tile_x/100000),
//                        File.separator,
                    (int) (tile_y / 10000), (int) (tile_x / 10000),
                    File.separator,
                    (int) (tile_y / 1000), (int) (tile_x / 1000)
                );

                sourceFile = new File(source.toString());
                if (!sourceFile.exists())
                {
                    Logging.logger().severe(ScankortElevationsLayer.this.getThreadId()
                        + source.toString() + " does NOT exists in " + rootDir.getAbsolutePath());
                    return null;
                }
            }
            catch (Exception ex)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, ScankortElevationsLayer.this.getThreadId(), ex);
            }
            return sourceFile;
        }

        private java.nio.ByteBuffer mosaic(File[] sourceFiles,
            Sector extent, int width, int height,
            short srcNoData, short destNoData, String outType)
        {
            java.nio.ByteBuffer bilImage = null;
            File tmpFile = TempFile.getTempFile();
            try
            {
                GDALUtils gdal = GDALUtils.getGDAL();
                gdal.warp(ScankortElevationsLayer.this.getThreadId(),
                    Option.Warp.Resampling.Bilinear,
                    new String[] {
//                                "--config", "GDAL_CACHEMAX", "1024",
//                                "-wm", "1024",
//                                "--debug", "ON",
//                            "-q",
                        "-ot", outType,
                        "-srcnodata", String.valueOf(srcNoData),
                        "-dstnodata", String.valueOf(destNoData),
                        "-s_srs", "+proj=utm +zone=32 +datum=WGS84 +no_defs",
//                             "-t_srs", "EPSG:4326",
                        "-t_srs", "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs"
                    },
                    sourceFiles,
                    extent, width, height,
                    ReadWriteFormat.ENVI,
                    tmpFile
                );

                bilImage = this.readFileToBuffer(tmpFile);
            }
            catch (Exception ex)
            {
                String msg = ScankortElevationsLayer.this.getThreadId() + ex.toString();
                Logging.logger().severe(msg);
            }
            finally
            {
                // delete temporary .HDR file
                if (tmpFile.exists())
                {
                    try
                    {
                        new File(tmpFile.getAbsolutePath() + ".hdr").delete();
                    }
                    catch (Exception ignore)
                    {
                    }
                    try
                    {
                        tmpFile.delete();
                    }
                    catch (Exception ignore)
                    {
                    }
                }
            }
            return bilImage;
        }

        private java.nio.ByteBuffer readFileToBuffer(java.io.File file) throws IOException
        {
            FileInputStream is = new FileInputStream(file);
            try
            {
                FileChannel fc = is.getChannel();
                java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate((int) fc.size());
                for (int count = 0; count >= 0 && buffer.hasRemaining();)
                {
                    count = fc.read(buffer);
                }
                buffer.flip();
                return buffer;
            }
            finally
            {
                is.close();
            }
        }

        public void freeResources()
        {
        }
    }

    private short default_missing_data_signal = 0;
    private short missing_data_replacement = -999;

    private int utm_tile_overlap = 50;

    private double offset_width = 0d;
    private double offset_height = 0d;

    private File rootDir = null;
    private Sector BBOX = Sector.FULL_SPHERE; // Sector.EMPTY_SECTOR;

    private static final String crsStr = "EPSG:4326";

    private static final short TILE_WIDTH_IN_METERS = 1000;
    private static final short TILE_WIDTH_IN_PIXELS = 625;
    private static final short TILE_HEIGHT_IN_METERS = 1000;
    private static final short TILE_HEIGHT_IN_PIXELS = 625;

    private static final String PROPERTY_FILENAMING_FORMAT = "filenaming_format";
    public static String DEFAULT_FILENAMING_FORMAT = "%s%s%03d_%02d%sDSM_1km_%04d_%03d.asc";
    private String filenaming_format = DEFAULT_FILENAMING_FORMAT;
}

