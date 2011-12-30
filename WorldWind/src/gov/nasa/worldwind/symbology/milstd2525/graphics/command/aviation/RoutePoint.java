/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.AbstractCircularGraphic;
import gov.nasa.worldwind.util.WWUtil;

/**
 * Implementation of aviation route control point graphics. This class implements the following graphics:
 * <p/>
 * <ul> <li>Air Control Point (2.X.2.2.1.1)</li> <li>Communications Checkpoint (2.X.2.2.1.2)</li> </ul>
 *
 * @author pabercrombie
 * @version $Id$
 */
public class RoutePoint extends AbstractCircularGraphic implements TacticalPoint, PreRenderable
{
    /** Function ID for Air Control Point (2.X.2.2.1.1). */
    public final static String FUNCTION_ID_AIR_CONTROL = "APP---";
    /** Function ID for Communications Checkpoint (2.X.2.2.1.2). */
    public final static String FUNCTION_ID_COMMUNICATIONS_CHECKPOINT = "APC---";

    /** Create a new control point. */
    public RoutePoint()
    {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory()
    {
        return SymbologyConstants.CATEGORY_COMMAND_CONTROL_GENERAL_MANEUVER;
    }

    /**
     * Create the text for the main label on this graphic.
     *
     * @return Text for the main label. May return null if there is no text.
     */
    protected String createLabelText()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getGraphicLabel());
        sb.append("\n");
        sb.append(this.getText());

        return sb.toString();
    }

    protected String getGraphicLabel()
    {
        String functionId = this.getFunctionId();

        if (FUNCTION_ID_AIR_CONTROL.equals(functionId))
            return "ACP";
        else if (FUNCTION_ID_COMMUNICATIONS_CHECKPOINT.equals(functionId))
            return "CCP";

        return "";
    }

    @Override
    protected void createLabels()
    {
        String labelText = this.createLabelText();
        if (!WWUtil.isEmpty(labelText))
        {
            this.addLabel(labelText);
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
        LatLon center = this.circle.getCenter();
        this.labels.get(0).setPosition(new Position(center, 0));
    }
}
