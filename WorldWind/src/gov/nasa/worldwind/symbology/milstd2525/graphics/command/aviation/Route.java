/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.TacGrpSidc;
import gov.nasa.worldwind.util.Logging;

import java.util.*;

/**
 * Implementation of the aviation route graphics. This class implements the following graphics: <ul> <li>Air Corridor
 * (2.X.2.2.2.1)</li> <li>Minimum Risk Route (2.X.2.2.2.2)</li> <li>Standard Flight Route (2.X.2.2.2.3)</li>
 * <li>Unmanned Aircraft Route (2.X.2.2.2.4)</li> <li>Low Level Transit Route (2.X.2.2.2.5)</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class Route extends MilStd2525TacticalGraphic implements TacticalRoute, PreRenderable
{
    /** Width of the route if no width is specified in the modifiers. */
    public static final double DEFAULT_WIDTH = 2000;

    /** Path used to render the route. */
    protected List<Path> paths;

    /** Control points that define the shape. */
    protected Iterable<? extends Position> positions;

    /** Graphics drawn at the route control points. */
    protected Iterable<? extends TacticalPoint> children;

    /**
     * Indicates the graphics supported by this class.
     *
     * @return List of masked SIDC strings that identify graphics that this class supports.
     */
    public static List<String> getSupportedGraphics()
    {
        return Arrays.asList(
            TacGrpSidc.C2GM_AVN_LNE_ACDR,
            TacGrpSidc.C2GM_AVN_LNE_MRR,
            TacGrpSidc.C2GM_AVN_LNE_SAAFR,
            TacGrpSidc.C2GM_AVN_LNE_LLTR);
    }

    public Route(String sidc)
    {
        super(sidc);
    }

    /** {@inheritDoc} Overridden to apply the highlight state to child graphics. */
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

    /** {@inheritDoc} */
    public Iterable<? extends TacticalPoint> getControlPoints()
    {
        return this.children;
    }

    /** {@inheritDoc} */
    public void setControlPoints(Iterable<? extends TacticalPoint> points)
    {
        this.children = points;

        List<Position> newPositions = new ArrayList<Position>();

        double width = this.getWidth();

        for (TacticalPoint p : points)
        {
            // Set the circle's radius to the width of the route
            p.setModifier(SymbologyConstants.DISTANCE, width);

            // Assign the route as the point's delegate owner so that the entire route will highlight
            // as a unit.
            p.setDelegateOwner(this);
            newPositions.add(p.getPosition());
        }

        this.positions = newPositions;
    }

    /**
     * Indicates the width of the route, in meters.
     *
     * @return If the SymbologyConstants.DISTANCE modifier set, and is a Double, returns the value of this modifier.
     *         Otherwise returns a default width.
     */
    public double getWidth()
    {
        Object widthModifier = this.getModifier(SymbologyConstants.DISTANCE);
        if (widthModifier instanceof Double)
        {
            return (Double) widthModifier;
        }
        else
        {
            return DEFAULT_WIDTH;
        }
    }

    /**
     * Specifies the width of the route. Calling this method is equivalent to calling
     * <code>setModifier(SymbologyConstants.DISTANCE, value)</code>.
     *
     * @param width Width of the route, in meters.
     */
    public void setWidth(double width)
    {
        this.setModifier(SymbologyConstants.DISTANCE, width);
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

        this.positions = positions;

        // Move the control points to the new route positions
        Iterator<? extends Position> positionIterator = positions.iterator();
        Iterator<? extends TacticalPoint> childIterator = this.getControlPoints().iterator();
        while (positionIterator.hasNext() && childIterator.hasNext())
        {
            childIterator.next().setPosition(positionIterator.next());
        }

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
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.determineActiveAttributes();

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

    /** {@inheritDoc} */
    protected void applyDelegateOwner(Object owner)
    {
        if (this.paths != null)
        {
            for (Path path : this.paths)
            {
                path.setDelegateOwner(owner);
            }
        }

        if (this.children != null)
        {
            for (TacticalGraphic child : this.children)
            {
                child.setDelegateOwner(owner);
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

        this.paths = new ArrayList<Path>();

        double width = this.getWidth();

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
            perpendicular = perpendicular.normalize3().multiply3(width);

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
     * Create the text for the main label on this graphic.
     *
     * @return Text for the main label. May return null if there is no text.
     */
    protected String createLabelText()
    {
        StringBuilder sb = new StringBuilder();

        Object o = this.getModifier(SymbologyConstants.UNIQUE_DESIGNATION);
        if (o != null)
        {
            sb.append("Name: ");
            sb.append(o);
            sb.append("\n");
        }

        o = this.getModifier(SymbologyConstants.DISTANCE);
        if (o != null)
        {
            sb.append("Width: ");
            sb.append(o);
            sb.append(" m");
            sb.append("\n");
        }

        Object[] altitudes = TacticalGraphicUtil.getAltitudeRange(this);
        if (altitudes[0] != null)
        {
            sb.append("Min Alt: ");
            sb.append(altitudes[0]);
            sb.append("\n");
        }

        if (altitudes[1] != null)
        {
            sb.append("Max Alt: ");
            sb.append(altitudes[1]);
            sb.append("\n");
        }

        Object[] dates = TacticalGraphicUtil.getDateRange(this);
        if (dates[0] != null)
        {
            sb.append("DTG Start: ");
            sb.append(dates[0]);
            sb.append("\n");
        }

        if (dates[1] != null)
        {
            sb.append("DTG End: ");
            sb.append(dates[1]);
        }

        return sb.toString();
    }

    @Override
    protected void createLabels()
    {
        String labelText = this.createLabelText();
        if (labelText == null)
        {
            return;
        }

        Label label = this.addLabel(labelText);
        label.setTextAlign(AVKey.LEFT);
        label.setOffset(new Offset(0d, 0d, AVKey.FRACTION, AVKey.FRACTION));

        Iterator<? extends Position> iterator = this.getPositions().iterator();

        // Create a label for each segment of the route
        while (iterator.hasNext())
        {
            iterator.next();

            // Add a label if this is not the last control point
            if (iterator.hasNext())
            {
                this.addLabel(this.getGraphicLabel() + " " + this.getText());
            }
        }
    }

    /**
     * Return the string that identifies this type of route.
     *
     * @return The string the determines the type of route, such as "AC" for "Air Corridor".
     */
    protected String getGraphicLabel()
    {
        String code = this.maskedSymbolCode;

        if (TacGrpSidc.C2GM_AVN_LNE_ACDR.equals(code))
            return "AC";
        else if (TacGrpSidc.C2GM_AVN_LNE_MRR.equals(code))
            return "MRR";
        else if (TacGrpSidc.C2GM_AVN_LNE_SAAFR.equals(code))
            return "SAAFR";
        else if (TacGrpSidc.C2GM_AVN_LNE_LLTR.equals(code))
            return "LLTR";
        else if (TacGrpSidc.C2GM_AVN_LNE_UAR.equals(code))
            return "UA";

        return "";
    }

    /**
     * Compute the position for the area's main label. This position indicates the position of the first line of the
     * label. If there are more lines, they will be arranged South of the first line. This method places the label
     * between the first to control points on the route, and to the side of the route.
     *
     * @param dc Current draw context.
     */
    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        Iterator<? extends Position> iterator = this.getPositions().iterator();

        Position posA = iterator.next();

        int i = 0;
        while (iterator.hasNext())
        {
            Position posB = iterator.next();
            Position midpoint = Position.interpolate(0.5, posA, posB);

            // Compute the main label position on the first iteration
            if (i == 0)
            {
                // Position the main label to the side of the first segment
                this.labels.get(i).setPosition(this.computeMainLabelPosition(dc, midpoint, posB));
                i += 1;
            }

            Label label = this.labels.get(i);

            // Position segment label at the midpoint of the segment
            label.setPosition(midpoint);

            // Orient label along the line from A to B
            label.setOrientationPosition(posB);

            i += 1;
            posA = posB;
        }
    }

    /**
     * Compute the position of the graphic's main label. This label is positioned to the side of the first segment along
     * the route.
     *
     * @param dc       Current draw context.
     * @param midpoint Midpoint of the first route segment.
     * @param posB     End point of the first route segment.
     *
     * @return The position of the main label.
     */
    protected Position computeMainLabelPosition(DrawContext dc, Position midpoint, Position posB)
    {
        Globe globe = dc.getGlobe();

        Vec4 pMid = globe.computePointFromPosition(midpoint);
        Vec4 pB = globe.computePointFromPosition(posB);
        Vec4 normal = globe.computeSurfaceNormalAtPoint(pMid);

        Vec4 vMB = pB.subtract3(pMid);

        // Compute a vector perpendicular to the route, at the midpoint of the first two control points
        Vec4 perpendicular = vMB.cross3(normal);
        perpendicular = perpendicular.normalize3().multiply3(
            DEFAULT_WIDTH * 2); // TODO arbitrary offset, should be based on size of label

        // Position the label to the side of the route
        Vec4 pLabel = pMid.add3(perpendicular);

        return globe.computePositionFromPoint(pLabel);
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
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        path.setDelegateOwner(this.getActiveDelegateOwner());
        path.setAttributes(this.getActiveShapeAttributes());
        return path;
    }
}
