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
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.WWUtil;

import java.util.*;

/**
 * Implementation of general area graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>General Area (2.X.2.1.3.1)</li> <li>Assembly Area (2.X.2.1.3.2)</li> <li>Engagement Area (2.X.2.1.3.3)</li>
 * <li>Drop Zone (2.X.2.1.3.5)</li> <li>Extraction Zone (2.X.2.1.3.6)</li> </li><li>Landing Zone (2.X.2.1.3.7)</li>
 * <li>Pickup Zone (2.X.2.1.3.8)</li> <li>Forward Arming and Refueling Area (FARP) (2.X.5.3.3)</li></ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class BasicArea extends MilStd2525TacticalGraphic implements PreRenderable
{
    /** Function ID for General Area (2.X.2.1.3.1). */
    public final static String FUNCTION_ID_GENERAL = "GAG---";
    /** Function ID for Assembly Area (2.X.2.1.3.2). */
    public final static String FUNCTION_ID_ASSEMBLY = "GAA---";
    /** Function ID for Drop Zone (2.X.2.1.3.5). */
    public final static String FUNCTION_ID_DROP = "GAD---";
    /** Function ID for Engagement Area (2.X.2.1.3.3). */
    public final static String FUNCTION_ID_ENGAGEMENT = "GAE---";
    /** Function ID for Extraction Zone (2.X.2.1.3.6). */
    public final static String FUNCTION_ID_EXTRACTION = "GAX---";
    /** Function ID for Landing Zone (2.X.2.1.3.7). */
    public final static String FUNCTION_ID_LANDING = "GAL---";
    /** Function ID for Pickup Zone (2.X.2.1.3.8). */
    public final static String FUNCTION_ID_PICKUP = "GAP---";

    protected SurfacePolygon polygon;

    /** First "ENY" label, for hostile entities. */
    protected Label identityLabel1;
    /** Second "ENY" label, for hostile entities. */
    protected Label identityLabel2;

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

        if (locations == null)
        {
            return null;
        }

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

    /** {@inheritDoc} */
    public void preRender(DrawContext dc)
    {
        if (!this.isVisible())
        {
            return;
        }

        this.makeShapes(dc);

        this.determinePerFrameAttributes(dc);

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
    protected void doRenderGraphic(DrawContext dc)
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
        String label = this.getGraphicLabel();
        String text = this.getText();

        if (label == null && text == null)
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (!WWUtil.isEmpty(label))
        {
            sb.append(label).append("\n");
        }

        if (!WWUtil.isEmpty(text))
        {
            sb.append(text);
        }

        return sb.toString();
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

    /**
     * Indicates the alignment of the graphic's main label.
     *
     * @return Alignment for the main label. One of AVKey.CENTER, AVKey.LEFT, or AVKey.RIGHT.
     */
    protected String getLabelAlignment()
    {
        return AVKey.CENTER;
    }

    @Override
    protected void createLabels()
    {
        String labelText = this.createLabelText();
        if (!WWUtil.isEmpty(labelText))
        {
            Label mainLabel = this.addLabel(labelText);
            mainLabel.setTextAlign(this.getLabelAlignment());

            mainLabel.setOffset(this.getDefaultLabelOffset());
        }

        if (this.mustCreateIdentityLabels())
        {
            this.identityLabel1 = this.addLabel(SymbologyConstants.HOSTILE_ENEMY);
            this.identityLabel2 = this.addLabel(SymbologyConstants.HOSTILE_ENEMY);
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
        if (this.labels == null || this.labels.isEmpty())
            return;

        Position mainLabelPosition = this.determineMainLabelPosition(dc);
        this.labels.get(0).setPosition(mainLabelPosition);

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
     *
     * @return Position for the graphic's main label.
     */
    protected Position determineMainLabelPosition(DrawContext dc)
    {
        List<Sector> sectors = this.polygon.getSectors(dc);
        if (sectors != null)
        {
            // TODO: centroid of bounding sector is not always a good choice for label position
            Sector sector = sectors.get(0);
            return new Position(sector.getCentroid(), 0);
        }
        return this.getReferencePosition();
    }

    protected void determineIdentityLabelPositions()
    {
        // Position the first label between the first and second control points.
        Iterator<? extends Position> iterator = this.getPositions().iterator();
        Position first = iterator.next();
        Position second = iterator.next();

        LatLon midpoint = LatLon.interpolate(0.5, first, second);
        if (this.identityLabel1 != null)
        {
            this.identityLabel1.setPosition(new Position(midpoint, 0));
        }

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
        if (this.identityLabel2 != null)
        {
            this.identityLabel2.setPosition(new Position(midpoint, 0));
        }
    }

    protected boolean mustCreateIdentityLabels()
    {
        return this.isShowHostileIndicator()
            && SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equals(this.getStandardIdentity());
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
