/*
Copyright (C) 2001, 2010 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.applications.ve;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.app.BasicHttpServerApplication;
import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.servers.wms.formats.BufferedImageFormatter;
import gov.nasa.worldwind.util.*;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class VirtualEarthTileServer extends BasicHttpServerApplication
{
    protected static final int WW_MAX_TILE_SIZE = 512;
    protected static final String MIME_IMAGE_JPEG = "image/jpeg";

    protected long veTileExpiryTime = VirtualEarthTileRetriever.DEFAULT_EXPIRY_TIME;
    protected String veTileBaseFolder = null;
    protected String mimeImageFormat = MIME_IMAGE_JPEG;

    @SuppressWarnings( {"UnusedDeclaration"})
    public VirtualEarthTileServer()
    {
        super();
    }

    @SuppressWarnings( {"UnusedDeclaration"})
    public VirtualEarthTileServer(AVList config)
    {
        super(config);
    }

    @Override
    protected void doStart()
    {
        if (null == this.getVirtualDirectory())
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SERVER_VIRTUAL_DIRECTORY);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (!this.hasKey(AVKey.SERVER_REDIRECT_TO))
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SERVER_REDIRECT_TO);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Object o = this.getValue(AVKey.EXPIRY_TIME);
        if (o != null)
        {
            try
            {
                if (o instanceof String)
                {
                    this.veTileExpiryTime = Long.parseLong((String) o);
                }
                else if (o instanceof Long)
                {
                    this.veTileExpiryTime = (Long) o;
                }
                else
                {
                    throw new WWRuntimeException(AVKey.EXPIRY_TIME);
                }
            }
            catch (Exception e)
            {
                String message = Logging.getMessage("generic.UnknownValueForKey", o, AVKey.EXPIRY_TIME);
                Logging.logger().log(java.util.logging.Level.FINEST, message, e);
                throw new IllegalArgumentException(message);
            }
        }

        this.veTileBaseFolder = null;

        o = this.getValue(AVKey.FILE_STORE_LOCATION);
        if (o != null && o instanceof String)
        {
            File baseDir = new File((String) o);
            if (!baseDir.exists())
            {
                WWIO.makeParentDirs(baseDir.getAbsolutePath());
                //noinspection ResultOfMethodCallIgnored
                baseDir.mkdirs();
            }

            if (!baseDir.exists())
            {
                String message = Logging.getMessage("generic.FolderDoesNotExist", baseDir.getAbsolutePath());
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            if (!baseDir.canWrite())
            {
                String message = Logging.getMessage("generic.FolderNoWritePermission", baseDir.getAbsolutePath());
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            this.veTileBaseFolder = baseDir.getAbsolutePath();
        }

        if (null == this.veTileBaseFolder)
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.FILE_STORE_LOCATION);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    protected void doGet(HTTPRequest req, HTTPResponse resp)
    {
        try
        {
            this.serviceRequest(req, resp);
        }
        catch (WWRuntimeException wwe)
        {
            this.sendExceptionToClient(resp, wwe.toString());
        }
        catch (SecurityException se)
        {
            String reason = se.getMessage();
            Logging.logger().finest(reason);
            this.sendExceptionToClient(resp, reason);
        }
        catch (Throwable t)
        {
            String msg = t.getMessage();
            Logging.logger().log(java.util.logging.Level.FINEST, msg, t);
            this.sendExceptionToClient(resp, msg);
        }
    }

    protected void serviceRequest(HTTPRequest req, HTTPResponse resp) throws WWRuntimeException, IOException
    {
        // A typical request
        // http://worldwind28.arc.nasa.gov/vewms/vewms.aspx?T=a&L=4&X=1820&Y=900

        String layerName = req.getParameter("T");
        if (null == layerName || 0 == layerName.length())
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", "T");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        String x = req.getParameter("X");
        if (null == x || 0 == x.length())
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", "X");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        String y = req.getParameter("Y");
        if (null == y || 0 == y.length())
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", "Y");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        String level = req.getParameter("L");
        if (null == level || 0 == level.length())
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", "L");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        VirtualEarthLayer veLayer = VirtualEarthLayer.parse(layerName);
        int wwLevel = Integer.parseInt(level);
        int wwRow = Integer.parseInt(y);
        int wwCol = Integer.parseInt(x);

        Sector bbox = calcTileSector(wwLevel, wwRow, wwCol);

        VirtualEarthTile[] veTiles = VirtualEarthTileSystem.createTiles(bbox, veLayer);

        if (null == veTiles)
        {
            String message = Logging.getMessage("nullValue.TileIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        // Update each tile path by adding a base directory 
        for (VirtualEarthTile tile : veTiles)
        {
            String fullTilePath = WWIO.appendPathPart(this.veTileBaseFolder, tile.getPath());
            tile.setPath(fullTilePath);
        }

        VirtualEarthTileRetriever retriever = new VirtualEarthTileRetriever(veTiles);
        retriever.setTileExpiryTime(this.veTileExpiryTime);
        retriever.downloadTiles();

        DataRaster raster = buildWorldWindTile(bbox, veTiles);

        if (null == raster || !(raster instanceof BufferedImageRaster))
        {
            String message = Logging.getMessage("nullValue.RasterIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        BufferedImage image = ((BufferedImageRaster) raster).getBufferedImage();

        BufferedImageFormatter formatter = new BufferedImageFormatter(image);

        resp.setStatus(HTTPResponse.OK);
        resp.setContentType(this.mimeImageFormat);
        resp.addHeader(HTTP.Header.VARY, HTTP.Header.ACCEPT_ENCODING);

        InputStream inp = null;

        try
        {
            InputStream is = formatter.getStreamFromMimeType(this.mimeImageFormat, null);
            inp = HTTP.encodePayload(is, this.mimeImageFormat, req, resp);

            resp.write(inp);
        }
        finally
        {
            WWIO.closeStream(inp, null);
        }
    }

    /**
     * Calculates a sector (bounding box) of the requested World Wind tile
     *
     * @param wwLevel WorldWind zoom level
     * @param wwRow   WorldWind tile row
     * @param wwCol   WorldWind tile column
     *
     * @return Sector Returns a bounding box
     */
    protected static Sector calcTileSector(int wwLevel, int wwRow, int wwCol)
    {
        // Level Zero Tile Size (in degrees)
        // WW[0]=>36, WW[1]=18, WW[2]=9, WW[3]=4.5, WW[4]=2.25,
        double levelZeroTileSize = 2.25d;

        double dstTileSize = levelZeroTileSize * Math.pow(0.5, wwLevel);

        // calc max latitude
        double north = wwRow * dstTileSize + dstTileSize - 90;

        // calc min latitude
        double south = wwRow * dstTileSize - 90;

        // calc min longitude
        double west = wwCol * dstTileSize - 180;

        // calc max longitude
        double east = wwCol * dstTileSize + dstTileSize - 180;

        return Sector.fromDegrees(south, north, west, east);
    }

    /**
     * Builds a BufferedImageRaster by loading and mosaicing VirtualEarth tiles
     *
     * @param bbox  Sector, a bounding box of the requested WorldWind tile
     * @param tiles An array of VirtualEarthTile
     *
     * @return DataRaster Returns a BufferedImageRaster
     */
    protected static DataRaster buildWorldWindTile(Sector bbox, VirtualEarthTile[] tiles)
    {
        if (null == bbox)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        if (null == tiles || 0 == tiles.length)
        {
            String message = Logging.getMessage("nullValue.TileIsNull");
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        BufferedImage image = ImageUtil.createCompatibleImage(WW_MAX_TILE_SIZE, WW_MAX_TILE_SIZE, 1);

        AVList params = new AVListImpl();
        params.setValue(AVKey.COORDINATE_SYSTEM, AVKey.COORDINATE_SYSTEM_GEOGRAPHIC);
        params.setValue(AVKey.WIDTH, WW_MAX_TILE_SIZE);
        params.setValue(AVKey.HEIGHT, WW_MAX_TILE_SIZE);
        params.setValue(AVKey.SECTOR, bbox);

        BufferedImageRaster raster = (BufferedImageRaster) BufferedImageRaster.wrap(image, params);

        for (VirtualEarthTile tile : tiles)
        {
            try
            {
                BufferedImage tileImage = ImageIO.read(new File(tile.getPath()));

                AVList tileParams = new AVListImpl();

                tileParams.setValue(AVKey.WIDTH, tile.getWidth());
                tileParams.setValue(AVKey.HEIGHT, tile.getHeight());
                tileParams.setValue(AVKey.SECTOR, tile.getSector());

                BufferedImageRaster tileRaster = (BufferedImageRaster) BufferedImageRaster.wrap(tileImage, tileParams);

                tileRaster.drawOnTo(raster);
            }
            catch (Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }
        }

        return raster;
    }
}
