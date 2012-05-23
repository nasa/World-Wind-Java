/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * @author garakl
 * @version $Id$
 */
public class Route extends TrackAirspace
{
    private List<LatLon> locations = new ArrayList<LatLon>();
    private double width = 1.0;

    public Route(List<? extends LatLon> locations, double width)
    {
        if (width < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "width=" + width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.width = width;
        this.addLocations(locations);
        this.setEnableInnerCaps(false);
    }

    public Route(AirspaceAttributes attributes)
    {
        super(attributes);
        this.setEnableInnerCaps(false);
    }

    public Route()
    {
        this.setEnableInnerCaps(false);
    }

    public Iterable<? extends LatLon> getLocations()
    {
        return java.util.Collections.unmodifiableList(this.locations);
    }

    public void setLocations(Iterable<? extends LatLon> locations)
    {
        this.locations.clear();
        this.removeAllLegs();
        this.addLocations(locations);
    }

    protected void addLocations(Iterable<? extends LatLon> newLocations)
    {
        if (newLocations != null)
        {
            LatLon last = null;
            for (LatLon cur : newLocations)
            {
                if (cur != null)
                {
                    if (last != null)
                        this.addLeg(last, cur);
                    last = cur;
                }
            }
            this.setExtentOutOfDate();
            this.setLegsOutOfDate();
        }
    }

    public double getWidth()
    {
        return this.width;
    }

    public void setWidth(double width)
    {
        if (width < 0.0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "width=" + width);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.width = width;

        double legWidth = this.width / 2.0;
        for (Box l : this.getLegs())
        {
            l.setWidths(legWidth, legWidth);
        }

        this.setExtentOutOfDate();
        this.setLegsOutOfDate();
    }

    public Box addLeg(LatLon start, LatLon end)
    {
        if (start == null)
        {
            String message = "nullValue.StartIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (end == null)
        {
            String message = "nullValue.EndIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.locations.size() == 0)
        {
            this.locations.add(start);
            this.locations.add(end);
        }
        else
        {
            LatLon last = this.locations.get(this.locations.size() - 1);
            if (start.equals(last))
            {
                this.locations.add(end);
            }
            else
            {
                String message = "Shapes.Route.DisjointLegDetected";
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }
        }

        double[] altitudes = this.getAltitudes();
        boolean[] terrainConformant = this.isTerrainConforming();
        double legWidth = this.width / 2.0;

        Box leg = new Box();
        leg.setAltitudes(altitudes[0], altitudes[1]);
        leg.setTerrainConforming(terrainConformant[0], terrainConformant[1]);
        leg.setLocations(start, end);
        leg.setWidths(legWidth, legWidth);
        this.addLeg(leg);

        return leg;
    }

    public Position getReferencePosition()
    {
        return this.computeReferencePosition(this.locations, this.getAltitudes());
    }

    protected void doMoveTo(Position oldRef, Position newRef)
    {
        if (oldRef == null)
        {
            String message = "nullValue.OldRefIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (newRef == null)
        {
            String message = "nullValue.NewRefIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        super.doMoveTo(oldRef, newRef);

        int count = this.locations.size();
        LatLon[] newLocations = new LatLon[count];
        for (int i = 0; i < count; i++)
        {
            LatLon ll = this.locations.get(i);
            double distance = LatLon.greatCircleDistance(oldRef, ll).radians;
            double azimuth = LatLon.greatCircleAzimuth(oldRef, ll).radians;
            newLocations[i] = LatLon.greatCircleEndPosition(newRef, azimuth, distance);
        }
        this.setLocations(Arrays.asList(newLocations));
    }

    @Override
    protected void doGetRestorableState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.doGetRestorableState(rs, context);

        rs.addStateValueAsDouble(context, "width", this.width);
        rs.addStateValueAsLatLonList(context, "locations", this.locations);
    }

    @Override
    protected void doRestoreState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        super.doRestoreState(rs, context);

        Double d = rs.getStateValueAsDouble(context, "width");
        if (d != null)
            this.setWidth(d);

        List<LatLon> locs = rs.getStateValueAsLatLonList(context, "locations");
        if (locs != null)
            this.setLocations(locs);
    }
}
