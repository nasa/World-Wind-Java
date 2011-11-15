/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;

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

    protected SurfacePolygon polygon;

    /** Text for the main label. */
    protected String labelText;
    /** SurfaceText used to draw the main label. This list contains one element for each line of text. */
    protected List<SurfaceText> labels;
    /** SurfaceText used to draw "ENY" labels to indicate a hostile identity. */
    protected List<SurfaceText> identityLabels;

    protected boolean showIdentityLabels = true;

    public GeneralArea()
    {
        this.polygon = new SurfacePolygon();
        this.polygon.setDelegateOwner(this);
        this.polygon.setAttributes(this.getActiveShapeAttributes());
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

    /**
     * Indicates whether or not "ENY" labels will be displayed on hostile entities.
     *
     * @return {@code true} if the identity labels will be displayed.
     */
    public boolean isShowIdentityLabels()
    {
        return showIdentityLabels;
    }

    /**
     * Specifies whether or not to display "ENY" labels on hostile entities.
     *
     * @param showIdentityLabels {@code true} if the identity labels will be displayed.
     */
    public void setShowIdentityLabels(boolean showIdentityLabels)
    {
        this.showIdentityLabels = showIdentityLabels;
    }

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.makeShapes(dc);

        if (this.labels == null && this.labelText == null)
        {
            this.createLabels();
        }

        this.determineActiveAttributes();
        this.determineLabelAttributes();

        if (this.isShowIdentityLabels()
            && this.identityLabels == null
            && SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equals(this.getStandardIdentity()))
        {
            this.determineIdentityLabelPosition();
        }

        if (this.labels != null)
        {
            this.determineLabelPositions(dc);
            for (SurfaceText text : this.labels)
            {
                text.preRender(dc);
            }
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
     * Create the text for the main label on this graphic.
     *
     * @return Text for the main label. May return null if there is no text.
     */
    protected String createLabelText()
    {
        return this.getText();
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

        Position position = new Position(this.computeLabelLocation(dc), 0);

        for (SurfaceText label : this.labels)
        {
            label.setPosition(position);
            position = new Position(Position.greatCircleEndPosition(position, Angle.POS180, textHeight), 0);
        }
    }

    /**
     * Compute the position for the area's main label. This position indicates the position of the first line of the
     * label. If there are more lines, they will be arranged South of the first line.
     *
     * @param dc Current draw context.
     *
     * @return Position of the first line of the main label.
     */
    protected LatLon computeLabelLocation(DrawContext dc)
    {
        List<Sector> sectors = this.polygon.getSectors(dc);
        if (sectors != null)
        {
            // TODO: centroid of bounding sector is not always a good choice for label position
            Sector sector = sectors.get(0);
            return sector.getCentroid();
        }
        return null;
    }

    protected void determineIdentityLabelPosition()
    {
        if (SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equals(this.getStandardIdentity()))
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

    protected void determineLabelAttributes()
    {
        Color color = this.getLabelMaterial().getDiffuse();

        Font font = this.getActiveOverrideAttributes().getTextModifierFont();
        if (font == null)
            font = DEFAULT_FONT;

        if (this.labels != null)
        {
            for (SurfaceText text : this.labels)
            {
                text.setColor(color);
                text.setFont(font);
            }
        }

        if (this.identityLabels != null)
        {
            for (SurfaceText text : this.identityLabels)
            {
                text.setColor(color);
                text.setFont(font);
            }
        }
    }
}
