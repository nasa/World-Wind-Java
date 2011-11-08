/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.lines.axis;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.graphics.command.offense.lines.AbstractOffenseArrow;

import java.util.*;

/**
 * Implementation of the Aviation offensive graphic (hierarchy 2.X.2.5.2.1.1, SIDC: G*GPOLAV--****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class Aviation extends AbstractOffenseArrow
{
    /** Function ID for this graphic. */
    public final static String FUNCTION_ID = "OLAV--";

    /** Create a new Aviation graphic. */
    public Aviation()
    {
        this(1);
    }

    /**
     * Create a new Aviation graphic, composed of more than one path. This constructor is for use by subclasses that
     * extend the base Aviation graphic by adding additional paths.
     *
     * @param numPaths Number of paths to create.
     */
    protected Aviation(int numPaths)
    {
        super(numPaths);
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
    protected double createArrowHeadPositions(List<Position> leftPositions, List<Position> rightPositions,
        List<Position> arrowHeadPositions, Globe globe)
    {
        double halfWidth = super.createArrowHeadPositions(leftPositions, rightPositions, arrowHeadPositions, globe);

        // Aviation graphic is the same as the base graphic, except that the left and right lines cross between
        // points 1 and 2. Swap the control points in the left and right lists to achieve this effect.
        if (rightPositions.size() > 0 && leftPositions.size() > 0)
        {
            Position temp = leftPositions.get(0);

            leftPositions.set(0, rightPositions.get(0));
            rightPositions.set(0, temp);
        }

        // Arrow head points need to be in reverse order to match the reversed first line positions.
        Collections.reverse(arrowHeadPositions);

        return halfWidth;
    }
}
