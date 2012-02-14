/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import com.sun.opengl.util.j2d.TextRenderer;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.geom.*;

/**
 * A label drawn as part of a tactical graphic. The label is drawn at constant screen size. The label can include
 * multiple lines of text, and can optionally be kept aligned with features on the globe. To align a label with the
 * globe specify an {@link #setOrientationPosition(gov.nasa.worldwind.geom.Position) orientationPosition} for the label.
 * The label will be drawn along a line connecting the label's position to the orientation position.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class TacticalGraphicLabel implements OrderedRenderable
{
    /** Default font. */
    public static final Font DEFAULT_FONT = Font.decode("Arial-BOLD-16");
    /**
     * Default offset. The default offset aligns the label horizontal with the text alignment position, and centers the
     * label vertically. For example, if the text alignment is <code>AVKey.LEFT</code>, then the left edge of the text
     * will be aligned with the geographic position, and the label will be centered vertically.
     */
    public static final Offset DEFAULT_OFFSET = new Offset(0d, -0.5d, AVKey.FRACTION, AVKey.FRACTION);

    /** Label text. */
    protected String text;
    /** The label's geographic position. */
    protected Position position;
    /** Offset from the geographic position at which to draw the label. */
    protected Offset offset = DEFAULT_OFFSET;
    /** Text alignment for multi-line labels. */
    protected String textAlign = AVKey.LEFT;
    /** The label is drawn along a line from the label position to the orientation position. */
    protected Position orientationPosition;

    /** Material used to draw the label. */
    protected Material material = Material.BLACK;
    /** Opacity of the text, as a value between 0 and 1. */
    protected double opacity = 1.0;
    /** Font used to draw the label. */
    protected Font font = DEFAULT_FONT;
    /** Space (in pixels) between lines in a multi-line label. */
    protected int lineSpacing = 5; // TODO compute default based on font size

    /** Indicates whether or not batch rendering is enabled. */
    protected boolean enableBatchRendering = false;
    /** Indicates whether or not batch picking is enabled. */
    protected boolean enableBatchPicking = true;

    /** Indicates an object that represents the label during picking. */
    protected Object delegateOwner;

    // Computed each frame
    protected long frameTimestamp = -1L;
    /** Geographic position in cartesian coordinates. */
    protected Vec4 placePoint;
    /** Location of the place point projected onto the screen. */
    protected Vec4 screenPlacePoint;
    /**
     * Location of the upper left corner of the text measured from the lower left corner of the viewport. This point in
     * OGL coordinates.
     */
    protected Point screenPoint;
    /** Rotation applied to the label. This is computed each frame based on the orientation position. */
    protected Angle rotation;
    /** Size of the label. */
    protected Rectangle2D bounds;
    /** Extent of the label on the screen. */
    protected Rectangle screenExtent;
    /** Distance from the eye point to the label's geographic location. */
    protected double eyeDistance;

    /** Stack handler used for beginDrawing/endDrawing state. */
    protected OGLStackHandler BEogsh = new OGLStackHandler();
    /** Support object used during picking. */
    protected PickSupport pickSupport = new PickSupport();
    /** Active layer. */
    protected Layer pickLayer;

    /**
     * Indicates the text of this label.
     *
     * @return The label's text.
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * Specifies the text of this label. The text may include multiple lines, separated by newline characters.
     *
     * @param text New text.
     */
    public void setText(String text)
    {
        this.text = text;
        this.bounds = null; // Need to recompute
    }

    /**
     * Indicates the label's position. The label is drawn at an offset from this position.
     *
     * @return The label's geographic position.
     *
     * @see #getOffset()
     */
    public Position getPosition()
    {
        return this.position;
    }

    /**
     * Indicates the label's geographic position. The label is drawn at an offset from this position.
     *
     * @param position New position.
     *
     * @see #getOffset()
     */
    public void setPosition(Position position)
    {
        this.position = position;
    }

    /**
     * Indicates the current text alignment. Can be one of {@link AVKey#LEFT} (default), {@link AVKey#CENTER} or {@link
     * AVKey#RIGHT}.
     *
     * @return the current text alignment.
     */
    public String getTextAlign()
    {
        return this.textAlign;
    }

    /**
     * Specifies the text alignment. Can be one of {@link AVKey#LEFT} (default), {@link AVKey#CENTER}, or {@link
     * AVKey#RIGHT}.
     *
     * @param textAlign New text alignment.
     */
    public void setTextAlign(String textAlign)
    {
        if (textAlign == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.textAlign = textAlign;
    }

    /**
     * Indicates the offset from the geographic position at which to draw the label. See {@link
     * #setOffset(gov.nasa.worldwind.render.Offset) setOffset} for more information on how the offset is interpreted.
     *
     * @return The offset at which to draw the label.
     */
    public Offset getOffset()
    {
        return this.offset;
    }

    /**
     * Specifies the offset from the geographic position at which to draw the label. The default offset aligns the label
     * horizontal with the text alignment position, and centers the label vertically. For example, if the text alignment
     * is <code>AVKey.LEFT</code>., then the left edge of the text will be aligned with the geographic position, and the
     * label will be centered vertically.
     * <p/>
     * When the text is rotated a horizontal offset moves the text along the orientation line, and a vertical offset
     * moves the text perpendicular to the orientation line.
     *
     * @param offset The offset at which to draw the label.
     */
    public void setOffset(Offset offset)
    {
        if (offset == null)
        {
            String message = Logging.getMessage("nullValue.OffsetIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.offset = offset;
    }

    /**
     * Indicates the font used to draw the label.
     *
     * @return The label's font.
     */
    public Font getFont()
    {
        return this.font;
    }

    /**
     * Specifies the font used to draw the label.
     *
     * @param font New font.
     */
    public void setFont(Font font)
    {
        if (font == null)
        {
            String message = Logging.getMessage("nullValue.FontIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (font != this.font)
        {
            this.font = font;
            this.bounds = null; // Need to recompute
        }
    }

    /**
     * Indicates the line spacing applied to multi-line labels.
     *
     * @return The space (in pixels) between lines of a multi-line label.
     */
    public int getLineSpacing()
    {
        return lineSpacing;
    }

    /**
     * Specifies the line spacing applied to multi-line labels.
     *
     * @param lineSpacing New line spacing.
     */
    public void setLineSpacing(int lineSpacing)
    {
        if (lineSpacing < 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.lineSpacing = lineSpacing;
    }

    /**
     * Indicates the material used to draw the label.
     *
     * @return The label's material.
     */
    public Material getMaterial()
    {
        return this.material;
    }

    /**
     * Specifies the material used to draw the label.
     *
     * @param material New material.
     */
    public void setMaterial(Material material)
    {
        if (material == null)
        {
            String message = Logging.getMessage("nullValue.MaterialIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.material = material;
    }

    /**
     * Indicates the opacity of the text as a floating-point value in the range 0.0 to 1.0. A value of 1.0 specifies a
     * completely opaque text, and 0.0 specifies a completely transparent text. Values in between specify a partially
     * transparent text.
     *
     * @return the opacity of the text as a floating-point value from 0.0 to 1.0.
     */
    public double getOpacity()
    {
        return this.opacity;
    }

    /**
     * Specifies the opacity of the text as a floating-point value in the range 0.0 to 1.0. A value of 1.0 specifies a
     * completely opaque text, and 0.0 specifies a completely transparent text. Values in between specify a partially
     * transparent text.
     *
     * @param opacity the opacity of text as a floating-point value from 0.0 to 1.0.
     *
     * @throws IllegalArgumentException if <code>opacity</code> is less than 0.0 or greater than 1.0.
     */
    public void setOpacity(double opacity)
    {
        if (opacity < 0 || opacity > 1)
        {
            String message = Logging.getMessage("generic.OpacityOutOfRange", opacity);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.opacity = opacity;
    }

    /**
     * Indicates the orientation position. The label oriented on a line drawn from the label's position to the
     * orientation position.
     *
     * @return Position used to orient the label. May be null.
     */
    public Position getOrientationPosition()
    {
        return this.orientationPosition;
    }

    /**
     * Specifies the orientation position. The label is oriented on a line drawn from the label's position to the
     * orientation position. If the orientation position is null then the label is drawn with no rotation.
     *
     * @param orientationPosition Draw label oriented toward this position.
     */
    public void setOrientationPosition(Position orientationPosition)
    {
        this.orientationPosition = orientationPosition;
    }

    /**
     * Returns the delegate owner of this label. If non-null, the returned object replaces the label as the pickable
     * object returned during picking. If null, the label itself is the pickable object returned during picking.
     *
     * @return the object used as the pickable object returned during picking, or null to indicate the the label is
     *         returned during picking.
     */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /**
     * Specifies the delegate owner of this label. If non-null, the delegate owner replaces the label as the pickable
     * object returned during picking. If null, the label itself is the pickable object returned during picking.
     *
     * @param owner the object to use as the pickable object returned during picking, or null to return the label.
     */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
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
        return this.enableBatchPicking;
    }

    /**
     * Specifies whether adjacent Labels in the ordered renderable list may be pick-tested together if they are
     * contained in the same layer. This increases performance but allows only the top-most of the label to be reported
     * in a {@link gov.nasa.worldwind.event.SelectEvent} even if several of the labels are at the pick position.
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
     * Indicates whether batch rendering is enabled.
     *
     * @return true if batch rendering is enabled, otherwise false.
     *
     * @see #setEnableBatchRendering(boolean).
     */
    public boolean isEnableBatchRendering()
    {
        return this.enableBatchRendering;
    }

    /**
     * Specifies whether adjacent Labels in the ordered renderable list may be rendered together if they are contained
     * in the same layer. This increases performance and there is seldom a reason to disable it.
     *
     * @param enableBatchRendering true to enable batch rendering, otherwise false.
     */
    public void setEnableBatchRendering(boolean enableBatchRendering)
    {
        this.enableBatchRendering = enableBatchRendering;
    }

    /**
     * Compute the label's screen position from its geographic position.
     *
     * @param dc Current draw context.
     */
    protected void computeGeometry(DrawContext dc)
    {
        // Project the label position onto the viewport
        Position pos = this.getPosition();
        if (pos == null)
            return;

        this.placePoint = dc.computeTerrainPoint(pos.getLatitude(), pos.getLongitude(), 0);
        this.screenPlacePoint = dc.getView().project(this.placePoint);

        this.eyeDistance = this.placePoint.distanceTo3(dc.getView().getEyePoint());

        boolean orientationReversed = false;
        if (this.orientationPosition != null)
        {
            // Project the orientation point onto the screen
            Vec4 orientationPlacePoint = dc.computeTerrainPoint(this.orientationPosition.getLatitude(),
                this.orientationPosition.getLongitude(), 0);
            Vec4 orientationScreenPoint = dc.getView().project(orientationPlacePoint);

            this.rotation = this.computeRotation(this.screenPlacePoint, orientationScreenPoint);

            // The orientation is reversed if the orientation point falls to the right of the screen point. Text is
            // never drawn upside down, so when the orientation is reversed the text flips vertically to keep the text
            // right side up.
            orientationReversed = (orientationScreenPoint.x <= this.screenPlacePoint.x);
        }

        TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(),
            this.getFont());
        MultiLineTextRenderer mltr = new MultiLineTextRenderer(textRenderer);
        mltr.setLineSpacing(this.getLineSpacing());

        // Compute bounds if they are not available. Computing text bounds is expensive, so only do this
        // calculation if necessary.
        if (this.bounds == null)
        {
            this.bounds = this.getMultilineTextBounds(this.text, textRenderer);
        }

        Offset offset = this.getOffset();
        Point2D offsetPoint = offset.computeOffset(this.bounds.getWidth(), this.bounds.getHeight(), null, null);

        // If a rotation is applied to the text, then rotate the offset as well. An offset in the x direction
        // will move the text along the orientation line, and a offset in the y direction will move the text
        // perpendicular to the orientation line.
        if (this.rotation != null)
        {
            double dy = offsetPoint.getY();

            // If the orientation is reversed we need to adjust the vertical offset to compensate for the flipped
            // text. For example, if the offset normally aligns the top of the text with the place point then without
            // this adjustment the bottom of the text would align with the place point when the orientation is
            // reversed.
            if (orientationReversed)
            {
                dy = -(dy + this.bounds.getHeight());
            }

            Vec4 pOffset = new Vec4(offsetPoint.getX(), dy);
            Matrix rot = Matrix.fromRotationZ(this.rotation.multiply(-1));

            pOffset = pOffset.transformBy3(rot);

            offsetPoint = new Point((int) pOffset.getX(), (int) pOffset.getY());
        }

        int x = (int) (this.screenPlacePoint.x + offsetPoint.getX());
        int y = (int) (this.screenPlacePoint.y - offsetPoint.getY());

        this.screenPoint = new Point(x, y);
        this.screenExtent = this.computeTextExtent(x, y, this.rotation);
    }

    /**
     * Determine if this label intersects the view or pick frustum.
     *
     * @param dc Current draw context.
     *
     * @return True if this label intersects the active frustum (view or pick). Otherwise false.
     */
    protected boolean intersectsFrustum(DrawContext dc)
    {
        View view = dc.getView();
        Frustum frustum = view.getFrustumInModelCoordinates();

        // Test the label's model coordinate point against the near and far clipping planes.
        if (this.placePoint != null
            && (frustum.getNear().distanceTo(this.placePoint) < 0
            || frustum.getFar().distanceTo(this.placePoint) < 0))
        {
            return false;
        }

        if (dc.isPickingMode())
            return dc.getPickFrustums().intersectsAny(this.screenExtent);
        else
            return view.getViewport().intersects(this.screenExtent);
    }

    /**
     * Compute the amount of rotation to apply to a label in order to keep it oriented toward its orientation position.
     *
     * @param screenPoint            Geographic position of the text, projected onto the screen.
     * @param orientationScreenPoint Orientation position, projected onto the screen.
     *
     * @return The rotation angle to apply when drawing the label.
     */
    protected Angle computeRotation(Vec4 screenPoint, Vec4 orientationScreenPoint)
    {
        // Determine delta between the orientation position and the label position
        double deltaX = screenPoint.x - orientationScreenPoint.x;
        double deltaY = screenPoint.y - orientationScreenPoint.y;

        if (deltaX != 0)
        {
            double angle = Math.atan(deltaY / deltaX);
            return Angle.fromRadians(angle);
        }
        else
        {
            return Angle.POS90; // Vertical label
        }
    }

    /** {@inheritDoc} */
    public double getDistanceFromEye()
    {
        return this.eyeDistance;
    }

    /** {@inheritDoc} */
    public void render(DrawContext dc)
    {
        // This render method is called three times during frame generation. It's first called as a Renderable
        // during Renderable picking. It's called again during normal rendering. And it's called a third
        // time as an OrderedRenderable. The first two calls determine whether to add the label the ordered renderable
        // list during pick and render. The third call just draws the ordered renderable.

        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (dc.isOrderedRenderingMode())
            this.drawOrderedRenderable(dc);
        else
            this.makeOrderedRenderable(dc);
    }

    /** {@inheritDoc} */
    public void pick(DrawContext dc, Point pickPoint)
    {
        // This method is called only when ordered renderables are being drawn.
        // Arg checked within call to render.

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

    /**
     * Draws the graphic as an ordered renderable.
     *
     * @param dc the current draw context.
     */
    protected void makeOrderedRenderable(DrawContext dc)
    {
        if (this.text == null || this.position == null)
            return;

        long timestamp = dc.getFrameTimeStamp();
        if (this.frameTimestamp != timestamp)
        {
            this.computeGeometry(dc);
            this.frameTimestamp = timestamp;
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

    /**
     * Draws the graphic as an ordered renderable.
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
     * Draw this label during ordered rendering.
     *
     * @param dc          Current draw context.
     * @param pickSupport Support object used during picking.
     */
    protected void doDrawOrderedRenderable(DrawContext dc, PickSupport pickSupport)
    {
        TextRenderer textRenderer = OGLTextRenderer.getOrCreateTextRenderer(dc.getTextRendererCache(), font);
        MultiLineTextRenderer mltr = new MultiLineTextRenderer(textRenderer);
        mltr.setLineSpacing(this.getLineSpacing());

        if (dc.isPickingMode())
        {
            this.doPick(dc, mltr, pickSupport);
        }
        else
        {
            this.drawText(dc, mltr);
        }
    }

    /**
     * Establish the OpenGL state needed to draw text.
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
                | GL.GL_ENABLE_BIT; // for enable/disable changes

        this.BEogsh.pushAttrib(gl, attrMask);

        if (!dc.isPickingMode())
        {
            gl.glEnable(GL.GL_BLEND);
            OGLUtil.applyBlending(gl, false);
        }

        // Do not depth buffer the label. (Labels beyond the horizon are culled above.)
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDepthMask(false);

        // The image is drawn using a parallel projection.
        this.BEogsh.pushProjectionIdentity(gl);
        gl.glOrtho(0d, dc.getView().getViewport().width, 0d, dc.getView().getViewport().height, -1d, 1d);

        this.BEogsh.pushModelviewIdentity(gl);
    }

    /**
     * Pop the state set in beginDrawing.
     *
     * @param dc the current draw context.
     */
    protected void endDrawing(DrawContext dc)
    {
        this.BEogsh.pop(dc.getGL());
    }

    /**
     * Draw labels for picking.
     *
     * @param dc          Current draw context.
     * @param mltr        Text rendered used to draw the labels.
     * @param pickSupport the PickSupport instance to be used.
     */
    protected void doPick(DrawContext dc, MultiLineTextRenderer mltr, PickSupport pickSupport)
    {
        GL gl = dc.getGL();

        mltr.setTextAlign(this.textAlign);

        Angle heading = this.rotation;

        double headingDegrees;
        if (heading != null)
            headingDegrees = heading.degrees;
        else
            headingDegrees = 0;

        int x = this.screenPoint.x;
        int y = this.screenPoint.y;

        boolean matrixPushed = false;
        try
        {
            if (headingDegrees != 0)
            {
                gl.glPushMatrix();
                matrixPushed = true;

                gl.glTranslated(x, y, 0);
                gl.glRotated(headingDegrees, 0, 0, 1);
                gl.glTranslated(-x, -y, 0);
            }

            mltr.pick(this.text, x, y, mltr.getLineHeight(), dc, pickSupport, this.getPickedObject(), this.position);
        }
        finally
        {
            if (matrixPushed)
            {
                gl.glPopMatrix();
            }
        }
    }

    /**
     * Draw the label's text. This method sets up the text renderer, and then calls {@link
     * #doDrawText(gov.nasa.worldwind.render.MultiLineTextRenderer) doDrawText} to actually draw the text.
     *
     * @param dc   Current draw context.
     * @param mltr Text renderer.
     */
    protected void drawText(DrawContext dc, MultiLineTextRenderer mltr)
    {
        GL gl = dc.getGL();
        TextRenderer textRenderer = mltr.getTextRenderer();

        Angle heading = this.rotation;

        double headingDegrees;
        if (heading != null)
            headingDegrees = heading.degrees;
        else
            headingDegrees = 0;

        boolean matrixPushed = false;
        try
        {
            int x = this.screenPoint.x;
            int y = this.screenPoint.y;

            if (headingDegrees != 0)
            {
                gl.glPushMatrix();
                matrixPushed = true;

                gl.glTranslated(x, y, 0);
                gl.glRotated(headingDegrees, 0, 0, 1);
                gl.glTranslated(-x, -y, 0);
            }

            textRenderer.begin3DRendering();
            try
            {
                this.doDrawText(mltr);

                // Draw other labels that share the same text renderer configuration, if possible.
                if (this.isEnableBatchRendering())
                    this.drawBatchedText(dc, mltr);
            }
            finally
            {
                textRenderer.end3DRendering();
            }
        }
        finally
        {
            if (matrixPushed)
            {
                gl.glPopMatrix();
            }
        }
    }

    /**
     * Draw the label's text. This method assumes that the text renderer context has already been set up.
     *
     * @param mltr Text renderer to use.
     */
    protected void doDrawText(MultiLineTextRenderer mltr)
    {
        TextRenderer textRenderer = mltr.getTextRenderer();
        mltr.setTextAlign(this.textAlign);

        Color color = this.material.getDiffuse();
        Color backgroundColor = this.computeBackgroundColor(color);
        float opacity = (float) this.getOpacity();

        int x = this.screenPoint.x;
        int y = this.screenPoint.y;

        float[] compArray = new float[3];
        if (backgroundColor != null)
        {
            backgroundColor.getRGBColorComponents(compArray);

            textRenderer.setColor(compArray[0], compArray[1], compArray[2], opacity);
            mltr.draw(this.text, x + 1, y - 1);
        }

        color.getRGBColorComponents(compArray);
        textRenderer.setColor(compArray[0], compArray[1], compArray[2], opacity);
        mltr.draw(this.text, x, y);
    }

    /**
     * Get the bounds of a multi-line text string. Each newline character in the input string (\n) indicates the start
     * of a new line.
     *
     * @param text         Text to find bounds of.
     * @param textRenderer Text renderer to use to compute bounds.
     *
     * @return A rectangle that describes the node bounds. See com.sun.opengl.util.j2d.TextRenderer.getBounds for
     *         information on how this rectangle should be interpreted.
     */
    protected Rectangle2D getMultilineTextBounds(String text, TextRenderer textRenderer)
    {
        int width = 0;
        int maxLineHeight = 0;
        String[] lines = text.split("\n");

        for (String line : lines)
        {
            Rectangle2D lineBounds = textRenderer.getBounds(line);
            width = (int) Math.max(lineBounds.getWidth(), width);

            double thisLineHeight = Math.abs(lineBounds.getY());
            maxLineHeight = (int) Math.max(thisLineHeight, maxLineHeight);
        }

        // Compute final height using maxLineHeight and number of lines
        return new Rectangle(lines.length, maxLineHeight, width,
            lines.length * maxLineHeight + lines.length * this.lineSpacing);
    }

    /**
     * Draws this ordered renderable and all subsequent Label ordered renderables in the ordered renderable list. This
     * method differs from {@link #drawBatchedText(gov.nasa.worldwind.render.DrawContext,
     * gov.nasa.worldwind.render.MultiLineTextRenderer) drawBatchedText} in that this method re-initializes the text
     * renderer to draw the next label, while {@code drawBatchedText} re-uses the active text renderer context. That is,
     * {@code drawBatchedText} attempts to draw as many labels as possible that share same text renderer configuration
     * as this label, and this method attempts to draw as many labels as possible regardless of the text renderer
     * configuration of the subsequent labels.
     *
     * @param dc the current draw context.
     */
    protected void drawBatched(DrawContext dc)
    {
        // Draw as many as we can in a batch to save ogl state switching.
        Object nextItem = dc.peekOrderedRenderables();

        if (!dc.isPickingMode())
        {
            while (nextItem != null && nextItem instanceof TacticalGraphicLabel)
            {
                TacticalGraphicLabel nextLabel = (TacticalGraphicLabel) nextItem;
                if (!nextLabel.isEnableBatchRendering())
                    break;

                dc.pollOrderedRenderables(); // take it off the queue
                nextLabel.doDrawOrderedRenderable(dc, this.pickSupport);

                nextItem = dc.peekOrderedRenderables();
            }
        }
        else if (this.isEnableBatchPicking())
        {
            while (nextItem != null && nextItem instanceof TacticalGraphicLabel)
            {
                TacticalGraphicLabel nextLabel = (TacticalGraphicLabel) nextItem;
                if (!nextLabel.isEnableBatchRendering() || !nextLabel.isEnableBatchPicking())
                    break;

                if (nextLabel.pickLayer != this.pickLayer) // batch pick only within a single layer
                    break;

                dc.pollOrderedRenderables(); // take it off the queue
                nextLabel.doDrawOrderedRenderable(dc, this.pickSupport);

                nextItem = dc.peekOrderedRenderables();
            }
        }
    }

    /**
     * Draws text for subsequent Label ordered renderables in the ordered renderable list. This method is called after
     * the text renderer has been set up (after beginRendering has been called), so this method can only draw text for
     * subsequent labels that use the same font and rotation as this label. This method differs from {@link
     * #drawBatched(gov.nasa.worldwind.render.DrawContext) drawBatched} in that this method reuses the active text
     * renderer context to draw as many labels as possible without switching text renderer state.
     *
     * @param dc   the current draw context.
     * @param mltr Text renderer used to draw the label.
     */
    protected void drawBatchedText(DrawContext dc, MultiLineTextRenderer mltr)
    {
        // Draw as many as we can in a batch to save ogl state switching.
        Object nextItem = dc.peekOrderedRenderables();

        if (!dc.isPickingMode())
        {
            while (nextItem != null && nextItem instanceof TacticalGraphicLabel)
            {
                TacticalGraphicLabel nextLabel = (TacticalGraphicLabel) nextItem;
                if (!nextLabel.isEnableBatchRendering())
                    break;

                boolean sameFont = this.font.equals(nextLabel.getFont());
                boolean sameRotation = (this.rotation == null && nextLabel.rotation == null)
                    || (this.rotation != null && this.rotation.equals(nextLabel.rotation));

                // We've already set up the text renderer state, so we can can't change the font or text rotation.
                if (!sameFont || !sameRotation)
                    break;

                dc.pollOrderedRenderables(); // take it off the queue
                nextLabel.doDrawText(mltr);

                nextItem = dc.peekOrderedRenderables();
            }
        }
    }

    /**
     * Indicates the object that represents this label during picking.
     *
     * @return If a delegate owner is set, returns the delegate owner. Otherwise returns this label.
     */
    protected Object getPickedObject()
    {
        Object owner = this.getDelegateOwner();
        return (owner != null) ? owner : this;
    }

    /**
     * Determine the screen rectangle covered by a label. The input coordinate identifies either the top left, top
     * center, or top right corner of the label, depending on the text alignment. If the label is rotated to align with
     * features on the surface then the extent will be the smallest screen rectangle that completely encloses the
     * rotated label.
     *
     * @param x        X coordinate at which to draw the label.
     * @param y        Y coordinate at which to draw the label.
     * @param rotation Label rotation.
     *
     * @return The rectangle, in OGL screen coordinates (origin at bottom left corner), that is covered by the label.
     */
    protected Rectangle computeTextExtent(int x, int y, Angle rotation)
    {
        double width = this.bounds.getWidth();
        double height = this.bounds.getHeight();

        String textAlign = this.getTextAlign();

        int xAligned = x;
        if (AVKey.CENTER.equals(textAlign))
            xAligned = x - (int) (width / 2);
        else if (AVKey.RIGHT.equals(textAlign))
            xAligned = x - (int) width;

        int yAligned = (int) (y - height);

        Rectangle screenRect = new Rectangle(xAligned, yAligned, (int) width, (int) height);

        // Compute bounds of the rotated rectangle, if there is a rotation angle.
        if (rotation != null && rotation.degrees != 0)
        {
            screenRect = this.computeRotatedScreenExtent(screenRect, x, y, rotation);
        }

        return screenRect;
    }

    /**
     * Compute the bounding screen extent of a rotated rectangle.
     *
     * @param rect     Rectangle to rotate.
     * @param x        X coordinate of the rotation point.
     * @param y        Y coordinate of the rotation point.
     * @param rotation Rotation angle.
     *
     * @return The smallest rectangle that completely contains {@code rect} when rotated by the specified angle.
     */
    protected Rectangle computeRotatedScreenExtent(Rectangle rect, int x, int y, Angle rotation)
    {
        Rectangle r = new Rectangle(rect);

        // Translate the rectangle to the rotation point.
        r.translate(-x, -y);

        // Compute corner points
        Vec4[] corners = {
            new Vec4(r.getMaxX(), r.getMaxY()),
            new Vec4(r.getMaxX(), r.getMinY()),
            new Vec4(r.getMinX(), r.getMaxY()),
            new Vec4(r.getMinX(), r.getMinY())
        };

        // Rotate the rectangle
        Matrix rotationMatrix = Matrix.fromRotationZ(rotation);
        for (int i = 0; i < corners.length; i++)
        {
            corners[i] = corners[i].transformBy3(rotationMatrix);
        }

        // Find the bounding rectangle of rotated points.
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = -Integer.MAX_VALUE;
        int maxY = -Integer.MAX_VALUE;

        for (Vec4 v : corners)
        {
            if (v.x > maxX)
                maxX = (int) v.x;

            if (v.x < minX)
                minX = (int) v.x;

            if (v.y > maxY)
                maxY = (int) v.y;

            if (v.y < minY)
                minY = (int) v.y;
        }

        // Set bounds and translate the rectangle back to where it started.
        r.setBounds(minX, minY, maxX - minX, maxY - minY);
        r.translate(x, y);

        return r;
    }

    /**
     * Compute a contrasting background color to draw the label's outline.
     *
     * @param color Label color.
     *
     * @return A color that contrasts with {@code color}.
     */
    protected Color computeBackgroundColor(Color color)
    {
        float[] colorArray = new float[4];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), colorArray);

        if (colorArray[2] > 0.5)
            return new Color(0, 0, 0, 0.7f);
        else
            return new Color(1, 1, 1, 0.7f);
    }
}
