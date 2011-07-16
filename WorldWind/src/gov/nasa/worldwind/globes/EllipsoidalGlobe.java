/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.globes;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.terrain.*;
import gov.nasa.worldwind.util.*;

import java.util.List;

/**
 * @author Tom Gaskins
 * @version $Id$
 */
public class EllipsoidalGlobe extends WWObjectImpl implements Globe
{
    protected final double equatorialRadius;
    protected final double polarRadius;
    protected final double es;
    private final Vec4 center;
    private ElevationModel elevationModel;
    private Tessellator tessellator;

    public EllipsoidalGlobe(double equatorialRadius, double polarRadius, double es, ElevationModel em)
    {
        this.equatorialRadius = equatorialRadius;
        this.polarRadius = polarRadius;
        this.es = es; // assume it's consistent with the two radii
        this.center = Vec4.ZERO;
        this.elevationModel = em;
        this.tessellator = (Tessellator) WorldWind.createConfigurationComponent(AVKey.TESSELLATOR_CLASS_NAME);
    }

    public EllipsoidalGlobe(double equatorialRadius, double polarRadius, double es, ElevationModel em, Vec4 center)
    {
        this.equatorialRadius = equatorialRadius;
        this.polarRadius = polarRadius;
        this.es = es; // assume it's consistent with the two radii
        this.center = center;
        this.elevationModel = em;
        this.tessellator = (Tessellator) WorldWind.createConfigurationComponent(AVKey.TESSELLATOR_CLASS_NAME);
    }

    protected class StateKey implements GlobeStateKey
    {
        protected Globe globe;
        protected final Tessellator tessellator;
        protected double verticalExaggeration;
        protected ElevationModel elevationModel;

        public StateKey(DrawContext dc)
        {
            if (dc == null)
            {
                String msg = Logging.getMessage("nullValue.DrawContextIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            this.globe = dc.getGlobe();
            this.tessellator = EllipsoidalGlobe.this.tessellator;
            this.verticalExaggeration = dc.getVerticalExaggeration();
            this.elevationModel = this.globe.getElevationModel();
        }

        public StateKey(Globe globe)
        {
            this.globe = globe;
            this.tessellator = EllipsoidalGlobe.this.tessellator;
            this.verticalExaggeration = 1;
            this.elevationModel = this.globe.getElevationModel();
        }

        public Globe getGlobe()
        {
            return this.globe;
        }

        @SuppressWarnings( {"RedundantIfStatement"})
        @Override
        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            StateKey stateKey = (StateKey) o;

            if (Double.compare(stateKey.verticalExaggeration, verticalExaggeration) != 0)
                return false;
            if (elevationModel != null ? !elevationModel.equals(stateKey.elevationModel) :
                stateKey.elevationModel != null)
                return false;
            if (globe != null ? !globe.equals(stateKey.globe) : stateKey.globe != null)
                return false;
            if (tessellator != null ? !tessellator.equals(stateKey.tessellator) : stateKey.tessellator != null)
                return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result;
            long temp;
            result = globe != null ? globe.hashCode() : 0;
            result = 31 * result + (tessellator != null ? tessellator.hashCode() : 0);
            temp = verticalExaggeration != +0.0d ? Double.doubleToLongBits(verticalExaggeration) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (elevationModel != null ? elevationModel.hashCode() : 0);
            return result;
        }
    }

    public Object getStateKey(DrawContext dc)
    {
        return this.getGlobeStateKey(dc);
    }

    public GlobeStateKey getGlobeStateKey(DrawContext dc)
    {
        return new StateKey(dc);
    }

    public GlobeStateKey getGlobeStateKey()
    {
        return new StateKey(this);
    }

    public Tessellator getTessellator()
    {
        return tessellator;
    }

    public void setTessellator(Tessellator tessellator)
    {
        this.tessellator = tessellator;
    }

    public ElevationModel getElevationModel()
    {
        return elevationModel;
    }

    public void setElevationModel(ElevationModel elevationModel)
    {
        this.elevationModel = elevationModel;
    }

    public double getRadius()
    {
        return this.equatorialRadius;
    }

    public double getEquatorialRadius()
    {
        return this.equatorialRadius;
    }

    public double getPolarRadius()
    {
        return this.polarRadius;
    }

    public double getMaximumRadius()
    {
        return this.equatorialRadius;
    }

    public double getRadiusAt(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.computePointFromPosition(latitude, longitude, 0d).getLength3();
    }

    public double getRadiusAt(LatLon latLon)
    {
        if (latLon == null)
        {
            String msg = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.computePointFromPosition(latLon.getLatitude(), latLon.getLongitude(), 0d).getLength3();
    }

    public double getEccentricitySquared()
    {
        return this.es;
    }

    public double getDiameter()
    {
        return this.equatorialRadius * 2;
    }

    public Vec4 getCenter()
    {
        return this.center;
    }

    public double getMaxElevation()
    {
        return this.elevationModel != null ? this.elevationModel.getMaxElevation() : 0;
    }

    public double getMinElevation()
    {
        // TODO: The value returned might not reflect the globe's actual minimum elevation if the elevation model does
        // not span the full globe. See WWJINT-435.
        return this.elevationModel != null ? this.elevationModel.getMinElevation() : 0;
    }

    public double[] getMinAndMaxElevations(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            String msg = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.elevationModel != null ? this.elevationModel.getExtremeElevations(latitude, longitude)
            : new double[] {0, 0};
    }

    public double[] getMinAndMaxElevations(Sector sector)
    {
        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.elevationModel != null ? this.elevationModel.getExtremeElevations(sector) : new double[] {0, 0};
    }

    public Extent getExtent()
    {
        return this;
    }

    public double getEffectiveRadius(Plane plane)
    {
        return this.getRadius();
    }

    public boolean intersects(Frustum frustum)
    {
        if (frustum == null)
        {
            String message = Logging.getMessage("nullValue.FrustumIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return frustum.intersects(this);
    }

    public Intersection[] intersect(Line line)
    {
        return this.intersect(line, this.equatorialRadius, this.polarRadius);
    }

    public Intersection[] intersect(Line line, double altitude)
    {
        return this.intersect(line, this.equatorialRadius + altitude, this.polarRadius + altitude);
    }

    protected Intersection[] intersect(Line line, double equRadius, double polRadius)
    {
        if (line == null)
            return null;

        // Taken from Lengyel, 2Ed., Section 5.2.3, page 148.

        double m = equRadius / polRadius; // "ratio of the x semi-axis length to the y semi-axis length"
        double n = 1d;                    // "ratio of the x semi-axis length to the z semi-axis length"
        double m2 = m * m;
        double n2 = n * n;
        double r2 = equRadius * equRadius; // nominal radius squared //equRadius * polRadius;

        double vx = line.getDirection().x;
        double vy = line.getDirection().y;
        double vz = line.getDirection().z;
        double sx = line.getOrigin().x;
        double sy = line.getOrigin().y;
        double sz = line.getOrigin().z;

        double a = vx * vx + m2 * vy * vy + n2 * vz * vz;
        double b = 2d * (sx * vx + m2 * sy * vy + n2 * sz * vz);
        double c = sx * sx + m2 * sy * sy + n2 * sz * sz - r2;

        double discriminant = discriminant(a, b, c);
        if (discriminant < 0)
            return null;

        double discriminantRoot = Math.sqrt(discriminant);
        if (discriminant == 0)
        {
            Vec4 p = line.getPointAt((-b - discriminantRoot) / (2 * a));
            return new Intersection[] {new Intersection(p, true)};
        }
        else // (discriminant > 0)
        {
            Vec4 near = line.getPointAt((-b - discriminantRoot) / (2 * a));
            Vec4 far = line.getPointAt((-b + discriminantRoot) / (2 * a));
            if (c >= 0) // Line originates outside the Globe.
                return new Intersection[] {new Intersection(near, false), new Intersection(far, false)};
            else // Line originates inside the Globe.
                return new Intersection[] {new Intersection(far, false)};
        }
    }

    static private double discriminant(double a, double b, double c)
    {
        return b * b - 4 * a * c;
    }

    public Intersection[] intersect(Triangle t, double elevation)
    {
        if (t == null)
            return null;

        boolean bA = isPointAboveElevation(t.getA(), elevation);
        boolean bB = isPointAboveElevation(t.getB(), elevation);
        boolean bC = isPointAboveElevation(t.getC(), elevation);

        if (!(bA ^ bB) && !(bB ^ bC))
            return null; // all triangle points are either above or below the given elevation

        Intersection[] inter = new Intersection[2];
        int idx = 0;

        // Assumes that intersect(Line) returns only one intersection when the line
        // originates inside the ellipsoid at the given elevation.
        if (bA ^ bB)
            if (bA)
                inter[idx++] = intersect(new Line(t.getB(), t.getA().subtract3(t.getB())), elevation)[0];
            else
                inter[idx++] = intersect(new Line(t.getA(), t.getB().subtract3(t.getA())), elevation)[0];

        if (bB ^ bC)
            if (bB)
                inter[idx++] = intersect(new Line(t.getC(), t.getB().subtract3(t.getC())), elevation)[0];
            else
                inter[idx++] = intersect(new Line(t.getB(), t.getC().subtract3(t.getB())), elevation)[0];

        if (bC ^ bA)
            if (bC)
                inter[idx] = intersect(new Line(t.getA(), t.getC().subtract3(t.getA())), elevation)[0];
            else
                inter[idx] = intersect(new Line(t.getC(), t.getA().subtract3(t.getC())), elevation)[0];

        return inter;
    }

    public boolean intersects(Line line)
    {
        if (line == null)
            return false;

        return line.distanceTo(this.center) <= this.equatorialRadius;
    }

    public boolean intersects(Plane plane)
    {
        if (plane == null)
            return false;

        double dq1 = plane.dot(this.center);
        return dq1 <= this.equatorialRadius;
    }

    /** {@inheritDoc} */
    public double getProjectedArea(View view)
    {
        if (view == null)
        {
            String message = Logging.getMessage("nullValue.ViewIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return WWMath.computeSphereProjectedArea(view, this.getCenter(), this.getRadius());
    }

    public double getElevations(Sector sector, List<? extends LatLon> latlons, double targetResolution,
        double[] elevations)
    {
        return this.elevationModel != null ?
            this.elevationModel.getElevations(sector, latlons, targetResolution, elevations) : 0;
    }

    public double getElevation(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.elevationModel != null ? this.elevationModel.getElevation(latitude, longitude) : 0;
    }

    public Vec4 computePointFromPosition(Position position)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.geodeticToCartesian(position.getLatitude(), position.getLongitude(), position.getElevation());
    }

    public Vec4 computePointFromLocation(LatLon location)
    {
        if (location == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.geodeticToCartesian(location.getLatitude(), location.getLongitude(), 0);
    }

    public Vec4 computePointFromPosition(LatLon latLon, double metersElevation)
    {
        if (latLon == null)
        {
            String message = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.geodeticToCartesian(latLon.getLatitude(), latLon.getLongitude(), metersElevation);
    }

    public Vec4 computePointFromPosition(Angle latitude, Angle longitude, double metersElevation)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.geodeticToCartesian(latitude, longitude, metersElevation);
    }

    public Position computePositionFromPoint(Vec4 point)
    {
        if (point == null)
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.cartesianToGeodetic(point);
    }

    /**
     * Returns the normal to the Globe at the specified position.
     *
     * @param latitude  the latitude of the position.
     * @param longitude the longitude of the position.
     *
     * @return the Globe normal at the specified position.
     */
    public Vec4 computeSurfaceNormalAtLocation(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double cosLat = latitude.cos();
        double cosLon = longitude.cos();
        double sinLat = latitude.sin();
        double sinLon = longitude.sin();

        double eqSquared = this.equatorialRadius * this.equatorialRadius;
        double polSquared = this.polarRadius * this.polarRadius;

        double x = cosLat * sinLon / eqSquared;
        double y = (1.0 - this.es) * sinLat / polSquared;
        double z = cosLat * cosLon / eqSquared;

        return new Vec4(x, y, z).normalize3();
    }

    /**
     * Returns the normal to the Globe at the specified cartiesian point.
     *
     * @param point the cartesian point.
     *
     * @return the Globe normal at the specified point.
     */
    public Vec4 computeSurfaceNormalAtPoint(Vec4 point)
    {
        if (point == null)
        {
            String msg = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        double eqSquared = this.equatorialRadius * this.equatorialRadius;
        double polSquared = this.polarRadius * this.polarRadius;

        double x = (point.x - this.center.x) / eqSquared;
        double y = (point.y - this.center.y) / polSquared;
        double z = (point.z - this.center.z) / eqSquared;

        return new Vec4(x, y, z).normalize3();
    }

    public Vec4 computeNorthPointingTangentAtLocation(Angle latitude, Angle longitude)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Latitude is treated clockwise as rotation about the X-axis. We flip the latitude value so that a positive
        // rotation produces a clockwise rotation (when facing the axis).
        latitude = latitude.multiply(-1.0);

        double cosLat = latitude.cos();
        double sinLat = latitude.sin();
        double cosLon = longitude.cos();
        double sinLon = longitude.sin();

        // The north-pointing tangent is derived by rotating the vector (0, 1, 0) about the Y-axis by longitude degrees,
        // then rotating it about the X-axis by -latitude degrees. This can be represented by a combining two rotation
        // matrices Rlat, and Rlon, then transforming the vector (0, 1, 0) by the combined transform:
        //
        // NorthTangent = (Rlon * Rlat) * (0, 1, 0)
        //
        // Since the input vector only has a Y coordinate, this computation can be simplified. The simplified
        // computation is shown here as NorthTangent = (x, y, z).
        //
        double x = sinLat * sinLon;
        //noinspection UnnecessaryLocalVariable
        double y = cosLat;
        double z = sinLat * cosLon;

        return new Vec4(x, y, z).normalize3();
    }

    public Matrix computeModelCoordinateOriginTransform(Angle latitude, Angle longitude, double metersElevation)
    {
        return this.computeSurfaceOrientationAtPosition(latitude, longitude, metersElevation);
    }

    public Matrix computeModelCoordinateOriginTransform(Position position)
    {
        return this.computeSurfaceOrientationAtPosition(position);
    }

    /** {@inheritDoc} */
    public Matrix computeSurfaceOrientationAtPosition(Angle latitude, Angle longitude, double metersElevation)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4 point = this.geodeticToCartesian(latitude, longitude, metersElevation);
        // Transform to the cartesian coordinates of (latitude, longitude, metersElevation).
        Matrix transform = Matrix.fromTranslation(point);
        // Rotate the coordinate system to match the longitude.
        // Longitude is treated as counter-clockwise rotation about the Y-axis.
        transform = transform.multiply(Matrix.fromRotationY(longitude));
        // Rotate the coordinate system to match the latitude.
        // Latitude is treated clockwise as rotation about the X-axis. We flip the latitude value so that a positive
        // rotation produces a clockwise rotation (when facing the axis).
        transform = transform.multiply(Matrix.fromRotationX(latitude.multiply(-1.0)));
        return transform;
    }

    /** {@inheritDoc} */
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

    public Position getIntersectionPosition(Line line)
    {
        if (line == null)
        {
            String msg = Logging.getMessage("nullValue.LineIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Intersection[] intersections = this.intersect(line);
        if (intersections == null)
            return null;

        return this.computePositionFromPoint(intersections[0].getIntersectionPoint());
    }

    /**
     * Maps a position to world Cartesian coordinates. The Y axis points to the north pole. The Z axis points to the
     * intersection of the prime meridian and the equator, in the equatorial plane. The X axis completes a right-handed
     * coordinate system, and is 90 degrees east of the Z axis and also in the equatorial plane. Sea level is at z =
     * zero.
     *
     * @param latitude        the latitude of the position.
     * @param longitude       the longitude of the position.
     * @param metersElevation the number of meters above or below mean sea level.
     *
     * @return The Cartesian point corresponding to the input position.
     */
    protected Vec4 geodeticToCartesian(Angle latitude, Angle longitude, double metersElevation)
    {
        if (latitude == null || longitude == null)
        {
            String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        double cosLat = Math.cos(latitude.radians);
        double sinLat = Math.sin(latitude.radians);
        double cosLon = Math.cos(longitude.radians);
        double sinLon = Math.sin(longitude.radians);

        double rpm = // getRadius (in meters) of vertical in prime meridian
            this.equatorialRadius / Math.sqrt(1.0 - this.es * sinLat * sinLat);

        double x = (rpm + metersElevation) * cosLat * sinLon;
        double y = (rpm * (1.0 - this.es) + metersElevation) * sinLat;
        double z = (rpm + metersElevation) * cosLat * cosLon;

        return new Vec4(x, y, z);
    }
//
//    protected Position cartesianToGeodeticOriginal(Vec4 cart)
//    {
//        if (cart == null)
//        {
//            String message = Logging.getMessage("nullValue.PointIsNull");
//            Logging.logger().severe(message);
//            throw new IllegalArgumentException(message);
//        }
//
//        // according to
//        // H. Vermeille,
//        // Direct transformation from geocentric to geodetic ccordinates,
//        // Journal of Geodesy (2002) 76:451-454
//        double ra2 = 1 / (this.equatorialRadius * equatorialRadius);
//
//        double X = cart.z;
//        //noinspection SuspiciousNameCombination
//        double Y = cart.x;
//        double Z = cart.y;
//        double e2 = this.es;
//        double e4 = e2 * e2;
//
//        double XXpYY = X * X + Y * Y;
//        double sqrtXXpYY = Math.sqrt(XXpYY);
//        double p = XXpYY * ra2;
//        double q = Z * Z * (1 - e2) * ra2;
//        double r = 1 / 6.0 * (p + q - e4);
//        double s = e4 * p * q / (4 * r * r * r);
//        double t = Math.pow(1 + s + Math.sqrt(s * (2 + s)), 1 / 3.0);
//        double u = r * (1 + t + 1 / t);
//        double v = Math.sqrt(u * u + e4 * q);
//        double w = e2 * (u + v - q) / (2 * v);
//        double k = Math.sqrt(u + v + w * w) - w;
//        double D = k * sqrtXXpYY / (k + e2);
//        double lon = 2 * Math.atan2(Y, X + sqrtXXpYY);
//        double sqrtDDpZZ = Math.sqrt(D * D + Z * Z);
//        double lat = 2 * Math.atan2(Z, D + sqrtDDpZZ);
//        double elevation = (k + e2 - 1) * sqrtDDpZZ / k;
//
//        return Position.fromRadians(lat, lon, elevation);
//    }

    @SuppressWarnings( {"SuspiciousNameCombination"})
    protected Position cartesianToGeodetic(Vec4 cart)
    {
        // Contributed by Nathan Kronenfeld. Integrated 1/24/2011. Brings this calculation in line with Vermeille's
        // most recent update.
        if (null == cart)
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // According to
        // H. Vermeille,
        // "An analytical method to transform geocentric into geodetic coordinates"
        // http://www.springerlink.com/content/3t6837t27t351227/fulltext.pdf
        // Journal of Geodesy, accepted 10/2010, not yet published
        double X = cart.z;
        double Y = cart.x;
        double Z = cart.y;
        double XXpYY = X * X + Y * Y;
        double sqrtXXpYY = Math.sqrt(XXpYY);

        double a = this.equatorialRadius;
        double ra2 = 1 / (a * a);
        double e2 = this.es;
        double e4 = e2 * e2;

        // Step 1
        double p = XXpYY * ra2;
        double q = Z * Z * (1 - e2) * ra2;
        double r = (p + q - e4) / 6;

        double h;
        double phi;

        double evoluteBorderTest = 8 * r * r * r + e4 * p * q;
        if (evoluteBorderTest > 0 || q != 0)
        {
            double u;

            if (evoluteBorderTest > 0)
            {
                // Step 2: general case
                double rad1 = Math.sqrt(evoluteBorderTest);
                double rad2 = Math.sqrt(e4 * p * q);

                // 10*e2 is my arbitrary decision of what Vermeille means by "near... the cusps of the evolute".
                if (evoluteBorderTest > 10 * e2)
                {
                    double rad3 = Math.cbrt((rad1 + rad2) * (rad1 + rad2));
                    u = r + 0.5 * rad3 + 2 * r * r / rad3;
                }
                else
                {
                    u = r + 0.5 * Math.cbrt((rad1 + rad2) * (rad1 + rad2)) + 0.5 * Math.cbrt(
                        (rad1 - rad2) * (rad1 - rad2));
                }
            }
            else
            {
                // Step 3: near evolute
                double rad1 = Math.sqrt(-evoluteBorderTest);
                double rad2 = Math.sqrt(-8 * r * r * r);
                double rad3 = Math.sqrt(e4 * p * q);
                double atan = 2 * Math.atan2(rad3, rad1 + rad2) / 3;

                u = -4 * r * Math.sin(atan) * Math.cos(Math.PI / 6 + atan);
            }

            double v = Math.sqrt(u * u + e4 * q);
            double w = e2 * (u + v - q) / (2 * v);
            double k = (u + v) / (Math.sqrt(w * w + u + v) + w);
            double D = k * sqrtXXpYY / (k + e2);
            double sqrtDDpZZ = Math.sqrt(D * D + Z * Z);

            h = (k + e2 - 1) * sqrtDDpZZ / k;
            phi = 2 * Math.atan2(Z, sqrtDDpZZ + D);
        }
        else
        {
            // Step 4: singular disk
            double rad1 = Math.sqrt(1 - e2);
            double rad2 = Math.sqrt(e2 - p);
            double e = Math.sqrt(e2);

            h = -a * rad1 * rad2 / e;
            phi = rad2 / (e * rad2 + rad1 * Math.sqrt(p));
        }

        // Compute lambda
        double lambda;
        double s2 = Math.sqrt(2);
        if ((s2 - 1) * Y < sqrtXXpYY + X)
        {
            // case 1 - -135deg < lambda < 135deg
            lambda = 2 * Math.atan2(Y, sqrtXXpYY + X);
        }
        else if (sqrtXXpYY + Y < (s2 + 1) * X)
        {
            // case 2 - -225deg < lambda < 45deg
            lambda = -Math.PI * 0.5 + 2 * Math.atan2(X, sqrtXXpYY - Y);
        }
        else
        {
            // if (sqrtXXpYY-Y<(s2=1)*X) {  // is the test, if needed, but it's not
            // case 3: - -45deg < lambda < 225deg
            lambda = Math.PI * 0.5 - 2 * Math.atan2(X, sqrtXXpYY + Y);
        }

        return Position.fromRadians(phi, lambda, h);
    }
//
//    /**
//     * Returns a cylinder that minimally surrounds the sector at a specified vertical exaggeration.
//     *
//     * @param verticalExaggeration the vertical exaggeration to apply to the globe's elevations when computing the
//     *                             cylinder.
//     * @param sector               the sector to return the bounding cylinder for.
//     *
//     * @return The minimal bounding cylinder in Cartesian coordinates.
//     *
//     * @throws IllegalArgumentException if <code>sector</code> is null
//     */
//    public Cylinder computeBoundingCylinder(double verticalExaggeration, Sector sector)
//    {
//        if (sector == null)
//        {
//            String msg = Logging.getMessage("nullValue.SectorIsNull");
//            Logging.logger().severe(msg);
//            throw new IllegalArgumentException(msg);
//        }
//
//        return Sector.computeBoundingCylinder(this, verticalExaggeration, sector);
//    }
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
//     *
//     * @throws IllegalArgumentException if <code>sector</code> is null
//     */
//    public Cylinder computeBoundingCylinder(double verticalExaggeration, Sector sector,
//        double minElevation, double maxElevation)
//    {
//        if (sector == null)
//        {
//            String msg = Logging.getMessage("nullValue.SectorIsNull");
//            Logging.logger().severe(msg);
//            throw new IllegalArgumentException(msg);
//        }
//
//        // Compute the exaggerated minimum and maximum heights.
//        double minHeight = minElevation * verticalExaggeration;
//        double maxHeight = maxElevation * verticalExaggeration;
//
//        if (minHeight == maxHeight)
//            maxHeight = minHeight + 1; // ensure the top and bottom of the cylinder won't be coincident
//
//        // If the sector spans both poles in latitude, or spans greater than 180 degrees in longitude, we cannot use the
//        // sector's Cartesian quadrilateral to compute a bounding cylinde. This is because the quadrilateral is either
//        // smaller than the geometry defined by the sector (when deltaLon >= 180), or the quadrilateral degenerates to
//        // two points (when deltaLat >= 180). So we compute a bounging cylinder that spans the equator and covers the
//        // sector's latitude range. In some cases this cylinder may be too large, but we're typically not interested
//        // in culling these cylinders since the sector will span most of the globe.
//        if (sector.getDeltaLatDegrees() >= 180d || sector.getDeltaLonDegrees() >= 180d)
//        {
//            return this.computeBoundsFromSectorLatitudeRange(sector, minHeight, maxHeight);
//        }
//        // Otherwise, create a standard bounding cylinder that minimally surrounds the specified sector and elevations.
//        else
//        {
//            return this.computeBoundsFromSectorQuadrilateral(sector, minHeight, maxHeight);
//        }
//    }
//
//    public Cylinder computeBoundingCylinderNew(double verticalExaggeration, Sector sector, double minElevation,
//        double maxElevation)
//    {
//        if (sector == null)
//        {
//            String msg = Logging.getMessage("nullValue.SectorIsNull");
//            Logging.logger().severe(msg);
//            throw new IllegalArgumentException(msg);
//        }
//
//        // Compute the exaggerated minimum and maximum heights.
//        double minHeight = minElevation * verticalExaggeration;
//        double maxHeight = maxElevation * verticalExaggeration;
//
//        if (minHeight == maxHeight)
//            maxHeight = minHeight + 1; // ensure the top and bottom of the cylinder won't be coincident
//
//        List<Vec4> points = new ArrayList<Vec4>();
//        for (LatLon ll : sector)
//        {
//            points.add(this.computePointFromPosition(ll, minHeight));
//            points.add(this.computePointFromPosition(ll, maxHeight));
//        }
//        if (sector.getDeltaLatDegrees() >= 180d || sector.getDeltaLonDegrees() >= 180d)
//            points.add(this.computePointFromPosition(sector.getCentroid(), maxHeight));
//
//        try
//        {
//            return Cylinder.compute(points);
//        }
//        catch (Exception e)
//        {
//            return new Cylinder(points.get(0), points.get(0).add3(Vec4.UNIT_Y), 1);
//        }
//    }
//
//    public static void main(String[] args)
//    {
//        EllipsoidalGlobe globe = new Earth();
//        Sector sector = Sector.fromDegrees(0, 1, 0, 1);
//
//        int n = 1000000;
//
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < n; i++)
//        {
//            Extent cyl1 = globe.computeBoundingVolume(1d, sector, 0, 100);
//        }
//        System.out.println(System.currentTimeMillis() - start);
//
//        start = System.currentTimeMillis();
//        for (int i = 0; i < n; i++)
//        {
//            Cylinder cyl2 = globe.computeBoundingCylinder2(1d, sector, 0, 100);
//        }
//        System.out.println(System.currentTimeMillis() - start);
//    }
//
//    public Cylinder computeBoundingCylinder(double verticalExaggeration, Iterable<? extends Position> positions)
//    {
//        List<Vec4> points = new ArrayList<Vec4>();
//
//        for (Position pos : positions)
//        {
//            if (pos != null)
//                points.add(this.computePointFromPosition(pos, pos.elevation * verticalExaggeration));
//        }
//
//        return Cylinder.compute(points);
//    }

    public SectorGeometryList tessellate(DrawContext dc)
    {
        if (this.tessellator == null)
        {
            this.tessellator = (Tessellator) WorldWind.createConfigurationComponent(AVKey.TESSELLATOR_CLASS_NAME);

            if (this.tessellator == null)
            {
                String msg = Logging.getMessage("Tessellator.TessellatorUnavailable");
                Logging.logger().severe(msg);
                throw new IllegalStateException(msg);
            }
        }

        return this.tessellator.tessellate(dc);
    }

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

        return (point.x() * point.x()) / ((this.equatorialRadius + elevation) * (this.equatorialRadius + elevation))
            + (point.y() * point.y()) / ((this.polarRadius + elevation) * (this.polarRadius + elevation))
            + (point.z() * point.z()) / ((this.equatorialRadius + elevation) * (this.equatorialRadius + elevation))
            - 1 > 0;
    }

    /**
     * Construct an elevation model given a key for a configuration source and the source's default value.
     *
     * @param key          the key identifying the configuration property in {@link Configuration}.
     * @param defaultValue the default value of the property to use if it's not found in {@link Configuration}.
     *
     * @return a new elevation model configured according to the configuration source.
     */
    public static ElevationModel makeElevationModel(String key, String defaultValue)
    {
        if (key == null)
        {
            String msg = Logging.getMessage("nullValue.KeyIsNull");
            throw new IllegalArgumentException(msg);
        }

        Object configSource = Configuration.getStringValue(key, defaultValue);
        return (ElevationModel) BasicFactory.create(AVKey.ELEVATION_MODEL_FACTORY, configSource);
    }
}