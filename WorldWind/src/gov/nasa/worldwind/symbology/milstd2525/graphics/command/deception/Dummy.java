/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.deception;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.TacticalGraphicAttributes;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * Implementation of the Dummy graphic (hierarchy 2.X.2.3.1, SIDC: G*GPPD----****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class Dummy extends MilStd2525TacticalGraphic
{
    public final static String FUNCTION_ID = "PD----";

    protected Path path;

    protected long frameTimestamp = -1L;

    public Dummy()
    {
        this.path = this.createPath();
    }

    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    public void doRenderGraphic(DrawContext dc)
    {
        if (this.frameTimestamp != dc.getFrameTimeStamp())
        {
            this.determineActiveAttributes();
            this.frameTimestamp = dc.getFrameTimeStamp();
        }

        this.path.render(dc);
    }

    /**
     * {@inheritDoc}
     *
     * The dummy graphic requires exactly three control points. Any positions beyond the first three will be ignored.
     *
     * @throws IllegalArgumentException if less than three control points are provided.
     */
    public void setPositions(Iterable<? extends Position> positions)
    {
        if (positions == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            Iterator<? extends Position> iterator = positions.iterator();
            Position pt1 = iterator.next();
            Position pt2 = iterator.next();
            Position pt3 = iterator.next();

            this.path.setPositions(Arrays.asList(pt2, pt1, pt3));
        }
        catch (NoSuchElementException e)
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return this.path.getPositions();
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.path.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        this.path.move(position);
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.path.moveTo(position);
    }

    protected void determineActiveAttributes()
    {
        ShapeAttributes shapeAttributes;
        if (this.isHighlighted())
        {
            shapeAttributes = this.path.getHighlightAttributes();
            if (shapeAttributes == null)
            {
                shapeAttributes = new BasicShapeAttributes();
                this.path.setHighlightAttributes(shapeAttributes);
            }

            TacticalGraphicAttributes highlightAttributes = this.getHighlightAttributes();
            if (highlightAttributes != null)
            {
                this.applyDefaultAttributes(shapeAttributes);
                this.applyOverrideAttributes(highlightAttributes, shapeAttributes);
            }
        }
        else
        {
            shapeAttributes = this.path.getAttributes();
            if (shapeAttributes == null)
            {
                shapeAttributes = new BasicShapeAttributes();
                this.path.setAttributes(shapeAttributes);
            }
            this.applyDefaultAttributes(shapeAttributes);

            TacticalGraphicAttributes normalAttributes = this.getAttributes();
            if (normalAttributes != null)
            {
                this.applyOverrideAttributes(normalAttributes, shapeAttributes);
            }
        }
    }

    @Override
    protected void applyDefaultAttributes(ShapeAttributes attributes)
    {
        super.applyDefaultAttributes(attributes);

        attributes.setOutlineWidth(2.0);
        attributes.setOutlineStippleFactor(15);
        attributes.setOutlineStipplePattern((short) 0xAAAA);
    }

    protected Path createPath()
    {
        Path path = new Path();
        path.setFollowTerrain(true);
        path.setPathType(AVKey.GREAT_CIRCLE);
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // TODO how to handle altitude mode?
        path.setDelegateOwner(this);
        return path;
    }
}
