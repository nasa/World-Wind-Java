/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.view;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;

/**
 * @author jym
 * @version $Id$
 */
public class BasicViewPropertyLimits implements ViewPropertyLimits
{

    protected Sector eyeLocationLimits;
    protected Angle minHeading;
    protected Angle maxHeading;
    protected Angle minPitch;
    protected Angle maxPitch;
    protected Angle minRoll;
    protected Angle maxRoll;
    protected double minEyeElevation;
    protected double maxEyeElevation;

    public BasicViewPropertyLimits()
    {
        this.eyeLocationLimits = Sector.FULL_SPHERE;
        this.minEyeElevation = -Double.MAX_VALUE;
        this.maxEyeElevation = Double.MAX_VALUE;
        this.minHeading = Angle.NEG180;
        this.maxHeading = Angle.POS180;
        this.minPitch = Angle.ZERO;
        this.maxPitch = Angle.POS90;
        this.minRoll = Angle.NEG180;
        this.maxRoll = Angle.POS180;
    }

    public Sector getEyeLocationLimits()
    {
        return this.eyeLocationLimits;
    }

    public void setEyeLocationLimits(Sector sector)
    {
        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.eyeLocationLimits = sector;
    }

    public double[] getEyeElevationLimits()
    {
        return new double[] {this.minEyeElevation, this.maxEyeElevation};
    }

    public void setEyeElevationLimits(double minValue, double maxValue)
    {
        this.minEyeElevation = minValue;
        this.maxEyeElevation = maxValue;
    }

     public Angle[] getHeadingLimits()
    {
        return new Angle[] {this.minHeading, this.maxHeading};
    }

    public void setHeadingLimits(Angle minAngle, Angle maxAngle)
    {
        if (minAngle == null || maxAngle == null)
        {
            String message = Logging.getMessage("nullValue.MinOrMaxAngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.minHeading = minAngle;
        this.maxHeading = maxAngle;
    }

    public Angle[] getPitchLimits()
    {
        return new Angle[] {this.minPitch, this.maxPitch};
    }

    public void setPitchLimits(Angle minAngle, Angle maxAngle)
    {
        if (minAngle == null || maxAngle == null)
        {
            String message = Logging.getMessage("nullValue.MinOrMaxAngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.minPitch = minAngle;
        this.maxPitch = maxAngle;
    }

    /**
     * Get the limits for roll.
     *
     * @return The roll limits as a two element array {minRoll, maxRoll},
     */
    public Angle[] getRollLimits()
    {
        return new Angle[] { this.minRoll, this.maxRoll };
    }

    /**
     * Set the roll limits.
     *
     * @param minAngle The smallest allowable roll.
     * @param maxAngle The largest allowable roll.
     */
    public void setRollLimits(Angle minAngle, Angle maxAngle)
    {
        if (minAngle == null || maxAngle == null)
        {
            String message = Logging.getMessage("nullValue.MinOrMaxAngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.minRoll = minAngle;
        this.maxRoll = maxAngle;
    }

     public static Angle limitHeading(Angle angle, ViewPropertyLimits viewLimits)
    {
        if (angle == null)
        {
            String message = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (viewLimits == null)
        {
            String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Angle[] limits = viewLimits.getHeadingLimits();
        Angle newAngle = angle;

        if (angle.compareTo(limits[0]) < 0)
        {
            newAngle = limits[0];
        }
        else if (angle.compareTo(limits[1]) > 0)
        {
            newAngle = limits[1];
        }

        return newAngle;
    }

    public static Angle limitPitch(Angle angle, ViewPropertyLimits viewLimits)
    {
        if (angle == null)
        {
            String message = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (viewLimits == null)
        {
            String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Angle[] limits = viewLimits.getPitchLimits();
        Angle newAngle = angle;
        if (angle.compareTo(limits[0]) < 0)
        {
            newAngle = limits[0];
        }
        else if (angle.compareTo(limits[1]) > 0)
        {
            newAngle = limits[1];
        }

        return newAngle;
    }

    /**
     * Clamp a roll angle to the range specified in a limit object.
     *
     * @param angle      Angle to clamp to the allowed range.
     * @param viewLimits defines the roll limits. 
     */
    public static Angle limitRoll(Angle angle, ViewPropertyLimits viewLimits)
    {
        if (angle == null)
        {
            String message = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (viewLimits == null)
        {
            String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Angle[] limits = viewLimits.getRollLimits();
        Angle newAngle = angle;
        if (angle.compareTo(limits[0]) < 0)
        {
            newAngle = limits[0];
        }
        else if (angle.compareTo(limits[1]) > 0)
        {
            newAngle = limits[1];
        }

        return newAngle;
    }

    public static double limitEyeElevation(double elevation, ViewPropertyLimits viewLimits)
    {
        if (viewLimits == null)
        {
            String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        double newElevation = elevation;
        double[] elevLimits = viewLimits.getEyeElevationLimits();

        if (elevation < elevLimits[0])
        {
             newElevation = elevLimits[0];
        }
        else if (elevation > elevLimits[1])
        {
            newElevation = elevLimits[1];
        }
        return(newElevation);
    }

    public static LatLon limitEyePositionLocation(Angle latitude, Angle longitude, ViewPropertyLimits viewLimits)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (viewLimits == null)
        {
            String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Sector limits = viewLimits.getEyeLocationLimits();
        Angle newLatitude = latitude;
        Angle newLongitude = longitude;

        if (latitude.compareTo(limits.getMinLatitude()) < 0)
        {
            newLatitude = limits.getMinLatitude();
        }
        else if (latitude.compareTo(limits.getMaxLatitude()) > 0)
        {
            newLatitude = limits.getMaxLatitude();
        }

        if (longitude.compareTo(limits.getMinLongitude()) < 0)
        {
            newLongitude = limits.getMinLongitude();
        }
        else if (longitude.compareTo(limits.getMaxLongitude()) > 0)
        {
            newLongitude = limits.getMaxLongitude();
        }

        return new LatLon(newLatitude, newLongitude);
    }

    //**************************************************************//
    //******************** Restorable State  ***********************//
    //**************************************************************//

    public void getRestorableState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        rs.addStateValueAsSector(context, "eyeLocationLimits", this.eyeLocationLimits);
        rs.addStateValueAsDouble(context, "minEyeElevation", this.minEyeElevation);
        rs.addStateValueAsDouble(context, "maxEyeElevation", this.maxEyeElevation);
        rs.addStateValueAsDouble(context, "minHeadingDegrees", this.minHeading.degrees);
        rs.addStateValueAsDouble(context, "maxHeadingDegrees", this.maxHeading.degrees);
        rs.addStateValueAsDouble(context, "minPitchDegrees", this.minPitch.degrees);
        rs.addStateValueAsDouble(context, "maxPitchDegrees", this.maxPitch.degrees);
    }

    public void restoreState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        Sector sector = rs.getStateValueAsSector(context, "eyeLocationLimits");
        if (sector != null)
            this.setEyeLocationLimits(sector);

        // Min and max center elevation.
        double[] minAndMaxValue = this.getEyeElevationLimits();
        Double min = rs.getStateValueAsDouble(context, "minEyeElevation");
        if (min != null)
            minAndMaxValue[0] = min;

        Double max = rs.getStateValueAsDouble(context, "maxEyeElevation");
        if (max != null)
            minAndMaxValue[1] = max;

        if (min != null || max != null)
            this.setEyeElevationLimits(minAndMaxValue[0], minAndMaxValue[1]);

        // Min and max heading angle.
        Angle[] minAndMaxAngle = this.getHeadingLimits();
        min = rs.getStateValueAsDouble(context, "minHeadingDegrees");
        if (min != null)
            minAndMaxAngle[0] = Angle.fromDegrees(min);

        max = rs.getStateValueAsDouble(context, "maxHeadingDegrees");
        if (max != null)
            minAndMaxAngle[1] = Angle.fromDegrees(max);

        if (min != null || max != null)
            this.setHeadingLimits(minAndMaxAngle[0], minAndMaxAngle[1]);

        // Min and max pitch angle.
        minAndMaxAngle = this.getPitchLimits();
        min = rs.getStateValueAsDouble(context, "minPitchDegrees");
        if (min != null)
            minAndMaxAngle[0] = Angle.fromDegrees(min);

        max = rs.getStateValueAsDouble(context, "maxPitchDegrees");
        if (max != null)
            minAndMaxAngle[1] = Angle.fromDegrees(max);

        if (min != null || max != null)
            this.setPitchLimits(minAndMaxAngle[0], minAndMaxAngle[1]);

    }
}
