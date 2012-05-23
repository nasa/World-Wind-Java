/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.geom.*;

import java.awt.*;

/**
 * SurfaceTileDrawContext contains the context needed to render to an off-screen surface tile. SurfaceTileDrawContext is
 * defined by a geographic Sector and a corresponding tile viewport. The Sector maps geographic coordinates to pixels in
 * an abstract off-screen tile.
 *
 * @author dcollins
 * @version $Id$
 */
public class SurfaceTileDrawContext
{
    protected Sector sector;
    protected Rectangle viewport;
    protected Matrix modelview;

    /**
     * Constructs a SurfaceTileDrawContext with a specified surface Sector and viewport. The Sector defines the
     * context's geographic extent, and the viewport defines the context's corresponding viewport in pixels.
     *
     * @param sector   the context's Sector.
     * @param viewport the context's viewport in pixels.
     *
     * @throws IllegalArgumentException if either the sector or viewport are null, or if the viewport width or height is
     *                                  less than or equal to zero.
     */
    public SurfaceTileDrawContext(Sector sector, Rectangle viewport)
    {
        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (viewport == null)
        {
            String message = Logging.getMessage("nullValue.ViewportIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (viewport.width <= 0)
        {
            String message = Logging.getMessage("Geom.WidthInvalid");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (viewport.height <= 0)
        {
            String message = Logging.getMessage("Geom.HeightInvalid");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.sector = sector;
        this.viewport = viewport;
        this.modelview = Matrix.fromGeographicToViewport(sector, viewport.x, viewport.y,
            viewport.width, viewport.height);
    }

    /**
     * Constructs a SurfaceTileDrawContext with a specified surface Sector and viewport dimension. The Sector defines
     * the context's geographic extent, and the viewport defines the context's corresponding viewport's dimension in
     * pixels.
     *
     * @param sector         the context's Sector.
     * @param viewportWidth  the context's viewport width in pixels.
     * @param viewportHeight the context's viewport height in pixels.
     *
     * @throws IllegalArgumentException if the sector is null, or if the viewport width or height is less than or equal
     *                                  to zero.
     */
    public SurfaceTileDrawContext(Sector sector, int viewportWidth, int viewportHeight)
    {
        this(sector, new Rectangle(viewportWidth, viewportHeight));
    }

    /**
     * Returns the context's Sector.
     *
     * @return the context's Sector.
     */
    public Sector getSector()
    {
        return this.sector;
    }

    /**
     * Returns the context's viewport.
     *
     * @return the context's viewport.
     */
    public Rectangle getViewport()
    {
        return this.viewport;
    }

    /**
     * Returns a Matrix mapping geographic coordinates to pixels in the off-screen tile.
     *
     * @return Matrix mapping geographic coordinates to tile coordinates.
     */
    public Matrix getModelviewMatrix()
    {
        return this.modelview;
    }

    /**
     * Returns a Matrix mapping geographic coordinates to pixels in the off-screen tile. The reference location defines
     * the geographic coordinate origin.
     *
     * @param referenceLocation the geographic coordinate origin.
     *
     * @return Matrix mapping geographic coordinates to tile coordinates.
     *
     * @throws IllegalArgumentException if the reference location is null.
     */
    public Matrix getModelviewMatrix(LatLon referenceLocation)
    {
        if (referenceLocation == null)
        {
            String message = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.modelview.multiply(
            Matrix.fromTranslation(referenceLocation.getLongitude().degrees, referenceLocation.getLatitude().degrees,
                0));
    }
}
