/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.target;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.TacticalGraphicAttributes;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.util.*;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class CircularTarget extends MilStd2525TacticalGraphic implements PreRenderable
{
    public final static String FUNCTION_ID = "ATC---";

    protected SurfaceCircle circle;
    protected SurfaceText label;

    public CircularTarget()
    {
        this.circle = new SurfaceCircle();
        this.circle.setDelegateOwner(this);
    }

    /** {@inheritDoc} */
    public String getFunctionId()
    {
        return FUNCTION_ID;
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
        if (AVKey.DISTANCE.equals(modifier) && (value instanceof Double))
        {
            this.circle.setRadius((Double) value);
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
        if (AVKey.DISTANCE.equals(modifier))
            return this.circle.getRadius();
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

    @Override
    public void setText(String text)
    {
        super.setText(text);

        String fullText = this.createText(text);
        if (fullText != null)
        {
            this.label = new SurfaceText(fullText, Position.ZERO);
        }
        else
        {
            this.label = null;
        }
    }

    /** {@inheritDoc} Overridden to apply highlight to all parts of the graphic. */
    @Override
    public void setHighlighted(boolean highlighted)
    {
        super.setHighlighted(highlighted);
        this.circle.setHighlighted(highlighted);
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.determineActiveAttributes();

        if (this.label != null)
        {
            this.determineLabelPosition();
            this.label.preRender(dc);
        }

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

    /**
     * Render the labels.
     *
     * @param dc Current draw context.
     */
    @Override
    public void doRenderModifiers(DrawContext dc)
    {
        if (this.label != null)
        {
            this.label.render(dc);
        }
    }

    protected String createText(String text)
    {
        return text;
    }

    protected void determineLabelPosition()
    {
        this.label.setPosition(new Position(this.circle.getCenter(), 0));
    }

    protected int getPositionCount()
    {
        int count = 0;
        //noinspection UnusedDeclaration
        for (Position p : this.getPositions())
        {
            count++;
        }
        return count;
    }

    /** Determine active attributes for this frame. */
    protected void determineActiveAttributes()
    {
        ShapeAttributes shapeAttributes;
        if (this.isHighlighted())
        {
            shapeAttributes = this.circle.getHighlightAttributes();
            TacticalGraphicAttributes highlightAttributes = this.getHighlightAttributes();
            if (highlightAttributes != null)
            {
                if (shapeAttributes == null)
                {
                    shapeAttributes = new BasicShapeAttributes();
                    this.circle.setHighlightAttributes(shapeAttributes);
                }

                this.applyDefaultAttributes(shapeAttributes);
                this.applyOverrideAttributes(highlightAttributes, shapeAttributes);
            }
        }
        else
        {
            shapeAttributes = this.circle.getAttributes();
            if (shapeAttributes == null)
            {
                shapeAttributes = new BasicShapeAttributes();
                this.circle.setAttributes(shapeAttributes);
            }
            this.applyDefaultAttributes(shapeAttributes);

            TacticalGraphicAttributes normalAttributes = this.getAttributes();
            if (normalAttributes != null)
            {
                this.applyOverrideAttributes(normalAttributes, shapeAttributes);
            }

            Color color = shapeAttributes.getOutlineMaterial().getDiffuse();
            if (this.label != null)
            {
                this.label.setColor(color);
            }
        }
    }
}
