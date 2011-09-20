/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.terrain;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.Logging;

import java.util.*;

/**
 * @author dcollins
 * @version $Id$
 */
public class ZeroElevationModel extends AbstractElevationModel
{
    // Corresponds to about 10 meters for Earth (radius approx. 6.4e6 meters).
    protected static final double DEFAULT_BEST_RESOLUTION = 1.6e-6;

    public ZeroElevationModel()
    {
        this.minAndMaxElevations[1] = 1;
    }

    /** {@inheritDoc} */
    public int intersects(Sector sector)
    {
        return 0;
    }

    /** {@inheritDoc} */
    public boolean contains(Angle latitude, Angle longitude)
    {
        return true;
    }

    /** {@inheritDoc} */
    public double getBestResolution(Sector sector)
    {
        return DEFAULT_BEST_RESOLUTION;
    }

    /** {@inheritDoc} */
    public double getElevation(Angle latitude, Angle longitude)
    {
        return 0;
    }

    /** {@inheritDoc} */
    public double getElevations(Sector sector, List<? extends LatLon> locations, double targetResolution,
        double[] buffer)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (locations == null)
        {
            String msg = Logging.getMessage("nullValue.LocationsListIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (targetResolution <= 0)
        {
            String msg = Logging.getMessage("generic.ResolutionIsInvalid", targetResolution);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (buffer == null)
        {
            String msg = Logging.getMessage("nullValue.BufferIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (buffer.length < locations.size())
        {
            String msg = Logging.getMessage("generic.BufferInvalidLength", buffer.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        Arrays.fill(buffer, 0, locations.size(), 0d);
        return 0;
    }

    /** {@inheritDoc} */
    public double getElevations(Sector sector, int numLat, int numLon, double targetResolution, double[] buffer)
    {
        if (sector == null)
        {
            String msg = Logging.getMessage("nullValue.SectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (numLat <= 0)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", numLat);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (numLon <= 0)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", numLon);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (targetResolution <= 0)
        {
            String msg = Logging.getMessage("generic.ResolutionIsInvalid", targetResolution);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (buffer == null)
        {
            String msg = Logging.getMessage("nullValue.BufferIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (buffer.length < numLat * numLon)
        {
            String msg = Logging.getMessage("generic.BufferInvalidLength", buffer.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        Arrays.fill(buffer, 0, numLat * numLon, 0d);
        return 0;
    }
}
