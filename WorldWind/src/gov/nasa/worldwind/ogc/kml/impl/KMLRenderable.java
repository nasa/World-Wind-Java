/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml.impl;

import gov.nasa.worldwind.render.DrawContext;

/**
 * Interface for rendering KML elements.
 *
 * @author tag
 * @version $Id$
 */
public interface KMLRenderable
{
    /**
     * Pre-render this element.
     *
     * @param tc the current KML traversal context.
     * @param dc the current draw context.
     *
     * @throws IllegalArgumentException if either the traversal context or the draw context is null.
     */
    void preRender(KMLTraversalContext tc, DrawContext dc);

    /**
     * Render this element.
     *
     * @param tc the current KML traversal context.
     * @param dc the current draw context.
     *
     * @throws IllegalArgumentException if either the traversal context or the draw context is null.
     */
    void render(KMLTraversalContext tc, DrawContext dc);
}
