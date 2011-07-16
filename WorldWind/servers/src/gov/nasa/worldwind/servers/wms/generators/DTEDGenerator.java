/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.servers.wms.generators;

/**
 * @author Lado Garakanidze
 * @version $Id$
 */

import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.servers.tools.Stats;
import gov.nasa.worldwind.servers.tools.gdal.Option;
import gov.nasa.worldwind.servers.wms.IMapRequest;
import gov.nasa.worldwind.servers.wms.MapSource;
import gov.nasa.worldwind.servers.wms.WMSGetElevationsRequest;
import gov.nasa.worldwind.servers.wms.WMSServiceException;
import gov.nasa.worldwind.servers.wms.formats.DataRasterFormatter;
import gov.nasa.worldwind.servers.wms.formats.ImageFormatter;
import gov.nasa.worldwind.util.FileTree;
import gov.nasa.worldwind.util.Logging;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DTEDGenerator extends AbstractElevationGenerator
{
    public ServiceInstance getServiceInstance()
    {
        return new DTEDServiceInstance();
    }

    public class DTEDFileFilter implements FileFilter
    {
        DTEDLevel[] dtedLevel = null;

        public DTEDFileFilter(DTEDLevel[] dtedLevel)
        {
            this.dtedLevel = dtedLevel;
        }

        public boolean accept(File pathname)
        {
            String filename = null;
            if (null != pathname
                && pathname.isFile()
                && null != (filename = pathname.getName())
                && (filename.endsWith(".dt0") || filename.endsWith(".dt1") || filename.endsWith(".dt2"))
                    )
            {
                try
                {
                    // example of DTED tile : /data/elevations/DTED/Afghanistan/2/e070/n36.dt2
                    String s = pathname.getAbsolutePath().toLowerCase();
                    int len = s.length();

                    String s_level = s.substring(len - 1, len);
                    String latitide = s.substring(len - 6, len - 4);
                    String ns = s.substring(len - 7, len - 6);
                    String longitude = s.substring(len - 11, len - 8);
                    String ew = s.substring(len - 12, len - 11);

                    int lat = Integer.parseInt(latitide);
                    lat = ("s".equals(ns)) ? -lat : lat;
                    if (lat < (int) MIN_LAT || lat > (int) MAX_LAT)
                    {
                        Logging.logger().severe(Logging.getMessage("generic.LatitudeOutOfRange", lat));
                    }

                    int lon = Integer.parseInt(longitude);
                    lon = ("w".equals(ew)) ? -lon : lon;
                    if (lon < (int) MIN_LON || lon > (int) MAX_LON)
                    {
                        Logging.logger().severe(Logging.getMessage("generic.LongitudeOutOfRange", lon));
                    }

                    int dted_level = Integer.parseInt(s_level);
                    if (dted_level > 2 || dted_level < 0)
                    {
                        Logging.logger().severe("DTED level out of range " + dted_level + ", valid range [0-2]");
                    }

                    this.dtedLevel[dted_level].addTile(pathname, lon, lat);

                    Logging.logger().finest("Added --> " + pathname.getAbsolutePath());
                }
                catch (Exception ex)
                {
                    Logging.logger().severe(ex.getMessage());
                }
            }
            return false;
        }
    }

    private void findTiles(File dtedfolder)
    {
        Logging.logger().fine("Searching in the folder " + dtedfolder.getAbsolutePath());

        FileFilter filter = new DTEDFileFilter(this.dtedLevel);
        FileTree fileTree = new FileTree(dtedfolder);
        fileTree.setMode(FileTree.FILES_ONLY);
        fileTree.asList(filter);
    }

    public boolean initialize(MapSource mapSource) throws IOException, WMSServiceException
    {
        this.setThreadId("DTED");
        boolean success = false;

        try
        {
            String srcName = mapSource.getName();
            Properties props = mapSource.getProperties();
            if (props == null)
            {
                throw new IllegalArgumentException("Missing properties in configuration for MapSource: "
                                                   + mapSource.getName());
            }

            this.mapSource = mapSource;

            this.max_tiles_per_request = this.getProperty(props, MAX_TILES_PER_REQUEST, this.max_tiles_per_request,
                    srcName);

            String level_switch_algorithm = this.getProperty(props, "level_switch_algorithm",
                    this.level_switch_algorithm, srcName);
            if (!"exact".equals(level_switch_algorithm) && !"nearest".equals(level_switch_algorithm))
            {
                Logging.logger().severe(this.getThreadId() + "unknown value \"" + level_switch_algorithm
                                        + "\" of \"level-switch-algorithm\" parameters; known values are \"exact\" and \"nearest\"; ");
            }
            else
            {
                this.level_switch_algorithm = level_switch_algorithm;
            }

            File source = new File(this.mapSource.getRootDir());
            if (!source.exists())
            {
                String msg = Logging.getMessage("generic.FolderDoesNotExist", this.mapSource.getRootDir());
                Logging.logger().severe(msg);
                throw new FileNotFoundException(msg);
            }

            try
            {
                String s = DTEDGenerator.this.getMapSource().getMissingDataSignal();
                this.missingDataSignal = (null != s) ? Double.valueOf(s).shortValue() : DTED_MISSING_DATA_SIGNAL;
            }
            catch (Exception e)
            {
                this.missingDataSignal = DTED_MISSING_DATA_SIGNAL;
                Logging.logger().finest(e.toString());
            }

            try
            {
                String s = DTEDGenerator.this.getMapSource().getMissingDataReplacement();
                this.defaultMissingDataReplacement =
                        (null != s) ? Double.valueOf(s).shortValue() : DEFAULT_MISSING_DATA_REPLACEMENT;
            }
            catch (Exception e)
            {
                this.defaultMissingDataReplacement = DEFAULT_MISSING_DATA_REPLACEMENT;
                Logging.logger().finest(e.toString());
            }

            this.dtedLevel = new DTEDLevel[3];
            this.dtedLevel[0] = new DTEDLevel(1d / 120d);
            this.dtedLevel[1] = new DTEDLevel(1d / 1200d);
            this.dtedLevel[2] = new DTEDLevel(1d / 3600d);

            this.findTiles(source);

            this.bbox = null;

            double high_res = Double.MAX_VALUE, low_res = Double.MIN_VALUE;

            for (int i = 0; i <= 2; i++)
            {
                Sector coverage = this.dtedLevel[i].getCoverage();
                if (null != coverage && coverage != Sector.EMPTY_SECTOR)
                {
                    Logging.logger().finest("DTED ( " + i + " ) coverage = " + coverage.toString());
                    this.bbox = Sector.union(this.bbox, coverage);

                    double level_pixel_size = this.dtedLevel[i].getPixelSize();
                    high_res = (level_pixel_size < high_res) ? level_pixel_size : high_res;
                    low_res = (level_pixel_size > low_res) ? level_pixel_size : low_res;
                }
                else
                {
                    Logging.logger().finest("DTED ( " + i + " ) has NO coverage");
                }
            }
            this.bbox = (null == this.bbox) ? Sector.EMPTY_SECTOR : this.bbox;
            this.mapSource.setScaleHint(low_res, high_res);

            Logging.logger().fine(this.mapSource.getName() + " Done adding DTED: coverage={ " + this.bbox.toString()
                                  + " , scaleHints={ low_res=" + low_res + ", high_res=" + high_res + " } ");
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
        return (null == this.bbox) ? Sector.EMPTY_SECTOR : this.bbox;
    }

    public String[] getCRS()
    {
        return new String[]{crsStr};
    }

    public double getPixelSize()
    {
        return this.mapSource.getScaleHintMax();
    }

    // --------------------------------------------
    // class DTEDServiceInstance
    //
    // Used to manage per-request state.
    //
    public class DTEDServiceInstance extends AbstractServiceInstance
    {
        private String threadID = "DTED";

        public String getThreadID()
        {
            return this.threadID;
        }

        public DTEDServiceInstance()
        {
            super();
            this.threadID = new StringBuffer(DTEDGenerator.this.mapSource.getName()).append(" (").append(
                    Thread.currentThread().getId()).append("): ").toString();
        }

        public ImageFormatter serviceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            return this.doServiceRequest(req);
        }

        private DataRasterFormatter doServiceRequest(IMapRequest req) throws IOException, WMSServiceException
        {
            DataRaster targetRaster = Mosaicer.createCompatibleDataRaster(req);
            DataRasterFormatter formatter = new DataRasterFormatter(targetRaster);

            DTEDGenerator gen = DTEDGenerator.this;

            long begTime = System.currentTimeMillis();

            Logging.logger().info(this.getThreadID() + "processing service request ...");

            ArrayList<File> tiles = new ArrayList<File>();

            try
            {
                short missingDataReplacement = gen.defaultMissingDataReplacement;
                try
                {
                    Double d = req.getBGColorAsDouble();
                    missingDataReplacement = (null != d) ? d.shortValue() : gen.defaultMissingDataReplacement;
                }
                catch (Exception ignore)
                {
                }

                Sector reqSector = req.getExtent(); // req.getExtentForElevationRequest();

                if (!reqSector.intersects(gen.getBBox()))
                {
                    Logging.logger().info(this.getThreadID()
                                          + "DTEDGenerator::serviceRequest - out of coverage, skipping "
                                          + reqSector.toString());
                    return formatter;
                }

                double req_pixelSize = reqSector.getDeltaLatDegrees() / (double) req.getHeight();

                int target_dted_level = -1;
                double closest = Double.MAX_VALUE;
                for (int i = 0; i <= 2; i++)
                {
                    // the specific DTED level could be missing or may not even cover the requested area
                    Sector bbox = gen.dtedLevel[i].getCoverage();
                    if (null == bbox || bbox == Sector.EMPTY_SECTOR || !reqSector.intersects(bbox))
                    {
                        continue;
                    }

                    if ("exact".equals(gen.level_switch_algorithm))
                    {
                        target_dted_level = i;
                        if (req_pixelSize >= gen.dtedLevel[i].getPixelSize())
                        {
                            break;
                        }
                    }
                    else if ("nearest".equals(gen.level_switch_algorithm))
                    {
                        double d = Math.abs(req_pixelSize - gen.dtedLevel[i].getPixelSize());
                        if (d < closest) // && reqSector.intersects(gen.dtedLevel[i].getCoverage()))
                        {
                            closest = d;
                            target_dted_level = i;
                        }
                    }
                    else
                    {
                        Logging.logger().severe(this.getThreadID() + "unknown value \"" + gen.level_switch_algorithm
                                                + "\" of \"level-switch-algorithm\" parameters; known values are \"exact\" and \"nearest\"");
                    }
                }

                if (target_dted_level == -1)
                {
                    Logging.logger().severe(this.getThreadID()
                                            + " DTEDGenerator::serviceRequest - no suitable coverage found for " + reqSector.toString()
                                            + " and pixel_size = " + req_pixelSize);
                    return formatter;
                }

                // narrow down coverage and extend the requested extent to overlap with neighboor pixels
                reqSector = req.getExtentForElevationRequest();

                for (int level = target_dted_level; level >= 0; level--)
                {
                    Logging.logger().finest(
                            this.getThreadID() + "DTED(" + level + ") processing the request with pixel size "
                            + req_pixelSize);

                    tiles = gen.dtedLevel[level].getTilesIntersectingWith(reqSector);
                    if (null == tiles || tiles.size() == 0)
                    {
                        continue;
                    }

                    if (tiles.size() > gen.max_tiles_per_request)
                    {   // ignore
                        // TODO zz: garakl: send an WMS error
                        Logging.logger().severe(
                                this.getThreadID() + "Ignoring request: requested too big area; tiles included = "
                                + tiles.size());
                        break;
                    }
                    else if (tiles.size() > 0)
                    {
                        File[] sourceFiles = new File[tiles.size()];
                        sourceFiles = tiles.toArray(sourceFiles);

                        DataRaster raster = null;

                        if (req instanceof WMSGetElevationsRequest.GetElevationRequest)
                        {
                            // this is a "HACK" to accelerate GetElevations queries for max resolution
                            raster = Mosaicer.mosaicElevations(
                                    this.getThreadID(),
                                    sourceFiles, reqSector,
                                    req.getWidth(), req.getHeight(),
                                    gen.missingDataSignal,
                                    missingDataReplacement,
                                    ("application/bil32".equals(req.getFormat())) ? "Float32" : "Int16",
                                    Option.Warp.Resampling.Bilinear
                            );

                            if (null != raster)
                            {
                                formatter = new DataRasterFormatter(raster);
                            }

                            break;
                        }
                        else
                        {
                            raster = Mosaicer.mosaicElevations(this.getThreadID(), sourceFiles, reqSector,
                                    req.getWidth(), req.getHeight(),
                                    gen.missingDataSignal,
                                    missingDataReplacement,
                                    ("application/bil32".equals(req.getFormat())) ? "Float32" : "Int16",
                                    Option.Warp.Resampling.Cubic);

                            if (null != raster)
                            {
                                formatter.merge(raster);
                            }
                            else
                            {
                                Logging.logger().severe(this.getThreadID()
                                                        + "attempt to generate a request tile is failed - source is null");
                            }

                            if (formatter.hasNoDataAreas())
                            {
                                Logging.logger().finest(this.getThreadID() + "After DTED(" + level
                                                        + ") processing the requested image still has missing areas!!!");
                            }
                            else
                            {
                                break;
                            }
                        }
                    }
                }

                long ellapsed = System.currentTimeMillis() - begTime;
                Stats.report("DTED", tiles.size(), ellapsed);
                Logging.logger().info(this.getThreadID()
                                      + "DONE " + tiles.size() + " tiles in " + ellapsed + " msec. " + Stats.getStats("DTED"));
            }
            catch (Exception ex)
            {
                String msg = Logging.getMessage("WMS.RequestFailed", ex.getMessage());
                Logging.logger().log(java.util.logging.Level.SEVERE, this.getThreadID() + msg, ex);
                throw new WMSServiceException(msg);
            }

            return formatter;
        }

        public void freeResources()
        { /* No-op */ }
    }

    private class DTEDLevel
    {
        private File[][] tiles = new File[360][180];
        private Sector coverage = null;
        private double pixelSize = 0;

        public DTEDLevel(double pixelSize)
        {
            this.pixelSize = pixelSize;
        }

        public double getPixelSize()
        {
            return this.pixelSize;
        }

        public Sector getCoverage()
        {
            return (null == this.coverage) ? Sector.EMPTY_SECTOR : this.coverage;
        }

        public boolean hasCoverage(Sector s)
        {
            return (null != this.coverage && this.coverage.intersects(s));
        }

        public void addTile(File tile, int lon, int lat)
        {
            if (null == tile)
            {
                throw new IllegalArgumentException(Logging.getMessage("nullValue.FileIsNull"));
            }

            if (lon < (int) MIN_LON || lon > (int) MAX_LON)
            {
                throw new IllegalArgumentException(Logging.getMessage("generic.LongitudeOutOfRange", lon));
            }

            if (lat < (int) MIN_LAT || lat > (int) MAX_LAT)
            {
                throw new IllegalArgumentException(Logging.getMessage("generic.LatitudeOutOfRange", lat));
            }

            this.tiles[lon + (int) MAX_LON][lat + (int) MAX_LAT] = tile;

            this.coverage = Sector.union(this.coverage,
                    Sector.fromDegrees((double) lat, (double) lat + 1d, (double) lon, (double) lon + 1d));
        }

        public File[][] getTiles()
        {
            return this.tiles;
        }

        public File getTile(int lon, int lat)
        {
            if (lon < (int) MIN_LON || lon > (int) MAX_LON)
            {
                throw new IllegalArgumentException(Logging.getMessage("generic.LongitudeOutOfRange", lon));
            }

            if (lat < (int) MIN_LAT || lat > (int) MAX_LAT)
            {
                throw new IllegalArgumentException(Logging.getMessage("generic.LatitudeOutOfRange", lat));
            }

            return this.tiles[lon + (int) MAX_LON][lat + (int) MAX_LAT];
        }

        public Sector getTileSector(int lon, int lat)
        {
            if (lon < (int) MIN_LON || lon > (int) MAX_LON)
            {
                throw new IllegalArgumentException(Logging.getMessage("generic.LongitudeOutOfRange", lon));
            }

            if (lat < (int) MIN_LAT || lat > (int) MAX_LAT)
            {
                throw new IllegalArgumentException(Logging.getMessage("generic.LatitudeOutOfRange", lat));
            }

            return Sector.fromDegrees((double) lat, (double) lat + 1d, (double) lon, (double) lon + 1d);
        }

        public ArrayList<File> getTilesIntersectingWith(Sector reqSector)
        {
            ArrayList<File> tiles = new ArrayList<File>();

            if (!reqSector.intersects(this.getCoverage()))
            {
                return tiles;
            }

            int iMinX = Math.min((int) reqSector.getMinLongitude().degrees, (int) coverage.getMinLongitude().degrees);
            int iMaxX = Math.min((int) reqSector.getMaxLongitude().degrees, (int) coverage.getMaxLongitude().degrees);
            int iMinY = Math.min((int) reqSector.getMinLatitude().degrees, (int) coverage.getMinLatitude().degrees);
            int iMaxY = Math.min((int) reqSector.getMaxLatitude().degrees, (int) coverage.getMaxLatitude().degrees);

            // determine the tiles overlapped by the request...

            locate_intersecting_tiles:
            {
                for (int ix = iMinX; ix <= iMaxX; ix++)
                {
                    for (int iy = iMinY; iy <= iMaxY; iy++)
                    {
                        Sector tileSector = this.getTileSector(ix, iy);
                        Sector overlap = reqSector.intersection(tileSector);

                        // Skip if no overlap or intersect exactly at an edge...
                        if (overlap == null || overlap.getDeltaLon().degrees <= 0.
                            || overlap.getDeltaLat().degrees <= 0.)
                        {
                            continue;
                        }

                        try
                        {
                            File tile = this.getTile(ix, iy);
                            if (null != tile && tile.exists())
                            {
                                if (tileSector.contains(reqSector))
                                {
                                    tiles.clear();
                                    tiles.add(tile);
                                    break locate_intersecting_tiles;
                                }
                                else
                                {
                                    tiles.add(tile);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Logging.logger().finest(e.getMessage());
                        }
                    }
                }
            }

            return tiles;
        }
    }

    private static final double MIN_LON = -180d;
    private static final double MAX_LON = 180d;
    private static final double MIN_LAT = -90d;
    private static final double MAX_LAT = 90d;
    private static final int TILE_SIZE = 1;  // DTED is always 1 degree x 1 degree
    private static final String crsStr = "EPSG:4326";

    // performance tuning parameters...

    protected static final short DEFAULT_MISSING_DATA_REPLACEMENT = Short.MIN_VALUE;
    protected static final short DTED_MISSING_DATA_SIGNAL = -32767;

    protected short defaultMissingDataReplacement = DEFAULT_MISSING_DATA_REPLACEMENT;
    protected short missingDataSignal = DTED_MISSING_DATA_SIGNAL;

    protected Sector bbox = null;

    protected DTEDLevel[] dtedLevel = null;
    protected String level_switch_algorithm = "exact";

    // Configuration property keys...
    protected int max_tiles_per_request = 101; // 25;
    protected static final String MAX_TILES_PER_REQUEST = "max_tiles_per_request";
}
