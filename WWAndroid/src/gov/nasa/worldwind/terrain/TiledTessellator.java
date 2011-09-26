/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.terrain;

import android.opengl.GLES20;
import android.util.Pair;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import java.beans.PropertyChangeEvent;
import java.nio.*;
import java.util.*;
import java.util.List;

/**
 * @author dcollins
 * @version $Id$
 */
public class TiledTessellator extends WWObjectImpl implements Tessellator, Tile.TileFactory
{
    protected static class TerrainTile extends Tile implements SectorGeometry
    {
        protected TiledTessellator tessellator;
        protected Extent extent;

        public TerrainTile(Sector sector, Level level, int row, int column, TiledTessellator tessellator)
        {
            super(sector, level, row, column);
            this.tessellator = tessellator;
        }

        public double getResolution()
        {
            return this.level.getTexelSize();
        }

        /** {@inheritDoc} */
        public Extent getExtent()
        {
            return this.extent;
        }

        public void setExtent(Extent extent)
        {
            this.extent = extent;
        }

        public TerrainGeometry getGeometry(MemoryCache cache)
        {
            return (TerrainGeometry) cache.get(this.tileKey);
        }

        public void setGeometry(MemoryCache cache, TerrainGeometry geom)
        {
            cache.put(this.tileKey, geom);
        }

        @Override
        public long getSizeInBytes()
        {
            // This tile's size in bytes is computed as follows:
            // superclass: variable
            // tessellator: 4 bytes (1 32-bit reference)
            // extent: 4 bytes (1 32-bit reference)
            // total: 8 bytes + superclass' size in bytes

            return 8 + super.getSizeInBytes();
        }

        /** {@inheritDoc} */
        public void render(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tessellator.render(dc, this);
        }

        /** {@inheritDoc} */
        public void renderWireframe(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tessellator.renderWireframe(dc, this);
        }

        /** {@inheritDoc} */
        public void renderOutline(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tessellator.renderOutline(dc, this);
        }

        /** {@inheritDoc} */
        public void beginRendering(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tessellator.beginRendering(dc, this);
        }

        /** {@inheritDoc} */
        public void endRendering(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tessellator.endRendering(dc, this);
        }
    }

    protected static class TerrainGeometry implements Cacheable
    {
        protected Vec4 referenceCenter = new Vec4();
        protected Matrix transformMatrix = Matrix.fromIdentity();
        protected FloatBuffer points;
        protected final Object vboCacheKey = new Object();
        protected boolean mustRegnerateVbos;
        protected TerrainSharedGeometry sharedGeom;

        public TerrainGeometry()
        {
        }

        public long getSizeInBytes()
        {
            // This tile's size in bytes is computed as follows:
            // self: 4 bytes (1 32-bit reference)
            // referenceCenter: 36 bytes (1 32-bit reference + 4 64-bit floats)
            // transformMatrix: 132 bytes (1 32-bit reference + 16 64-bit floats)
            // points: 4 bytes + variable (1 32-bit reference + variable num of 32-bit floats)
            // vboCacheKey: 4 bytes (1 32-bit reference)
            // sharedGeom: 4 bytes (1 32-bit reference)
            // total: 184 bytes

            long size = 184;
            size += this.points != null ? 4 * this.points.capacity() : 0;
            return size;
        }
    }

    protected static class TerrainSharedGeometry
    {
        protected FloatBuffer texCoords;
        protected ShortBuffer indices;
        protected ShortBuffer wireframeIndices;
        protected ShortBuffer outlineIndices;
        protected final Object vboCacheKey = new Object();

        public TerrainSharedGeometry()
        {
        }
    }

    protected static class TerrainTileList extends ArrayList<SectorGeometry> implements SectorGeometryList
    {
        protected Sector sector;
        protected TiledTessellator tessellator;

        public TerrainTileList(TiledTessellator tessellator)
        {
            this.tessellator = tessellator;
        }

        /** {@inheritDoc} */
        public Sector getSector()
        {
            return this.sector;
        }

        public void setSector(Sector sector)
        {
            this.sector = sector;
        }

        /** {@inheritDoc} */
        public void beginRendering(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tessellator.beginRendering(dc);
        }

        /** {@inheritDoc} */
        public void endRendering(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.error(msg);
                throw new IllegalArgumentException(msg);
            }

            this.tessellator.endRendering(dc);
        }
    }

    protected static final double DEFAULT_DETAIL_HINT_ORIGIN = 1.4;
    protected static Map<Object, TerrainSharedGeometry> sharedGeometry = new HashMap<Object, TerrainSharedGeometry>();

    protected double detailHintOrigin = DEFAULT_DETAIL_HINT_ORIGIN;
    protected double detailHint;
    protected LevelSet levels;
    protected List<Tile> topLevelTiles = new ArrayList<Tile>();
    protected TerrainTileList currentTiles = new TerrainTileList(this);
    protected Sector currentCoverage = new Sector();
    // Data structures used to track when the elevation model changes.
    protected List<Sector> expiredSectors = new ArrayList<Sector>();
    protected List<Sector> currentExpiredSectors = new ArrayList<Sector>();
    protected final Object expiredSectorLock = new Object();
    // Temporary properties used to avoid constant reallocation of data used during tile assembly and rendering.
    protected Matrix mvpMatrix = Matrix.fromIdentity();
    protected double[] tileElevations;
    protected double[] tileRowElevations;
    protected Vec4[] tilePoints;
    protected float[] tileCoords;

    public TiledTessellator(AVList params)
    {
        if (params == null)
        {
            String msg = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.initWithParams(params);
    }

    public TiledTessellator(Element element)
    {
        if (element == null)
        {
            String msg = Logging.getMessage("nullValue.ElementIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.initWithConfigDoc(element);
    }

    protected void initWithParams(AVList params)
    {
        Object o = params.getValue(AVKey.DETAIL_HINT);
        if (o != null && o instanceof Number)
            this.detailHint = ((Number) o).doubleValue();

        this.levels = new LevelSet(params);
    }

    protected void initWithConfigDoc(Element element)
    {
        XPath xpath = WWXML.makeXPath();

        Double d = WWXML.getDouble(element, "DetailHint", xpath);
        if (d != null)
            this.detailHint = d;

        this.levels = new LevelSet(LevelSet.paramsFromConfigDoc(element));
    }

    public double getDetailHint()
    {
        return this.detailHint;
    }

    public void setDetailHint(double detailHint)
    {
        this.detailHint = detailHint;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        super.propertyChange(event);

        // Listen to the Globe's elevation model for changes in elevation model sectors. We mark these sectors as
        // expired and regenerate geometry for tiles intersecting these sectors.
        //noinspection StringEquality
        if (event != null && event.getPropertyName() == AVKey.ELEVATION_MODEL && event.getNewValue() instanceof Tile)
        {
            Sector tileSector = ((Tile) event.getNewValue()).getSector();
            this.markSectorExpired(tileSector);
        }
    }

    public SectorGeometryList tessellate(DrawContext dc)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.assembleExpiredSectors();
        this.assembleTiles(dc);
        this.currentExpiredSectors.clear();

        return this.currentTiles;
    }

    public Tile createTile(Sector sector, Level level, int row, int column)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (level == null)
        {
            String msg = Logging.getMessage("nullValue.LevelIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (row < 0)
        {
            String msg = Logging.getMessage("generic.RowIndexOutOfRange", row);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (column < 0)
        {
            String msg = Logging.getMessage("generic.ColumnIndexOutOfRange", column);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new TerrainTile(sector, level, row, column, this);
    }

    protected void assembleTiles(DrawContext dc)
    {
        // Start with an empty list of tile and empty coverage. An empty coverage sector is handled as a special case
        // below and in addTile. We use the sector's empty state instead of setting the coverage to null in order to
        // avoid reallocating a sector every frame.
        this.currentTiles.clear();
        this.currentCoverage.setDegrees(0, 0, 0, 0);

        if (this.topLevelTiles.isEmpty())
            this.createTopLevelTiles();

        for (Tile tile : this.topLevelTiles)
        {
            this.updateTileExtent(dc, (TerrainTile) tile);

            if (this.intersectsFrustum(dc, (TerrainTile) tile))
                this.addTileOrDescendants(dc, (TerrainTile) tile);
        }

        this.currentTiles.setSector(this.currentCoverage.isEmpty() ? null : this.currentCoverage);
    }

    protected void createTopLevelTiles()
    {
        if (this.levels.getFirstLevel() == null)
        {
            Logging.warning(Logging.getMessage("generic.FirstLevelIsNull"));
            return;
        }

        this.topLevelTiles.clear();
        Tile.createTilesForLevel(this.levels.getFirstLevel(), this.levels.getSector(), this, this.topLevelTiles);
    }

    protected void addTileOrDescendants(DrawContext dc, TerrainTile tile)
    {
        this.updateTileExtent(dc, tile);

        if (this.meetsRenderCriteria(dc, tile))
        {
            this.addTile(dc, tile);
            return;
        }

        MemoryCache cache = this.getTerrainTileCache();

        Tile[] subTiles = tile.subdivide(this.levels.getLevel(tile.getLevelNumber() + 1), cache, this);
        for (Tile child : subTiles)
        {
            // Put all sub-tiles in the terrain tile cache to avoid repeatedly allocating them each frame. Top level
            // tiles are not cached because they are held in the topLevelTiles list. Sub tiles are placed in the cache
            // here, and updated when their terrain geometry changes.
            if (!cache.contains(child.getTileKey()))
                cache.put(child.getTileKey(), child);

            // Add descendant tiles that intersect the LevelSet's sector and intersect the viewing frustum. If half or
            // more of the tile (in either latitude or longitude) extends beyond the LevelSet's sector, then two or
            // three of its children will be entirely outside the LevelSet's sector.
            if (this.levels.getSector().intersects(child.getSector()) && this.intersectsFrustum(dc,
                (TerrainTile) child))
            {
                this.addTileOrDescendants(dc, (TerrainTile) child);
            }
        }
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected void addTile(DrawContext dc, TerrainTile tile)
    {
        if (this.mustRegenerateGeometry(dc, tile))
            this.regenerateGeometry(dc, tile);

        this.currentTiles.add(tile);

        // If the current coverage sector is empty, just set the coverage to the tile's sector. This ensures that we
        // don't create a coverage sector that always contains the lat/lon origin. Otherwise set the current coverage
        // to the union of itself with the tile's sector.
        if (this.currentCoverage.isEmpty())
            this.currentCoverage.set(tile.getSector());
        else
            this.currentCoverage.union(tile.getSector());
    }

    protected boolean intersectsFrustum(DrawContext dc, TerrainTile tile)
    {
        Extent extent = tile.getExtent();
        return extent == null || dc.getView().getFrustumInModelCoordinates().intersects(extent);
    }

    protected boolean meetsRenderCriteria(DrawContext dc, TerrainTile tile)
    {
        return this.levels.isFinalLevel(tile.getLevelNumber())
            || this.atBestResolution(dc, tile)
            || !this.needToSubdivide(dc, tile);
    }

    protected boolean atBestResolution(DrawContext dc, TerrainTile tile)
    {
        return tile.getResolution() <= dc.getGlobe().getBestResolution(tile.getSector());
    }

    protected boolean needToSubdivide(DrawContext dc, TerrainTile tile)
    {
        return tile.mustSubdivide(dc, this.getDetailFactor());
    }

    protected double getDetailFactor()
    {
        return this.detailHintOrigin + this.detailHint;
    }

    protected void updateTileExtent(DrawContext dc, TerrainTile tile)
    {
        boolean expired = this.isExpired(dc, tile);

        if (tile.getExtent() == null || expired)
        {
            tile.setExtent(this.computeTileExtent(dc, tile));
        }

        // Update the tile's reference points.
        Vec4[] points = tile.getReferencePoints();
        if (points == null || expired)
        {
            points = new Vec4[] {new Vec4(), new Vec4(), new Vec4(), new Vec4(), new Vec4()};
            tile.getSector().computeCornerPoints(dc.getGlobe(), dc.getVerticalExaggeration(), points);
            tile.getSector().computeCentroidPoint(dc.getGlobe(), dc.getVerticalExaggeration(), points[4]);
            tile.setReferencePoints(points);
        }
    }

    protected Extent computeTileExtent(DrawContext dc, TerrainTile tile)
    {
        return Sector.computeBoundingBox(dc.getGlobe(), dc.getVerticalExaggeration(), tile.getSector());
    }

    protected boolean mustRegenerateGeometry(DrawContext dc, TerrainTile tile)
    {
        MemoryCache cache = this.getTerrainGeometryCache();
        return tile.getGeometry(cache) == null || this.isExpired(dc, tile);
    }

    protected void assembleExpiredSectors()
    {
        synchronized (this.expiredSectorLock)
        {
            this.currentExpiredSectors.addAll(this.expiredSectors);
            this.expiredSectors.clear();
        }
    }

    protected void markSectorExpired(Sector sector)
    {
        synchronized (this.expiredSectorLock)
        {
            this.expiredSectors.add(sector);
        }
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected boolean isExpired(DrawContext dc, TerrainTile tile)
    {
        if (this.currentExpiredSectors.isEmpty())
            return false;

        Sector tileSector = tile.getSector();
        for (Sector sector : this.currentExpiredSectors)
        {
            if (tileSector.intersects(sector))
                return true;
        }

        return false;
    }

    protected void regenerateGeometry(DrawContext dc, TerrainTile tile)
    {
        MemoryCache cache = this.getTerrainGeometryCache();
        TerrainGeometry geom = tile.getGeometry(cache);
        if (geom == null)
            geom = new TerrainGeometry();

        this.buildTileVertices(dc, tile, geom);
        this.buildSharedGeometry(tile, geom);

        // Update the geometry's cached size.
        tile.setGeometry(cache, geom);
    }

    protected void buildTileVertices(DrawContext dc, TerrainTile tile, TerrainGeometry geom)
    {
        // The WWAndroid terrain tessellator attempts to improves upon the WWJ tessellator's vertex construction
        // performance by exploiting the fact that each terrain tile is a regular geographic grid. The following three
        // critical differences have improved the performance of buildTileVertices by approximately 10x (from ~8ms to
        // ~0.7ms on a Samsung Galaxy Tab 10.1 32GB):
        //
        // 1) Avoid explicitly computing and storing the tile's locations by adding methods to Globe that exploit the
        //    tile's grid:
        //    Globe.getElevations(Sector sector, int numLat, int numLon, double targetResolution, double[] buffer)
        //    Globe.computePointsFromPositions(Sector sector, int numLat, int numLon, double[] metersElevation, Vec4[] result).
        //
        // 2) Compute the world Cartesian points for each row in one call to Globe.computePointsFromPositions. Since
        //    each row has constant latitude, the implementation of this method can reduce the number of computations by
        //    computing latitude dependent values once per row.
        //
        // 3) Putting the world Cartesian points for each row into the TerrainGeometry's point buffer in bulk using a
        //    temporary float array. This is necessary on Android because the performance of FloatBuffer.put(float) is
        //    slow. Putting the points in bulk improves performance by approximately 2x (from ~1.4ms to ~0.7ms on a
        //    Samsung Galaxy Tab 10.1 32GB). Note that this change would likely yield no improvement for the desktop
        //    based World Wind Java.

        // Convert from the tile's cell width and height to a number of latitude and longitude vertices. The tile width
        // and height indicates the number of cell rows and columns in the tile. We add one row and column of vertices
        // because there is a row/column of vertices in between each cell.
        int numLat = tile.getLevel().getTileHeight() + 1;
        int numLon = tile.getLevel().getTileWidth() + 1;

        // Allocate arrays to hold the elevations for a tile and for each row. These arrays are properties of the
        // tessellator to avoid constantly reallocating them each time a tile is updated.
        if (this.tileElevations == null || this.tileElevations.length < numLat * numLon)
            this.tileElevations = new double[numLat * numLon];
        if (this.tileRowElevations == null || this.tileRowElevations.length < numLon)
            this.tileRowElevations = new double[numLon];

        // Get the elevation values for the tile from the Globe. Any elevations that are unknown or outside the Globe's
        // elevation model are assigned the value 0.0.
        Globe globe = dc.getGlobe();
        globe.getElevations(tile.getSector(), numLat, numLon, tile.getResolution(), this.tileElevations);

        // Adjust the tile's elevations and min elevation by the DrawContext's vertical exaggeration. We skip this step
        // if the vertical exaggeration is 1.0.
        //
        // Note: Previous versions of RectangularTessellator in the World Wind Java project contained a bug where
        // Globe.getMinElevation returned a value greater than zero if the Globe's elevation model did not span the
        // entire globe. To work around that bug, RectangularTessellator would apply the verticalExaggeration to the
        // skirt elevation only when the Globe's min elevation was less than zero, or the verticalExaggeration was less
        // than zero. That bug has been fixed in the WWAndroid project, and we can rely on Globe to return a min
        // elevation of 0 if the elevation model does not span the entire globe.
        double verticalExaggeration = dc.getVerticalExaggeration();
        double minElevation = globe.getMinElevation();
        if (verticalExaggeration != 1.0)
        {
            for (int i = 0; i < numLat * numLon; i++)
            {
                this.tileElevations[i] *= verticalExaggeration;
            }

            minElevation *= verticalExaggeration;
        }

        // Compute a local coordinate origin for the tile's world Cartesian points. We use this origin to keep each
        // world coordinate small in order to achieve the resolution we need on the Gpu.
        tile.getSector().computeCentroidPoint(globe, dc.getVerticalExaggeration(), geom.referenceCenter);
        geom.transformMatrix.setTranslation(geom.referenceCenter);

        // Re-use the tile's existing vertex buffer whenever possible. Create a new buffer if one has not been set or if
        // the tile density has changed. We clear the buffer if it is non-null and has enough capacity to ensure that
        // the previous limit does not interfere with what the new limit should be after filling the buffer. We add two
        // rows and columns of vertices to provide an outer row/column for the tile skirt.
        int numCoords = 3 * (numLat + 2) * (numLon + 2);
        if (geom.points == null || geom.points.capacity() < numCoords)
            geom.points = ByteBuffer.allocateDirect(4 * numCoords).order(ByteOrder.nativeOrder()).asFloatBuffer();
        geom.points.clear();

        double minLat = tile.getSector().minLatitude.degrees;
        double maxLat = tile.getSector().maxLatitude.degrees;
        double minLon = tile.getSector().minLongitude.degrees;
        double maxLon = tile.getSector().maxLongitude.degrees;
        // Vertex latitudes and longitudes are separated by the cell latitude and longitude delta.
        double deltaLat = tile.getSector().getDeltaLatDegrees() / tile.getLevel().getTileHeight();
        Sector rowSector = new Sector();

        // Add redundant points with the row's minimum latitude. These points are used to display the tile's skirt, and
        // have the same locations as the first row, but are assigned the minimum elevation instead of the actual
        // elevations. buildTileRowVertices handles adding the redundant columns for the tile's skirt.
        rowSector.setDegrees(minLat, minLat, minLon, maxLon);
        Arrays.fill(this.tileRowElevations, minElevation);
        this.buildTileRowVertices(dc, rowSector, numLon, this.tileRowElevations, minElevation, geom);

        double lat = minLat;
        int elevOffset = 0;
        for (int j = 0; j < numLat; j++)
        {
            // Explicitly set the first and last row to minLat and maxLat, respectively, rather than using the
            // accumulated lat value. We do this to ensure that the edges of adjacent tiles line up perfectly.
            if (j == 0)
                lat = minLat;
            else if (j == numLat - 1)
                lat = maxLat;
            else
                lat += deltaLat;

            // Process each tile row in bulk.
            rowSector.setDegrees(lat, lat, minLon, maxLon);
            System.arraycopy(this.tileElevations, elevOffset, this.tileRowElevations, 0, numLon);
            this.buildTileRowVertices(dc, rowSector, numLon, this.tileRowElevations, minElevation, geom);

            elevOffset += numLon;
        }

        // Add redundant points with the row's maximum latitude. These points are used to display the tile's skirt, and
        // have the same locations as the last row, but are assigned the minimum elevation instead of the actual
        // elevations. buildTileRowVertices handles adding the redundant columns for the tile's skirt.
        rowSector.setDegrees(maxLat, maxLat, minLon, maxLon);
        Arrays.fill(this.tileRowElevations, minElevation);
        this.buildTileRowVertices(dc, rowSector, numLon, this.tileRowElevations, minElevation, geom);

        // Set the limit to the current position then set the position to zero. We flip the buffer because its capacity
        // may be greater than the space needed, and the GL commands that ready this buffer rely on the limit to
        // determine how many buffer elements to read.
        geom.points.flip();
        geom.mustRegnerateVbos = true;
    }

    protected void buildTileRowVertices(DrawContext dc, Sector rowSector, int width, double[] elevations,
        double minElevation, TerrainGeometry geom)
    {
        // Allocate an array of points that hold the Cartesian coordinates for each XYZ point in this row. The array
        // is a property of this tessellator to avoid constantly reallocating it each time a tile is updated.
        if (this.tilePoints == null || this.tilePoints.length < width)
        {
            this.tilePoints = new Vec4[width];
            for (int i = 0; i < width; i++)
            {
                this.tilePoints[i] = new Vec4();
            }
        }

        // Allocate an array of floats to hold the combined coordinates of each point. We populate this array then add
        // it to the tile's point buffer in bulk. Adding an entire row of points into the FloatBuffer using a temporary
        // array is approximately 2x faster than adding each coordinate individually.
        int numCoords = 3 * (width + 2);
        if (this.tileCoords == null || this.tileCoords.length < numCoords)
            this.tileCoords = new float[numCoords];

        Globe globe = dc.getGlobe();
        int index = 0;

        // Add a redundant point with the row's minimum latitude. This point is used to display the tile's skirt, and
        // has the same location as the row's first location, but is assigned the minimum elevation instead of the
        // location's actual elevation. We subtract the tile's reference center from the Cartesian point to keep its
        // values as near to zero as possible. This enables us to achieve the resolution we need on the Gpu.
        globe.computePointFromPosition(rowSector.minLatitude, rowSector.minLongitude, minElevation, this.tilePoints[0]);
        this.tilePoints[0].subtract3AndSet(geom.referenceCenter);
        this.tilePoints[0].toArray3(this.tileCoords, index);
        index += 3;

        // Add points for each location in the row. We subtract the tile's reference center from the Cartesian point to
        // keep its values as near to zero as possible. This enables us to achieve the resolution we need on the Gpu.
        globe.computePointsFromPositions(rowSector, 1, width, elevations, this.tilePoints);
        for (int i = 0; i < width; i++)
        {
            this.tilePoints[i].subtract3AndSet(geom.referenceCenter);
            this.tilePoints[i].toArray3(this.tileCoords, index);
            index += 3;
        }

        // Add a redundant point with the row's maximum latitude. This point is used to display the tile's skirt, and
        // has the same location as the row's last location, but is assigned the minimum elevation instead of the
        // location's actual elevation. We subtract the tile's reference center from the Cartesian point to keep its
        // values as near to zero as possible. This enables us to achieve the resolution we need on the Gpu.
        globe.computePointFromPosition(rowSector.minLatitude, rowSector.maxLongitude, minElevation, this.tilePoints[0]);
        this.tilePoints[0].subtract3AndSet(geom.referenceCenter);
        this.tilePoints[0].toArray3(this.tileCoords, index);
        index += 3;

        // Put the row's points into the tile's point buffer in bulk. Adding an entire row of points into the
        // FloatBuffer using a temporary array is approximately 2x faster than adding each coordinate individually.
        geom.points.put(this.tileCoords, 0, numCoords);
    }

    protected void buildSharedGeometry(TerrainTile tile, TerrainGeometry geom)
    {
        int tileWidth = tile.getWidth();
        int tileHeight = tile.getHeight();
        Object key = Pair.create(tileWidth, tileHeight);

        TerrainSharedGeometry sharedGeom = sharedGeometry.get(key);
        if (sharedGeom == null)
        {
            sharedGeom = new TerrainSharedGeometry();
            sharedGeom.texCoords = this.buildTexCoords(tileWidth, tileHeight);
            sharedGeom.indices = this.buildIndices(tileWidth, tileHeight);
            sharedGeom.wireframeIndices = this.buildWireframeIndices(tileWidth, tileHeight);
            sharedGeom.outlineIndices = this.buildOutlineIndices(tileWidth, tileHeight);
            sharedGeometry.put(key, sharedGeom);
        }

        geom.sharedGeom = sharedGeom;
    }

    /**
     * Returns the memory cache used to cache terrain tiles, initializing the cache if it doesn't yet exist.
     *
     * @return the memory cache associated with terrain tiles.
     */
    protected MemoryCache getTerrainTileCache()
    {
        if (!WorldWind.getMemoryCacheSet().contains(TerrainTile.class.getName()))
        {
            long size = Configuration.getLongValue(AVKey.SECTOR_GEOMETRY_TILE_CACHE_SIZE);
            MemoryCache cache = new BasicMemoryCache((long) (0.8 * size), size);
            cache.setName("Tessellator Tiles");
            WorldWind.getMemoryCacheSet().put(TerrainTile.class.getName(), cache);
        }

        return WorldWind.getMemoryCacheSet().get(TerrainTile.class.getName());
    }

    /**
     * Returns the memory cache used to cache terrain geometry, initializing the cache if it doesn't yet exist.
     *
     * @return the memory cache associated with terrain geometry.
     */
    protected MemoryCache getTerrainGeometryCache()
    {
        if (!WorldWind.getMemoryCacheSet().contains(TerrainGeometry.class.getName()))
        {
            long size = Configuration.getLongValue(AVKey.SECTOR_GEOMETRY_CACHE_SIZE);
            MemoryCache cache = new BasicMemoryCache((long) (0.8 * size), size);
            cache.setName("Tessellator Geometry");
            WorldWind.getMemoryCacheSet().put(TerrainGeometry.class.getName(), cache);
        }

        return WorldWind.getMemoryCacheSet().get(TerrainGeometry.class.getName());
    }

    protected FloatBuffer buildTexCoords(int tileWidth, int tileHeight)
    {
        // The tile width and height indicates the number of cell rows and columns in the tile. We add one row and
        // column of vertices because there is a row/column of vertices in between each cell. We add two rows and
        // columns of vertices to provide an outer row/column for the tile skirt.
        int numLat = tileWidth + 3;
        int numLon = tileHeight + 3;
        int numCoords = 2 * numLat * numLon;
        FloatBuffer texCoords = ByteBuffer.allocateDirect(4 * numCoords).order(ByteOrder.nativeOrder()).asFloatBuffer();

        double minS = 0;
        double maxS = 1;
        double minT = 0;
        double maxT = 1;
        double deltaS = (maxS - minS) / tileWidth;
        double deltaT = (maxT - minT) / tileHeight;

        double s = minS; // Horizontal texture coordinate; varies along tile width or longitude.
        double t = minT; // Vertical texture coordinate; varies along tile height or latitude.

        for (int j = 0; j < numLat; j++)
        {
            if (j <= 1) // First two columns repeat the min T-coordinate to provide a column for the skirt.
                t = minT;
            else if (j >= numLat - 2) // Last two columns repeat the max T-coordinate to provide a column for the skirt.
                t = maxT;
            else
                t += deltaT; // Non-boundary latitudes are separated by the cell latitude delta.

            for (int i = 0; i < numLon; i++)
            {
                if (i <= 1) // First two rows repeat the min S-coordinate to provide a row for the skirt.
                    s = minS;
                else if (i >= numLon - 2) // Last two rows repeat the max S-coordinate to provide a row for the skirt.
                    s = maxS;
                else
                    s += deltaS; // Non-boundary longitudes are separated by the cell longitude delta.

                texCoords.put((float) s).put((float) t);
            }
        }

        texCoords.rewind();
        return texCoords;
    }

    protected ShortBuffer buildIndices(int tileWidth, int tileHeight)
    {
        // The tile width and height indicates the number of cell rows and columns in the tile. We add one row and
        // column of vertices because there is a row/column of vertices in between each cell. We add two rows and
        // columns of vertices to provide an outer row/column for the tile skirt.
        int numLat = tileHeight + 3;
        int numLon = tileWidth + 3;

        // Allocate a native short buffer to hold the indices used to draw a tile of the specified width and height as
        // a triangle strip. Shorts are the largest primitive that OpenGL ES allows for an index buffer. The largest
        // tileWidth and tileHeight that can be indexed by a short is 2565x255 (excluding the extra rows and columns to
        // convert between cell count and vertex count, and the extra rows and columns for the tile skirt).
        int numIndices = 2 * (numLat - 1) * numLon + 2 * (numLat - 2);
        ShortBuffer indices = ByteBuffer.allocateDirect(2 * numIndices).order(ByteOrder.nativeOrder()).asShortBuffer();

        for (int j = 0; j < numLat - 1; j++)
        {
            if (j != 0)
            {
                // Attach the previous and next triangle strips by repeating the last and first vertices of the previous
                // and current strips, respectively. This creates a degenerate triangle between the two strips which is
                // not rasterized because it has zero area. We don't perform this step when j==0 because there is no
                // previous triangle strip to connect with.
                indices.put((short) ((numLon - 1) + (j - 1) * numLon)); // Last vertex of previous strip.
                indices.put((short) (j * numLon + numLon)); // First vertex of current strip.j
            }

            for (int i = 0; i < numLon; i++)
            {
                // Create a triangle strip joining each adjacent row of vertices, starting in the lower left corner and
                // proceeding upward. The first vertex starts with the upper row of vertices and moves down to create a
                // counter-clockwise winding order.
                int vertex = i + j * numLon;
                indices.put((short) (vertex + numLon));
                indices.put((short) vertex);
            }
        }

        // Reset the buffer's position to zero since it has been advanced to the limit in the code above. We call rewind
        // instead of flip because the limit is already set the the capacity, and should never be any other value
        // because the buffer is allocated with the exact space needed.
        indices.rewind();
        return indices;
    }

    protected ShortBuffer buildWireframeIndices(int tileWidth, int tileHeight)
    {
        // The tile width and height indicates the number of cell rows and columns in the tile. We add one row and
        // column of vertices because there is a row/column of vertices in between each cell. This outline ignores the
        // tile skirt and draws only the vertices that appear on the surface.
        int numLat = tileHeight + 1;
        int numLon = tileWidth + 1;

        int numIndices = 2 * numLat * (numLon - 1) + 2 * (numLat - 1) * numLon;
        ShortBuffer indices = ByteBuffer.allocateDirect(2 * numIndices).order(ByteOrder.nativeOrder()).asShortBuffer();

        // Add two columns of vertices to the row stride to account for the two additional vertices that provide an
        // outer row/column for the tile skirt.
        int rowStride = numLon + 2;
        // Skip the skirt row and column to start at the first interior vertex.
        int offset = rowStride + 1;

        // Add a line between each column to define the horizontal cell outlines. Starts and ends at the vertices that
        // appear on the surface, thereby ignoring the tile skirt.
        for (int j = 0; j < numLat; j++)
        {
            for (int i = 0; i < numLon - 1; i++)
            {
                int vertex = offset + i + j * rowStride;
                indices.put((short) vertex).put((short) (vertex + 1));
            }
        }

        // Add a line between each row to define the vertical cell outlines. Starts and ends at the vertices that appear
        // on the surface, thereby ignoring the tile skirt.
        for (int i = 0; i < numLon; i++)
        {
            for (int j = 0; j < numLat - 1; j++)
            {
                int vertex = offset + i + j * rowStride;
                indices.put((short) vertex).put((short) (vertex + rowStride));
            }
        }

        // Reset the buffer's position to zero since it has been advanced to the limit in the code above. We call rewind
        // instead of flip because the limit is already set the the capacity, and should never be any other value
        // because the buffer is allocated with the exact space needed.
        indices.rewind();
        return indices;
    }

    protected ShortBuffer buildOutlineIndices(int tileWidth, int tileHeight)
    {
        // The tile width and height indicates the number of cell rows and columns in the tile. We add one row and
        // column of vertices because there is a row/column of vertices in between each cell. This outline ignores the
        // tile skirt and draws only the vertices that appear on the surface.
        int numLat = tileHeight + 1;
        int numLon = tileWidth + 1;

        // The outline indices ignore the extra rows and columns for the tile skirt.
        int numIndices = 2 * (numLat - 1) + 2 * numLon - 1;
        ShortBuffer indices = ByteBuffer.allocateDirect(2 * numIndices).order(ByteOrder.nativeOrder()).asShortBuffer();

        // Add two columns of vertices to the row stride to account for the two additional vertices that provide an
        // outer row/column for the tile skirt.
        int rowStride = numLon + 2;

        // Bottom row. Offset by rowStride + 1 to start at the lower left corner, ignoring the tile skirt.
        int offset = rowStride + 1;
        for (int i = 0; i < numLon; i++)
        {
            indices.put((short) (offset + i));
        }

        // Rightmost column. Offset by rowStride - 2 to start at the lower right corner, ignoring the tile skirt. Skips
        // the bottom vertex, which is already included in the bottom row.
        offset = 2 * rowStride - 2;
        for (int j = 1; j < numLat; j++)
        {
            indices.put((short) (offset + j * rowStride));
        }

        // Top row. Offset by tileHeight* rowStride + 1 to start at the top left corner, ignoring the tile skirt. Skips
        // the rightmost vertex, which is already included in the rightmost column.
        offset = numLat * rowStride + 1;
        for (int i = numLon - 2; i >= 0; i--)
        {
            indices.put((short) (offset + i));
        }

        // Leftmost column. Offset by rowStride + 1 to start at the lower left corner, ignoring the tile skirt. Skips
        // the topmost vertex, which is already included in the top row.
        offset = rowStride + 1;
        for (int j = numLat - 2; j >= 0; j--)
        {
            indices.put((short) (offset + j * rowStride));
        }

        // Reset the buffer's position to zero since it has been advanced to the limit in the code above. We call rewind
        // instead of flip because the limit is already set the the capacity, and should never be any other value
        // because the buffer is allocated with the exact space needed.
        indices.rewind();
        return indices;
    }

    protected void beginRendering(DrawContext dc)
    {
        GpuProgram program = dc.getCurrentProgram();
        if (program == null)
        {
            Logging.warning(Logging.getMessage("generic.NoCurrentProgram"));
            return;
        }

        int point = program.getAttribLocation("vertexPoint"); // TODO: handle -1 return.
        GLES20.glEnableVertexAttribArray(point);

        int texCoord = program.getAttribLocation("vertexTexCoord"); // TODO: handle -1 return.
        GLES20.glEnableVertexAttribArray(texCoord);
    }

    protected void endRendering(DrawContext dc)
    {
        // Restore the array and element array buffer bindings to 0.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GpuProgram program = dc.getCurrentProgram();
        if (program == null)
            return; // Message logged in beginRendering(DrawContext).

        int point = program.getAttribLocation("vertexPoint"); // TODO: handle -1 return.
        GLES20.glDisableVertexAttribArray(point);

        int texCoord = program.getAttribLocation("vertexTexCoord"); // TODO: handle -1 return.
        GLES20.glDisableVertexAttribArray(texCoord);
    }

    protected void beginRendering(DrawContext dc, TerrainTile tile)
    {
        GpuProgram program = dc.getCurrentProgram();
        if (program == null)
            return;// Message logged in beginRendering(DrawContext).

        MemoryCache memCache = this.getTerrainGeometryCache();
        TerrainGeometry geom = tile.getGeometry(memCache);
        if (geom == null)
        {
            Logging.warning(
                Logging.getMessage("Tessellator.SurfaceGeometryNotInCache", tile, memCache.getUsedCapacity()));
            return;
        }

        this.loadGeometryVbos(dc, geom);
        this.loadSharedGeometryVBOs(dc, geom.sharedGeom);

        GpuResourceCache gpuCache = dc.getGpuResourceCache();
        int[] vboIds = (int[]) gpuCache.get(geom.vboCacheKey);
        if (vboIds == null)
        {
            Logging.warning(
                Logging.getMessage("Tessellator.SurfaceGeometryVBONotInGpuCache", tile, gpuCache.getUsedCapacity()));
            return;
        }

        int[] sharedVboIds = (int[]) gpuCache.get(geom.sharedGeom.vboCacheKey);
        if (sharedVboIds == null)
        {
            Logging.warning(
                Logging.getMessage("Tessellator.SharedGeometryVBONotInGpuCache", tile, gpuCache.getUsedCapacity()));
            return;
        }

        int point = program.getAttribLocation("vertexPoint"); // TODO: handle -1 return.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[0]);
        GLES20.glVertexAttribPointer(point, 3, GLES20.GL_FLOAT, false, 0, 0);

        int texCoord = program.getAttribLocation("vertexTexCoord"); // TODO: handle -1 return.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sharedVboIds[0]);
        GLES20.glVertexAttribPointer(texCoord, 2, GLES20.GL_FLOAT, false, 0, 0);

        // Multiply the View's modelview-projection matrix by the tile's transform matrix to correctly transform tile
        // points into eye coordinates. This achieves the resolution we need on Gpus with limited floating point
        // precision keeping both the modelview-projection matrix and the point coordinates the Gpu uses as small as
        // possible when the eye point is near the tile.
        this.mvpMatrix.multiplyAndSet(dc.getView().getModelviewProjectionMatrix(), geom.transformMatrix);
        program.loadUniformMatrix("mvpMatrix", this.mvpMatrix);
    }

    @SuppressWarnings({"UnusedParameters"})
    protected void endRendering(DrawContext dc, TerrainTile tile)
    {
        // Intentionally left blank. All GL state is restored in endRendering(DrawContext).
    }

    protected void render(DrawContext dc, TerrainTile tile)
    {
        GpuProgram program = dc.getCurrentProgram();
        if (program == null)
            return; // Message logged in beginRendering(DrawContext).

        TerrainGeometry geom = tile.getGeometry(this.getTerrainGeometryCache());
        if (geom == null)
            return; // Message logged in beginRendering(DrawContext, TerrainTile).

        GpuResourceCache gpuCache = dc.getGpuResourceCache();
        int[] sharedVboIds = (int[]) gpuCache.get(geom.sharedGeom.vboCacheKey);
        if (sharedVboIds == null)
        {
            Logging.warning(
                Logging.getMessage("Tessellator.SharedGeometryVBONotInGpuCache", tile, gpuCache.getUsedCapacity()));
            return;
        }

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sharedVboIds[1]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, geom.sharedGeom.indices.remaining(), GLES20.GL_UNSIGNED_SHORT,
            0);
    }

    protected void renderWireframe(DrawContext dc, TerrainTile tile)
    {
        GpuProgram program = dc.getCurrentProgram();
        if (program == null)
            return; // Message logged in beginRendering(DrawContext).

        TerrainGeometry geom = tile.getGeometry(this.getTerrainGeometryCache());
        if (geom == null)
            return; // Message logged in beginRendering(DrawContext, TerrainTile).

        GpuResourceCache gpuCache = dc.getGpuResourceCache();
        int[] sharedVboIds = (int[]) gpuCache.get(geom.sharedGeom.vboCacheKey);
        if (sharedVboIds == null)
        {
            Logging.warning(
                Logging.getMessage("Tessellator.SharedGeometryVBONotInGpuCache", tile, gpuCache.getUsedCapacity()));
            return;
        }

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sharedVboIds[2]);
        GLES20.glDrawElements(GLES20.GL_LINES, geom.sharedGeom.wireframeIndices.remaining(), GLES20.GL_UNSIGNED_SHORT,
            0);
    }

    protected void renderOutline(DrawContext dc, TerrainTile tile)
    {
        GpuProgram program = dc.getCurrentProgram();
        if (program == null)
            return; // Message logged in beginRendering(DrawContext).

        TerrainGeometry geom = tile.getGeometry(this.getTerrainGeometryCache());
        if (geom == null)
            return; // Message logged in beginRendering(DrawContext, TerrainTile).

        GpuResourceCache gpuCache = dc.getGpuResourceCache();
        int[] sharedVboIds = (int[]) gpuCache.get(geom.sharedGeom.vboCacheKey);
        if (sharedVboIds == null)
        {
            Logging.warning(
                Logging.getMessage("Tessellator.SharedGeometryVBONotInGpuCache", tile, gpuCache.getUsedCapacity()));
            return;
        }

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sharedVboIds[3]);
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP, geom.sharedGeom.outlineIndices.remaining(),
            GLES20.GL_UNSIGNED_SHORT, 0);
    }

    protected void loadGeometryVbos(DrawContext dc, TerrainGeometry geom)
    {
        // Load the terrain geometry into the GpuResourceCache.
        GpuResourceCache cache = dc.getGpuResourceCache();
        int[] vboIds = (int[]) cache.get(geom.vboCacheKey);
        if (vboIds != null && !geom.mustRegnerateVbos)
            return;

        if (vboIds == null)
        {
            vboIds = new int[1];
            GLES20.glGenBuffers(1, vboIds, 0);
        }

        try
        {
            int sizeInBytes = 4 * geom.points.remaining();
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, sizeInBytes, geom.points, GLES20.GL_STREAM_DRAW);

            // Don't overwrite these VBOs if they're already in the cache. Doing so would cause the cache to delete
            // the existing VBO objects. Since we're reusing the same VBO ids, this would delete the VBO ids we're
            // using.
            if (!cache.contains(geom.vboCacheKey))
                cache.put(geom.vboCacheKey, vboIds, GpuResourceCache.VBO_BUFFERS, sizeInBytes);

            geom.mustRegnerateVbos = false;
        }
        finally
        {
            // Restore the array buffer binding to 0.
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }
    }

    protected void loadSharedGeometryVBOs(DrawContext dc, TerrainSharedGeometry geom)
    {
        GpuResourceCache cache = dc.getGpuResourceCache();
        int[] vboIds = (int[]) cache.get(geom.vboCacheKey);
        if (vboIds != null)
            return;

        vboIds = new int[4];
        GLES20.glGenBuffers(4, vboIds, 0);

        try
        {
            long totalSizeInBytes = 0;
            int sizeInBytes = 4 * geom.texCoords.remaining();
            totalSizeInBytes += sizeInBytes;
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[0]);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, sizeInBytes, geom.texCoords, GLES20.GL_STREAM_DRAW);

            sizeInBytes = 2 * geom.indices.remaining();
            totalSizeInBytes += sizeInBytes;
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, sizeInBytes, geom.indices, GLES20.GL_STREAM_DRAW);

            sizeInBytes = 2 * geom.wireframeIndices.remaining();
            totalSizeInBytes += sizeInBytes;
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboIds[2]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, sizeInBytes, geom.wireframeIndices,
                GLES20.GL_STREAM_DRAW);

            sizeInBytes = 2 * geom.outlineIndices.remaining();
            totalSizeInBytes += sizeInBytes;
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboIds[3]);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, sizeInBytes, geom.outlineIndices,
                GLES20.GL_STREAM_DRAW);

            cache.put(geom.vboCacheKey, vboIds, GpuResourceCache.VBO_BUFFERS, totalSizeInBytes);
        }
        finally
        {
            // Restore the array and element array buffer bindings to 0.
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }
}
