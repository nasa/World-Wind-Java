/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import java.util.*;

/**
 * @author dcollins
 * @version $Id$
 */
public abstract class TiledImageLayer extends AbstractLayer implements Tile.TileFactory
{
    protected abstract void requestTile(DrawContext dc, GpuTextureTile tile);

    protected static final double DEFAULT_DETAIL_HINT_ORIGIN = 2.8;

    protected LevelSet levels;
    protected double detailHintOrigin = DEFAULT_DETAIL_HINT_ORIGIN;
    protected double detailHint;
    protected List<Tile> topLevelTiles = new ArrayList<Tile>();
    protected List<GpuTextureTile> currentTiles = new ArrayList<GpuTextureTile>();
    protected GpuTextureFactory textureFactory;
    protected GpuTextureTile currentAncestorTile;

    public TiledImageLayer(AVList params)
    {
        super(params);
    }

    public TiledImageLayer(Element element)
    {
        super(element);
    }

    @Override
    protected void initWithParams(AVList params)
    {
        super.initWithParams(params);

        Object o = params.getValue(AVKey.DETAIL_HINT);
        if (o != null && o instanceof Number)
            this.detailHint = ((Number) o).doubleValue();

        this.levels = new LevelSet(params);
    }

    @Override
    protected void initWithConfigDoc(Element element)
    {
        super.initWithConfigDoc(element);

        XPath xpath = WWXML.makeXPath();

        Double d = WWXML.getDouble(element, "DetailHint", xpath);
        if (d != null)
            this.detailHint = d;

        this.levels = new LevelSet(LevelSet.paramsFromConfigDoc(element));
    }

    @Override
    protected void init()
    {
        super.init();

        this.textureFactory = this.createTextureFactory();
    }

    protected GpuTextureFactory createTextureFactory()
    {
        return (GpuTextureFactory) WorldWind.createConfigurationComponent(AVKey.GPU_TEXTURE_FACTORY);
    }

    /**
     * Indicates the layer's detail hint, which is described in {@link #setDetailHint(double)}.
     *
     * @return the detail hint
     *
     * @see #setDetailHint(double)
     */
    public double getDetailHint()
    {
        return this.detailHint;
    }

    /**
     * Modifies the default relationship of image resolution to screen resolution as the viewing altitude changes.
     * Values greater than 0 cause imagery to appear at higher resolution at greater altitudes than normal, but at an
     * increased performance cost. Values less than 0 decrease the default resolution at any given altitude. The default
     * value is 0. Values typically range between -0.5 and 0.5.
     * <p/>
     * Note: The resolution-to-height relationship is defined by a scale factor that specifies the approximate size of
     * discernible lengths in the image relative to eye distance. The scale is specified as a power of 10. A value of 3,
     * for example, specifies that 1 meter on the surface should be distinguishable from an altitude of 10^3 meters
     * (1000 meters). The default scale is 1/10^2.8, (1 over 10 raised to the power 2.8). The detail hint specifies
     * deviations from that default. A detail hint of 0.2 specifies a scale of 1/1000, i.e., 1/10^(2.8 + .2) = 1/10^3.
     * Scales much larger than 3 typically cause the applied resolution to be higher than discernible for the altitude.
     * Such scales significantly decrease performance.
     *
     * @param detailHint the degree to modify the default relationship of image resolution to screen resolution with
     *                   changing view altitudes. Values greater than 1 increase the resolution. Values less than zero
     *                   decrease the resolution. The default value is 0.
     */
    public void setDetailHint(double detailHint)
    {
        this.detailHint = detailHint;
    }

    protected LevelSet getLevels()
    {
        return levels;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overridden to return <code>false</code> when this layer's LevelSet is entirely outside of the current visible
     * sector. This provides an effective way to cull the entire layer before it performs any unnecessary work.
     */
    @Override
    public boolean isLayerInView(DrawContext dc)
    {
        return dc.getVisibleSector() == null || dc.getVisibleSector().intersects(this.levels.getSector());
    }

    @Override
    protected void doRender(DrawContext dc)
    {
        this.assembleTiles(dc);

        if (!this.currentTiles.isEmpty())
        {
            dc.getSurfaceTileRenderer().renderTiles(dc, this.currentTiles);
            dc.addPerFrameStatistic(PerformanceStatistic.IMAGE_TILE_COUNT, "Tile Count: " + this.getName(),
                this.currentTiles.size());
            this.currentTiles.clear();
        }

        // TODO: clear fallback tiles
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

        return new GpuTextureTile(sector, level, row, column, this.getTextureTileCache(), this.textureFactory);
    }

    protected void assembleTiles(DrawContext dc)
    {
        this.currentTiles.clear();

        if (this.topLevelTiles.isEmpty())
            this.createTopLevelTiles();

        for (Tile tile : this.topLevelTiles)
        {
            this.updateTileExtent(dc, (GpuTextureTile) tile);
            this.currentAncestorTile = null;

            if (this.isTileVisible(dc, (GpuTextureTile) tile))
                this.addTileOrDescendants(dc, (GpuTextureTile) tile);
        }
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

    protected void addTileOrDescendants(DrawContext dc, GpuTextureTile tile)
    {
        this.updateTileExtent(dc, tile);

        if (this.meetsRenderCriteria(dc, tile))
        {
            this.addTile(dc, tile);
            return;
        }

        // The incoming tile does not meet the rendering criteria, so it must be subdivided and those subdivisions
        // tested against the criteria.

        // All tiles that meet the selection criteria are drawn, but some of those tiles will not have textures
        // associated with them because their texture isn't loaded yet. In this case the tiles use the texture of the
        // closest ancestor that has a texture loaded. The ancestor is called the currentAncestorTile. A texture
        // transform is applied during rendering to align the sector's texture coordinates with the appropriate region
        // of the ancestor's texture.

        MemoryCache cache = this.getTextureTileCache();
        GpuTextureTile ancestorTile = null;

        try
        {
            if (tile.isTextureInMemory(dc.getGpuResourceCache()) || tile.getLevelNumber() == 0)
            {
                ancestorTile = this.currentAncestorTile;
                this.currentAncestorTile = tile;
            }

            Tile[] subTiles = tile.subdivide(this.levels.getLevel(tile.getLevelNumber() + 1), cache, this);
            for (Tile child : subTiles)
            {
                // Put all sub-tiles in the terrain tile cache to avoid repeatedly allocating them each frame. Top level
                // tiles are not cached because they are held in the topLevelTiles list. Sub tiles are placed in the
                // cache here, and updated when their terrain geometry changes.
                if (!cache.contains(child.getTileKey()))
                    cache.put(child.getTileKey(), child);

                // Add descendant tiles that intersect the LevelSet's sector and are visible. If half or more of this
                // tile (in either latitude or longitude) extends beyond the LevelSet's sector, then two or three of its
                // children will be entirely outside the LevelSet's sector.
                if (this.levels.getSector().intersects(child.getSector())
                    && this.isTileVisible(dc, (GpuTextureTile) child))
                {
                    this.addTileOrDescendants(dc, (GpuTextureTile) child);
                }
            }
        }
        finally
        {
            if (ancestorTile != null)
                this.currentAncestorTile = ancestorTile;
        }
    }

    protected void addTile(DrawContext dc, GpuTextureTile tile)
    {
        tile.setFallbackTile(null);

        // If this tile's level is empty, just ignore it. When the view moves closer to the tile it is subdivided and
        // an non-empty child level is eventually added.
        if (tile.getLevel().isEmpty())
            return;

        // If the tile's texture is in memory, add it to the list of current tiles and return.
        if (tile.isTextureInMemory(dc.getGpuResourceCache()))
        {
            this.currentTiles.add(tile);
            return;
        }

        // The tile's texture is not in memory. Issue a request for the texture data if the tile is not already marked
        // as an absent resource. We ignore absent resources to avoid flooding the system with requests for resources
        // that are never resolved.
        if (!this.levels.isTileAbsent(tile.getTileKey()))
            this.requestTile(dc, tile);

        if (this.currentAncestorTile != null)
        {
            // If the current ancestor tile's texture is in memory, then use it as this tile's fallback tile and add
            // this tile to the list of current tiles. Otherwise, we check if the ancestor tile is a level zero tile and
            // if so issue a request to load it into memory. This is critical to correctly handling the case when an
            // application is resumed with the view close to the globe. In that case, the level zero tiles are never
            // initially loaded and the tile that meets the render criteria may have no data. By issuing a request for
            // level zero ancestor tiles, we ensure that something displays when the application resumes.

            if (this.currentAncestorTile.isTextureInMemory(dc.getGpuResourceCache()))
            {
                tile.setFallbackTile(this.currentAncestorTile);
                this.currentTiles.add(tile);
            }
            else if (this.currentAncestorTile.getLevelNumber() == 0)
            {
                if (!this.levels.isTileAbsent(this.currentAncestorTile.getTileKey()))
                    this.requestTile(dc, this.currentAncestorTile);
            }
        }
    }

    protected boolean isTileVisible(DrawContext dc, GpuTextureTile tile)
    {
        // TODO: compute extent every frame or periodically update
        if (tile.getExtent() == null)
            tile.setExtent(this.computeTileExtent(dc, tile));

        Sector visibleSector = dc.getVisibleSector();
        Extent extent = tile.getExtent();

        return (visibleSector == null || visibleSector.intersects(tile.getSector()))
            && (extent == null || dc.getView().getFrustumInModelCoordinates().intersects(extent));
    }

    protected boolean meetsRenderCriteria(DrawContext dc, GpuTextureTile tile)
    {
        return this.levels.isFinalLevel(tile.getLevelNumber()) || !this.needToSubdivide(dc, tile);
    }

    protected boolean needToSubdivide(DrawContext dc, GpuTextureTile tile)
    {
        return tile.mustSubdivide(dc, this.getDetailFactor());
    }

    protected double getDetailFactor()
    {
        return this.detailHintOrigin + this.detailHint;
    }

    protected void updateTileExtent(DrawContext dc, GpuTextureTile tile)
    {
        // TODO: regenerate the tile extent and reference points whenever the underlying elevation model changes.
        // TODO: regenerate the tile extent and reference points whenever the vertical exaggeration changes.

        if (tile.getExtent() == null)
        {
            tile.setExtent(this.computeTileExtent(dc, tile));
        }

        // Update the tile's reference points.
        Vec4[] points = tile.getReferencePoints();
        if (points == null)
        {
            points = new Vec4[] {new Vec4(), new Vec4(), new Vec4(), new Vec4(), new Vec4()};
            tile.getSector().computeCornerPoints(dc.getGlobe(), dc.getVerticalExaggeration(), points);
            tile.getSector().computeCentroidPoint(dc.getGlobe(), dc.getVerticalExaggeration(), points[4]);
            tile.setReferencePoints(points);
        }
    }

    protected Extent computeTileExtent(DrawContext dc, GpuTextureTile tile)
    {
        return Sector.computeBoundingBox(dc.getGlobe(), dc.getVerticalExaggeration(), tile.getSector());
    }

    /**
     * Returns the memory cache used to cache texture tiles, initializing the cache if it doesn't yet exist.
     *
     * @return the memory cache associated with texture tiles.
     */
    protected MemoryCache getTextureTileCache()
    {
        if (!WorldWind.getMemoryCacheSet().contains(GpuTextureTile.class.getName()))
        {
            long size = Configuration.getLongValue(AVKey.GPU_TEXTURE_TILE_CACHE_SIZE);
            MemoryCache cache = new BasicMemoryCache((long) (0.8 * size), size);
            cache.setName("Texture Tiles");
            WorldWind.getMemoryCacheSet().put(GpuTextureTile.class.getName(), cache);
        }

        return WorldWind.getMemoryCacheSet().get(GpuTextureTile.class.getName());
    }
}
