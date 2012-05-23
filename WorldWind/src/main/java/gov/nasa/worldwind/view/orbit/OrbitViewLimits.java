/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.view.orbit;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.view.ViewPropertyLimits;

/**
 * <code>OrbitViewLimits</code> defines a restriction on the standard viewing parameters of an <code>OrbitView</code>.
 *
 * @author dcollins
 * @version $Id$
 */
public interface OrbitViewLimits extends ViewPropertyLimits
{
    /**
     * Returns the <code>Sector</code> which limits the <code>OrbitView</code> center latitude and longitude.
     *
     * @return <code>Sector</code> which limits the center latitude and longitude.
     */
    Sector getCenterLocationLimits();

    /**
     * Sets the <code>Sector</code> which will limit the <code>OrbitView</code> center latitude and longitude.
     *
     * @param sector <code>Sector</code> which will limit the center latitude and longitude.
     * @throws IllegalArgumentException if <code>sector</code> is null.
     */
    void setCenterLocationLimits(Sector sector);

    /**
     * Returns the minimum and maximum values for the <code>OrbitView</code> center elevation.
     *
     * @return minimum and maximum allowable values for center elevation.
     */
    double[] getCenterElevationLimits();

    /**
     * Sets the minimum and maximum values which will limit the <code>OrbitView</code> center elevation.
     *
     * @param minValue the minimum allowable value for center elevation.
     * @param maxValue the maximum allowable value for center elevation.
     */
    void setCenterElevationLimits(double minValue, double maxValue);

    

    /**
     * Returns the minimum and maximum values for the <code>OrbitView</code> zoom property.
     *
     * @return minimum and maximum allowable values for zoom.
     */
    double[] getZoomLimits();

    /**
     * Sets the minimum and maximum values which will limit the <code>OrbitView</code> zoom property.
     *
     * @param minValue the mimimum allowable value for zoom.
     * @param maxValue the maximum allowable value for zoom.
     */
    void setZoomLimits(double minValue, double maxValue);

}
