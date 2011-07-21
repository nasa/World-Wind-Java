/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import com.sun.opengl.util.j2d.TextRenderer;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.ogc.kml.impl.KMLExportUtil;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import javax.xml.stream.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import static gov.nasa.worldwind.ogc.kml.impl.KMLExportUtil.kmlBoolean;

/**
 * Represents a point placemark consisting of an image, an optional line linking the image to a corresponding point on
 * the terrain, and an optional label. The image and the label are displayed in the plane of the screen.
 * <p/>
 * Point placemarks have separate attributes for normal rendering and highlighted rendering. If highlighting is
 * requested but no highlight attributes are specified, the normal attributes are used. If the normal attributes are not
 * specified, default attributes are used. See {@link #getDefaultAttributes()}.
 * <p/>
 * This class implements and extends the functionality of a KML <i>Point</i>.
 *
 * @author tag
 * @version $Id$
 */
public class PointPlacemark extends WWObjectImpl
    implements OrderedRenderable, Locatable, Movable, Highlightable, Exportable
{
    /** The scale to use when highlighting if no highlight attributes are specified. */
    protected static final Double DEFAULT_HIGHLIGHT_SCALE = 1.3;
    /** The label offset to use if none is specified but an image has been specified. */
    protected static final Offset DEFAULT_LABEL_OFFSET_IF_UNSPECIFIED = new Offset(1d, 0.6d, AVKey.FRACTION,
        AVKey.FRACTION);
    /** The point size to use when none is specified. */
    protected static final Double DEFAULT_POINT_SIZE = 5d;

    /** The attributes used if attributes are not specified. */
    protected static final PointPlacemarkAttributes defaultAttributes = new PointPlacemarkAttributes();

    static
    {
        defaultAttributes.setImageAddress(PointPlacemarkAttributes.DEFAULT_IMAGE_PATH);
        defaultAttributes.setImageOffset(PointPlacemarkAttributes.DEFAULT_IMAGE_OFFSET);
        defaultAttributes.setLabelOffset(PointPlacemarkAttributes.DEFAULT_LABEL_OFFSET);
        defaultAttributes.setScale(PointPlacemarkAttributes.DEFAULT_IMAGE_SCALE);
        defaultAttributes.setLabelScale(PointPlacemarkAttributes.DEFAULT_LABEL_SCALE);
    }

    protected Position position;
    protected String labelText;
    protected PointPlacemarkAttributes normalAttrs;
    protected PointPlacemarkAttributes highlightAttrs;
    protected PointPlacemarkAttributes activeAttributes = new PointPlacemarkAttributes(); // re-determined each frame
    protected Map<String, WWTexture> textures = new HashMap<String, WWTexture>(); // holds the textures created
    protected WWTexture activeTexture; // determined each frame

    protected boolean highlighted;
    protected boolean visible = true;
    protected int altitudeMode = WorldWind.CLAMP_TO_GROUND;
    protected boolean lineEnabled;
    protected boolean applyVerticalExaggeration = true;
    protected int linePickWidth = 10;
    protected boolean enableBatchRendering = true;
    protected boolean enableBatchPicking = true;
    protected Object delegateOwner;

    // Values computed once per frame and reused during the frame as needed.
    protected long frameNumber = -1; // identifies frame used to calculate these values
    protected Vec4 placePoint; // the Cartesian point corresponding to the placemark position
    protected Vec4 terrainPoint; // point on the terrain extruded from the placemark position.
    protected Vec4 screenPoint; // the projection of the place-point in the viewport (on the screen)
    protected double eyeDistance; // used to order the placemark as an ordered renderable
    protected double dx; // offsets needed to position image relative to the placemark position
    protected double dy;
    protected Layer pickLayer; // shape's layer when ordered renderable was created

    protected PickSupport pickSupport = new PickSupport();

    /**
     * Construct a point placemark.
     *
     * @param position the placemark position.
     *
     * @throws IllegalArgumentException if the position is null.
     */
    public PointPlacemark(Position position)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.position = position;
    }

    /**
     * Sets the placemark's position.
     *
     * @param position the placemark position.
     *
     * @throws IllegalArgumentException if the position is null.
     */
    public void setPosition(Position position)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.position = position;
    }

    /**
     * Returns the placemark's position.
     *
     * @return the placemark's position.
     */
    public Position getPosition()
    {
        return this.position;
    }

    /**
     * Indicates whether the placemark is drawn when in view.
     *
     * @return true if the placemark is drawn when in view, otherwise false.
     */
    public boolean isVisible()
    {
        return this.visible;
    }

    /**
     * Specifies whether the placemark is drawn when in view.
     *
     * @param visible true if the placemark is drawn when in view, otherwise false.
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Returns the placemark's altitude mode. See {@link #setAltitudeMode(int)} for a description of the modes.
     *
     * @return the placemark's altitude mode.
     */
    public int getAltitudeMode()
    {
        return this.altitudeMode;
    }

    /**
     * Specifies the placemark's altitude mode. Recognized modes are: <ul> <li><b>@link WorldWind#CLAMP_TO_GROUND}</b>
     * -- the point is placed on the terrain at the latitude and longitude of its position.</li> <li><b>@link
     * WorldWind#RELATIVE_TO_GROUND}</b> -- the point is placed above the terrain at the latitude and longitude of its
     * position and the distance specified by its elevation.</li> <li><b>{@link WorldWind#ABSOLUTE}</b> -- the point is
     * placed at its specified position. </ul>
     *
     * @param altitudeMode the altitude mode
     */
    public void setAltitudeMode(int altitudeMode)
    {
        this.altitudeMode = altitudeMode;
    }

    /** {@inheritDoc} * */
    public double getDistanceFromEye()
    {
        return this.eyeDistance;
    }

    /**
     * Indicates whether a line from the placemark point to the corresponding position on the terrain is drawn.
     *
     * @return true if the line is drawn, otherwise false.
     */
    public boolean isLineEnabled()
    {
        return lineEnabled;
    }

    /**
     * Specifies whether a line from the placemark point to the corresponding position on the terrain is drawn.
     *
     * @param lineEnabled true if the line is drawn, otherwise false.
     */
    public void setLineEnabled(boolean lineEnabled)
    {
        this.lineEnabled = lineEnabled;
    }

    /**
     * Specifies the attributes used when the placemark is drawn normally, not highlighted.
     *
     * @param attrs the attributes to use in normal mode. May be null to indicate use of default attributes.
     */
    public void setAttributes(PointPlacemarkAttributes attrs)
    {
        if (this.normalAttrs != null && this.normalAttrs.getImageAddress() != null)
            this.textures.remove(this.normalAttrs.getImageAddress());

        this.normalAttrs = attrs;
    }

    /**
     * Returns the attributes used when the placemark is drawn normally, not highlighted.
     *
     * @return the attributes used in normal mode. May be null to indicate use of default attributes.
     */
    public PointPlacemarkAttributes getAttributes()
    {
        return this.normalAttrs;
    }

    /**
     * Specifies the attributes used to draw the placemark when it's highlighted.
     *
     * @param attrs the attributes to use in normal mode. May be null to indicate use of the normal attributes.
     */
    public void setHighlightAttributes(PointPlacemarkAttributes attrs)
    {
        if (this.highlightAttrs != null && this.highlightAttrs.getImageAddress() != null)
            this.textures.remove(this.highlightAttrs.getImageAddress());

        this.highlightAttrs = attrs;
    }

    /**
     * Returns the attributes used to draw the placemark when it's highlighted.
     *
     * @return the attributes used in normal mode. May be null to indicate use of the normal attributes.
     */
    public PointPlacemarkAttributes getHighlightAttributes()
    {
        return this.highlightAttrs;
    }

    /**
     * Returns the attributes used if normal attributes are not specified.
     *
     * @return the default attributes.
     */
    public PointPlacemarkAttributes getDefaultAttributes()
    {
        return defaultAttributes;
    }

    /**
     * Indicates whether the placemark is drawn highlighted.
     *
     * @return true if the placemark is drawn highlighted, otherwise false.
     */
    public boolean isHighlighted()
    {
        return this.highlighted;
    }

    /**
     * Specfies whether the placemark is drawn highlighted.
     *
     * @param highlighted true if the placemark is drawn highlighted, otherwise false.
     */
    public void setHighlighted(boolean highlighted)
    {
        this.highlighted = highlighted;
    }

    /**
     * Returns the placemark's label text.
     *
     * @return the placemark's label next, which man be null.
     */
    public String getLabelText()
    {
        return labelText;
    }

    /**
     * Specifies the placemark's label text that is displayed alongside the placemark.
     *
     * @param labelText the placemark label text. If null, no label is displayed.
     */
    public void setLabelText(String labelText)
    {
        this.labelText = labelText != null ? labelText.trim() : null;
    }

    public boolean isApplyVerticalExaggeration()
    {
        return applyVerticalExaggeration;
    }

    public void setApplyVerticalExaggeration(boolean applyVerticalExaggeration)
    {
        this.applyVerticalExaggeration = applyVerticalExaggeration;
    }

    public int getLinePickWidth()
    {
        return linePickWidth;
    }

    public void setLinePickWidth(int linePickWidth)
    {
        this.linePickWidth = linePickWidth;
    }

    /**
     * Indicates whether batch rendering is enabled.
     *
     * @return true if batch rendering is enabled, otherwise false.
     *
     * @see #setEnableBatchRendering(boolean).
     */
    public boolean isEnableBatchRendering()
    {
        return enableBatchRendering;
    }

    /**
     * Specifies whether adjacent Polygons in the ordered renderable list may be rendered together if they are contained
     * in the same layer. This increases performance and there is seldom a reason to disable it.
     *
     * @param enableBatchRendering true to enable batch rendering, otherwise false.
     */
    public void setEnableBatchRendering(boolean enableBatchRendering)
    {
        this.enableBatchRendering = enableBatchRendering;
    }

    /**
     * Indicates whether batch picking is enabled.
     *
     * @return true if batch rendering is enabled, otherwise false.
     *
     * @see #setEnableBatchPicking(boolean).
     */
    public boolean isEnableBatchPicking()
    {
        return enableBatchPicking;
    }

    /**
     * Returns the delegate owner of this placemark. If non-null, the returned object replaces the placemark as the
     * pickable object returned during picking. If null, the placemark itself is the pickable object returned during
     * picking.
     *
     * @return the object used as the pickable object returned during picking, or null to indicate the the placemark is
     *         returned during picking.
     */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /**
     * Specifies the delegate owner of this placemark. If non-null, the delegate owner replaces the placemark as the
     * pickable object returned during picking. If null, the placemark itself is the pickable object returned during
     * picking.
     *
     * @param owner the object to use as the pickable object returned during picking, or null to return the placemark.
     */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
    }

    protected PointPlacemarkAttributes getActiveAttributes()
    {
        return this.activeAttributes;
    }

    /**
     * Specifies whether adjacent Polygons in the ordered renderable list may be pick-tested together if they are
     * contained in the same layer. This increases performance but allows only the top-most of the polygons to be
     * reported in a {@link gov.nasa.worldwind.event.SelectEvent} even if several of the polygons are at the pick
     * position.
     * <p/>
     * Batch rendering ({@link #setEnableBatchRendering(boolean)}) must be enabled in order for batch picking to occur.
     *
     * @param enableBatchPicking true to enable batch rendering, otherwise false.
     */
    public void setEnableBatchPicking(boolean enableBatchPicking)
    {
        this.enableBatchPicking = enableBatchPicking;
    }

    /**
     * Indicates whether a point should be drawn when the active texture is null.
     *
     * @param dc the current draw context.
     *
     * @return true if a point should be drawn, otherwise false.
     */
    @SuppressWarnings( {"UnusedParameters"})
    protected boolean isDrawPoint(DrawContext dc)
    {
        return this.activeTexture == null && this.getActiveAttributes().isUsePointAsDefaultImage();
    }

    public void pick(DrawContext dc, Point pickPoint)
    {
        // This method is called only when ordered renderables are being drawn.
        // Arg checked within call to render.

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

        if (dc.getSurfaceGeometry() == null)
            return;

        if (!this.isVisible())
            return;

        if (dc.isOrderedRenderingMode())
            this.drawOrderedRenderable(dc);
        else
            this.makeOrderedRenderable(dc);
    }

    /**
     * If the scene controller is rendering ordered renderables, this method draws this placemark's image as an ordered
     * renderable. Otherwise the method determines whether this instance should be added to the ordered renderable
     * list.
     * <p/>
     * The Cartesian and screen points of the placemark are computed during the first call per frame and re-used in
     * subsequent calls of that frame.
     *
     * @param dc the current draw context.
     */
    protected void makeOrderedRenderable(DrawContext dc)
    {
        // The rest of the code in this method determines whether to queue an ordered renderable for the placemark
        // and its optional line.

        // Re-use values already calculated this frame.
        if (dc.getFrameTimeStamp() != this.frameNumber)
        {
            this.computePlacemarkPoints(dc);
            if (this.placePoint == null || this.screenPoint == null)
                return;

            this.determineActiveAttributes();
            if (this.activeTexture == null && !this.getActiveAttributes().isUsePointAsDefaultImage())
                return;

            this.computeImageOffset(dc); // calculates offsets to align the image with the hotspot

            this.frameNumber = dc.getFrameTimeStamp();
        }

        // Don't draw if beyond the horizon.
        double horizon = dc.getView().getHorizonDistance();
        if (this.eyeDistance > horizon)
            return;

        if (this.intersectsFrustum(dc) || this.isDrawLine(dc))
            dc.addOrderedRenderable(this); // add the image ordered renderable

        if (dc.isPickingMode())
            this.pickLayer = dc.getCurrentLayer();
    }

    /**
     * Determines whether the placemark image intersects the view frustum.
     *
     * @param dc the current draw context.
     *
     * @return true if the image intersects the frustum, otherwise false.
     */
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

        Rectangle rect = this.computeImageRectangle(dc);
        if (dc.isPickingMode())
            return dc.getPickFrustums().intersectsAny(rect);
        else
            return view.getViewport().intersects(rect);
    }

    /**
     * Establish the OpenGL state needed to draw Paths.
     *
     * @param dc the current draw context.
     */
    protected void beginDrawing(DrawContext dc)
    {
        GL gl = dc.getGL();

        int attrMask =
            GL.GL_DEPTH_BUFFER_BIT // for depth test, depth mask and depth func
                | GL.GL_TRANSFORM_BIT // for modelview and perspective
                | GL.GL_VIEWPORT_BIT // for depth range
                | GL.GL_CURRENT_BIT // for current color
                | GL.GL_COLOR_BUFFER_BIT // for alpha test func and ref, and blend
                | GL.GL_DEPTH_BUFFER_BIT // for depth func
                | GL.GL_ENABLE_BIT // for enable/disable changes
                | GL.GL_HINT_BIT | GL.GL_LINE_BIT; // for antialiasing and line attrs

        gl.glPushAttrib(attrMask);

        if (!dc.isPickingMode())
        {
            gl.glEnable(GL.GL_BLEND);
            OGLUtil.applyBlending(gl, false);
        }
    }

    /**
     * Pop the state set in beginDrawing.
     *
     * @param dc the current draw context.
     */
    protected void endDrawing(DrawContext dc)
    {
        dc.getGL().glBindTexture(GL.GL_TEXTURE_2D, 0);
        dc.getGL().glPopAttrib();
    }

    /**
     * Draws the path as an ordered renderable.
     *
     * @param dc the current draw context.
     */
    protected void drawOrderedRenderable(DrawContext dc)
    {
        this.beginDrawing(dc);
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

    /**
     * Draws this ordered renderable and all subsequent PointPlacemark ordered renderables in the ordered renderable
     * list.
     *
     * @param dc the current draw context.
     */
    protected void drawBatched(DrawContext dc)
    {
        // Draw as many as we can in a batch to save ogl state switching.
        Object nextItem = dc.peekOrderedRenderables();

        if (!dc.isPickingMode())
        {
            while (nextItem != null && nextItem instanceof PointPlacemark)
            {
                PointPlacemark p = (PointPlacemark) nextItem;
                if (!p.isEnableBatchRendering())
                    break;

                dc.pollOrderedRenderables(); // take it off the queue
                p.doDrawOrderedRenderable(dc, this.pickSupport);

                nextItem = dc.peekOrderedRenderables();
            }
        }
        else if (this.isEnableBatchPicking())
        {
            while (nextItem != null && nextItem instanceof PointPlacemark)
            {
                PointPlacemark p = (PointPlacemark) nextItem;
                if (!p.isEnableBatchRendering() || !p.isEnableBatchPicking())
                    break;

                if (p.pickLayer != this.pickLayer) // batch pick only within a single layer
                    break;

                dc.pollOrderedRenderables(); // take it off the queue
                p.doDrawOrderedRenderable(dc, this.pickSupport);

                nextItem = dc.peekOrderedRenderables();
            }
        }
    }

    /**
     * Draw this placemark as an ordered renderable. If in picking mode, add it to the picked object list of specified
     * {@link PickSupport}. The <code>PickSupport</code> may not be the one associated with this instance. During batch
     * picking the <code>PickSupport</code> of the instance initiating the batch picking is used so that all shapes
     * rendered in batch are added to the same pick list.
     *
     * @param dc             the current draw context.
     * @param pickCandidates a pick support holding the picked object list to add this shape to.
     */
    protected void doDrawOrderedRenderable(DrawContext dc, PickSupport pickCandidates)
    {
        if (this.isDrawLine(dc))
            this.drawLine(dc, pickCandidates);

        if (this.activeTexture == null)
        {
            if (this.isDrawPoint(dc))
                this.drawPoint(dc, pickCandidates);
            return;
        }

        javax.media.opengl.GL gl = dc.getGL();

        OGLStackHandler osh = new OGLStackHandler();
        try
        {
            if (dc.isPickingMode())
            {
                // Set up to replace the non-transparent texture colors with the single pick color.
                gl.glEnable(GL.GL_TEXTURE_2D);
                gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE);
                gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, GL.GL_PREVIOUS);
                gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, GL.GL_REPLACE);

                Color pickColor = dc.getUniquePickColor();
                pickCandidates.addPickableObject(this.createPickedObject(dc, pickColor));
                gl.glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
            }
            else
            {
                gl.glEnable(GL.GL_TEXTURE_2D);
                Color color = this.getActiveAttributes().getImageColor();
                if (color == null)
                    color = PointPlacemarkAttributes.DEFAULT_IMAGE_COLOR;
                gl.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(),
                    (byte) color.getAlpha());
            }

            // The image is drawn using a parallel projection.
            osh.pushProjectionIdentity(gl);
            gl.glOrtho(0d, dc.getView().getViewport().width, 0d, dc.getView().getViewport().height, -1d, 1d);

            // Apply the depth buffer but don't change it (for screen-space shapes).
            if ((!dc.isDeepPickingEnabled()))
                gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthMask(false);

            // Suppress any fully transparent image pixels.
            gl.glEnable(GL.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL.GL_GREATER, 0.001f);

            // Adjust depth of image to bring it slightly forward
            double depth = screenPoint.z - (8d * 0.00048875809d);
            depth = depth < 0d ? 0d : (depth > 1d ? 1d : depth);
            gl.glDepthFunc(GL.GL_LESS);
            gl.glDepthRange(depth, depth);

            // The image is drawn using a translated and scaled unit quad.
            // Translate to screen point and adjust to align hot spot.
            osh.pushModelviewIdentity(gl);
            gl.glTranslated(screenPoint.x + this.dx, screenPoint.y + this.dy, 0);

            // Compute the scale
            double xscale;
            Double scale = this.getActiveAttributes().getScale();
            if (scale != null)
                xscale = scale * this.activeTexture.getWidth(dc);
            else
                xscale = this.activeTexture.getWidth(dc);

            double yscale;
            if (scale != null)
                yscale = scale * this.activeTexture.getHeight(dc);
            else
                yscale = this.activeTexture.getHeight(dc);

            Double heading = getActiveAttributes().getHeading();
            Double pitch = getActiveAttributes().getPitch();

            // Adjust heading to be relative to globe or screen
            if (heading != null)
            {
                if (AVKey.RELATIVE_TO_GLOBE.equals(this.getActiveAttributes().getHeadingReference()))
                    heading = dc.getView().getHeading().degrees - heading;
                else
                    heading = -heading;
            }

            // Apply the heading and pitch if specified.
            if (heading != null || pitch != null)
            {
                gl.glTranslated(xscale / 2, yscale / 2, 0);
                if (pitch != null)
                    gl.glRotated(pitch, 1, 0, 0);
                if (heading != null)
                    gl.glRotated(heading, 0, 0, 1);
                gl.glTranslated(-xscale / 2, -yscale / 2, 0);
            }

            // Scale the unit quad
            gl.glScaled(xscale, yscale, 1);

            if (this.activeTexture.bind(dc))
                dc.drawUnitQuad(activeTexture.getTexCoords());

            gl.glDepthRange(0, 1); // reset depth range to the OGL default
//
//            gl.glDisable(GL.GL_TEXTURE_2D);
//            dc.drawUnitQuadOutline(); // for debugging label placement

            if (this.mustDrawLabel() && !dc.isPickingMode()) // don't pick via the label
                this.drawLabel(dc);
        }
        finally
        {
            if (dc.isPickingMode())
            {
                gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, OGLUtil.DEFAULT_TEX_ENV_MODE);
                gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, OGLUtil.DEFAULT_SRC0_RGB);
                gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, OGLUtil.DEFAULT_COMBINE_RGB);
            }

            gl.glDisable(GL.GL_TEXTURE_2D);
            osh.pop(gl);
        }
    }

    /**
     * Create a {@link PickedObject} for this placemark. The PickedObject returned by this method will be added to the
     * pick list to represent the current placemark.
     *
     * @param dc        Active draw context.
     * @param pickColor Unique color for this PickedObject.
     *
     * @return A new picked object.
     */
    protected PickedObject createPickedObject(DrawContext dc, Color pickColor)
    {
        Object delegateOwner = this.getDelegateOwner();
        return new PickedObject(pickColor.getRGB(), delegateOwner != null ? delegateOwner : this);
    }

    /**
     * Determines if the placemark label will be rendered.
     *
     * @return True if the label must be drawn. This implementation always returns true.
     */
    protected boolean mustDrawLabel()
    {
        return true;
    }

    /**
     * Draws the placemark's label if a label is specified.
     *
     * @param dc the current draw context.
     */
    protected void drawLabel(DrawContext dc)
    {
        if (this.labelText == null)
            return;

        Color color = this.getActiveAttributes().getLabelColor();
        // Use the default color if the active attributes do not specify one.
        if (color == null)
            color = PointPlacemarkAttributes.DEFAULT_LABEL_COLOR;
        // If the label color's alpha component is 0 or less, then the label is completely transparent. Exit
        // immediately; the label does not need to be rendered.
        if (color.getAlpha() <= 0)
            return;

        // Apply the label color's alpha component to the background color. This causes both the label foreground and
        // background to blend evenly into the frame. If the alpha component is 255 we just use the pre-defined constant
        // for BLACK to avoid creating a new background color every frame.
        Color backgroundColor = (color.getAlpha() < 255 ? new Color(0, 0, 0, color.getAlpha()) : Color.BLACK);

        Font font = this.getActiveAttributes().getLabelFont();
        if (font == null)
            font = PointPlacemarkAttributes.DEFAULT_LABEL_FONT;

        float x = (float) (this.screenPoint.x + this.dx);
        float y = (float) (this.screenPoint.y + this.dy);

        Double imageScale = this.getActiveAttributes().getScale();
        Offset os = this.getActiveAttributes().getLabelOffset();
        if (os == null)
            os = DEFAULT_LABEL_OFFSET_IF_UNSPECIFIED;
        double w = this.activeTexture != null ? this.activeTexture.getWidth(dc) : 1;
        double h = this.activeTexture != null ? this.activeTexture.getHeight(dc) : 1;
        Point.Double offset = os.computeOffset(w, h, imageScale, imageScale);
        x += offset.x;
        y += offset.y;

        javax.media.opengl.GL gl = dc.getGL();

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        Double labelScale = this.getActiveAttributes().getLabelScale();
        if (labelScale != null)
        {
            gl.glTranslatef(x, y, 0); // Assumes matrix mode is MODELVIEW
            gl.glScaled(labelScale, labelScale, 1);
            gl.glTranslatef(-x, -y, 0);
        }

        // Do not depth buffer the label. (Placemarks beyond the horizon are culled above.)
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDepthMask(false);

        TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), font);
        try
        {
            textRenderer.begin3DRendering();
            textRenderer.setColor(backgroundColor);
            textRenderer.draw3D(this.labelText, x + 1, y - 1, 0, 1);
            textRenderer.setColor(color);
            textRenderer.draw3D(this.labelText, x, y, 0, 1);
        }
        finally
        {
            textRenderer.end3DRendering();
        }
    }

    /**
     * Draws the placemark's line.
     *
     * @param dc             the current draw context.
     * @param pickCandidates the pick support object to use when adding this as a pick candidate.
     */
    protected void drawLine(DrawContext dc, PickSupport pickCandidates)
    {
        javax.media.opengl.GL gl = dc.getGL();

        if ((!dc.isDeepPickingEnabled()))
            gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glDepthMask(true);

        try
        {
            dc.getView().pushReferenceCenter(dc, this.placePoint); // draw relative to the place point

            this.setLineWidth(dc);
            this.setLineColor(dc, pickCandidates);

            gl.glBegin(GL.GL_LINE_STRIP);
            gl.glVertex3d(Vec4.ZERO.x, Vec4.ZERO.y, Vec4.ZERO.z);
            gl.glVertex3d(this.terrainPoint.x - this.placePoint.x, this.terrainPoint.y - this.placePoint.y,
                this.terrainPoint.z - this.placePoint.z);
            gl.glEnd();
        }
        finally
        {
            dc.getView().popReferenceCenter(dc);
        }
    }

    /**
     * Draws the placemark's line.
     *
     * @param dc             the current draw context.
     * @param pickCandidates the pick support object to use when adding this as a pick candidate.
     */
    protected void drawPoint(DrawContext dc, PickSupport pickCandidates)
    {
        javax.media.opengl.GL gl = dc.getGL();

        OGLStackHandler osh = new OGLStackHandler();
        try
        {
            osh.pushAttrib(gl, GL.GL_POINT_BIT);

            this.setLineColor(dc, pickCandidates);
            this.setPointSize(dc);

            // The point is drawn using a parallel projection.
            osh.pushProjectionIdentity(gl);
            gl.glOrtho(0d, dc.getView().getViewport().width, 0d, dc.getView().getViewport().height, -1d, 1d);

            osh.pushModelviewIdentity(gl);

            // Apply the depth buffer but don't change it (for screen-space shapes).
            if ((!dc.isDeepPickingEnabled()))
                gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDepthMask(false);

            // Suppress any fully transparent pixels.
            gl.glEnable(GL.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL.GL_GREATER, 0.001f);

            // Adjust depth of point to bring it slightly forward
            double depth = this.screenPoint.z - (8d * 0.00048875809d);
            depth = depth < 0d ? 0d : (depth > 1d ? 1d : depth);
            gl.glDepthFunc(GL.GL_LESS);
            gl.glDepthRange(depth, depth);

            gl.glBegin(GL.GL_POINTS);
            gl.glVertex3d(this.screenPoint.x, this.screenPoint.y, 0);
            gl.glEnd();

            gl.glDepthRange(0, 1); // reset depth range to the OGL default

            if (!dc.isPickingMode()) // don't pick via the label
                this.drawLabel(dc);
        }
        finally
        {
            osh.pop(gl);
        }
    }

    /**
     * Determines whether the placemark's optional line should be drawn and whether it intersects the view frustum.
     *
     * @param dc the current draw context.
     *
     * @return true if the line should be drawn and it intersects the view frustum, otherwise false.
     */
    protected boolean isDrawLine(DrawContext dc)
    {
        if (!this.isLineEnabled() || this.getAltitudeMode() == WorldWind.CLAMP_TO_GROUND || this.terrainPoint == null)
            return false;

        if (dc.isPickingMode())
            return dc.getPickFrustums().intersectsAny(this.placePoint, this.terrainPoint);
        else
            return dc.getView().getFrustumInModelCoordinates().intersectsSegment(this.placePoint, this.terrainPoint);
    }

    /**
     * Sets the width of the placemark's line during rendering.
     *
     * @param dc the current draw context.
     */
    protected void setLineWidth(DrawContext dc)
    {
        Double lineWidth = this.getActiveAttributes().getLineWidth();
        if (lineWidth != null)
        {
            GL gl = dc.getGL();

            if (dc.isPickingMode())
            {
                gl.glLineWidth(lineWidth.floatValue() + this.getLinePickWidth());
            }
            else
                gl.glLineWidth(lineWidth.floatValue());

            if (!dc.isPickingMode())
            {
                gl.glHint(GL.GL_LINE_SMOOTH_HINT, this.getActiveAttributes().getAntiAliasHint());
                gl.glEnable(GL.GL_LINE_SMOOTH);
            }
        }
    }

    /**
     * Sets the width of the placemark's point during rendering.
     *
     * @param dc the current draw context.
     */
    protected void setPointSize(DrawContext dc)
    {
        GL gl = dc.getGL();

        Double scale = this.getActiveAttributes().getScale();
        if (scale == null)
            scale = DEFAULT_POINT_SIZE;

        if (dc.isPickingMode())
            gl.glPointSize(scale.floatValue() + this.getLinePickWidth());
        else
            gl.glPointSize(scale.floatValue());

        if (!dc.isPickingMode())
        {
            gl.glEnable(GL.GL_POINT_SMOOTH);
            gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
        }
    }

    /**
     * Sets the color of the placemark's line during rendering.
     *
     * @param dc             the current draw context.
     * @param pickCandidates the pick support object to use when adding this as a pick candidate.
     */
    protected void setLineColor(DrawContext dc, PickSupport pickCandidates)
    {
        if (!dc.isPickingMode())
        {
            Color color = this.getActiveAttributes().getLineColor();
            if (color == null)
                color = PointPlacemarkAttributes.DEFAULT_LINE_COLOR;
            dc.getGL().glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(),
                (byte) color.getAlpha());
        }
        else
        {
            Color pickColor = dc.getUniquePickColor();
            pickCandidates.addPickableObject(pickColor.getRGB(), this, null);
            dc.getGL().glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());
        }
    }

    /** Determines which attributes -- normal, highlight or default -- to use each frame. */
    protected void determineActiveAttributes()
    {
        PointPlacemarkAttributes actAttrs = this.getActiveAttributes();

        if (this.isHighlighted())
        {
            if (this.getHighlightAttributes() != null)
            {
                actAttrs.copy(this.getHighlightAttributes());

                // Even though there are highlight attributes, there may not be an image for them, so use the normal image.
                if (WWUtil.isEmpty(actAttrs.getImageAddress())
                    && this.getAttributes() != null && !WWUtil.isEmpty(this.getAttributes().getImageAddress()))
                {
                    actAttrs.setImageAddress(this.getAttributes().getImageAddress());
                    if (this.getAttributes().getScale() != null)
                        actAttrs.setScale(DEFAULT_HIGHLIGHT_SCALE * this.getAttributes().getScale());
                    else
                        actAttrs.setScale(DEFAULT_HIGHLIGHT_SCALE);
                }
            }
            else
            {
                // If no highlight attributes have been specified we need to use the normal attributes but adjust them
                // for highlighting.
                if (this.getAttributes() != null)
                {
                    actAttrs.copy(this.getAttributes());
                    if (getAttributes().getScale() != null)
                        actAttrs.setScale(DEFAULT_HIGHLIGHT_SCALE * this.getAttributes().getScale());
                    else
                        actAttrs.setScale(DEFAULT_HIGHLIGHT_SCALE);
                }
                else
                {
                    actAttrs.copy(defaultAttributes);
                    if (defaultAttributes.getScale() != null)
                        actAttrs.setScale(DEFAULT_HIGHLIGHT_SCALE * defaultAttributes.getScale());
                    else
                        actAttrs.setScale(DEFAULT_HIGHLIGHT_SCALE);
                }
            }
        }
        else if (this.getAttributes() != null)
        {
            actAttrs.copy(this.getAttributes());
        }
        else
        {
            actAttrs.copy(defaultAttributes);
            if (this.activeTexture == null && actAttrs.isUsePointAsDefaultImage())
            {
                actAttrs.setImageAddress(null);
                actAttrs.setScale(DEFAULT_POINT_SIZE);
            }
        }

        this.activeTexture = this.chooseTexture(actAttrs);

        if (this.activeTexture == null && actAttrs.isUsePointAsDefaultImage())
        {
            actAttrs.setImageAddress(null);
            actAttrs.setImageOffset(null);
            if (actAttrs.getScale() == null)
                actAttrs.setScale(DEFAULT_POINT_SIZE);
        }
    }

    /**
     * Determines the appropriate texture for the current availability.
     *
     * @param attrs the attributes specifying the placemark image and properties.
     *
     * @return the appropriate texture, or null if an image is not available.
     */
    protected WWTexture chooseTexture(PointPlacemarkAttributes attrs)
    {
        if (!WWUtil.isEmpty(attrs.getImageAddress()))
        {
            WWTexture texture = this.textures.get(attrs.getImageAddress());
            if (texture != null)
                return texture;

            URL localUrl = WorldWind.getDataFileStore().requestFile(attrs.getImageAddress());
            if (localUrl != null)
            {
                texture = new BasicWWTexture(localUrl, true);
                this.textures.put(attrs.getImageAddress(), texture);
                return texture;
            }
        }

        if (this.getActiveAttributes().usePointAsDefaultImage)
            return null;

        // Use the default image if no other is defined or it's not yet available.
        WWTexture texture = this.textures.get(defaultAttributes.getImageAddress());
        this.getActiveAttributes().setImageOffset(defaultAttributes.getImageOffset());
        if (attrs.getScale() != null)
            this.getActiveAttributes().setScale(defaultAttributes.getScale() * attrs.getScale());
        else
            this.getActiveAttributes().setScale(defaultAttributes.getScale());
        if (texture == null)
        {
            URL localUrl = WorldWind.getDataFileStore().requestFile(defaultAttributes.getImageAddress());
            if (localUrl != null)
            {
                texture = new BasicWWTexture(localUrl, true);
                this.textures.put(defaultAttributes.getImageAddress(), texture);
            }
        }

        return texture;
    }

    /**
     * Computes and stores the placemark's Cartesian location, the Cartesian location of the corresponding point on the
     * terrain (if the altitude mode requires it), and the screen-space projection of the placemark's point. Applies the
     * placemark's altitude mode when computing the points.
     *
     * @param dc the current draw context.
     */
    protected void computePlacemarkPoints(DrawContext dc)
    {
        this.placePoint = null;
        this.terrainPoint = null;
        this.screenPoint = null;

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
        else  // ABSOLUTE
        {
            double height = pos.getElevation()
                * (this.isApplyVerticalExaggeration() ? dc.getVerticalExaggeration() : 1);
            this.placePoint = dc.getGlobe().computePointFromPosition(pos.getLatitude(), pos.getLongitude(), height);
        }

        if (this.placePoint == null)
            return;

        // Compute a terrain point if needed.
        if (this.isLineEnabled() && this.altitudeMode != WorldWind.CLAMP_TO_GROUND)
            this.terrainPoint = dc.computeTerrainPoint(pos.getLatitude(), pos.getLongitude(), 0);

        // Compute the placemark point's screen location.
        this.screenPoint = dc.getView().project(this.placePoint);
        this.eyeDistance = this.placePoint.distanceTo3(dc.getView().getEyePoint());
    }

    /**
     * Computes the screen-space rectangle bounding the placemark image.
     *
     * @param dc the current draw context.
     *
     * @return the bounding rectangle.
     */
    protected Rectangle computeImageRectangle(DrawContext dc)
    {
        double s = this.getActiveAttributes().getScale() != null ? this.getActiveAttributes().getScale() : 1;

        double width = s * (this.activeTexture != null ? this.activeTexture.getWidth(dc) : 1);
        double height = s * (this.activeTexture != null ? this.activeTexture.getHeight(dc) : 1);

        double x = this.screenPoint.x + (this.isDrawPoint(dc) ? -0.5 * s : this.dx);
        double y = this.screenPoint.y + (this.isDrawPoint(dc) ? -0.5 * s : this.dy);

        return new Rectangle((int) x, (int) y, (int) Math.ceil(width), (int) Math.ceil(height));
    }

    protected void computeImageOffset(DrawContext dc)
    {
        // Determine the screen-space offset needed to align the image hot spot with the placemark point.
        this.dx = 0;
        this.dy = 0;

        if (this.isDrawPoint(dc))
            return;

        Offset os = this.getActiveAttributes().getImageOffset();
        if (os == null)
            return;

        double w = this.activeTexture != null ? this.activeTexture.getWidth(dc) : 1;
        double h = this.activeTexture != null ? this.activeTexture.getHeight(dc) : 1;
        Point.Double offset = os.computeOffset(w, h,
            this.getActiveAttributes().getScale(), this.getActiveAttributes().getScale());

        this.dx = -offset.x;
        this.dy = -offset.y;
    }

    /** {@inheritDoc} */
    public String isExportFormatSupported(String format)
    {
        if (KMLConstants.KML_MIME_TYPE.equalsIgnoreCase(format))
            return Exportable.FORMAT_SUPPORTED;
        else
            return Exportable.FORMAT_NOT_SUPPORTED;
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.getPosition();
    }

    /** {@inheritDoc} */
    public void move(Position delta)
    {
        if (delta == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position refPos = this.getReferencePosition();

        // The reference position is null if this shape has positions. With PointPlacemark, this should never happen
        // because its position must always be non-null. We check and this case anyway to handle a subclass overriding
        // getReferencePosition and returning null. In this case moving the shape by a relative delta is meaningless
        // because the shape has no geographic location. Therefore we fail softly by exiting and doing nothing.
        if (refPos == null)
            return;

        this.moveTo(refPos.add(delta));
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.setPosition(position);
    }

    /**
     * Export the Placemark. The {@code output} object will receive the exported data. The type of this object depends
     * on the export format. The formats and object types supported by this class are:
     * <p/>
     * <pre>
     * Format                                         Supported output object types
     * ================================================================================
     * KML (application/vnd.google-earth.kml+xml)     java.io.Writer
     *                                                java.io.OutputStream
     *                                                javax.xml.stream.XMLStreamWriter
     * </pre>
     *
     * @param mimeType MIME type of desired export format.
     * @param output   An object that will receive the exported data. The type of this object depends on the export
     *                 format (see above).
     *
     * @throws IOException If an exception occurs writing to the output object.
     */
    public void export(String mimeType, Object output) throws IOException
    {
        if (mimeType == null)
        {
            String message = Logging.getMessage("nullValue.Format");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (output == null)
        {
            String message = Logging.getMessage("nullValue.OutputBufferIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (KMLConstants.KML_MIME_TYPE.equalsIgnoreCase(mimeType))
        {
            try
            {
                exportAsKML(output);
            }
            catch (XMLStreamException e)
            {
                Logging.logger().throwing(getClass().getName(), "export", e);
                throw new IOException(e);
            }
        }
        else
        {
            String message = Logging.getMessage("Export.UnsupportedFormat", mimeType);
            Logging.logger().warning(message);
            throw new UnsupportedOperationException(message);
        }
    }

    /**
     * Export the placemark to KML as a {@code <Placemark>} element. The {@code output} object will receive the data.
     * This object must be one of: java.io.Writer java.io.OutputStream javax.xml.stream.XMLStreamWriter
     *
     * @param output Object to receive the generated KML.
     *
     * @throws XMLStreamException If an exception occurs while writing the KML
     * @throws IOException        if an exception occurs while exporting the data.
     * @see #export(String, Object)
     */
    protected void exportAsKML(Object output) throws IOException, XMLStreamException
    {
        XMLStreamWriter xmlWriter = null;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        boolean closeWriterWhenFinished = true;

        if (output instanceof XMLStreamWriter)
        {
            xmlWriter = (XMLStreamWriter) output;
            closeWriterWhenFinished = false;
        }
        else if (output instanceof Writer)
        {
            xmlWriter = factory.createXMLStreamWriter((Writer) output);
        }
        else if (output instanceof OutputStream)
        {
            xmlWriter = factory.createXMLStreamWriter((OutputStream) output);
        }

        if (xmlWriter == null)
        {
            String message = Logging.getMessage("Export.UnsupportedOutputObject");
            Logging.logger().warning(message);
            throw new IllegalArgumentException(message);
        }

        xmlWriter.writeStartElement("Placemark");
        xmlWriter.writeStartElement("name");
        xmlWriter.writeCharacters(this.getLabelText());
        xmlWriter.writeEndElement();

        xmlWriter.writeStartElement("visibility");
        xmlWriter.writeCharacters(kmlBoolean(this.isVisible()));
        xmlWriter.writeEndElement();

        String shortDescription = (String) getValue(AVKey.SHORT_DESCRIPTION);
        if (shortDescription != null)
        {
            xmlWriter.writeStartElement("Snippet");
            xmlWriter.writeCharacters(shortDescription);
            xmlWriter.writeEndElement();
        }

        String description = (String) getValue(AVKey.BALLOON_TEXT);
        if (description != null)
        {
            xmlWriter.writeStartElement("description");
            xmlWriter.writeCharacters(description);
            xmlWriter.writeEndElement();
        }

        final PointPlacemarkAttributes normalAttributes = getAttributes();
        final PointPlacemarkAttributes highlightAttributes = getHighlightAttributes();

        // Write style map
        if (normalAttributes != null || highlightAttributes != null)
        {
            xmlWriter.writeStartElement("StyleMap");
            exportAttributesAsKML(xmlWriter, KMLConstants.NORMAL, normalAttributes);
            exportAttributesAsKML(xmlWriter, KMLConstants.HIGHLIGHT, highlightAttributes);
            xmlWriter.writeEndElement(); // StyleMap
        }

        // Write geometry
        xmlWriter.writeStartElement("Point");

        xmlWriter.writeStartElement("extrude");
        xmlWriter.writeCharacters(kmlBoolean(isLineEnabled()));
        xmlWriter.writeEndElement();

        final String altitudeMode = KMLExportUtil.kmlAltitudeMode(getAltitudeMode());
        xmlWriter.writeStartElement("altitudeMode");
        xmlWriter.writeCharacters(altitudeMode);
        xmlWriter.writeEndElement();

        final String coordString = String.format(Locale.US, "%f,%f,%f",
            position.getLongitude().getDegrees(),
            position.getLatitude().getDegrees(),
            position.getElevation());
        xmlWriter.writeStartElement("coordinates");
        xmlWriter.writeCharacters(coordString);
        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement(); // Point
        xmlWriter.writeEndElement(); // Placemark

        xmlWriter.flush();
        if (closeWriterWhenFinished)
            xmlWriter.close();
    }

    /**
     * Export PointPlacemarkAttributes as KML Style element.
     *
     * @param xmlWriter  Writer to receive the Style element.
     * @param styleType  The type of style: normal or highlight. Value should match either {@link KMLConstants#NORMAL}
     *                   or {@link KMLConstants#HIGHLIGHT}
     * @param attributes Attributes to export. The method takes no action if this parameter is null.
     *
     * @throws XMLStreamException if exception occurs writing XML.
     * @throws IOException        if exception occurs exporting data.
     */
    private void exportAttributesAsKML(XMLStreamWriter xmlWriter, String styleType, PointPlacemarkAttributes attributes)
        throws XMLStreamException, IOException
    {
        if (attributes != null)
        {
            xmlWriter.writeStartElement("Pair");
            xmlWriter.writeStartElement("key");
            xmlWriter.writeCharacters(styleType);
            xmlWriter.writeEndElement();

            attributes.export(KMLConstants.KML_MIME_TYPE, xmlWriter);
            xmlWriter.writeEndElement(); // Pair
        }
    }
}