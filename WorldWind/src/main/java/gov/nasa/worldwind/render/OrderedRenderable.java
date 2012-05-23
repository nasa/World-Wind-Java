/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

/**
 * @author tag
 * @version $Id$
 */
public interface OrderedRenderable extends Renderable
{
    /**
     * Returns the ordered renderable's distance from the current view's eye point. Intended to be used only to sort a
     * list of ordered renderables according to eye distance, and only during frame generation when a view is active.
     *
     * @return the distance of the ordered renderable from the current view's eye point.
     */
    double getDistanceFromEye();

    /**
     * Executes a pick of the ordered renderable.
     *
     * @param dc        the current draw context.
     * @param pickPoint the pick point.
     */
    public void pick(DrawContext dc, java.awt.Point pickPoint);
}
