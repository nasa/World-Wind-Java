/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.Locatable;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.pick.PickSupport;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.nio.Buffer;
import java.util.Iterator;

/**
 * @author dcollins
 * @version $Id$
 */
public class AirspaceRenderer
{
    private static final String EXT_BLEND_FUNC_SEPARATE_STRING = "GL_EXT_blend_func_separate";

    private boolean enableAntialiasing;
    private boolean enableBlending;
    private boolean enableDepthOffset;
    private boolean enableLighting;
    private boolean useEXTBlendFuncSeparate;
    private boolean haveEXTBlendFuncSeparate;
    private boolean drawExtents;
    private boolean drawWireframe;
    private final PickSupport pickSupport = new PickSupport();
    private double linePickWidth;
    private Double depthOffsetFactor; // Initially null, indicating the DrawContext's default is used.
    private Double depthOffsetUnits;  // Initially null, indicating the DrawContext's default is used.
    protected boolean enableBatchRendering = true;
    protected boolean enableBatchPicking = true;

    /** Implements the the interface used by the draw context's outlined-shape renderer. */
    protected OutlinedShape outlineShapeRenderer = new OutlinedShape()
    {
        public boolean isDrawOutline(DrawContext dc, Object shape)
        {
            return ((Airspace) shape).getAttributes().isDrawOutline();
        }

        public boolean isDrawInterior(DrawContext dc, Object shape)
        {
            return ((Airspace) shape).getAttributes().isDrawInterior();
        }

        public boolean isEnableDepthOffset(DrawContext dc, Object shape)
        {
            return AirspaceRenderer.this.isEnableDepthOffset();
        }

        public void drawOutline(DrawContext dc, Object shape)
        {
            AirspaceRenderer.this.drawAirspaceOutline(dc, (Airspace) shape);
        }

        public void drawInterior(DrawContext dc, Object shape)
        {
            AirspaceRenderer.this.drawAirspaceInterior(dc, (Airspace) shape);
        }

        public Double getDepthOffsetFactor(DrawContext dc, Object shape)
        {
            return AirspaceRenderer.this.getDepthOffsetFactor();
        }

        public Double getDepthOffsetUnits(DrawContext dc, Object shape)
        {
            return AirspaceRenderer.this.getDepthOffsetUnits();
        }
    };

    public AirspaceRenderer()
    {
        this.enableAntialiasing = false;
        this.enableBlending = true;
        this.enableDepthOffset = false;
        this.enableLighting = true;
        this.useEXTBlendFuncSeparate = true;
        this.haveEXTBlendFuncSeparate = false;
        this.drawExtents = false;
        this.drawWireframe = false;
        this.linePickWidth = 8.0;
    }

    public boolean isEnableAntialiasing()
    {
        return this.enableAntialiasing;
    }

    public void setEnableAntialiasing(boolean enable)
    {
        this.enableAntialiasing = enable;
    }

    public boolean isEnableBlending()
    {
        return this.enableBlending;
    }

    public void setEnableBlending(boolean enable)
    {
        this.enableBlending = enable;
    }

    public boolean isEnableDepthOffset()
    {
        return this.enableDepthOffset;
    }

    public void setEnableDepthOffset(boolean enable)
    {
        this.enableDepthOffset = enable;
    }

    public boolean isEnableLighting()
    {
        return this.enableLighting;
    }

    public void setEnableLighting(boolean enable)
    {
        this.enableLighting = enable;
    }

    public boolean isUseEXTBlendFuncSeparate()
    {
        return this.useEXTBlendFuncSeparate;
    }

    public void setUseEXTBlendFuncSeparate(boolean useEXTBlendFuncSeparate)
    {
        this.useEXTBlendFuncSeparate = useEXTBlendFuncSeparate;
    }

    protected boolean isHaveEXTBlendFuncSeparate()
    {
        return this.haveEXTBlendFuncSeparate;
    }

    protected void setHaveEXTBlendFuncSeparate(boolean haveEXTBlendFuncSeparate)
    {
        this.haveEXTBlendFuncSeparate = haveEXTBlendFuncSeparate;
    }

    public boolean isDrawExtents()
    {
        return this.drawExtents;
    }

    public void setDrawExtents(boolean draw)
    {
        this.drawExtents = draw;
    }

    public boolean isDrawWireframe()
    {
        return this.drawWireframe;
    }

    public void setDrawWireframe(boolean draw)
    {
        this.drawWireframe = draw;
    }

    public double getLinePickWidth()
    {
        return linePickWidth;
    }

    public void setLinePickWidth(double width)
    {
        if (width < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "width < 0");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.linePickWidth = width;
    }

    public Double getDepthOffsetFactor()
    {
        return this.depthOffsetFactor;
    }

    public void setDepthOffsetFactor(Double factor)
    {
        this.depthOffsetFactor = factor;
    }

    public Double getDepthOffsetUnits()
    {
        return depthOffsetUnits;
    }

    public void setDepthOffsetUnits(Double units)
    {
        this.depthOffsetUnits = units;
    }

    public PickSupport getPickSupport()
    {
        return this.pickSupport;
    }

    public boolean isEnableBatchRendering()
    {
        return enableBatchRendering;
    }

    public void setEnableBatchRendering(boolean enableBatchRendering)
    {
        this.enableBatchRendering = enableBatchRendering;
    }

    public boolean isEnableBatchPicking()
    {
        return enableBatchPicking;
    }

    public void setEnableBatchPicking(boolean enableBatchPicking)
    {
        this.enableBatchPicking = enableBatchPicking;
    }

    public void renderOrdered(DrawContext dc, Iterable<? extends Airspace> airspaces)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (airspaces == null)
        {
            String msg = Logging.getMessage("nullValue.AirspaceIterableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.drawOrdered(dc, airspaces);
    }

    public void pickNow(DrawContext dc, Iterable<? extends Airspace> airspaces, java.awt.Point pickPoint, Layer layer)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (airspaces == null)
        {
            String msg = Logging.getMessage("nullValue.AirspaceIterableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        PickSupport pickSupport = this.getPickSupport();
        pickSupport.clearPickList();
        pickSupport.beginPicking(dc);
        this.beginRendering(dc);
        try
        {
            // The pick method will bind pickable objects to the renderer's PickSupport.
            this.drawNow(dc, airspaces, pickSupport);
        }
        finally
        {
            this.endRendering(dc);
            pickSupport.endPicking(dc);
            pickSupport.resolvePick(dc, pickPoint, layer);
        }
    }

    public void renderNow(DrawContext dc, Iterable<? extends Airspace> airspaces)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (airspaces == null)
        {
            String msg = Logging.getMessage("nullValue.AirspaceIterableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.beginRendering(dc);
        try
        {
            // The render method does not bind any pickable objects.
            this.drawNow(dc, airspaces, null);
        }
        finally
        {
            this.endRendering(dc);
        }
    }

    protected void drawOrdered(DrawContext dc, Iterable<? extends Airspace> airspaces)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (airspaces == null)
        {
            String msg = Logging.getMessage("nullValue.AirspaceIterableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        for (Airspace airspace : airspaces)
        {
            if (!airspace.isVisible())
                continue;

            if (!airspace.isAirspaceVisible(dc))
                continue;

            airspace.makeOrderedRenderable(dc, this);
        }
    }

    protected void drawNow(DrawContext dc, Iterable<? extends Airspace> airspaces, PickSupport pickSupport)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (airspaces == null)
        {
            String msg = Logging.getMessage("nullValue.AirspaceIterableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        for (Airspace airspace : airspaces)
        {
            if (airspace == null)
                continue;

            if (!airspace.isVisible())
                continue;

            if (!airspace.isAirspaceVisible(dc))
                continue;

            this.drawAirspace(dc, airspace, pickSupport);
        }
    }

    //**************************************************************//
    //********************  Ordered Airspaces  *********************//
    //**************************************************************//

    public OrderedRenderable createOrderedRenderable(DrawContext dc, Airspace airspace, double eyeDistance,
        Object pickedObject)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (airspace == null)
        {
            String msg = Logging.getMessage("nullValue.AirspaceIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return new OrderedAirspace(this, airspace, dc.getCurrentLayer(), eyeDistance, pickedObject);
    }

    protected static class OrderedAirspace implements OrderedRenderable
    {
        protected AirspaceRenderer renderer;
        protected Airspace airspace;
        protected Layer layer;
        protected double eyeDistance;
        protected Object pickedObject;

        public OrderedAirspace(AirspaceRenderer renderer, Airspace airspace, Layer layer, double eyeDistance,
            Object pickedObject)
        {
            if (renderer == null)
            {
                String msg = Logging.getMessage("nullValue.RendererIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            if (airspace == null)
            {
                String msg = Logging.getMessage("nullValue.AirspaceIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.renderer = renderer;
            this.airspace = airspace;
            this.layer = layer;
            this.eyeDistance = eyeDistance;
            this.pickedObject = pickedObject;
        }

        public AirspaceRenderer getRenderer()
        {
            return this.renderer;
        }

        public Airspace getAirspace()
        {
            return this.airspace;
        }

        public Layer getLayer()
        {
            return this.layer;
        }

        public double getDistanceFromEye()
        {
            return this.eyeDistance;
        }

        public Object getPickedObject()
        {
            return this.pickedObject;
        }

        public void render(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            // The render method does not bind any pickable objects.
            this.draw(dc, null);
        }

        public void pick(DrawContext dc, Point pickPoint)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            if (pickPoint == null)
            {
                String msg = Logging.getMessage("nullValue.PickPoint");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            PickSupport pickSupport = this.getRenderer().getPickSupport();
            pickSupport.clearPickList();
            try
            {
                pickSupport.beginPicking(dc);
                // The pick method will bind pickable objects to the renderer's PickSupport.
                this.draw(dc, pickSupport);
            }
            finally
            {
                pickSupport.endPicking(dc);
                pickSupport.resolvePick(dc, pickPoint, this.getLayer());
            }
        }

        protected void draw(DrawContext dc, PickSupport pickSupport)
        {
            AirspaceRenderer renderer = this.getRenderer();
            renderer.drawOrderedAirspace(dc, this, pickSupport);
        }
    }

    protected void drawOrderedAirspace(DrawContext dc, OrderedAirspace oa, PickSupport pickSupport)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (oa == null)
        {
            String msg = Logging.getMessage("nullValue.OrderedAirspace");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.beginRendering(dc);
        try
        {
            this.drawAirspace(dc, oa.getAirspace(), oa.getPickedObject(), pickSupport);
            this.drawOrderedAirspaces(dc, pickSupport);
        }
        finally
        {
            this.endRendering(dc);
        }
    }

    protected void drawOrderedAirspaces(DrawContext dc, PickSupport pickSupport)
    {
        if (dc == null)
        {
            String msg = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (!this.isEnableBatchRendering())
            return;

        if (dc.isPickingMode() && !this.isEnableBatchPicking())
            return;

        // Batch render as many Airspaces as we can to save OpenGL state switching.
        OrderedRenderable top = dc.peekOrderedRenderables();
        while (top != null && top instanceof OrderedAirspace)
        {
            OrderedAirspace oa = (OrderedAirspace) top;

            // If the next OrderedAirspace's renderer is different, then we must stop batching. Otherwise, we would
            // render an airspace with a renderer with potentially different properties or behavior.
            if (this != oa.getRenderer())
                return;

            this.drawAirspace(dc, oa.getAirspace(), oa.getPickedObject(), pickSupport);

            // Take the ordered airspace off the queue, then peek at the next item in the queue (but do not remove it).
            dc.pollOrderedRenderables();
            top = dc.peekOrderedRenderables();
        }
    }

    //**************************************************************//
    //********************  Airspace Rendering  ********************//
    //**************************************************************//

    protected void drawAirspace(DrawContext dc, Airspace airspace, PickSupport pickSupport)
    {
        this.drawAirspace(dc, airspace, airspace, pickSupport); // Use the airspace as the picked object.
    }

    protected void drawAirspace(DrawContext dc, Airspace airspace, Object pickedObject, PickSupport pickSupport)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (airspace == null)
        {
            String message = Logging.getMessage("nullValue.AirspaceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            if (pickSupport != null)
            {
                this.bindPickableObject(dc, pickedObject, pickSupport);
            }

            this.doDrawAirspace(dc, airspace);
        }
        catch (Exception e)
        {
            String message = Logging.getMessage("generic.ExceptionWhileRenderingAirspace");
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
        }
    }

    protected void doDrawAirspace(DrawContext dc, Airspace airspace)
    {
        dc.drawOutlinedShape(this.outlineShapeRenderer, airspace);

        if (!dc.isPickingMode())
        {
            if (this.isDrawExtents())
                airspace.renderExtent(dc);
        }
    }

    protected void drawAirspaceInterior(DrawContext dc, Airspace airspace)
    {
        if (!dc.isPickingMode())
        {
            airspace.getAttributes().applyInterior(dc, this.isEnableLighting());
        }

        airspace.renderGeometry(dc, Airspace.DRAW_STYLE_FILL);
    }

    protected void drawAirspaceOutline(DrawContext dc, Airspace airspace)
    {
        int[] lightEnabledState = new int[1];

        if (dc.isPickingMode())
        {
            double lineWidth = airspace.getAttributes().getOutlineWidth();

            // If the airspace interior isn't drawn, make the outline wider during picking.
            if (!airspace.getAttributes().isDrawInterior())
            {
                if (lineWidth != 0.0)
                    lineWidth += this.getLinePickWidth();
            }

            dc.getGL().glLineWidth((float) lineWidth);
        }
        else
        {
            if (this.isEnableLighting())
            {
                dc.getGL().glGetIntegerv(GL.GL_LIGHTING, lightEnabledState, 0);
                if (lightEnabledState[0] == GL.GL_TRUE)
                    dc.getGL().glDisable(GL.GL_LIGHTING);
            }

            airspace.getAttributes().applyOutline(dc, false);
        }

        airspace.renderGeometry(dc, Airspace.DRAW_STYLE_OUTLINE);

        if (!dc.isPickingMode() && this.isEnableLighting() && lightEnabledState[0] == GL.GL_TRUE)
        {
            dc.getGL().glEnable(GL.GL_LIGHTING);
        }
    }

    protected void beginRendering(DrawContext dc)
    {
        GL gl = dc.getGL();

        gl.glPushClientAttrib(GL.GL_CLIENT_VERTEX_ARRAY_BIT);
        gl.glEnableClientState(GL.GL_VERTEX_ARRAY);

        if (!dc.isPickingMode())
        {
            int attribMask =
                GL.GL_COLOR_BUFFER_BIT
                    // For color write mask. If blending is enabled: for blending src and func, and alpha func.
                    | GL.GL_CURRENT_BIT // For current color.
                    | GL.GL_LINE_BIT // For line width, line smoothing.
                    | GL.GL_POLYGON_BIT // For polygon mode, polygon offset.
                    | GL.GL_TRANSFORM_BIT; // For matrix mode.
            gl.glPushAttrib(attribMask);

            if (this.isDrawWireframe())
                gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);

            if (this.isEnableBlending())
                this.setBlending(dc);

            if (this.isEnableLighting())
            {
                gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
                dc.beginStandardLighting();
            }

            if (this.isEnableAntialiasing())
                gl.glEnable(GL.GL_LINE_SMOOTH);
        }
        else
        {
            int attribMask =
                GL.GL_CURRENT_BIT // For current color.
//                    | GL.GL_DEPTH_BUFFER_BIT // For depth test and depth func.
                    | GL.GL_LINE_BIT; // For line width.
            gl.glPushAttrib(attribMask);
        }
    }

    protected void endRendering(DrawContext dc)
    {
        GL gl = dc.getGL();

        dc.endStandardLighting();
        gl.glPopAttrib();
        gl.glPopClientAttrib();
    }

    protected void bindPickableObject(DrawContext dc, Object pickedObject, PickSupport pickSupport)
    {
        java.awt.Color pickColor = dc.getUniquePickColor();
        dc.getGL().glColor3ub((byte) pickColor.getRed(), (byte) pickColor.getGreen(), (byte) pickColor.getBlue());

        if (pickedObject instanceof Locatable)
        {
            pickSupport.addPickableObject(pickColor.getRGB(), pickedObject, ((Locatable) pickedObject).getPosition());
        }
        else
        {
            pickSupport.addPickableObject(pickColor.getRGB(), pickedObject);
        }
    }

    //**************************************************************//
    //********************  Geometry Rendering  ********************//
    //**************************************************************//

    public void drawGeometry(DrawContext dc, int mode, int count, int type, Buffer elementBuffer, Geometry geom)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (elementBuffer == null)
        {
            String message = "nullValue.ElementBufferIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (geom == null)
        {
            String message = "nullValue.AirspaceGeometryIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (geom.getBuffer(Geometry.VERTEX) == null)
        {
            String message = "nullValue.VertexBufferIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        GL gl = dc.getGL();

        int minElementIndex, maxElementIndex;
        int size, glType, stride;
        Buffer vertexBuffer, normalBuffer;

        size = geom.getSize(Geometry.VERTEX);
        glType = geom.getGLType(Geometry.VERTEX);
        stride = geom.getStride(Geometry.VERTEX);
        vertexBuffer = geom.getBuffer(Geometry.VERTEX);
        gl.glVertexPointer(size, glType, stride, vertexBuffer);

        normalBuffer = null;
        if (!dc.isPickingMode())
        {
            if (this.isEnableLighting())
            {
                normalBuffer = geom.getBuffer(Geometry.NORMAL);
                if (normalBuffer == null)
                {
                    gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
                }
                else
                {
                    glType = geom.getGLType(Geometry.NORMAL);
                    stride = geom.getStride(Geometry.NORMAL);
                    gl.glNormalPointer(glType, stride, normalBuffer);
                }
            }
        }

        // On some hardware, using glDrawRangeElements allows vertex data to be prefetched. We know the minimum and
        // maximum index values that are valid in elementBuffer (they are 0 and vertexCount-1), so it's harmless
        // to use this approach and allow the hardware to optimize.
        minElementIndex = 0;
        maxElementIndex = geom.getCount(Geometry.VERTEX) - 1;
        gl.glDrawRangeElements(mode, minElementIndex, maxElementIndex, count, type, elementBuffer);

        if (!dc.isPickingMode())
        {
            if (this.isEnableLighting())
            {
                if (normalBuffer == null)
                    gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
            }
            this.logGeometryStatistics(dc, geom);
        }
    }

    public void drawGeometry(DrawContext dc, Geometry geom)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (geom == null)
        {
            String message = "nullValue.AirspaceGeometryIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (geom.getBuffer(Geometry.ELEMENT) == null)
        {
            String message = "nullValue.ElementBufferIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int mode, count, type;
        Buffer elementBuffer;

        mode = geom.getMode(Geometry.ELEMENT);
        count = geom.getCount(Geometry.ELEMENT);
        type = geom.getGLType(Geometry.ELEMENT);
        elementBuffer = geom.getBuffer(Geometry.ELEMENT);

        this.drawGeometry(dc, mode, count, type, elementBuffer, geom);
    }

    public void drawGeometry(DrawContext dc, Geometry elementGeom, Geometry vertexGeom)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (elementGeom == null)
        {
            String message = "nullValue.ElementGeometryIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (elementGeom.getBuffer(Geometry.ELEMENT) == null)
        {
            String message = "nullValue.ElementBufferIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (vertexGeom == null)
        {
            String message = "nullValue.VertexGeometryIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int mode, count, type;
        Buffer elementBuffer;

        mode = elementGeom.getMode(Geometry.ELEMENT);
        count = elementGeom.getCount(Geometry.ELEMENT);
        type = elementGeom.getGLType(Geometry.ELEMENT);
        elementBuffer = elementGeom.getBuffer(Geometry.ELEMENT);

        this.drawGeometry(dc, mode, count, type, elementBuffer, vertexGeom);
    }

    //**************************************************************//
    //********************  Rendering Support  *********************//
    //**************************************************************//

    public void setBlending(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        GL gl = dc.getGL();

        if (this.isUseEXTBlendFuncSeparate())
            this.setHaveEXTBlendFuncSeparate(gl.isExtensionAvailable(EXT_BLEND_FUNC_SEPARATE_STRING));

        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0.0f);

        gl.glEnable(GL.GL_BLEND);
        // The separate blend function correctly handles regular (non-premultiplied) colors. We want
        //     Cd = Cs*As + Cf*(1-As)
        //     Ad = As    + Af*(1-As)
        // So we use GL_EXT_blend_func_separate to specify different blending factors for source color and source
        // alpha.
        if (this.isUseEXTBlendFuncSeparate() && this.isHaveEXTBlendFuncSeparate())
        {
            gl.glBlendFuncSeparate(
                GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA,             // rgb   blending factors
                GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);                  // alpha blending factors
        }
        // Fallback to a single blending factor for source color and source alpha. The destination alpha will be
        // incorrect.
        else
        {
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); // rgba  blending factors
        }
    }

    protected void logGeometryStatistics(DrawContext dc, Geometry geom)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (geom == null)
        {
            String message = Logging.getMessage("nullValue.GeometryIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        int geomCount = 0;
        int vertexCount = 0;

        Iterator<PerformanceStatistic> iter = dc.getPerFrameStatistics().iterator();
        while (iter.hasNext())
        {
            PerformanceStatistic stat = iter.next();
            if (PerformanceStatistic.AIRSPACE_GEOMETRY_COUNT.equals(stat.getKey()))
            {
                geomCount += (Integer) stat.getValue();
                iter.remove();
            }
            if (PerformanceStatistic.AIRSPACE_VERTEX_COUNT.equals(stat.getKey()))
            {
                vertexCount += (Integer) stat.getValue();
                iter.remove();
            }
        }

        geomCount += 1;
        vertexCount += geom.getCount(Geometry.VERTEX);
        dc.setPerFrameStatistic(PerformanceStatistic.AIRSPACE_GEOMETRY_COUNT, "Airspace Geometry Count", geomCount);
        dc.setPerFrameStatistic(PerformanceStatistic.AIRSPACE_VERTEX_COUNT, "Airspace Vertex Count", vertexCount);
    }
}
