/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

/**
 * An interface for lighting models.
 *
 * @author tag
 * @version $Id$
 */
public interface LightingModel
{
    /**
     * Initializes the OpenGL state necessary to effect the lighting model.
     *
     * @param dc the current draw context.
     *
     * @see DrawContext#setStandardLightingModel(LightingModel)
     * @see DrawContext#endStandardLighting()
     */
    void beginLighting(DrawContext dc);

    /**
     * Restores state set by {@link #beginLighting(DrawContext)} to its original state.
     *
     * @param dc the current draw context.
     */
    void endLighting(DrawContext dc);
}
