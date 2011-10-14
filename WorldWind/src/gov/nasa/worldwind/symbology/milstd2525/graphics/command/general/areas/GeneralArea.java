/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.TacticalShape;
import gov.nasa.worldwind.symbology.milstd2525.SymbolCode;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class GeneralArea extends SurfacePolygon implements TacticalShape
{
    public final static String FUNCTION_ID = "GAG---";

    public final static String HOSTILE_INDICATOR = "ENY";

    protected String standardIdentity;
    protected String echelon;
    protected String category;
    protected String status;
    // TODO: add country code, etc.

    protected String text;

    protected SurfaceText label;
    protected List<SurfaceText> identityLabels;

    public GeneralArea()
    {
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

        String fullText = this.createText(text);
        this.label = new SurfaceText(fullText, Position.ZERO);
    }

    protected String createText(String text)
    {
        return text;
    }

    protected void determineLabelPosition(DrawContext dc)
    {
        List<Sector> sectors = this.getSectors(dc);
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

    @Override
    public void preRender(DrawContext dc)
    {
        if (this.identityLabels == null && SymbolCode.IDENTITY_HOSTILE.equals(this.getStandardIdentity()))
        {
            this.determineIdentityLabelPosition();
        }

        // If the attributes have not been created yet, create them now.
        // The default attributes are determined by the symbol code.
        if (this.normalAttrs == null)
        {
            ShapeAttributes attrs = this.createShapeAttributes();
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

        super.preRender(dc);
    }

    @Override
    public void pick(DrawContext dc, Point pickPoint)
    {
        if (this.label != null)
        {
            this.label.pick(dc, pickPoint);
        }

        if (this.identityLabels != null)
        {
            for (SurfaceText text : this.identityLabels)
            {
                text.pick(dc, pickPoint);
            }
        }

        super.pick(dc, pickPoint);
    }

    protected ShapeAttributes createShapeAttributes()
    {
        ShapeAttributes attrs = new BasicShapeAttributes();

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
        this.setLocations(positions);
    }

    public Iterable<? extends Position> getPositions()
    {
        Iterable<? extends LatLon> locations = this.getLocations();
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
}
