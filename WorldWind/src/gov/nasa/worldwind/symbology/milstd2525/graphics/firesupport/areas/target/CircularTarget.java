/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.target;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.util.*;

/**
 * Implementation of the Circular Target graphic (hierarchy 2.X.4.3.1.2, SIDC: G*FPATC---****X).
 *
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
        this.circle = this.createShape();
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbolCode.CATEGORY_FIRE_SUPPORT_COMBAT_SERVICE_SUPPORT;
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
            this.circle.setRadius((Double) value);
        else
            super.setModifier(modifier, value);
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
            this.determineLabelAttributes();
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

    protected String createText(String text)
    {
        return text;
    }

    protected void determineLabelPosition()
    {
        this.label.setPosition(new Position(this.circle.getCenter(), 0));
    }

    protected void determineLabelAttributes()
    {
        Color color = this.getLabelMaterial().getDiffuse();

        Font font = this.getActiveOverrideAttributes().getTextModifierFont();
        if (font == null)
            font = DEFAULT_FONT;

        this.label.setColor(color);
        this.label.setFont(font);
    }

    protected SurfaceCircle createShape()
    {
        SurfaceCircle circle = new SurfaceCircle();
        circle.setDelegateOwner(this);
        circle.setAttributes(this.activeShapeAttributes);
        return circle;
    }
}
