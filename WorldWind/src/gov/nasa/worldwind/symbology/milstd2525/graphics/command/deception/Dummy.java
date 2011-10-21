/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.deception;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * @author pabercrombie
 * @version $Id$
 */
public class Dummy extends MilStd2525TacticalGraphic
{
    public final static String FUNCTION_ID = "PD----";

    protected Path path;

    public Dummy()
    {
        this.path = new Path();
        this.path.setFollowTerrain(true);
        this.path.setPathType(AVKey.GREAT_CIRCLE);
        this.path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // TODO how to handle altitude mode?
        this.path.setDelegateOwner(this);
    }

    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    public void render(DrawContext dc)
    {
        if (this.attributes == null)
        {
            TacticalGraphicAttributes attrs = this.createDefaultAttributes();
            this.setAttributes(attrs);
        }

        this.path.render(dc);
    }

    /**
     * {@inheritDoc}
     *
     * The dummy graphic requires exactly three control points. Any positions beyond the first three will be ignored.
     *
     * @throws IllegalArgumentException if less than three control points are provided.
     */
    public void setPositions(Iterable<? extends Position> positions)
    {
        if (positions == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        try
        {
            Iterator<? extends Position> iterator = positions.iterator();
            Position pt1 = iterator.next();
            Position pt2 = iterator.next();
            Position pt3 = iterator.next();

            this.path.setPositions(Arrays.asList(pt2, pt1, pt3));
        }
        catch (NoSuchElementException e)
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
    }

    public Iterable<? extends Position> getPositions()
    {
        return this.path.getPositions();
    }

    public void setAttributes(TacticalGraphicAttributes attributes)
    {
    }

    public boolean isModifierVisible(String modifier)
    {
        return false;
    }

    public void setModifierVisible(String modifier, boolean visible)
    {
    }

    public void setAttributes(ShapeAttributes attributes)
    {
    }

    protected TacticalGraphicAttributes createDefaultAttributes()
    {
        BasicTacticalGraphicAttributes attrs = new BasicTacticalGraphicAttributes();

        String identity = this.getStandardIdentity();
        if (SymbolCode.IDENTITY_FRIEND.equals(identity))
        {
            attrs.setOutlineMaterial(Material.BLACK);
        }
        else if (SymbolCode.IDENTITY_HOSTILE.equals(identity))
        {
            attrs.setOutlineMaterial(Material.RED);
        }

        attrs.setOutlineStippleFactor(15);
        attrs.setOutlineStipplePattern((short) 0xAAAA);

        return attrs;
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
}
