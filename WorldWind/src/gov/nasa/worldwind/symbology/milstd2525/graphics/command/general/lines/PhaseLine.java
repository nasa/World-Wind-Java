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
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.util.Iterator;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class PhaseLine extends MilStd2525TacticalGraphic implements PreRenderable
{
    public final static String FUNCTION_ID = "GLP---";

    protected Path path;
    protected SurfaceText labelStart;
    protected SurfaceText labelEnd;

    public PhaseLine()
    {
        this.path = new Path();
        this.path.setFollowTerrain(true);
        this.path.setPathType(AVKey.GREAT_CIRCLE);
        this.path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // TODO how to handle altitude mode?
        this.path.setDelegateOwner(this);
        this.setText("");
    }

    public void setPositions(Iterable<? extends Position> positions)
    {
        this.path.setPositions(positions);
    }

    public Iterable<? extends Position> getPositions()
    {
        return this.path.getPositions();
    }

    @Override
    public void setText(String text)
    {
        super.setText(text);

        StringBuilder sb = new StringBuilder();
        sb.append("PL");
        if (!WWUtil.isEmpty(text))
            sb.append(" ").append(text);

        String fullText = sb.toString();

        this.labelStart = new SurfaceText(fullText, Position.ZERO);
        this.labelEnd = new SurfaceText(fullText, Position.ZERO);
    }

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

    public Position getReferencePosition()
    {
        return this.path.getReferencePosition();
    }

    public void move(Position position)
    {
        this.path.move(position);
    }

    public void moveTo(Position position)
    {
        this.path.moveTo(position);
    }

    public void preRender(DrawContext dc)
    {
        this.determineLabelPositions(dc);

        // If the attributes have not been created yet, create them now.
        // The default attributes are determined by the symbol code.
        if (this.normalAttributes == null)
        {
            TacticalGraphicAttributes attrs = this.createDefaultAttributes();
            this.setAttributes(attrs);
            this.path.setAttributes(attrs);

            Color color = attrs.getOutlineMaterial().getDiffuse();
            this.labelStart.setColor(color);
            this.labelEnd.setColor(color);
        }

        this.labelStart.preRender(dc);
        this.labelEnd.preRender(dc);
    }

    public void render(DrawContext dc)
    {
        this.path.render(dc);
    }

    protected TacticalGraphicAttributes createDefaultAttributes()
    {
        TacticalGraphicAttributes attrs = new BasicTacticalGraphicAttributes();

        String identity = this.getStandardIdentity();
        if (SymbolCode.IDENTITY_FRIEND.equals(identity))
        {
            attrs.setOutlineMaterial(Material.BLACK);
        }
        else if (SymbolCode.IDENTITY_HOSTILE.equals(identity))
        {
            attrs.setOutlineMaterial(Material.RED);
        }

        String status = this.getStatus();
        if (SymbolCode.STATUS_ANTICIPATED.equals(status))
        {
            attrs.setOutlineStippleFactor(6);
            attrs.setOutlineStipplePattern((short) 0xAAAA);
        }

        return attrs;
    }

    public String getFunctionId()
    {
        return FUNCTION_ID;
    }
}
