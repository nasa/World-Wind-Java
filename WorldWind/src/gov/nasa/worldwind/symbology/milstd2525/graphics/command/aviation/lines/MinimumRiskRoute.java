/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.lines;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.points.AbstractRoutePoint;
import gov.nasa.worldwind.util.Logging;

import java.util.*;

/**
 * Implementation of the Minimum Risk Route graphic (hierarchy 2.X.2.2.2.2, SIDC: G*GPALM---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class MinimumRiskRoute extends MilStd2525TacticalGraphic implements PreRenderable
{
    /** Function ID for the Phase Line. */
    public final static String FUNCTION_ID = "ALM---";

    // TODO how wide should the route be?
    public static final double DEFAULT_WIDTH = 2000;

    /** Path used to render the line. */
    protected List<Path> paths;

    /** Control points that define the shape. */
    protected Iterable<? extends Position> positions;

    /** Graphics drawn at the route control points. */
    protected List<TacticalGraphic> children;

    /** Create a route graphic. */
    public MinimumRiskRoute()
    {
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_COMMAND_CONTROL_GENERAL_MANEUVER;
    }

    /** {@inheritDoc} */
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    /** {@inheritDoc} */
    @Override
    public Object getModifier(String modifier)
    {
        if (AVKey.GRAPHIC.equals(modifier))
        {
            return this.children;
        }
        return super.getModifier(modifier);
    }

    /** {@inheritDoc} */
    @Override
    public void setModifier(String modifier, Object value)
    {
        if (AVKey.GRAPHIC.equals(modifier))
        {
            if (value instanceof Iterable)
            {
                this.setChildGraphics((Iterable) value);
            }
            else if (value instanceof TacticalGraphic)
            {
                this.setChildGraphics(Arrays.asList((TacticalGraphic) value));
            }
        }
        else
        {
            super.setModifier(modifier, value);
        }
    }

    /**
     * {@inheritDoc} Overridden to apply the highlight state to child graphics. */
    @Override
    public void setHighlighted(boolean highlighted)
    {
        super.setHighlighted(highlighted);

        // Apply the highlight state to the child graphics
        if (this.children != null)
        {
            for (TacticalGraphic child : this.children)
            {
                child.setHighlighted(highlighted);
            }
        }
    }

    /**
     * Set child graphics that will be drawn at the route control points. Child graphics may be Air Control Points
     * (2.X.2.2.1.1) or Communications Checkpoints (2.X.2.2.1.2).
     *
     * @param graphics Iterable of child graphics. Any members which are not Air Control Points or Communications
     *                 Checkpoints will be ignored.
     */
    public void setChildGraphics(Iterable graphics)
    {
        this.children = new ArrayList<TacticalGraphic>();

        // Go through the list and pull out all the valid children.
        for (Object o : graphics)
        {
            if (o instanceof AbstractRoutePoint)
            {
                AbstractRoutePoint child = (AbstractRoutePoint) o;

                // Set the circle's radius to the width of the route
                child.setModifier(AVKey.RADIUS, DEFAULT_WIDTH); // TODO how to set width

                // Assign the route as the point's delegate owner so that the entire route will highlight
                // as a unit.
                child.setDelegateOwner(this);

                this.children.add(child);
            }
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
        }
        catch (NoSuchElementException e)
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.positions = positions;
        this.paths = null; // Need to regenerate paths
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return this.positions;
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
    public void preRender(DrawContext dc)
    {
        if (this.children != null)
        {
            for (TacticalGraphic child : this.children)
            {
                if (child instanceof PreRenderable)
                {
                    ((PreRenderable) child).preRender(dc);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void doRenderGraphic(DrawContext dc)
    {
        if (this.paths == null)
        {
            this.createPaths(dc);
        }

        for (Path path : this.paths)
        {
            path.render(dc);
        }

        if (this.children != null)
        {
            for (TacticalGraphic child : this.children)
            {
                child.render(dc);
            }
        }
    }

    /**
     * Create the paths used to draw the route.
     *
     * @param dc Current draw context.
     */
    protected void createPaths(DrawContext dc)
    {
        Globe globe = dc.getGlobe();

        this.paths = new ArrayList<Path>(); // TODO could set size here

        Iterator<? extends Position> iterator = this.getPositions().iterator();

        Position posA = iterator.next();

        Vec4 pA = globe.computePointFromPosition(posA);
        Vec4 pB;

        Vec4 normal = globe.computeSurfaceNormalAtPoint(pA);

        while (iterator.hasNext())
        {
            Position posB = iterator.next();
            pB = globe.computePointFromPosition(posB);

            Vec4 vAB = pB.subtract3(pA);

            Vec4 perpendicular = vAB.cross3(normal);
            perpendicular = perpendicular.normalize3().multiply3(DEFAULT_WIDTH); // TODO what to use for width?

            Vec4 pStart = pA.add3(perpendicular);
            Vec4 pEnd = pB.add3(perpendicular);

            Position posStart = globe.computePositionFromPoint(pStart);
            Position posEnd = globe.computePositionFromPoint(pEnd);

            Path path = this.createPath(posStart, posEnd);
            this.paths.add(path);

            pStart = pA.subtract3(perpendicular);
            pEnd = pB.subtract3(perpendicular);

            posStart = globe.computePositionFromPoint(pStart);
            posEnd = globe.computePositionFromPoint(pEnd);

            path = this.createPath(posStart, posEnd);
            this.paths.add(path);

            pA = pB;
        }
    }

    /**
     * Create between two points and configure the Path.
     *
     * @param start First position
     * @param end   Second position
     *
     * @return New path configured with defaults appropriate for this type of graphic.
     */
    protected Path createPath(Position start, Position end)
    {
        Path path = new Path(start, end);
        path.setFollowTerrain(true);
        path.setPathType(AVKey.GREAT_CIRCLE);
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // TODO how to handle altitude mode?
        path.setDelegateOwner(this);
        path.setAttributes(this.getActiveShapeAttributes());
        return path;
    }
}
