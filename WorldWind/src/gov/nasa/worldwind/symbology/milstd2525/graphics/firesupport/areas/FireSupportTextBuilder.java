/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics.firesupport.areas;

import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.TacGrpSidc;
import gov.nasa.worldwind.util.*;

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
        if (graphic == null)
        {
            String message = Logging.getMessage("nullValue.GraphicIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String[] result;

        // Compute the masked SIDC for this graphic.
        SymbolCode symCode = new SymbolCode(graphic.getIdentifier());
        String maskedSidc = symCode.toMaskedString();

        if (TacGrpSidc.FSUPP_ARS_ARATGT_CIRTGT.equals(maskedSidc))
        {
            // Circular Target just uses the Unique Designation as a label.
            result = new String[] {graphic.getText()};
        }
        else if (TacGrpSidc.FSUPP_ARS_ARATGT_BMARA.equals(maskedSidc))
        {
            // Bomb graphic just says "BOMB"
            result = new String[] {"BOMB"};
        }
        else if (TacGrpSidc.FSUPP_ARS_C2ARS_TGMF.equals(maskedSidc))
        {
            // Terminally guided munitions footprint says "TGMF", and does not support modifiers.
            result = new String[] {"TGMF"};
        }
        else
        {
            boolean useSeparateTimeLabel = this.isShowSeparateTimeLabel(maskedSidc);

            String mainText;

            if (this.isAirspaceCoordinationArea(maskedSidc))
            {
                mainText = this.createAirspaceCoordinationText(graphic);
            }
            else
            {
                mainText = this.createMainText(graphic, maskedSidc, !useSeparateTimeLabel);
            }

            if (useSeparateTimeLabel)
            {
                String timeText = this.createTimeRangeText(graphic);
                result = new String[] {mainText, timeText};
            }
            else
            {
                result = new String[] {mainText};
            }
        }
        return result;
    }

    protected boolean isShowSeparateTimeLabel(String functionId)
    {
        return CircularFireSupportArea.getGraphicsWithTimeLabel().contains(functionId)
            || RectangularFireSupportArea.getGraphicsWithTimeLabel().contains(functionId)
            || IrregularFireSupportArea.getGraphicsWithTimeLabel().contains(functionId);
    }

    protected boolean isAirspaceCoordinationArea(String functionId)
    {
        return TacGrpSidc.FSUPP_ARS_C2ARS_ACA_IRR.equals(functionId)
            || TacGrpSidc.FSUPP_ARS_C2ARS_ACA_RTG.equals(functionId)
            || TacGrpSidc.FSUPP_ARS_C2ARS_ACA_CIRCLR.equals(functionId);
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

    protected String getGraphicLabel(String sidc)
    {
        if (TacGrpSidc.FSUPP_ARS_C2ARS_FFA_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_FFA_CIRCLR.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_FFA_IRR.equals(sidc))
        {
            return "FFA";
        }
        else if (TacGrpSidc.FSUPP_ARS_C2ARS_RFA_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_RFA_CIRCLR.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_RFA_IRR.equals(sidc))
        {
            return "RFA";
        }
        else if (TacGrpSidc.FSUPP_ARS_C2ARS_FSA_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_FSA_CIRCLR.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_FSA_IRR.equals(sidc))
        {
            return "FSA";
        }
        else if (TacGrpSidc.FSUPP_ARS_C2ARS_SNSZ_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_SNSZ_CIRCLR.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_SNSZ_IRR.equals(sidc))
        {
            return "SENSOR\nZONE";
        }
        else if (TacGrpSidc.FSUPP_ARS_C2ARS_DA_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_DA_CIRCLR.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_DA_IRR.equals(sidc))
        {
            return "DA";
        }
        else if (TacGrpSidc.FSUPP_ARS_C2ARS_ZOR_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_ZOR_CIRCLR.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_DA_IRR.equals(sidc))
        {
            return "ZOR";
        }
        else if (TacGrpSidc.FSUPP_ARS_C2ARS_TBA_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_TBA_CIRCLR.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_TBA_IRR.equals(sidc))
        {
            return "TBA";
        }
        else if (TacGrpSidc.FSUPP_ARS_C2ARS_TVAR_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_TVAR_CIRCLR.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_C2ARS_TVAR_IRR.equals(sidc))
        {
            return "TVAR";
        }
        else if (TacGrpSidc.FSUPP_ARS_TGTAQZ_ATIZ_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_TGTAQZ_ATIZ_IRR.equals(sidc))
        {
            return "ATI ZONE";
        }
        else if (TacGrpSidc.FSUPP_ARS_TGTAQZ_CFFZ_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_TGTAQZ_CFFZ_IRR.equals(sidc))
        {
            return "CFF ZONE";
        }
        else if (TacGrpSidc.FSUPP_ARS_TGTAQZ_CNS_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_TGTAQZ_CNS_IRR.equals(sidc))
        {
            return "CENSOR ZONE";
        }
        else if (TacGrpSidc.FSUPP_ARS_TGTAQZ_CFZ_RTG.equals(sidc)
            || TacGrpSidc.FSUPP_ARS_TGTAQZ_CFZ_IRR.equals(sidc))
        {
            return "CF ZONE";
        }

        return "";
    }

    protected String createAirspaceCoordinationText(MilStd2525TacticalGraphic graphic)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ACA\n");

        Object o = graphic.getText();
        if (o != null)
        {
            sb.append(o);
            sb.append("\n");
        }

        Object[] altitudes = TacticalGraphicUtil.getAltitudeRange(graphic);
        if (altitudes[0] != null)
        {
            sb.append("MIN ALT: ");
            sb.append(altitudes[0]);
            sb.append("\n");
        }

        if (altitudes[1] != null)
        {
            sb.append("MAX ALT: ");
            sb.append(altitudes[1]);
            sb.append("\n");
        }

        o = graphic.getModifier(SymbologyConstants.ADDITIONAL_INFORMATION);
        if (o != null)
        {
            sb.append("Grids: ");
            sb.append(o);
            sb.append("\n");
        }

        Object[] dates = TacticalGraphicUtil.getDateRange(graphic);
        if (dates[0] != null)
        {
            sb.append("EFF: ");
            sb.append(dates[0]);
            sb.append("\n");
        }

        if (dates[1] != null)
        {
            sb.append("     "); // TODO do a better job of vertically aligning the start and end time labels
            sb.append(dates[1]);
        }

        return sb.toString();
    }
}
