/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.globes;

import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.terrain.*;

import java.util.List;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public interface Globe extends WWObject, Extent
{
    Extent getExtent();

    double getEquatorialRadius();

    double getPolarRadius();

    double getMaximumRadius();

    double getRadiusAt(Angle latitude, Angle longitude);

    double getElevation(Angle latitude, Angle longitude);

    double getElevations(Sector sector, List<? extends LatLon> latlons, double targetResolution, double[] elevations);

    double getMaxElevation();

    double getMinElevation();

    Position getIntersectionPosition(Line line);

    double getEccentricitySquared();

    Vec4 computePointFromPosition(Angle latitude, Angle longitude, double metersElevation);

    Vec4 computePointFromPosition(LatLon latLon, double metersElevation);

    Vec4 computePointFromPosition(Position position);

    Vec4 computePointFromLocation(LatLon location);

    Position computePositionFromPoint(Vec4 point);

    Vec4 computeSurfaceNormalAtLocation(Angle latitude, Angle longitude);

    Vec4 computeSurfaceNormalAtPoint(Vec4 point);

    Vec4 computeNorthPointingTangentAtLocation(Angle latitude, Angle longitude);

    /** @see #computeSurfaceOrientationAtPosition(gov.nasa.worldwind.geom.Angle, gov.nasa.worldwind.geom.Angle, double) */
    @SuppressWarnings( {"JavaDoc"})
    Matrix computeModelCoordinateOriginTransform(Angle latitude, Angle longitude, double metersElevation);

    /** @see #computeSurfaceOrientationAtPosition(gov.nasa.worldwind.geom.Position) */
    @SuppressWarnings( {"JavaDoc"})
    Matrix computeModelCoordinateOriginTransform(Position position);

    /**
     * Returns the cartesian transform Matrix that maps model coordinates to a local coordinate system at (latitude,
     * longitude, metersElevation). They X axis is mapped to the vector tangent to the globe and pointing East. The Y
     * axis is mapped to the vector tangent to the Globe and pointing to the North Pole. The Z axis is mapped to the
     * Globe normal at (latitude, longitude, metersElevation). The origin is mapped to the cartesian position of
     * (latitude, longitude, metersElevation).
     *
     * @param latitude        the latitude of the position.
     * @param longitude       the longitude of the position.
     * @param metersElevation the number of meters above or below mean sea level.
     *
     * @return the cartesian transform Matrix that maps model coordinates to the local coordinate system at the
     *         specified position.
     */
    Matrix computeSurfaceOrientationAtPosition(Angle latitude, Angle longitude, double metersElevation);

    /**
     * Returns the cartesian transform Matrix that maps model coordinates to a local coordinate system at (latitude,
     * longitude, metersElevation). They X axis is mapped to the vector tangent to the globe and pointing East. The Y
     * axis is mapped to the vector tangent to the Globe and pointing to the North Pole. The Z axis is mapped to the
     * Globe normal at (latitude, longitude, metersElevation). The origin is mapped to the cartesian position of
     * (latitude, longitude, metersElevation).
     *
     * @param position the latitude, longitude, and number of meters above or below mean sea level.
     *
     * @return the cartesian transform Matrix that maps model coordinates to the local coordinate system at the
     *         specified position.
     */
    Matrix computeSurfaceOrientationAtPosition(Position position);

    /**
     * Indicates this globe's radius at a specified location.
     *
     * @param latLon the location of interest.
     *
     * @return the globe's radius at that location.
     */
    double getRadiusAt(LatLon latLon);

    /**
     * Returns the minimum and maximum elevations at a specified location on this Globe. This returns a two-element
     * array filled with zero if this Globe has no elevation model.
     *
     * @param latitude  the latitude of the location in question.
     * @param longitude the longitude of the location in question.
     *
     * @return A two-element <code>double</code> array indicating the minimum and maximum elevations at the specified
     *         location, respectively. These values are the global minimum and maximum if the local minimum and maximum
     *         values are currently unknown, or zero if this Globe has no elevation model.
     */
    double[] getMinAndMaxElevations(Angle latitude, Angle longitude);

    /**
     * Returns the minimum and maximum elevations within a specified sector on this Globe. This returns a two-element
     * array filled with zero if this Globe has no elevation model.
     *
     * @param sector the sector in question.
     *
     * @return A two-element <code>double</code> array indicating the sector's minimum and maximum elevations,
     *         respectively. These elements are the global minimum and maximum if the local minimum and maximum values
     *         are currently unknown, or zero if this Globe has no elevation model.
     */
    double[] getMinAndMaxElevations(Sector sector);

    /**
     * Intersects a specified line with this globe. Only the ellipsoid itself is considered; terrain elevations are not
     * incorporated.
     *
     * @param line     the line to intersect.
     * @param altitude a distance in meters to expand the globe's equatorial and polar radii prior to performing the
     *                 intersection.
     *
     * @return the intersection points, or null if no intersection occurs or the <code>line</code> is null.
     */
    Intersection[] intersect(Line line, double altitude);

    /**
     * Intersects a specified triangle with the globe. Only the ellipsoid itself is considered; terrain elevations are
     * not incorporated.
     *
     * @param triangle the triangle to intersect.
     * @param altitude a distance in meters to expand the globe's equatorial and polar radii prior to performing the
     *                 intersection.
     *
     * @return the intersection points, or null if no intersection occurs or <code>triangle</code> is null.
     */
    Intersection[] intersect(Triangle triangle, double altitude);

    /**
     * Returns this globe's current tessellator.
     *
     * @return the globe's current tessellator.
     */
    Tessellator getTessellator();

    /**
     * Specifies this globe's tessellator.
     *
     * @param tessellator the new tessellator. Specify null to use the default tessellator.
     */
    void setTessellator(Tessellator tessellator);

    /**
     * Tessellate this globe for the currently visible region.
     *
     * @param dc the current draw context.
     *
     * @return the tessellation, or null if the tessellation failed or the draw context identifies no visible region.
     *
     * @throws IllegalStateException if the globe has no tessellator and a default tessellator cannot be created.
     */
    SectorGeometryList tessellate(DrawContext dc);

    /**
     * Returns a state key identifying this globe's current configuration. Can be used to subsequently determine whether
     * the globe's configuration has changed.
     *
     * @param dc the current draw context.
     *
     * @return a state key for the globe's current configuration.
     *
     * @throws IllegalArgumentException if the draw context is null.
     */
    Object getStateKey(DrawContext dc);

    /**
     * Returns a typed state key identifying this globe's current configuration. Can be used to subsequently determine
     * whether the globe's configuration has changed.
     *
     * @param dc the current draw context.
     *
     * @return a state key for the globe's current configuration.
     *
     * @throws IllegalArgumentException if the draw context is null.
     */
    GlobeStateKey getGlobeStateKey(DrawContext dc);

    /**
     * Returns a typed state key identifying this globe's current configuration. Can be used to subsequently determine
     * whether the globe's configuration has changed.
     *
     * @return a state key for the globe's current configuration.
     *
     * @throws IllegalArgumentException if the draw context is null.
     */
    GlobeStateKey getGlobeStateKey();

    /**
     * Indicates this globe's elevation model.
     *
     * @return this globe's elevation model.
     */
    ElevationModel getElevationModel();

    /**
     * Specifies this globe's elevation model.
     *
     * @param elevationModel this globe's elevation model. May be null to indicate no elevation model.
     */
    void setElevationModel(ElevationModel elevationModel);

    /**
     * Determines whether a point is above a given elevation.
     *
     * @param point     the <code>Vec4</code> point to test. If null, this method returns false.
     * @param elevation the elevation to test for.
     *
     * @return true if the given point is above the given elevation, otherwise false.
     */
    boolean isPointAboveElevation(Vec4 point, double elevation);
}
