/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.terrain;

import gov.nasa.worldwind.WWObject;
import gov.nasa.worldwind.geom.*;

import java.util.List;

/**
 * <p/>
 * Provides the elevations to a {@link gov.nasa.worldwind.globes.Globe} or other object holding elevations.
 * <p/>
 * An <code>ElevationModel</code> often approximates elevations at multiple levels of spatial resolution. For any given
 * viewing position, the model determines an appropriate target resolution. That target resolution may not be
 * immediately achievable, however, because the corresponding elevation data might not be locally available and must be
 * retrieved from a remote location. When this is the case, the elevations returned for a sector represent the
 * resolution achievable with the data currently available. That resolution may not be the same as the target
 * resolution. The achieved resolution is made available in the interface.
 * <p/>
 *
 * @author dcollins
 * @version $Id$
 */
public interface ElevationModel extends WWObject
{
    /**
     * Returns the elevation model's name.
     *
     * @return the elevation model's name.
     *
     * @see #setName(String)
     */
    String getName();

    /**
     * Set the elevation model's name. The name is a convenience attribute typically used to identify the elevation
     * model in user interfaces. By default, an elevation model has no name.
     *
     * @param name the name to give the elevation model.
     */
    void setName(String name);

    /**
     * Indicates whether the elevation model covers a specified sector either partially or fully.
     *
     * @param sector the sector in question.
     *
     * @return 0 if the elevation model fully contains the sector, 1 if the elevation model intersects the sector but
     *         does not fully contain it, or -1 if the sector does not intersect the elevation model.
     */
    int intersects(Sector sector);

    /**
     * Indicates whether a specified location is within the elevation model's domain.
     *
     * @param latitude  the latitude of the location in question.
     * @param longitude the longitude of the location in question.
     *
     * @return true if the location is within the elevation model's domain, otherwise false.
     */
    boolean contains(Angle latitude, Angle longitude);

    /**
     * Indicates the best resolution attainable for a specified sector.
     *
     * @param sector the sector in question. If null, the elevation model's best overall resolution is returned. This is
     *               the best attainable at <em>some</em> locations but not necessarily at all locations.
     *
     * @return the best resolution attainable for the specified sector, in radians, or {@link Double#MAX_VALUE} if the
     *         sector does not intersect the elevation model.
     */
    double getBestResolution(Sector sector);

    /**
     * Returns the minimum elevation contained in the elevation model. When associated with a globe, this value is the
     * elevation of the lowest point on the globe. It may be negative, indicating a value below mean surface level. (Sea
     * level in the case of Earth.)
     *
     * @return The minimum elevation of the model.
     */
    double getMinElevation();

    /**
     * Returns the maximum elevation contained in the elevation model. When the elevation model is associated with a
     * globe, this value is the elevation of the highest point on the globe.
     *
     * @return The maximum elevation of the elevation model.
     */
    double getMaxElevation();

    /**
     * Returns the minimum and maximum elevations at a specified location.
     *
     * @param latitude  the latitude of the location in question.
     * @param longitude the longitude of the location in question.
     *
     * @return A two-element <code>double</code> array indicating, respectively, the minimum and maximum elevations at
     *         the specified location. These values are the global minimum and maximum if the local minimum and maximum
     *         values are currently unknown.
     */
    double[] getMinAndMaxElevations(Angle latitude, Angle longitude);

    /**
     * Returns the minimum and maximum elevations within a specified sector of the elevation model.
     *
     * @param sector the sector in question.
     *
     * @return A two-element <code>double</code> array indicating, respectively, the sector's minimum and maximum
     *         elevations. These elements are the global minimum and maximum if the local minimum and maximum values are
     *         currently unknown.
     */
    double[] getMinAndMaxElevations(Sector sector);

    /**
     * Returns the elevation at a specified location. If the elevation at the specified location is the elevation
     * model's missing data signal, or if the location specified is outside the elevation model's coverage area, the
     * elevation model's missing data replacement value is returned.
     * <p/>
     * The elevation returned from this method is the best available in memory. If no elevation is in memory, the
     * elevation model's minimum extreme elevation at the location is returned. Local disk caches are not consulted.
     *
     * @param latitude  the latitude of the location in question.
     * @param longitude the longitude of the location in question.
     *
     * @return The elevation corresponding to the specified location, or the elevation model's missing-data replacement
     *         value if there is no elevation for the given location.
     */
    double getElevation(Angle latitude, Angle longitude);

    /**
     * Returns the elevations of a collection of locations. Replaces any elevation values corresponding to the missing
     * data signal with the elevation model's missing data replacement value. If a location within the elevation model's
     * coverage area cannot currently be determined, the elevation model's minimum extreme elevation for that location
     * is returned in the output buffer. If a location is outside the elevation model's coverage area, the output buffer
     * for that location is not modified; it retains the buffer's original value.
     *
     * @param sector           the sector in question.
     * @param locations        the locations to return elevations for. If a location is null, the output buffer for that
     *                         location is not modified.
     * @param targetResolution the desired horizontal resolution, in radians, of the raster or other elevation sample
     *                         from which elevations are drawn. (To compute radians from a distance, divide the distance
     *                         by the radius of the globe, ensuring that both the distance and the radius are in the
     *                         same units.)
     * @param buffer           an array in which to place the returned elevations. The array must be pre-allocated and
     *                         contain at least as many elements as the list of locations.
     *
     * @return the resolution achieved, in radians, or {@link Double#MAX_VALUE} if individual elevations cannot be
     *         determined for all of the locations.
     */
    double getElevations(Sector sector, List<? extends LatLon> locations, double targetResolution, double[] buffer);

    /**
     * Returns the elevations corresponding to a grid of locations in a specified sector. The grid is evenly spaced
     * locations in latitude and longitude defined by numLat and numLon.  The buffer is populated with this model's
     * elevation value at each grid point, starting in the sector's lower left corner and proceeding in row major order.
     * The buffer must have length of at least numLat * numLon. If a location is outside the elevation model's coverage
     * area, the output buffer for that location is not modified; it retains the buffer's original value.
     *
     * @param sector           the sector in question.
     * @param numLat           the grid height in number of latitude locations.
     * @param numLon           the grid width in number of longitude locations.
     * @param targetResolution the desired horizontal resolution, in radians, of the raster or other elevation sample
     *                         from which elevations are drawn. (To compute radians from a distance, divide the distance
     *                         by the radius of the globe, ensuring that both the distance and the radius are in the
     *                         same units.)
     * @param buffer           an array in which to place the returned elevations. The array must be pre-allocated and
     *                         contain at least numLat * numLon elements.
     *
     * @return the resolution achieved, in radians, or {@link Double#MAX_VALUE} if individual elevations cannot be
     *         determined for all of the locations.
     *
     * @throws IllegalArgumentException if the sector is null, if either numLat or numLon are less than one, if
     *                                  targetResolution is less than or equal to zero, if the buffer is null, or if the
     *                                  buffer's length is less than numLat * numLon.
     */
    double getElevations(Sector sector, int numLat, int numLon, double targetResolution, double[] buffer);
}
