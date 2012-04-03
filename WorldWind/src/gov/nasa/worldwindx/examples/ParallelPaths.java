/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwindx.examples;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Example of how to draw parallel paths. This example specifies the positions of a control path, and then computes four
 * paths that run parallel to the control path.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class ParallelPaths extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            // Create list of positions along the control line.
            List<Position> positions = Arrays.asList(
                Position.fromDegrees(49.0457, -122.8115, 100),
                Position.fromDegrees(49.0539, -122.8091, 110),
                Position.fromDegrees(49.0621, -122.7937, 120),
                Position.fromDegrees(49.0681, -122.8044, 130),
                Position.fromDegrees(49.0682, -122.7730, 140),
                Position.fromDegrees(49.0482, -122.7764, 150),
                Position.fromDegrees(49.0498, -122.7466, 140),
                Position.fromDegrees(49.0389, -122.7453, 130),
                Position.fromDegrees(49.0321, -122.7759, 120),
                Position.fromDegrees(49.0394, -122.7689, 110),
                Position.fromDegrees(49.0629, -122.7666, 100));

            // We will generate four paths parallel to the control path. Allocate lists to store the positions of these
            // paths.
            List<Position> pathPositions1 = new ArrayList<Position>();
            List<Position> pathPositions2 = new ArrayList<Position>();
            List<Position> pathPositions3 = new ArrayList<Position>();
            List<Position> pathPositions4 = new ArrayList<Position>();

            Globe globe = getWwd().getModel().getGlobe();

            // Generate two sets of lines parallel to the control line. The positions will be added to the pathPosition lists.
            this.generateParallelLines(positions, pathPositions1, pathPositions2, 50, globe);
            this.generateParallelLines(positions, pathPositions3, pathPositions4, 100, globe);

            // Create Path objects from the position lists, and add them to a layer.
            RenderableLayer layer = new RenderableLayer();
            this.addPath(layer, positions, "Control Path");
            this.addPath(layer, pathPositions1, "Path 1");
            this.addPath(layer, pathPositions2, "Path 2");
            this.addPath(layer, pathPositions3, "Path 3");
            this.addPath(layer, pathPositions4, "Path 4");

            insertBeforePlacenames(getWwd(), layer);
        }

        public static class ExamplePositionColors implements Path.PositionColors
        {
            public Color getColor(Position position, int ordinal)
            {
                // Color the positions based on their altitude.
                double altitude = position.getAltitude();
                return altitude < 115 ? Color.GREEN : altitude < 135 ? Color.BLUE : Color.RED;
            }
        }

        protected void addPath(RenderableLayer layer, List<Position> positions, String displayName)
        {
            ShapeAttributes attrs = new BasicShapeAttributes();
            attrs.setOutlineWidth(5);

            Path path = new Path(positions);
            path.setPathType(AVKey.LINEAR);
            path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            path.setAttributes(attrs);
            path.setValue(AVKey.DISPLAY_NAME, displayName);
            layer.addRenderable(path);

            // Show how to make the colors vary along the paths.
            path.setPositionColors(new ExamplePositionColors());
        }

        /**
         * Create positions that describe lines parallel to a control line.
         *
         * @param controlPositions List of positions along the control line.
         * @param leftPositions    List to receive positions on the left line.
         * @param rightPositions   List to receive positions on the right line.
         * @param distance         Distance from the center line to the left and right lines.
         * @param globe            Globe used to compute positions.
         */
        protected void generateParallelLines(List<Position> controlPositions, List<Position> leftPositions,
            List<Position> rightPositions, double distance, Globe globe)
        {
            if (controlPositions == null || leftPositions == null || rightPositions == null)
            {
                String message = Logging.getMessage("nullValue.PositionListIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }
            if (globe == null)
            {
                String message = Logging.getMessage("nullValue.GlobeIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            // Starting at the start of the line, take points three at a time. B is the current control point, A is the next
            // point in the line, and C is the previous point. We need to a find a vector that bisects angle ABC.
            //       B
            //       ---------> C
            //      /
            //     /
            //    /
            // A /

            Iterator<? extends Position> iterator = controlPositions.iterator();

            Position posB = iterator.next();
            Position posA = iterator.next();

            // Compute points, ignoring elevation. We will project the path onto the surface of the globe, compute the
            // parallel points on the surface, and then raise the points to the correct elevation.
            Vec4 ptA = globe.computePointFromLocation(posA);
            Vec4 ptB = globe.computePointFromLocation(posB);
            Vec4 ptC;

            // We'll keep track of the offset used to compute parallel points as we go through the list. We need this
            // to handle cases where the position list contains sequential co-located points.
            Vec4 prevOffset = null;

            // Compute side points at the start of the line.
            prevOffset = this.generateParallelPoints(ptB, null, ptA, leftPositions, rightPositions, distance,
                posB.getElevation(), globe, prevOffset);

            double prevElevation;
            while (iterator.hasNext())
            {
                prevElevation = posA.getElevation();
                posA = iterator.next();

                ptC = ptB;
                ptB = ptA;
                ptA = globe.computePointFromLocation(posA);

                prevOffset = this.generateParallelPoints(ptB, ptC, ptA, leftPositions, rightPositions, distance,
                    prevElevation, globe, prevOffset);
            }

            // Compute side points at the end of the line.
            this.generateParallelPoints(ptA, ptB, null, leftPositions, rightPositions, distance, posA.getElevation(),
                globe, prevOffset);
        }

        /**
         * Compute points on either side of a line segment. This method requires a point on the line, and either a next
         * point, previous point, or both.
         *
         * @param point          Center point about which to compute side points.
         * @param prev           Previous point on the line. May be null if {@code next} is non-null.
         * @param next           Next point on the line. May be null if {@code prev} is non-null.
         * @param leftPositions  Left position will be added to this list.
         * @param rightPositions Right position will be added to this list.
         * @param distance       Distance from the center line to the left and right lines.
         * @param elevation      Elevation at which to place the generated positions.
         * @param globe          Globe used to compute positions.
         * @param previousOffset Offset vector from a previous call to this method. May be null.
         *
         * @return Offset vector that should be passed back to this method on the next call for a list of positions.
         *         (Used to generate parallel points when a position list contains sequential co-located positions.)
         */
        protected Vec4 generateParallelPoints(Vec4 point, Vec4 prev, Vec4 next, List<Position> leftPositions,
            List<Position> rightPositions, double distance, double elevation, Globe globe, Vec4 previousOffset)
        {
            if ((point == null) || (prev == null && next == null))
            {
                String message = Logging.getMessage("nullValue.PointIsNull");
                Logging.logger().severe(message);
                throw new IllegalArgumentException(message);
            }

            Vec4 offset;
            Vec4 normal = globe.computeSurfaceNormalAtPoint(point);

            // Compute vector in the direction backward along the line.
            Vec4 backward = (prev != null) ? prev.subtract3(point) : point.subtract3(next);

            // Compute a vector perpendicular to segment BC, and the globe normal vector.
            Vec4 perpendicular = backward.cross3(normal);

            // If the current point is co-located with either the next or prev points, then reuse the previously computed offset.
            if (point.equals(prev) || (point.equals(next)) && previousOffset != null)
            {
                offset = previousOffset;
            }
            // If both next and previous points are supplied then calculate the angle that bisects the angle current, next, prev.
            else if (next != null && prev != null && !Vec4.areColinear(prev, point, next))
            {
                // Compute vector in the forward direction.
                Vec4 forward = next.subtract3(point);

                // Calculate the vector that bisects angle ABC.
                offset = forward.normalize3().add3(backward.normalize3());
                offset = offset.normalize3();

                // Determine the length of the offset vector that will keep the left and right lines parallel to the control
                // line.
                Angle theta = backward.angleBetween3(offset);

                // If the angle is less than 1/10 of a degree than treat this segment as if it were linear.
                double length;
                if (theta.degrees > 0.1)
                    length = distance / theta.sin();
                else
                    length = distance;

                // Compute the scalar triple product of the vector BC, the normal vector, and the offset vector to
                // determine if the offset points to the left or the right of the control line.
                double tripleProduct = perpendicular.dot3(offset);
                if (tripleProduct < 0)
                {
                    offset = offset.multiply3(-1);
                }

                offset = offset.multiply3(length);
            }
            else
            {
                offset = perpendicular.normalize3();
                offset = offset.multiply3(distance);
            }

            // Determine the left and right points by applying the offset.
            Vec4 ptRight = point.add3(offset);
            Vec4 ptLeft = point.subtract3(offset);

            // Convert cartesian points to geographic.
            Position posLeft = new Position(globe.computePositionFromPoint(ptLeft), elevation);
            Position posRight = new Position(globe.computePositionFromPoint(ptRight), elevation);

            leftPositions.add(posLeft);
            rightPositions.add(posRight);

            return offset;
        }
    }

    public static void main(String[] args)
    {
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 49.05);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, -122.78);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 8000);

        ApplicationTemplate.start("World Wind Multi Path", AppFrame.class);
    }
}
