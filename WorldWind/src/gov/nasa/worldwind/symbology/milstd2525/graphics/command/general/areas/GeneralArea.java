/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class GeneralArea extends MilStd2525TacticalGraphic implements PreRenderable
{
    public final static String FUNCTION_ID = "GAG---";

    public final static String HOSTILE_INDICATOR = "ENY";

    protected SurfacePolygon polygon;
    protected SurfaceText label;
    protected List<SurfaceText> identityLabels;

    protected boolean textVisible;

    public GeneralArea()
    {
        this.polygon = new SurfacePolygon();
        this.polygon.setDelegateOwner(this);
    }

    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    @Override
    public void setText(String text)
    {
        super.setText(text);

        String fullText = this.createText(text);
        this.label = new SurfaceText(fullText, Position.ZERO);
    }

    protected String createText(String text)
    {
        return text;
    }

    protected void determineLabelPosition(DrawContext dc)
    {
        List<Sector> sectors = this.polygon.getSectors(dc);
        if (sectors != null)
        {
            // TODO: centroid of bounding sector is not always a good choice for label position
            Sector sector = sectors.get(0);
            LatLon centroid = sector.getCentroid();

            this.label.setPosition(new Position(centroid, 0));
        }
    }

    protected void determineIdentityLabelPosition()
    {
        if (SymbolCode.IDENTITY_HOSTILE.equals(this.getStandardIdentity()))
        {
            this.identityLabels = new ArrayList<SurfaceText>();

            // Position the first label between the first and second control points.
            Iterator<? extends Position> iterator = this.getPositions().iterator();
            Position first = iterator.next();
            Position second = iterator.next();

            LatLon midpoint = LatLon.interpolate(0.5, first, second);
            SurfaceText idLabel = new SurfaceText(HOSTILE_INDICATOR, new Position(midpoint, 0));
            this.identityLabels.add(idLabel);

            // Position the second label between the middle two control points in the position list. If the control
            // points are more or less evenly distributed, this will be about half way around the shape.
            int count = this.getPositionCount();
            iterator = this.getPositions().iterator();
            for (int i = 0; i < count / 2 + 1; i++)
            {
                first = iterator.next();
            }
            second = iterator.next();

            midpoint = LatLon.interpolate(0.5, first, second);
            SurfaceText idLabel2 = new SurfaceText(HOSTILE_INDICATOR, new Position(midpoint, 0));
            this.identityLabels.add(idLabel2);
        }
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

    public TacticalGraphicAttributes getAttributes()
    {
        if (this.attributes == null)
        {
            this.attributes = this.createDefaultAttributes();
        }

        return this.attributes;
    }

    public boolean isModifierVisible(String modifier)
    {
        //noinspection SimplifiableIfStatement
        if (AVKey.TEXT.equals(modifier))
            return this.textVisible;
        else
            return false;
    }

    public void setModifierVisible(String modifier, boolean visible)
    {
        if (AVKey.TEXT.equals(modifier))
        {
            this.textVisible = visible;
        }
    }

    public void preRender(DrawContext dc)
    {
        if (this.identityLabels == null && SymbolCode.IDENTITY_HOSTILE.equals(this.getStandardIdentity()))
        {
            this.determineIdentityLabelPosition();
        }

        // If the attributes have not been created yet, create them now.
        // The default attributes are determined by the symbol code.
        if (this.attributes == null)
        {
            TacticalGraphicAttributes attrs = this.createDefaultAttributes();
            this.setAttributes(attrs);

            Color color = attrs.getOutlineMaterial().getDiffuse();
            if (this.label != null)
            {
                this.label.setColor(color);
            }

            if (this.identityLabels != null)
            {
                for (SurfaceText text : this.identityLabels)
                {
                    text.setColor(color);
                }
            }
        }

        if (this.label != null)
        {
            this.determineLabelPosition(dc);
            this.label.preRender(dc);
        }

        if (this.identityLabels != null)
        {
            for (SurfaceText text : this.identityLabels)
            {
                text.preRender(dc);
            }
        }

        this.polygon.preRender(dc);
    }

    public void render(DrawContext dc)
    {
        if (this.label != null && this.textVisible)
        {
            this.label.render(dc);
        }

        if (this.identityLabels != null)
        {
            for (SurfaceText text : this.identityLabels)
            {
                text.render(dc);
            }
        }

        this.polygon.render(dc);
    }

    protected TacticalGraphicAttributes createDefaultAttributes()
    {
        TacticalGraphicAttributes attrs = new BasicTacticalGraphicAttributes();

        attrs.setDrawInterior(false);

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

    public void setPositions(Iterable<? extends Position> positions)
    {
        this.polygon.setLocations(positions);
    }

    public Iterable<? extends Position> getPositions()
    {
        Iterable<? extends LatLon> locations = this.polygon.getLocations();
        ArrayList<Position> positions = new ArrayList<Position>();

        for (LatLon ll : locations)
        {
            if (ll instanceof Position)
                positions.add((Position) ll);
            else
                positions.add(new Position(ll, 0));
        }

        return positions;
    }

    @Override
    public void setAttributes(TacticalGraphicAttributes attributes)
    {
        this.attributes = attributes;
        this.polygon.setAttributes(attributes);
    }

    public Position getReferencePosition()
    {
        return this.polygon.getReferencePosition();
    }

    public void move(Position position)
    {
        this.polygon.move(position);
    }

    public void moveTo(Position position)
    {
        this.polygon.moveTo(position);
    }
}
