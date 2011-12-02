/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.lines;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.*;

import java.util.Iterator;

/**
 * Implementation of the Phase Line graphic (hierarchy: 2.X.2.1.2.4, SIDC G*GPGLP---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class PhaseLine extends MilStd2525TacticalGraphic
{
    /** Function ID for the Phase Line. */
    public final static String FUNCTION_ID = "GLP---";

    /** Path used to render the line. */
    protected Path path;

    /** Create a new Phase Line. */
    public PhaseLine()
    {
        this.path = this.createPath();
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
    public void doRenderGraphic(DrawContext dc)
    {
        if (!dc.isOrderedRenderingMode())
        {
            this.path.render(dc);
        }
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
        path.setAttributes(this.getActiveShapeAttributes());
        return path;
    }

    /** Create labels for the start and end of the path. */
    @Override
    protected void createLabels()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("PL");

        String text = this.getText();
        if (!WWUtil.isEmpty(text))
            sb.append(" ").append(text);

        String fullText = sb.toString();

        this.addLabel(fullText); // Start label
        this.addLabel(fullText); // End label
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
        this.labels.get(0).setPosition(new Position(ll, 0));

        azimuth = LatLon.greatCircleAzimuth(nextToLast, last);
        ll = LatLon.greatCircleEndPosition(last, azimuth.radians, 500d / dc.getGlobe().getRadius());
        this.labels.get(1).setPosition(new Position(ll, 0));
    }
}
