/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.general.lines;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.milstd2525.*;

import java.util.Iterator;

/**
 * Implementation of phase line graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Phase Line (2.X.2.1.2.4)</li> <li>Light Line (2.X.2.1.2.5)</li> <li>Final Coordination Line
 * (2.X.2.5.2.3)</li> <li>Limits of Advance (2.X.2.5.2.5)</li> <li>Line of Departure (2.X.2.5.2.6)</li> <li>Line of
 * Departure/Line of Contact (2.X.2.5.2.7)</li> <li>Line of Departure/Line of Contact (2.X.2.5.2.8)</li> <li>Release
 * Line (2.X.2.6.1.3)</li> <li>No-Fire Line (2.X.4.2.2.3)</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class PhaseLine extends MilStd2525TacticalGraphic
{
    /** Function ID for Phase Line (2.X.2.1.2.4). */
    public final static String FUNCTION_ID_PHASE = "GLP---";
    /** Function ID for Light Line (2.X.2.1.2.5). */
    public final static String FUNCTION_ID_LIGHT = "GLL---";
    /** Function ID for Final Coordination Line (2.X.2.5.2.3). */
    public final static String FUNCTION_ID_FINAL = "OLF---";
    /** Function ID for Limits of Advance (2.X.2.5.2.5). */
    public final static String FUNCTION_ID_ADVANCE = "OLL---";
    /** Function ID for Line of Departure (2.X.2.5.2.6). */
    public final static String FUNCTION_ID_DEPARTURE = "OLT---";
    /** Function ID for Line of Departure/Line of Contact (2.X.2.5.2.7). */
    public final static String FUNCTION_ID_DEPARTURE_CONTACT = "OLC---";
    /** Function ID for Line of Departure/Line of Contact (2.X.2.5.2.8). */
    public final static String FUNCTION_ID_DEPLOYMENT = "OLP---";
    /** Function ID for Release Line (2.X.2.6.1.3). */
    public final static String FUNCTION_ID_RELEASE = "SLR---";
    /** Function ID for No-Fire Line (2.X.4.2.2.3). */
    public final static String FUNCTION_ID_NO_FIRE = "LCN---";

    /** Path used to render the line. */
    protected Path path;

    /** Create a new Phase Line. */
    public PhaseLine()
    {
        this.path = this.createPath();
    }

    /** {@inheritDoc} */
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_COMMAND_CONTROL_GENERAL_MANEUVER;
    }

    /** {@inheritDoc} */
    public void setPositions(Iterable<? extends Position> positions)
    {
        this.path.setPositions(positions);
    }

    /** {@inheritDoc} */
    public Iterable<? extends Position> getPositions()
    {
        return this.path.getPositions();
    }

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.path.getReferencePosition();
    }

    /** {@inheritDoc} */
    public void move(Position position)
    {
        this.path.move(position);
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.path.moveTo(position);
    }

    /** {@inheritDoc} */
    public void doRenderGraphic(DrawContext dc)
    {
        if (!dc.isOrderedRenderingMode())
        {
            this.path.render(dc);
        }
    }

    /**
     * Create and configure the Path used to render this graphic.
     *
     * @return New path configured with defaults appropriate for this type of graphic.
     */
    protected Path createPath()
    {
        Path path = new Path();
        path.setFollowTerrain(true);
        path.setPathType(AVKey.GREAT_CIRCLE);
        path.setAltitudeMode(WorldWind.CLAMP_TO_GROUND); // TODO how to handle altitude mode?
        path.setDelegateOwner(this);
        path.setAttributes(this.getActiveShapeAttributes());
        return path;
    }

    protected String getGraphicLabel()
    {
        String functionId = this.getFunctionId();

        String pattern = null;

        if (FUNCTION_ID_PHASE.equals(functionId))
            pattern = "PL %s";
        else if (FUNCTION_ID_LIGHT.equals(functionId))
            pattern = "LL\n(PL %s)";
        else if (FUNCTION_ID_FINAL.equals(functionId))
            pattern = "FINAL CL\n(PL %s)";
        else if (FUNCTION_ID_ADVANCE.equals(functionId))
            pattern = "LOA\n(PL %s)";
        else if (FUNCTION_ID_DEPARTURE.equals(functionId))
            pattern = "LD\n(PL %s)";
        else if (FUNCTION_ID_DEPARTURE_CONTACT.equals(functionId))
            pattern = "LD/LC\n(PL %s)";
        else if (FUNCTION_ID_DEPLOYMENT.equals(functionId))
            pattern = "PLD\n(PL %s)";
        else if (FUNCTION_ID_RELEASE.equals(functionId))
            pattern = "RL\n(PL %s)";
        else if (FUNCTION_ID_NO_FIRE.equals(functionId))
            pattern = "NFL\n(PL %s)";

        if (pattern != null)
        {
            String text = this.getText();
            return String.format(pattern, text != null ? text : "");
        }

        return "";
    }

    /** Create labels for the start and end of the path. */
    @Override
    protected void createLabels()
    {
        String text = this.getGraphicLabel();

        this.addLabel(text); // Start label
        this.addLabel(text); // End label
    }

    /**
     * Determine positions for the start and end labels.
     *
     * @param dc Current draw context.
     */
    protected void determineLabelPositions(DrawContext dc)
    {
        Iterator<? extends Position> iterator = this.path.getPositions().iterator();

        // Find the first and last positions on the path
        Position first = iterator.next();
        Position last = first;
        while (iterator.hasNext())
        {
            last = iterator.next();
        }

        Label startLabel = this.labels.get(0);
        Label endLabel = this.labels.get(1);

        // Position the labels at the ends of the path
        startLabel.setPosition(first);
        endLabel.setPosition(last);

        // Set the West-most label to right alignment, and the East-most label to left alignment.
        if (first.longitude.degrees < last.longitude.degrees)
        {
            startLabel.setTextAlign(AVKey.RIGHT);
            endLabel.setTextAlign(AVKey.LEFT);
        }
        else
        {
            startLabel.setTextAlign(AVKey.LEFT);
            endLabel.setTextAlign(AVKey.RIGHT);
        }
    }
}
