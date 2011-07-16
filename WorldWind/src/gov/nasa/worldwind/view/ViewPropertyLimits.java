/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.view;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.RestorableSupport;

/**
 * @author jym
 * @version $Id$
 */
public interface ViewPropertyLimits
{
    /**
     * Sets the <code>Sector</code> which will limit the <code>View</code> eye position latitude and longitude.
     *
     * @param sector <code>Sector</code> which will limit the eye position latitude and longitude.
     * @throws IllegalArgumentException if <code>sector</code> is null.
     */
    void setEyeLocationLimits(Sector sector);
    /**
     * Returns the <code>Sector</code> which limits the <code>View</code> eye position latitude and longitude.
     *
     * @return <code>Sector</code> which limits the eye position latitude and longitude.
     */
    Sector getEyeLocationLimits();

    /**
     * Returns the minimum and maximum values for the <code>View</code> elevation.
     *
     * @return minimum and maximum allowable values for the elevation.
     */
    double[] getEyeElevationLimits();

    /**
     * Sets the minimum and maximum values for the <code>View</code> elevation.
     *
     * @param minValue The minimum elevation.
     * @param maxValue The maximum elevation.
     */
    void setEyeElevationLimits(double minValue, double maxValue);

    /**
     * Returns the minimum and maximum <code>Angles</code> for the <code>OrbitView</code> heading property.
     *
     * @return minimum and maximum allowable <code>Angles</code> for heading.
     */
    Angle[] getHeadingLimits();

    /**
     * Sets the minimum and maximum <code>Angles</code> which will limit the <code>OrbitView</code> heading property.
     *
     * @param minAngle the minimum allowable angle for heading.
     * @param maxAngle the maximum allowable angle for heading.
     * @throws IllegalArgumentException if either <code>minAngle</code> or <code>maxAngle</code> is null.
     */
    void setHeadingLimits(Angle minAngle, Angle maxAngle);

    /**
     * Returns the minimum and maximum <code>Angles</code> for the <code>OrbitView</code> pitch property.
     *
     * @return minimum and maximum allowable <code>Angles</code> for pitch.
     */
    Angle[] getPitchLimits();

    /**
     * Sets the minimum and maximum <code>Angles</code> which will limit the <code>OrbitView</code> pitch property.
     *
     * @param minAngle the minimum allowable angle for pitch.
     * @param maxAngle the maximum allowable angle for pitch.
     * @throws IllegalArgumentException if either <code>minAngle</code> or <code>maxAngle</code> is null.
     */
    void setPitchLimits(Angle minAngle, Angle maxAngle);

    /**
     * Returns the minimum and maximum <code>Angles</code> for the <code>OrbitView</code> roll property.
     *
     * @return minimum and maximum allowable <code>Angles</code> for roll.
     */
    Angle[] getRollLimits();

    /**
     * Sets the minimum and maximum <code>Angles</code> which will limit the <code>OrbitView</code> roll property.
     *
     * @param minAngle the minimum allowable angle for roll.
     * @param maxAngle the maximum allowable angle for roll.
     *
     * @throws IllegalArgumentException if either <code>minAngle</code> or <code>maxAngle</code> is null.
     */
    void setRollLimits(Angle minAngle, Angle maxAngle);


    void getRestorableState(RestorableSupport rs, RestorableSupport.StateObject context);

    void restoreState(RestorableSupport rs, RestorableSupport.StateObject context);
}
