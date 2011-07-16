/* Copyright (C) 2001, 2011 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.wms.formats;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.servers.wms.generators.Mosaicer;
import gov.nasa.worldwind.util.*;

import java.awt.image.*;
import java.io.*;
import java.nio.ByteOrder;
import java.util.Properties;

/**
 * @author garakl
 * @version $Id$
 */

public class DataRasterFormatter extends ImageFormatter
{
    protected DataRaster raster = null;

    public DataRaster getRaster()
    {
        return this.raster;
    }

    public DataRasterFormatter(DataRaster raster)
    {
        this.raster = raster;
    }

    public void merge(DataRaster raster) throws Exception
    {
        if (null == raster)
        {
            String msg = Logging.getMessage("nullValue.RasterIsNull");
            Logging.logger().severe(msg);
            // throw new IOException( Logging.getMessage("WMS.Server.InternalError", msg ));
            return;
        }

        if (null == this.raster)
        {
            String msg = Logging.getMessage("nullValue.RasterIsNull");
            Logging.logger().severe(msg);
            // throw new IOException( Logging.getMessage("WMS.Server.InternalError", msg ));
            return;
        }

        if (this.raster.getWidth() != raster.getWidth() || this.raster.getHeight() != raster.getHeight())
        {
            String msg = Logging.getMessage("generic.InvalidImageSize", raster.getWidth(), raster.getHeight());
            Logging.logger().severe(msg);
            // throw new IOException( Logging.getMessage("WMS.Server.InternalError", msg ));
            return;
        }

        if (!(this.raster instanceof ByteBufferRaster) || !(raster instanceof ByteBufferRaster))
        {
            String msg = Logging.getMessage("generic.InvalidDataSource", raster.getClass().getName());
            Logging.logger().severe(msg);
            // throw new IOException( Logging.getMessage("WMS.Server.InternalError", msg ));
            return;
        }

        // TODO check for elevation type

        ByteBufferRaster bbA = (ByteBufferRaster) this.raster;
        ByteBufferRaster bbB = (ByteBufferRaster) raster;

        double nodataA = bbA.getTransparentValue();
        double nodataB = bbB.getTransparentValue();

        int width = this.raster.getWidth();
        int height = this.raster.getHeight();

        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                double a = bbA.getDoubleAtPosition(row, col);
                // we want to overwrite nodata areas only
                if (a == nodataA && nodataA != 0d)
                {
                    double b = bbB.getDoubleAtPosition(row, col);
                    if (b != nodataB)
                    {
                        bbA.setDoubleAtPosition(row, col, b);
                    }
                }
                else if (a == 0d)
                {
                    double b = bbB.getDoubleAtPosition(row, col);
                    if (b < 0d)
                    {
                        bbA.setDoubleAtPosition(row, col, b);
                    }
                }
            }
        }
    }

    @Override
    public InputStream asBIL() throws IOException
    {
        if (null == this.raster)
        {
            String msg = Logging.getMessage("nullValue.RasterIsNull");
            Logging.logger().severe(msg);
            throw new IOException(Logging.getMessage("WMS.Server.InternalError", msg));
        }

        if (!(this.raster instanceof ByteBufferRaster))
        {
            String msg = Logging.getMessage("WMS.Server.UnknownOrUnsupportedDataFormat",
                this.raster.getClass().getName());
            Logging.logger().severe(msg);
            throw new IOException(msg);
        }

        ByteBufferRaster bbr = (ByteBufferRaster) this.raster;

        if (bbr.getByteBuffer().order() != ByteOrder.LITTLE_ENDIAN)
        {
            // if the order of the original data raster (elevations) is NOT LittleEndian,
            // we must convert it to LittleEndian, because BIL files must be in LittleEndian order

            String pixelType = AVListImpl.getStringValue(this.raster, AVKey.DATA_TYPE);
            if (WWUtil.isEmpty(pixelType))
            {
                pixelType = (bbr.getBuffer() instanceof BufferWrapper.ShortBufferWrapper) ? AVKey.INT16 : AVKey.FLOAT32;
            }

            Double missingDataSignal = AVListImpl.getDoubleValue(this.raster, AVKey.MISSING_DATA_SIGNAL,
                (double) Short.MIN_VALUE /*bbr.getTransparentValue()*/);

            bbr = (ByteBufferRaster) Mosaicer.createDataRaster(bbr.getWidth(), bbr.getHeight(), bbr.getSector(),
                pixelType, missingDataSignal);

            raster.drawOnTo(bbr);
        }

        return new ByteArrayInputStream(bbr.getByteBuffer().array());
    }

    @Override
    protected InputStream intermediateToBIL(BufferedImage image) throws IOException
    {
        // TODO
        return null;
    }

    public BufferedImage toIntermediateForm() throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::toIntermediateForm() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    @Override
    public InputStream asPng() throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::asPng() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    @Override
    public InputStream asDDS(Properties properties) throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::asDDS() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    @Override
    public InputStream asJPEG() throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::asJPEG() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    @Override
    public InputStream asTiff() throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::asTiff() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    @Override
    protected InputStream intermediateToPng(BufferedImage image) throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::intermediateToPng() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    @Override
    protected InputStream intermediateToJPEG(BufferedImage image) throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::intermediateToJPEG() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    @Override
    protected InputStream intermediateToDDS(BufferedImage image, Properties properties) throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::intermediateToDDS() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    @Override
    protected InputStream intermediateToTiff(BufferedImage image) throws IOException
    {
        String msg = Logging.getMessage("WMS.Server.InternalError",
            "DataRasterFormatter::intermediateToTiff() method is not implemented");
        Logging.logger().severe(msg);
        throw new IOException(msg);
    }

    public boolean hasNoDataAreas()
    {
        return DataRasterFormatter.hasNoDataAreas(this.raster);
    }

    public static boolean hasNoDataAreas(DataRaster raster)
    {
        if (null == raster || !(raster instanceof ByteBufferRaster))
        {
            return true;
        }

        ByteBufferRaster bbRaster = (ByteBufferRaster) raster;
        double nodata = bbRaster.getTransparentValue();
        int width = bbRaster.getWidth();
        int height = bbRaster.getHeight();

        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                double d = bbRaster.getDoubleAtPosition(row, col);
                if (d == nodata || d == 0d)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
