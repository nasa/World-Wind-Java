/* Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class SingleFileLayer extends AbstractMapGenerator
{
    protected File sourceFile;
    protected DataRaster[] rasters;
    protected AVList params;
    protected boolean isElevation = false;
    protected boolean convertFeetToMeters = false;
    protected double pixelHeight = 0d;
    protected short nodataSignal = 0;
    protected short nodataReplacement = 0;

    protected DataRasterReaderFactory readerFactory;

    protected static final String[] CRS = {"EPSG:4326"};
    protected Sector BBOX = Sector.FULL_SPHERE; // Sector.EMPTY_SECTOR;

    protected static DataRasterReader[] readers = new DataRasterReader[]
        {
            new GeotiffRasterReader(),
            new BILRasterReader()
        };

    public String getDataType()
    {
        return (this.isElevation) ? "elevation" : "imagery";
    }

    @Override
    public String getThreadId()
    {
        String threadId = "[" + Thread.currentThread().getId() + "]: ";

        if (null != this.mapSource && null != this.mapSource.getName())
        {
            return this.mapSource.getName() + " " + threadId;
        }

        return threadId;
    }

    public SingleFileLayer()
    {
        super();
    }

    public ServiceInstance getServiceInstance()
    {
        return new SingleFileImageServiceInstance();
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        if (null == mapSource)
        {
            String msg = Logging.getMessage("nullValue.MapSourceIsNull");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.mapSource = mapSource;

        this.params = mapSource.getParameters();
        if (null == params)
        {
            String msg = Logging.getMessage("nullValue.AVListIsNull");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        if (!params.hasKey(AVKey.FILE_NAME))
        {
            String msg = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.sourceFile = new File(params.getStringValue(AVKey.FILE_NAME));
        if (!this.sourceFile.exists())
        {
            String msg = Logging.getMessage("generic.FileNotFound", this.sourceFile.getAbsolutePath());
            Logging.logger().severe(msg);
            throw new FileNotFoundException(msg);
        }

        AVList fileParams = this.params.copy();

        try
        {
            this.readerFactory = (DataRasterReaderFactory) WorldWind.createConfigurationComponent(
                AVKey.DATA_RASTER_READER_FACTORY_CLASS_NAME);
        }
        catch (Exception e)
        {
            this.readerFactory = new BasicDataRasterReaderFactory();
        }
        DataRasterReader reader = this.readerFactory.findReaderFor(this.sourceFile, fileParams, readers);
        if (reader == null)
        {
            String msg = Logging.getMessage("nullValue.ReaderIsNull", this.sourceFile);
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        reader.readMetadata(this.sourceFile, fileParams);

        this.params.setValues(fileParams);

        if (!this.params.hasKey(AVKey.SECTOR))
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }
        this.BBOX = (Sector) this.params.getValue(AVKey.SECTOR);

        if (0d == this.BBOX.getDeltaLatDegrees() || 0d == this.BBOX.getDeltaLonDegrees())
        {
            String msg = Logging.getMessage("generic.SectorSizeInvalid");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        int height = 0;
        if (!this.params.hasKey(AVKey.HEIGHT))
        {
            String msg = Logging.getMessage("generic.InvalidHeight", 0);
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }
        else
        {
            Object o = this.params.getValue(AVKey.HEIGHT);
            double d = Double.parseDouble("" + o);
            height = (int) d;
        }

        if (!this.params.hasKey(AVKey.WIDTH))
        {
            String msg = Logging.getMessage("generic.InvalidWidth", 0);
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.isElevation = (this.params.hasKey(AVKey.PIXEL_FORMAT)
            && AVKey.ELEVATION.equals(this.params.getValue(AVKey.PIXEL_FORMAT)));

        if (this.params.hasKey(AVKey.MISSING_DATA_SIGNAL))
        {
            try
            {
                Object o = this.params.getValue(AVKey.MISSING_DATA_SIGNAL);
                double d = Double.parseDouble("" + o);
                this.nodataSignal = (short) d;
            }
            catch (Exception e)
            {
                this.nodataSignal = (this.isElevation) ? Short.MIN_VALUE : 0;
            }
        }
        else
        {
            this.nodataSignal = (this.isElevation) ? Short.MIN_VALUE : 0;
        }

        if (this.params.hasKey(AVKey.MISSING_DATA_REPLACEMENT))
        {
            try
            {
                Object o = this.params.getValue(AVKey.MISSING_DATA_REPLACEMENT);
                double d = Double.parseDouble("" + o);
                this.nodataReplacement = (short) d;
            }
            catch (Exception e)
            {
                Logging.logger().finest(e.getMessage());
                this.nodataReplacement = (this.isElevation) ? Short.MIN_VALUE : 0;
            }
        }
        else
        {
            this.nodataReplacement = (this.isElevation) ? Short.MIN_VALUE : 0;
        }

        if (this.isElevation)
        {
            if (this.params.hasKey(AVKey.ELEVATION_UNIT))
            {
                try
                {
                    String unit = this.params.getStringValue(AVKey.ELEVATION_UNIT);
                    this.convertFeetToMeters = "feet".equalsIgnoreCase(unit);
                }
                catch (Exception e)
                {
                    Logging.logger().finest(e.getMessage());
                }
            }
        }

        // if PIXEL_HEIGHT is specified, we are not overriding it
        // because UTM images will have different pixel size
        if (!this.params.hasKey(AVKey.PIXEL_HEIGHT))
        {
            this.pixelHeight = this.BBOX.getDeltaLatDegrees() / (double) height;
        }
        else
        {
            try
            {
                Object o = this.params.getValue(AVKey.PIXEL_HEIGHT);
                this.pixelHeight = Double.parseDouble("" + o);
            }
            catch (Exception e)
            {
                Logging.logger().finest(e.getMessage());
            }
        }

        this.rasters = reader.read(this.sourceFile, this.params);

        if (null == this.rasters || 0 == this.rasters.length)
        {
            String msg = Logging.getMessage("nullValue.RasterIsNull");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
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
        return this.pixelHeight;
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
            ImageFormatter formatter = null;
            try
            {
                Sector reqExtent = (SingleFileLayer.this.isElevation)
                    ? req.getExtentForElevationRequest() : req.getExtent();

                if (null == this.intersects(reqExtent, SingleFileLayer.this.getBBox()))
                {
                    String msg = Logging.getMessage("WMS.Layer.OutOfCoverage", reqExtent.toString(),
                        SingleFileLayer.this.getBBox().toString());
                    Logging.logger().severe(msg);
                    throw new WMSServiceException(msg);
                }

                if (req.getHeight() <= 0 || req.getWidth() <= 0)
                {
                    String msg = Logging.getMessage("generic.InvalidImageSize", req.getWidth(), req.getHeight());
                    Logging.logger().severe(msg);
                    throw new WMSServiceException(msg);
                }

                DataRaster[] rasters = SingleFileLayer.this.rasters;
                if (null == rasters || 0 == rasters.length)
                {
                    String msg = Logging.getMessage("nullValue.RasterIsNull");
                    Logging.logger().severe(msg);
                    throw new WMSServiceException(msg);
                }

                double missingDataReplacement = (double) SingleFileLayer.this.nodataReplacement;
                try
                {
                    String s = req.getBGColor();
                    if (null != s)
                    {
                        missingDataReplacement = Double.parseDouble(s);
                    }
                }
                catch (Exception e)
                {
                    missingDataReplacement = (double) SingleFileLayer.this.nodataReplacement;
                }

                DataRaster raster = rasters[0];
                if (raster instanceof BufferedImageRaster)
                {
                    BufferedImageRaster reqRaster = new BufferedImageRaster(req.getWidth(), req.getHeight(),
                        Transparency.TRANSLUCENT, reqExtent);

                    raster.drawOnTo(reqRaster);

                    BufferedImage img = reqRaster.getBufferedImage();
                    this.makeNoDataTransparent(img, SingleFileLayer.this.nodataSignal, (short) missingDataReplacement);

                    formatter = new BufferedImageFormatter(img);
                }
                else if (raster instanceof ByteBufferRaster)
                {
                    AVList reqParams = new AVListImpl();

                    reqParams.setValue(AVKey.WIDTH, req.getWidth());
                    reqParams.setValue(AVKey.HEIGHT, req.getHeight());
                    reqParams.setValue(AVKey.SECTOR, reqExtent);
                    reqParams.setValue(AVKey.BYTE_ORDER, AVKey.LITTLE_ENDIAN); // by default BIL is LITTLE ENDIAN
                    reqParams.setValue(AVKey.PIXEL_FORMAT, AVKey.ELEVATION);

                    String reqFormat = req.getFormat();
                    if (null != reqFormat && reqFormat.endsWith("32"))
                    {
                        reqParams.setValue(AVKey.DATA_TYPE, AVKey.FLOAT32);
                    }
                    else
                    {
                        reqParams.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                    }
                    reqParams.setValue(AVKey.MISSING_DATA_REPLACEMENT, missingDataReplacement);

                    ByteBufferRaster reqRaster = new ByteBufferRaster(req.getWidth(), req.getHeight(),
                        reqExtent, reqParams);

                    raster.drawOnTo(reqRaster);

                    if (SingleFileLayer.this.convertFeetToMeters)
                    {
                        this.convertFeetToMeters(reqRaster);
                    }

                    formatter = new DataRasterFormatter(reqRaster);
                }
                else
                {
                    String msg = Logging.getMessage("generic.UnrecognizedImageSourceType", raster.getClass().getName());
                    Logging.logger().severe(msg);
                    throw new WMSServiceException(msg);
                }
            }
            catch (WMSServiceException wmsse)
            {
                throw wmsse;
            }
            catch (Exception ex)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE,
                    SingleFileLayer.this.getThreadId() + ex.getMessage(), ex);
                // throw new WMSServiceException( s );
            }
            finally
            {
            }

            return formatter;
        }

        public void freeResources()
        {
        }

        private void convertFeetToMeters(ByteBufferRaster raster)
        {
            if (null == raster)
            {
                return;
            }

            int width = raster.getWidth();
            int height = raster.getHeight();

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    raster.setDoubleAtPosition(y, x, raster.getDoubleAtPosition(y, x) / WWMath.METERS_TO_FEET);
                }
            }
        }

        private void makeNoDataTransparent(BufferedImage image, short missingDataSignal, short missingDataReplacement)
        {
            WritableRaster raster = null;

            if (null != image
                && (image.getType() == BufferedImage.TYPE_4BYTE_ABGR || image.getType() == BufferedImage.TYPE_INT_ARGB)
                && null != (raster = image.getRaster())
                )
            {
                int nodata_r = missingDataSignal & 0xff;
                int nodata_g = missingDataSignal & 0xff;
                int nodata_b = missingDataSignal & 0xff;

                int[] pixel = new int[4];
                int[] transparentPixel = new int[] {0xFF & missingDataReplacement /* red */,
                    0xFF & missingDataReplacement /* green */,
                    0xFF & missingDataReplacement, 0x00 /* alpha */};

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
//                            pixel[0] = pixel[1] = pixel[2] = 0xFF & missingDataReplacement;
//                            pixel[3] = 0;   // transparent
                            raster.setPixel(i, j, transparentPixel);
                        }
                        else
                        {
                            pixel[3] = 0xff; // non-transparent
                            raster.setPixel(i, j, pixel);
                        }
                    }
                }
            }
        }
    }
}
