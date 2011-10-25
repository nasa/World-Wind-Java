/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwindx.examples.util;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.event.*;

/**
 * ScreenSelector is an application utility that provides interactive screen rectangle selection with visual feedback.
 * The screen rectangle creating by using ScreenSelector is displayed on a layer, and is used as the WorldWindow's
 * selection box. Objects in the selected rectangle can be accessed by either calling the WorldWindow method {@link
 * gov.nasa.worldwind.WorldWindow#getObjectsInSelectionBox()}, or by registering a select listener and responding to
 * BOX_ROLLOVER select events.
 * <p/>
 * <h3>Using ScreenSelector</h3>
 * <p/>
 * To use ScreenSelector in an application, create a new instance of ScreenSelector and specify the application's
 * WorldWindow as the sole parameter. The ScreenSelector registers itself as a mouse listener on the WorldWindow's input
 * handler. When the user wants to define a screen selection, call {@link #enable} and the ScreenSelector then
 * translates mouse events to changes in the screen selection and sets the WorldWindow's selection box to the selected
 * rectangle. The screen selection is displayed as a filled rectangle with a 1-pixel wide border drawn in the
 * ScreenSelector's current interiorColor and borderColor. The ScreenSelector consumes mouse events that it responds to
 * to prevent the World Wind View from responding to those same events. When the user selection is done, call {@link
 * #disable} and the ScreenSelector stops responding to mouse events and sets the WorldWindow's selection box to
 * <code>null</code>.
 * <p/>
 * <h3>User Input</h3>
 * <p/>
 * When ScreenSelector is enabled, pressing the first mouse button and dragging a causes ScreenSelector to display the
 * selection rectangle and set the WorldWindow's pick rectangle to the selected rectangle. Subsequently releasing the
 * first mouse button causes ScreenSelector to stop displaying the selection rectangle, but does not change the
 * WorldWindow's pick rectangle (until the first mouse button is subsequently pressed or the ScreenSelector is
 * disabled). This enables the application to access the user's final selection by calling
 * WorldWindow.getObjectsInSelectionBox. To customize ScreenSelector's response to mouse events, create a subclass of
 * ScreenSelector and override the methods mousePressed, mouseReleased, and mouseDragged.
 * <p/>
 * ScreenSelector translates its raw mouse events to the semantic selection events selectionStarted, selectionEnded, and
 * selectionChanged. To customize how ScreenSelector responds to these semantic events without changing the user input
 * model, create a subclass of ScreenSelector and override any of these methods.
 * <p/>
 * <h3>Screen Rectangle Appearance</h3>
 * <p/>
 * To customize the appearance of the rectangle displayed by ScreenRectangle, call {@link
 * #setInteriorColor(java.awt.Color)} and {@link #setBorderColor(java.awt.Color)} to specify the rectangle's interior
 * and border colors, respectively. Setting either value to <code>null</code> causes ScreenRectangle to use the default
 * values: 25% opaque white interior, 100% opaque white border.
 * <p/>
 * To further customize the displayed rectangle, create a subclass of ScreenSelector, override the method
 * createSelectionRectangle, and return a subclass of the internal class ScreenSelector.SelectionRectangle.
 *
 * @author dcollins
 * @version $Id$
 */
public class ScreenSelector extends WWObjectImpl implements MouseListener, MouseMotionListener
{
    protected static class SelectionRectangle implements OrderedRenderable
    {
        protected static final Color DEFAULT_INTERIOR_COLOR = new Color(255, 255, 255, 64);
        protected static final Color DEFAULT_BORDER_COLOR = Color.WHITE;

        protected Rectangle rect;
        protected Point startPoint;
        protected Point endPoint;
        protected Color interiorColor;
        protected Color borderColor;
        protected OGLStackHandler BEogsh = new OGLStackHandler();

        public SelectionRectangle()
        {
            this.rect = new Rectangle();
            this.startPoint = new Point();
            this.endPoint = new Point();
        }

        public boolean hasSelection()
        {
            return !this.rect.isEmpty();
        }

        public Rectangle getSelection()
        {
            return this.rect;
        }

        public void startSelection(Point point)
        {
            if (point == null)
            {
                String msg = Logging.getMessage("nullValue.PointIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.startPoint.setLocation(point);
            this.endPoint.setLocation(point);
            this.rect.setRect(point.x, point.y, 0, 0);
        }

        public void endSelection(Point point)
        {
            if (point == null)
            {
                String msg = Logging.getMessage("nullValue.PointIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.endPoint.setLocation(point);

            // Compute the selection's extremes along the x axis.
            double minx, maxx;
            if (this.startPoint.x < this.endPoint.x)
            {
                minx = this.startPoint.x;
                maxx = this.endPoint.x;
            }
            else
            {
                minx = this.endPoint.x;
                maxx = this.startPoint.x;
            }

            // Compute the selection's extremes along the y axis. The selection is defined in AWT screen coordinates, so
            // the origin is in the upper left corner and the y axis points down.
            double miny, maxy;
            if (this.startPoint.y < this.endPoint.y)
            {
                miny = this.startPoint.y;
                maxy = this.endPoint.y;
            }
            else
            {
                miny = this.endPoint.y;
                maxy = this.startPoint.y;
            }

            // If only one of the selection rectangle's dimensions is zero, then the selection is either a horizontal or
            // vertical line. In this case, we set the zero dimension to 1 because both dimensions must be nonzero to
            // perform a selection.
            if (minx == maxx && miny < maxy)
                maxx = minx + 1;
            if (miny == maxy && minx < maxx)
                miny = maxy - 1;

            this.rect.setRect(minx, maxy, maxx - minx, maxy - miny);
        }

        public void clearSelection()
        {
            this.startPoint.setLocation(0, 0);
            this.endPoint.setLocation(0, 0);
            this.rect.setRect(0, 0, 0, 0);
        }

        public Color getInteriorColor()
        {
            return this.interiorColor;
        }

        public void setInteriorColor(Color color)
        {
            this.interiorColor = color;
        }

        public Color getBorderColor()
        {
            return this.borderColor;
        }

        public void setBorderColor(Color color)
        {
            this.borderColor = color;
        }

        public double getDistanceFromEye()
        {
            return 0; // Screen rectangle is drawn on top of other ordered renderables, except other screen objects.
        }

        public void pick(DrawContext dc, Point pickPoint)
        {
            // Intentionally left blank. SelectionRectangle is not pickable.
        }

        public void render(DrawContext dc)
        {
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

        protected void makeOrderedRenderable(DrawContext dc)
        {
            if (this.hasSelection())
                dc.addOrderedRenderable(this);
        }

        protected void drawOrderedRenderable(DrawContext dc)
        {
            int attrs = GL.GL_COLOR_BUFFER_BIT // For blend enable, alpha enable, blend func, alpha func.
                | GL.GL_CURRENT_BIT // For current color.
                | GL.GL_DEPTH_BUFFER_BIT; // For depth test disable.

            GL gl = dc.getGL();
            this.BEogsh.pushAttrib(gl, attrs);
            this.BEogsh.pushClientAttrib(gl, GL.GL_VERTEX_ARRAY);
            try
            {
                // Configure the modelview-projection matrix to transform vertex points from screen rectangle
                // coordinates to clip coordinates without any perspective transformation. We offset the rectangle by
                // 0.5 pixels to ensure that the line loop draws a line without a 1-pixel gap between the line's
                // beginning and its end. We scale by (width - 1, height - 1) to ensure that only the actual selected
                // area is filled. If we scaled by (width, height), GL line rasterization would fill one pixel beyond
                // the actual selected area.
                this.BEogsh.pushProjectionIdentity(gl);
                gl.glOrtho(0, dc.getDrawableWidth(), 0, dc.getDrawableHeight(), -1, 1); // l, r, b, t, n, f
                this.BEogsh.pushModelviewIdentity(gl);
                Rectangle r = this.getSelection();
                gl.glTranslated(0.5, 0.5, 0.0);
                gl.glTranslated(r.getMinX(), dc.getDrawableHeight() - r.getMinY(), 0);
                gl.glScaled(r.getWidth() - 1, r.getHeight() - 1, 1);

                // Disable the depth test and enable blending so this screen rectangle appears on top of the existing
                // framebuffer contents.
                gl.glDisable(GL.GL_DEPTH_TEST);
                gl.glEnable(GL.GL_BLEND);
                OGLUtil.applyBlending(gl, false); // SelectionRectangle does not use premultplied colors.

                // Draw this screen rectangle's interior as a filled quadrilateral.
                Color c = this.getInteriorColor() != null ? this.getInteriorColor() : DEFAULT_INTERIOR_COLOR;
                gl.glColor4ub((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue(), (byte) c.getAlpha());
                dc.drawUnitQuad();

                // Draw this screen rectangle's border as a line loop. This assumes the default line width of 1.0.
                c = this.getBorderColor() != null ? this.getBorderColor() : DEFAULT_BORDER_COLOR;
                gl.glColor4ub((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue(), (byte) c.getAlpha());
                dc.drawUnitQuadOutline();
            }
            finally
            {
                this.BEogsh.pop(dc.getGL());
            }
        }
    }

    protected WorldWindow wwd;
    protected Layer layer;
    protected SelectionRectangle screenRect;
    protected boolean armed;

    public ScreenSelector(WorldWindow worldWindow)
    {
        if (worldWindow == null)
        {
            String msg = Logging.getMessage("nullValue.WorldWindow");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.wwd = worldWindow;
        this.layer = this.createLayer();
        this.layer.setPickEnabled(false); // The screen selector is not pickable.
        this.screenRect = this.createSelectionRectangle();
        ((RenderableLayer) this.layer).addRenderable(this.screenRect);
    }

    protected Layer createLayer()
    {
        return new RenderableLayer();
    }

    protected SelectionRectangle createSelectionRectangle()
    {
        return new SelectionRectangle();
    }

    public WorldWindow getWwd()
    {
        return this.wwd;
    }

    public Layer getLayer()
    {
        return this.layer;
    }

    public Color getInteriorColor()
    {
        return this.screenRect.getInteriorColor();
    }

    public void setInteriorColor(Color color)
    {
        this.screenRect.setInteriorColor(color);
    }

    public Color getBorderColor()
    {
        return this.screenRect.getBorderColor();
    }

    public void setBorderColor(Color color)
    {
        this.screenRect.setBorderColor(color);
    }

    public void enable()
    {
        this.screenRect.clearSelection();
        this.getWwd().getSceneController().setPickRectangle(null);

        LayerList layers = this.getWwd().getModel().getLayers();

        if (!layers.contains(this.getLayer()))
            layers.add(this.getLayer());

        if (!this.getLayer().isEnabled())
            this.getLayer().setEnabled(true);

        this.getWwd().getInputHandler().addMouseListener(this);
        this.getWwd().getInputHandler().addMouseMotionListener(this);
    }

    public void disable()
    {
        this.screenRect.clearSelection();
        this.getWwd().getSceneController().setPickRectangle(null);

        this.getWwd().getModel().getLayers().remove(this.getLayer());

        this.getWwd().getInputHandler().removeMouseListener(this);
        this.getWwd().getInputHandler().removeMouseMotionListener(this);
    }

    public void mouseClicked(MouseEvent mouseEvent)
    {
        // Intentionally left blank. ScreenSelector does not respond to mouse clicked events.
    }

    public void mousePressed(MouseEvent mouseEvent)
    {
        if (mouseEvent == null) // Ignore null events.
            return;

        if (MouseEvent.BUTTON1_DOWN_MASK != mouseEvent.getModifiersEx()) // Respond to button 1 down w/o modifiers.
            return;

        this.armed = true;
        this.selectionStarted(mouseEvent);
        mouseEvent.consume(); // Consume the mouse event to prevent the view from responding to it.
    }

    public void mouseReleased(MouseEvent mouseEvent)
    {
        if (mouseEvent == null) // Ignore null events.
            return;

        if (!this.armed) // Respond to mouse released events when armed.
            return;

        this.armed = false;
        this.selectionEnded(mouseEvent);
        mouseEvent.consume(); // Consume the mouse event to prevent the view from responding to it.
    }

    public void mouseEntered(MouseEvent mouseEvent)
    {
        // Intentionally left blank. ScreenSelector does not respond to mouse entered events.
    }

    public void mouseExited(MouseEvent mouseEvent)
    {
        // Intentionally left blank. ScreenSelector does not respond to mouse exited events.
    }

    public void mouseDragged(MouseEvent mouseEvent)
    {
        if (mouseEvent == null) // Ignore null events.
            return;

        if (!this.armed) // Respond to mouse dragged events when armed.
            return;

        this.selectionChanged(mouseEvent);
        mouseEvent.consume(); // Consume the mouse event to prevent the view from responding to it.
    }

    public void mouseMoved(MouseEvent mouseEvent)
    {
        // Intentionally left blank. ScreenSelector does not respond to mouse moved events.
    }

    protected void selectionStarted(MouseEvent mouseEvent)
    {
        this.screenRect.startSelection(mouseEvent.getPoint());
        this.wwd.getSceneController().setPickRectangle(null);
        this.wwd.redraw();
    }

    @SuppressWarnings( {"UnusedParameters"})
    protected void selectionEnded(MouseEvent mouseEvent)
    {
        this.screenRect.clearSelection();
        this.wwd.redraw();
    }

    protected void selectionChanged(MouseEvent mouseEvent)
    {
        // Specify the selection's end point and set the scene controller's pick rectangle to the selected rectangle.
        // We create a copy of the selected rectangle to insulate the scene controller from changes to rectangle
        // returned by ScreenRectangle.getSelection.
        this.screenRect.endSelection(mouseEvent.getPoint());
        this.wwd.getSceneController().setPickRectangle(
            this.screenRect.hasSelection() ? new Rectangle(this.screenRect.getSelection()) : null);
        this.wwd.redraw();
    }
}
