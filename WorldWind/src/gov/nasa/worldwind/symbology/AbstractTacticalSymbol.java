/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import com.sun.opengl.util.j2d.TextRenderer;
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
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

/**
 * @author dcollins
 * @version $Id$
 */
public abstract class AbstractTacticalSymbol extends WWObjectImpl implements TacticalSymbol, OrderedRenderable
{
    protected static class IconSource
    {
        protected IconRetriever retriever;
        protected String symbolId;
        protected AVList retrieverParams;

        public IconSource(IconRetriever retriever, String symbolId, AVList retrieverParams)
        {
            this.retriever = retriever;
            this.symbolId = symbolId;

            if (retrieverParams != null)
            {
                // If the specified parameters are non-null, then store a copy of the parameters in this key's params
                // property to insulate it from changes made by the caller. This params list must not change after
                // construction this key's properties must be immutable.
                this.retrieverParams = new AVListImpl();
                this.retrieverParams.setValues(retrieverParams);
            }
        }

        public IconRetriever getRetriever()
        {
            return this.retriever;
        }

        public String getSymbolId()
        {
            return this.symbolId;
        }

        public AVList getRetrieverParams()
        {
            return this.retrieverParams;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            IconSource that = (IconSource) o;

            if (this.retriever != null ? !this.retriever.equals(that.retriever)
                : that.retriever != null)
                return false;
            if (this.symbolId != null ? !this.symbolId.equals(that.symbolId) : that.symbolId != null)
                return false;

            if (this.retrieverParams != null && that.retrieverParams != null)
            {
                Set<Map.Entry<String, Object>> theseEntries = this.retrieverParams.getEntries();
                Set<Map.Entry<String, Object>> thoseEntries = that.retrieverParams.getEntries();

                return theseEntries.equals(thoseEntries);
            }
            return (this.retrieverParams == null && that.retrieverParams == null);
        }

        @Override
        public int hashCode()
        {
            int result = this.retriever != null ? this.retriever.hashCode() : 0;
            result = 31 * result + (this.symbolId != null ? this.symbolId.hashCode() : 0);
            result = 31 * result + (this.retrieverParams != null ? this.retrieverParams.getEntries().hashCode() : 0);
            return result;
        }

        @Override
        public String toString()
        {
            return this.symbolId;
        }
    }

    // Use an IconKey as the texture's image source. The image source is what defines the contents of this texture,
    // and is used as an address for the texture's contents in the cache.
    protected static class IconTexture extends LazilyLoadedTexture
    {
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
                IconSource source = (IconSource) this.getImageSource();
                BufferedImage image = source.getRetriever().createIcon(source.getSymbolId(),
                    source.getRetrieverParams());

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

    protected static class IconAtlasElement extends TextureAtlasElement
    {
        protected Point point;
        /** Indicates the last time, in milliseconds, the element was requested or added. */
        protected long lastUsed = System.currentTimeMillis();

        public IconAtlasElement(TextureAtlas atlas, IconSource source)
        {
            super(atlas, source);
        }

        public Point getPoint()
        {
            return this.point;
        }

        public void setPoint(Point point)
        {
            this.point = point;
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
                IconSource source = (IconSource) this.getImageSource();
                BufferedImage image = source.getRetriever().createIcon(source.getSymbolId(),
                    source.getRetrieverParams());

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

    protected static class Label
    {
        protected TextRenderer renderer;
        protected String text;
        protected Point point;
        protected Color color;

        public Label(TextRenderer renderer, String text, Point point, Color color)
        {
            if (renderer == null)
            {
                String msg = Logging.getMessage("nullValue.RendererIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            if (text == null)
            {
                String msg = Logging.getMessage("nullValue.StringIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            if (point == null)
            {
                String msg = Logging.getMessage("nullValue.PointIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            if (color == null)
            {
                String msg = Logging.getMessage("nullValue.ColorIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.renderer = renderer;
            this.text = text;
            this.point = point;
            this.color = color;
        }

        public TextRenderer getTextRenderer()
        {
            return this.renderer;
        }

        public String getText()
        {
            return this.text;
        }

        public Point getPoint()
        {
            return this.point;
        }

        public Color getColor()
        {
            return this.color;
        }
    }

    protected static class Line
    {
        protected Iterable<? extends Point2D> points;

        public Line()
        {
        }

        public Line(Iterable<? extends Point2D> points)
        {
            if (points == null)
            {
                String msg = Logging.getMessage("nullValue.IterableIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.points = points;
        }

        public Iterable<? extends Point2D> getPoints()
        {
            return points;
        }

        public void setPoints(Iterable<? extends Point2D> points)
        {
            this.points = points;
        }
    }

    protected static final String LAYOUT_ABSOLUTE = "gov.nasa.worldwind.symbology.TacticalSymbol.LayoutAbsolute";
    protected static final String LAYOUT_RELATIVE = "gov.nasa.worldwind.symbology.TacticalSymbol.LayoutRelative";
    protected static final String LAYOUT_NONE = "gov.nasa.worldwind.symbology.TacticalSymbol.LayoutNone";
    /**
     * The default depth offset in device independent depth units: -8200. This value is configured to match the depth
     * offset produced by existing screen elements such as PointPlacemark.
     */
    protected static final double DEFAULT_DEPTH_OFFSET = -8200;
    protected static final long DEFAULT_MAX_TIME_SINCE_LAST_USED = 10000;
    // TODO: make this configurable
    /**
     * The default glyph texture atlas. This texture atlas holds all glyph images loaded by calls to
     * <code>layoutGlyphModifier</code>. Initialized with initial dimensions of 128x128 and maximum dimensions of
     * 2048x2048. Configured to remove the least recently used texture elements when more space is needed.
     */
    protected static final TextureAtlas DEFAULT_GLYPH_ATLAS = new TextureAtlas(128, 128, 2048, 2048);

    /** The attributes used if attributes are not specified. */
    protected static TacticalSymbolAttributes defaultAttrs;

    static
    {
        // Create and populate the default attributes.
        defaultAttrs = new BasicTacticalSymbolAttributes();
        defaultAttrs.setOpacity(BasicTacticalSymbolAttributes.DEFAULT_OPACITY);
        defaultAttrs.setScale(BasicTacticalSymbolAttributes.DEFAULT_SCALE);
        //defaultAttrs.setTextModifierFont(BasicTacticalSymbolAttributes.DEFAULT_TEXT_MODIFIER_FONT);
        defaultAttrs.setTextModifierMaterial(BasicTacticalSymbolAttributes.DEFAULT_TEXT_MODIFIER_MATERIAL);

        // Configure the atlas to remove old texture elements that are likely no longer used to make room for new
        // modifiers when the atlas is full.
        DEFAULT_GLYPH_ATLAS.setEvictOldElements(true);
    }

    /**
     * Indicates whether this symbol is drawn when in view. <code>true</code> if this symbol is drawn when in view,
     * otherwise <code>false</code>. Initially <code>true</code>.
     */
    protected boolean visible = true;
    /**
     * Indicates whether this symbol is highlighted. <code>true</code> if this symbol is highlighted, otherwise
     * <code>false</code>. Initially <code>false</code>.
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
     * Indicates whether this symbol draws its supplemental graphic modifiers. <code>true</code> if this symbol draws
     * its graphic modifiers, otherwise <code>false</code>. Initially <code>true</code>.
     */
    protected boolean showGraphicModifiers = true;
    /**
     * Indicates whether this symbol draws its supplemental text modifiers. <code>true</code> if this symbol draws its
     * text modifiers, otherwise <code>false</code>. Initially <code>true</code>.
     */
    protected boolean showTextModifiers = true;

    /** Indicates an object to attach to the picked object list instead of this symbol. */
    protected Object delegateOwner;
    protected boolean enableBatchRendering = true;
    protected boolean enableBatchPicking = true;
    /** Indicates whether or not to display the implicit location modifier. */
    protected boolean showLocation = true;
    /** Indicates whether or not to display the implicit hostile indicator modifier. */
    protected boolean showHostileIndicator;
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
    protected Offset offset;
    protected Offset iconOffset;
    protected Size iconSize;
    protected Double depthOffset;
    protected IconRetriever iconRetriever;
    protected IconRetriever modifierRetriever;

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
     * Per-frame screen scale indicating this symbol's x-scale relative to the screen offset. Calculated each frame in
     * {@link #computeTransform(gov.nasa.worldwind.render.DrawContext)}. Initially 0.
     */
    protected double sx;
    /**
     * Per-frame screen scale indicating this symbol's y-scale relative to the screen offset. Calculated each frame in
     * {@link #computeTransform(gov.nasa.worldwind.render.DrawContext)}. Initially 0.
     */
    protected double sy;

    /**
     * Per-frame screen offset indicating this symbol's x-offset relative to the screenPoint. Calculated each frame in
     * {@link #computeTransform(gov.nasa.worldwind.render.DrawContext)}. Initially 0.
     */
    protected double dx;
    /**
     * Per-frame screen offset indicating this symbol's y-offset relative to the screenPoint. Calculated each frame in
     * {@link #computeTransform(gov.nasa.worldwind.render.DrawContext)}. Initially 0.
     */
    protected double dy;

    protected Rectangle iconRect;
    protected Rectangle layoutRect;
    protected Rectangle screenRect;

    protected List<IconAtlasElement> currentGlyphs = new ArrayList<IconAtlasElement>();
    protected List<Label> currentLabels = new ArrayList<Label>();
    protected List<Line> currentLines = new ArrayList<Line>();

    protected IconTexture iconTexture;
    protected TextureAtlas glyphAtlas;
    protected Map<String, IconAtlasElement> glyphMap = new HashMap<String, IconAtlasElement>();
    protected long maxTimeSinceLastUsed = DEFAULT_MAX_TIME_SINCE_LAST_USED;

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

    /** Constructs a new symbol with no position. */
    protected AbstractTacticalSymbol()
    {
        this.setGlyphAtlas(DEFAULT_GLYPH_ATLAS);
    }

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
        this.setGlyphAtlas(DEFAULT_GLYPH_ATLAS);
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

    /** {@inheritDoc} */
    public boolean isShowLocation()
    {
        return this.showLocation;
    }

    /** {@inheritDoc} */
    public void setShowLocation(boolean show)
    {
        this.showLocation = show;
    }

    /** {@inheritDoc} */
    public boolean isShowHostileIndicator()
    {
        return this.showHostileIndicator;
    }

    /** {@inheritDoc} */
    public void setShowHostileIndicator(boolean show)
    {
        this.showHostileIndicator = show;
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

        this.modifiers.setValue(modifier, value);
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

    /** {@inheritDoc} */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /** {@inheritDoc} */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
    }

    protected Offset getOffset()
    {
        return this.offset;
    }

    protected void setOffset(Offset offset)
    {
        this.offset = offset;
    }

    protected Double getDepthOffset()
    {
        return this.depthOffset;
    }

    protected void setDepthOffset(Double depthOffset)
    {
        this.depthOffset = depthOffset; // Null is accepted, and indicates the default depth offset is used.
    }

    protected IconRetriever getIconRetriever()
    {
        return this.iconRetriever;
    }

    protected void setIconRetriever(IconRetriever retriever)
    {
        this.iconRetriever = retriever;
    }

    protected IconRetriever getModifierRetriever()
    {
        return this.modifierRetriever;
    }

    protected void setModifierRetriever(IconRetriever retriever)
    {
        this.modifierRetriever = retriever;
    }

    protected TextureAtlas getGlyphAtlas()
    {
        return this.glyphAtlas;
    }

    protected void setGlyphAtlas(TextureAtlas atlas)
    {
        // Note that we do not explicitly remove this symbol's glyphs from the old atlas. The modifier texture atlas
        // should be  configured to evict the oldest glyphs when the atlas is full. Leaving this symbol's glyphs in the
        // atlas does not incur any additional overhead, and has the benefit of ensuring that we do not remove glyphs
        // used by another symbol.

        this.glyphAtlas = atlas;
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
        // Calculate this symbol's per-frame values, re-using values already calculated this frame.
        if (dc.getFrameTimeStamp() != this.frameNumber)
        {
            // Compute the model and screen coordinate points corresponding to the position and altitude mode.
            this.computeSymbolPoints(dc);
            if (this.placePoint == null || this.screenPoint == null)
                return;

            // Compute the currently active attributes from either the normal or the highlight attributes.
            this.determineActiveAttributes();
            if (this.getActiveAttributes() == null)
                return;

            // Compute the icon and modifier layout.
            // TODO: layout only when necessary
            // TODO: layout must not issue new requests for resources, but must load resources that have already been requested.
            this.layout(dc);

            // Compute the scale and offset parameters that are applied during rendering. This must be done after
            // layout, because the transform depends on the frame rectangle computed during layout.
            this.computeTransform(dc);

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
        this.eyeDistance = 0;

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

        // Compute the symbol's screen location the distance between the eye point and the place point.
        this.screenPoint = dc.getView().project(this.placePoint);
        this.eyeDistance = this.placePoint.distanceTo3(dc.getView().getEyePoint());
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

    protected void layout(DrawContext dc)
    {
        this.screenRect = null;
        this.layoutRect = null;

        if (this.mustDrawIcon(dc))
            this.layoutIcon(dc);

        if (this.mustDrawGraphicModifiers(dc) || this.mustDrawTextModifiers(dc))
            this.layoutModifiers(dc);

        this.removeDeadModifiers(System.currentTimeMillis());
    }

    protected void layoutIcon(DrawContext dc)
    {
        if (this.getIconRetriever() == null)
            return;

        if (this.iconTexture == null) // Lazily create the symbol icon texture.
        {
            IconSource source = new IconSource(this.getIconRetriever(), this.getIdentifier(), this.modifiers);
            this.iconTexture = new IconTexture(source);
        }

        if (this.iconRect == null && this.iconTexture.bind(dc)) // Lazily assign the symbol icon rectangle.
        {
            // Compute the symbol icon's frame rectangle in local coordinates. This is used by the modifier layout to
            // determine where to place modifier graphics and modifier text. Note that we bind the texture in order to
            // load the texture image, and make the width and height available.
            int w = this.iconTexture.getWidth(dc);
            int h = this.iconTexture.getHeight(dc);
            Point2D point = this.iconOffset != null ? this.iconOffset.computeOffset(w, h, null, null) : new Point(0, 0);
            Dimension size = this.iconSize != null ? this.iconSize.compute(w, h, w, h) : new Dimension(w, h);
            this.iconRect = new Rectangle((int) point.getX(), (int) point.getY(), size.width, size.height);
        }

        if (this.iconRect != null)
        {
            if (this.screenRect != null)
                this.screenRect.add(this.iconRect);
            else
                this.screenRect = new Rectangle(this.iconRect);

            if (this.layoutRect != null)
                this.layoutRect.add(this.iconRect);
            else
                this.layoutRect = new Rectangle(this.iconRect);
        }
    }

    protected void layoutModifiers(DrawContext dc)
    {
        // Intentionally left blank. Subclasses can override this method in order to layout any modifiers associated
        // with this tactical symbol.
    }

    protected Rectangle layoutRect(Offset offset, Offset hotspot, Dimension size, Object layoutMode)
    {
        int x = 0;
        int y = 0;

        if (offset != null)
        {
            Rectangle rect;
            if (LAYOUT_ABSOLUTE.equals(layoutMode))
                rect = this.iconRect;
            else if (LAYOUT_RELATIVE.equals(layoutMode))
                rect = this.layoutRect;
            else // LAYOUT_NONE
                rect = this.iconRect;

            Point2D p = offset.computeOffset(rect.getWidth(), rect.getHeight(), null, null);
            x += rect.getX() + p.getX();
            y += rect.getY() + p.getY();
        }

        if (hotspot != null)
        {
            Point2D p = hotspot.computeOffset(size.getWidth(), size.getHeight(), null, null);
            x -= p.getX();
            y -= p.getY();
        }

        Rectangle rect = new Rectangle(x, y, size.width, size.height);

        if (this.screenRect != null)
            this.screenRect.add(rect);
        else
            this.screenRect = new Rectangle(rect);

        if (LAYOUT_ABSOLUTE.equals(layoutMode) || LAYOUT_RELATIVE.equals(layoutMode))
        {
            if (this.layoutRect != null)
                this.layoutRect.add(rect);
            else
                this.layoutRect = new Rectangle(rect);
        }

        return rect;
    }

    protected List<? extends Point2D> layoutPoints(Offset offset, List<? extends Point2D> points, Object layoutMode,
        int numPointsInLayout)
    {
        int x = 0;
        int y = 0;

        if (offset != null)
        {
            Rectangle rect;
            if (LAYOUT_ABSOLUTE.equals(layoutMode))
                rect = this.iconRect;
            else if (LAYOUT_RELATIVE.equals(layoutMode))
                rect = this.layoutRect;
            else // LAYOUT_NONE
                rect = this.iconRect;

            Point2D p = offset.computeOffset(rect.getWidth(), rect.getHeight(), null, null);
            x += rect.getX() + p.getX();
            y += rect.getY() + p.getY();
        }

        for (int i = 0; i < points.size(); i++)
        {
            Point2D p = points.get(i);
            p.setLocation(x + p.getX(), y + p.getY());

            if (this.screenRect != null)
                this.screenRect.add(p);
            else
                this.screenRect = new Rectangle((int) p.getX(), (int) p.getY(), 0, 0);

            if (i < numPointsInLayout && (LAYOUT_ABSOLUTE.equals(layoutMode) || LAYOUT_RELATIVE.equals(layoutMode)))
            {
                if (this.layoutRect != null)
                    this.layoutRect.add(p);
                else
                    this.layoutRect = new Rectangle((int) p.getX(), (int) p.getY(), 0, 0);
            }
        }

        return points;
    }

    protected void addGlyph(DrawContext dc, Offset offset, Offset hotspot, String modifierCode)
    {
        this.addGlyph(dc, offset, hotspot, modifierCode, null, null);
    }

    protected void addGlyph(DrawContext dc, Offset offset, Offset hotspot, String modifierCode, AVList retrieverParams,
        Object layoutMode)
    {
        IconAtlasElement elem = this.getGlyph(modifierCode, retrieverParams);

        if (elem.load(dc))
        {
            Rectangle rect = this.layoutRect(offset, hotspot, elem.getSize(), layoutMode);
            elem.setPoint(rect.getLocation());
            this.currentGlyphs.add(elem);
        }
    }

    protected void addLabel(DrawContext dc, Offset offset, Offset hotspot, String modifierText)
    {
        this.addLabel(dc, offset, hotspot, modifierText, null, null, null);
    }

    protected void addLabel(DrawContext dc, Offset offset, Offset hotspot, String modifierText, Font font, Color color,
        Object layoutMode)
    {
        if (font == null)
        {
            // Use either the currently specified text modifier font or compute a default if no font is specified.
            font = this.getActiveAttributes().getTextModifierFont();
            if (font == null)
                font = BasicTacticalSymbolAttributes.DEFAULT_TEXT_MODIFIER_FONT;
        }

        if (color == null)
        {
            // Use either the currently specified text modifier material or the default if no material is specified.
            Material material = this.getActiveAttributes().getTextModifierMaterial();
            if (material == null)
                material = BasicTacticalSymbolAttributes.DEFAULT_TEXT_MODIFIER_MATERIAL;

            // Use either the currently specified opacity or the default if no opacity is specified.
            Double opacity = this.getActiveAttributes().getOpacity();
            if (opacity == null)
                opacity = BasicTacticalSymbolAttributes.DEFAULT_OPACITY;

            int alpha = (int) (255 * opacity + 0.5);
            Color diffuse = material.getDiffuse();
            color = new Color(diffuse.getRed(), diffuse.getGreen(), diffuse.getBlue(), alpha);
        }

        TextRenderer tr = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), font);
        Rectangle bounds = tr.getBounds(modifierText).getBounds();
        Rectangle rect = this.layoutRect(offset, hotspot, bounds.getSize(), layoutMode);
        Point point = new Point(rect.getLocation().x, rect.getLocation().y + bounds.y + bounds.height);

        this.currentLabels.add(new Label(tr, modifierText, point, color));
    }

    protected void addLine(DrawContext dc, Offset offset, List<? extends Point2D> points)
    {
        this.addLine(dc, offset, points, null, 0);
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected void addLine(DrawContext dc, Offset offset, List<? extends Point2D> points, Object layoutMode,
        int numPointsInLayout)
    {
        points = this.layoutPoints(offset, points, layoutMode, numPointsInLayout);
        this.currentLines.add(new Line(points));
    }

    protected IconAtlasElement getGlyph(String modifierCode, AVList retrieverParams)
    {
        if (this.getGlyphAtlas() == null || this.getModifierRetriever() == null)
            return null;

        IconAtlasElement elem = this.glyphMap.get(modifierCode);

        if (elem == null)
        {
            IconSource source = new IconSource(this.getModifierRetriever(), modifierCode, retrieverParams);
            elem = new IconAtlasElement(this.getGlyphAtlas(), source);
            this.glyphMap.put(modifierCode, elem);
        }

        elem.lastUsed = System.currentTimeMillis();

        return elem;
    }

    protected void removeDeadModifiers(long now)
    {
        if (this.glyphMap.isEmpty())
            return;

        List<String> deadKeys = null; // Lazily created below to avoid unnecessary allocation.

        for (Map.Entry<String, IconAtlasElement> entry : this.glyphMap.entrySet())
        {
            if (entry.getValue().lastUsed + this.maxTimeSinceLastUsed < now)
            {
                if (deadKeys == null)
                    deadKeys = new ArrayList<String>();
                deadKeys.add(entry.getKey());
            }
        }

        if (deadKeys == null)
            return;

        for (String key : deadKeys)
        {
            this.glyphMap.remove(key);
        }
    }

    protected void computeTransform(DrawContext dc)
    {
        if (this.getActiveAttributes().getScale() != null)
        {
            this.sx = this.getActiveAttributes().getScale();
            this.sy = this.getActiveAttributes().getScale();
        }
        else
        {
            this.sx = BasicTacticalSymbolAttributes.DEFAULT_SCALE;
            this.sy = BasicTacticalSymbolAttributes.DEFAULT_SCALE;
        }

        if (this.getOffset() != null && this.iconRect != null)
        {
            Point2D p = this.getOffset().computeOffset(this.iconRect.getWidth(), this.iconRect.getHeight(), null, null);
            this.dx = -this.iconRect.getX() - p.getX();
            this.dy = -this.iconRect.getY() - p.getY();
        }
        else
        {
            this.dx = 0;
            this.dy = 0;
        }
    }

    protected Rectangle computeScreenExtent()
    {
        if (this.screenRect == null)
            return null;

        double x = this.screenPoint.x + this.sx * (this.dx + this.screenRect.getX());
        double y = this.screenPoint.y + this.sy * (this.dy + this.screenRect.getY());
        double width = this.sx * this.screenRect.getWidth();
        double height = this.sy * this.screenRect.getHeight();

        return new Rectangle((int) x, (int) y, (int) Math.ceil(width), (int) Math.ceil(height));
    }

    protected boolean intersectsFrustum(DrawContext dc)
    {
        View view = dc.getView();

        // Test the symbol's model coordinate point against the near and far clipping planes.
        if (this.placePoint != null
            && (view.getFrustumInModelCoordinates().getNear().distanceTo(this.placePoint) < 0
            || view.getFrustumInModelCoordinates().getFar().distanceTo(this.placePoint) < 0))
        {
            return false;
        }

        Rectangle screenExtent = this.computeScreenExtent();
        if (screenExtent != null)
        {
            if (dc.isPickingMode())
                return dc.getPickFrustums().intersectsAny(screenExtent);
            else
                return view.getViewport().intersects(screenExtent);
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

        Rectangle viewport = dc.getView().getViewport();

        this.BEogsh.clear(); // Reset the stack handler's internal state.
        this.BEogsh.pushAttrib(gl, attrMask);
        // TODO: comment on why this works
        this.BEogsh.pushProjectionIdentity(gl);
        gl.glOrtho(0d, viewport.getWidth(), 0d, viewport.getHeight(), 0d, -1d);
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

        if (dc.isPickingMode())
        {
            Color pickColor = dc.getUniquePickColor();
            pickCandidates.addPickableObject(this.createPickedObject(pickColor.getRGB()));
            gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
        }
        else
        {
            // Set the current color to white with the symbol's current opacity. This applies the symbol's opacity to
            // its icon texture and graphic modifier textures by multiplying texture fragment colors by the opacity. We
            // pre-multiply the white RGB color components by the alpha since the texture's RGB color components have
            // also been pre-multiplied by their color component.
            float a = this.getActiveAttributes().getOpacity() != null
                ? this.getActiveAttributes().getOpacity().floatValue()
                : (float) BasicTacticalSymbolAttributes.DEFAULT_OPACITY;
            gl.glColor4f(a, a, a, a);
        }

        Double depthOffsetUnits = this.getDepthOffset();
        try
        {
            // Apply any custom depth offset specified by the caller. This overrides the default depth offset specified
            // in beginRendering, and is therefore restored in the finally block below.
            if (depthOffsetUnits != null)
                gl.glPolygonOffset(0f, depthOffsetUnits.floatValue());

            this.prepareToDraw(dc);
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

    protected void prepareToDraw(DrawContext dc)
    {
        // Apply the symbol's offset in screen coordinates. We translate the X and Y coordinates so that the
        // symbol's hot spot (identified by its offset) is aligned with its screen point. We translate the Z
        // coordinate so that the symbol's depth values are appropriately computed by OpenGL according to its
        // distance from the eye. The orthographic projection matrix configured in beginRendering correctly maps
        // the screen point's Z coordinate to its corresponding depth value.
        GL gl = dc.getGL();
        gl.glLoadIdentity(); // Assumes that the current matrix mode is GL_MODELVIEW.
        gl.glTranslated(this.screenPoint.x, this.screenPoint.y, this.screenPoint.z);
        gl.glScaled(this.sx, this.sy, 1d);
        gl.glTranslated(this.dx, this.dy, 0d);
    }

    protected void draw(DrawContext dc)
    {
        if (this.mustDrawIcon(dc))
            this.drawIcon(dc);

        if (this.mustDrawGraphicModifiers(dc))
            this.drawGraphicModifiers(dc);

        if (this.mustDrawTextModifiers(dc) && !dc.isPickingMode())
            this.drawTextModifiers(dc);
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected boolean mustDrawIcon(DrawContext dc)
    {
        return true;
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected boolean mustDrawGraphicModifiers(DrawContext dc)
    {
        return this.isShowGraphicModifiers();
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected boolean mustDrawTextModifiers(DrawContext dc)
    {
        return this.isShowTextModifiers();
    }

    protected void drawIcon(DrawContext dc)
    {
        if (this.iconTexture == null || this.iconRect == null)
            return;

        if (!this.iconTexture.bind(dc))
            return;

        GL gl = dc.getGL();
        try
        {
            gl.glPushMatrix();
            gl.glScaled(this.iconTexture.getWidth(dc), this.iconTexture.getHeight(dc), 1d);
            dc.drawUnitQuad(this.iconTexture.getTexCoords());
        }
        finally
        {
            gl.glPopMatrix();
        }
    }

    protected void drawGraphicModifiers(DrawContext dc)
    {
        this.drawGlyphs(dc);
        this.drawLines(dc);
    }

    protected void drawTextModifiers(DrawContext dc)
    {
        this.drawLabels(dc);
    }

    protected void drawGlyphs(DrawContext dc)
    {
        if (this.glyphAtlas == null || this.currentGlyphs.isEmpty())
            return;

        if (!this.glyphAtlas.bind(dc))
            return;

        GL gl = dc.getGL();

        for (IconAtlasElement atlasElem : this.currentGlyphs)
        {
            Point point = atlasElem.getPoint();
            Dimension size = atlasElem.getSize();
            TextureCoords texCoords = atlasElem.getTexCoords();

            if (point == null || size == null || texCoords == null)
                continue;

            try
            {
                gl.glPushMatrix();
                gl.glTranslated(point.getX(), point.getY(), 0d);
                gl.glScaled(size.getWidth(), size.getHeight(), 1d);
                dc.drawUnitQuad(texCoords);
            }
            finally
            {
                gl.glPopMatrix();
            }
        }
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected void drawLabels(DrawContext dc)
    {
        if (this.currentLabels.isEmpty())
            return;

        TextRenderer tr = null;
        try
        {
            for (Label modifier : this.currentLabels)
            {
                if (tr == null || tr != modifier.getTextRenderer())
                {
                    if (tr != null)
                        tr.end3DRendering();
                    tr = modifier.getTextRenderer();
                    tr.begin3DRendering();
                }

                Point p = modifier.getPoint();
                tr.setColor(modifier.getColor());
                tr.draw(modifier.getText(), p.x, p.y);
            }
        }
        finally
        {
            if (tr != null)
                tr.end3DRendering();
        }
    }

    protected void drawLines(DrawContext dc)
    {
        // Use either the currently specified opacity or the default if no opacity is specified.
        Double opacity = this.getActiveAttributes().getOpacity() != null ? this.getActiveAttributes().getOpacity()
            : BasicTacticalSymbolAttributes.DEFAULT_OPACITY;

        GL gl = dc.getGL();

        try
        {
            // Disable the depth test and texturing. We draw the direction of movement as black lines in screen space.
            // Direction of movement lines for ground objects therefore likely penetrate the terrain, but must appear
            // on top to be visible.
            gl.glDisable(GL.GL_DEPTH_TEST);
            gl.glDisable(GL.GL_TEXTURE_2D);

            // Set the current color to black with the current opacity value as the alpha component. Blending is set to
            // pre-multiplied alpha mode, but we can just specify 0 for the RGB components because multiplying them by
            // the alpha component has no effect.
            if (!dc.isPickingMode())
                gl.glColor4f(0f, 0f, 0f, opacity.floatValue());

            for (Line lm : this.currentLines)
            {
                try
                {
                    gl.glBegin(GL.GL_LINE_STRIP);

                    for (Point2D p : lm.getPoints())
                    {
                        gl.glVertex2d(p.getX(), p.getY());
                    }
                }
                finally
                {
                    gl.glEnd();
                }
            }
        }
        finally
        {
            // Restore the depth test enable state and the texture 2D enable state to the values specified in
            // beginDrawing.
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glEnable(GL.GL_TEXTURE_2D);

            // Restore the current color to that specified in doDrawOrderedRenderable.
            if (!dc.isPickingMode())
                gl.glColor4f(opacity.floatValue(), opacity.floatValue(), opacity.floatValue(), opacity.floatValue());
        }
    }

    protected PickedObject createPickedObject(int colorCode)
    {
        Object owner = this.getDelegateOwner();
        return new PickedObject(colorCode, owner != null ? owner : this);
    }
}
