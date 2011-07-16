/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind;

import gov.nasa.worldwind.cache.GpuResourceCache;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.terrain.SectorGeometryList;
import gov.nasa.worldwind.util.PerformanceStatistic;

import java.util.*;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public interface SceneController extends WWObject, Disposable
{
    Model getModel();

    /**
     * Specifes the scene controller's model. This method fires an {@link gov.nasa.worldwind.avlist.AVKey#MODEL}
     * property change event.
     *
     * @param model the scene controller's model.
     */
    void setModel(Model model);

    /**
     * Returns the current view. This method fires an {@link gov.nasa.worldwind.avlist.AVKey#VIEW} property change
     * event.
     *
     * @return the current view.
     */
    View getView();

    /**
     * Sets the current view.
     *
     * @param view the view.
     */
    void setView(View view);

    /**
     * Cause the window to regenerate the frame, including pick resolution.
     *
     * @return if greater than zero, the window should be automatically repainted again at the indicated number of
     *         milliseconds from this method's return.
     */
    int repaint();

    /**
     * Specifies the exaggeration to apply to elevation values of terrain and other displayed items.
     *
     * @param verticalExaggeration the vertical exaggeration to apply.
     */
    void setVerticalExaggeration(double verticalExaggeration);

    /**
     * Indicates the current vertical exaggeration.
     *
     * @return the current vertical exaggeration.
     */
    double getVerticalExaggeration();

    /**
     * Returns the current list of picked objects.
     *
     * @return the list of currently picked objects, or null if no objects are currently picked.
     */
    PickedObjectList getPickedObjectList();

    /**
     * Returns the current average frames drawn per second. A frame is one repaint of the window and includes a pick
     * pass and a render pass.
     *
     * @return the current average number of frames drawn per second.
     */
    double getFramesPerSecond();

    /**
     * Returns the per-frame timestamp.
     *
     * @return the per-frame timestamp, in milliseconds.
     */
    double getFrameTime();

    /**
     * Specifies the current pick point.
     *
     * @param pickPoint the current pick point, or null.
     */
    void setPickPoint(java.awt.Point pickPoint);

    /**
     * Returns the current pick point.
     *
     * @return the current pick point, or null if no pick point is current.
     */
    java.awt.Point getPickPoint();

    /**
     * Specifies whether all items under the cursor are identified during picking and within {@link
     * gov.nasa.worldwind.event.SelectEvent}s.
     *
     * @param tf true to identify all items under the cursor during picking, otherwise false.
     */
    void setDeepPickEnabled(boolean tf);

    /**
     * Indicates whether all items under the cursor are identified during picking and within {@link
     * gov.nasa.worldwind.event.SelectEvent}s.
     *
     * @return true if all items under the cursor are identified during picking, otherwise false.
     */
    boolean isDeepPickEnabled();

    /**
     * Specifies the GPU Resource cache to use.
     *
     * @param gpuResourceCache the texture cache.
     */
    void setGpuResourceCache(GpuResourceCache gpuResourceCache);

    /**
     * Returns this scene controller's GPU Resource cache.
     *
     * @return this scene controller's GPU Resource cache.
     */
    GpuResourceCache getGpuResourceCache();

    /**
     * Returns the current per-frame statistics.
     *
     * @return the current per-frame statistics.
     */
    Collection<PerformanceStatistic> getPerFrameStatistics();

    /**
     * Specifies the performance values to monitor. See {@link gov.nasa.worldwind.util.PerformanceStatistic} for the
     * available keys.
     *
     * @param keys the performance statistic keys to monitor.
     */
    void setPerFrameStatisticsKeys(Set<String> keys);

    /**
     * Returns the rendering exceptions accumulated by this SceneController during the last frame as a {@link
     * java.util.Collection} of {@link Throwable} objects.
     *
     * @return the Collection of accumulated rendering exceptions.
     */
    Collection<Throwable> getRenderingExceptions();

    /**
     * Returns the terrain geometry used to draw the most recent frame. The geometry spans only the area most recently
     * visible.
     *
     * @return the terrain geometry used to draw the most recent frame. May be null.
     */
    SectorGeometryList getTerrain();

    /**
     * Returns the current draw context.
     *
     * @return the current draw context.
     */
    DrawContext getDrawContext();

    /** Reinitializes the scene controller. */
    void reinitialize();

    /**
     * Returns the current screen credit controller.
     *
     * @return the current screen credit controller. May be null.
     *
     * @see #setScreenCreditController(gov.nasa.worldwind.render.ScreenCreditController)
     */
    ScreenCreditController getScreenCreditController();

    /**
     * Specifies the {@link gov.nasa.worldwind.render.ScreenCreditController} to use for displaying screen credits for
     * the model of this screen controller.
     *
     * @param screenCreditRenderer the screen credit controller. May be null, in which case screen credits are not
     *                             displayed.
     */
    void setScreenCreditController(ScreenCreditController screenCreditRenderer);

    /**
     * Returns the {@link GLRuntimeCapabilities} associated with this SceneController.
     *
     * @return this SceneController's associated GLRuntimeCapabilities.
     */
    GLRuntimeCapabilities getGLRuntimeCapabilities();

    /**
     * Sets the {@link GLRuntimeCapabilities} associated with this SceneController to the specified parameter.
     *
     * @param capabilities the GLRuntimeCapabilities to be associated with this SceneController.
     *
     * @throws IllegalArgumentException if the capabilities are null.
     */
    void setGLRuntimeCapabilities(GLRuntimeCapabilities capabilities);
}
