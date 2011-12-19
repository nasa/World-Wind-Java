/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalGraphic;
import gov.nasa.worldwind.util.WWUtil;

/**
 * Utility class to construct text for the graphics of Fire Support Area graphics. Many of these graphics come in three
 * versions (quad, circle, and polygon), but share the same text. This class encodes the logic to construct the
 * appropriate label text depending on the type of graphic.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class FireSupportTextBuilder
{
    /**
     * Construct the text for the main label of a graphic.
     *
     * @param graphic Graphic for which to create text.
     *
     * @return Label text.
     */
    public String createText(MilStd2525TacticalGraphic graphic)
    {
        String functionId = graphic.getFunctionId();
        if (CircularFireSupportArea.FUNCTION_ID_TARGET.equals(functionId))
            return graphic.getText();
        else
            return createDateRangeText(graphic, functionId);
    }

    protected String createDateRangeText(TacticalGraphic graphic, String functionId)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getGraphicLabel(functionId)).append("\n");

        String s = graphic.getText();
        if (!WWUtil.isEmpty(s))
        {
            sb.append(s).append("\n");
        }

        Object[] dates = TacticalGraphicUtil.getDateRange(graphic);
        if (dates[0] != null)
        {
            sb.append(dates[0]);
            sb.append("-");
        }

        if (dates[1] != null)
        {
            sb.append(dates[1]);
        }

        return sb.toString();
    }

    protected String getGraphicLabel(String functionId)
    {
        if (RectangularFireSupportArea.FUNCTION_ID_FFA.equals(functionId)
            || CircularFireSupportArea.FUNCTION_ID_FFA.equals(functionId)
            || IrregularFireSupportArea.FUNCTION_ID_FFA.equals(functionId))
        {
            return "FFA";
        }
        else if (RectangularFireSupportArea.FUNCTION_ID_RFA.equals(functionId)
            || CircularFireSupportArea.FUNCTION_ID_RFA.equals(functionId)
            || IrregularFireSupportArea.FUNCTION_ID_RFA.equals(functionId))
        {
            return "RFA";
        }

        return "";
    }
}
