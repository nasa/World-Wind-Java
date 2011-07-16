/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.applications.naip;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.servers.app.BasicHttpServerApplication;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;
import gov.nasa.worldwind.servers.wms.WMS;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWUtil;

import java.io.IOException;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

public class USGSNAIPWMSServer extends BasicHttpServerApplication
{
    protected static final int WW_MAX_TILE_SIZE = 512;
    protected static final String MIME_IMAGE_PNG = "image/png";

//    protected long veTileExpiryTime = VirtualEarthTileRetriever.DEFAULT_EXPIRY_TIME;
//
//    protected String veTileBaseFolder = null;
    protected String mimeImageFormat = MIME_IMAGE_PNG;

    @SuppressWarnings({"UnusedDeclaration"})
    public USGSNAIPWMSServer()
    {
        super();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public USGSNAIPWMSServer(AVList config)
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
        // A typical NAIP request
        // http://raster.nationalmap.gov/arcgis/services/Combined/USGS_EDC_Ortho_NAIP/MapServer/WMSServer?
        // service=WMS&request=GetMap&version=1.3.0&crs=CRS:84&layers=0&styles=
        // &width=512&height=512&format=image/jpeg&transparent=TRUE&bgcolor=0x000000&bbox=-123,47,-122,48

        String request = req.getParameter( WMS.Param.REQUEST.toString() );
        if( WWUtil.isEmpty(request) )
        {
            String message = Logging.getMessage("generic.MissingRequiredParameter", WMS.Param.REQUEST.toString() );
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }
        else if( WMS.Request.GetCapabilities.equals(request) )
        {
            this.doGetCapabilities(req, resp);
        }
        else if( WMS.Request.GetMap.equals(request) )
        {
            this.doGetCapabilities(req, resp);
        }
        else
        {
            String message = Logging.getMessage("generic.FeatureNotImplemented", request );
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

//        VirtualEarthLayer veLayer = VirtualEarthLayer.parse(layerName);
//        int wwLevel = Integer.parseInt(level);
//        int wwRow = Integer.parseInt(y);
//        int wwCol = Integer.parseInt(x);
//
//        Sector bbox = calcTileSector(wwLevel, wwRow, wwCol);
//
//        VirtualEarthTile[] veTiles = VirtualEarthTileSystem.createTiles(bbox, veLayer);
//
//        if (null == veTiles)
//        {
//            String message = Logging.getMessage("nullValue.TileIsNull");
//            Logging.logger().severe(message);
//            throw new WWRuntimeException(message);
//        }
//
//        // Update each tile path by adding a base directory
//        for (VirtualEarthTile tile : veTiles)
//        {
//            String fullTilePath = WWIO.appendPathPart(this.veTileBaseFolder, tile.getPath());
//            tile.setPath(fullTilePath);
//        }
//
//        VirtualEarthTileRetriever retriever = new VirtualEarthTileRetriever(veTiles);
//        retriever.setTileExpiryTime(this.veTileExpiryTime);
//        retriever.downloadTiles();
//
//        DataRaster raster = buildWorldWindTile(bbox, veTiles);
//
//        if (null == raster || !(raster instanceof BufferedImageRaster))
//        {
//            String message = Logging.getMessage("nullValue.RasterIsNull");
//            Logging.logger().severe(message);
//            throw new WWRuntimeException(message);
//        }
//
//        BufferedImage image = ((BufferedImageRaster) raster).getBufferedImage();
//
//        BufferedImageFormatter formatter = new BufferedImageFormatter(image);
//
//        resp.setStatus(HTTPResponse.OK);
//        resp.setContentType(this.mimeImageFormat);
//        resp.addHeader(HTTP.Header.VARY, HTTP.Header.ACCEPT_ENCODING);
//
//        InputStream is = formatter.getStreamFromMimeType(this.mimeImageFormat, null);
//        InputStream inp = HTTP.encodePayload(is, this.mimeImageFormat, req, resp);
//
//        resp.setContentLength(inp.available());
//        OutputStream out = resp.getOutputStream();
//
//        byte[] buff = new byte[HTTP.BUFFER_SIZE];
//
//        for (; ;)
//        {
//            int len = inp.read(buff, 0, HTTP.BUFFER_SIZE);
//            if (len == -1)
//                break;
//            out.write(buff, 0, len);
//        }
//
//        resp.flushBuffer();
//        inp.close();
    }


    protected void doGetCapabilities(HTTPRequest req, HTTPResponse resp) throws WWRuntimeException, IOException
    {
        // TODO Impement GetCapabilities
    }

    protected void doGetMap(HTTPRequest req, HTTPResponse resp) throws WWRuntimeException, IOException
    {
        // TODO Impement GetMap
    }
}