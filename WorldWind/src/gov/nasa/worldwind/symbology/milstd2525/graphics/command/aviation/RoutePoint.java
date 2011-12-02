/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * Implementation of aviation route control point graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Air Control Point (2.X.2.2.1.1)</li> <li>Communications Checkpoint (2.X.2.2.1.2)</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class RoutePoint extends MilStd2525TacticalGraphic implements TacticalPoint, PreRenderable
{
    /** Function ID for Air Control Point (2.X.2.2.1.1). */
    public final static String FUNCTION_ID_AIR_CONTROL = "APP---";
    /** Function ID for Communications Checkpoint (2.X.2.2.1.2). */
    public final static String FUNCTION_ID_COMMUNICATIONS_CHECKPOINT = "APC---";

    /** Radius of the point if no radius is specified in the modifiers. */
    public final double DEFAULT_RADIUS = 2000;

    protected SurfaceCircle circle;

    protected Object delegateOwner;

    /** Create a new control point. */
    public RoutePoint()
    {
        this.circle = this.createShape();
        this.circle.setRadius(DEFAULT_RADIUS);
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_COMMAND_CONTROL_GENERAL_MANEUVER;
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
        if (AVKey.RADIUS.equals(modifier) && (value instanceof Double))
            this.circle.setRadius((Double) value);
        else
            super.setModifier(modifier, value);
    }

    /** {@inheritDoc} */
    @Override
    public Object getModifier(String modifier)
    {
        if (AVKey.RADIUS.equals(modifier))
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

    /** {@inheritDoc} Overridden to apply the delegate owner to shapes used to draw the route point. */
    @Override
    protected void determineActiveAttributes()
    {
        super.determineActiveAttributes();

        // Apply the delegate owner to the circle, if an owner has been set. If no owner is set, make this graphic the
        // circle's owner. This allows
        Object owner = this.getDelegateOwner();
        if (owner != null)
            this.circle.setDelegateOwner(owner);
        else
            this.circle.setDelegateOwner(this);
    }

    /**
     * Render the polygon.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        // SurfaceCircle is not an ordered renderable
        if (!dc.isOrderedRenderingMode())
        {
            this.circle.render(dc);
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
        sb.append(this.getGraphicLabel());
        sb.append("\n");
        sb.append(this.getText());

        return sb.toString();
    }

    public String getGraphicLabel()
    {
        String functionId = this.getFunctionId();

        if (FUNCTION_ID_AIR_CONTROL.equals(functionId))
            return "ACP";
        else if (FUNCTION_ID_COMMUNICATIONS_CHECKPOINT.equals(functionId))
            return "CCP";

        return "";
    }

    @Override
    protected void createLabels()
    {
        String labelText = this.createLabelText();
        if (!WWUtil.isEmpty(labelText))
        {
            this.addLabel(labelText);
        }
    }

    /**
     * Determine the appropriate position for the graphic's labels.
     *
     * @param dc Current draw context.
     */
    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        LatLon center = this.circle.getCenter();
        this.labels.get(0).setPosition(new Position(center, 0));
    }

    protected SurfaceCircle createShape()
    {
        SurfaceCircle circle = new SurfaceCircle();
        circle.setAttributes(this.activeShapeAttributes);

        Object delegateOwner = this.getDelegateOwner();
        circle.setDelegateOwner(delegateOwner != null ? delegateOwner : this);

        return circle;
    }
}
