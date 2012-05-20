/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.airspaces.*;
import gov.nasa.worldwind.util.Logging;

/**
 * The <code>Airspace</code> class manages a collection of {@link gov.nasa.worldwind.render.airspaces.Airspace} objects
 * for rendering and picking. <code>AirspaceLayer</code> delegates to its internal {@link
 * gov.nasa.worldwind.render.airspaces.AirspaceRenderer} for rendering and picking operations.
 *
 * @author dcollins
 * @version $Id$
 * @see gov.nasa.worldwind.render.airspaces.Airspace
 * @see gov.nasa.worldwind.render.airspaces.AirspaceRenderer
 */
public class AirspaceLayer extends AbstractLayer
{
    private final java.util.Collection<Airspace> airspaces = new java.util.concurrent.ConcurrentLinkedQueue<Airspace>();
    private Iterable<Airspace> airspacesOverride;
    private AirspaceRenderer airspaceRenderer = new AirspaceRenderer();

    /** Creates a new <code>Airspace</code> with an empty collection of Airspaces. */
    public AirspaceLayer()
    {
    }

    /**
     * Adds the specified <code>airspace</code> to this layer's internal collection. If this layer's internal collection
     * has been overridden with a call to {@link #setAirspaces(Iterable)}, this will throw an exception.
     *
     * @param airspace Airspace to add.
     *
     * @throws IllegalArgumentException If <code>airspace</code> is null.
     * @throws IllegalStateException    If a custom Iterable has been specified by a call to <code>setAirspaces</code>.
     */
    public void addAirspace(Airspace airspace)
    {
        if (airspace == null)
        {
            String msg = "nullValue.AirspaceIsNull";
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (this.airspacesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        this.airspaces.add(airspace);
    }

    /**
     * Adds the contents of the specified <code>airspaces</code> to this layer's internal collection. If this layer's
     * internal collection has been overridden with a call to {@link #setAirspaces(Iterable)}, this will throw an
     * exception.
     *
     * @param airspaces Airspaces to add.
     *
     * @throws IllegalArgumentException If <code>airspaces</code> is null.
     * @throws IllegalStateException    If a custom Iterable has been specified by a call to <code>setAirspaces</code>.
     */
    public void addAirspaces(Iterable<Airspace> airspaces)
    {
        if (airspaces == null)
        {
            String msg = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (this.airspacesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        for (Airspace airspace : airspaces)
        {
            // Internal list of airspaces does not accept null values.
            if (airspace != null)
                this.airspaces.add(airspace);
        }
    }

    /**
     * Removes the specified <code>airspace</code> from this layer's internal collection, if it exists. If this layer's
     * internal collection has been overridden with a call to {@link #setAirspaces(Iterable)}, this will throw an
     * exception.
     *
     * @param airspace Airspace to remove.
     *
     * @throws IllegalArgumentException If <code>airspace</code> is null.
     * @throws IllegalStateException    If a custom Iterable has been specified by a call to <code>setAirspaces</code>.
     */
    public void removeAirspace(Airspace airspace)
    {
        if (airspace == null)
        {
            String msg = "nullValue.AirspaceIsNull";
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        if (this.airspacesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        this.airspaces.remove(airspace);
    }

    /**
     * Clears the contents of this layer's internal Airspace collection. If this layer's internal collection has been
     * overridden with a call to {@link #setAirspaces(Iterable)}, this will throw an exception.
     *
     * @throws IllegalStateException If a custom Iterable has been specified by a call to <code>setAirspaces</code>.
     */
    public void removeAllAirspaces()
    {
        if (this.airspacesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        clearAirspaces();
    }

    private void clearAirspaces()
    {
        if (this.airspaces != null && this.airspaces.size() > 0)
            this.airspaces.clear();
    }

    /**
     * Returns the Iterable of Airspaces currently in use by this layer. If the caller has specified a custom Iterable
     * via {@link #setAirspaces(Iterable)}, this will returns a reference to that Iterable. If the caller passed
     * <code>setAirspaces</code> a null parameter, or if <code>setAirspaces</code> has not been called, this returns a
     * view of this layer's internal collection of Airspaces.
     *
     * @return Iterable of currently active Airspaces.
     */
    public Iterable<Airspace> getAirspaces()
    {
        if (this.airspacesOverride != null)
        {
            return this.airspacesOverride;
        }
        else
        {
            // Return an unmodifiable reference to the internal list of airspaces.
            // This prevents callers from changing this list and invalidating any invariants we have established.
            return java.util.Collections.unmodifiableCollection(this.airspaces);
        }
    }

    /**
     * Returns the Iterable of currently active Airspaces. If the caller has specified a custom Iterable via {@link
     * #setAirspaces(Iterable)}, this will returns a reference to that Iterable. If the caller passed
     * <code>setAirspaces</code> a null parameter, or if <code>setAirspaces</code> has not been called, this returns a
     * view of this layer's internal collection of Airspaces.
     *
     * @return Iterable of currently active Airspaces.
     */
    private Iterable<Airspace> getActiveAirspaces()
    {
        if (this.airspacesOverride != null)
        {
            return this.airspacesOverride;
        }
        else
        {
            return this.airspaces;
        }
    }

    /**
     * Overrides the collection of currently active Airspaces with the specified <code>airspaceIterable</code>. This
     * layer will maintain a reference to <code>airspaceIterable</code> strictly for picking and rendering. This layer
     * will not modify the Iterable reference. However, this will clear the internal collection of Airspaces, and will
     * prevent any modification to its contents via <code>addAirspace, addAirspaces, or removeAirspaces</code>.
     * <p/>
     * If the specified <code>airspaceIterable</code> is null, this layer will revert to maintaining its internal
     * collection.
     *
     * @param airspaceIterable Iterable to use instead of this layer's internal collection, or null to use this layer's
     *                         internal collection.
     */
    public void setAirspaces(Iterable<Airspace> airspaceIterable)
    {
        this.airspacesOverride = airspaceIterable;
        // Clear the internal collection of Airspaces.
        clearAirspaces();
    }

    public boolean isEnableAntialiasing()
    {
        return this.airspaceRenderer.isEnableAntialiasing();
    }

    public void setEnableAntialiasing(boolean enable)
    {
        this.airspaceRenderer.setEnableAntialiasing(enable);
    }

    public boolean isEnableBlending()
    {
        return this.airspaceRenderer.isEnableBlending();
    }

    public void setEnableBlending(boolean enable)
    {
        this.airspaceRenderer.setEnableBlending(enable);
    }

    public boolean isEnableDepthOffset()
    {
        return this.airspaceRenderer.isEnableDepthOffset();
    }

    public void setEnableDepthOffset(boolean enable)
    {
        this.airspaceRenderer.setEnableDepthOffset(enable);
    }

    public boolean isEnableLighting()
    {
        return this.airspaceRenderer.isEnableLighting();
    }

    public void setEnableLighting(boolean enable)
    {
        this.airspaceRenderer.setEnableLighting(enable);
    }

    public boolean isDrawExtents()
    {
        return this.airspaceRenderer.isDrawExtents();
    }

    public void setDrawExtents(boolean draw)
    {
        this.airspaceRenderer.setDrawExtents(draw);
    }

    public boolean isDrawWireframe()
    {
        return this.airspaceRenderer.isDrawWireframe();
    }

    public void setDrawWireframe(boolean draw)
    {
        this.airspaceRenderer.setDrawWireframe(draw);
    }

    public Double getDepthOffsetFactor()
    {
        return this.airspaceRenderer.getDepthOffsetFactor();
    }

    public void setDepthOffsetFactor(Double factor)
    {
        this.airspaceRenderer.setDepthOffsetFactor(factor);
    }

    public Double getDepthOffsetUnits()
    {
        return this.airspaceRenderer.getDepthOffsetUnits();
    }

    public void setDepthOffsetUnits(Double units)
    {
        this.airspaceRenderer.setDepthOffsetUnits(units);
    }

    protected AirspaceRenderer getRenderer()
    {
        return this.airspaceRenderer;
    }

    public boolean isEnableBatchRendering()
    {
        return this.getRenderer().isEnableBatchRendering();
    }

    public void setEnableBatchRendering(boolean enableBatchRendering)
    {
        this.getRenderer().setEnableBatchRendering(enableBatchRendering);
    }

    public boolean isEnableBatchPicking()
    {
        return this.getRenderer().isEnableBatchPicking();
    }

    public void setEnableBatchPicking(boolean enableBatchPicking)
    {
        this.getRenderer().setEnableBatchPicking(enableBatchPicking);
    }

    @Override
    protected void doPick(DrawContext dc, java.awt.Point pickPoint)
    {
        this.airspaceRenderer.renderOrdered(dc, getActiveAirspaces()); // Picking handled during ordered rendering.
    }

    @Override
    protected void doRender(DrawContext dc)
    {
        this.airspaceRenderer.renderOrdered(dc, getActiveAirspaces());
    }

    @Override
    public String toString()
    {
        return Logging.getMessage("layers.AirspaceLayer.Name");
    }
}
