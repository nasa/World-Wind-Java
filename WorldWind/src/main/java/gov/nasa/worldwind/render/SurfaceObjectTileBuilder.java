/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

import com.sun.opengl.util.texture.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.Cacheable;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Builds a list of {@link gov.nasa.worldwind.render.SurfaceTile} instances who's content is defined by a specified set
 * of {@link gov.nasa.worldwind.render.SurfaceObject} references. It's typically not necessary to use
 * SurfaceObjectTileBuilder directly. World Wind's default {@link gov.nasa.worldwind.SceneController} automatically
 * batches instances of {@link gov.nasa.worldwind.render.SurfaceObject} in a single SurfaceObjectTileBuilder.
 * Applications that need to draw basic surface shapes should use or extend {@link
 * gov.nasa.worldwind.render.SurfaceShape} instead of using SurfaceObjectTileBuilder directly.
 * <p/>
 * Surface tiles are built by calling {@link #buildTiles(DrawContext, Iterable)} (DrawContext, Iterable)} with an
 * Iterable of SurfaceObjects. This assembles a set of surface tiles that meet the resolution requirements for the
 * specified draw context, then draws the SurfaceObjects into those offscreen surface tiles by calling {@link
 * gov.nasa.worldwind.render.SurfaceObject#render(DrawContext)}. This process may temporarily use the framebuffer to
 * perform offscreen rendering, and therefore should be called during the preRender method of a World Wind {@link
 * gov.nasa.worldwind.layers.Layer}. See {@link gov.nasa.worldwind.render.PreRenderable} for details. Once built, the
 * surface tiles can be rendered by a {@link gov.nasa.worldwind.render.SurfaceTileRenderer}.
 * <p/>
 * By default, SurfaceObjectTileBuilder creates texture tiles with a width and height of 512 pixels, and with internal
 * format <code>GL.GL_RGBA</code>. These parameters are configurable by calling {@link
 * #setTileDimension(java.awt.Dimension)} or {@link #setTileTextureFormat(int)}.
 * <p/>
 * The most common usage pattern for SurfaceObjectTileBuilder is to build the surface tiles from a set of SurfaceObjects
 * during the preRender phase, then draw those surface tiles during the render phase. For example, a {@link
 * gov.nasa.worldwind.render.Renderable} can use SurfaceObjectTileBuilder to draw a set of SurfaceObjects as follows:
 * <p/>
 * <code>
 * <pre>
 * class MyRenderable implements Renderable, PreRenderable
 * {
 *     protected SurfaceObjectTileBuilder tileBuilder = new SurfaceObjectTileBuilder();
 *     protected ArrayList<SurfaceTile> tiles = new ArrayList<SurfaceTile>();
 *
 *     public void preRender(DrawContext dc)
 *     {
 *         List<?> surfaceObjects = Arrays.asList(
 *             new SurfaceCircle(LatLon.fromDegrees(0, 100), 10000),
 *             new SurfaceSquare(LatLon.fromDegrees(0, 101), 10000));
 *         this.tiles.addAll(this.tileBuilder.buildSurfaceTiles(dc, surfaceObjects));
 *     }
 *
 *     public void render(DrawContext dc)
 *     {
 *         dc.getGeographicSurfaceTileRenderer().renderTiles(dc, this.tiles);
 *     }
 * }
 * </pre>
 * </code>
 *
 * @author dcollins
 * @version $Id$
 */
public class SurfaceObjectTileBuilder
{
    /** The default surface tile texture dimension, in pixels. */
    protected static final int DEFAULT_TEXTURE_DIMENSION = 512;
    /** The default OpenGL internal format used to create surface tile textures. */
    protected static final int DEFAULT_TEXTURE_INTERNAL_FORMAT = GL.GL_RGBA8;
    /** The default OpenGL pixel format used to create surface tile textures. */
    protected static final int DEFAULT_TEXTURE_PIXEL_FORMAT = GL.GL_RGBA;
    /**
     * The default split scale. The split scale 2.9 has been empirically determined to render sharp lines and edges with
     * the SurfaceShapes such as SurfacePolyline and SurfacePolygon.
     */
    protected static final double DEFAULT_SPLIT_SCALE = 2.9;
    /** The default level zero tile delta used to construct a LevelSet. */
    protected static final LatLon DEFAULT_LEVEL_ZERO_TILE_DELTA = LatLon.fromDegrees(36, 36);
    /**
     * The default number of levels used to construct a LevelSet. Approximately 0.1 meters per pixel at the Earth's
     * equator.
     */
    protected static final int DEFAULT_NUM_LEVELS = 17;
    /** The next unique ID. This property is shared by all instances of SurfaceObjectTileBuilder. */
    protected static long nextUniqueId = 1;
    /**
     * Map associating a tile texture dimension to its corresponding LevelSet. This map is a class property in order to
     * share LevelSets across all instances of SurfaceObjectTileBuilder.
     */
    protected static Map<Dimension, LevelSet> levelSetMap = new HashMap<Dimension, LevelSet>();
    /**
     * Map associating a tile texture dimension to a tile cache name. This map is an instance property so that the cache
     * names for each instance and each tile dimension are unique.
     */
    protected Map<Dimension, String> tileCacheNameMap = new HashMap<Dimension, String>();

    /**
     * Indicates the desired tile texture width and height, in pixels. Initially set to
     * <code>DEFAULT_TEXTURE_DIMENSION</code>.
     */
    protected Dimension tileDimension = new Dimension(DEFAULT_TEXTURE_DIMENSION, DEFAULT_TEXTURE_DIMENSION);
    /**
     * Indicates the currently used tile texture width and height, in pixels. This is different than the caller
     * specified <code>tileDimension</code> if this value does not fit in the viewport, or is not a power of two.
     * Initially <code>null</code>.
     */
    protected Dimension currentTileDimension;
    /** The surface tile OpenGL texture format. 0 indicates the default format is used. */
    protected int tileTextureFormat;
    /** Controls if surface tiles are rendered using a linear filter or a nearest-neighbor filter. */
    protected boolean useLinearFilter = true;
    /** Controls if mip-maps are generated for surface tile textures. */
    protected boolean useMipmaps;
    /** Controls the tile resolution as distance changes between the globe's surface and the eye point. */
    protected double splitScale = DEFAULT_SPLIT_SCALE;
    /** List of currently assembled surface objects. Used during tile assembly and updating. */
    protected List<SurfaceObject> currentSurfaceObjects = new ArrayList<SurfaceObject>();
    /** List of currently assembled surface tiles. */
    protected List<SurfaceObjectTile> currentTiles = new ArrayList<SurfaceObjectTile>();
    /** Support class used to render to an offscreen surface tile. */
    protected OGLRenderToTextureSupport rttSupport = new OGLRenderToTextureSupport();

    /**
     * Constructs a new SurfaceObjectTileBuilder with a tile width and height of <code>512</code>, with the default tile
     * texture format, with linear filtering enabled, and with mip-mapping disabled.
     */
    public SurfaceObjectTileBuilder()
    {
    }

    /**
     * Constructs a new SurfaceObjectTileBuilder width the specified tile dimension, tile texture format, and flags
     * specifying if linear filtering and mip-mapping are enabled.
     *
     * @param tileTextureDimension the surface tile texture dimension, in pixels.
     * @param tileTextureFormat    the surface tile OpenGL texture format, or 0 to use the default format.
     * @param useLinearFilter      true to use linear filtering while rendering surface tiles; false to use
     *                             nearest-neighbor filtering.
     * @param useMipmaps           true to generate mip-maps for surface tile textures; false otherwise.
     *
     * @throws IllegalArgumentException if the tile dimension is null.
     */
    public SurfaceObjectTileBuilder(Dimension tileTextureDimension, int tileTextureFormat, boolean useLinearFilter,
        boolean useMipmaps)
    {
        if (tileTextureDimension == null)
        {
            String message = Logging.getMessage("nullValue.DimensionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setTileDimension(tileTextureDimension);
        this.setTileTextureFormat(tileTextureFormat);
        this.setUseLinearFilter(useLinearFilter);
        this.setUseMipmaps(useMipmaps);
    }

    /**
     * Returns the surface tile dimension.
     *
     * @return the surface tile dimension, in pixels.
     */
    public Dimension getTileDimension()
    {
        return this.tileDimension;
    }

    /**
     * Specifies the preferred surface tile texture dimension. If the dimension is larger than the viewport dimension,
     * this uses a dimension with width and height set to the largest power of two that is less than or equal to the
     * specified dimension and the viewport dimension.
     *
     * @param dimension the surface tile dimension, in pixels.
     *
     * @throws IllegalArgumentException if the dimension is null.
     */
    public void setTileDimension(Dimension dimension)
    {
        if (dimension == null)
        {
            String message = Logging.getMessage("nullValue.DimensionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.tileDimension = dimension;
    }

    /**
     * Returns the surface tile's OpenGL texture format, or 0 to indicate that the default format is used.
     *
     * @return the OpenGL texture format, or 0 if the default format is used.
     *
     * @see #setTileTextureFormat(int)
     */
    public int getTileTextureFormat()
    {
        return tileTextureFormat;
    }

    /**
     * Specifies the surface tile's OpenGL texture format. A value of 0 indicates that the default format should be
     * used. Otherwise, the texture format may be one of the following: <code> <ul> <li>GL.GL_ALPHA</li>
     * <li>GL.GL_ALPHA4</li> <li>GL.GL_ALPHA8</li> <li>GL.GL_ALPHA12</li> <li>GL.GL_ALPHA16</li>
     * <li>GL.GL_COMPRESSED_ALPHA</li> <li>GL.GL_COMPRESSED_LUMINANCE</li> <li>GL.GL_COMPRESSED_LUMINANCE_ALPHA</li>
     * <li>GL.GL_COMPRESSED_INTENSITY</li> <li>GL.GL_COMPRESSED_RGB</li> <li>GL.GL_COMPRESSED_RGBA</li>
     * <li>GL.GL_DEPTH_COMPONENT</li> <li>GL.GL_DEPTH_COMPONENT16</li> <li>GL.GL_DEPTH_COMPONENT24</li>
     * <li>GL.GL_DEPTH_COMPONENT32</li> <li>GL.GL_LUMINANCE</li> <li>GL.GL_LUMINANCE4</li> <li>GL.GL_LUMINANCE8</li>
     * <li>GL.GL_LUMINANCE12</li> <li>GL.GL_LUMINANCE16</li> <li>GL.GL_LUMINANCE_ALPHA</li>
     * <li>GL.GL_LUMINANCE4_ALPHA4</li> <li>GL.GL_LUMINANCE6_ALPHA2</li> <li>GL.GL_LUMINANCE8_ALPHA8</li>
     * <li>GL.GL_LUMINANCE12_ALPHA4</li> <li>GL.GL_LUMINANCE12_ALPHA12</li> <li>GL.GL_LUMINANCE16_ALPHA16</li>
     * <li>GL.GL_INTENSITY</li> <li>GL.GL_INTENSITY4</li> <li>GL.GL_INTENSITY8</li> <li>GL.GL_INTENSITY12</li>
     * <li>GL.GL_INTENSITY16</li> <li>GL.GL_R3_G3_B2</li> <li>GL.GL_RGB</li> <li>GL.GL_RGB4</li> <li>GL.GL_RGB5</li>
     * <li>GL.GL_RGB8</li> <li>GL.GL_RGB10</li> <li>GL.GL_RGB12</li> <li>GL.GL_RGB16</li> <li>GL.GL_RGBA</li>
     * <li>GL.GL_RGBA2</li> <li>GL.GL_RGBA4</li> <li>GL.GL_RGB5_A1</li> <li>GL.GL_RGBA8</li> <li>GL.GL_RGB10_A2</li>
     * <li>GL.GL_RGBA12</li> <li>GL.GL_RGBA16</li> <li>GL.GL_SLUMINANCE</li> <li>GL.GL_SLUMINANCE8</li>
     * <li>GL.GL_SLUMINANCE_ALPHA</li> <li>GL.GL_SLUMINANCE8_ALPHA8</li> <li>GL.GL_SRGB</li> <li>GL.GL_SRGB8</li>
     * <li>GL.GL_SRGB_ALPHA</li> <li>GL.GL_SRGB8_ALPHA8</li> </ul> </code>
     * <p/>
     * If the texture format is any of <code>GL.GL_RGB, GL.GL_RGB8, GL.GL_RGBA, or GL.GL_RGBA8</code>, the tile builder
     * attempts to use OpenGL framebuffer objects to render shapes to the texture tiles. Otherwise, this renders shapes
     * to the framebuffer and copies the framebuffer contents to the texture tiles.
     *
     * @param textureFormat the OpenGL texture format, or 0 to use the default format.
     */
    public void setTileTextureFormat(int textureFormat)
    {
        this.tileTextureFormat = textureFormat;
    }

    /**
     * Returns if linear filtering is used when rendering surface tiles.
     *
     * @return true if linear filtering is used; false if nearest-neighbor filtering is used.
     */
    public boolean isUseLinearFilter()
    {
        return useLinearFilter;
    }

    /**
     * Specifies if linear filtering should be used when rendering surface tiles.
     *
     * @param useLinearFilter true to use linear filtering; false to use nearest-neighbor filtering.
     */
    public void setUseLinearFilter(boolean useLinearFilter)
    {
        this.useLinearFilter = useLinearFilter;
    }

    /**
     * Returns if mip-maps are generated for surface tile textures.
     *
     * @return true if mip-maps are generated; false otherwise.
     */
    public boolean isUseMipmaps()
    {
        return this.useMipmaps;
    }

    /**
     * Specifies if mip-maps should be generated for surface tile textures.
     *
     * @param useMipmaps true to generate mip-maps; false otherwise.
     */
    public void setUseMipmaps(boolean useMipmaps)
    {
        this.useMipmaps = useMipmaps;
    }

    /**
     * Sets the parameter controlling the tile resolution as distance changes between the globe's surface and the eye
     * point. Higher resolution is displayed as the split scale increases from 1.0. Lower resolution is displayed as the
     * split scale decreases from 1.0. The default value is 2.9.
     *
     * @param splitScale a value near 1.0 that controls the tile's surface texel resolution as the distance between the
     *                   globe's surface and the eye point change. Increasing values select higher resolution,
     *                   decreasing values select lower resolution. The default value is 2.9.
     */
    public void setSplitScale(double splitScale)
    {
        this.splitScale = splitScale;
    }

    /**
     * Returns the split scale value controlling the tile's surface texel resolution relative to the distance between
     * the globe's surface at the image position and the eye point.
     *
     * @return the current split scale.
     *
     * @see #setSplitScale(double)
     */
    public double getSplitScale()
    {
        return this.splitScale;
    }

    /**
     * Assembles the surface tiles and draws any SurfaceObjects in the iterable into those offscreen tiles. The surface
     * tiles are assembled to meet the necessary resolution of to the draw context's {@link gov.nasa.worldwind.View}.
     * This may temporarily use the framebuffer to perform offscreen rendering, and therefore should be called during
     * the preRender method of a World Wind {@link gov.nasa.worldwind.layers.Layer}.
     * <p/>
     * This returns an empty List if the specified iterable is null, is empty or contains no SurfaceObjects.
     *
     * @param dc       the draw context to build tiles for.
     * @param iterable the iterable to gather SurfaceObjects from.
     *
     * @return a List of SurfaceTiles containing a composite representation of the specified SurfaceObjects.
     *
     * @throws IllegalArgumentException if the draw context is null.
     */
    public List<SurfaceTile> buildTiles(DrawContext dc, Iterable<?> iterable)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.currentSurfaceObjects.clear();
        this.currentTiles.clear();

        if (iterable == null)
            return Collections.emptyList();

        // Assemble the list of current surface objects from the specified iterable.
        this.assembleSurfaceObjects(iterable);

        // We've cleared any tile assembly state from the last rendering pass. Determine if we can assemble and update
        // the tiles. If not, we're done.
        if (this.currentSurfaceObjects.isEmpty() || !this.canAssembleTiles(dc))
            return Collections.emptyList();

        // Assemble the current visible tiles and update their associated textures if necessary.
        this.assembleTiles(dc);
        this.updateTiles(dc);

        // Tiles in the current tile list contain references to SurfaceObjects. These references are used during tile
        // update, and are longer needed. Clear these lists to ensure we don't retain any dangling references to the
        // surface objects.
        for (SurfaceObjectTile tile : this.currentTiles)
        {
            tile.clearObjectList();
        }

        ArrayList<SurfaceTile> tiles = new ArrayList<SurfaceTile>(this.currentTiles);

        // Objects in the current surface object list and current tile list are no longer needed. Clear the lists to
        // ensure we don't retain dangling references to any objects or tiles.
        this.currentSurfaceObjects.clear();
        this.currentTiles.clear();

        return tiles;
    }

    /**
     * Indicates the tile cache name to use for the specified <code>tileDimension</code>. The cache name is unique to
     * this <code>SurfaceObjectTileBuilder</code> and the <code>tileDimension</code>. Using a unique cache name for each
     * instance and tile dimension ensures that the tiles for this instances and <code>tileDimension</code> do not
     * conflict with any other tiles in the cache.
     * <p/>
     * In practices, there are at most 10 dimensions we'll use (512, 256, 128, 64, 32, 16, 8, 4, 2, 1 ), and therefore
     * at most 10 cache names for each <code>SurfaceObjectTileBuilder</code>.
     * <p/>
     * Subsequent calls are guaranteed to return the same cache name for the same <code>tileDimension</code>.
     *
     * @param tileDimension the texture tile dimension who's cache name is returned.
     *
     * @return the tile cache name for the specified <code>tileDimension</code>
     */
    protected String getTileCacheName(Dimension tileDimension)
    {
        String s = this.tileCacheNameMap.get(tileDimension);
        if (s == null)
        {
            s = this.nextCacheName();
            this.tileCacheNameMap.put(tileDimension, s);
        }

        return s;
    }

    /**
     * Returns a unique name appropriate for use as part of a cache name. The returned string is constructed as follows:
     * {@code this.getClass().getName() + "/" + nextUniqueId()}.
     *
     * @return a unique cache name.
     */
    protected String nextCacheName()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append("/");
        sb.append(nextUniqueId());

        return sb.toString();
    }

    /**
     * Returns the next unique integer associated with a SurfaceObjectTileBuilder. This method is synchronized to ensure
     * that two threads calling simultaneously receive different IDs. Since this method is called from
     * SurfaceObjectTileBuilder's constructor, this is critical to ensure that SurfaceObjectTileBuilders can be safely
     * constructed on separate threads.
     *
     * @return the next unique integer.
     */
    protected static synchronized long nextUniqueId()
    {
        return nextUniqueId++;
    }

    /**
     * Returns the tile dimension used to create the tile textures for the specified <code>DrawContext</code>. This
     * attempts to use the caller configured <code>{@link #tileDimension}</code>, but always returns a dimension that is
     * is a power of two, is square, and fits in the <code>DrawContext</code>'s viewport.
     *
     * @param dc the <code>DrawContext</code> to compute a texture tile dimension for.
     *
     * @return a texture tile dimension appropriate for the specified <code>DrawContext</code>.
     */
    protected Dimension computeTextureTileDimension(DrawContext dc)
    {
        // Force a square dimension by using the maximum of the tile builder's tileWidth and tileHeight.
        int maxSize = Math.max(this.tileDimension.width, this.tileDimension.height);

        // The viewport may be smaller than the desired dimension. For that reason, we constrain the desired tile
        // dimension by the viewport width and height.
        Rectangle viewport = dc.getView().getViewport();
        if (maxSize > viewport.width)
            maxSize = viewport.width;
        if (maxSize > viewport.height)
            maxSize = viewport.height;

        // The final dimension used to render all surface tiles will be the power of two which is less than or equal to
        // the preferred dimension, and which fits into the viewport.

        int potSize = WWMath.powerOfTwoFloor(maxSize);
        return new Dimension(potSize, potSize);
    }

    //**************************************************************//
    //********************  Tile Updating  *************************//
    //**************************************************************//

    /**
     * Updates each {@link SurfaceObjectTileBuilder.SurfaceObjectTile} in the {@link #currentTiles} list. This is
     * typically called after {@link #assembleTiles(DrawContext)} to update the assembled tiles.
     * <p/>
     * This method does nothing if <code>currentTiles</code> is empty.
     *
     * @param dc the draw context the tiles relate to.
     */
    protected void updateTiles(DrawContext dc)
    {
        if (this.currentTiles.isEmpty())
            return;

        // The tile drawing rectangle has the same dimension as the current tile viewport, but it's lower left corner
        // is placed at the origin. This is because the orthographic projection setup by OGLRenderToTextureSupport
        // maps (0, 0) to the lower left corner of the drawing region, therefore we can drop the (x, y) offset when
        // drawing pixels to the texture, as (0, 0) is automatically mapped to (x, y). Since we've created the tiles
        // from a LevelSet where each level has equivalent dimension, we assume that tiles in the current tile list
        // have equivalent dimension.

        // The OpenGL framebuffer object extension used by RenderToTextureSupport works only for texture formats
        // GL_RGB and GL_RGBA. Disable framebuffer objects if the tile builder has been configured with a different
        // format.
        this.rttSupport.setEnableFramebufferObject(
            this.tileTextureFormat == 0 || // Default format is GL_RGB8.
                this.tileTextureFormat == GL.GL_RGB ||
                this.tileTextureFormat == GL.GL_RGB8 ||
                this.tileTextureFormat == GL.GL_RGBA ||
                this.tileTextureFormat == GL.GL_RGBA8);

        this.rttSupport.beginRendering(dc, 0, 0, this.currentTileDimension.width, this.currentTileDimension.height);
        try
        {
            for (SurfaceObjectTile tile : this.currentTiles)
            {
                this.updateTile(dc, tile);
            }
        }
        finally
        {
            this.rttSupport.endRendering(dc);
        }
    }

    /**
     * Draws the current list of SurfaceObjects into the specified surface tile. The surface tiles is updated only when
     * necessary. The tile keeps track of the list of SurfaceObjects rendered into it, and the state keys those objects.
     * The tile is updated if the list changes, if any of the state keys change, or if the tile has no texture.
     * Otherwise the tile is left unchanged and the update is skipped.
     *
     * @param dc   the draw context the tile relates to.
     * @param tile the tile to update.
     */
    protected void updateTile(DrawContext dc, SurfaceObjectTile tile)
    {
        // Get the tile's texture from the draw context's texture cache. If null we create a new texture and update the
        // texture cache below.
        Texture texture = tile.getTexture(dc.getTextureCache());

        // Compare the previous tile state against the currently computed state to determine if the tile needs to be
        // updated. The tile needs to be updated if any the following conditions are true:
        // * The tile has no texture.
        // * The tile has no state.
        // * The list of intersecting objects has changed.
        // * An intersecting object's state key is different than one stored in the tile's previous state key.
        Object tileStateKey = tile.getStateKey(dc);

        if (texture != null && tileStateKey.equals(tile.lastUpdateStateKey))
            return;

        // If the tile needs to be updated, then assign its lastUpdateStateKey before its texture is created. This
        // ensures that the lastUpdateStateKey is current when the tile is added to the cache.
        tile.lastUpdateStateKey = tileStateKey;

        if (texture == null) // Create the tile's texture if it doesn't already have one.
        {
            texture = this.createTileTexture(dc, tile.getWidth(), tile.getHeight());
            tile.setTexture(dc.getTextureCache(), texture);
        }

        if (texture == null) // This should never happen, but we check anyway.
        {
            Logging.logger().warning(Logging.getMessage("nullValue.TextureIsNull"));
            return;
        }

        try
        {
            // SurfaceObjects expect the SurfaceTileDrawContext to be attached to the draw context's AVList. Create a
            // SurfaceTileDrawContext with the tile's Sector and viewport. The Sector defines the context's geographic
            // extent, and the viewport defines the context's corresponding viewport in pixels.
            dc.setValue(AVKey.SURFACE_TILE_DRAW_CONTEXT, this.createSurfaceTileDrawContext(tile));

            this.rttSupport.setColorTarget(dc, texture);
            this.rttSupport.clear(dc, new Color(0, 0, 0, 0)); // Set all texture pixels to transparent black.

            if (tile.hasObjects())
            {
                for (SurfaceObject so : tile.getObjectList())
                {
                    so.render(dc);
                }
            }
        }
        finally
        {
            this.rttSupport.setColorTarget(dc, null);

            dc.removeKey(AVKey.SURFACE_TILE_DRAW_CONTEXT);
        }
    }

    /**
     * Returns a new surface tile texture for use on the specified draw context with the specified width and height.
     * <p/>
     * The returned texture's internal format is specified by <code>tilePixelFormat</code>. If
     * <code>tilePixelFormat</code> is zero, this returns a texture with internal format <code>GL.GL_RGBA8</code>.
     * <p/>
     * The returned texture's parameters are configured as follows: <table> <tr><th>Parameter
     * Name</th><th>Value</th></tr> <tr><td><code>GL.GL_TEXTURE_MIN_FILTER</code></td><td><code>GL_LINEAR_MIPMAP_LINEAR</code>
     * if <code>useLinearFilter</code> and <code>useMipmaps</code> are both true, <code>GL_LINEAR</code> if
     * <code>useLinearFilter</code> is true and <code>useMipmaps</code> is false, and <code>GL_NEAREST</code> if
     * <code>useLinearFilter</code> is false.</td></tr> <tr><td><code>GL.GL_TEXTURE_MAG_FILTER</code></td><td><code>GL_LINEAR</code>
     * if <code>useLinearFilter</code> is true, <code>GL_NEAREST</code> if <code>useLinearFilter</code> is
     * false.</td></tr> <tr><td><code>GL.GL_TEXTURE_WRAP_S</code></td><td><code>GL_CLAMP_TO_EDGE</code></td></tr>
     * <tr><td><code>GL.GL_TEXTURE_WRAP_T</code></td><td><code>GL_CLAMP_TO_EDGE</code></td></tr>
     *
     * @param dc     the draw context to create a texture for.
     * @param width  the texture's width, in pixels.
     * @param height the texture's height, in pixels.
     *
     * @return a new texture with the specified width and height.
     */
    protected Texture createTileTexture(DrawContext dc, int width, int height)
    {
        int internalFormat = this.tileTextureFormat;
        if (internalFormat == 0)
            internalFormat = DEFAULT_TEXTURE_INTERNAL_FORMAT;

        int pixelFormat = OGLUtil.computeTexturePixelFormat(internalFormat);
        if (pixelFormat == 0)
            pixelFormat = DEFAULT_TEXTURE_PIXEL_FORMAT;

        Texture t;
        GL gl = dc.getGL();

        TextureData td = new TextureData(
            internalFormat,       // internal format
            width, height,        // dimension
            0,                    // border
            pixelFormat,          // pixel format
            GL.GL_UNSIGNED_BYTE,  // pixel type
            this.isUseMipmaps(),  // mipmap
            false, false,         // dataIsCompressed, mustFlipVertically
            null, null)           // buffer, flusher
        {
            /**
             * Overridden to return a non-zero size. TextureData does not compute an estimated memory size if the buffer
             * is null. Therefore we override getEstimatedMemorySize() to return the appropriate size in bytes of a
             * texture with the common pixel formats.
             */
            @Override
            public int getEstimatedMemorySize()
            {
                int sizeInBytes = OGLUtil.estimateTextureMemorySize(this.getInternalFormat(), this.getWidth(),
                    this.getHeight(), this.getMipmap());
                if (sizeInBytes > 0)
                    return sizeInBytes;

                return super.getEstimatedMemorySize();
            }
        };

        t = TextureIO.newTexture(td);
        t.bind();

        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, this.isUseLinearFilter() ?
            (this.isUseMipmaps() ? GL.GL_LINEAR_MIPMAP_LINEAR : GL.GL_LINEAR) : GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, this.isUseLinearFilter() ?
            GL.GL_LINEAR : GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

        return t;
    }

    /**
     * Returns a new Object representing the drawing context for the specified tile. The returned object should
     * represent the tile's sector and it's corresponding viewport in pixels.
     *
     * @param tile The tile to create a context for.
     *
     * @return a new drawing context for the specified tile.
     */
    protected Object createSurfaceTileDrawContext(SurfaceObjectTile tile)
    {
        return new SurfaceTileDrawContext(tile.getSector(), tile.getWidth(), tile.getHeight());
    }

    //**************************************************************//
    //********************  Surface Object Assembly  ***************//
    //**************************************************************//

    /**
     * Adds any SurfaceObjects in the specified Iterable to the tile builder's {@link #currentSurfaceObjects} list.
     *
     * @param iterable the Iterable to gather SurfaceObjects from.
     */
    protected void assembleSurfaceObjects(Iterable<?> iterable)
    {
        // Gather up all the SurfaceObjects, ignoring null references and non SurfaceObjects.
        for (Object o : iterable)
        {
            if (o instanceof SurfaceObject)
                this.currentSurfaceObjects.add((SurfaceObject) o);
        }
    }

    //**************************************************************//
    //********************  LevelSet Assembly  *********************//
    //**************************************************************//

    /**
     * Returns a shared <code>LevelSet</code> for the specified <code>tileDimension</code>. All instances of
     * <code>SurfaceObjectTileBuilder</code> share common LevelSets to determine which tiles are visible, but create
     * unique tile instances and uses a unique tile cache name. Since all instances use the same tile structure to
     * determine visible tiles, this saves memory while ensuring that each instance stores its own tiles in the cache.
     * <p/>
     * The returned LevelSet's cache name and dataset name are dummy values, and should not be used. Use this tile
     * builder's cache name for the specified <code>tileDimension</code> instead.
     * <p/>
     * In practice, there are at most 10 dimensions we use: 512, 256, 128, 64, 32, 16, 8, 4, 2, 1. Therefore keeping the
     * <code>LevelSet</code>s in a map requires little memory overhead, and ensures each <code>LevelSet</code> is
     * retained once constructed. Retaining references to the <code>LevelSet</code>s means we're able to re-use the
     * texture resources associated with each <code>LevelSet</code> in the <code>DrawContext</code>'s texture cache.
     * <p/>
     * Subsequent calls are guaranteed to return the same <code>LevelSet</code> for the same
     * <code>tileDimension</code>.
     *
     * @param tileDimension the DrawContext to return a LevelSet for.
     *
     * @return a LevelSet who's tile dimension fits in the DrawContext's viewport.
     */
    protected LevelSet getLevelSet(Dimension tileDimension)
    {
        // If we already have a LevelSet for the dimension, just return it. Otherwise create it and put it in a map for
        // use during subsequent calls.
        LevelSet levelSet = levelSetMap.get(tileDimension);
        if (levelSet == null)
        {
            levelSet = createLevelSet(tileDimension.width, tileDimension.height);
            levelSetMap.put(tileDimension, levelSet);
        }

        return levelSet;
    }

    /**
     * Returns a new LevelSet with the specified tile width and height. The LevelSet overs the full sphere, has a level
     * zero tile delta of {@link #DEFAULT_LEVEL_ZERO_TILE_DELTA}, has number of levels equal to {@link
     * #DEFAULT_NUM_LEVELS} (with no empty levels). The LevelSets' cache name and dataset name dummy values, and should
     * not be used.
     *
     * @param tileWidth  the LevelSet's tile width, in pixels.
     * @param tileHeight the LevelSet's tile height, in pixels.
     *
     * @return a new LevelSet configured to with
     */
    protected static LevelSet createLevelSet(int tileWidth, int tileHeight)
    {
        AVList params = new AVListImpl();
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, DEFAULT_LEVEL_ZERO_TILE_DELTA);
        params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);
        params.setValue(AVKey.NUM_LEVELS, DEFAULT_NUM_LEVELS);
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
        params.setValue(AVKey.TILE_WIDTH, tileWidth);
        params.setValue(AVKey.TILE_HEIGHT, tileHeight);
        // This is a shared LevelSet, so just supply a dummy cache name and dataset name.
        params.setValue(AVKey.DATA_CACHE_NAME, SurfaceObjectTileBuilder.class.getName());
        params.setValue(AVKey.DATASET_NAME, SurfaceObjectTileBuilder.class.getName());
        // We won't use any tile resource paths, so just supply a dummy format suffix.
        params.setValue(AVKey.FORMAT_SUFFIX, SurfaceObjectTileBuilder.class.getName());

        return new LevelSet(params);
    }

    //**************************************************************//
    //********************  Tile Assembly  *************************//
    //**************************************************************//

    /**
     * Returns true if the draw context's viewport width and height are greater than zero.
     *
     * @param dc the DrawContext to test.
     *
     * @return true if the DrawContext's has a non-zero viewport; false otherwise.
     */
    protected boolean canAssembleTiles(DrawContext dc)
    {
        Rectangle viewport = dc.getView().getViewport();
        return viewport.getWidth() > 0 && viewport.getHeight() > 0;
    }

    /**
     * Assembles a set of surface tiles that are visible in the specified DrawContext and meet the tile builder's
     * resolution criteria. Tiles are culled against the current SurfaceObject list, against the DrawContext's view
     * frustum during rendering mode, and against the DrawContext's pick frustums during picking mode. If a tile does
     * not meet the tile builder's resolution criteria, it's split into four sub-tiles and the process recursively
     * repeated on the sub-tiles. Visible leaf tiles are added to the <code>{@link #currentTiles}</code> list.
     * <p/>
     * During assembly each SurfaceObject in {@link #currentSurfaceObjects} is sorted into the tiles they intersect. The
     * top level tiles are used as an index to quickly determine which tiles each SurfaceObjects intersects.
     * SurfaceObjects are sorted into sub-tiles by simple intersection tests. SurfaceObjects are added to each tile's
     * surface object list at most once. See {@link SurfaceObjectTileBuilder.SurfaceObjectTile#addSurfaceObject(SurfaceObject,
     * gov.nasa.worldwind.geom.Sector)}. Tiles that don't intersect any SurfaceObjects are discarded.
     *
     * @param dc the DrawContext to assemble tiles for.
     */
    protected void assembleTiles(DrawContext dc)
    {
        // Compute the texture tile dimension to use for this DrawContext. This dimension is always square and a power
        // of two.
        this.currentTileDimension = this.computeTextureTileDimension(dc);
        // Get the level set and tile cache name to use for the current tile dimension. The LevelSet is shared by all
        // SurfaceObjectTileBuilders, while the cache name is unique to this instance. Since all
        // SurfaceObjectTileBuilders use the same tile structure to determine visible tiles, this saves memory while
        // preventing a conflict in the tile and texture caches.
        String tileCacheName = this.getTileCacheName(this.currentTileDimension);
        LevelSet levelSet = this.getLevelSet(this.currentTileDimension);

        Level level = levelSet.getFirstLevel();
        Angle dLat = level.getTileDelta().getLatitude();
        Angle dLon = level.getTileDelta().getLongitude();
        Angle latOrigin = levelSet.getTileOrigin().getLatitude();
        Angle lonOrigin = levelSet.getTileOrigin().getLongitude();

        // Store the top level tiles in a set to ensure that each top level tile is added only once. Store the tiles
        // that intersect each surface object in a set to ensure that each object is added to a tile at most once.
        Set<SurfaceObjectTile> topLevelTiles = new HashSet<SurfaceObjectTile>();
        Set<Object> intersectingTileKeys = new HashSet<Object>();

        // Iterate over the current surface objects, adding each surface object to the top level tiles that it
        // intersects. This produces a set of top level tiles containing the surface objects that intersect each tile.
        // We use the tile structure as an index to quickly determine the tiles a surface object intersects, and add
        // object to those tiles. This has the effect of quickly sorting the objects into the top level tiles. We
        // collect the top level tiles in a HashSet to ensure there are no duplicates when multiple objects intersect
        // the same top level tiles.
        for (SurfaceObject so : this.currentSurfaceObjects)
        {
            List<Sector> sectors = so.getSectors(dc);
            if (sectors == null)
                continue;

            for (Sector s : sectors)
            {
                // Use the LevelSets tiling scheme to index the surface object's sector into the top level tiles. This
                // index operation is faster than computing an intersection test between each tile and the list of
                // surface objects.
                int firstRow = Tile.computeRow(dLat, s.getMinLatitude(), latOrigin);
                int firstCol = Tile.computeColumn(dLon, s.getMinLongitude(), lonOrigin);
                int lastRow = Tile.computeRow(dLat, s.getMaxLatitude(), latOrigin);
                int lastCol = Tile.computeColumn(dLon, s.getMaxLongitude(), lonOrigin);

                Angle p1 = Tile.computeRowLatitude(firstRow, dLat, latOrigin);
                for (int row = firstRow; row <= lastRow; row++)
                {
                    Angle p2;
                    p2 = p1.add(dLat);

                    Angle t1 = Tile.computeColumnLongitude(firstCol, dLon, lonOrigin);
                    for (int col = firstCol; col <= lastCol; col++)
                    {
                        Angle t2;
                        t2 = t1.add(dLon);

                        Object tileKey = this.createTileKey(level, row, col, tileCacheName);

                        // Ignore this tile if the surface object has already been added to it. This handles dateline
                        // spanning surface objects which have two sectors that share a common boundary.
                        if (intersectingTileKeys.contains(tileKey))
                            continue;

                        SurfaceObjectTile tile = (SurfaceObjectTile) TextureTile.getMemoryCache().getObject(tileKey);
                        if (tile == null)
                        {
                            tile = this.createTile(new Sector(p1, p2, t1, t2), level, row, col, tileCacheName);
                            TextureTile.getMemoryCache().add(tileKey, tile);
                        }

                        intersectingTileKeys.add(tileKey); // Set of intersecting tile keys ensure no duplicate objects.
                        topLevelTiles.add(tile); // Set of top level tiles ensures no duplicates tiles.
                        tile.addSurfaceObject(so, s);

                        t1 = t2;
                    }
                    p1 = p2;
                }
            }

            intersectingTileKeys.clear(); // Clear the intersecting tile keys for the next surface object.            
        }

        // Add each top level tile or its descendants to the current tile list.
        for (SurfaceObjectTile tile : topLevelTiles)
        {
            this.addTileOrDescendants(dc, levelSet, null, tile);
        }
    }

    /**
     * Potentially adds the specified tile or its descendants to the tile builder's {@link #currentTiles} list. The tile
     * and its descendants are discarded if the tile is not visible or does not intersect any SurfaceObjects in the
     * parent's surface object list. See {@link SurfaceObjectTileBuilder.SurfaceObjectTile#getObjectList()}.
     * <p/>
     * If the tile meet the tile builder's resolution criteria it's added to the tile builder's
     * <code>currentTiles</code> list. Otherwise, it's split into four sub-tiles and each tile is recursively processed.
     * See {@link #meetsRenderCriteria(DrawContext, gov.nasa.worldwind.util.LevelSet, gov.nasa.worldwind.util.Tile)}.
     *
     * @param dc       the current DrawContext.
     * @param levelSet the tile's LevelSet.
     * @param parent   the tile's parent, or null if the tile is a top level tile.
     * @param tile     the tile to add.
     */
    protected void addTileOrDescendants(DrawContext dc, LevelSet levelSet, SurfaceObjectTile parent,
        SurfaceObjectTile tile)
    {
        // Ignore this tile if it falls completely outside the DrawContext's visible sector.
        if (!this.intersectsVisibleSector(dc, tile))
        {
            // This tile is not added to the current tile list, so we clear it's object list to prepare it for use
            // during the next frame.
            tile.clearObjectList();
            return;
        }

        // Ignore this tile if it falls completely outside the frustum. This may be the viewing frustum or the pick
        // frustum, depending on the implementation.
        if (!this.intersectsFrustum(dc, tile))
        {
            // This tile is not added to the current tile list, so we clear it's object list to prepare it for use
            // during the next frame.
            tile.clearObjectList();
            return;
        }

        // If the parent tile is not null, add any parent surface objects that intersect this tile.
        if (parent != null)
            this.addIntersectingObjects(dc, parent, tile);

        // Ignore tiles that do not intersect any surface objects.
        if (!tile.hasObjects())
            return;

        // If this tile meets the current rendering criteria, add it to the current tile list. This tile's object list
        // is cleared after the tile update operation.
        if (this.meetsRenderCriteria(dc, levelSet, tile))
        {
            this.addTile(tile);
            return;
        }

        Level nextLevel = levelSet.getLevel(tile.getLevelNumber() + 1);
        for (TextureTile subTile : tile.createSubTiles(nextLevel))
        {
            this.addTileOrDescendants(dc, levelSet, tile, (SurfaceObjectTile) subTile);
        }

        // This tile is not added to the current tile list, so we clear it's object list to prepare it for use during
        // the next frame.
        tile.clearObjectList();
    }

    /**
     * Adds SurfaceObjects from the parent's object list to the specified tile's object list. If the tile's sector does
     * not intersect the sector bounding the parent's object list, this does nothing. Otherwise, this adds any of the
     * parent's SurfaceObjects that intersect the tile's sector to the tile's object list.
     *
     * @param dc     the current DrawContext.
     * @param parent the tile's parent.
     * @param tile   the tile to add intersecting SurfaceObject to.
     */
    protected void addIntersectingObjects(DrawContext dc, SurfaceObjectTile parent, SurfaceObjectTile tile)
    {
        // If the parent has no objects, then there's nothing to add to this tile and we exit immediately.
        if (!parent.hasObjects())
            return;

        // If this tile does not intersect the parent's object bounding sector, then none of the parent's objects
        // intersect this tile. Therefore we exit immediately, and do not add any objects to this tile.
        if (!tile.getSector().intersects(parent.getObjectSector()))
            return;

        // If this tile contains the parent's object bounding sector, then all of the parent's objects intersect this
        // tile. Therefore we just add all of the parent's objects to this tile. Additionally, the parent's object
        // bounding sector becomes this tile's object bounding sector.
        if (tile.getSector().contains(parent.getObjectSector()))
        {
            tile.addAllSurfaceObjects(parent.getObjectList(), parent.getObjectSector());
        }
        // Otherwise, the tile may intersect some of the parent's object list. Compute which objects intersect this
        // tile, and compute this tile's bounding sector as the union of those object's sectors.
        else
        {
            for (SurfaceObject so : parent.getObjectList())
            {
                List<Sector> sectors = so.getSectors(dc);
                if (sectors == null)
                    continue;

                // Test intersection against each of the SurfaceObject's sectors. We break after finding an intersection
                // to avoid adding the same object to the tile more than once.
                for (Sector s : sectors)
                {
                    if (tile.getSector().intersects(s))
                    {
                        tile.addSurfaceObject(so, s);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Adds the specified tile to the tile builder's {@link #currentTiles} list.
     *
     * @param tile the tile to add.
     */
    protected void addTile(SurfaceObjectTile tile)
    {
        this.currentTiles.add(tile);
        TextureTile.getMemoryCache().add(tile.getTileKey(), tile);
    }

    /**
     * Test if the tile intersects the specified draw context's frustum. During picking mode, this tests intersection
     * against all of the draw context's pick frustums. During rendering mode, this tests intersection against the draw
     * context's viewing frustum.
     *
     * @param dc   the draw context the SurfaceObject is related to.
     * @param tile the tile to test for intersection.
     *
     * @return true if the tile intersects the draw context's frustum; false otherwise.
     */
    protected boolean intersectsFrustum(DrawContext dc, TextureTile tile)
    {
        Extent extent = tile.getExtent(dc);
        if (extent == null)
            return false;

        if (dc.isPickingMode())
            return dc.getPickFrustums().intersectsAny(extent);

        return dc.getView().getFrustumInModelCoordinates().intersects(extent);
    }

    /**
     * Test if the specified tile intersects the draw context's visible sector. This returns false if the draw context's
     * visible sector is null.
     *
     * @param dc   the current draw context.
     * @param tile the tile to test for intersection.
     *
     * @return true if the tile intersects the draw context's visible sector; false otherwise.
     */
    protected boolean intersectsVisibleSector(DrawContext dc, TextureTile tile)
    {
        return dc.getVisibleSector() != null && dc.getVisibleSector().intersects(tile.getSector());
    }

    /**
     * Tests if the specified tile meets the rendering criteria on the specified draw context. This returns true if the
     * tile is from the level set's final level, or if the tile achieves the desired resolution on the draw context.
     *
     * @param dc       the current draw context.
     * @param levelSet the level set the tile belongs to.
     * @param tile     the tile to test.
     *
     * @return true if the tile meets the rendering criteria; false otherwise.
     */
    protected boolean meetsRenderCriteria(DrawContext dc, LevelSet levelSet, Tile tile)
    {
        return levelSet.isFinalLevel(tile.getLevel().getLevelNumber()) || !this.needToSplit(dc, tile);
    }

    /**
     * Tests if the specified tile must be split to meets the desired resolution on the specified draw context. This
     * compares the distance form the eye point to the tile to determine if the tile meets the desired resolution for
     * the {@link gov.nasa.worldwind.View} attached to the draw context.
     *
     * @param dc   the current draw context.
     * @param tile the tile to test.
     *
     * @return true if the tile must be split; false otherwise.
     */
    protected boolean needToSplit(DrawContext dc, Tile tile)
    {
        Vec4[] corners = tile.getSector().computeCornerPoints(dc.getGlobe(), dc.getVerticalExaggeration());
        Vec4 centerPoint = tile.getSector().computeCenterPoint(dc.getGlobe(), dc.getVerticalExaggeration());

        Vec4 eyePoint = dc.getView().getEyePoint();
        double d1 = eyePoint.distanceTo3(corners[0]);
        double d2 = eyePoint.distanceTo3(corners[1]);
        double d3 = eyePoint.distanceTo3(corners[2]);
        double d4 = eyePoint.distanceTo3(corners[3]);
        double d5 = eyePoint.distanceTo3(centerPoint);

        double minDistance = d1;
        if (d2 < minDistance)
            minDistance = d2;
        if (d3 < minDistance)
            minDistance = d3;
        if (d4 < minDistance)
            minDistance = d4;
        if (d5 < minDistance)
            minDistance = d5;

        // Compute the cell size as a function of the tile's latitude delta in meters and the tile builder's target tile
        // density in texels. We use the target density instead of the tile's actual density to ensure that tiles
        // smaller than the target density are split according to the same metric. Smaller tiles are used when the
        // viewport dimension is smaller than the target density. In this case, using the same metric to split tiles
        // avoids creating a large number of tiles for a small viewport, while still rendering sharp lines and edges in
        // the smaller viewport.
        double cellSize = tile.getSector().getDeltaLatRadians() * dc.getGlobe().getRadius()
            / (double) this.tileDimension.height;

        return Math.log10(minDistance) < (this.getSplitScale() + Math.log10(cellSize));
    }

    //**************************************************************//
    //********************  Surface Object Tile  *******************//
    //**************************************************************//

    /**
     * Returns a new SurfaceObjectTile corresponding to the specified {@code sector}, {@code level}, {@code row}, {@code
     * column}, and {@code cacheName}.
     *
     * @param sector    The tile's Sector.
     * @param level     The tile's Level in a {@link LevelSet}.
     * @param row       The tile's row in the Level, starting from 0 and increasing to the right.
     * @param column    The tile's column in the Level, starting from 0 and increasing upward.
     * @param cacheName Tile tile's cache name.
     *
     * @return a new SurfaceObjectTile.
     */
    protected SurfaceObjectTile createTile(Sector sector, Level level, int row, int column, String cacheName)
    {
        return new SurfaceObjectTile(sector, level, row, column, cacheName);
    }

    /**
     * Returns a new tile key corresponding to the tile with the specified {@code level}, {@code row}, {@code column},
     * and {@code cacheName}.
     *
     * @param level     The tile's Level in a {@link LevelSet}.
     * @param row       The tile's row in the Level, starting from 0 and increasing to the right.
     * @param column    The tile's column in the Level, starting from 0 and increasing upward.
     * @param cacheName Tile tile's cache name.
     *
     * @return a tile key.
     */
    protected Object createTileKey(Level level, int row, int column, String cacheName)
    {
        return new TileKey(level.getLevelNumber(), row, column, cacheName);
    }

    /**
     * Represents a {@link gov.nasa.worldwind.layers.TextureTile} who's contents is constructed by a set of surface
     * objects. The tile maintains a collection of surface objects that intersect the tile, and provides methods for to
     * modify and retrieve that collection. Additionally, the method {@link #getStateKey(DrawContext)} provides a
     * mechanism to uniquely identify the tile's current state, including the state of each intersecting surface
     * object.
     */
    protected static class SurfaceObjectTile extends TextureTile
    {
        /** The sector that bounds the surface objects intersecting the tile. */
        protected Sector objectSector;
        /** List of surface objects intersecting the tile. */
        protected List<SurfaceObject> intersectingObjects;
        /** The state key that was valid when the tile was last updated. */
        protected Object lastUpdateStateKey;

        /**
         * Constructs a tile for a given sector, level, row and column of the tile's containing tile set.
         *
         * @param sector    The sector corresponding with the tile.
         * @param level     The tile's level within a containing level set.
         * @param row       The row index (0 origin) of the tile within the indicated level.
         * @param column    The column index (0 origin) of the tile within the indicated level.
         * @param cacheName The tile's cache name. Overrides the Level's cache name to associates the tile with it's
         *                  tile builder in a global cache.
         *
         * @throws IllegalArgumentException if any of the {@code sector}, {@code level}, or {@code cacheName } are
         *                                  {@code null}.
         */
        public SurfaceObjectTile(Sector sector, Level level, int row, int column, String cacheName)
        {
            super(sector, level, row, column, cacheName);
        }

        /**
         * Returns the tile's size in bytes. Overridden to append the size of the {@link #cacheName} and the {@link
         * #lastUpdateStateKey} to the superclass' computed size.
         *
         * @return The tile's size in bytes.
         */
        @Override
        public long getSizeInBytes()
        {
            long size = super.getSizeInBytes();

            if (this.lastUpdateStateKey instanceof Cacheable)
                size += ((Cacheable) this.lastUpdateStateKey).getSizeInBytes();
            else if (this.lastUpdateStateKey != null)
                size += 4; // If the object doesn't implement Cacheable, just account for the reference to it.

            return size;
        }

        /**
         * Returns an object that uniquely identifies the tile's state on the specified draw context. This object is
         * guaranteed to be globally unique; an equality test with a state key from another always returns false.
         *
         * @param dc the draw context the state key relates to.
         *
         * @return an object representing surface object's current state.
         */
        public Object getStateKey(DrawContext dc)
        {
            return new SurfaceObjectTileStateKey(dc, this);
        }

        /**
         * Returns a sector that bounds the surface objects intersecting the tile. This returns null if no surface
         * objects intersect the tile.
         *
         * @return a sector bounding the tile's intersecting objects.
         */
        public Sector getObjectSector()
        {
            return this.objectSector;
        }

        /**
         * Returns whether list of surface objects intersecting this tile has elements.
         *
         * @return {@code true} if the list of surface objects intersecting this tile has elements, and {@code false}
         *         otherwise.
         */
        public boolean hasObjects()
        {
            return this.intersectingObjects != null && !this.intersectingObjects.isEmpty();
        }

        /**
         * Returns a list of surface objects intersecting the tile.
         *
         * @return a tile's intersecting objects.
         */
        public List<SurfaceObject> getObjectList()
        {
            return this.intersectingObjects;
        }

        /**
         * Clears the tile's list of intersecting objects. {@link #getObjectSector()} returns null after calling this
         * method.
         */
        public void clearObjectList()
        {
            this.intersectingObjects = null;
            this.objectSector = null;
        }

        /**
         * Adds the specified surface object to the tile's list of intersecting objects.
         *
         * @param so     the surface object to add.
         * @param sector the sector bounding the specified surface object.
         */
        public void addSurfaceObject(SurfaceObject so, Sector sector)
        {
            if (this.intersectingObjects == null)
                this.intersectingObjects = new ArrayList<SurfaceObject>();

            this.intersectingObjects.add(so);
            this.objectSector = (this.objectSector != null) ? this.objectSector.union(sector) : sector;
        }

        /**
         * Adds the specified collection of surface objects to the tile's list of intersecting objects.
         *
         * @param c      the collection of surface objects to add.
         * @param sector the sector bounding the specified surface object collection.
         */
        public void addAllSurfaceObjects(List<SurfaceObject> c, Sector sector)
        {
            if (this.intersectingObjects == null)
                this.intersectingObjects = new ArrayList<SurfaceObject>();

            this.intersectingObjects.addAll(c);
            this.objectSector = (this.objectSector != null) ? this.objectSector.union(sector) : sector;
        }

        /**
         * {@inheritDoc}
         * <p/>
         * Overridden to return a new SurfaceObjectTile. The returned tile is created with the same cache name as this
         * tile.
         */
        @Override
        protected TextureTile createSubTile(Sector sector, Level level, int row, int col)
        {
            return new SurfaceObjectTile(sector, level, row, col, this.getCacheName());
        }

        /**
         * {@inheritDoc}
         * <p/>
         * Overridden to return a TileKey with the same cache name as this tile.
         */
        @Override
        protected TileKey createSubTileKey(Level level, int row, int col)
        {
            return new TileKey(level.getLevelNumber(), row, col, this.getCacheName());
        }
    }

    /**
     * Represents a surface object tile's current state. TileStateKey distinguishes the tile's state by comparing the
     * individual state keys of the surface objects intersecting the tile. This does not retain any references to the
     * surface objects themselves. Should the tile state key live longer than the surface objects, the state key does
     * not prevent those objects from being reclaimed by the garbage collector.
     */
    protected static class SurfaceObjectTileStateKey implements Cacheable
    {
        protected final TileKey tileKey;
        protected final Object[] intersectingObjectKeys;

        /**
         * Construsts a tile state key for the specified surface object tile.
         *
         * @param dc   the draw context the state key is related to.
         * @param tile the tile to construct a state key for.
         */
        public SurfaceObjectTileStateKey(DrawContext dc, SurfaceObjectTile tile)
        {
            if (tile != null && tile.hasObjects())
            {
                this.tileKey = tile.getTileKey();
                this.intersectingObjectKeys = new Object[tile.getObjectList().size()];

                int index = 0;
                for (SurfaceObject so : tile.getObjectList())
                {
                    this.intersectingObjectKeys[index++] = so.getStateKey(dc);
                }
            }
            else
            {
                this.tileKey = null;
                this.intersectingObjectKeys = null;
            }
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || this.getClass() != o.getClass())
                return false;

            // Compare the tile keys and each state key in the array. The state keys are equal if the tile keys are
            // equal, the arrays equivalent length, and each array element is equivalent. Arrays.equals() correctly
            // handles null references.
            SurfaceObjectTileStateKey that = (SurfaceObjectTileStateKey) o;
            return (this.tileKey != null ? this.tileKey.equals(that.tileKey) : that.tileKey == null)
                && Arrays.equals(this.intersectingObjectKeys, that.intersectingObjectKeys);
        }

        @Override
        public int hashCode()
        {
            int result = this.tileKey != null ? this.tileKey.hashCode() : 0;
            result = 31 * result + Arrays.hashCode(this.intersectingObjectKeys); // Correctly handles a null reference.
            return result;
        }

        /**
         * Returns the tile state key's size in bytes. The total size of the intersecting object keys, plus the size of
         * the array itself. The tileKey is owned by the SurfaceObjectTile, so we don't include it in the state key's
         * size.
         *
         * @return The state key's size in bytes.
         */
        public long getSizeInBytes()
        {
            if (this.intersectingObjectKeys == null)
                return 0;

            long size = 4 * this.intersectingObjectKeys.length; // For the array references.

            for (Object o : this.intersectingObjectKeys)
            {
                if (o instanceof Cacheable)
                    size += ((Cacheable) o).getSizeInBytes();
                else if (o != null)
                    size += 4; // If the object doesn't implement Cacheable, just account for the reference to it.
            }

            return size;
        }
    }
}
