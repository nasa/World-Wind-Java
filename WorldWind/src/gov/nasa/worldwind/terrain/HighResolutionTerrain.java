/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.terrain;

import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Provides operations on the best available terrain. Operations such as line/terrain intersection and surface point
 * computation use the highest resolution terrain data available from the globe's elevation model. Because the best
 * available data may not be available when the operations are performed, the operations block while they retrieve the
 * required data from either the local disk cache or a remote server. A timeout may be specified to limit the amount of
 * time allowed for retrieving data. Operations fail if the timeout is exceeded.
 *
 * @author tag
 * @version $Id$
 */
public class HighResolutionTerrain extends WWObjectImpl implements Terrain
{
    /** Holds a tile's geometry. It's heavyweight so cached when created to enable re-use. */
    protected static class RenderInfo
    {
        protected final int density;
        protected final Vec4 referenceCenter; // all vertices are relative to this point
        protected final float[] vertices;

        protected RenderInfo(int density, float[] vertices, Vec4 refCenter)
        {
            this.density = density;
            this.referenceCenter = refCenter;
            this.vertices = vertices;
        }

        protected long getSizeInBytes()
        {
            // 2 references, an int, and vertices. (indices are shared among all tiles)
//            System.out.println(2 * 4 + 4 + this.vertices.length * 3 * 4);
            return 2 * 4 + 4 + this.vertices.length * 3 * 4; // 588 bytes at a density of 3
        }
    }

    // The RectTile class is meant to be small and quick to construct so that many can exist and they can be created
    // anew for each intersection or getSurfacePoint operation.

    /**
     * Defines an internal terrain tile. This class is meant to be small and quickly constructed so that many can exist
     * simultaneously and can be created anew for each operation. The geometry they refer to is cached and is
     * independent of a particular RectTile instance. Current and future RectTile instances use the same cached geometry
     * instance.
     */
    protected static class RectTile
    {
        protected final Sector sector;
        protected final int density;
        protected Extent extent; // extent of sector in object coordinates
        protected RenderInfo ri;

        public RectTile(Extent extent, int density, Sector sector)
        {
            this.density = density;
            this.sector = sector;
            this.extent = extent;
        }
    }

    protected static final int DEFAULT_DENSITY = 3;
    protected static final long DEFAULT_CACHE_CAPACITY = (long) 20e6; // about 34,000 RenderInfos at a density of 20

    // User-specified fields.
    protected Globe globe;
    protected Sector sector;
    protected double verticalExaggeration = 1;
    protected Long timeout;

    // Internal fields.
    protected int density = DEFAULT_DENSITY;
    protected double targetResolution;
    protected double latTileSize;
    protected double lonTileSize;
    protected int numRows;
    protected int numCols;
    protected int[] indices;
    protected MemoryCache geometryCache;
    protected ThreadLocal<Long> startTime = new ThreadLocal<Long>();

    /**
     * Constructs a terrain object for a specified globe.
     *
     * @param globe            the terrain's globe.
     * @param targetResolution the target terrain resolution, in meters, or null to use the globe's highest resolution.
     *
     * @throws IllegalArgumentException if the globe is null.
     */
    public HighResolutionTerrain(Globe globe, Double targetResolution)
    {
        this(globe, null, targetResolution, null);
    }

    /**
     * Constructs a terrain object for a specified sector of a specified globe.
     *
     * @param globe                the terrain's globe.
     * @param sector               the desired range for the terrain. Only locations within this sector may be used in
     *                             operations. If null, the sector spans the entire globe.
     * @param targetResolution     the target terrain resolution, in meters, or null to use the globe's highest
     *                             resolution.
     * @param verticalExaggeration the vertical exaggeration to apply to terrain elevations. Null indicates no
     *                             exaggeration.
     *
     * @throws IllegalArgumentException if the globe is null.
     */
    public HighResolutionTerrain(Globe globe, Sector sector, Double targetResolution, Double verticalExaggeration)
    {
        if (globe == null)
        {
            String msg = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.globe = globe;
        this.sector = sector != null ? sector : Sector.FULL_SPHERE;

        if (targetResolution != null)
            this.targetResolution = targetResolution / this.globe.getRadius();
        else
            this.targetResolution = this.globe.getElevationModel().getBestResolution(null);

        this.verticalExaggeration = verticalExaggeration != null ? verticalExaggeration : 1;

        this.indices = this.getIndices(this.density);

        this.computeDimensions();

        this.geometryCache = new BasicMemoryCache((long) (0.85 * DEFAULT_CACHE_CAPACITY), DEFAULT_CACHE_CAPACITY);
//        this.geometryCache.addCacheListener(new MemoryCache.CacheListener()
//        {
//            public void entryRemoved(Object key, Object clientObject)
//            {
//                long cap = geometryCache.getCapacity();
//                long cs = geometryCache.getUsedCapacity();
//                long no = geometryCache.getNumObjects();
//                System.out.printf("CACHE CLEAN capacity %d, used %d, num entries %d\n", cap, cs, no);
//            }
//        });
    }

    /**
     * Indicates the proportion of the cache currently used.
     *
     * @return the fraction of the cache currently used: a number between 0 and 1.
     */
    public double getCacheUsage()
    {
        return this.geometryCache.getUsedCapacity() / (double) this.geometryCache.getCapacity();
    }

    /**
     * Returns the number of entries currently in the cache.
     *
     * @return the number of entries currently in the cache.s
     */
    public int getNumCacheEntries()
    {
        return this.geometryCache.getNumObjects();
    }

    /**
     * Returns the object's globe.
     *
     * @return the globe specified to the constructor.
     */
    public Globe getGlobe()
    {
        return globe;
    }

    /**
     * Returns the object's sector.
     *
     * @return the object's sector, either the sector specified at construction or the default sector if no sector was
     *         specified at construction.
     */
    public Sector getSector()
    {
        return sector;
    }

    public double getTargetResolution()
    {
        return targetResolution;
    }

    /**
     * Indicates the vertical exaggeration used when performing terrain operations.
     *
     * @return the vertical exaggeration. The default is 1: no exaggeration.
     */
    public double getVerticalExaggeration()
    {
        return this.verticalExaggeration;
    }

    /**
     * Indicates the current timeout for operations requiring terrain data retrieval.
     *
     * @return the current timeout, in milliseconds. May be null.
     *
     * @see #setTimeout(Long)
     */
    public synchronized Long getTimeout()
    {
        return this.timeout;
    }

    /**
     * Specifies the maximum amount of time allowed for retrieval of all terrain data necessary to satisfy an operation.
     * Operations that retrieve data throw a {@link gov.nasa.worldwind.exception.WWTimeoutException} if the specified
     * timeout is exceeded.
     *
     * @param timeout the number of milliseconds to wait. May be null, to indicate that operations have unlimited amount
     *                of time to operate.
     */
    public synchronized void setTimeout(Long timeout)
    {
        this.timeout = timeout;
    }

    /**
     * Indicates the current cache capacity.
     *
     * @return the current cache capacity, in bytes.
     */
    public long getCacheCapacity()
    {
        return this.geometryCache.getCapacity();
    }

    /**
     * Specifies the cache capacity.
     *
     * @param size the cache capacity, in bytes. Values less than 1 MB are clamped to 1 MB.
     */
    public void setCacheCapacity(long size)
    {
        this.geometryCache.setCapacity(Math.max(size, (long) 1e6));
    }

    /**
     * Computes the Cartesian, model-coordinate point of a position on the terrain.
     * <p/>
     * This operation fails with a {@link gov.nasa.worldwind.exception.WWTimeoutException} if a timeout has been
     * specified and it is exceeded during the operation.
     *
     * @param position the position.
     *
     * @return the Cartesian, model-coordinate point of the specified position, or null if the specified position does
     *         not exist within this instance's sector or if the operation is interrupted.
     *
     * @throws IllegalArgumentException if the position is null.
     * @throws gov.nasa.worldwind.exception.WWTimeoutException
     *                                  if the current timeout is exceeded while retrieving terrain data.
     * @throws gov.nasa.worldwind.exception.WWRuntimeException
     *                                  if the operation is interrupted.
     * @see #setTimeout(Long)
     */
    public Vec4 getSurfacePoint(Position position)
    {
        return this.getSurfacePoint(position.getLatitude(), position.getLongitude(), position.getAltitude());
    }

    /**
     * Computes the Cartesian, model-coordinate point of a location on the terrain.
     * <p/>
     * This operation fails with a {@link gov.nasa.worldwind.exception.WWTimeoutException} if a timeout has been
     * specified and it is exceeded during the operation.
     *
     * @param latitude     the location's latitude.
     * @param longitude    the location's longitude.
     * @param metersOffset the location's distance above the terrain.
     *
     * @return the Cartesian, model-coordinate point of the specified location, or null if the specified location does
     *         not exist within this instance's sector or if the operation is interrupted.
     *
     * @throws IllegalArgumentException if the latitude or longitude are null.
     * @throws gov.nasa.worldwind.exception.WWTimeoutException
     *                                  if the current timeout is exceeded while retrieving terrain data.
     * @throws WWRuntimeException       if the operation is interrupted.
     * @see #setTimeout(Long)
     */
    public Vec4 getSurfacePoint(Angle latitude, Angle longitude, double metersOffset)
    {
        if (latitude == null || longitude == null)
        {
            String msg = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        try
        {
            this.startTime.set(System.currentTimeMillis());

            RectTile tile = this.getContainingTile(latitude, longitude);

            return tile != null ? this.getSurfacePoint(tile, latitude, longitude, metersOffset) : null;
        }
        catch (InterruptedException e)
        {
            throw new WWRuntimeException(e);
        }
        finally
        {
            this.startTime.set(null); // signals that no operation is active
        }
    }

    /** {@inheritDoc} */
    public Double getElevation(LatLon location)
    {
        if (location == null)
        {
            String msg = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        // TODO: Why isn't this just getting the elevation directly from the elevation model?
        Vec4 pt = this.getSurfacePoint(location.getLatitude(), location.getLongitude(), 0);
        if (pt == null)
            return null;

        Vec4 p = this.globe.computePointFromPosition(location.getLatitude(), location.getLongitude(), 0);

        return p.distanceTo3(pt) * (pt.getLength3() >= p.getLength3() ? 1 : -1);
    }

    /**
     * Computes the intersections of a line with the terrain. The line is specified by two positions. All intersection
     * points are returned.
     * <p/>
     * This operation fails with a {@link gov.nasa.worldwind.exception.WWTimeoutException} if a timeout has been
     * specified and it is exceeded during the operation.
     *
     * @param pA the line's first position.
     * @param pB the line's second position.
     *
     * @return an array of Cartesian model-coordinate intersection points, or null if no intersections occur or the
     *         operation is interrupted.
     *
     * @throws IllegalArgumentException if either position is null.
     * @throws gov.nasa.worldwind.exception.WWTimeoutException
     *
     * @throws WWRuntimeException       if the operation is interrupted. if the current timeout is exceeded while
     *                                  retrieving terrain data.
     * @see #setTimeout(Long)
     */
    public Intersection[] intersect(Position pA, Position pB)
    {
        if (pA == null || pB == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        try
        {
            this.startTime.set(System.currentTimeMillis());

            return this.doIntersect(pA, pB);
        }
        catch (InterruptedException e)
        {
            throw new WWRuntimeException(e);
        }
        finally
        {
            this.startTime.set(null); // signals that no operation is active
        }
    }

    /**
     * Cause the tiles used by subsequent intersection calculations to be cached so that they are available immediately
     * to those subsequent calculations.
     * <p/>
     * Pre-caching is unnecessary and is useful only when it can occur before the intersection calculations are needed.
     * It will incur extra overhead otherwise. The normal intersection calculations cause the same caching, so that
     * subsequent calculations on the same area will be faster.
     *
     * @param pA the line's first position. Identical to the same argument of {@link #intersect(gov.nasa.worldwind.geom.Position,
     *           gov.nasa.worldwind.geom.Position)}.
     * @param pB the line's second position. Identical to the same argument of {@link #intersect(gov.nasa.worldwind.geom.Position,
     *           gov.nasa.worldwind.geom.Position)}.
     *
     * @throws IllegalArgumentException if either position is null.
     * @throws WWRuntimeException       if the operation is interrupted. if the current timeout is exceeded while
     *                                  retrieving terrain data.
     */
    public void cacheIntersectingTiles(Position pA, Position pB)
    {
        if (pA == null || pB == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        try
        {
            this.startTime.set(System.currentTimeMillis());

            List<RectTile> tiles = this.getIntersectingTiles(pA, pB);
            if (tiles == null)
                return;

            for (RectTile tile : tiles)
            {
                this.makeVerts(tile);

                if (this.geometryCache.getFreeCapacity() < (long) 10e3) // cache is full so stop caching
                    return;
            }
        }
        catch (InterruptedException e)
        {
            throw new WWRuntimeException(e);
        }
        finally
        {
            this.startTime.set(null); // signals that no operation is active
        }
    }

    public List<Sector> getIntersectionTiles(Position pA, Position pB)
    {
        List<RectTile> tiles = this.getIntersectingTiles(pA, pB);
        if (tiles == null || tiles.size() == 0)
            return null;

        List<Sector> sectors = new ArrayList<Sector>(tiles.size());

        for (RectTile tile : tiles)
        {
            sectors.add(tile.sector);
        }

        return sectors;
    }
//
//    public IntBuffer getIntersectingTilesGeometry(Position pA, Position pB, List<FloatBuffer> vertices,
//        List<Vec4> refCenters) throws InterruptedException
//    {
//        List<RectTile> tiles = this.getIntersectingTiles(pA, pB);
//        if (tiles == null || tiles.size() == 0)
//            return null;
//
//        for (RectTile tile : tiles)
//        {
//            this.makeVerts(tile);
//            if (tile.ri == null)
//            {
//                System.out.println("NOT CACHED " + tile.sector);
//                return null;
//            }
//
//            vertices.add(BufferUtil.newFloatBuffer(tile.ri.vertices.length).put(tile.ri.vertices));
//            refCenters.add(tile.ri.referenceCenter);
//        }
//
//        return BufferUtil.newIntBuffer(this.indices.length).put(this.indices);
//    }

    /** Computes the row and column dimensions of the tile array. */
    protected void computeDimensions()
    {
        double resTarget = Math.max(this.globe.getElevationModel().getBestResolution(null), this.targetResolution);

        this.numCols = (int) Math.ceil(this.sector.getDeltaLonRadians() / (this.density * resTarget));
        this.numRows = (int) Math.ceil(this.sector.getDeltaLatRadians() / (this.density * resTarget));

        this.lonTileSize = this.sector.getDeltaLonDegrees() / this.numCols;
        this.latTileSize = this.sector.getDeltaLatDegrees() / this.numRows;
    }

    /**
     * Determines the tile that contains a specfied location.
     *
     * @param latitude  the location's latitude.
     * @param longitude the location's longitude.
     *
     * @return the tile containing the specified location.
     */
    protected RectTile getContainingTile(Angle latitude, Angle longitude)
    {
        if (!this.sector.contains(latitude, longitude))
            return null;

        int row = this.computeRow(this.sector, latitude);
        int col = this.computeColumn(this.sector, longitude);

        return this.createTile(row, col);
    }

    /**
     * Creates a tile for a specified row and column of the tile array.
     *
     * @param row the tile's 0-origin row index.
     * @param col the tile's 0-origin column index.
     *
     * @return the tile for the specified row and column, or null if the row or column are invalid.
     */
    protected RectTile createTile(int row, int col)
    {
        if (row < 0 || col < 0 || row >= this.numRows || col >= this.numCols)
            return null;

        double minLon = Math.max(this.sector.getMinLongitude().degrees + col * this.lonTileSize, -180);
        double maxLon = Math.min(minLon + this.lonTileSize, 180);

        double minLat = Math.max(this.sector.getMinLatitude().degrees + row * this.latTileSize, -90);
        double maxLat = Math.min(minLat + this.latTileSize, 90);

        return this.createTile(Sector.fromDegrees(minLat, maxLat, minLon, maxLon));
    }

    /**
     * Creates the tile for a specified sector.
     *
     * @param tileSector the sector for which to create the tile.
     *
     * @return the tile for the sector, or null if the sector is outside this instance's sector.
     */
    protected RectTile createTile(Sector tileSector)
    {
        Extent extent = Sector.computeBoundingBox(this.globe, this.verticalExaggeration, tileSector);

        return new RectTile(extent, this.density, tileSector);
    }

    /**
     * Computes the row index corresponding to a specified latitude.
     *
     * @param range    the reference sector, typically that of this terrain instance.
     * @param latitude the latitude in question.
     *
     * @return the row index for the sector.
     */
    protected int computeRow(Sector range, Angle latitude)
    {
        double top = range.getMaxLatitude().degrees;
        double bot = range.getMinLatitude().degrees;

        double s = (latitude.degrees - bot) / (top - bot);

        return (int) (s * (double) this.numRows);
    }

    /**
     * Computes the column index corresponding to a specified latitude.
     *
     * @param range     the reference sector, typically that of this terrain instance.
     * @param longitude the latitude in question.
     *
     * @return the column index for the sector.
     */
    protected int computeColumn(Sector range, Angle longitude)
    {
        double right = range.getMaxLongitude().degrees;
        double left = range.getMinLongitude().degrees;

        double s = (longitude.degrees - left) / (right - left);

        return (int) (s * (double) this.numCols);
    }

    /**
     * Computes intersections of a line with the terrain.
     *
     * @param pA the line's first position.
     * @param pB the line's second position.
     *
     * @return an array of intersections, or null if no intersections occur.
     *
     * @throws InterruptedException if the operation is interrupted.
     */
    protected Intersection[] doIntersect(Position pA, Position pB) throws InterruptedException
    {
        if (pA == null || pB == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        List<RectTile> tiles = this.getIntersectingTiles(pA, pB);
        if (tiles == null)
            return null;

        RectTile tileA = this.getContainingTile(pA.getLatitude(), pA.getLongitude());
        RectTile tileB = this.getContainingTile(pB.getLatitude(), pB.getLongitude());
        if (tileA == null || tileB == null)
            return null;

        Vec4 ptA = this.getSurfacePoint(tileA, pA.getLatitude(), pA.getLongitude(), pA.getAltitude());
        Vec4 ptB = this.getSurfacePoint(tileB, pB.getLatitude(), pB.getLongitude(), pB.getAltitude());
        if (ptA == null || ptB == null)
            return null;

        if (pA.getLatitude().equals(pB.getLatitude()) && pA.getLongitude().equals(pB.getLongitude()))
            return null;

        Line line = new Line(ptA, ptB.subtract3(ptA).normalize3());

        Intersection[] hits;
        ArrayList<Intersection> list = new ArrayList<Intersection>();
        for (RectTile tile : tiles)
        {
            if (tile.extent.intersects(line))
            {
                if ((hits = this.intersect(tile, line)) != null)
                    list.addAll(Arrays.asList(hits));
            }
        }

        if (list.size() == 0)
            return null;

        hits = new Intersection[list.size()];
        list.toArray(hits);

        if (list.size() == 1)
            return hits;

        final Vec4 origin = line.getOrigin();
        Arrays.sort(hits, new Comparator<Intersection>()
        {
            public int compare(Intersection i1, Intersection i2)
            {
                if (i1 == null && i2 == null)
                    return 0;
                if (i2 == null)
                    return -1;
                if (i1 == null)
                    return 1;

                Vec4 v1 = i1.getIntersectionPoint();
                Vec4 v2 = i2.getIntersectionPoint();
                double d1 = origin.distanceTo3(v1);
                double d2 = origin.distanceTo3(v2);
                return Double.compare(d1, d2);
            }
        });

        return hits;
    }

    /**
     * Determines and creates the terrain tiles intersected by a specified line.
     *
     * @param pA the line's first position.
     * @param pB the line's second position.
     *
     * @return a list of tiles that likely intersect the line. Some returned tiles may not intersect the line but will
     *         only be near it.
     */
    protected List<RectTile> getIntersectingTiles(LatLon pA, LatLon pB)
    {
        int rowA = this.computeRow(this.sector, pA.getLatitude());
        int colA = this.computeColumn(this.sector, pA.getLongitude());
        int rowB = this.computeRow(this.sector, pB.getLatitude());
        int colB = this.computeColumn(this.sector, pB.getLongitude());

        List<Point> cells = WWMath.bresenham(colA, rowA, colB, rowB);
        if (cells == null || cells.size() == 0)
            return null;

        List<RectTile> tiles = new ArrayList<RectTile>(3 * cells.size());
        for (Point cell : cells)
        {
            RectTile centerCell = this.createTile(cell.y, cell.x);

            RectTile upperCell = this.createTile(cell.y + 1, cell.x);
            RectTile lowerCell = this.createTile(cell.y - 1, cell.x);
            RectTile leftCell = this.createTile(cell.y, cell.x - 1);
            RectTile rightCell = this.createTile(cell.y, cell.x + 1);

            if (centerCell != null)
                tiles.add(centerCell);
            if (upperCell != null)
                tiles.add(upperCell);
            if (lowerCell != null)
                tiles.add(lowerCell);
            if (leftCell != null)
                tiles.add(leftCell);
            if (rightCell != null)
                tiles.add(rightCell);
        }

        return tiles.size() > 0 ? tiles : null;
    }

    /**
     * Computes a terrain tiles's vertices of draws them from the cache.
     *
     * @param tile the tile to compute vertices forl
     *
     * @throws InterruptedException if the operation is interrupted.
     * @throws gov.nasa.worldwind.exception.WWTimeoutException
     *                              if terrain data retrieval exceedes the currnet timeout.
     */
    protected void makeVerts(RectTile tile) throws InterruptedException
    {
        // First see if the vertices have been previously computed and are in the cache.
        tile.ri = (RenderInfo) this.geometryCache.getObject(tile.sector);
        if (tile.ri != null)
            return;
//
//        System.out.println(
//            "CACHE MISS " + this.geometryCache.getUsedCapacity() + ", " + this.geometryCache.getNumObjects());

        tile.ri = this.buildVerts(tile);
        if (tile.ri != null)
        {
            this.geometryCache.add(tile.sector, tile.ri, tile.ri.getSizeInBytes());
        }
    }

    /**
     * Computes a terrain tiles's vertices.
     *
     * @param tile the tile to compute vertices forl
     *
     * @return the computed vertex information.
     *
     * @throws InterruptedException if the operation is interrupted.
     * @throws gov.nasa.worldwind.exception.WWTimeoutException
     *                              if terrain data retrieval exceedes the currnet timeout.
     */
    protected RenderInfo buildVerts(RectTile tile) throws InterruptedException
    {
        int density = tile.density;
        int numVertices = (density + 1) * (density + 1);

        float[] verts;

        //Re-use the RenderInfo vertices buffer. If it has not been set or the density has changed, create a new buffer
        if (tile.ri == null || tile.ri.vertices == null)
        {
            verts = new float[numVertices * 3];
        }
        else
        {
            verts = tile.ri.vertices;
        }

        ArrayList<LatLon> latlons = this.computeLocations(tile);
        double[] elevations = new double[latlons.size()];

        // In general, the best attainable resolution varies over the elevation model, so determine the best
        // attainable ^for this tile^ and use that as the convergence criteria.
        double localTargetResolution =
            Math.max(this.globe.getElevationModel().getBestResolution(tile.sector), this.targetResolution);
        this.getElevations(tile.sector, latlons, localTargetResolution, elevations);

        LatLon centroid = tile.sector.getCentroid();
        Vec4 refCenter = globe.computePointFromPosition(centroid.getLatitude(), centroid.getLongitude(), 0d);

        int ie = 0;
        int iv = 0;
        Iterator<LatLon> latLonIter = latlons.iterator();
        for (int j = 0; j <= density; j++)
        {
            for (int i = 0; i <= density; i++)
            {
                LatLon latlon = latLonIter.next();
                double elevation = this.verticalExaggeration * elevations[ie++];

                Vec4 p = this.globe.computePointFromPosition(latlon.getLatitude(), latlon.getLongitude(), elevation);
                verts[iv++] = (float) (p.x - refCenter.x);
                verts[iv++] = (float) (p.y - refCenter.y);
                verts[iv++] = (float) (p.z - refCenter.z);
            }
        }

        return new RenderInfo(density, verts, refCenter);
    }

    protected double getElevations(Sector sector, List<LatLon> latlons, double targetResolution, double[] elevations)
        throws InterruptedException
    {
        double actualResolution = Double.MAX_VALUE;
        while (actualResolution > targetResolution)
        {
            actualResolution = this.globe.getElevations(sector, latlons, targetResolution, elevations);
            // Uncomment the two lines below if you want to watch the resolution converge
//                System.out.printf("Target resolution = %s, Actual resolution = %s\n",
//                    Double.toString(targetResolution), Double.toString(actualResolution));

            if (actualResolution <= targetResolution)
                break;

            // Give the system a chance to retrieve data from the disk cache or the server. Also catches interrupts
            // and throws interrupt exceptions.
            Thread.sleep(5);

            Long timeout = this.getTimeout();
            if (this.startTime.get() != null && timeout != null)
            {
                if (System.currentTimeMillis() - this.startTime.get() > timeout)
                    throw new WWRuntimeException("Terrain convergence timed out");
            }
        }

        return actualResolution;
    }

    /**
     * Computes the tile's cell locations, determined by the tile's density and sector.
     *
     * @param tile the tile to compute locations for.
     *
     * @return the cell locations.
     */
    protected ArrayList<LatLon> computeLocations(RectTile tile)
    {
        int density = tile.density;
        int numVertices = (density + 1) * (density + 1);

        Angle latMax = tile.sector.getMaxLatitude();
        Angle dLat = tile.sector.getDeltaLat().divide(density);
        Angle lat = tile.sector.getMinLatitude();

        Angle lonMin = tile.sector.getMinLongitude();
        Angle lonMax = tile.sector.getMaxLongitude();
        Angle dLon = tile.sector.getDeltaLon().divide(density);

        ArrayList<LatLon> latlons = new ArrayList<LatLon>(numVertices);
        for (int j = 0; j <= density; j++)
        {
            Angle lon = lonMin;
            for (int i = 0; i <= density; i++)
            {
                latlons.add(new LatLon(lat, lon));

                if (i == density)
                    lon = lonMax;
                else
                    lon = lon.add(dLon);

                if (lon.degrees < -180)
                    lon = Angle.NEG180;
                else if (lon.degrees > 180)
                    lon = Angle.POS180;
            }

            if (j == density)
                lat = latMax;
            else
                lat = lat.add(dLat);
        }

        return latlons;
    }

    /**
     * Computes the Cartesian, model-coordinate point of a location within a terrain tile.
     * <p/>
     * This operation fails with a {@link gov.nasa.worldwind.exception.WWTimeoutException} if a timeout has been
     * specified and it is exceeded during the operation.
     *
     * @param tile         the terrain tile.
     * @param latitude     the location's latitude.
     * @param longitude    the location's longitude.
     * @param metersOffset the location's distance above the terrain.
     *
     * @return the Cartesian, model-coordinate point of a the specified location, or null if the specified location does
     *         not exist within this instance's sector or if the operation is interrupted.
     *
     * @throws IllegalArgumentException if the latitude or longitude are null.
     * @throws gov.nasa.worldwind.exception.WWTimeoutException
     *                                  if the current timeout is exceeded while retrieving terrain data.
     * @throws InterruptedException     if the operation is interrupted.
     * @see #setTimeout(Long)
     */
    protected Vec4 getSurfacePoint(RectTile tile, Angle latitude, Angle longitude, double metersOffset)
        throws InterruptedException
    {
        Vec4 result = this.getSurfacePoint(tile, latitude, longitude);
        if (metersOffset != 0 && result != null)
            result = applyOffset(result, metersOffset);

        return result;
    }

    /**
     * Applies a specified vertical offset to a surface point.
     *
     * @param point        the surface point.
     * @param metersOffset the vertical offset to add to the point.
     *
     * @return a new point offset the specified amount from the input point.
     */
    protected Vec4 applyOffset(Vec4 point, double metersOffset)
    {
        Vec4 normal = this.globe.computeSurfaceNormalAtPoint(point);
        point = Vec4.fromLine3(point, metersOffset, normal);
        return point;
    }

    /**
     * Computes the Cartesian, model-coordinate point of a location within a terrain tile.
     * <p/>
     * This operation fails with a {@link gov.nasa.worldwind.exception.WWTimeoutException} if a timeout has been
     * specified and it is exceeded during the operation.
     *
     * @param tile      the terrain tile.
     * @param latitude  the location's latitude.
     * @param longitude the location's longitude.
     *
     * @return the Cartesian, model-coordinate point of a the specified location, or null if the specified location does
     *         not exist within this instance's sector or if the operation is interrupted.
     *
     * @throws IllegalArgumentException if the latitude or longitude are null.
     * @throws gov.nasa.worldwind.exception.WWTimeoutException
     *                                  if the current timeout is exceeded while retrieving terrain data.
     * @throws InterruptedException     if the operation is interrupted.
     * @see #setTimeout(Long)
     */
    protected Vec4 getSurfacePoint(RectTile tile, Angle latitude, Angle longitude) throws InterruptedException
    {
        if (latitude == null || longitude == null)
        {
            String msg = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!tile.sector.contains(latitude, longitude))
        {
            // not on this geometry
            return null;
        }

        if (tile.ri == null)
            this.makeVerts(tile);

        if (tile.ri == null)
            return null;

        double lat = latitude.getDegrees();
        double lon = longitude.getDegrees();

        double bottom = tile.sector.getMinLatitude().getDegrees();
        double top = tile.sector.getMaxLatitude().getDegrees();
        double left = tile.sector.getMinLongitude().getDegrees();
        double right = tile.sector.getMaxLongitude().getDegrees();

        double leftDecimal = (lon - left) / (right - left);
        double bottomDecimal = (lat - bottom) / (top - bottom);

        int row = (int) (bottomDecimal * (tile.density));
        int column = (int) (leftDecimal * (tile.density));

        double l = createPosition(column, leftDecimal, tile.ri.density);
        double h = createPosition(row, bottomDecimal, tile.ri.density);

        Vec4 result = interpolate(row, column, l, h, tile.ri);
        result = result.add3(tile.ri.referenceCenter);

        return result;
    }

    protected static double createPosition(int start, double decimal, int density)
    {
        double l = ((double) start) / (double) density;
        double r = ((double) (start + 1)) / (double) density;

        return (decimal - l) / (r - l);
    }

    protected static Vec4 interpolate(int row, int column, double xDec, double yDec, RenderInfo ri)
    {
        int numVerticesPerEdge = ri.density + 1;

        int bottomLeft = row * numVerticesPerEdge + column;

        bottomLeft *= 3;

        int numVertsTimesThree = numVerticesPerEdge * 3;

        int i = bottomLeft;
        Vec4 bL = new Vec4(ri.vertices[i++], ri.vertices[i++], ri.vertices[i++]);
        Vec4 bR = new Vec4(ri.vertices[i++], ri.vertices[i++], ri.vertices[i]);

        bottomLeft += numVertsTimesThree;

        i = bottomLeft;
        Vec4 tL = new Vec4(ri.vertices[i++], ri.vertices[i++], ri.vertices[i++]);
        Vec4 tR = new Vec4(ri.vertices[i++], ri.vertices[i++], ri.vertices[i]);

        return interpolate(bL, bR, tR, tL, xDec, yDec);
    }

    protected static Vec4 interpolate(Vec4 bL, Vec4 bR, Vec4 tR, Vec4 tL, double xDec, double yDec)
    {
        double pos = xDec + yDec;
        if (pos == 1)
        {
            // on the diagonal - what's more, we don't need to do any "oneMinusT" calculation
            return new Vec4(
                tL.x * yDec + bR.x * xDec,
                tL.y * yDec + bR.y * xDec,
                tL.z * yDec + bR.z * xDec);
        }
        else if (pos > 1)
        {
            // in the "top right" half

            // vectors pointing from top right towards the point we want (can be thought of as "negative" vectors)
            Vec4 horizontalVector = (tL.subtract3(tR)).multiply3(1 - xDec);
            Vec4 verticalVector = (bR.subtract3(tR)).multiply3(1 - yDec);

            return tR.add3(horizontalVector).add3(verticalVector);
        }
        else
        {
            // pos < 1 - in the "bottom left" half

            // vectors pointing from the bottom left towards the point we want
            Vec4 horizontalVector = (bR.subtract3(bL)).multiply3(xDec);
            Vec4 verticalVector = (tL.subtract3(bL)).multiply3(yDec);

            return bL.add3(horizontalVector).add3(verticalVector);
        }
    }

    /**
     * Computes the intersections of a line with a tile.
     *
     * @param tile the tile.
     * @param line the line.
     *
     * @return an array of intersections, or null if no intersections occur.
     *
     * @throws InterruptedException if the operation is interrupted.
     */
    protected Intersection[] intersect(RectTile tile, Line line) throws InterruptedException
    {
        if (tile.ri == null)
            this.makeVerts(tile);

        if (tile.ri == null)
            return null;

        Intersection[] hits;
        ArrayList<Intersection> list = new ArrayList<Intersection>();

        double cx = tile.ri.referenceCenter.x;
        double cy = tile.ri.referenceCenter.y;
        double cz = tile.ri.referenceCenter.z;

        // Loop through all the tile's triangles
        int n = tile.density + 1;
        float[] coords = tile.ri.vertices;

        for (int j = 0; j < n - 1; j++)
        {
            for (int i = 0; i < n - 1; i++)
            {
                int k = (j * n + i) * 3;
                Vec4 va = new Vec4(coords[k] + cx, coords[k + 1] + cy, coords[k + 2] + cz);

                k += 3;
                Vec4 vb = new Vec4(coords[k] + cx, coords[k + 1] + cy, coords[k + 2] + cz);

                k += n * 3;
                Vec4 vc = new Vec4(coords[k] + cx, coords[k + 1] + cy, coords[k + 2] + cz);

                k -= 3;
                Vec4 vd = new Vec4(coords[k] + cx, coords[k + 1] + cy, coords[k + 2] + cz);

                // Intersect triangles with line
                Intersection intersection;

                if ((intersection = Triangle.intersect(line, va, vb, vc)) != null)
                    list.add(intersection);

                if ((intersection = Triangle.intersect(line, va, vc, vd)) != null)
                    list.add(intersection);
            }
        }

        int numHits = list.size();
        if (numHits == 0)
            return null;

        // Sort the intersections by distance from line origin, nearer are first in the sorted list.
        hits = new Intersection[numHits];
        list.toArray(hits);

        final Vec4 origin = line.getOrigin();
        Arrays.sort(hits, new Comparator<Intersection>()
        {
            public int compare(Intersection i1, Intersection i2)
            {
                if (i1 == null && i2 == null)
                    return 0;
                if (i2 == null)
                    return -1;
                if (i1 == null)
                    return 1;

                Vec4 v1 = i1.getIntersectionPoint();
                Vec4 v2 = i2.getIntersectionPoint();
                double d1 = origin.distanceTo3(v1);
                double d2 = origin.distanceTo3(v2);
                return Double.compare(d1, d2);
            }
        });

        return hits;
    }

    /**
     * Computes the buffer of indices forming a tile.
     *
     * @param density the tile density.
     *
     * @return the buffer of indices.
     */
    protected int[] getIndices(int density)
    {
        if (density < 1)
            density = 1;

        int sideSize = density;

        int indexCount = 2 * sideSize * sideSize + 4 * sideSize - 2;
        int[] indexBuffer = new int[indexCount];
        int ib = 0;
        int k = 0;

        for (int i = 0; i < sideSize; i++)
        {
            indexBuffer[ib++] = k;
            if (i > 0)
            {
                indexBuffer[ib++] = ++k;
                indexBuffer[ib++] = k;
            }

            if (i % 2 == 0) // even
            {
                indexBuffer[ib++] = ++k;
                for (int j = 0; j < sideSize; j++)
                {
                    k += sideSize;
                    indexBuffer[ib++] = k;
                    indexBuffer[ib++] = ++k;
                }
            }
            else // odd
            {
                indexBuffer[ib++] = --k;
                for (int j = 0; j < sideSize; j++)
                {
                    k -= sideSize;
                    indexBuffer[ib++] = k;
                    indexBuffer[ib++] = --k;
                }
            }
        }

        return indexBuffer;
    }
}
