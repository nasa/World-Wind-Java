/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.lines;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.TacticalGraphicAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.Logging;

import java.util.*;

/**
 * Base class for Offense arrow shapes.
 *
 * @author pabercrombie
 * @version $Id$
 */
public abstract class AbstractOffenseArrow extends MilStd2525TacticalGraphic
{
    /** Path used to render the line. */
    protected Path[] paths;

    /** Control points that define the shape. */
    protected Iterable<? extends Position> positions;

    /** Positions computed from the control points, used to draw the arrow path. */
    protected List<? extends Position> arrowPositions;

    protected long frameTimestamp = -1L;

    /** Create a new arrow graphic. */
    public AbstractOffenseArrow()
    {
        this(1);
    }

    /**
     * Create a new arrow graphic made up of more than one path. This constructor allocates the {@link #paths}, and
     * creates the requested number of paths. The first element in the array is the main path that outlines the arrow.
     * Subclasses are responsible for configuring the other paths.
     *
     * @param numPaths Number of paths to create.
     */
    public AbstractOffenseArrow(int numPaths)
    {
        if (numPaths < 1)
        {
            String message = Logging.getMessage("generic.ArrayInvalidLength", numPaths);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.paths = new Path[numPaths];
        for (int i = 0; i < numPaths; i++)
        {
            this.paths[i] = this.createPath();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param positions Control points that orient the graphic. Must provide at least three points.
     */
    public void setPositions(Iterable<? extends Position> positions)
    {
        if (positions == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Ensure that the position list provides at least 3 control points.
        try
        {
            Iterator<? extends Position> iterator = positions.iterator();
            iterator.next();
            iterator.next();
            iterator.next();
        }
        catch (NoSuchElementException e)
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.positions = positions;
        this.arrowPositions = null; // Need to recompute path for the new control points
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return this.positions;
    }

    /** {@inheritDoc} */
    @Override
    public void setHighlighted(boolean highlighted)
    {
        super.setHighlighted(highlighted);

        for (Path path : this.paths)
        {
            path.setHighlighted(highlighted);
        }
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        if (this.positions != null)
        {
            return this.positions.iterator().next(); // use the first position
        }
        return null;
    }

    /** {@inheritDoc} */
    public void move(Position delta)
    {
        if (delta == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position refPos = this.getReferencePosition();

        // The reference position is null if this shape has no positions. In this case moving the shape by a
        // relative delta is meaningless. Therefore we fail softly by exiting and doing nothing.
        if (refPos == null)
            return;

        this.moveTo(refPos.add(delta));
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position oldPosition = this.getReferencePosition();

        // The reference position is null if this shape has no positions. In this case moving the shape to a new
        // reference position is meaningless. Therefore we fail softly by exiting and doing nothing.
        if (oldPosition == null)
            return;

        List<Position> newPositions = Position.computeShiftedPositions(oldPosition, position, this.positions);

        if (newPositions != null)
            this.setPositions(newPositions);
    }

    /** {@inheritDoc} */
    public void doRenderGraphic(DrawContext dc)
    {
        if (this.arrowPositions == null)
        {
            this.createShapePositions(dc);
        }

        long timeStamp = dc.getFrameTimeStamp();
        if (this.frameTimestamp != timeStamp)
        {
            this.determineActiveAttributes();
            this.frameTimestamp = timeStamp;
        }

        for (Path path : this.paths)
        {
            path.render(dc);
        }
    }

    /**
     * Create the list of positions that describe the arrow.
     *
     * @param dc Current draw context.
     */
    protected void createShapePositions(DrawContext dc)
    {
        Globe globe = dc.getGlobe();

        // Collect positions in two lists, one for points on the left side of the control line, and one for the right side.
        List<Position> leftPositions = new ArrayList<Position>();
        List<Position> rightPositions = new ArrayList<Position>();
        List<Position> arrowHeadPositions = new ArrayList<Position>();

        double halfWidth = this.createArrowHeadPositions(leftPositions, rightPositions, arrowHeadPositions, globe);

        this.createLinePositions(leftPositions, rightPositions, halfWidth, globe);

        Collections.reverse(leftPositions);

        List<Position> allPositions = new ArrayList<Position>(leftPositions);
        allPositions.addAll(arrowHeadPositions);
        allPositions.addAll(rightPositions);

        this.arrowPositions = allPositions;
        this.paths[0].setPositions(allPositions); // Apply positions to the main path
    }

    /**
     * Create positions that make up the arrow head.
     * <p/>
     * The arrow head is defined by the first two control points, and the last point. Pt. 1' is the point on the center
     * line at the base of the arrow head, and Pt. N' is the reflection of Pt. N about the center line.
     * <pre>
     *                 Pt N
     *                 |\
     * Left line       | \
     * ----------------|  \
     * Pt 2        Pt 1'   \ Pt 1
     *                     /
     * ----------------|  /
     * Right line      | /
     *                 |/Pt N'
     * </pre>
     *
     * @param leftPositions      List to collect positions on the left arrow line. This list receives the position where
     *                           the left line meets the arrow head.
     * @param rightPositions     List to collect positions on the right arrow line. This list receives the position
     *                           where the right line meets the arrow head.
     * @param arrowHeadPositions List to collect positions that make up the arrow head. This list receives positions for
     *                           Pt. N, Pt. 1, and Pt. N', in that order.
     * @param globe              Current globe.
     *
     * @return The distance from the center line to the left and right lines.
     */
    protected double createArrowHeadPositions(List<Position> leftPositions, List<Position> rightPositions,
        List<Position> arrowHeadPositions, Globe globe)
    {
        Iterator<? extends Position> iterator = this.positions.iterator();

        Position pos1 = iterator.next();
        Position pos2 = iterator.next();

        Position posN = null;
        while (iterator.hasNext())
        {
            posN = iterator.next();
        }

        if (posN == null)
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Vec4 p1 = globe.computePointFromLocation(pos1);
        Vec4 p2 = globe.computePointFromLocation(pos2);
        Vec4 pN = globe.computePointFromLocation(posN);

        // Compute a vector that points from Pt. 1 toward Pt. 2
        Vec4 v12 = p1.subtract3(p2).normalize3();

        Vec4 p1_prime = p1.add3(v12.multiply3(pN.subtract3(p1).dot3(v12)));
        Vec4 pN_prime = p1_prime.subtract3(pN.subtract3(p1_prime));

        Vec4 normal = globe.computeSurfaceNormalAtPoint(p1_prime);

        // Compute the distance from the center line to the left and right lines.
        double halfWidth = pN.subtract3(p1_prime).getLength3() / 2;

        Vec4 offset = normal.cross3(v12).normalize3().multiply3(halfWidth);

        Vec4 pLeft = p1_prime.add3(offset);
        Vec4 pRight = p1_prime.subtract3(offset);

        Position posLeft = globe.computePositionFromPoint(pLeft);
        Position posRight = globe.computePositionFromPoint(pRight);

        leftPositions.add(posLeft);
        rightPositions.add(posRight);

        Position posN_prime = globe.computePositionFromPoint(pN_prime);

        arrowHeadPositions.add(posN);
        arrowHeadPositions.add(pos1);
        arrowHeadPositions.add(posN_prime);

        return halfWidth;
    }

    /**
     * Create positions that make up the left and right arrow lines.
     *
     * @param leftPositions  List to collect positions on the left line.
     * @param rightPositions List to collect positions on the right line.
     * @param halfWidth      Distance from the center line to the left or right lines. Half the width of the arrow's
     *                       double lines.
     * @param globe          Current globe.
     */
    protected void createLinePositions(List<Position> leftPositions, List<Position> rightPositions, double halfWidth,
        Globe globe)
    {
        Iterator<? extends Position> iterator = positions.iterator();

        Position posB = iterator.next();
        Position posA = iterator.next();

        // Starting at the arrow head end of the line, take points three at a time. B is the current control point, A is
        // the next point in the line, and C is the previous point. We need to a find a vector that bisects angle ABC.
        //       B
        //       ---------> C
        //      /
        //     /
        //    /
        // A /

        Vec4 pA = globe.computePointFromLocation(posA);
        Vec4 pB = globe.computePointFromLocation(posB);
        Vec4 pC;
        while (iterator.hasNext())
        {
            posA = iterator.next();

            pC = pB;
            pB = pA;
            pA = globe.computePointFromLocation(posA);

            Vec4 offset;
            Vec4 normal = globe.computeSurfaceNormalAtPoint(pB);

            Vec4 vBC = pC.subtract3(pB);

            // Compute a vector perpendicular to segment BC, and the globe normal vector.
            Vec4 perpendicular = vBC.cross3(normal);

            if (iterator.hasNext())
            {
                Vec4 vBA = pA.subtract3(pB);

                // Calculate the vector that bisects angle ABC.
                offset = vBA.normalize3().add3(vBC.normalize3());
                offset = offset.normalize3();

                // Compute the scalar triple product of the vector BC, the normal vector, and the offset vector to
                // determine if the offset points to the left or the right of the control line.
                double tripleProduct = perpendicular.dot3(offset);
                if (tripleProduct < 0)
                {
                    offset = offset.multiply3(-1);
                }
            }
            else
            {
                // If this is the last control point then don't consider the surrounding points, just compute an offset
                // perpendicular to the control line.
                offset = perpendicular.normalize3();
            }

            // Determine the length of the offset vector that will keep the left and right lines parallel to the control
            // line.
            Angle theta = vBC.angleBetween3(offset);
            double length = halfWidth / theta.sin();
            offset = offset.multiply3(length);

            // Determine the left and right points by applying the offset.
            Vec4 pRight = pB.add3(offset);
            Vec4 pLeft = pB.subtract3(offset);

            // Convert cartesian points to geographic.
            Position posLeft = globe.computePositionFromPoint(pLeft);
            Position posRight = globe.computePositionFromPoint(pRight);

            leftPositions.add(posLeft);
            rightPositions.add(posRight);
        }
    }

    /**
     * Create and configure the Path used to render this graphic.
     *
     * @return New path configured with defaults appropriate for this type of graphic.
     */
    protected Path createPath()
    {
        Path path = new Path();
        path.setFollowTerrain(true);
        path.setPathType(AVKey.GREAT_CIRCLE);
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // TODO how to handle altitude mode?
        path.setDelegateOwner(this);
        return path;
    }

    /** Determine active attributes for this frame. */
    protected void determineActiveAttributes()
    {
        ShapeAttributes shapeAttributes;
        if (this.isHighlighted())
        {
            shapeAttributes = this.paths[0].getHighlightAttributes();
            TacticalGraphicAttributes highlightAttributes = this.getHighlightAttributes();
            if (highlightAttributes != null)
            {
                if (shapeAttributes == null)
                {
                    shapeAttributes = new BasicShapeAttributes();

                    for (Path path : this.paths)
                    {
                        path.setHighlightAttributes(shapeAttributes);
                    }
                }

                this.applyDefaultAttributes(shapeAttributes);
                this.applyOverrideAttributes(highlightAttributes, shapeAttributes);
            }
        }
        else
        {
            shapeAttributes = this.paths[0].getAttributes();
            if (shapeAttributes == null)
            {
                shapeAttributes = new BasicShapeAttributes();

                for (Path path : this.paths)
                {
                    path.setAttributes(shapeAttributes);
                }
            }
            this.applyDefaultAttributes(shapeAttributes);

            TacticalGraphicAttributes normalAttributes = this.getAttributes();
            if (normalAttributes != null)
            {
                this.applyOverrideAttributes(normalAttributes, shapeAttributes);
            }
        }
    }
}
