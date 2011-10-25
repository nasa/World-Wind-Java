/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.lines;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.TacticalGraphicAttributes;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.util.Iterator;

/**
 * Implementation of the Phase Line graphic (hierarchy: 2.X.2.1.2.4, SIDC G*GPGLP---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class PhaseLine extends MilStd2525TacticalGraphic implements PreRenderable
{
    /** Function ID for the Phase Line. */
    public final static String FUNCTION_ID = "GLP---";

    /** Path used to render the line. */
    protected Path path;
    /** Label rendered at the start of the line. */
    protected SurfaceText labelStart;
    /** Label rendered at the end of the line. */
    protected SurfaceText labelEnd;

    /** Create a new Phase Line. */
    public PhaseLine()
    {
        this.path = this.createPath();
    }

    /** {@inheritDoc} */
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    /** {@inheritDoc} */
    public void setPositions(Iterable<? extends Position> positions)
    {
        this.path.setPositions(positions);
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

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        if (this.labelStart == null)
        {
            this.createLabels();
        }

        this.determineActiveAttributes();
        this.determineLabelPositions(dc);

        if (this.isShowModifiers())
        {
            this.labelStart.preRender(dc);
            this.labelEnd.preRender(dc);
        }
    }

    /** {@inheritDoc} */
    public void doRenderGraphic(DrawContext dc)
    {
        this.path.render(dc);
    }

    /**
     * Create and configure the Path used to render this graphic.
     *
     * @return New path configured with defaults appropriate for this type of graphic.
     */
    protected Path createPath()
    {
        Path path = new Path();
        path.setFollowTerrain(true);
        path.setPathType(AVKey.GREAT_CIRCLE);
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // TODO how to handle altitude mode?
        path.setDelegateOwner(this);
        return path;
    }

    /**
     * Create labels for the start and end of the path.
     */
    protected void createLabels()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("PL");

        String text = this.getText();
        if (!WWUtil.isEmpty(text))
            sb.append(" ").append(text);

        String fullText = sb.toString();

        this.labelStart = new SurfaceText(fullText, Position.ZERO);
        this.labelEnd = new SurfaceText(fullText, Position.ZERO);
    }

    /**
     * Determine positions for the start and end labels.
     *
     * @param dc Current draw context.
     */
    protected void determineLabelPositions(DrawContext dc)
    {
        Iterator<? extends Position> iterator = this.path.getPositions().iterator();

        // Find the first two positions on the path
        Position first = iterator.next();
        Position second = iterator.next();

        // Find the last two positions on the path
        Position last = second;
        Position nextToLast = first;
        while (iterator.hasNext())
        {
            nextToLast = last;
            last = iterator.next();
        }

        // Position the labels at the ends of the path
        // TODO: figure better rules for positioning and sizing the labels
        Angle azimuth = LatLon.greatCircleAzimuth(second, first);
        LatLon ll = LatLon.greatCircleEndPosition(first, azimuth.radians, 500d / dc.getGlobe().getRadius());
        this.labelStart.setPosition(new Position(ll, 0));

        azimuth = LatLon.greatCircleAzimuth(nextToLast, last);
        ll = LatLon.greatCircleEndPosition(last, azimuth.radians, 500d / dc.getGlobe().getRadius());
        this.labelEnd.setPosition(new Position(ll, 0));
    }

    public void setHighlighted(boolean highlighted)
    {
        super.setHighlighted(highlighted);
        this.path.setHighlighted(highlighted);
    }

    /** Determine active attributes for this frame. */
    protected void determineActiveAttributes()
    {
        ShapeAttributes shapeAttributes;
        if (this.isHighlighted())
        {
            shapeAttributes = this.path.getHighlightAttributes();
            TacticalGraphicAttributes highlightAttributes = this.getHighlightAttributes();
            if (highlightAttributes != null)
            {
                if (shapeAttributes == null)
                {
                    shapeAttributes = new BasicShapeAttributes();
                    this.path.setHighlightAttributes(shapeAttributes);
                }

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
            Color color = shapeAttributes.getOutlineMaterial().getDiffuse();
            labelStart.setColor(color);
            labelEnd.setColor(color);
        }
    }
}
