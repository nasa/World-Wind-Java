/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.*;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.image.*;
import java.nio.FloatBuffer;
import java.util.*;

/**
 * @author dcollins
 * @version $Id$
 */
public abstract class AbstractTacticalSymbol extends WWObjectImpl implements TacticalSymbol, OrderedRenderable
{
    protected static class IconSource
    {
        protected String symbolId;
        protected IconRetriever iconRetriever;
        protected AVList params;

        public IconSource(String symbolId, IconRetriever iconRetriever, AVList params)
        {
            this.symbolId = symbolId;
            this.iconRetriever = iconRetriever;

            if (params != null)
            {
                // If the specified parameters are non-null, then store a copy of the parameters in this key's params
                // property to insulate it from changes made by the caller. This params list must not change after
                // construction this key's properties must be immutable.
                this.params = new AVListImpl();
                this.params.setValues(params);
            }
        }

        public String getSymbolId()
        {
            return this.symbolId;
        }

        public IconRetriever getIconRetriever()
        {
            return this.iconRetriever;
        }

        public AVList getParams()
        {
            return this.params;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            IconSource that = (IconSource) o;

            if (this.iconRetriever != null ? !this.iconRetriever.equals(that.iconRetriever)
                : that.iconRetriever != null)
                return false;
            // TODO: need a mechanism to equate the retriever parameters.
            //if (this.params != null ? !this.params.equals(that.params) : that.params != null)
            //    return false;
            //noinspection RedundantIfStatement
            if (this.symbolId != null ? !this.symbolId.equals(that.symbolId) : that.symbolId != null)
                return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = this.symbolId != null ? this.symbolId.hashCode() : 0;
            result = 31 * result + (this.iconRetriever != null ? this.iconRetriever.hashCode() : 0);
            // TODO: need a mechanism to equate the retriever parameters.
            //result = 31 * result + (this.params != null ? this.params.hashCode() : 0);
            return result;
        }

        @Override
        public String toString()
        {
            return this.symbolId;
        }
    }

    protected static class ModifierSource
    {
        protected String modifierKey;
        protected Object modifierValue;
        protected ModifierRetriever modifierRetriever;

        public ModifierSource(String modifierKey, Object modifierValue, ModifierRetriever modifierRetriever)
        {
            this.modifierKey = modifierKey;
            this.modifierValue = modifierValue;
            this.modifierRetriever = modifierRetriever;
        }

        public String getModifierKey()
        {
            return this.modifierKey;
        }

        public Object getModifierValue()
        {
            return this.modifierValue;
        }

        public ModifierRetriever getModifierRetriever()
        {
            return this.modifierRetriever;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || this.getClass() != o.getClass())
                return false;

            ModifierSource that = (ModifierSource) o;

            if (this.modifierKey != null ? !this.modifierKey.equals(that.modifierKey) : that.modifierKey != null)
                return false;
            if (this.modifierValue != null ? !this.modifierValue.equals(that.modifierValue)
                : that.modifierValue != null)
                return false;
            //noinspection RedundantIfStatement
            if (this.modifierRetriever != null ? !this.modifierRetriever.equals(that.modifierRetriever)
                : that.modifierRetriever != null)
                return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = this.modifierKey != null ? this.modifierKey.hashCode() : 0;
            result = 31 * result + (this.modifierValue != null ? this.modifierValue.hashCode() : 0);
            result = 31 * result + (this.modifierRetriever != null ? this.modifierRetriever.hashCode() : 0);
            return result;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(this.modifierKey);

            if (this.modifierValue != null)
                sb.append("=").append(this.modifierValue);

            return sb.toString();
        }
    }

    // Use an IconKey as the texture's image source. The image source is what defines the contents of this texture,
    // and is used as an address for the texture's contents in the cache.
    protected static class IconTexture extends LazilyLoadedTexture
    {
        public Rectangle frameRect;

        public IconTexture(IconSource imageSource)
        {
            super(imageSource);
        }

        public IconTexture(IconSource imageSource, boolean useMipMaps)
        {
            super(imageSource, useMipMaps);
        }

        protected boolean loadTextureData()
        {
            TextureData td = this.createIconTextureData();

            if (td != null)
                this.setTextureData(td);

            return td != null;
        }

        protected TextureData createIconTextureData()
        {
            try
            {
                IconSource iconSource = (IconSource) this.getImageSource();
                BufferedImage image = iconSource.getIconRetriever().createIcon(iconSource.getSymbolId(),
                    iconSource.getParams());

                if (image == null)
                {
                    // IconRetriever returns null if the symbol identifier is not recognized, or if the parameter list
                    // specified an empty icon. In either case, we mark the texture initialization as having failed to
                    // suppress  any further requests.
                    this.textureInitializationFailed = true;
                    return null;
                }

                return TextureIO.newTextureData(image, this.isUseMipMaps());
            }
            catch (Exception e)
            {
                String msg = Logging.getMessage("Symbology.ExceptionRetrievingTacticalIcon", this.getImageSource());
                Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
                this.textureInitializationFailed = true; // Suppress subsequent requests for this tactical icon.
                return null;
            }
        }

        @Override
        protected Runnable createRequestTask()
        {
            return new IconRequestTask(this);
        }

        protected static class IconRequestTask implements Runnable
        {
            protected final IconTexture texture;

            protected IconRequestTask(IconTexture texture)
            {
                if (texture == null)
                {
                    String message = Logging.getMessage("nullValue.TextureIsNull");
                    Logging.logger().severe(message);
                    throw new IllegalArgumentException(message);
                }

                this.texture = texture;
            }

            public void run()
            {
                if (Thread.currentThread().isInterrupted())
                    return; // the task was cancelled because it's a duplicate or for some other reason

                if (this.texture.loadTextureData())
                    this.texture.notifyTextureLoaded();
            }

            public boolean equals(Object o)
            {
                if (this == o)
                    return true;
                if (o == null || getClass() != o.getClass())
                    return false;

                final IconRequestTask that = (IconRequestTask) o;
                return this.texture != null ? this.texture.equals(that.texture) : that.texture == null;
            }

            public int hashCode()
            {
                return (this.texture != null ? this.texture.hashCode() : 0);
            }

            public String toString()
            {
                return this.texture.getImageSource().toString();
            }
        }
    }

    protected static class ModifierGlyph extends TextureAtlasElement
    {
        public ModifierGlyph(TextureAtlas atlas, ModifierSource modifierSource)
        {
            super(atlas, modifierSource);
        }

        @Override
        protected boolean loadImage()
        {
            BufferedImage image = this.createModifierImage();

            if (image != null)
                this.setImage(image);

            return image != null;
        }

        protected BufferedImage createModifierImage()
        {
            try
            {
                ModifierSource modifierSource = (ModifierSource) this.getImageSource();
                BufferedImage image = modifierSource.getModifierRetriever().createModifier(
                    modifierSource.getModifierKey(), modifierSource.getModifierValue());

                if (image == null)
                {
                    // ModifierRetriever returns null if the modifier or its value is not recognized. In either case, we
                    // mark the image initialization as having failed to suppress any further requests.
                    this.imageInitializationFailed = true;
                    return null;
                }

                return image;
            }
            catch (Exception e)
            {
                String msg = Logging.getMessage("Symbology.ExceptionRetrievingGraphicModifier", this.getImageSource());
                Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
                this.imageInitializationFailed = true; // Suppress subsequent requests for this modifier.
                return null;
            }
        }
    }

    protected static interface ModifierRetriever
    {
        BufferedImage createModifier(String modifier, Object value);
    }

    protected static final double DEFAULT_OPACITY = 1d;
    protected static final double DEFAULT_SCALE = 1d;
    protected static final Font DEFAULT_TEXT_MODIFIER_FONT = Font.decode("Arial-PLAIN-12");
    protected static final Material DEFAULT_TEXT_MODIFIER_MATERIAL = Material.WHITE;
    /** Configured to match the depth offset produced by existing screen elements such as PointPlacemark. */
    protected static final double DEFAULT_DEPTH_OFFSET = -8200;

    /** The attributes used if attributes are not specified. */
    protected static TacticalSymbolAttributes defaultAttrs;

    static
    {
        // Create and populate the default attributes.
        defaultAttrs = new BasicTacticalSymbolAttributes();
        defaultAttrs.setOpacity(DEFAULT_OPACITY);
        defaultAttrs.setScale(DEFAULT_SCALE);
        defaultAttrs.setTextModifierFont(DEFAULT_TEXT_MODIFIER_FONT);
        defaultAttrs.setTextModifierMaterial(DEFAULT_TEXT_MODIFIER_MATERIAL);
    }

    /**
     * Indicates whether this symbol is drawn when in view. true if this symbol is drawn when in view, otherwise false.
     * Initially true.
     */
    protected boolean visible = true;
    /**
     * Indicates whether this symbol is highlighted. true if this symbol is highlighted, otherwise false. Initially
     * false.
     */
    protected boolean highlighted;
    /**
     * Indicates this symbol's geographic position. See {@link #setPosition(gov.nasa.worldwind.geom.Position)} for a
     * description of how tactical symbols interpret their position. Must be non-null, and is initialized during
     * construction.
     */
    protected Position position;
    /**
     * Indicates this symbol's altitude mode. See {@link #setAltitudeMode(int)} for a description of the valid altitude
     * modes. Initially Worldwind.ABSOLUTE.
     */
    protected int altitudeMode = WorldWind.ABSOLUTE;
    /**
     * Indicates whether this symbol draws its supplemental graphic modifiers. true if this symbol draws its graphic
     * modifiers, otherwise false. Initially true.
     */
    protected boolean showGraphicModifiers = true;
    /**
     * Indicates whether this symbol draws its supplemental text modifiers. true if this symbol draws its text
     * modifiers, otherwise false. Initially true.
     */
    protected boolean showTextModifiers = true;
    protected boolean enableBatchRendering = true;
    protected boolean enableBatchPicking = true;
    /**
     * Indicates the current text and graphic modifiers assigned to this symbol. This list of key-value pairs contains
     * both the modifiers specified by the string identifier during construction, and those specified by calling {@link
     * #setModifier(String, Object)}. Initialized to a new AVListImpl, and populated during construction from values in
     * the string identifier and the modifiers list.
     */
    protected AVList modifiers = new AVListImpl();
    /**
     * Indicates this symbol's normal (as opposed to highlight) attributes. May be <code>null</code>, indicating that
     * the default attributes are used. Initially <code>null</code>.
     */
    protected TacticalSymbolAttributes normalAttrs;
    /**
     * Indicates this symbol's highlight attributes. May be <code>null</code>, indicating that the default attributes
     * are used. Initially <code>null</code>.
     */
    protected TacticalSymbolAttributes highlightAttrs;
    /**
     * Indicates this symbol's currently active attributes. Updated in {@link #determineActiveAttributes}. Initialized
     * to a new BasicTacticalSymbolAttributes.
     */
    protected TacticalSymbolAttributes activeAttrs = new BasicTacticalSymbolAttributes();
    protected Double depthOffset;
    protected IconRetriever iconRetriever;
    protected ModifierRetriever modifierRetriever;

    /**
     * The frame used to calculate this symbol's per-frame values. Set to the draw context's frame number each frame.
     * Initially -1.
     */
    protected long frameNumber = -1;
    /**
     * Per-frame Cartesian point corresponding to this symbol's position. Calculated each frame in {@link
     * #computeSymbolPoints(gov.nasa.worldwind.render.DrawContext)}. Initially <code>null</code>.
     */
    protected Vec4 placePoint;
    /**
     * Per-frame screen point corresponding to the projection of the placePoint in the viewport (on the screen).
     * Calculated each frame in {@link #computeSymbolPoints(gov.nasa.worldwind.render.DrawContext)}. Initially
     * <code>null</code>.
     */
    protected Vec4 screenPoint;
    /**
     * Per-frame distance corresponding to the distance between the placePoint and the View's eye point. Used to order
     * the symbol as an ordered renderable, and is returned by getDistanceFromEye. Calculated each frame in {@link
     * #computeSymbolPoints(gov.nasa.worldwind.render.DrawContext)}. Initially 0.
     */
    protected double eyeDistance;
    /**
     * Per-frame screen offset indicating this symbol's x-offset relative to the screenPoint. Calculated each frame in
     * {@link #layout(gov.nasa.worldwind.render.DrawContext)}. Initially 0.
     */
    protected double dx;
    /**
     * Per-frame screen offset indicating this symbol's y-offset relative to the screenPoint. Calculated each frame in
     * {@link #layout(gov.nasa.worldwind.render.DrawContext)}. Initially 0.
     */
    protected double dy;
    protected Rectangle screenRect;
    protected Rectangle screenBounds;

    protected IconTexture iconTexture;
    protected Map<Object, ModifierGlyph> modifierGlyphMap = new HashMap<Object, ModifierGlyph>();
    protected TextureAtlas modifierAtlas;
    protected FloatBuffer iconVertices;
    protected FloatBuffer modifierVertices;

    /**
     * Support for setting up and restoring OpenGL state during rendering. Initialized to a new OGLStackHandler, and
     * used in {@link #beginDrawing(gov.nasa.worldwind.render.DrawContext, int)} and {@link
     * #endDrawing(gov.nasa.worldwind.render.DrawContext)}.
     */
    protected OGLStackHandler BEogsh = new OGLStackHandler();
    /**
     * Support for setting up and restoring picking state, and resolving the picked object. Initialized to a new
     * PickSupport, and used in {@link #pick(gov.nasa.worldwind.render.DrawContext, java.awt.Point)}.
     */
    protected PickSupport pickSupport = new PickSupport();
    /**
     * Per-frame layer indicating this symbol's layer when its ordered renderable was created. Assigned each frame in
     * {@link #makeOrderedRenderable(gov.nasa.worldwind.render.DrawContext)}. Used to define the picked object's layer
     * during pick resolution. Initially <code>null</code>.
     */
    protected Layer pickLayer;

    /**
     * Constructs a new symbol with the specified position. The position specifies the latitude, longitude, and altitude
     * where this symbol is drawn on the globe. The position's altitude component is interpreted according to the
     * altitudeMode.
     *
     * @param position the latitude, longitude, and altitude where the symbol is drawn.
     *
     * @throws IllegalArgumentException if the position is <code>null</code>.
     */
    protected AbstractTacticalSymbol(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.position = position;
    }

    /** {@inheritDoc} */
    public boolean isVisible()
    {
        return this.visible;
    }

    /** {@inheritDoc} */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /** {@inheritDoc} */
    public boolean isHighlighted()
    {
        return this.highlighted;
    }

    /** {@inheritDoc} */
    public void setHighlighted(boolean highlighted)
    {
        this.highlighted = highlighted;
    }

    /** {@inheritDoc} */
    public Position getPosition()
    {
        return this.position;
    }

    /** {@inheritDoc} */
    public void setPosition(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.position = position;
    }

    /** {@inheritDoc} */
    public int getAltitudeMode()
    {
        return this.altitudeMode;
    }

    /** {@inheritDoc} */
    public void setAltitudeMode(int altitudeMode)
    {
        this.altitudeMode = altitudeMode;
    }

    /** {@inheritDoc} */
    public boolean isShowGraphicModifiers()
    {
        return this.showGraphicModifiers;
    }

    /** {@inheritDoc} */
    public void setShowGraphicModifiers(boolean showGraphicModifiers)
    {
        if (this.showGraphicModifiers == showGraphicModifiers)
            return;

        this.showGraphicModifiers = showGraphicModifiers;
    }

    /** {@inheritDoc} */
    public boolean isShowTextModifiers()
    {
        return this.showTextModifiers;
    }

    /** {@inheritDoc} */
    public void setShowTextModifiers(boolean showTextModifiers)
    {
        if (this.showTextModifiers == showTextModifiers)
            return;

        this.showTextModifiers = showTextModifiers;
    }

    public boolean isEnableBatchRendering()
    {
        return this.enableBatchRendering;
    }

    public void setEnableBatchRendering(boolean enableBatchRendering)
    {
        this.enableBatchRendering = enableBatchRendering;
    }

    public boolean isEnableBatchPicking()
    {
        return this.enableBatchPicking;
    }

    public void setEnableBatchPicking(boolean enableBatchPicking)
    {
        this.enableBatchPicking = enableBatchPicking;
    }

    /** {@inheritDoc} */
    public Object getModifier(String modifier)
    {
        if (modifier == null)
        {
            String msg = Logging.getMessage("nullValue.ModifierIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.modifiers.getValue(modifier);
    }

    /** {@inheritDoc} */
    public void setModifier(String modifier, Object value)
    {
        if (modifier == null)
        {
            String msg = Logging.getMessage("nullValue.ModifierIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.modifiers.hasKey(modifier) && this.modifiers.getValue(modifier) == value)
            return;

        this.modifiers.setValue(modifier, value);

        // Note that we co not explicitly remove this glyphs from the atlas. Texture atlases are designed to
        // automatically evict the oldest entries once they're full. Leaving this glyph in the atlas does not incur
        // any additional overhead, and has the benefit of ensuring that we do not remove glyphs used by another symbol.
        this.modifierGlyphMap.remove(modifier);
    }

    /** {@inheritDoc} */
    public TacticalSymbolAttributes getAttributes()
    {
        return this.normalAttrs;
    }

    /** {@inheritDoc} */
    public void setAttributes(TacticalSymbolAttributes normalAttrs)
    {
        this.normalAttrs = normalAttrs; // Null is accepted, and indicates the default attributes are used.
    }

    /** {@inheritDoc} */
    public TacticalSymbolAttributes getHighlightAttributes()
    {
        return this.highlightAttrs;
    }

    /** {@inheritDoc} */
    public void setHighlightAttributes(TacticalSymbolAttributes highlightAttrs)
    {
        this.highlightAttrs = highlightAttrs; // Null is accepted, and indicates the default highlight attributes.
    }

    public Double getDepthOffset()
    {
        return this.depthOffset;
    }

    public void setDepthOffset(Double depthOffset)
    {
        this.depthOffset = depthOffset; // Null is accepted, and indicates the default depth offset is used.
    }

    public IconRetriever getIconRetriever()
    {
        return this.iconRetriever;
    }

    protected void setIconRetriever(IconRetriever iconRetriever)
    {
        this.iconRetriever = iconRetriever;
    }

    protected ModifierRetriever getModifierRetriever()
    {
        return this.modifierRetriever;
    }

    protected void setModifierRetriever(ModifierRetriever modifierRetriever)
    {
        this.modifierRetriever = modifierRetriever;
    }

    protected TextureAtlas getModifierAtlas()
    {
        return this.modifierAtlas;
    }

    protected void setModifierAtlas(TextureAtlas atlas)
    {
        // Note that we do not explicitly remove this symbol's glyphs from the old atlas. The modifier texture atlas
        // should be  configured to evict the oldest glyphs when the atlas is full. Leaving this symbol's glyphs in the
        // atlas does not incur any additional overhead, and has the benefit of ensuring that we do not remove glyphs
        // used by another symbol.

        this.modifierAtlas = atlas;
    }

    /** {@inheritDoc} */
    public double getDistanceFromEye()
    {
        return this.eyeDistance;
    }

    /** {@inheritDoc} */
    public void pick(DrawContext dc, Point pickPoint)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.pickSupport.clearPickList();
        try
        {
            this.pickSupport.beginPicking(dc);
            this.render(dc);
        }
        finally
        {
            this.pickSupport.endPicking(dc);
            this.pickSupport.resolvePick(dc, pickPoint, this.pickLayer);
        }
    }

    /** {@inheritDoc} */
    public void render(DrawContext dc)
    {
        // This render method is called three times during frame generation. It's first called as a {@link Renderable}
        // during <code>Renderable</code> picking. It's called again during normal rendering. And it's called a third
        // time as an OrderedRenderable. The first two calls determine whether to add the placemark  and its optional
        // line to the ordered renderable list during pick and render. The third call just draws the ordered renderable.

        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!this.isVisible())
            return;

        if (dc.isOrderedRenderingMode())
            this.drawOrderedRenderable(dc);
        else
            this.makeOrderedRenderable(dc);
    }

    protected void makeOrderedRenderable(DrawContext dc)
    {
        // Calculate this symbol's per-frame values, Re-using values already calculated this frame. This step computes
        // the symbol's Cartesian and screen coordinate points, its screen bounding rectangle, and the currently active
        // attributes. The symbol's layout and offset are computed in drawOrderedRenderable because the layout requires
        // that the symbol's textures be loaded. By delaying their computation until drawOrderedRenderable we ensure
        // that the textures for offscreen symbols are not loaded before the symbol is visible.
        if (dc.getFrameTimeStamp() != this.frameNumber)
        {
            this.computeSymbolPoints(dc);
            if (this.placePoint == null || this.screenPoint == null)
                return;

            this.determineActiveAttributes();
            if (this.getActiveAttributes() == null)
                return;

            this.frameNumber = dc.getFrameTimeStamp();
        }

        // Don't draw if beyond the horizon.
        double horizon = dc.getView().getHorizonDistance();
        if (this.eyeDistance > horizon)
            return;

        if (this.intersectsFrustum(dc))
            dc.addOrderedRenderable(this);

        if (dc.isPickingMode())
            this.pickLayer = dc.getCurrentLayer();
    }

    protected void computeSymbolPoints(DrawContext dc)
    {
        this.placePoint = null;
        this.screenPoint = null;
        this.screenBounds = null;

        Position pos = this.getPosition();
        if (pos == null)
            return;

        if (this.altitudeMode == WorldWind.CLAMP_TO_GROUND)
        {
            this.placePoint = dc.computeTerrainPoint(pos.getLatitude(), pos.getLongitude(), 0);
        }
        else if (this.altitudeMode == WorldWind.RELATIVE_TO_GROUND)
        {
            this.placePoint = dc.computeTerrainPoint(pos.getLatitude(), pos.getLongitude(), pos.getAltitude());
        }
        else // Default to ABSOLUTE
        {
            double height = pos.getElevation() * dc.getVerticalExaggeration();
            this.placePoint = dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(), height);
        }

        if (this.placePoint == null)
            return;

        // Compute the symbol''s screen location.
        this.screenPoint = dc.getView().project(this.placePoint);
        this.eyeDistance = this.placePoint.distanceTo3(dc.getView().getEyePoint());

        if (this.screenRect != null)
        {
            double s = this.getActiveAttributes().getScale() != null ? this.getActiveAttributes().getScale()
                : DEFAULT_SCALE;
            double width = s * this.screenRect.getWidth();
            double height = s * this.screenRect.getHeight();
            double x = this.screenPoint.x + s * (this.dx + this.screenRect.getX());
            double y = this.screenPoint.y + s * (this.dy + this.screenRect.getY());
            this.screenBounds = new Rectangle((int) x, (int) y, (int) Math.ceil(width), (int) Math.ceil(height));
        }
    }

    protected void determineActiveAttributes()
    {
        if (this.isHighlighted())
        {
            if (this.getHighlightAttributes() != null)
                this.activeAttrs.copy(this.getHighlightAttributes());
            else
            {
                // If no highlight attributes have been specified we need to use either the normal or default attributes
                // but adjust them to cause highlighting.
                if (this.getAttributes() != null)
                    this.activeAttrs.copy(this.getAttributes());
                else
                    this.activeAttrs.copy(defaultAttrs);
            }
        }
        else if (this.getAttributes() != null)
        {
            this.activeAttrs.copy(this.getAttributes());
        }
        else
        {
            this.activeAttrs.copy(defaultAttrs);
        }
    }

    protected TacticalSymbolAttributes getActiveAttributes()
    {
        return this.activeAttrs;
    }

    protected boolean intersectsFrustum(DrawContext dc)
    {
        View view = dc.getView();

        // Test the placemark's model coordinate point against the near and far clipping planes.
        if (this.placePoint != null
            && (view.getFrustumInModelCoordinates().getNear().distanceTo(this.placePoint) < 0
            || view.getFrustumInModelCoordinates().getFar().distanceTo(this.placePoint) < 0))
        {
            return false;
        }

        // TODO: screen bounds are one frame behind the layout
        if (this.screenBounds != null)
        {
            if (dc.isPickingMode())
                return dc.getPickFrustums().intersectsAny(this.screenBounds);
            else
                return view.getViewport().intersects(this.screenBounds);
        }

        return true;
    }

    protected void drawOrderedRenderable(DrawContext dc)
    {
        this.beginDrawing(dc, 0);
        try
        {
            this.doDrawOrderedRenderable(dc, this.pickSupport);

            if (this.isEnableBatchRendering())
                this.drawBatched(dc);
        }
        finally
        {
            this.endDrawing(dc);
        }
    }

    protected void drawBatched(DrawContext dc)
    {
        // Draw as many as we can in a batch to save ogl state switching.
        Object nextItem = dc.peekOrderedRenderables();

        if (!dc.isPickingMode())
        {
            while (nextItem != null && nextItem instanceof AbstractTacticalSymbol)
            {
                AbstractTacticalSymbol ts = (AbstractTacticalSymbol) nextItem;
                if (!ts.isEnableBatchRendering())
                    break;

                dc.pollOrderedRenderables(); // take it off the queue
                ts.doDrawOrderedRenderable(dc, this.pickSupport);

                nextItem = dc.peekOrderedRenderables();
            }
        }
        else if (this.isEnableBatchPicking())
        {
            while (nextItem != null && nextItem instanceof AbstractTacticalSymbol)
            {
                AbstractTacticalSymbol ts = (AbstractTacticalSymbol) nextItem;
                if (!ts.isEnableBatchRendering() || !ts.isEnableBatchPicking())
                    break;

                if (ts.pickLayer != this.pickLayer) // batch pick only within a single layer
                    break;

                dc.pollOrderedRenderables(); // take it off the queue
                ts.doDrawOrderedRenderable(dc, this.pickSupport);

                nextItem = dc.peekOrderedRenderables();
            }
        }
    }

    protected void beginDrawing(DrawContext dc, int attrMask)
    {
        GL gl = dc.getGL();

        attrMask |= GL.GL_DEPTH_BUFFER_BIT // for depth test enable, depth func, depth mask
            | GL.GL_COLOR_BUFFER_BIT // for alpha test enable, alpha func, blend enable, blend func
            | GL.GL_CURRENT_BIT // for current color
            | GL.GL_LINE_BIT; // for line smooth enable and line width

        this.BEogsh.clear(); // Reset the stack handler's internal state.
        this.BEogsh.pushAttrib(gl, attrMask);
        // TODO: comment on why this works
        this.BEogsh.pushProjectionIdentity(gl);
        gl.glOrtho(0d, dc.getDrawableWidth(), 0d, dc.getDrawableHeight(), 0d, -1d);
        this.BEogsh.pushModelviewIdentity(gl);

        // Enable OpenGL vertex arrays for all symbols by default. All tactical symbol drawing code specifies its data
        // to OpenGL using vertex arrays.
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

        // Enable the alpha test to suppress any fully transparent image pixels. We do this for both normal rendering
        // and picking because it eliminates fully transparent texture data from contributing to the pick frame.
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0f);

        // Apply the depth buffer but don't change it (for screen-space symbols).
        if (!dc.isDeepPickingEnabled())
            gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glDepthMask(false);

        // Enable OpenGL texturing for all symbols by default. The most common case is to render a series of textured
        // quads representing symbol icons and modifiers.
        gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(0, (float) DEFAULT_DEPTH_OFFSET);

        // Enable OpenGL texturing for all symbols by default. The most common case is to render a series of textured
        // quads representing symbol icons and modifiers.
        gl.glEnable(GL.GL_TEXTURE_2D);

        if (dc.isPickingMode())
        {
            // Set up to replace the non-transparent texture colors with the single pick color.
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE);
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, GL.GL_PREVIOUS);
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, GL.GL_REPLACE);

            // Give symbol modifier lines a thicker width during picking in order to make them easier to select.
            gl.glLineWidth(9f); // TODO: make this configurable
        }
        else
        {
            // Enable blending for RGB colors which have been premultiplied by their alpha component. We use this mode
            // because the icon texture and modifier textures RGB color components have been premultiplied by their color
            // component.
            gl.glEnable(GL.GL_BLEND);
            OGLUtil.applyBlending(gl, true);

            // Give symbol modifier lines a 3 pixel wide anti-aliased appearance. This GL state does not affect the
            // symbol icon and symbol modifiers drawn with textures.
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glLineWidth(3f); // TODO: make this configurable
        }
    }

    protected void endDrawing(DrawContext dc)
    {
        GL gl = dc.getGL();

        // Restore the default OpenGL vertex array state.
        gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);

        // Restore the default OpenGL polygon offset state.
        gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(0f, 0f);

        // Restore the default OpenGL texture state.
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

        if (dc.isPickingMode())
        {
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, OGLUtil.DEFAULT_TEX_ENV_MODE);
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, OGLUtil.DEFAULT_SRC0_RGB);
            gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, OGLUtil.DEFAULT_COMBINE_RGB);
        }

        this.BEogsh.pop(gl);
    }

    protected void doDrawOrderedRenderable(DrawContext dc, PickSupport pickCandidates)
    {
        GL gl = dc.getGL();

        // Compute the symbol frame and modifier layout. This step determines the symbol's frame size and modifier
        // layout relative to that frame. Since the offset usually depends on the frame size and modifier layout, this
        // must be done prior to computing the offset.
        this.layout(dc);

        // Compute the layout for the symbol's dynamic modifiers. This step must be performed every frame.
        this.layoutDynamicModifiers(dc);

        this.computeScreenRect(dc);

        // Compute the symbol's offset using the symbol frame size and modifier information computed during layout. This
        // step computes the symbol's dx and dy offsets, and therefore must be done prior to using dx and dy.
        this.computeOffset(dc);

        Double depthOffsetUnits = this.getDepthOffset();
        try
        {
            // Apply any custom depth offset specified by the caller. This overrides the default depth offset specified
            // in beginRendering, and is therefore restored in the finally block below.
            if (depthOffsetUnits != null)
                gl.glPolygonOffset(0f, depthOffsetUnits.floatValue());

            if (dc.isPickingMode())
            {
                Color pickColor = dc.getUniquePickColor();
                pickCandidates.addPickableObject(this.createPickedObject(pickColor.getRGB()));
                gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
            }
            else
            {
                // Set the current color to white with the symbol's current opacity. This applies the symbol's opacity
                // to its icon texture and graphic modifier textures by multiplying texture fragment colors by the
                // opacity. We premultiply the white RGB color components by the alpha since the texture's RGB color
                // components have also been premultiplied by their color component.
                float a = this.getActiveAttributes().getOpacity() != null
                    ? this.getActiveAttributes().getOpacity().floatValue() : (float) DEFAULT_OPACITY;
                gl.glColor4f(a, a, a, a);
            }

            // Apply the symbol's offset in screen coordinates. We translate the X and Y coordinates so that the
            // symbol's hot spot (identified by its offset) is aligned with its screen point. We translate the Z
            // coordinate so that the symbol's depth values are appropriately computed by OpenGL according to its
            // distance from the eye. The orthographic projection matrix configured in beginRendering correctly maps
            // the screen point's Z coordinate to its corresponding depth value.
            Double scale = this.getActiveAttributes().getScale() != null ? this.getActiveAttributes().getScale()
                : DEFAULT_SCALE;
            gl.glLoadIdentity(); // Assumes that the current matrix mode is GL_MODELVIEW.
            gl.glTranslated(this.screenPoint.x, this.screenPoint.y, this.screenPoint.z);
            gl.glScaled(scale.floatValue(), scale.floatValue(), 1d);
            gl.glTranslated(this.dx, this.dy, 0d);

            this.draw(dc);
        }
        finally
        {
            // If the caller specified a custom depth offset, we restore the default depth offset to the value specified
            // in beginRendering.
            if (depthOffsetUnits != null)
                gl.glPolygonOffset(0f, (float) DEFAULT_DEPTH_OFFSET);
        }
    }

    protected void layout(DrawContext dc)
    {
        if (this.mustDrawIcon(dc))
            this.layoutIcon(dc);
    }

    protected void layoutDynamicModifiers(DrawContext dc)
    {
        // Intentionally left blank. Subclasses override this method to perform layout on dynamic modifiers, which must
        // be updated every frame.
    }

    protected void layoutIcon(DrawContext dc)
    {
        if (this.iconTexture == null)
            this.iconTexture = this.createIconTexture(); // Lazily create the symbol icon texture.

        // We must know the symbol icon's width and height in order to determine its screen point coordinates and local
        // frame rectangle. Bind the texture in order to making the width and height available by loading the texture.
        if (this.iconTexture == null || !this.iconTexture.bind(dc))
            return;

        int w = this.iconTexture.getWidth(dc);
        int h = this.iconTexture.getHeight(dc);

        // Compute the symbol icon's frame rectangle in local coordinates. This is used by the modifier layout to
        // determine where to place modifier graphics and modifier text.
        Rectangle rect = this.computeIconFrameRect(w, h);
        if (rect != null)
            this.iconTexture.frameRect = rect;

        // Compute the symbol icon's vertex points in local coordinates. This buffer is used during icon drawing to
        // define the icon's location in screen coordinates.
        if (this.iconVertices != null)
            this.iconVertices.clear();
        this.iconVertices = this.addRectVertices(this.iconVertices, 0, 0, w, h, this.iconTexture.getTexCoords());
        this.iconVertices.flip(); // Set the limit to the current position.
    }

    protected IconTexture createIconTexture()
    {
        if (this.getIconRetriever() != null)
        {
            IconSource source = new IconSource(this.getIdentifier(), this.getIconRetriever(), this.modifiers);
            return new IconTexture(source);
        }

        return null;
    }

    protected Rectangle computeIconFrameRect(int imageWidth, int imageHeight)
    {
        return new Rectangle(0, 0, imageWidth, imageHeight);
    }

    protected void computeScreenRect(DrawContext dc)
    {
        this.screenRect = null;

        if (this.mustDrawIcon(dc) && this.iconTexture != null && this.iconTexture.frameRect != null)
        {
            this.screenRect = new Rectangle(this.iconTexture.frameRect);
        }

        if (this.mustDrawGraphicModifiers(dc) && this.modifierVertices != null)
        {
            this.addVerticesToScreenRect(this.modifierVertices, 4);
        }
    }

    protected void computeOffset(DrawContext dc)
    {
        if (this.iconTexture != null && this.iconTexture.frameRect != null && this.screenRect != null
            && this.getAltitudeMode() == WorldWind.CLAMP_TO_GROUND)
        {
            this.dy = -this.screenRect.getMinY();
            this.dx = -this.iconTexture.frameRect.getCenterX();
        }
        else if (this.iconTexture != null && this.iconTexture.frameRect != null)
        {
            this.dy = -this.iconTexture.frameRect.getCenterY();
            this.dx = -this.iconTexture.frameRect.getCenterX();
        }
        else
        {
            this.dx = 0;
            this.dy = 0;
        }
    }

    protected void draw(DrawContext dc)
    {
        if (this.mustDrawIcon(dc))
            this.drawIcon(dc);

        if (this.mustDrawGraphicModifiers(dc))
            this.drawGraphicModifiers(dc);
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected boolean mustDrawIcon(DrawContext dc)
    {
        return true;
    }

    protected void drawIcon(DrawContext dc)
    {
        if (this.iconVertices != null && this.iconVertices.remaining() >= 16
            && this.iconTexture != null && this.iconTexture.bind(dc))
        {
            this.drawRects(dc, this.iconVertices);
        }
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected boolean mustDrawGraphicModifiers(DrawContext dc)
    {
        return this.isShowGraphicModifiers();
    }

    protected void drawGraphicModifiers(DrawContext dc)
    {
        if (this.modifierVertices != null && this.modifierVertices.remaining() >= 16
            && this.modifierAtlas != null && this.modifierAtlas.bind(dc))
        {
            this.drawRects(dc, this.modifierVertices);
        }
    }

    protected void drawRects(DrawContext dc, FloatBuffer verts)
    {
        if (verts.remaining() < 16)
            return;

        GL gl = dc.getGL();

        gl.glVertexPointer(2, GL.GL_FLOAT, 16, verts);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 16, verts.position(2));
        verts.rewind(); // Restore the position to 0 after changing it to specify the tex coord pointer.

        gl.glDrawArrays(GL.GL_QUADS, 0, verts.remaining() / 4);
    }

    protected FloatBuffer addRectVertices(FloatBuffer verts, float x, float y, float w, float h,
        TextureCoords texCoords)
    {
        if (verts == null)
            verts = BufferUtil.newFloatBuffer(16);
        else if (verts.remaining() < 16)
        {
            FloatBuffer newBuffer = BufferUtil.newFloatBuffer(verts.capacity() + 16);
            verts.flip(); // Flip the vertex buffer so the elements between 0 and its current position are copied.
            newBuffer.put(verts); // Copy the current data into the new buffer, and advances new buffer's position.
            verts = newBuffer;
        }

        verts.put(x).put(y);
        verts.put(texCoords.left()).put(texCoords.bottom());
        verts.put(x + w).put(y);
        verts.put(texCoords.right()).put(texCoords.bottom());
        verts.put(x + w).put(y + h);
        verts.put(texCoords.right()).put(texCoords.top());
        verts.put(x).put(y + h);
        verts.put(texCoords.left()).put(texCoords.top());

        return verts;
    }

    protected void addVerticesToScreenRect(FloatBuffer verts, int stride)
    {
        for (int i = verts.position(); i < verts.remaining(); i += stride)
        {
            int x = (int) verts.get(i);
            int y = (int) verts.get(i + 1);

            if (this.screenRect == null)
                this.screenRect = new Rectangle(x, y, 0, 0);
            else
                this.screenRect.add(x, y);
        }
    }

    protected PickedObject createPickedObject(int colorCode)
    {
        return new PickedObject(colorCode, this);
    }
}
