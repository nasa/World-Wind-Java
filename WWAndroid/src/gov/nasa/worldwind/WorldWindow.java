/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind;

import gov.nasa.worldwind.cache.GpuResourceCache;
import gov.nasa.worldwind.event.*;

/**
 * @author dcollins
 * @version $Id$
 */
public interface WorldWindow extends WWObject
{
    /**
     * Returns the window's current model.
     *
     * @return the window's current model.
     */
    Model getModel();

    /**
     * Sets the model to display in this window. If <code>null</code> is specified for the model, the current model, if
     * any, is disassociated with the window.
     *
     * @param model the model to display. May be <code>null</code>.
     */
    void setModel(Model model);

    /**
     * Returns this window's current view.
     *
     * @return the window's current view.
     */
    View getView();

    /**
     * Sets the view to use when displaying this window's model. If <code>null</code> is specified for the view, the
     * current view, if any, is disassociated with the window.
     *
     * @param view the view to use to display this window's model. May be null.
     */
    void setView(View view);

    /**
     * Returns the scene controller associated with this instance.
     *
     * @return The scene controller associated with the instance, or <code>null</code> if no scene controller is
     *         associated.
     */
    SceneController getSceneController();

    /**
     * Specifies a new scene controller for the window. The caller is responsible for populating the new scene
     * controller with a {@link View}, {@link Model} and any desired per-frame statistics keys.
     *
     * @param sceneController the new scene controller.
     *
     * @see SceneController#setView(View)
     * @see SceneController#setModel(Model)
     */
    void setSceneController(SceneController sceneController);

    /**
     * Returns the input handler associated with this instance.
     *
     * @return The input handler associated with this instance, or <code>null</code> if no input handler is associated.
     */
    InputHandler getInputHandler();

    /**
     * Sets the input handler to use for this instance.
     *
     * @param inputHandler The input handler to use for this world window. May by <code>null</code> if <code>null</code>
     *                     is specified, the current input handler, if any, is disassociated with the world window.
     */
    void setInputHandler(InputHandler inputHandler);

    /**
     * Returns the GPU Resource used by this World Window.
     *
     * @return The GPU Resource cache used by this World Window.
     */
    GpuResourceCache getGpuResourceCache();

    /**
     * Adds a rendering listener to this world window. Rendering listeners are called at key points during World Wind
     * drawing and provide applications the ability to participate or monitor rendering.
     *
     * @param listener The rendering listener to add to those notified of rendering events by this world window.
     *
     * @throws IllegalArgumentException if the listener is <code>null</code>.
     */
    void addRenderingListener(RenderingListener listener);

    /**
     * Removes a specified rendering listener associated with this world window. See {@link
     * #addRenderingListener(gov.nasa.worldwind.event.RenderingListener)} for more information.
     *
     * @param listener The rendering listener to remove.
     *
     * @throws IllegalArgumentException if the listener is <code>null</code>.
     */
    void removeRenderingListener(RenderingListener listener);

    /**
     * Causes a repaint event to be enqued with the window system for this world window. The repaint will occur at the
     * window system's discretion, within the window system toolkit's event loop, and on the thread of that loop. This
     * is the preferred method for requesting a repaint of the world window.
     */
    void redraw();

    void invokeInRenderingThread(Runnable r);
}
