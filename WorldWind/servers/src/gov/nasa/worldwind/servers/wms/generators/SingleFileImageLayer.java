/* Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.formats.tiff.GeotiffReader;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.gdal.GDALUtils;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.tools.gdal.ReadWriteFormat;
import gov.nasa.worldwind.servers.wms.AVListMapSource;
import gov.nasa.worldwind.servers.wms.IMapRequest;
import gov.nasa.worldwind.servers.wms.MapSource;
import gov.nasa.worldwind.servers.wms.WMSServiceException;
import gov.nasa.worldwind.servers.wms.formats.BufferedImageFormatter;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class SingleFileImageLayer extends AbstractMapGenerator
{
    public static int MAX_GRAY_VALUE_8BITS = 255;
    public static int MAX_GRAY_VALUE_10BITS = 1023;
    public static int MAX_GRAY_VALUE_11BITS = 2047;
    public static int MAX_GRAY_VALUE_12BITS = 4095;
    public static int MAX_GRAY_VALUE_16BITS = 65535;

    protected AVListMapSource ms;
    protected File imageFile;

    public File getImageFile()
    {
        return this.imageFile;
    }

    public short getNodataReplacement()
    {
        return this.nodataReplacement;
    }

    public short getNodataSignal()
    {
        return this.nodataSignal;
    }

    protected short nodataSignal = 0;
    protected short nodataReplacement = 0;

    protected static final String[] CRS = {"EPSG:4326"};
    protected Sector BBOX = Sector.FULL_SPHERE; // Sector.EMPTY_SECTOR;

    public String getDataType()
    {
        return "imagery";
    }

    @Override
    public String getThreadId()
    {
        return this.ms.getName() + " (" + Thread.currentThread().getId() + "): ";
    }

    public SingleFileImageLayer()
    {
        super();
    }

    public ServiceInstance getServiceInstance()
    {
        return new SingleFileImageServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        if (!(mapSource instanceof AVListMapSource))
        {
            String msg = Logging.getMessage("generic.InvalidDataSource", mapSource.getName());
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.ms = (AVListMapSource) mapSource;
        this.mapSource = mapSource;

        this.imageFile = new File(this.ms.getStringValue(AVKey.FILE_NAME));
        if (!this.imageFile.exists())
        {
            String msg = Logging.getMessage("generic.FileNotFound", this.imageFile.getAbsolutePath());
            Logging.logger().severe(msg);
            throw new FileNotFoundException(msg);
        }

        Object obj = this.ms.getValue(AVKey.SECTOR);
        if (null == obj || !(obj instanceof Sector))
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }
        this.BBOX = (Sector) obj;

        if (0d == this.BBOX.getDeltaLatDegrees() || 0d == this.BBOX.getDeltaLonDegrees())
        {
            String msg = Logging.getMessage("generic.SectorSizeInvalid");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        if (!this.ms.hasValue(AVKey.HEIGHT) || !this.ms.hasValue(AVKey.WIDTH))
        {
            String msg = Logging.getMessage("generic.InvalidImageSize",
                    this.ms.getValue(AVKey.WIDTH), this.ms.getValue(AVKey.HEIGHT));
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        try
        {
            if (this.ms.hasValue(AVKey.MISSING_DATA_SIGNAL))
            {
                this.nodataSignal = (Short) this.ms.getValue(AVKey.MISSING_DATA_SIGNAL);
            }
        }
        catch (Exception ignore)
        {
            // TODO zz: garakl: log exception
        }

        try
        {
            if (this.ms.hasValue(AVKey.MISSING_DATA_REPLACEMENT))
            {
                this.nodataReplacement = (Short) this.ms.getValue(AVKey.MISSING_DATA_REPLACEMENT);
            }
        }
        catch (Exception ignore)
        {
            // TODO zz: garakl: log exception
        }

        return true;
    }

    public Sector getBBox()
    {
        return this.BBOX;
    }

    public boolean hasCoverage(Sector sector)
    {
        return (null != sector && sector.intersects(this.getBBox()));
    }

    public double getPixelSize()
    {
        return this.ms.getScaleHintMax();
    }

    public String[] getCRS()
    {
        return CRS;
    }

    public class SingleFileImageServiceInstance extends AbstractServiceInstance
    {
        private Sector intersects(Sector a, Sector b)
        {
            if (null != a && null != b)
            {
                Sector overlap = a.intersection(b);
                if (overlap != null
                    && overlap.getDeltaLon().degrees > 0d
                    && overlap.getDeltaLat().degrees > 0d
                        )
                {
                    return overlap;
                }
            }
            return null;
        }

        protected double calcPixelSizeOfRequestArea(IMapRequest req)
        {
            Sector reqSector = req.getExtent();
            double reqHeight = (double) ((req.getHeight() > 0) ? req.getHeight() : 512);
            return (reqSector.getDeltaLatDegrees() / reqHeight);
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            BufferedImageFormatter formatter = null;
            long begTime = System.currentTimeMillis();

            Logging.logger().finest(SingleFileImageLayer.this.getThreadId() + "processing service request ...");

            try
            {
                if (null == this.intersects(req.getExtent(), SingleFileImageLayer.this.getBBox()))
                {
                    String msg = Logging.getMessage("WMS.Layer.OutOfCoverage",
                            req.getExtent().toString() + " vs " + SingleFileImageLayer.this.getBBox().toString());
                    Logging.logger().severe(msg);
                    throw new WMSServiceException(msg);
                }

                if (req.getHeight() <= 0 || req.getWidth() <= 0)
                {
                    String msg = Logging.getMessage("generic.InvalidImageSize", req.getWidth(), req.getHeight());
                    Logging.logger().severe(msg);
                    throw new WMSServiceException(msg);
                }

                BufferedImage image = this.buildBufferedImage(req);
                if (null != image)
                {
                    formatter = new BufferedImageFormatter(image);
                }
            }
            catch (WMSServiceException wmsse)
            {
                throw wmsse;
            }
            catch (Exception ex)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE,
                        SingleFileImageLayer.this.getThreadId() + ex.getMessage(), ex);
                // throw new WMSServiceException( ex.getMessage() );
            }
            finally
            {
                long ellapsed = System.currentTimeMillis() - begTime;
                Logging.logger().fine(SingleFileImageLayer.this.getThreadId()
                                      + "Request with pixel size = " + this.calcPixelSizeOfRequestArea(req)
                                      + " completed in " + ellapsed + " msec. ");
            }

            if (null == formatter)
            {
                BufferedImage tmpImage = new BufferedImage(req.getWidth(), req.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                this.fill(tmpImage, this.extractNodataReplacementColor(req));
                formatter = new BufferedImageFormatter(tmpImage);
            }
            return formatter;
        }

        public BufferedImage buildBufferedImage(IMapRequest req) throws IOException, WMSServiceException
        {
            BufferedImage reqImage = null;

            try
            {
                File[] sourceFiles = new File[]{SingleFileImageLayer.this.getImageFile()};

                short desiredNodataColor = this.extractNodataReplacementColor(req);

                BufferedImage sourceImage = this.mosaic(
                        sourceFiles,
                        req.getExtent(),
                        req.getWidth(),
                        req.getHeight(),
                        SingleFileImageLayer.this.getNodataSignal(),
                        desiredNodataColor
                );


                if (null != sourceImage)
                {
                    if (sourceImage.getType() == BufferedImage.TYPE_4BYTE_ABGR)
                    {
                        reqImage = new BufferedImage(req.getWidth(), req.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

                        Graphics2D g2d = (Graphics2D) reqImage.getGraphics();
                        g2d.drawImage(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
                        g2d.dispose();
                    }
                    else if (sourceImage.getType() == BufferedImage.TYPE_USHORT_GRAY)
                    {
                        this.doAutoContrastUInt16Gray(sourceImage);
                        reqImage = sourceImage;
                    }
                    else
                    {
                        reqImage = sourceImage;
                    }

                    this.makeNoDataTransparent(reqImage, desiredNodataColor);
                }
            }
            catch (Exception ex)
            {
                String s = SingleFileImageLayer.this.getThreadId() + "request failed: " + ex.toString();
                Logging.logger().severe(s);
                // throw new WMSServiceException( s );
            }

            return reqImage;
        }


        private short extractNodataReplacementColor(IMapRequest req)
        {
            // original image may contain a "no data" (or aka "missing data signal" ),
            // which is usually "0" for images (for elevations: "-32768", "-32767", or "-9999")

            // Initial value does not matter, what matter is what WWJ client requested in the
            // WMS GetMap request in the "BGCOLOR" variable

            short desiredNodataColor = SingleFileImageLayer.this.getNodataReplacement();
            try
            {
                String bgColorStr = req.getBGColor();
                if (bgColorStr != null)
                {
                    desiredNodataColor = Short.parseShort(req.getBGColor());
                }
            }
            catch (Exception ignore)
            {
                desiredNodataColor = SingleFileImageLayer.this.getNodataReplacement();
            }
            return desiredNodataColor;
        }

        private void fill(BufferedImage image, int color)
        {
            WritableRaster raster = null;

            if (null != image
                && image.getType() == BufferedImage.TYPE_4BYTE_ABGR
                && null != (raster = image.getRaster())
                    )
            {
                int color_r = ((color >> 16) & 0xff);
                int color_g = ((color >> 8) & 0xff);
                int color_b = ((color) & 0xff);

                int[] pixel = new int[]{color_r, color_g, color_b, 0};

                int width = image.getWidth();
                int height = image.getHeight();
                for (int j = 0; j < height; j++)
                {
                    for (int i = 0; i < width; i++)
                    {
                        raster.setPixel(i, j, pixel);
                    }
                }
            }
        }


        private void doAutoContrastUInt16Gray(BufferedImage image)
        {
            WritableRaster raster = null;

            if (null != image
                && image.getType() == BufferedImage.TYPE_USHORT_GRAY
                && null != (raster = image.getRaster())
                    )
            {
                int gray_scale_max = SingleFileImageLayer.MAX_GRAY_VALUE_16BITS;

                double[] hist = new double[gray_scale_max + 1];

                int width = image.getWidth();
                int height = image.getHeight();


                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        hist[0xFFFF & raster.getSample(x, y, 0 /*  band */)]++;
                    }
                }

                // normalize histogram table
                double size = (double) width * (double) height;
                for (int i = 0; i <= gray_scale_max; i++)
                {
                    hist[i] = hist[i] / size;
                }

                // Determine the high input value
                int low = 0;
                double next_percentage = hist[0];
                for (int i = 0; i < gray_scale_max; i++)
                {
                    double percentage = next_percentage;
                    next_percentage += hist[i + 1];
                    if (Math.abs(percentage - 0.006d) < Math.abs(next_percentage - 0.006d))
                    {
                        low = i;
                        break;
                    }
                }

                // Determine the high input value
                int high = 0;
                next_percentage = hist[gray_scale_max];
                for (int i = gray_scale_max; i > 0; i--)
                {
                    double percentage = next_percentage;
                    next_percentage += hist[i - 1];
                    if (Math.abs(percentage - 0.006d) < Math.abs(next_percentage - 0.006d))
                    {
                        high = i;
                        break;
                    }
                }

                // Turn the histogram into a look up table to stretch the values

                int[] lut = new int[gray_scale_max + 1];

                for (int i = 0; i < low; i++)
                {
                    lut[i] = 0;
                }

                for (int i = gray_scale_max; i > high; i--)
                {
                    lut[i] = 0xFFFF & gray_scale_max;
                }

                int mult = (int) (gray_scale_max / (high - low));
                int base = 0;
                for (int i = low; i <= high; i++)
                {
                    lut[i] = base;
                    base += mult;
                }

                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        int gray = 0xFFFF & raster.getSample(x, y, 0 /* band */);
                        raster.setSample(x, y, 0, 0xFFFF & lut[gray]);
                    }
                }
            }
        }


        private void makeNoDataTransparent(BufferedImage image, int missingColor)
        {
            WritableRaster raster = null;

            if (null != image
                && image.getType() == BufferedImage.TYPE_4BYTE_ABGR
                && null != (raster = image.getRaster())
                    )
            {
                int nodata_r = ((missingColor >> 16) & 0xff);
                int nodata_g = ((missingColor >> 8) & 0xff);
                int nodata_b = ((missingColor) & 0xff);

                int[] pixel = new int[4];
                int width = image.getWidth();
                int height = image.getHeight();
                for (int j = 0; j < height; j++)
                {
                    for (int i = 0; i < width; i++)
                    {
                        // We know, by the nature of this source, that we are dealing with RGBA rasters...
                        raster.getPixel(i, j, pixel);
                        if (pixel[0] == nodata_r && pixel[1] == nodata_g && pixel[2] == nodata_b)
                        {
                            pixel[3] = 0;
                            raster.setPixel(i, j, pixel);
                        }
                    }
                }
            }
        }


        private BufferedImage mosaic(File[] sourceFiles, Sector extent, int width, int height, short srcNoData, short destNoData)
        {
            BufferedImage sourceImage = null;
            File tmpFile = TempFile.getTempFile();
            try
            {
                GDALUtils gdal = GDALUtils.getGDAL();

                ArrayList<String> params = new ArrayList<String>();

                params.add("-srcnodata");
                params.add(String.valueOf(srcNoData));

                params.add("-dstnodata");
                params.add(String.valueOf(destNoData));

//                params.add("-co");
//                params.add("PHOTOMETRIC=RGB");

                if (SingleFileImageLayer.this.ms.hasValue(AVKey.PROJECTION_ZONE))
                {
                    params.add("-t_srs");
                    params.add("EPSG:4326");
                }

//                params.add("-ot");
//                params.add("Byte");

//                params.add("-t_srs");
//                params.add("EPSG:4326");
//
//                params.add("-t_srs");
//                params.add("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");


                gdal.warp(SingleFileImageLayer.this.getThreadId(),
                        Option.Warp.Resampling.NearNeighbour,
                        params.toArray(new String[params.size()]),
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
                Logging.logger().log(java.util.logging.Level.SEVERE,
                        SingleFileImageLayer.this.getThreadId() + ex.getMessage(), ex);
            }
            finally
            {
                try
                {
                    tmpFile.delete();
                }
                catch (Exception ignore)
                {
                }
            }
            return sourceImage;
        }


        public void freeResources()
        {
        }


    }
}
