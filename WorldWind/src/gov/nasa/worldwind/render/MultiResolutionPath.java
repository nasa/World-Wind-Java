/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.render;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.Logging;

import java.util.Iterator;

/**
 * @author tag
 * @version $ID$
 */
public class MultiResolutionPath extends Path
{
    /**
     * This interface provides the means for the application to specify the algorithm used to determine the number of
     * specified positions skipped during path tessellation.
     * <p/>
     * This class overrides the method {@link Path#makePositions(DrawContext, PathData)}.
     */
    public interface SkipCountComputer
    {
        /**
         * Determines the number of positions to skip for the current viewing state. Determines the number of positions
         * to skip for the current viewing state.
         *
         * @param dc       the current draw context.
         * @param pathData this shape's current path data.
         *
         * @return the number of positions to skip when computing the tessellated or non-tessellated path.
         */
        public int computeSkipCount(DrawContext dc, PathData pathData);
    }

    public void setSkipCountComputer(SkipCountComputer computer)
    {
        if (computer == null)
        {
            String message = Logging.getMessage("nullValue.CallbackIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.skipCountComputer = computer;
    }

    /**
     * The default implementation of <code>SkipCountComputer</code>. This implementation returns a value of 4 when the
     * eye distance to the path is greater than 10e3, a value of 2 when the eye distance is greater than 1e3 meters but
     * less then 10e3, and a value of 1 when the eye distance is less than 1e3.
     */
    protected SkipCountComputer skipCountComputer = new SkipCountComputer()
    {
        public int computeSkipCount(DrawContext dc, PathData pathData)
        {
            double d = getDistanceMetric(dc, pathData);

            return d > 10e3 ? 4 : d > 1e3 ? 2 : 1;
        }
    };

    /**
     * Creates a path with specified positions. When the path is rendered, only path positions that are visually
     * distinct for the current viewing state are considered. The path adjusts the positions it uses as the view state
     * changes, using more of the specified positions as the eye point comes closer to the shape.
     * <p/>
     * Note: If fewer than two positions are specified, no path is drawn.
     *
     * @param positions the path positions. This reference is retained by this shape; the positions are not copied. If
     *                  any positions in the set change, {@link #setPositions(Iterable)} must be called to inform this
     *                  shape of the change.
     *
     * @throws IllegalArgumentException if positions is null.
     */
    public MultiResolutionPath(Iterable<? extends Position> positions)
    {
        super(positions);
    }

    /**
     * Creates a path with positions specified via a generic list.
     * <p/>
     * Note: If fewer than two positions is specified, the path is not drawn.
     *
     * @param positions the path positions. This reference is retained by this shape; the positions are not copied. If
     *                  any positions in the set change, {@link #setPositions(Iterable)} must be called to inform this
     *                  shape of the change.
     *
     * @throws IllegalArgumentException if positions is null.
     */
    /**
     * Creates a path with specified positions specified via a generic list. When the path is rendered, only path
     * positions that are visually distinct for the current viewing state are considered. The path adjusts the positions
     * it uses as the view state changes, using more of the specified positions as the eye point comes closer to the
     * shape.
     * <p/>
     * Note: If fewer than two positions are specified, no path is drawn.
     *
     * @param positions the path positions. This reference is retained by this shape; the positions are not copied. If
     *                  any positions in the set change, {@link #setPositions(Iterable)} must be called to inform this
     *                  shape of the change.
     *
     * @throws IllegalArgumentException if positions is null.
     */
    public MultiResolutionPath(Position.PositionList positions)
    {
        super(positions);
    }

    protected void makePositions(DrawContext dc, PathData pathData)
    {
        Iterator<? extends Position> iter = this.positions.iterator();
        Position posA = iter.next();
        this.addTessellatedPosition(posA, true, pathData); // add the first position of the path

        int skipCount = this.skipCountComputer.computeSkipCount(dc, pathData);

        // Tessellate each segment of the path.
        Vec4 ptA = this.computePoint(dc.getTerrain(), posA);

        for (int i = 1; iter.hasNext(); i++)
        {
            Position posB = iter.next();

            if (i % skipCount != 0 && iter.hasNext())
            {
                continue;
            }

            Vec4 ptB = this.computePoint(dc.getTerrain(), posB);

            if (iter.hasNext()) // if this is not the final position
            {
                // If the segment is very small or not visible, don't use it.
                if (this.isSmall(dc, ptA, ptB, 8) || !this.isSegmentVisible(dc, posA, posB, ptA, ptB))
                    continue;
            }

            this.makeSegment(dc, posA, posB, ptA, ptB, pathData);
            posA = posB;
            ptA = ptB;
        }
    }
}
