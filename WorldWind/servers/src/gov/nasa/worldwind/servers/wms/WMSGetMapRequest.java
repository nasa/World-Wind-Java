/* Copyright (C) 2001, 2007 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
*/
package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.http.HTTP;
import gov.nasa.worldwind.servers.http.HTTPRequest;
import gov.nasa.worldwind.servers.http.HTTPResponse;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.servers.wms.utilities.StringUtil;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author brownrigg
 * @version $Id$
 */
public class WMSGetMapRequest extends WMSRequest implements IMapRequest
{
    // data members...
    private String version = null;
    private String[] layers = null;
    private String[] styles = null;
    private String crs = null;
    private Sector bbox = Sector.EMPTY_SECTOR;
    private int width = -1;
    private int height = -1;
    private String format = null;
    private boolean transparent = false;
    private String bgColor = null;
    private String exceptions = null;
    private String time = null;

    protected static int MAX_REQ_HEIGHT = 4096, MAX_REQ_WIDTH = 4096;

    public WMSGetMapRequest(HTTPRequest req) throws WMSServiceException
    {
        super(req);

        String request = req.getParameter(WMS.Param.REQUEST.toString());
        if (!WMS.Request.GetMap.equals(request))
        {
            String msg = Logging.getMessage("WMS.UnknownOrUnsupportedRequest", request);
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

//        String service = req.getParameter( WMS.Param.SERVICE.toString() );
//        if( !"WMS".equalsIgnoreCase(service) )
//        {
//            String msg = Logging.getMessage("WMS.UnknownOrUnsupportedService", service );
//            Logging.logger().severe(msg);
//            throw new WMSServiceException( msg );
//        }

        this.layers = StringUtil.removePipePrefix(req.getParameterValues(WMS.Param.LAYERS.toString()));
        if (null == this.layers || 0 == this.layers.length)
        {
            String msg = Logging.getMessage("WMS.MissingLayerParameters");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.version = req.getParameter(WMS.Param.VERSION.toString());
        if (null == this.version || 0 == this.version.length())
        {
            String msg = Logging.getMessage("WMS.MissingRequiredParameter", WMS.Param.VERSION.toString());
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.crs = (this.version.equals(WMS.VER_1_1_1))
                   ? req.getParameter(WMS.Param.SRS.toString()) : req.getParameter(WMS.Param.CRS.toString());

        if (null == this.crs || 0 == this.crs.length())
        {
//            TODO We'll relax this requirement for now, as i) its not used by this server
//            present, and ii) its causing some of our client-apps grief.
//            String msg = Logging.getMessage("WMS.MissingRequiredParameter", WMS.Param.CRS.toString() );
//            Logging.logger().severe(msg);
//            throw new WMSServiceException( msg );
            this.crs = "EPSG:4326";
        }

        this.bbox = this.parseBoundingBox(this.version, this.crs, req.getParameterValues(WMS.Param.BBOX.toString()));

        this.format = req.getParameter(WMS.Param.FORMAT.toString());
        if (null == this.format || 0 == this.format.length())
        {
            String msg = Logging.getMessage("WMS.MissingRequiredParameter", WMS.Param.FORMAT.toString());
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.styles = req.getParameterValues(WMS.Param.STYLES.toString());
        if (null == this.styles || 0 == this.styles.length)
        {
            String msg = Logging.getMessage("WMS.MissingRequiredParameter", WMS.Param.STYLES.toString());
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        try
        {
            this.width = Integer.parseInt(req.getParameter(WMS.Param.WIDTH.toString()));
            if (this.width <= 0)
            {
                String msg = Logging.getMessage("Geom.WidthInvalid", this.width);
                Logging.logger().severe(msg);
                throw new WMSServiceException(msg);
            }
        }
        catch (NumberFormatException ex)
        {
            String msg = Logging.getMessage("Geom.WidthInvalid", ex.getMessage());
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        try
        {
            this.height = Integer.parseInt(req.getParameter(WMS.Param.HEIGHT.toString()));
            if (this.height <= 0)
            {
                String msg = Logging.getMessage("Geom.HeightInvalid", this.height);
                Logging.logger().severe(msg);
                throw new WMSServiceException(msg);
            }
        }
        catch (NumberFormatException ex)
        {
            String msg = Logging.getMessage("Geom.HeightInvalid", ex.getMessage());
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        try
        {
            this.transparent = Boolean.parseBoolean(req.getParameter(WMS.Param.TRANSPARENT.toString()));
        }
        catch (NumberFormatException ex)
        {
            String msg = Logging.getMessage("WMS.InvalidTransparentParameter", ex.getMessage());
            Logging.logger().severe(msg);
            this.transparent = false;
//            throw new WMSServiceException( msg );
        }

        this.bgColor = req.getParameter(WMS.Param.BGCOLOR.toString());

        this.exceptions = req.getParameter(WMS.Param.EXCEPTIONS.toString());
        this.time = req.getParameter(WMS.Param.TIME.toString());

        // A final integrity check...
        if (this.styles != null && this.layers != null && this.styles.length != this.layers.length)
        {
            String msg = Logging.getMessage("WMS.LayerNumberNotMatchingStylesNumber");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }
    }

    protected Sector parseBoundingBox(String ver, String crs, String[] bbox) throws WMSServiceException
    {
        if (null == bbox || 4 != bbox.length)
        {
            String msg = Logging.getMessage("WMS.MissingGeographicBoundingBoxParameter");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        Sector sector = Sector.EMPTY_SECTOR;

        double[] coords = new double[4];

        try
        {
            for (int i = 0; i < 4; i++)
            {
                coords[i] = Double.parseDouble(bbox[i]);
            }
        }
        catch (NumberFormatException ex)
        {
            String msg = Logging.getMessage("WMS.InvalidGeographicBoundingBoxParameter",
                    "{ " + bbox[0] + ", " + bbox[1] + ", " + bbox[2] + ", " + bbox[3] + " }");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        // WMS clients can use the CRS:84 coordinate system and order the BBOX coordinates as long/lat:
        // - ...&CRS=CRS:84&BBOX=-180.0,-90.0,180.0,90.0
        // OR
        // WMS clients also use the ESPG:4326 coordinates and use the axis ordering of lat/long:
        // - ...&EPSG:4326&BBOX=-90.0,-180.0,90,180.0
        //
        // but we default to BBOX=minx,miny,maxx,maxy

//        if( "EPSG:4326".equals(crs) )
//        {
        // version=1.3.0 & crs=EPSG:4326 & bbox=minLon(0),minLat(1),maxLon(2),maxLat(3)
        // Sector.fromDegrees( minLatitude, maxLatitude, minLongitude, maxLongitude )
        sector = Sector.fromDegrees(coords[1], coords[3], coords[0], coords[2]);
//        }

        return sector;
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer();
        str.append("GetMap: ");
        str.append(" layers(");
        for (int i = 0; i < this.layers.length; i++)
        {
            if (i > 0)
            {
                str.append(", ");
            }
            str.append(this.layers[i]);
        }
        str.append(") styles(");
        for (int i = 0; i < this.styles.length; i++)
        {
            if (i > 0)
            {
                str.append(", ");
            }
            str.append(this.styles[i]);
        }
        str.append(") crs(");
        str.append(crs);
        str.append(") bbox(").append(this.bbox.toString());
        str.append(") WxH(");
        str.append(this.width);
        str.append("x");
        str.append(this.height);
        str.append(") format(");
        str.append(this.format);
        str.append(") transp(");
        str.append(this.transparent);
        str.append(")");
        return str.toString();
    }

    // gettors...

    public String getVersion()
    {
        return this.version;
    }

    public String[] getLayers()
    {
        return this.layers;
    }

    public String[] getStyles()
    {
        return this.styles;
    }

    public String getCRS()
    {
        return this.crs;
    }

//    public double[] getBBox()
//    {
//        return this.bbox;
//    }

    public double getBBoxXMin()
    {
        return this.bbox.getMinLongitude().degrees;
    }

    public double getBBoxXMax()
    {
        return this.bbox.getMaxLongitude().degrees;
    }

    public double getBBoxYMin()
    {
        return this.bbox.getMinLatitude().degrees;
    }

    public double getBBoxYMax()
    {
        return this.bbox.getMaxLatitude().degrees;
    }

    public int getWidth()
    {
        return this.width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public String getFormat()
    {
        return this.format;
    }

    public boolean isTransparent()
    {
        return this.transparent;
    }

    public String getBGColor()
    {
        return this.bgColor;
    }

    public void setBGColor(String bgColor)
    {
        this.bgColor = bgColor;
    }

    public Double getBGColorAsDouble()
    {
        Double color = -9999d;
        try
        {
            color = Double.parseDouble(this.bgColor);
        }
        catch (Exception ex)
        {
            color = -9999d;
        }
        return color;
    }

    public Color getBGColorAsRGB()
    {
        if (null == this.bgColor)
        {
            return null;
        }

        try
        {
            return WWUtil.decodeColorRGBA(this.bgColor);
        }
        catch (Exception ex)
        {
            Logging.logger().finest("Unable to parse BGCOLOR in WMS request: " + this.bgColor
                                    + "; Details: " + ex.getMessage());
        }

        return null;
    }

    public String getExceptions()
    {
        return this.exceptions;
    }

    public String getTime()
    {
        return this.time;
    }

    public Sector getExtentForElevationRequest()
    {
        double width = (this.getWidth() > 0) ? this.getWidth() : 150;
        double height = (this.getHeight() > 0) ? this.getHeight() : 150;

        Sector sector = Sector.fromDegrees(this.getBBoxYMin(), this.getBBoxYMax(), this.getBBoxXMin(),
                this.getBBoxXMax());
        double latDelta = Math.abs(sector.getDeltaLatDegrees() / height);
        double lonDelta = Math.abs(sector.getDeltaLonDegrees() / width);

        // account half pixel shift (center) for elevation tiles
        double latShift = latDelta;
        double lonShift = lonDelta;

//        latShift = lonShift = 0d;
//        latDelta = lonDelta = 0d;

        return Sector.fromDegrees(
                sector.getMinLatitude().degrees - latShift,
                sector.getMaxLatitude().degrees + latShift,
                sector.getMinLongitude().degrees - lonShift,
                sector.getMaxLongitude().degrees + lonShift
        );
    }

    public Sector getExtent()
    {
        return Sector.fromDegrees(this.getBBoxYMin(), this.getBBoxYMax(), this.getBBoxXMin(), this.getBBoxXMax());
    }

    public void service(HTTPRequest req, HTTPResponse resp) throws IOException, WMSServiceException
    {
        long start = System.currentTimeMillis();

        // do we know about this map layer?
        // TODO - for the moment ignore all but first one, until we've implemented map-compositing
        String[] layers = this.getLayers();

        MapRegistry registry = this.getApplicationContext().getMapSourceRegistry();
        MapSource map = registry.get(layers[0]);
        if (map == null)
        {
            String message = Logging.getMessage("generic.UnrecognizedLayer", layers[0]);
            Logging.logger().severe(message);
            throw new WMSServiceException(message);
        }

        if (!ImageFormatter.isSupportedType(this.getFormat()))
        {
            String message = Logging.getMessage("generic.InvalidImageFormat", this.getFormat());
            Logging.logger().severe(message);
            throw new WMSServiceException(message);
        }

        if (this.getHeight() > MAX_REQ_HEIGHT || this.getHeight() < 1
            || this.getWidth() > MAX_REQ_WIDTH || this.getWidth() < 1
                )
        {
            String message = Logging.getMessage("generic.InvalidImageSize", this.getWidth(), this.getHeight());
            Logging.logger().severe(message);
            throw new WMSServiceException(message);
        }

        // create an instance of the MapGenerator.ServiceInstance and hand request to it...
        ImageFormatter image = null;

        start = System.currentTimeMillis();

        MapGenerator.ServiceInstance mapService = null;
        try
        {
            MapGenerator mapGen = map.getMapGenerator();
            mapService = mapGen.getServiceInstance();
            image = mapService.serviceRequest((IMapRequest) this);
        }
        catch (WMSServiceException wmsse)
        {
            throw wmsse;
        }
        catch (Exception ex)
        {
            throw new WMSServiceException("Failed to instantiate map-generator: " + ex.toString());
        }

        req.addStats("serviceRequest", System.currentTimeMillis() - start);

        resp.setStatus(HTTPResponse.OK);
        resp.setContentType(this.getFormat());
        resp.addHeader(HTTP.Header.VARY, HTTP.Header.ACCEPT_ENCODING);

        InputStream eis = null;

        try
        {
            InputStream is = image.getStreamFromMimeType(this.getFormat(), map.getProperties());
            eis = HTTP.encodePayload(is, this.getFormat(), this.getHttpRequest(), resp);

            resp.write(eis);
        }
        finally
        {
            WWIO.closeStream(eis, null);
            mapService.freeResources();
        }
    }
}
