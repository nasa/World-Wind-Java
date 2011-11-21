/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.areas;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.WWUtil;

import java.util.*;
import java.util.List;

/**
 * Implementation of the General Area graphic (hierarchy 2.X.2.1.3, SIDC: G*GPGAG---****X).
 *
 * @author pabercrombie
 * @version $Id$
 */
public class BasicArea extends MilStd2525TacticalGraphic implements PreRenderable
{
    public final static String FUNCTION_ID_GENERAL = "GAG---";
    public final static String FUNCTION_ID_ASSEMBLY = "GAA---";
    public final static String FUNCTION_ID_DROP = "GAD---";
    public final static String FUNCTION_ID_ENGAGEMENT = "GAE---";
    public final static String FUNCTION_ID_EXTRACTION = "GAX---";
    public final static String FUNCTION_ID_LANDING = "GAL---";
    public final static String FUNCTION_ID_PICKUP = "GAP---";

    protected SurfacePolygon polygon;

    protected boolean showIdentityLabels = true;

    public BasicArea()
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

        this.determineActiveAttributes();

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
        // SurfacePolygon is not an ordered renderable
        if (!dc.isOrderedRenderingMode())
        {
            this.polygon.render(dc);
        }
    }

    /**
     * Create the text for the main label on this graphic.
     *
     * @return Text for the main label. May return null if there is no text.
     */
    protected String createLabelText()
    {
        return this.getGraphicLabel() + "\n" + this.getText();
    }

    protected String getGraphicLabel()
    {
        String functionId = this.getFunctionId();

        if (FUNCTION_ID_GENERAL.equals(functionId))
            return "";
        else if (FUNCTION_ID_ASSEMBLY.equals(functionId))
            return "AA";
        else if (FUNCTION_ID_DROP.equals(functionId))
            return "DZ";
        else if (FUNCTION_ID_ENGAGEMENT.equals(functionId))
            return "EA";
        else if (FUNCTION_ID_EXTRACTION.equals(functionId))
            return "EZ";
        else if (FUNCTION_ID_LANDING.equals(functionId))
            return "LZ";
        else if (FUNCTION_ID_PICKUP.equals(functionId))
            return "PZ";

        return "";
    }

    protected Offset getLabelOffset()
    {
        return DEFAULT_OFFSET;
    }

    protected String getLabelAlignment()
    {
        return AVKey.CENTER;
    }

    @Override
    protected void createLabels()
    {
        String labelText = this.createLabelText();
        if (WWUtil.isEmpty(labelText))
        {
            return;
        }
        Label mainLabel = this.addLabel(labelText);
        mainLabel.setTextAlign(this.getLabelAlignment());

        mainLabel.setOffset(this.getLabelOffset());

        if (this.mustCreateIdentityLabels())
        {
            this.addLabel(HOSTILE_INDICATOR);
            this.addLabel(HOSTILE_INDICATOR);
        }
    }

    /**
     * Determine the appropriate position for the graphic's labels.
     *
     * @param dc Current draw context.
     */
    @Override
    protected void determineLabelPositions(DrawContext dc)
    {
        this.determineMainLabelPosition(dc);

        if (this.mustCreateIdentityLabels())
        {
            this.determineIdentityLabelPositions();
        }
    }

    /**
     * Compute the position for the area's main label. This position indicates the position of the first line of the
     * label. If there are more lines, they will be arranged South of the first line.
     *
     * @param dc Current draw context.
     */
    protected void determineMainLabelPosition(DrawContext dc)
    {
        List<Sector> sectors = this.polygon.getSectors(dc);
        if (sectors != null)
        {
            // TODO: centroid of bounding sector is not always a good choice for label position
            Sector sector = sectors.get(0);
            Position position = new Position(sector.getCentroid(), 0);

            this.labels.get(0).setPosition(position);
        }
    }

    protected void determineIdentityLabelPositions()
    {
        // Position the first label between the first and second control points.
        Iterator<? extends Position> iterator = this.getPositions().iterator();
        Position first = iterator.next();
        Position second = iterator.next();

        LatLon midpoint = LatLon.interpolate(0.5, first, second);
        this.labels.get(1).setPosition(new Position(midpoint, 0));

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
        this.labels.get(2).setPosition(new Position(midpoint, 0));
    }

    protected boolean mustCreateIdentityLabels()
    {
        return this.showIdentityLabels && SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equals(
            this.getStandardIdentity());
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
}
