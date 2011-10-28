/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.TacticalGraphicAttributes;
import gov.nasa.worldwind.symbology.milstd2525.*;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Implementation of the General Area graphic (hierarchy 2.X.2.1.3, SIDC: G*GPGAG---****X).
 *
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

    public GeneralArea()
    {
        this.polygon = new SurfacePolygon();
        this.polygon.setDelegateOwner(this);
        this.setText("");
    }

    /** {@inheritDoc} */
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    /** {@inheritDoc} */
    public void setPositions(Iterable<? extends Position> positions)
    {
        this.polygon.setLocations(positions);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.polygon.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        this.polygon.move(position);
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.polygon.moveTo(position);
    }

    @Override
    public void setText(String text)
    {
        super.setText(text);

        String fullText = this.createText(text);
        if (fullText != null)
        {
            this.label = new SurfaceText(fullText, Position.ZERO);
        }
        else
        {
            this.label = null;
        }
    }

    /** {@inheritDoc} Overridden to apply highlight to all parts of the graphic. */
    @Override
    public void setHighlighted(boolean highlighted)
    {
        super.setHighlighted(highlighted);
        this.polygon.setHighlighted(highlighted);
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.makeShapes(dc);

        this.determineActiveAttributes();

        if (this.identityLabels == null && SymbolCode.IDENTITY_HOSTILE.equals(this.getStandardIdentity()))
        {
            this.determineIdentityLabelPosition();
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

    protected void makeShapes(DrawContext dc)
    {
        // Do nothing, but allow subclasses to override
    }

    /**
     * Render the polygon.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        this.polygon.render(dc);
    }

    /**
     * Render the labels.
     *
     * @param dc Current draw context.
     */
    @Override
    public void doRenderModifiers(DrawContext dc)
    {
        if (this.label != null)
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

    /** Determine active attributes for this frame. */
    protected void determineActiveAttributes()
    {
        ShapeAttributes shapeAttributes;
        if (this.isHighlighted())
        {
            shapeAttributes = this.polygon.getHighlightAttributes();
            TacticalGraphicAttributes highlightAttributes = this.getHighlightAttributes();
            if (highlightAttributes != null)
            {
                if (shapeAttributes == null)
                {
                    shapeAttributes = new BasicShapeAttributes();
                    this.polygon.setHighlightAttributes(shapeAttributes);
                }

                this.applyDefaultAttributes(shapeAttributes);
                this.applyOverrideAttributes(highlightAttributes, shapeAttributes);
            }
        }
        else
        {
            shapeAttributes = this.polygon.getAttributes();
            if (shapeAttributes == null)
            {
                shapeAttributes = new BasicShapeAttributes();
                this.polygon.setAttributes(shapeAttributes);
            }
            this.applyDefaultAttributes(shapeAttributes);

            TacticalGraphicAttributes normalAttributes = this.getAttributes();
            if (normalAttributes != null)
            {
                this.applyOverrideAttributes(normalAttributes, shapeAttributes);
            }

            Color color = shapeAttributes.getOutlineMaterial().getDiffuse();
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
    }
}
