/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Implementation of MIL-STD-2525 point graphics. Point graphics are rendered in the same way as tactical symbols: by
 * drawing an icon at constant screen size.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class MilStd2525PointGraphic extends AVListImpl implements TacticalPoint
{
    // Implementation note: This class wraps an instance of TacticalGraphicSymbol. TacticalGraphicSymbol implements the
    // logic for rendering point graphics using the TacticalSymbol base classes. This class adapts the TacticalGraphic
    // interface to the TacticalSymbol interface.

    /** Symbol used to render this graphic. */
    protected TacticalGraphicSymbol symbol;

    /** Indicates whether or not the graphic is highlighted. */
    protected boolean highlighted;

    /**
     * Attributes to apply when the graphic is not highlighted. These attributes override defaults determined by the
     * graphic's symbol code.
     */
    protected TacticalGraphicAttributes normalAttributes;
    /**
     * Attributes to apply when the graphic is highlighted. These attributes override defaults determined by the
     * graphic's symbol code.
     */
    protected TacticalGraphicAttributes highlightAttributes;

    /** Current frame timestamp. */
    protected long frameTimestamp = -1L;
    /** Attributes to use for the current frame. */
    protected TacticalSymbolAttributes activeSymbolAttributes = new BasicTacticalSymbolAttributes();

    protected static TacticalSymbolAttributes defaultSymbolAttributes = new BasicTacticalSymbolAttributes();

    /**
     * Create a new point graphic.
     *
     * @param sidc MIL-STD-2525 SIDC code that identifies the graphic.
     */
    public MilStd2525PointGraphic(String sidc)
    {
        this.symbol = this.createSymbol(sidc);
    }

    /**
     * Create a tactical symbol to render this graphic.
     *
     * @param sidc Symbol code that identifies the graphic.
     *
     * @return A new tactical symbol.
     */
    protected TacticalGraphicSymbol createSymbol(String sidc)
    {
        TacticalGraphicSymbol symbol = new TacticalGraphicSymbol(sidc);
        symbol.setAttributes(this.activeSymbolAttributes);
        symbol.setDelegateOwner(this);
        return symbol;
    }

    /** {@inheritDoc} */
    public boolean isVisible()
    {
        return this.symbol.isVisible();
    }

    /** {@inheritDoc} */
    public void setVisible(boolean visible)
    {
        this.symbol.setVisible(visible);
    }

    /** {@inheritDoc} */
    public Object getModifier(String modifier)
    {
        return this.symbol.getModifier(modifier);
    }

    /** {@inheritDoc} */
    public void setModifier(String modifier, Object value)
    {
        this.symbol.setModifier(modifier, value);
    }

    /** {@inheritDoc} */
    public boolean isShowModifiers()
    {
        return this.symbol.isShowTextModifiers();
    }

    /** {@inheritDoc} */
    public void setShowModifiers(boolean showModifiers)
    {
        this.symbol.setShowGraphicModifiers(showModifiers);
        this.symbol.setShowTextModifiers(showModifiers);
    }

    /** {@inheritDoc} */
    public boolean isShowLocation()
    {
        return this.symbol.isShowLocation();
    }

    /** {@inheritDoc} */
    public void setShowLocation(boolean show)
    {
        this.symbol.setShowLocation(show);
    }

    /** {@inheritDoc} */
    public boolean isShowHostileIndicator()
    {
        return this.symbol.isShowHostileIndicator();
    }

    /** {@inheritDoc} */
    public void setShowHostileIndicator(boolean show)
    {
        this.symbol.setShowHostileIndicator(show);
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.symbol.getIdentifier();
    }

    /** {@inheritDoc} */
    public void setText(String text)
    {
        this.symbol.setModifier(SymbologyConstants.UNIQUE_DESIGNATION, text);
    }

    /** {@inheritDoc} */
    public String getText()
    {
        // Get the Unique Designation modifier. If it's an iterable, return the first value.
        Object value = this.getModifier(SymbologyConstants.UNIQUE_DESIGNATION);
        if (value instanceof String)
        {
            return (String) value;
        }
        else if (value instanceof Iterable)
        {
            Iterator iterator = ((Iterable) value).iterator();
            Object o = iterator.hasNext() ? iterator.next() : null;
            if (o != null)
                return o.toString();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return Always returns an Iterable with only one position.
     */
    public Iterable<? extends Position> getPositions()
    {
        return Arrays.asList(this.getPosition());
    }

    /**
     * {@inheritDoc}
     *
     * @param positions Control points. This graphic uses only one control point.
     */
    public void setPositions(Iterable<? extends Position> positions)
    {
        if (positions == null)
        {
            String message = Logging.getMessage("nullValue.PositionsListIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Iterator<? extends Position> iterator = positions.iterator();
        if (!iterator.hasNext())
        {
            String message = Logging.getMessage("generic.InsufficientPositions");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.setPosition(iterator.next());
    }

    /** {@inheritDoc} */
    public TacticalGraphicAttributes getAttributes()
    {
        return this.normalAttributes;
    }

    /** {@inheritDoc} */
    public void setAttributes(TacticalGraphicAttributes attributes)
    {
        this.normalAttributes = attributes;
    }

    /** {@inheritDoc} */
    public TacticalGraphicAttributes getHighlightAttributes()
    {
        return this.highlightAttributes;
    }

    /** {@inheritDoc} */
    public void setHighlightAttributes(TacticalGraphicAttributes attributes)
    {
        this.highlightAttributes = attributes;
    }

    /** {@inheritDoc} */
    public Offset getLabelOffset()
    {
        return null; // Does not apply to point graphic
    }

    /** {@inheritDoc} */
    public void setLabelOffset(Offset offset)
    {
        // Does not apply to point graphic
    }

    /** {@inheritDoc} */
    public Object getDelegateOwner()
    {
        // If the application has supplied a delegate owner, return that object. If the owner is this object (the
        // default), return null to keep the contract of getDelegateOwner, which specifies that a value of null
        // indicates that the graphic itself is used during picking.
        Object owner = this.symbol.getDelegateOwner();
        return owner != this ? owner : null;
    }

    /** {@inheritDoc} */
    public void setDelegateOwner(Object owner)
    {
        // Apply new delegate owner if non-null. If the new owner is null, set this object as symbol's delegate owner
        // (the default).
        if (owner != null)
            this.symbol.setDelegateOwner(owner);
        else
            this.symbol.setDelegateOwner(this);
    }

    /** {@inheritDoc} */
    public Position getPosition()
    {
        return this.symbol.getPosition();
    }

    /** {@inheritDoc} */
    public void setPosition(Position position)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.symbol.setPosition(position);
    }

    /////////////////////////////
    // Movable interface
    /////////////////////////////

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.getPosition();
    }

    /** {@inheritDoc} */
    public void move(Position delta)
    {
        if (delta == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        Position refPos = this.getReferencePosition();

        // The reference position is null if this shape has no positions. In this case moving the shape by a
        // relative delta is meaningless. Therefore we fail softly by exiting and doing nothing.
        if (refPos == null)
            return;

        this.moveTo(refPos.add(delta));
    }

    /** {@inheritDoc} */
    public void moveTo(Position position)
    {
        this.symbol.setPosition(position);
    }

    /////////////////////////////
    // Highlightable interface
    /////////////////////////////

    /** {@inheritDoc} */
    public boolean isHighlighted()
    {
        return this.highlighted;
    }

    /** {@inheritDoc} */
    public void setHighlighted(boolean highlighted)
    {
        this.highlighted = highlighted;
    }

    /////////////////////////////
    // Rendering
    /////////////////////////////

    /** {@inheritDoc} */
    public void render(DrawContext dc)
    {
        long timestamp = dc.getFrameTimeStamp();
        if (this.frameTimestamp != timestamp)
        {
            this.determineActiveAttributes();
            this.frameTimestamp = timestamp;
        }

        this.symbol.render(dc);
    }

    /** Determine active attributes for this frame. */
    protected void determineActiveAttributes()
    {
        // Reset symbol attributes to default before applying overrides.
        this.activeSymbolAttributes.copy(defaultSymbolAttributes);

        if (this.isHighlighted())
        {
            TacticalGraphicAttributes highlightAttributes = this.getHighlightAttributes();

            // If the application specified overrides to the highlight attributes, then apply the overrides
            if (highlightAttributes != null)
            {
                // Apply overrides specified by application
                this.applyAttributesToSymbol(highlightAttributes, this.activeSymbolAttributes);
            }
        }
        else
        {
            // Apply overrides specified by application
            TacticalGraphicAttributes normalAttributes = this.getAttributes();
            if (normalAttributes != null)
            {
                this.applyAttributesToSymbol(normalAttributes, this.activeSymbolAttributes);
            }
        }
    }

    /**
     * Apply graphic attributes to the symbol.
     *
     * @param graphicAttributes Tactical graphic attributes to apply to the tactical symbol.
     * @param symbolAttributes  Symbol attributes to be modified.
     */
    protected void applyAttributesToSymbol(TacticalGraphicAttributes graphicAttributes,
        TacticalSymbolAttributes symbolAttributes)
    {
        // Line and area graphics distinguish between interior and outline opacity. Tactical symbols only support one
        // opacity, so use the interior opacity.
        Double value = graphicAttributes.getInteriorOpacity();
        if (value != null)
        {
            symbolAttributes.setOpacity(value);
        }

        value = graphicAttributes.getScale();
        if (value != null)
        {
            symbolAttributes.setScale(value);
        }

        Material material = graphicAttributes.getInteriorMaterial();
        if (material != null)
        {
            this.symbol.setColor(material.getDiffuse());
        }
        else
        {
            this.symbol.setColor(null);
        }

        Font font = graphicAttributes.getTextModifierFont();
        if (font != null)
        {
            symbolAttributes.setTextModifierFont(font);
        }

        material = graphicAttributes.getTextModifierMaterial();
        if (material != null)
        {
            symbolAttributes.setTextModifierMaterial(material);
        }
    }

    /**
     * Indicates the graphics supported by this class.
     *
     * @return List of masked SIDC strings that identify graphics that this class supports.
     */
    public static List<String> getSupportedGraphics()
    {
        return Arrays.asList(
            TacGrpSidc.TSK_DSTY,
            TacGrpSidc.TSK_ITDT,
            TacGrpSidc.TSK_NEUT,
            TacGrpSidc.C2GM_GNL_PNT_USW_UH2_DTM,
            TacGrpSidc.C2GM_GNL_PNT_USW_UH2_BCON,
            TacGrpSidc.C2GM_GNL_PNT_USW_UH2_LCON,
            TacGrpSidc.C2GM_GNL_PNT_USW_UH2_SNK,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_PTNCTR,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_DIFAR,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_LOFAR,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_CASS,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_DICASS,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_BT,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_ANM,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_VLAD,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_ATAC,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_RO,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_KGP,
            TacGrpSidc.C2GM_GNL_PNT_USW_SNBY_EXP,
            TacGrpSidc.C2GM_GNL_PNT_USW_SRH,
            TacGrpSidc.C2GM_GNL_PNT_USW_SRH_ARA,
            TacGrpSidc.C2GM_GNL_PNT_USW_SRH_DIPPSN,
            TacGrpSidc.C2GM_GNL_PNT_USW_SRH_CTR,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT_NAVREF,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT_SPLPNT,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT_DLRP,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT_PIM,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT_MRSH,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT_WAP,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT_CRDRTB,
            TacGrpSidc.C2GM_GNL_PNT_REFPNT_PNTINR,
            TacGrpSidc.C2GM_GNL_PNT_WPN_AIMPNT,
            TacGrpSidc.C2GM_GNL_PNT_WPN_DRPPNT,
            TacGrpSidc.C2GM_GNL_PNT_WPN_ENTPNT,
            TacGrpSidc.C2GM_GNL_PNT_WPN_GRDZRO,
            TacGrpSidc.C2GM_GNL_PNT_WPN_MSLPNT,
            TacGrpSidc.C2GM_GNL_PNT_WPN_IMTPNT,
            TacGrpSidc.C2GM_GNL_PNT_WPN_PIPNT,
            TacGrpSidc.C2GM_GNL_PNT_FRMN,
            TacGrpSidc.C2GM_GNL_PNT_HBR,
            TacGrpSidc.C2GM_GNL_PNT_HBR_PNTQ,
            TacGrpSidc.C2GM_GNL_PNT_HBR_PNTA,
            TacGrpSidc.C2GM_GNL_PNT_HBR_PNTY,
            TacGrpSidc.C2GM_GNL_PNT_HBR_PNTX,
            TacGrpSidc.C2GM_GNL_PNT_RTE,
            TacGrpSidc.C2GM_GNL_PNT_RTE_RDV,
            TacGrpSidc.C2GM_GNL_PNT_RTE_DVSN,
            TacGrpSidc.C2GM_GNL_PNT_RTE_WAP,
            TacGrpSidc.C2GM_GNL_PNT_RTE_PIM,
            TacGrpSidc.C2GM_GNL_PNT_RTE_PNTR,
            TacGrpSidc.C2GM_GNL_PNT_ACTL,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_CAP,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_ABNEW,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_TAK,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_ASBWF,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_ASBWR,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_SUWF,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_SUWR,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_MIWF,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_MIWR,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_SKEIP,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_TCN,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_TMC,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_RSC,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_RPH,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_UA,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_VTUA,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_ORB,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_ORBF8,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_ORBRT,
            TacGrpSidc.C2GM_GNL_PNT_ACTL_ORBRD,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_CHKPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_CONPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_CRDPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_DCNPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_LNKUPT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_PSSPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_RAYPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_RELPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_STRPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_AMNPNT,
            TacGrpSidc.C2GM_GNL_PNT_ACTPNT_WAP,
            TacGrpSidc.C2GM_GNL_PNT_SCTL,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_USV,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_USV_RMV,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_USV_ASW,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_USV_SUW,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_USV_MIW,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_ASW,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_SUW,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_MIW,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_PKT,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_RDV,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_RSC,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_REP,
            TacGrpSidc.C2GM_GNL_PNT_SCTL_NCBTT,
            TacGrpSidc.C2GM_GNL_PNT_UCTL,
            TacGrpSidc.C2GM_GNL_PNT_UCTL_UUV,
            TacGrpSidc.C2GM_GNL_PNT_UCTL_UUV_ASW,
            TacGrpSidc.C2GM_GNL_PNT_UCTL_UUV_SUW,
            TacGrpSidc.C2GM_GNL_PNT_UCTL_UUV_MIW,
            TacGrpSidc.C2GM_GNL_PNT_UCTL_SBSTN,
            TacGrpSidc.C2GM_GNL_PNT_UCTL_SBSTN_ASW,
            TacGrpSidc.C2GM_DEF_PNT_TGTREF,
            TacGrpSidc.C2GM_DEF_PNT_OBSPST,
            TacGrpSidc.C2GM_DEF_PNT_OBSPST_CBTPST,
            TacGrpSidc.C2GM_DEF_PNT_OBSPST_RECON,
            TacGrpSidc.C2GM_DEF_PNT_OBSPST_FWDOP,
            TacGrpSidc.C2GM_DEF_PNT_OBSPST_SOP,
            TacGrpSidc.C2GM_DEF_PNT_OBSPST_CBRNOP,
            TacGrpSidc.C2GM_OFF_PNT_PNTD,
            TacGrpSidc.C2GM_AVN_PNT_DAPP,
            TacGrpSidc.MOBSU_OBST_ATO_TDTSM_FIXPFD,
            TacGrpSidc.MOBSU_OBST_ATO_TDTSM_MVB,
            TacGrpSidc.MOBSU_OBST_ATO_TDTSM_MVBPFD,
            TacGrpSidc.MOBSU_OBST_BBY,
            TacGrpSidc.MOBSU_OBST_MNE_USPMNE,
            TacGrpSidc.MOBSU_OBST_MNE_ATMNE,
            TacGrpSidc.MOBSU_OBST_MNE_ATMAHD,
            TacGrpSidc.MOBSU_OBST_MNE_ATMDIR,
            TacGrpSidc.MOBSU_OBST_MNE_APMNE,
            TacGrpSidc.MOBSU_OBST_MNE_WAMNE,
            TacGrpSidc.MOBSU_OBST_AVN_TWR_LOW,
            TacGrpSidc.MOBSU_OBST_AVN_TWR_HIGH,
            TacGrpSidc.MOBSU_OBSTBP_CSGSTE_ERP,
            TacGrpSidc.MOBSU_SU_ESTOF,
            TacGrpSidc.MOBSU_SU_FRT,
            TacGrpSidc.MOBSU_SU_SUFSHL,
            TacGrpSidc.MOBSU_SU_UGDSHL,
            TacGrpSidc.MOBSU_CBRN_NDGZ,
            TacGrpSidc.MOBSU_CBRN_FAOTP,
            TacGrpSidc.MOBSU_CBRN_REEVNT_BIO,
            TacGrpSidc.MOBSU_CBRN_REEVNT_CML,
            TacGrpSidc.MOBSU_CBRN_DECONP_USP,
            TacGrpSidc.MOBSU_CBRN_DECONP_ALTUSP,
            TacGrpSidc.MOBSU_CBRN_DECONP_TRP,
            TacGrpSidc.MOBSU_CBRN_DECONP_EQT,
            TacGrpSidc.MOBSU_CBRN_DECONP_EQTTRP,
            TacGrpSidc.MOBSU_CBRN_DECONP_OPDECN,
            TacGrpSidc.MOBSU_CBRN_DECONP_TRGH,
            TacGrpSidc.FSUPP_PNT_TGT_PTGT,
            TacGrpSidc.FSUPP_PNT_TGT_NUCTGT,
            TacGrpSidc.FSUPP_PNT_C2PNT_FSS,
            TacGrpSidc.FSUPP_PNT_C2PNT_SCP,
            TacGrpSidc.FSUPP_PNT_C2PNT_FP,
            TacGrpSidc.FSUPP_PNT_C2PNT_RP,
            TacGrpSidc.FSUPP_PNT_C2PNT_HP,
            TacGrpSidc.FSUPP_PNT_C2PNT_LP,
            TacGrpSidc.CSS_PNT_AEP,
            TacGrpSidc.CSS_PNT_CBNP,
            TacGrpSidc.CSS_PNT_CCP,
            TacGrpSidc.CSS_PNT_CVP,
            TacGrpSidc.CSS_PNT_DCP,
            TacGrpSidc.CSS_PNT_EPWCP,
            TacGrpSidc.CSS_PNT_LRP,
            TacGrpSidc.CSS_PNT_MCP,
            TacGrpSidc.CSS_PNT_RRRP,
            TacGrpSidc.CSS_PNT_ROM,
            TacGrpSidc.CSS_PNT_TCP,
            TacGrpSidc.CSS_PNT_TTP,
            TacGrpSidc.CSS_PNT_UMC,
            TacGrpSidc.CSS_PNT_SPT_GNL,
            TacGrpSidc.CSS_PNT_SPT_CLS1,
            TacGrpSidc.CSS_PNT_SPT_CLS2,
            TacGrpSidc.CSS_PNT_SPT_CLS3,
            TacGrpSidc.CSS_PNT_SPT_CLS4,
            TacGrpSidc.CSS_PNT_SPT_CLS5,
            TacGrpSidc.CSS_PNT_SPT_CLS6,
            TacGrpSidc.CSS_PNT_SPT_CLS7,
            TacGrpSidc.CSS_PNT_SPT_CLS8,
            TacGrpSidc.CSS_PNT_SPT_CLS9,
            TacGrpSidc.CSS_PNT_SPT_CLS10,
            TacGrpSidc.CSS_PNT_AP_ASP,
            TacGrpSidc.CSS_PNT_AP_ATP,
            TacGrpSidc.OTH_ER_DTHAC,
            TacGrpSidc.OTH_ER_PIW,
            TacGrpSidc.OTH_ER_DSTVES,
            TacGrpSidc.OTH_HAZ_SML,
            TacGrpSidc.OTH_HAZ_IB,
            TacGrpSidc.OTH_HAZ_OLRG,
            TacGrpSidc.OTH_SSUBSR_BTMRTN,
            TacGrpSidc.OTH_SSUBSR_BTMRTN_INS,
            TacGrpSidc.OTH_SSUBSR_BTMRTN_SBRSOO,
            TacGrpSidc.OTH_SSUBSR_BTMRTN_WRKND,
            TacGrpSidc.OTH_SSUBSR_BTMRTN_WRKD,
            TacGrpSidc.OTH_SSUBSR_MARLFE,
            TacGrpSidc.OTH_SSUBSR_SA,
            TacGrpSidc.OTH_FIX_ACU,
            TacGrpSidc.OTH_FIX_EM,
            TacGrpSidc.OTH_FIX_EOP);
    }
}
