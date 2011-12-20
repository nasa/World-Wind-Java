/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.Logging;

import java.util.*;

/**
 * Base class for circular area graphics.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class AbstractCircularGraphic extends MilStd2525TacticalGraphic implements TacticalCircle, PreRenderable
{
    protected SurfaceCircle circle;
    protected Object delegateOwner;

    /** Create a new circular area. */
    public AbstractCircularGraphic()
    {
        this.circle = this.createShape();
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
    }

    /** {@inheritDoc} */
    public double getRadius()
    {
        return this.circle.getRadius();
    }

    /** {@inheritDoc} */
    public void setRadius(double radius)
    {
        this.circle.setRadius(radius);
    }

    /** {@inheritDoc} */
    public Position getPosition()
    {
        return this.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void setPosition(Position position)
    {
        this.move(position);
    }

    /** {@inheritDoc} */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /** {@inheritDoc} */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
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

        this.circle.setCenter(iterator.next());
    }

    /** {@inheritDoc} */
    @Override
    public void setModifier(String modifier, Object value)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier) && (value instanceof Double))
            this.setRadius((Double) value);
        else
            super.setModifier(modifier, value);
    }

    /** {@inheritDoc} */
    @Override
    public Object getModifier(String modifier)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier))
            return this.getRadius();
        else
            return super.getModifier(modifier);
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return Arrays.asList(new Position(this.circle.getCenter(), 0));
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.circle.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        this.circle.move(position);
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.circle.moveTo(position);
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.determineActiveAttributes();

        this.circle.preRender(dc);
    }

    /**
     * Render the polygon.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        this.circle.render(dc);
    }

    /** {@inheritDoc} Overridden to apply the delegate owner to shapes used to draw the route point. */
    @Override
    protected void determineActiveAttributes()
    {
        super.determineActiveAttributes();

        // Apply the delegate owner to the circle, if an owner has been set. If no owner is set, make this graphic the
        // circle's owner.
        Object owner = this.getDelegateOwner();
        if (owner != null)
            this.circle.setDelegateOwner(owner);
        else
            this.circle.setDelegateOwner(this);
    }

    protected SurfaceCircle createShape()
    {
        SurfaceCircle circle = new SurfaceCircle();
        circle.setDelegateOwner(this);
        circle.setAttributes(this.activeShapeAttributes);
        return circle;
    }
}
