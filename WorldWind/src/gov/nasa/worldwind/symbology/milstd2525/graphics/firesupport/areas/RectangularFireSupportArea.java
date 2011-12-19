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
import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * Implementation of rectangular Fire Support graphics. This class implements the following graphics:
 *
 * <ul>
 *  <li>Free Fire Area (FFA), Rectangular (2.X.4.3.2.3.2)</li>
 *  <li>Restrictive Fire Area (RFA), Rectangular (2.X.4.3.2.5.2)</li>
 * </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class RectangularFireSupportArea extends MilStd2525TacticalGraphic implements TacticalQuad, PreRenderable
{
    /** Function ID for the Free Fire Area graphic (2.X.4.3.2.3.2). */
    public final static String FUNCTION_ID_FFA = "ACFR--";
    /** Function ID for the Restrictive Fire Area graphic (2.X.4.3.2.5.2). */
    public final static String FUNCTION_ID_RFA = "ACRR--";

    protected Iterable<? extends Position> positions;
    protected SurfaceQuad quad;

    protected boolean shapeInvalid;

    /** Create a new target. */
    public RectangularFireSupportArea()
    {
        this.quad = this.createShape();
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_FIRE_SUPPORT;
    }

    /** {@inheritDoc} */
    public double getWidth()
    {
        return this.quad.getHeight();
    }

    /** {@inheritDoc} */
    public void setWidth(double width)
    {
        //noinspection SuspiciousNameCombination
        this.quad.setHeight(width);
    }

    /** {@inheritDoc} */
    public double getLength()
    {
        return this.quad.getWidth();
    }

    /** {@inheritDoc} */
    public void setLength(double length)
    {
        this.quad.setWidth(length);
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
        try
        {
            Position pos1 = iterator.next();
            Position pos2 = iterator.next();

            LatLon center = LatLon.interpolateGreatCircle(0.5, pos1, pos2);
            this.quad.setCenter(center);

            Angle heading = LatLon.greatCircleAzimuth(pos2, pos1);
            this.quad.setHeading(heading.subtract(Angle.POS90));

            this.positions = positions;
            this.shapeInvalid = true; // Need to recompute quad size
        }
        catch (NoSuchElementException e)
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setModifier(String modifier, Object value)
    {
        if (SymbologyConstants.DISTANCE.equals(modifier))
        {
            if (value instanceof Double)
            {
                this.setWidth((Double) value);
            }
            else if (value instanceof Iterable)
            {
                // Only use the first value of the iterable. This graphic uses two control points and a width.
                Iterator iterator = ((Iterable) value).iterator();
                this.setWidth((Double) iterator.next());
            }
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
        if (SymbologyConstants.DISTANCE.equals(modifier))
            return this.getWidth();
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

        if (this.shapeInvalid)
        {
            this.computeQuadSize(dc);
            this.shapeInvalid = false;
        }

        this.determineActiveAttributes();
        this.quad.preRender(dc);
    }

    protected void computeQuadSize(DrawContext dc)
    {
        if (this.positions == null)
            return;

        Iterator<? extends Position> iterator = this.positions.iterator();

        Position pos1 = iterator.next();
        Position pos2 = iterator.next();

        Angle angularDistance = LatLon.greatCircleDistance(pos1, pos2);
        double length = angularDistance.radians * dc.getGlobe().getRadius();

        this.quad.setWidth(length);
    }

    /**
     * Render the quad.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        this.quad.render(dc);
    }

    /** Create labels for the graphic. */
    @Override
    protected void createLabels()
    {
        FireSupportTextBuilder textBuilder = new FireSupportTextBuilder();
        String text = textBuilder.createText(this);
        if (!WWUtil.isEmpty(text))
        {
            this.addLabel(text);
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