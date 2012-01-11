/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.Logging;

import java.util.*;

/**
 * Implementation of the Sector Weapon/Sensor Range Fans graphic (2.X.4.3.4.2). The range fans are defined by a center
 * position, list of radii, and list of left and right azimuths. If azimuths are not specified, the fans will be drawn
 * as full circles.
 *
 * @author pabercrombie
 * @version $Id$
 */
// TODO draw symbol at the center or the fan.
// TODO implement text for this graphic
public class SectorRangeFan extends MilStd2525TacticalGraphic implements PreRenderable
{
    /** Function ID for the Sector Weapon/Sensor Range Fans graphic. */
    public final static String FUNCTION_ID = "AXS---";

    /** Default number of intervals used to draw each arcs. */
    public final static int DEFAULT_NUM_INTERVALS = 32;

    /** Default length of the Center Of Sector line, as a fraction of the final range fan radius. */
    public final static double DEFAULT_CENTER_OF_SECTOR_LENGTH = 1.2;
    /** Default length of the arrowhead, as a fraction of the Center Of Sector line length. */
    public final static double DEFAULT_ARROWHEAD_LENGTH = 0.05;
    /** Default angle of the arrowhead. */
    public final static Angle DEFAULT_ARROWHEAD_ANGLE = Angle.fromDegrees(60.0);

    /** Length of the arrowhead from base to tip, as a fraction of the Center Of Sector line length. */
    protected Angle arrowAngle = DEFAULT_ARROWHEAD_ANGLE;
    /** Angle of the arrowhead. */
    protected double arrowLength = DEFAULT_ARROWHEAD_LENGTH;
    /** Length of the Center Of Sector line, as a fraction of the last range fan radius. */
    protected double centerOfSectorLength = DEFAULT_CENTER_OF_SECTOR_LENGTH;
    /** Number of intervals used to draw each arcs. */
    protected int intervals = DEFAULT_NUM_INTERVALS;

    /** Position of the center of the range fan. */
    protected Position position;
    /** Rings that make up the range fan. */
    protected List<Path> paths;
    /** Polygon to draw a filled arrow head on the Center Of Sector line. */
    protected SurfacePolygon arrowHead;

    /** Radii of the range fans, in meters. */
    protected Iterable<Double> radii;
    /** Azimuths of the range fans. The azimuths are specified in pairs, first the left azimuth, then the right azimuth. */
    protected Iterable<? extends Angle> azimuths;

    /** Create the range fan. */
    public SectorRangeFan()
    {
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
    }

    /**
     * Indicates the angle of the arrowhead.
     *
     * @return Angle of the arrowhead in the graphic.
     */
    public Angle getArrowAngle()
    {
        return this.arrowAngle;
    }

    /**
     * Specifies the angle of the arrowhead in the graphic.
     *
     * @param arrowAngle The angle of the arrowhead. Must be greater than zero degrees and less than 90 degrees.
     */
    public void setArrowAngle(Angle arrowAngle)
    {
        if (arrowAngle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (arrowAngle.degrees <= 0 || arrowAngle.degrees >= 90)
        {
            String msg = Logging.getMessage("generic.AngleOutOfRange");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.arrowAngle = arrowAngle;
    }

    /**
     * Indicates the length of the arrowhead.
     *
     * @return The length of the arrowhead as a fraction of the total line length.
     */
    public double getArrowLength()
    {
        return this.arrowLength;
    }

    /**
     * Specifies the length of the arrowhead.
     *
     * @param arrowLength Length of the arrowhead as a fraction of the total line length. If the arrowhead length is
     *                    0.25, then the arrowhead length will be one quarter of the total line length.
     */
    public void setArrowLength(double arrowLength)
    {
        if (arrowLength < 0)
        {
            String msg = Logging.getMessage("generic.ArgumentOutOfRange");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.arrowLength = arrowLength;
    }

    /**
     * Indicates the length of the Center Of Sector line.
     *
     * @return The length of the Center Of Sector as a fraction of the final range fan radius.
     */
    public double getCenterOfSectorLength()
    {
        return this.centerOfSectorLength;
    }

    /**
     * Specifies the length of the Center Of Sector line.
     *
     * @param centerOfSectorLength Length of the Center Of Sector arrow as a fraction of the final range fan radius.
     */
    public void setCenterOfSector(double centerOfSectorLength)
    {
        if (centerOfSectorLength < 0)
        {
            String msg = Logging.getMessage("generic.ArgumentOutOfRange");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.centerOfSectorLength = centerOfSectorLength;
    }

    /**
     * Indicates the number of intervals used to draw the arc in this graphic.
     *
     * @return Intervals used to draw arc.
     */
    public int getIntervals()
    {
        return this.intervals;
    }

    /**
     * Specifies the number of intervals used to draw the arc in this graphic. More intervals will result in a smoother
     * looking arc.
     *
     * @param intervals Number of intervals for drawing the arc.
     */
    public void setIntervals(int intervals)
    {
        if (intervals < 1)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", intervals);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.intervals = intervals;
        this.reset();
    }

    /**
     * Indicates the center position of the range ran.
     *
     * @return The range fan center position.
     */
    public Position getPosition()
    {
        return this.getReferencePosition();
    }

    /**
     * Specifies the center position of the range ran.
     *
     * @param position The new center position.
     */
    public void setPosition(Position position)
    {
        this.moveTo(position);
    }

    /**
     * {@inheritDoc}
     *
     * @param positions Control points. This graphic uses only one control point, which determines the center of the
     *                  circle.
     */
    public void setPositions(Iterable<? extends Position> positions)
    {
        if (positions == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Iterator<? extends Position> iterator = positions.iterator();
        if (!iterator.hasNext())
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.position = iterator.next();
        this.reset();
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public void setModifier(String modifier, Object value)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier))
        {
            if (value instanceof Iterable)
            {
                //noinspection unchecked
                this.setRadii((Iterable) value);
            }
            else if (value instanceof Double)
            {
                this.setRadii(Arrays.asList((Double) value));
            }
        }
        else if (SymbologyConstants.AZIMUTH.equals(modifier))
        {
            if (value instanceof Iterable)
            {
                //noinspection unchecked
                this.setAzimuths((Iterable) value);
            }
        }
        else
        {
            super.setModifier(modifier, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object getModifier(String modifier)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier))
            return this.getRadii();
        else if (SymbologyConstants.AZIMUTH.equals(modifier))
            return this.getAzimuths();
        else
            return super.getModifier(modifier);
    }

    /**
     * Indicates the radii of the rings that make up the range fan.
     *
     * @return List of radii, in meters. If there are no rings this returns an empty list.
     */
    public Iterable<Double> getRadii()
    {
        return this.radii;
    }

    /**
     * Specifies the radii of the rings that make up the range fan.
     *
     * @param radii List of radii, in meters. A circle will be created for each radius.
     */
    public void setRadii(Iterable<Double> radii)
    {
        this.radii = radii;
        this.onModifierChanged();
        this.reset();
    }

    /**
     * Indicates the left and right azimuths of the fans in this graphic. The list contains pairs of azimuths, first
     * left and then right.
     *
     * @return Left and right azimuths, measured clockwise from North.
     */
    public Iterable<? extends Angle> getAzimuths()
    {
        return this.azimuths;
    }

    /**
     * Specifies the left and right azimuths of the range fans in this graphic. The provided iterable must specify pairs
     * of azimuths: first left azimuth, first right azimuth, second left, second right, etc.
     *
     * @param azimuths Left and right azimuths, measured clockwise from North.
     */
    public void setAzimuths(Iterable<? extends Angle> azimuths)
    {
        if (azimuths == null)
        {
            String message = Logging.getMessage("nullValue.ListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.azimuths = azimuths;
        this.onModifierChanged();
        this.reset();
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return Arrays.asList(this.position);
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.position;
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.determineActiveAttributes();

        if (this.paths == null)
        {
            this.createShapes(dc);
        }

        if (this.arrowHead != null)
        {
            this.arrowHead.preRender(dc);
        }
    }

    /**
     * Render the polygon.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        for (Path path : this.paths)
        {
            path.render(dc);
        }

        if (this.arrowHead != null)
        {
            this.arrowHead.render(dc);
        }
    }

    /** Regenerate the graphics positions on the next frame. */
    protected void reset()
    {
        this.paths = null;
    }

    /**
     * Create the paths required to draw the graphic.
     *
     * @param dc Current draw context.
     */
    protected void createShapes(DrawContext dc)
    {
        Iterable<? extends Double> radii = this.getRadii();
        if (radii == null)
            return;

        // If no azimuths are provided we will draw full circles.
        Iterable<? extends Angle> azimuths = this.getAzimuths();
        if (azimuths == null)
        {
            azimuths = Collections.emptyList();
        }

        this.paths = new ArrayList<Path>();

        Iterator<? extends Double> radiusIterator = radii.iterator();
        Iterator<? extends Angle> azimuthIterator = azimuths.iterator();

        Angle leftAzimuth;
        Angle rightAzimuth;

        Angle prevLeftAzimuth = Angle.NEG360;
        Angle prevRightAzimuth = Angle.POS360;
        double prevRadius = 0;

        List<Position> positions;

        // Create range fan arcs.
        while (radiusIterator.hasNext())
        {
            double radius = radiusIterator.next();

            leftAzimuth = azimuthIterator.hasNext() ? azimuthIterator.next() : Angle.ZERO;
            rightAzimuth = azimuthIterator.hasNext() ? azimuthIterator.next() : Angle.POS360;

            if (leftAzimuth.compareTo(rightAzimuth) > 0)
                leftAzimuth = leftAzimuth.subtract(Angle.POS360);

            positions = new ArrayList<Position>();

            // Create an arc to complete the left side of the previous fan, if this fan is larger. If this fan is smaller
            // this will add a single point to the position list at the range of the previous radius.
            this.createArc(dc, prevRadius, Angle.max(leftAzimuth, prevLeftAzimuth), leftAzimuth, positions);

            // Create the arc for this fan.
            this.createArc(dc, radius, leftAzimuth, rightAzimuth, positions);

            // Create an arc to complete the right side of the previous fan.
            this.createArc(dc, prevRadius, rightAzimuth, Angle.min(rightAzimuth, prevRightAzimuth), positions);

            this.paths.add(this.createPath(positions));

            prevRadius = radius;
            prevLeftAzimuth = leftAzimuth;
            prevRightAzimuth = rightAzimuth;
        }

        // Create the Center Of Sector Arrow.
        this.createCenterOfSectorArrow(dc, prevLeftAzimuth, prevRightAzimuth, prevRadius);
    }

    /**
     * Create shapes to draw the Center Of Sector arrow. This arrow bisects the final range fan.
     *
     * @param dc                Current draw context.
     * @param finalLeftAzimuth  Left azimuth of the final range fan.
     * @param finalRightAzimuth Right azimuth of the final range fan.
     * @param finalRadius       Radius, in meters, of the final range fan.
     */
    protected void createCenterOfSectorArrow(DrawContext dc, Angle finalLeftAzimuth, Angle finalRightAzimuth,
        double finalRadius)
    {
        // Create the Center of Sector arrow.
        Angle centerAngle = this.computeCenterSectorAngle(finalLeftAzimuth, finalRightAzimuth);
        Position center = this.getPosition();

        // Create the line par of the arrow.
        List<Position> positions = new ArrayList<Position>();
        positions.add(center);
        this.createArc(dc, finalRadius * this.getCenterOfSectorLength(), centerAngle, centerAngle, positions);

        this.paths.add(this.createPath(positions));

        // The final position added by createArc above is the tip of the Center Of Sector arrow head.
        Position arrowTip = positions.get(positions.size() - 1);

        // Create a polygon to draw the arrow head.
        this.arrowHead = this.createPolygon();
        positions = this.computeArrowheadPositions(dc, center, arrowTip, this.getArrowLength(),
            this.getArrowAngle());
        this.arrowHead.setLocations(positions);
    }

    /**
     * Compute the angle of the Center Of Sector line. The center sector angle is computed as the average of the left
     * and right azimuths of the last range fan in the graphic. MIL-STD-2525C does not specify how this angle should be
     * computed, but the graphic template (pg. 693) suggests that the Center Of Sector line should bisect the last range
     * fan arc.
     *
     * @param finalLeftAzimuth  Left azimuth of the last range fan in the graphic.
     * @param finalRightAzimuth Right azimuth of the last range fan in the graphic.
     *
     * @return Azimuth, from North, of the Center Of Sector line.
     */
    protected Angle computeCenterSectorAngle(Angle finalLeftAzimuth, Angle finalRightAzimuth)
    {
        return finalLeftAzimuth.add(finalRightAzimuth).divide(2.0);
    }

    /**
     * Create positions to draw an arc around the graphic's center position. The arc is described by a radius, left
     * azimuth, and right azimuth. The arc positions are added onto an existing list of positions, in left to right
     * order (the arc draws from the left azimuth to the right azimuth).
     * <p/>
     * If the left and right azimuths are equal, then this methods adds a single position to the list at the desired
     * azimuth and radius.
     *
     * @param dc           Current draw context.
     * @param radius       Radius of the circular segment, in meters.
     * @param leftAzimuth  Azimuth (from North) of the left side of the arc.
     * @param rightAzimuth Azimuth (from North) of teh right side of the arc.
     * @param positions    List to collect positions for the arc.
     */
    protected void createArc(DrawContext dc, double radius, Angle leftAzimuth, Angle rightAzimuth,
        List<Position> positions)
    {
        Globe globe = dc.getGlobe();

        int intervals = this.getIntervals();

        Position center = this.getPosition();
        double globeRadius = globe.getRadiusAt(center.getLatitude(), center.getLongitude());
        double radiusRadians = radius / globeRadius;

        // If the left and right azimuths are equal then just add a single point and return.
        if (leftAzimuth.equals(rightAzimuth))
        {
            LatLon ll = LatLon.greatCircleEndPosition(center, leftAzimuth.radians, radiusRadians);
            positions.add(new Position(ll, 0));
            return;
        }

        Angle arcAngle = rightAzimuth.subtract(leftAzimuth);

        Angle da = arcAngle.divide(intervals);

        for (int i = 0; i < intervals + 1; i++)
        {
            double angle = i * da.radians + leftAzimuth.radians;

            LatLon ll = LatLon.greatCircleEndPosition(center, angle, radiusRadians);
            positions.add(new Position(ll, 0));
        }
    }

    /**
     * Compute the positions of the arrow head for the sector center line.
     *
     * @param dc          Current draw context
     * @param base        Position of the arrow's starting point.
     * @param tip         Position of the arrow head tip.
     * @param arrowLength Length of the arrowhead as a fraction of the total line length.
     * @param arrowAngle  Angle of the arrow head.
     *
     * @return Positions required to draw the arrow head.
     */
    protected List<Position> computeArrowheadPositions(DrawContext dc, Position base, Position tip, double arrowLength,
        Angle arrowAngle)
    {
        // Build a triangle to represent the arrowhead. The triangle is built from two vectors, one parallel to the
        // segment, and one perpendicular to it.

        Globe globe = dc.getGlobe();

        Vec4 ptA = globe.computePointFromPosition(base);
        Vec4 ptB = globe.computePointFromPosition(tip);

        // Compute parallel component
        Vec4 parallel = ptA.subtract3(ptB);

        Vec4 surfaceNormal = globe.computeSurfaceNormalAtPoint(ptB);

        // Compute perpendicular component
        Vec4 perpendicular = surfaceNormal.cross3(parallel);

        double finalArrowLength = arrowLength * parallel.getLength3();
        double arrowHalfWidth = finalArrowLength * arrowAngle.tanHalfAngle();

        perpendicular = perpendicular.normalize3().multiply3(arrowHalfWidth);
        parallel = parallel.normalize3().multiply3(finalArrowLength);

        // Compute geometry of direction arrow
        Vec4 vertex1 = ptB.add3(parallel).add3(perpendicular);
        Vec4 vertex2 = ptB.add3(parallel).subtract3(perpendicular);

        return TacticalGraphicUtil.asPositionList(globe, vertex1, vertex2, ptB);
    }

    /** {@inheritDoc} Overridden to turn on shape interiors. */
    @Override
    protected void applyDefaultAttributes(ShapeAttributes attributes)
    {
        super.applyDefaultAttributes(attributes);

        // Turn on the shape interior for the arrow head. All other parts of the graphic are Paths, which do not draw
        // an interior, so this setting only affects the arrow head.
        Material material = this.getDefaultOutlineMaterial();
        attributes.setInteriorMaterial(material);

        attributes.setDrawInterior(true);
    }

    /**
     * Create and configure the Path used to render this graphic.
     *
     * @param positions Positions that define the path.
     *
     * @return New path configured with defaults appropriate for this type of graphic.
     */
    protected Path createPath(List<Position> positions)
    {
        Path path = new Path(positions);
        path.setFollowTerrain(true);
        path.setPathType(AVKey.GREAT_CIRCLE);
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        path.setDelegateOwner(this);
        path.setAttributes(this.getActiveShapeAttributes());
        return path;
    }

    /**
     * Create and configure a SurfacePolygon to render the arrow head on the sector center line.
     *
     * @return New surface polygon.
     */
    protected SurfacePolygon createPolygon()
    {
        SurfacePolygon polygon = new SurfacePolygon();
        polygon.setDelegateOwner(this);
        polygon.setAttributes(this.getActiveShapeAttributes());
        return polygon;
    }
}