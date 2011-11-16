/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation.points;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Implementation of the Air Control Point graphic (hierarchy 2.X.2.2.1.1, SIDC: G*GPAPP---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class AirControlPoint extends MilStd2525TacticalGraphic implements PreRenderable
{
    public final static String FUNCTION_ID = "APP---";

    public final double DEFAULT_RADIUS = 1000; // TODO: what should default be?

    /** Text for the main label. */
    protected String labelText;
    /** SurfaceText used to draw the main label. This list contains one element for each line of text. */
    protected List<SurfaceText> labels;

    protected SurfaceCircle circle;

    /** Create a new control point. */
    public AirControlPoint()
    {
        this.circle = this.createShape();
        this.circle.setRadius(DEFAULT_RADIUS);
    }

    /** {@inheritDoc} */
    public String getFunctionId()
    {
        return FUNCTION_ID;
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_COMMAND_CONTROL_GENERAL_MANEUVER;
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

        if (this.labels == null && this.labelText == null)
        {
            this.createLabels();
        }

        this.determineActiveAttributes();

        this.determineLabelAttributes();
        this.determineLabelPositions(dc);

        for (SurfaceText label : this.labels)
        {
            label.preRender(dc);
        }

        this.circle.preRender(dc);
    }

    /**
     * Render the polygon.
     *
     * @param dc Current draw context.
     */
    public void doRenderGraphic(DrawContext dc)
    {
        this.circle.render(dc);
    }

    /**
     * Create the text for the main label on this graphic.
     *
     * @return Text for the main label. May return null if there is no text.
     */
    protected String createLabelText()
    {
        return "ACP\n" + this.getText();
    }

    protected Offset getLabelOffset()
    {
        return SurfaceText.DEFAULT_OFFSET;
    }

    protected void createLabels()
    {
        this.labelText = this.createLabelText();
        if (this.labelText == null)
        {
            // No label. Set the text to an empty string so we won't try to generate it again.
            this.labelText = "";
            return;
        }

        String[] lines = this.labelText.split("\n");

        this.labels = new ArrayList<SurfaceText>(lines.length);

        Offset offset = this.getLabelOffset();

        for (String line : lines)
        {
            SurfaceText text = new SurfaceText(line, Position.ZERO);
            text.setOffset(offset);
            this.labels.add(text);
        }
    }

    /**
     * Determine the appropriate position for the graphic's labels.
     *
     * @param dc Current draw context.
     */
    protected void determineLabelPositions(DrawContext dc)
    {
        if (this.labels == null)
            return;

        Angle textHeight = Angle.fromRadians(
            SurfaceText.DEFAULT_TEXT_SIZE_IN_METERS * 1.25 / dc.getGlobe().getRadius());

        LatLon center = this.circle.getCenter();
        Position position = new Position(Position.greatCircleEndPosition(center, Angle.ZERO, textHeight.divide(2.0)), 0);

        for (SurfaceText label : this.labels)
        {
            label.setPosition(position);
            position = new Position(Position.greatCircleEndPosition(position, Angle.POS180, textHeight), 0);
        }
    }

    protected void determineLabelAttributes()
    {
        Color color = this.getLabelMaterial().getDiffuse();

        Font font = this.getActiveOverrideAttributes().getTextModifierFont();
        if (font == null)
            font = DEFAULT_FONT;

        for (SurfaceText text : this.labels)
        {
            text.setColor(color);
            text.setFont(font);
        }
    }

    protected SurfaceCircle createShape()
    {
        SurfaceCircle circle = new SurfaceCircle();
        circle.setDelegateOwner(this);
        circle.setAttributes(this.activeShapeAttributes);
        return circle;
    }
}
