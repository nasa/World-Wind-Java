/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.util.Logging;

import java.util.*;

import static gov.nasa.worldwind.symbology.milstd2525.MilStd2525PointGraphic.LabelLayout;

/**
 * Object to provide default label layouts for MIL-STD-2525C tactical point graphics. The layout is used to arrange text
 * modifiers around the icon.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class DefaultLabelLayouts implements TacGrpSidc
{
    /** Offset to align the center of the graphic with the geographic position. */
    protected static Offset CENTER_OFFSET = new Offset(0.5, 0.5, AVKey.FRACTION, AVKey.FRACTION);

    /** Map to hold layouts. */
    protected Map<String, Map<String, List<LabelLayout>>> layouts
        = new HashMap<String, Map<String, List<LabelLayout>>>();

    /** Create the map and populate it with the default layouts. */
    public DefaultLabelLayouts()
    {
        this.populateMap();
    }

    /**
     * Indicates the layout for a particular type of graphic.
     *
     * @param sidc Symbol code of the graphic.
     *
     * @return Map that represents the label layout. The keys indicate the modifier key (unique designation, additional
     *         info, etc.). The values are lists of LabelLayout. Most modifiers will only specify a single layout, but
     *         some graphics support multiple instances of the same modifier, in which case the list will contain
     *         multiple layouts.
     */
    public Map<String, List<LabelLayout>> get(String sidc)
    {
        Map<String, List<LabelLayout>> layout = this.layouts.get(sidc);
        return layout != null ? layout : Collections.<String, List<LabelLayout>>emptyMap();
    }

    /** Populate the map with the default layouts. */
    protected void populateMap()
    {
        this.layouts.put(C2GM_GNL_PNT_HBR,
            this.createLayout(SymbologyConstants.ADDITIONAL_INFORMATION, CENTER_OFFSET, CENTER_OFFSET));

        // T, center of graphic
        this.layouts.put(C2GM_GNL_PNT_ACTPNT_DCNPNT,
            this.createLayout(SymbologyConstants.UNIQUE_DESIGNATION, CENTER_OFFSET, CENTER_OFFSET));

//        C2GM_GNL_PNT_ACTPNT; // Pentagon, full layout
//
//        // Pentagon, no H1
        Map<String, List<LabelLayout>> layout = new HashMap<String, List<LabelLayout>>();
        this.add(layout, SymbologyConstants.UNIQUE_DESIGNATION,
            Offset.fromFraction(1.1, 1.0),
            Offset.fromFraction(0.0, 1.0));
        this.add(layout, SymbologyConstants.ADDITIONAL_INFORMATION,
            Offset.fromFraction(0.5, 1.0),
            Offset.fromFraction(0.5, 0.0));
        this.add(layout, SymbologyConstants.HOSTILE_ENEMY,
            Offset.fromFraction(1.1, 0.35),
            Offset.fromFraction(0.0, 0.0));

        this.putAll(layout,
            C2GM_GNL_PNT_ACTPNT_CHKPNT,
            C2GM_GNL_PNT_ACTPNT_LNKUPT,
            C2GM_GNL_PNT_ACTPNT_PSSPNT,
            C2GM_GNL_PNT_ACTPNT_RAYPNT,
            C2GM_GNL_PNT_ACTPNT_RELPNT,
            C2GM_GNL_PNT_ACTPNT_STRPNT,
            C2GM_GNL_PNT_ACTPNT_AMNPNT,
            C2GM_OFF_PNT_PNTD,
            MOBSU_OBSTBP_CSGSTE_ERP,
            MOBSU_CBRN_DECONP_ALTUSP,
            MOBSU_CBRN_DECONP_TRP,
            MOBSU_CBRN_DECONP_EQT,
            MOBSU_CBRN_DECONP_EQTTRP,
            MOBSU_CBRN_DECONP_OPDECN,
            MOBSU_CBRN_DECONP_TRGH,
            FSUPP_PNT_C2PNT_SCP,
            FSUPP_PNT_C2PNT_FP,
            FSUPP_PNT_C2PNT_RP,
            FSUPP_PNT_C2PNT_HP,
            FSUPP_PNT_C2PNT_LP,
            CSS_PNT_CBNP,
            CSS_PNT_CCP,
            CSS_PNT_CVP,
            CSS_PNT_DCP,
            CSS_PNT_EPWCP,
            CSS_PNT_LRP,
            CSS_PNT_MCP,
            CSS_PNT_RRRP,
            CSS_PNT_ROM,
            CSS_PNT_TCP,
            CSS_PNT_TTP,
            CSS_PNT_UMC,
            CSS_PNT_SPT_GNL,
            CSS_PNT_SPT_CLS1,
            CSS_PNT_SPT_CLS2,
            CSS_PNT_SPT_CLS3,
            CSS_PNT_SPT_CLS4,
            CSS_PNT_SPT_CLS5,
            CSS_PNT_SPT_CLS6,
            CSS_PNT_SPT_CLS7,
            CSS_PNT_SPT_CLS8,
            CSS_PNT_SPT_CLS9,
            CSS_PNT_SPT_CLS10,
            CSS_PNT_AP_ASP,
            CSS_PNT_AP_ATP);
//
//        // Pentagon w/ T1
//        CSS_PNT

        layout = new HashMap<String, List<LabelLayout>>();
        this.add(layout, SymbologyConstants.DATE_TIME_GROUP,
            Offset.fromFraction(0.0, 1.0),
            Offset.fromFraction(1.0, 1.0));
        this.add(layout, SymbologyConstants.DATE_TIME_GROUP,
            Offset.fromFraction(0.0, 1.0),
            Offset.fromFraction(1.0, 1.0));
        this.add(layout, SymbologyConstants.ADDITIONAL_INFORMATION,
            Offset.fromFraction(1.0, 1.0),
            Offset.fromFraction(0.0, 1.0));
        this.add(layout, SymbologyConstants.HOSTILE_ENEMY,
            Offset.fromFraction(1.0, 0.0),
            Offset.fromFraction(0.0, 0.0));
        this.add(layout, SymbologyConstants.TYPE,
            Offset.fromFraction(0.0, 0.5),
            Offset.fromFraction(1.0, 0.5));
        this.add(layout, SymbologyConstants.UNIQUE_DESIGNATION,
            Offset.fromFraction(0.0, 0.0),
            Offset.fromFraction(1.0, 0.0));
        this.layouts.put(MOBSU_CBRN_REEVNT_BIO, layout);
        this.layouts.put(MOBSU_CBRN_REEVNT_CML, layout);

        layout = new HashMap<String, List<LabelLayout>>(layout);
        this.add(layout, SymbologyConstants.QUANTITY,
            Offset.fromFraction(0.5, 1.0),
            Offset.fromFraction(0.5, 0.0));
        this.layouts.put(MOBSU_CBRN_NDGZ, layout);

        // Google maps flag, custom offset
        layout = this.createLayout(SymbologyConstants.UNIQUE_DESIGNATION,
            Offset.fromFraction(0.5, 0.7), CENTER_OFFSET);
        this.layouts.put(C2GM_GNL_PNT_REFPNT_PNTINR, layout);

        // Square flag
        layout = this.createLayout(SymbologyConstants.UNIQUE_DESIGNATION,
            Offset.fromFraction(0.5, 0.65), CENTER_OFFSET);
        this.layouts.put(C2GM_GNL_PNT_ACTPNT_CONPNT, layout);

        // X, T on left
        layout = this.createLayout(SymbologyConstants.UNIQUE_DESIGNATION,
            Offset.fromFraction(0.75, 0.5),
            Offset.fromFraction(0.0, 0.5));
        this.layouts.put(C2GM_GNL_PNT_ACTPNT_WAP, layout);
        this.layouts.put(FSUPP_PNT_C2PNT_FSS, layout);

        // cross, T in upper right quad
        layout = this.createLayout(SymbologyConstants.UNIQUE_DESIGNATION,
            Offset.fromFraction(0.75, 0.75),
            Offset.fromFraction(0.0, 0.0));
        this.layouts.put(C2GM_DEF_PNT_TGTREF, layout);
        this.layouts.put(FSUPP_PNT_TGT_NUCTGT, layout);

        // Tower graphics use the altitude modifier
        layout = this.createLayout(SymbologyConstants.ALTITUDE_DEPTH,
            Offset.fromFraction(0.75, 0.75),
            Offset.fromFraction(0.0, 0.0));
        this.layouts.put(MOBSU_OBST_AVN_TWR_LOW, layout);
        this.layouts.put(MOBSU_OBST_AVN_TWR_HIGH, layout);

        layout = new HashMap<String, List<LabelLayout>>();
        this.add(layout, SymbologyConstants.UNIQUE_DESIGNATION,
            Offset.fromFraction(0.75, 0.75),
            Offset.fromFraction(0.0, 0.0));
        this.add(layout, SymbologyConstants.ADDITIONAL_INFORMATION,
            Offset.fromFraction(0.75, 0.25),
            Offset.fromFraction(0.0, 1.0),
            Offset.fromFraction(0.25, 0.25),
            Offset.fromFraction(1.0, 1.0));
        this.layouts.put(FSUPP_PNT_TGT_PTGT, layout);
    }

    protected Map<String, List<LabelLayout>> createLayout(String key, Offset offset, Offset hotspot)
    {
        Map<String, List<LabelLayout>> layout
            = new HashMap<String, List<LabelLayout>>();

        layout.put(key, Arrays.asList(new LabelLayout(offset, hotspot)));

        return layout;
    }

    protected void add(Map<String, List<LabelLayout>> layout, String key, Offset... offsets)
    {
        if (offsets.length % 2 != 0)
        {
            String msg = Logging.getMessage("generic.ArrayInvalidLength", offsets.length);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        List<LabelLayout> list = new ArrayList<LabelLayout>();
        for (int i = 0; i < offsets.length; i += 2)
        {
            Offset offset = offsets[i];
            Offset hotspot = offsets[i + 1];

            list.add(new LabelLayout(offset, hotspot));
        }

        layout.put(key, list);
    }

    /**
     * Map one value to many keys.
     *
     * @param value Value to add.
     * @param keys  Keys that map to the value.
     */
    protected void putAll(Map<String, List<LabelLayout>> value, String... keys)
    {
        for (String sidc : keys)
        {
            this.layouts.put(sidc, value);
        }
    }
}
