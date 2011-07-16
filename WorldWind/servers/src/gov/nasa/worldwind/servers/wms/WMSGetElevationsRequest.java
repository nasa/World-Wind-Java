/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.data.ByteBufferRaster;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.servers.http.*;
import gov.nasa.worldwind.servers.wms.formats.*;
import gov.nasa.worldwind.servers.wms.generators.Mosaicer;
import gov.nasa.worldwind.servers.wms.utilities.StringUtil;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */
public class WMSGetElevationsRequest extends WMSRequest
{
    public static final double DEFAULT_NODATA = -9999d;
    // data members...
    private String version = "1.1.1";
    private String[] layers = null;
    private String crs = null;
    private Sector bbox = Sector.EMPTY_SECTOR;
    private int width = 1;
    private int height = 1;
    private String format = "application/bil32";
    private String bgColor = null;
    private String exceptions = null;
    private String time = null;

    private ArrayList<LatLon> latlons = null;
    private int maxThreads = 1;
    private HashMap<String, TileTask> tasks = new HashMap<String, TileTask>();

    public WMSGetElevationsRequest(HTTPRequest req) throws WMSServiceException
    {
        super(req);

        String request = req.getParameter(WMS.Param.REQUEST.toString());
        if (!WMS.Request.GetElevations.equals(request))
        {
            String msg = Logging.getMessage("WMS.UnknownOrUnsupportedRequest", request);
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.layers = StringUtil.removePipePrefix(req.getParameterValues(WMS.Param.LAYERS.toString()));
        if (null == this.layers || 0 == this.layers.length)
        {
            String msg = Logging.getMessage("WMS.MissingLayerParameters");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.bgColor = req.getParameter(WMS.Param.BGCOLOR.toString());
        if (null == this.bgColor)
            this.bgColor = Double.toString(DEFAULT_NODATA);

        String locations = req.getParameter(WMS.Param.LOCATIONS.toString());
        this.latlons = this.parseLatLonPairs(locations);
        if (null == this.latlons || 0 == this.latlons.size())
        {
            String msg = Logging.getMessage("WMS.MissingGeographicBoundingBoxParameter");
            Logging.logger().severe(msg);
            throw new WMSServiceException(msg);
        }

        this.bbox = Sector.boundingSector(this.latlons);

        try
        {
            this.maxThreads = Integer.valueOf(req.getParameter(WMS.Param.WW_MAX_THREADS.toString()));
        }
        catch (Exception e)
        {
            Logging.logger().severe(e.getMessage());
            this.maxThreads = 1;
        }

//        this.format = req.getParameter( WMS.Param.FORMAT.toString() );
//        if( null == this.format || 0 == this.format.length() )
//        {
//            String msg = Logging.getMessage("WMS.MissingRequiredParameter", WMS.Param.FORMAT.toString() );
//            Logging.logger().severe(msg);
//            throw new WMSServiceException( msg );
//        }
    }

    private ArrayList<LatLon> parseLatLonPairs(String locations)
    {
        ArrayList<LatLon> latlons = new ArrayList<LatLon>();
        StringTokenizer parser = new StringTokenizer(locations, ";");
        while (parser.hasMoreTokens())
        {
            String pair = parser.nextToken();
            StringTokenizer splitter = new StringTokenizer(pair, ",");
            if (2 == splitter.countTokens())
            {
                try
                {
                    double lon = Double.parseDouble(splitter.nextToken());
                    double lat = Double.parseDouble(splitter.nextToken());
                    latlons.add(new LatLon(Angle.fromDegreesLatitude(lat), Angle.fromDegreesLongitude(lon)));
                }
                catch (Exception e)
                {
                    Logging.logger().severe(e.getMessage());
                }
            }
        }
        return latlons;
    }

    public LatLon[] getLocations()
    {
        LatLon[] array = new LatLon[this.latlons.size()];
        this.latlons.toArray(array);
        return array;
    }

    public String toString()
    {
        StringBuffer str = new StringBuffer("GetElevations: ");

        str.append(" layers(");
        for (int i = 0; i < this.layers.length; i++)
        {
            if (i > 0)
                str.append(", ");
            str.append(this.layers[i]);
        }
        str.append("), locations ( ");
        for (int i = 0; i < this.latlons.size(); i++)
        {
            str.append("{ ").append(this.latlons.get(i)).append(" } ");
        }
        str.append("}");
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
        return null;
    }

    public String getCRS()
    {
        return this.crs;
    }

    public double[] getBBox()
    {
        return new double[] {
            this.bbox.getMinLongitude().degrees,
            this.bbox.getMinLatitude().degrees,
            this.bbox.getMaxLongitude().degrees,
            this.bbox.getMaxLatitude().degrees
        };
    }

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
        return false;
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
        Double color = DEFAULT_NODATA;
        try
        {
            color = Double.parseDouble(this.bgColor);
        }
        catch (Exception ex)
        {
            color = DEFAULT_NODATA;
        }
        return color;
    }

    public Color getBGColorAsRGB()
    {
        if (null == this.bgColor)
            return null;

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

        double latDelta = Math.abs(this.bbox.getDeltaLatDegrees() / height);
        double lonDelta = Math.abs(this.bbox.getDeltaLonDegrees() / width);

        // account half pixel shift (center) for elevation tiles
        return Sector.fromDegrees(
            this.bbox.getMinLatitude().degrees - latDelta,
            this.bbox.getMaxLatitude().degrees + latDelta,
            this.bbox.getMinLongitude().degrees - lonDelta,
            this.bbox.getMaxLongitude().degrees + lonDelta
        );
    }

    public Sector getExtent()
    {
        return this.bbox;
    }

    public void service(HTTPRequest httpReq, HTTPResponse resp) throws IOException, WMSServiceException
    {
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

        LatLon[] locations = this.getLocations();
        int numOfLocations = (null != locations) ? locations.length : 0;

        double bgColor = this.getBGColorAsDouble();

        ByteBufferRaster resultRaster = (ByteBufferRaster) Mosaicer.createDataRaster(3, numOfLocations,
            Sector.EMPTY_SECTOR, AVKey.FLOAT32, bgColor);

        DataRasterFormatter resultFmt = new DataRasterFormatter(resultRaster);

        try
        {
            MapGenerator mapGen = map.getMapGenerator();

//            double maxResolution = mapGen.getPixelSize();
//            double half = Math.abs( maxResolution );

            // build a list of TileTasks (each TileTask has a 1x1 degrees sector associated
            // with it, and later we will add list of points that fall within the sector)
            // Each task will be responsible to retrieve elevation tile and find elevations
            // at every location. Most likely every task will run in it own thread
            this.buildTileTaskList(mapGen);

            // let's assign locations to a specific TileTask
            this.assignLocationsToTileTask();

            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                this.maxThreads, this.maxThreads, 10, TimeUnit.SECONDS, queue);

            try
            {
                // submit tasks for execution
                for (TileTask task : this.tasks.values())
                {
                    executor.execute(task);
                }

                executor.shutdown();

                // wait upto 5min (300 sec)
                executor.awaitTermination(300, TimeUnit.SECONDS);
                if (!executor.isTerminated())
                {
                    Logging.logger().severe(
                        "Force shutdown - " + queue.size() + " were pending out of " + this.tasks.size());
                    executor.shutdownNow();
                }
            }
            catch (Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }

            // extract elevations
            int i = 0;
            for (TileTask task : this.tasks.values())
            {
                for (Position p : task.getPositions())
                {
                    resultRaster.setDoubleAtPosition(i, 0, p.getLatitude().degrees);
                    resultRaster.setDoubleAtPosition(i, 1, p.getLongitude().degrees);
                    resultRaster.setDoubleAtPosition(i, 2, p.getElevation());
                    i++;
                }
            }

            // TODO - sort results
        }
        catch (Exception ex)
        {
            throw new WMSServiceException("Failed to instantiate map-generator: " + ex.toString());
        }

        resp.setStatus(HTTPResponse.OK);
        resp.setContentType(this.getFormat());
        resp.addHeader(HTTP.Header.VARY, HTTP.Header.ACCEPT_ENCODING);

        InputStream inp = HTTP.encodePayload(
            resultFmt.getStreamFromMimeType(this.getFormat(), map.getProperties()),
            this.getFormat(), this.getHttpRequest(), resp);

        resp.setContentLength(inp.available());
        OutputStream out = resp.getOutputStream();
        byte[] buff = new byte[4096];
        int len;
        while ((len = inp.read(buff)) > 0)
        {
            out.write(buff, 0, len);
        }

        resp.flushBuffer();
        inp.close();
    }

    private int getIntegerPart(double degrees)
    {
        return (int) ((degrees < 0d) ? degrees - 1d : degrees);
    }

    private String calcLookupKey(LatLon location)
    {
        if (null == location)
            return null;

        StringBuffer sb = new StringBuffer();

        int lon = this.getIntegerPart(location.getLongitude().degrees);
        sb.append((lon < 0) ? "W" : "E").append(Math.abs(lon));

        int lat = this.getIntegerPart(location.getLatitude().degrees);
        sb.append((lat < 0) ? "S" : "N").append(Math.abs(lat));

        return sb.toString();
    }

    private void buildTileTaskList(MapGenerator mapGen)
    {
        this.tasks.clear();

        int minLon = this.getIntegerPart(this.bbox.getMinLongitude().degrees);
        int maxLon = this.getIntegerPart(this.bbox.getMaxLongitude().degrees);

        int minLat = this.getIntegerPart(this.bbox.getMinLatitude().degrees);
        int maxLat = this.getIntegerPart(this.bbox.getMaxLatitude().degrees);

        for (int lon = minLon; lon <= maxLon; lon++)
        {
            for (int lat = minLat; lat <= maxLat; lat++)
            {
                Sector tileSector = Sector.fromDegrees((double) lat, (double) lat + 1, (double) lon, (double) lon + 1d);
                TileTask task = new TileTask(tileSector, mapGen, this.getBGColor());

                String key = this.calcLookupKey(tileSector.getCentroid());
                this.tasks.put(key, task);
            }
        }
    }

    private void assignLocationsToTileTask()
    {
        double bgColor = this.getBGColorAsDouble();

        for (LatLon ll : this.latlons)
        {
            String key = this.calcLookupKey(ll);
            if (this.tasks.containsKey(key))
            {
                TileTask task = this.tasks.get(key);
                task.add(new Position(ll, bgColor));
            }
            else
            {
                Logging.logger().severe("Impossible! Why there is no task for " + key + " for location " + ll);
            }
        }
    }

    private class TileTask implements Runnable
    {
        private ArrayList<Position> positions = new ArrayList<Position>();

        private Sector tileSector = null;
        private MapGenerator mapGen = null;
        private String bkColor = "-9999";

        public TileTask(Sector s, MapGenerator mapGen, String bkColor)
        {
            if (null == s)
            {
                String msg = Logging.getMessage("nullValue.BoundingBoxIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            if (null == mapGen)
            {
                String msg = Logging.getMessage("WMS.MapGeneratorIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tileSector = s;
            this.mapGen = mapGen;
            this.bkColor = bkColor;
        }

        public void add(Position position)
        {
            if (null != position)
                this.positions.add(position);
        }

        public Position[] getPositions()
        {
            Position[] array = new Position[this.positions.size()];
            this.positions.toArray(array);
            return array;
        }

        public void run()
        {
            if (0 == this.positions.size())
                return;

            // the requested sector must be fully contained within this.tileSector
            if (!this.tileSector.contains(Sector.boundingSector(this.positions)))
            {
                String msg = Logging.getMessage("WMS.Server.InternalError",
                    "bounding box must be contained within tile sector");
                Logging.logger().severe(msg);
                throw new RuntimeException(msg);
            }

            MapGenerator.ServiceInstance mapSvc = this.mapGen.getServiceInstance();
            double maxResolution = Math.abs(mapGen.getPixelSize());
            maxResolution = (0d == maxResolution) ? 0.00027777 : maxResolution;

            // we must use the this.tileSector, otherwise if only few or one pixel is requested, it will nver use max resolution
            int w = (int) Math.abs(this.tileSector.getDeltaLonDegrees() / maxResolution);
            int h = (int) Math.abs(this.tileSector.getDeltaLatDegrees() / maxResolution);
            int size = (int) Math.max(w, h);
            size = (int) Math.min(size, 3600);

            try
            {
                WMSGetElevationsRequest.GetElevationRequest elevRequest =
                    new WMSGetElevationsRequest.GetElevationRequest(this.tileSector, size, size, bkColor);

                DataRasterFormatter fmt = (DataRasterFormatter) mapSvc.serviceRequest(elevRequest);
                ByteBufferRaster raster = (ByteBufferRaster) (fmt.getRaster());

                double defaultElevation;
                try
                {
                    defaultElevation = Double.parseDouble(this.bkColor);
                }
                catch (Exception e)
                {
//                    Logging.logger().finest( e.getMessage() );
                    defaultElevation = -9999d;
                }

                ArrayList<Position> elevations = new ArrayList<Position>();

                int kernelSize = 3;
                double kernelSpatialRadius = ((double) kernelSize) * maxResolution / 2d;

                for (Position ll : this.positions)
                {
                    double lat = ll.getLatitude().degrees;
                    double lon = ll.getLongitude().degrees;

                    Sector kernelSector = Sector.fromDegrees(lat - kernelSpatialRadius, lat + kernelSpatialRadius,
                        lon - kernelSpatialRadius, lon + kernelSpatialRadius);

                    ByteBufferRaster kernelRaster = (ByteBufferRaster) Mosaicer.createDataRaster(
                        kernelSize, kernelSize, kernelSector, AVKey.FLOAT32, defaultElevation);

                    raster.drawOnTo(kernelRaster);
                    elevations.add(new Position(ll, kernelRaster.getDoubleAtPosition(1, 1)));
                }

                this.positions = elevations;
            }
            catch (Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public class GetElevationRequest implements IMapRequest
    {
        protected int width = 1;
        protected int height = 1;
        protected Sector sector = null;
        protected String bgColor = Double.toString(WMSGetElevationsRequest.DEFAULT_NODATA);

        public GetElevationRequest(Sector sector, int width, int height, String bgColor)
        {
            this.sector = sector;
            this.bgColor = bgColor;
            this.width = width;
            this.height = height;
        }

        public GetElevationRequest(Sector sector, String bgColor)
        {
            this.sector = sector;
            this.bgColor = bgColor;
            this.width = 1;
            this.height = 1;
            if (null != bgColor)
                this.bgColor = bgColor;
        }

        public String getFormat()
        {
            return "application/bil32";
        }

        public void setWidth(int width)
        {
            this.width = width;
        }

        public int getWidth()
        {
            return this.width;
        }

        public void setHeight(int height)
        {
            this.height = height;
        }

        public int getHeight()
        {
            return this.height;
        }

        public Sector getSector()
        {
            return this.sector;
        }

        public Sector getExtentForElevationRequest()
        {
            return this.sector;
        }

        public Double getBGColorAsDouble()
        {
            try
            {
                return Double.parseDouble(this.bgColor);
            }
            catch (Exception ignore)
            {
            }
            return WMSGetElevationsRequest.DEFAULT_NODATA;
        }

        public Sector getExtent()
        {
            return this.sector;
        }

        public void setBGColor(String color)
        {
            this.bgColor = color;
        }

        public String getBGColor()
        {
            return this.bgColor;
        }

        public Color getBGColorAsRGB()
        {
            if (null == this.bgColor)
                return null;
            try
            {
                return WWUtil.decodeColorRGBA(this.bgColor);
            }
            catch (Exception ignore)
            {
            }
            return null;
        }

        public double getBBoxXMin()
        {
            return this.sector.getMinLongitude().degrees;
        }

        public double getBBoxXMax()
        {
            return this.sector.getMaxLongitude().degrees;
        }

        public double getBBoxYMin()
        {
            return this.sector.getMinLatitude().degrees;
        }

        public double getBBoxYMax()
        {
            return this.sector.getMaxLatitude().degrees;
        }
    }
}



