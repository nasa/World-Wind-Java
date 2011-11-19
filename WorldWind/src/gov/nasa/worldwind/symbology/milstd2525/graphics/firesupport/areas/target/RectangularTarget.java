/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas.target;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * Implementation of the Rectangular Target graphic (hierarchy 2.X.4.3.1.1, SIDC: G*FPATR---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
// TODO need to implement heading for SurfaceText
public class RectangularTarget extends MilStd2525TacticalGraphic implements PreRenderable
{
    public final static String FUNCTION_ID = "ATR---";

    protected SurfaceQuad quad;

    public RectangularTarget()
    {
        this.quad = this.createShape();
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
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

        this.quad.setCenter(iterator.next());
    }

    /** {@inheritDoc} */
    @Override
    public void setModifier(String modifier, Object value)
    {
        // Map Length modifier to the width field of the quad, and Width modifier to height. MIL-STD-2525 uses different
        // names for these dimensions than SurfaceQuad.
        if (AVKey.LENGTH.equals(modifier) && (value instanceof Double))
            this.quad.setWidth((Double) value);
        else if (AVKey.WIDTH.equals(modifier) && (value instanceof Double))
            this.quad.setHeight((Double) value);
        else if (AVKey.HEADING.equals(modifier) && (value instanceof Angle))
            this.quad.setHeading((Angle) value);
        else
            super.setModifier(modifier, value);
    }

    /** {@inheritDoc} */
    @Override
    public Object getModifier(String modifier)
    {
        // Map Length modifier to the width field of the quad, and Width modifier to height. MIL-STD-2525 uses different
        // names for these dimensions than SurfaceQuad.
        if (AVKey.WIDTH.equals(modifier))
            return this.quad.getHeight();
        else if (AVKey.LENGTH.equals(modifier))
            return this.quad.getWidth();
        else if (AVKey.HEADING.equals(modifier))
            return this.quad.getHeading();
        else
            return super.getModifier(modifier);
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return Arrays.asList(new Position(this.quad.getCenter(), 0));
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.quad.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        this.quad.move(position);
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.quad.moveTo(position);
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.determineActiveAttributes();
        this.quad.preRender(dc);
    }

    /**
     * Render the polygon.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        // SurfaceQuad is not an ordered renderable
        if (!dc.isOrderedRenderingMode())
        {
            this.quad.render(dc);
        }
    }

    /** Create labels for the start and end of the path. */
    @Override
    protected void createLabels()
    {
        String text = this.getText();
        if (!WWUtil.isEmpty(text))
        {
            this.addLabel(this.getText());
        }
    }

    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        this.labels.get(0).setPosition(new Position(this.quad.getCenter(), 0));
    }

    protected SurfaceQuad createShape()
    {
        SurfaceQuad quad = new SurfaceQuad();
        quad.setDelegateOwner(this);
        quad.setAttributes(this.getActiveShapeAttributes());
        return quad;
    }
}
