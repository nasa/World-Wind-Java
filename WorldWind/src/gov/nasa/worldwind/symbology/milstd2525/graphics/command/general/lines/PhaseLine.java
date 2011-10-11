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
import gov.nasa.worldwind.symbology.milstd2525.SymbolCode;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.util.Iterator;

/**
 * @author pabercrombie
 * @version $Id$
 */
// TODO: text annotation at the ends of the line
public class PhaseLine extends Path implements TacticalShape, PreRenderable
{
    public final static String FUNCTION_ID = "GLP---";

    protected String standardIdentity;
    protected String echelon;
    protected String category;
    protected String status;
    // TODO: add country code, etc.

    protected String text;

    protected SurfaceText labelStart;
    protected SurfaceText labelEnd;

    public PhaseLine()
    {
        this.setFollowTerrain(true);
        this.setPathType(AVKey.GREAT_CIRCLE);
        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // TODO how to handle altitude mode?
        this.setText("");
    }

    public String getIdentifier()
    {
        SymbolCode symCode = new SymbolCode();
        symCode.setValue(SymbolCode.STANDARD_IDENTITY, this.standardIdentity);
        symCode.setValue(SymbolCode.ECHELON, this.echelon);
        symCode.setValue(SymbolCode.CATEGORY, this.category);
        symCode.setValue(SymbolCode.FUNCTION_ID, FUNCTION_ID);

        return symCode.toString();
    }

    public String getStandardIdentity()
    {
        return this.standardIdentity;
    }

    public void setStandardIdentity(String standardIdentity)
    {
        if (standardIdentity == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.standardIdentity = standardIdentity;
    }

    public String getCategory()
    {
        return this.category;
    }

    public void setCategory(String category)
    {
        if (category == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.category = category;
    }

    public String getEchelon()
    {
        return this.echelon;
    }

    public void setEchelon(String echelon)
    {
        if (echelon == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.echelon = echelon;
    }

    public String getStatus()
    {
        return this.status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        this.text = text;

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
        Iterator<? extends Position> iterator = this.positions.iterator();

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

    public void preRender(DrawContext dc)
    {
        this.determineLabelPositions(dc);

        // If the attributes have not been created yet, create them now.
        // The default attributes are determined by the symbol code.
        if (this.normalAttrs == null)
        {
            ShapeAttributes attrs = this.createShapeAttributes();
            this.setAttributes(attrs);

            Color color = attrs.getOutlineMaterial().getDiffuse();
            this.labelStart.setColor(color);
            this.labelEnd.setColor(color);
        }

        this.labelStart.preRender(dc);
        this.labelEnd.preRender(dc);
    }

    @Override
    public void render(DrawContext dc)
    {
        super.render(dc);
    }

    @Override
    public void pick(DrawContext dc, Point pickPoint)
    {
        this.labelStart.pick(dc, pickPoint);
        this.labelEnd.pick(dc, pickPoint);

        super.pick(dc, pickPoint);
    }

    protected ShapeAttributes createShapeAttributes()
    {
        ShapeAttributes attrs = new BasicShapeAttributes();

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
}
