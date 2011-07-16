/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public interface Renderable
{
    /**
     * Causes this <code>Renderable</code> to render itself using the provided draw context.
     *
     * @param dc the <code>DrawContext</code> to be used
     *
     * @throws IllegalArgumentException if the draw context is null.
     * @see DrawContext
     */
    public void render(DrawContext dc);
}
