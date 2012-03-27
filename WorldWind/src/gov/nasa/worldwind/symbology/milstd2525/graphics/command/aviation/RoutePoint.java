/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.command.aviation;

import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.TacticalPoint;
import gov.nasa.worldwind.symbology.milstd2525.graphics.*;
import gov.nasa.worldwind.util.WWUtil;

import java.util.*;

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
    /**
     * Indicates the graphics supported by this class.
     *
     * @return List of masked SIDC strings that identify graphics that this class supports.
     */
    public static List<String> getSupportedGraphics()
    {
        return Arrays.asList(
            TacGrpSidc.C2GM_AVN_PNT_ACP,
            TacGrpSidc.C2GM_AVN_PNT_COMMCP
        );
    }

    /**
     * Create a new control point.
     *
     * @param sidc Symbol code the identifies the graphic.
     */
    public RoutePoint(String sidc)
    {
        super(sidc);
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
        String code = this.maskedSymbolCode;

        if (TacGrpSidc.C2GM_AVN_PNT_ACP.equalsIgnoreCase(code))
            return "ACP";
        else if (TacGrpSidc.C2GM_AVN_PNT_COMMCP.equalsIgnoreCase(code))
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
