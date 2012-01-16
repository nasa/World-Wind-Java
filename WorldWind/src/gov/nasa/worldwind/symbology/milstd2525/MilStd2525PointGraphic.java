/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.Logging;

import java.util.*;

/**
 * Implementation of MIL-STD-2525 point graphics. Point graphics are rendered in the same way as tactical symbols: by
 * drawing an icon at constant screen size.
 *
 * @author pabercrombie
 * @version $Id$
 */
// TODO: apply attributes to symbol
// TODO: apply delegate owner to symbol.
public class MilStd2525PointGraphic extends MilStd2525TacticalGraphic implements TacticalPoint
{
    /** Implementation of TacticalSymbol that is configured to create and layout tactical point graphics. */
    protected static class PointGraphicSymbol extends AbstractTacticalSymbol
    {
        /** Default icon retrieval URL. */
        protected static final String DEFAULT_RETRIEVER_BASE_URL = "http://worldwindserver.net/milstd2525/";
        /** Note that we use a static default retriever instance in order to cache the results it returns. */
        protected static final IconRetriever DEFAULT_ICON_RETRIEVER = new MilStd2525PointGraphicRetriever(
            DEFAULT_RETRIEVER_BASE_URL);

        protected SymbolCode symbolCode;

        /**
         * Constructs a new symbol with the specified position. The position specifies the latitude, longitude, and
         * altitude where this symbol is drawn on the globe. The position's altitude component is interpreted according
         * to the altitudeMode.
         *
         * @param symbolId 2525 SIDC for this symbol.
         * @param position The latitude, longitude, and altitude where the symbol is drawn.
         *
         * @throws IllegalArgumentException if the position is <code>null</code>.
         */
        protected PointGraphicSymbol(String symbolId, Position position)
        {
            super(position);

            // Initialize the symbol code from the symbol identifier specified at construction.
            this.symbolCode = new SymbolCode(symbolId);
            this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);

            // Configure this tactical graphic with the default tactical icon retriever, the default modifier retriever,
            // and the default modifier atlas.
            // TODO: replace default retrievers with per-instance objects that use the base URL to determine equality.
            this.setIconRetriever(DEFAULT_ICON_RETRIEVER);
        }

        /** {@inheritDoc} */
        public String getIdentifier()
        {
            return this.symbolCode.toString();
        }
    }

    /**
     * Indicates a string identifier for this symbol. The format of the identifier depends on the symbol set to which
     * this graphic belongs. For symbols belonging to the MIL-STD-2525 symbol set, this returns a 15-character
     * alphanumeric symbol identification code (SIDC). Calculated from the current modifiers at construction and during
     * each call to {@link #setModifier(String, Object)}. Initially <code>null</code>.
     */
    protected SymbolCode symbolCode;

    protected Object delegateOwner;

    /** Position of this graphic. */
    protected Position position;

    /** Symbol used to render this graphic. */
    protected TacticalSymbol symbol;

    /**
     * Create a new point graphic.
     *
     * @param sidc MIL-STD-2525 SIDC code that identifies the graphic.
     */
    public MilStd2525PointGraphic(String sidc)
    {
        this.init(sidc, modifiers);
    }

    protected void init(String symbolId, AVList modifiers)
    {
        // Initialize the symbol code from the symbol identifier specified at construction.
        this.symbolCode = new SymbolCode(symbolId);

        // Apply any caller-specified key-value pairs to the modifiers list. We apply these pairs last to give them
        // precedence.
        if (modifiers != null)
            this.modifiers.setValues(modifiers);
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.symbolCode.toString();
    }

    /**
     * Create a tactical symbol to render this graphic.
     *
     * @param symbolId SIDC of the symbol.
     * @param position Position of the symbol.
     *
     * @return A new tactical symbol.
     */
    protected TacticalSymbol createSymbol(String symbolId, Position position)
    {
        return new PointGraphicSymbol(symbolId, position);
    }

    @Override
    public String getCategory()
    {
        return this.symbolCode.getCategory();
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
    public Position getPosition()
    {
        return this.position;
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

        this.position = position;
    }

    /** {@inheritDoc} */
    public Object getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /** {@inheritDoc} */
    public void setDelegateOwner(Object owner)
    {
        this.delegateOwner = owner;
    }

    /////////////////////////////
    // Movable interface
    /////////////////////////////

    /** {@inheritDoc} */
    public Position getReferencePosition()
    {
        return this.getPosition();
    }

    /////////////////////////////
    // Rendering
    /////////////////////////////

    @Override
    protected void doRenderGraphic(DrawContext dc)
    {
        Position position = this.getPosition();
        if (position == null)
            return;

        // Create the symbol used to render the graphic, if it has not been created already.
        if (this.symbol == null)
        {
            this.symbol = this.createSymbol(this.getIdentifier(), position);
        }

        this.symbol.render(dc);
    }

    // TODO: create symbolic constants for each function id?
    public static final String[] POINT_GRAPHIC_FUNCTION_IDS = {
        "PCB---",
        "PCH---",
        "PCL---",
        "PCR---",
        "PCS---",
        "PS----",
        "PTN---",
        "PTS---",
        "APD---",
        "DPT---",
        "GPAA--",
        "GPAC--",
        "GPAH--",
        "GPAK--",
        "GPAL--",
        "GPAM--",
        "GPAO--",
        "GPAP--",
        "GPAR--",
        "GPAS--",
        "GPAT--",
        "GPAW--",
        "GPF---",
        "GPH---",
        "GPHA--",
        "GPHQ--",
        "GPHX--",
        "GPHY--",
        "GPO---",
        "GPOD--",
        "GPOP--",
        "GPOR--",
        "GPOW--",
        "GPOZ--",
        "GPP---",
        "GPPC--",
        "GPPD--",
        "GPPE--",
        "GPPK--",
        "GPPL--",
        "GPPO--",
        "GPPP--",
        "GPPR--",
        "GPPS--",
        "GPPW--",
        "GPRD--",
        "GPRI--",
        "GPRN--",
        "GPRS--",
        "GPUS--",
        "GPUSA-",
        "GPUSC-",
        "GPUSD-",
        "GPUUB-",
        "GPUUD-",
        "GPUUL-",
        "GPUUS-",
        "GPUY--",
        "GPUYA-",
        "GPUYK-",
        "GPUYP-",
        "GPUYR-",
        "GPUYT-",
        "GPWA--",
        "GPWD--",
        "GPWE--",
        "GPWG--",
        "GPWI--",
        "GPWM--",
        "GPWP--",
        "OPP---",
        "BCP---",
        "NEB---",
        "NEC---",
        "NF----",
        "NZ----",
        "OB----",
        "OMD---",
        "OME---",
        "OMP---",
        "OMT---",
        "OMU---",
        "OMW---",
        "SE----",
        "SF----",
        "SS----",
        "SU----",
        "PD----",
        "ED----",
        "EP----",
        "EV----",
        "FA----",
        "FE----",
        "FO----",
        "HI----",
        "HM----",
        "HO----",
        "SB----",
        "SBM---",
        "SBN---",
        "SBW---",
        "SBWD--",
        "SM----",
        "SS----",
        "PAS---",
        "PAT---",
        "PC----",
        "PD----",
        "PE----",
        "PI----",
        "PL----",
        "PM----",
        "PN----",
        "PO----",
        "PR----",
        "PSA---",
        "PSB---",
        "PSC---",
        "PSD---",
        "PSE---",
        "PSF---",
        "PSG---",
        "PSH---",
        "PSI---",
        "PSJ---",
        "PSZ---",
        "PT----",
        "PU----",
        "PX----",
        "PY----X"
    };
}
