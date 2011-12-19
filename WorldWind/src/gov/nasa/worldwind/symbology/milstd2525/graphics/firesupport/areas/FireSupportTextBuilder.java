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
     * Construct the text for labels in a Fire Support area graphic. All area graphics support main label placed inside
     * the area. Some also support a time range label placed at the left side of the graphic. This method returns text
     * for all applicable labels as a list. The first element of the list is the main label text. The second element (if
     * present) is the time range label text.
     *
     * @param graphic Graphic for which to create text.
     *
     * @return Array of text for labels. This array will always include at least one string: the main label text. It may
     *         include a second element. The second element (if present) is text for a label that must be placed at the
     *         left side of the area.
     */
    public String[] createText(MilStd2525TacticalGraphic graphic)
    {
        String[] result;

        String functionId = graphic.getFunctionId();
        if (CircularFireSupportArea.FUNCTION_ID_TARGET.equals(functionId))
        {
            result = new String[] { graphic.getText() };
        }
        else
        {
            boolean useSeparateTimeLabel = this.isShowSeparateTimeLabel(functionId);
            String mainText = this.createMainText(graphic, functionId, !useSeparateTimeLabel);

            if (useSeparateTimeLabel)
            {
                String timeText = this.createTimeRangeText(graphic);
                result = new String[] { mainText, timeText };
            }
            else
            {
                result = new String[] { mainText };
            }
        }
        return result;
    }

    protected boolean isShowSeparateTimeLabel(String functionId)
    {
        return CircularFireSupportArea.FUNCTION_ID_FSA.equals(functionId)
            || CircularFireSupportArea.FUNCTION_ID_SENSOR_ZONE.equals(functionId)
            || CircularFireSupportArea.FUNCTION_ID_DEAD_SPACE_AREA.equals(functionId)
            || CircularFireSupportArea.FUNCTION_ID_ZONE_OF_RESPONSIBILITY.equals(functionId)
            || CircularFireSupportArea.FUNCTION_ID_TARGET_BUILDUP.equals(functionId)
            || CircularFireSupportArea.FUNCTION_ID_TARGET_VALUE.equals(functionId);
    }

    protected String createMainText(MilStd2525TacticalGraphic graphic, String functionId, boolean includeTime)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getGraphicLabel(functionId)).append("\n");

        String s = graphic.getText();
        if (!WWUtil.isEmpty(s))
        {
            sb.append(s).append("\n");
        }

        if (includeTime)
        {
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
        }

        return sb.toString();
    }

    protected String createTimeRangeText(TacticalGraphic graphic)
    {
        StringBuilder sb = new StringBuilder();

        Object[] dates = TacticalGraphicUtil.getDateRange(graphic);
        if (dates[0] != null)
        {
            sb.append(dates[0]);
            sb.append("-\n");
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
        else if (CircularFireSupportArea.FUNCTION_ID_FSA.equals(functionId))
        {
            return "FSA";
        }
        else if (CircularFireSupportArea.FUNCTION_ID_SENSOR_ZONE.equals(functionId))
        {
            return "SENSOR\nZONE";
        }
        else if (CircularFireSupportArea.FUNCTION_ID_DEAD_SPACE_AREA.equals(functionId))
        {
            return "DA";
        }
        else if (CircularFireSupportArea.FUNCTION_ID_ZONE_OF_RESPONSIBILITY.equals(functionId))
        {
            return "ZOR";
        }
        else if (CircularFireSupportArea.FUNCTION_ID_TARGET_BUILDUP.equals(functionId))
        {
            return "TBA";
        }
        else if (CircularFireSupportArea.FUNCTION_ID_TARGET_VALUE.equals(functionId))
        {
            return "TVAR";
        }

        return "";
    }
}
