/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.globes;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.render.DrawContext;

/**
 * Defines a Globe represented as a projection onto a plane. The projection type is modifiable.
 *
 * @author Patrick Murris
 * @version $Id$
 */
public class FlatGlobe extends EllipsoidalGlobe
{
    public final static String PROJECTION_LAT_LON = "gov.nasa.worldwind.globes.projectionLatLon";
    public final static String PROJECTION_MERCATOR = "gov.nasa.worldwind.globes.projectionMercator";
    public final static String PROJECTION_SINUSOIDAL = "gov.nasa.worldwind.globes.projectionSinusoidal";
    public final static String PROJECTION_MODIFIED_SINUSOIDAL =
        "gov.nasa.worldwind.globes.projectionModifiedSinusoidal";

    private String projection = PROJECTION_MERCATOR;

    public FlatGlobe(double equatorialRadius, double polarRadius, double es, ElevationModel em)
    {
        super(equatorialRadius, polarRadius, es, em);
    }

    private class FlatStateKey extends StateKey
    {
        protected final String projection;
        protected double verticalExaggeration;

        public FlatStateKey(DrawContext dc)
        {
            super(dc);
            this.projection = FlatGlobe.this.projection;
        }

        public FlatStateKey(Globe globe)
        {
            super(globe);
            this.projection = FlatGlobe.this.projection;
        }

        @SuppressWarnings({"RedundantIfStatement"})
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            if (!super.equals(o))
                return false;

            FlatStateKey that = (FlatStateKey) o;

            if (Double.compare(that.verticalExaggeration, verticalExaggeration) != 0)
                return false;
            if (projection != null ? !projection.equals(that.projection) : that.projection != null)
                return false;

            return true;
        }

        public int hashCode()
        {
            int result = super.hashCode();
            long temp;
            result = 31 * result + (projection != null ? projection.hashCode() : 0);
            temp = verticalExaggeration != +0.0d ? Double.doubleToLongBits(verticalExaggeration) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    public Object getStateKey(DrawContext dc)
    {
        return this.getGlobeStateKey(dc);
    }

    public GlobeStateKey getGlobeStateKey(DrawContext dc)
    {
        return new FlatStateKey(dc);
    }

    public GlobeStateKey getGlobeStateKey()
    {
        return new FlatStateKey(this);
    }

    @Override
    public double getRadiusAt(Angle latitude, Angle longitude)
    {
        // TODO: Find a more accurate workaround than getMaximumRadius()
        if (latitude == null || longitude == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        return getMaximumRadius();
    }

    @Override
    public double getRadiusAt(LatLon latLon)
    {
        // TODO: Find a more accurate workaround then getMaximumRadius()
        if (latLon == null)
        {
            String msg = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }
        return getMaximumRadius();
    }

    public void setProjection(String projection)
    {
        if (projection == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.projection.equals(projection))
            return;

        this.projection = projection;
        this.setTessellator(null);
    }

    public String getProjection()
    {
        return this.projection;
    }

    @Override
    protected Intersection[] intersect(Line line, double equRadius, double polarRadius)
    {
        // Flat World Note: plane/line intersection point (OK)
        // Flat World Note: extract altitude from equRadius by subtracting this.equatorialRadius (OK)
        if (line == null)
        {
            String message = Logging.getMessage("nullValue.LineIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        // Intersection with world plane
        Plane plane = new Plane(0, 0, 1, -(equRadius - this.equatorialRadius));   // Flat globe plane
        Vec4 p = plane.intersect(line);
        if (p == null)
            return null;
        // Check if we are in the world boundaries
        Position pos = this.computePositionFromPoint(p);
        if (pos == null)
            return null;
        if (pos.getLatitude().degrees < -90 || pos.getLatitude().degrees > 90 ||
            pos.getLongitude().degrees < -180 || pos.getLongitude().degrees > 180)
            return null;

        return new Intersection[] {new Intersection(p, false)};
    }

    @Override
    public boolean intersects(Line line)
    {
        // Flat World Note: plane/line intersection test (OK)
        if (line == null)
        {
            String msg = Logging.getMessage("nullValue.LineIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.intersect(line) != null;
    }

    @Override
    public boolean intersects(Plane plane)
    {
        // Flat World Note: plane/plane intersection test (OK)
        if (plane == null)
        {
            String msg = Logging.getMessage("nullValue.PlaneIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Vec4 n = plane.getNormal();
        return !(n.x == 0 && n.y == 0 && n.z == 1);
    }

    @Override
    public Vec4 computeSurfaceNormalAtLocation(Angle latitude, Angle longitude)
    {
        // Flat World Note: return constant (OK)
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return Vec4.UNIT_Z;
    }

    @Override
    public Vec4 computeSurfaceNormalAtPoint(Vec4 point)
    {
        // Flat World Note: return constant (OK)
        if (point == null)
        {
            String msg = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return Vec4.UNIT_Z;
    }

    @Override
    public Vec4 computeNorthPointingTangentAtLocation(Angle latitude, Angle longitude)
    {
        // Flat World Note: return constant (OK)
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return Vec4.UNIT_Y;
    }

    @Override
    public Matrix computeSurfaceOrientationAtPosition(Angle latitude, Angle longitude, double metersElevation)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4 point = this.geodeticToCartesian(latitude, longitude, metersElevation);
        return Matrix.fromTranslation(point);
    }

    @Override
    public Matrix computeSurfaceOrientationAtPosition(Position position)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.computeSurfaceOrientationAtPosition(position.getLatitude(), position.getLongitude(),
            position.getElevation());
    }

    @Override
    public double getElevation(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Flat World Note: return zero if outside the lat/lon normal boundaries (OK)
        if (latitude.degrees < -90 || latitude.degrees > 90 || longitude.degrees < -180 || longitude.degrees > 180)
            return 0d;

        return super.getElevation(latitude, longitude);
    }

    /**
     * Maps a position to a flat world Cartesian coordinates. The world plane is located at the origin and has UNIT-Z as
     * normal. The Y axis points to the north pole. The Z axis points up. The X axis completes a right-handed coordinate
     * system, and points east. Latitude and longitude zero are at the origine on y and x respectively. Sea level is at
     * z = zero.
     *
     * @param latitude        the latitude of the position.
     * @param longitude       the longitude of the position.
     * @param metersElevation the number of meters above or below mean sea level.
     *
     * @return The Cartesian point corresponding to the input position.
     */
    @Override
    protected Vec4 geodeticToCartesian(Angle latitude, Angle longitude, double metersElevation)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4 cart = null;
        if (this.projection.equals(PROJECTION_LAT_LON))
        {
            // Lat/Lon projection - plate carree
            cart = new Vec4(this.equatorialRadius * longitude.radians,
                this.equatorialRadius * latitude.radians,
                metersElevation);
        }
        else if (this.projection.equals(PROJECTION_MERCATOR))
        {
            // Mercator projection
            if (latitude.degrees > 75)
                latitude = Angle.fromDegrees(75);
            if (latitude.degrees < -75)
                latitude = Angle.fromDegrees(-75);
            cart = new Vec4(this.equatorialRadius * longitude.radians,
                this.equatorialRadius * Math.log(Math.tan(Math.PI / 4 + latitude.radians / 2)),
                metersElevation);
        }
        else if (this.projection.equals(PROJECTION_SINUSOIDAL))
        {
            // Sinusoidal projection
            double latCos = latitude.cos();
            cart = new Vec4(
                latCos > 0 ? this.equatorialRadius * longitude.radians * latitude.cos() : 0,
                this.equatorialRadius * latitude.radians,
                metersElevation);
        }
        else if (this.projection.equals(PROJECTION_MODIFIED_SINUSOIDAL))
        {
            // Modified Sinusoidal projection
            double latCos = latitude.cos();
            cart = new Vec4(
                latCos > 0 ? this.equatorialRadius * longitude.radians * Math.pow(latCos, .3) : 0,
                this.equatorialRadius * latitude.radians,
                metersElevation);
        }
        else
        {
            String message = Logging.getMessage("generic.UnknownProjection", this.projection);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return cart;
    }

    @Override
    protected Position cartesianToGeodetic(Vec4 cart)
    {
        if (cart == null)
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Position pos = null;
        if (this.projection.equals(PROJECTION_LAT_LON))
        {
            // Lat/Lon projection - plate carree
            pos = Position.fromRadians(
                cart.y / this.equatorialRadius,
                cart.x / this.equatorialRadius,
                cart.z);
        }
        else if (this.projection.equals(PROJECTION_MERCATOR))
        {
            // Mercator projection
            pos = Position.fromRadians(
                Math.atan(Math.sinh(cart.y / this.equatorialRadius)),
                cart.x / this.equatorialRadius,
                cart.z);
        }
        else if (this.projection.equals(PROJECTION_SINUSOIDAL))
        {
            // Sinusoidal projection
            double lat = cart.y / this.equatorialRadius;
            double latCos = Math.cos(lat);
            pos = Position.fromRadians(
                lat,
                latCos > 0 ? cart.x / this.equatorialRadius / latCos : 0,
                cart.z);
        }
        else if (this.projection.equals(PROJECTION_MODIFIED_SINUSOIDAL))
        {
            // Modified Sinusoidal projection
            double lat = cart.y / this.equatorialRadius;
            double latCos = Math.cos(lat);
            pos = Position.fromRadians(
                lat,
                latCos > 0 ? cart.x / this.equatorialRadius / Math.pow(latCos, .3) : 0,
                cart.z);
        }
        return pos;
    }

//
//    /**
//     * Returns a cylinder that minimally surrounds the specified minimum and maximum elevations in the sector at a
//     * specified vertical exaggeration.
//     *
//     * @param verticalExaggeration the vertical exaggeration to apply to the minimum and maximum elevations when
//     *                             computing the cylinder.
//     * @param sector               the sector to return the bounding cylinder for.
//     * @param minElevation         the minimum elevation of the bounding cylinder.
//     * @param maxElevation         the maximum elevation of the bounding cylinder.
//     *
//     * @return The minimal bounding cylinder in Cartesian coordinates.
//     * @throws IllegalArgumentException if <code>sector</code> is null
//     */
//    @Override
//    public Cylinder computeBoundingCylinder(double verticalExaggeration, Sector sector,
//                                            double minElevation, double maxElevation)
//    {
//        if (sector == null)
//        {
//            String msg = Logging.getMessage("nullValue.SectorIsNull");
//            Logging.logger().severe(msg);
//            throw new IllegalArgumentException(msg);
//        }
//
//        // Compute the center points of the bounding cylinder's top and bottom planes.
//        LatLon center = sector.getCentroid();
//        double minHeight = minElevation * verticalExaggeration;
//        double maxHeight = maxElevation * verticalExaggeration;
//
//        if (minHeight == maxHeight)
//            maxHeight = minHeight + 1; // ensure the top and bottom of the cylinder won't be coincident
//
//        Vec4 centroidTop = this.computePointFromPosition(center.getLatitude(), center.getLongitude(), maxHeight);
//        Vec4 centroidBot = this.computePointFromPosition(center.getLatitude(), center.getLongitude(), minHeight);
//
//        // Compute radius of circumscribing circle using largest distance from center to corners.
//        Vec4 northwest = this.computePointFromPosition(sector.getMaxLatitude(), sector.getMinLongitude(), maxHeight);
//        Vec4 southeast = this.computePointFromPosition(sector.getMinLatitude(), sector.getMaxLongitude(), maxHeight);
//        Vec4 southwest = this.computePointFromPosition(sector.getMinLatitude(), sector.getMinLongitude(), maxHeight);
//        Vec4 northeast = this.computePointFromPosition(sector.getMaxLatitude(), sector.getMaxLongitude(), maxHeight);
//        double a = southwest.distanceTo3(centroidBot);
//        double b = southeast.distanceTo3(centroidBot);
//        double c = northeast.distanceTo3(centroidBot);
//        double d = northwest.distanceTo3(centroidBot);
//        double radius = Math.max(Math.max(a, b), Math.max(c, d));
//
//        return new Cylinder(centroidBot, centroidTop, radius);
//    }

    /**
     * Determines whether a point is above a given elevation
     *
     * @param point     the <code>Vec4</code> point to test.
     * @param elevation the elevation to test for.
     *
     * @return true if the given point is above the given elevation.
     */
    public boolean isPointAboveElevation(Vec4 point, double elevation)
    {
        if (point == null)
            return false;

        return point.z() > elevation;
    }
}
