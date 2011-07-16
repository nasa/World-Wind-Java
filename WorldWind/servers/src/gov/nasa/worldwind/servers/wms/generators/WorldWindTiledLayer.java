/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms.generators;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.wms.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * @author dcollins
 * @version $Id$
 */
public class WorldWindTiledLayer extends AbstractMapGenerator
{
    protected Sector coverage = Sector.EMPTY_SECTOR;
    protected static final String crsStr = "EPSG:4326";
    protected String layerType = null;
    protected double pixelSize = 0d;

    protected DataConfigurationMapSource ms = null;
    protected Element configElement = null;
    protected AVList params = null;
    protected LevelSet levels = null;

    protected DataRasterReader[] readers = new DataRasterReader[0];

    public WorldWindTiledLayer()
    {
    }

    public ServiceInstance getServiceInstance()
    {
        return new BasicServiceInstance();
    }

    public Sector getBBox()
    {
        return this.coverage;
    }

    public String[] getCRS()
    {
        return new String[] {crsStr};
    }

    public String getDataType()
    {
        return this.layerType;
    }

    public double getPixelSize()
    {
        // default is 0 after initialization,
        // so if it is not 0, that means it was already calculated
        if (0d == this.pixelSize && null != this.levels && this.levels.getNumLevels() > 0)
        {
            for (Level level : WorldWindTiledLayer.this.levels.getLevels())
            {
                double d = level.getTexelSize();
                if (0d == this.pixelSize || d < this.pixelSize)
                {
                    this.pixelSize = d;
                }
            }
        }
        return this.pixelSize;
    }

    public LevelSet getLevelSet()
    {
        return this.levels;
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        boolean success;

        Logging.logger().info("WorldWindTiledLayer:initialize(): started... ");
        try
        {
            this.doInitialize(mapSource);

            success = true;
            Logging.logger().info(
                "WorldWindTiledLayer:initialize(): status=Done! type=" + this.layerType + " with coverage="
                    + this.coverage.toString());
        }
        catch (Exception ex)
        {
            success = false;
            String msg = Logging.getMessage("WMS.MapGenerator.CannotInstantiate", ex.getMessage());
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
        }
        return success;
    }

    protected boolean isElevationModelLayer()
    {
        return "elevation".equalsIgnoreCase(this.layerType);
    }

    protected boolean isImageLayer()
    {
        return "imagery".equalsIgnoreCase(this.layerType);
    }

    protected void doInitialize(MapSource mapSource)
    {
        if (null == mapSource)
        {
            String msg = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!(mapSource instanceof DataConfigurationMapSource))
        {
            String msg = Logging.getMessage("generic.InvalidDataSource");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.ms = (DataConfigurationMapSource) mapSource;
        this.configElement = this.ms.getConfigurationDocument();
        this.params = this.ms.getParameters();

        // legacy
        this.mapSource = mapSource;

        if (WMSDataConfigurationUtils.isWorldWindLayerConfig(this.configElement, this.params))
        {
            this.layerType = "imagery";
            this.readers = new DataRasterReader[] {
                new ImageIORasterReader(true), // Generate mipmaps when reading source imagery.
                new DDSRasterReader()
            };
        }
        else if (WMSDataConfigurationUtils.isWorldWindElevationModelConfig(this.configElement, this.params))
        {
            this.layerType = "elevation";
            this.readers = new DataRasterReader[] {new BILRasterReader()};
        }
        else
        {
            String msg = Logging.getMessage("generic.UnrecognizedLayerType",
                DataConfigurationUtils.getDataConfigType(this.configElement));
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.coverage = WWXML.getSector(this.configElement, "Sector", null);
        if (null == this.coverage)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.levels = new LevelSet(this.params);
    }

    protected TileCacheMosaicer createMosaicer() throws Exception
    {
        return this.doCreateMosaicer();
    }

    protected TileCacheMosaicer doCreateMosaicer() throws Exception
    {
        AVList tileParams = null;

        if (this.isElevationModelLayer())
        {
            AVList mapSourceParams = this.params;

            tileParams = new AVListImpl();

            tileParams.setValue(AVKey.BYTE_ORDER, mapSourceParams.getValue(AVKey.BYTE_ORDER));
            tileParams.setValue(AVKey.PIXEL_FORMAT, AVKey.ELEVATION);
            tileParams.setValue(AVKey.DATA_TYPE, mapSourceParams.getValue(AVKey.DATA_TYPE));
            // Legacy code expects the string "gov.nasa.worldwind.avkey.MissingDataValue", which now corresponds to
            // the key MISSING_DATA_REPLACEMENT.
            tileParams.setValue(AVKey.MISSING_DATA_REPLACEMENT, mapSourceParams.getValue(AVKey.MISSING_DATA_SIGNAL));
        }

        return new BasicTileCacheMosaicer(this.ms.getFileStore(), this.levels, tileParams, this.readers);
    }

    protected DataRaster createResponseRaster(int width, int height, Sector sector, AVList reqParams) throws Exception
    {
        if (this.isElevationModelLayer())
        {
            ByteBufferRaster raster = new ByteBufferRaster(width, height, sector, reqParams);

            Object o = reqParams.getValue(AVKey.MISSING_DATA_SIGNAL);
            if (null != o && o instanceof Double)
            {
                raster.fill((Double) o);
                raster.setTransparentValue((Double) o);
            }

            return raster;
        }
        else // Default to imagery.
        {
            BufferedImageRaster raster = new BufferedImageRaster(width, height, java.awt.Transparency.TRANSLUCENT,
                sector);

            Object o = reqParams.getValue(AVKey.MISSING_DATA_SIGNAL);
            if (null != o && o instanceof java.awt.Color)
            {
                raster.fill((java.awt.Color) o);
            }

            return raster;
        }
    }

    protected ImageFormatter createResponseFormatter(DataRaster responseRaster) throws Exception
    {
        if (this.isElevationModelLayer())
        {
            return new BILImageFormatter((ByteBufferRaster) responseRaster);
        }
        else // Default to imagery.
        {
            return new BufferedImageFormatter(((BufferedImageRaster) responseRaster).getBufferedImage());
        }
    }

    // --------------------------------------------
    // class BasicServiceInstance
    //
    // Used to service requests for mosaiced tile cache data.
    //

    protected class BasicServiceInstance extends AbstractServiceInstance
    {
        public BasicServiceInstance()
        {
        }

        public void freeResources()
        {
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            try
            {
                return this.doServiceRequest(req);
            }
            catch (Exception ex)
            {
                String msg = Logging.getMessage("WMS.RequestFailed", ex.getMessage());
                Logging.logger().log(java.util.logging.Level.SEVERE, msg, ex);
                throw new WMSServiceException(msg);
            }
        }

        public ImageFormatter doServiceRequest(IMapRequest req) throws Exception
        {
            AVList reqParams = new AVListImpl();
            this.getRequestParams(req, reqParams);

            int reqWidth = (Integer) reqParams.getValue(AVKey.WIDTH);
            int reqHeight = (Integer) reqParams.getValue(AVKey.HEIGHT);
            Sector reqSector = (Sector) reqParams.getValue(AVKey.SECTOR);

            DataRaster responseRaster =
                WorldWindTiledLayer.this.createResponseRaster(reqWidth, reqHeight, reqSector, reqParams);

            if (WorldWindTiledLayer.this.coverage.intersects(reqSector))
            {
                TileCacheMosaicer mosaicer = WorldWindTiledLayer.this.createMosaicer();
                mosaicer.mosaicBestAvailableTiles(reqWidth, reqHeight, reqSector, responseRaster);
            }
            else
            {
                // TODO optimize with a pre-built emtpy tile
                Logging.logger().info(
                    "WorldWindTiledLayer::serviceRequest - out of coverage, skipping" + reqSector.toString());
            }

            return WorldWindTiledLayer.this.createResponseFormatter(responseRaster);
        }

        protected void getRequestParams(IMapRequest request, AVList params) throws Exception
        {
            boolean isElevation = WorldWindTiledLayer.this.isElevationModelLayer();

            int defaultDimension = isElevation ? 150 : 512;
            int width = (request.getWidth() > 0) ? request.getWidth() : defaultDimension;
            int height = (request.getHeight() > 0) ? request.getHeight() : defaultDimension;
            params.setValue(AVKey.WIDTH, width);
            params.setValue(AVKey.HEIGHT, height);

            Sector reqSector = Sector.fromDegrees(request.getBBoxYMin(), request.getBBoxYMax(), request.getBBoxXMin(),
                request.getBBoxXMax());
            params.setValue(AVKey.SECTOR, reqSector);

            String bgColorStr = request.getBGColor();
            if (bgColorStr != null && 0 < bgColorStr.length())
            {
                try
                {
                    if (isElevation)
                    {
                        params.setValue(AVKey.MISSING_DATA_SIGNAL, request.getBGColorAsDouble());
                    }
                    else // Default to imagery.
                    {
                        params.setValue(AVKey.MISSING_DATA_SIGNAL, request.getBGColorAsRGB());
                    }
                }
                catch (NumberFormatException ex)
                {
                    // If we cannot parse the background color string, then simply ignore it. We will use a default
                    // value below.
                }
            }

            if (null == params.getValue(AVKey.MISSING_DATA_SIGNAL))
            {
                params.setValue(AVKey.MISSING_DATA_SIGNAL,
                    WorldWindTiledLayer.this.params.getValue(AVKey.MISSING_DATA_SIGNAL));
            }

            if (isElevation)
            {
                params.setValue(AVKey.BYTE_ORDER, AVKey.LITTLE_ENDIAN);

                String dataType = request.getFormat();
                if (null == dataType || dataType.length() == 0)
                {
                    Logging.logger().info("WorldWindTiledLayer: default .BIL (int16) type is used");
                    params.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                }
                else
                {
                    if ("application/bil32".equals(dataType))
                    {
                        params.setValue(AVKey.DATA_TYPE, AVKey.FLOAT32);
                    }
                    else if ("application/bil16".equals(dataType))
                    {
                        params.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                    }
                    else if ("application/bil".equals(dataType))
                    {
                        params.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                    }
                    else if ("image/bil".equals(dataType))
                    {
                        params.setValue(AVKey.DATA_TYPE, AVKey.INT16);
                    }
                    else
                    {
                        throw new WMSServiceException("Unknown or unsupported format - " + dataType);
                    }
                }
            }
        }
    }
}
