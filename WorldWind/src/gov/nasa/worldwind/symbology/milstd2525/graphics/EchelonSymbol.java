/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.symbology.AbstractTacticalSymbol;
import gov.nasa.worldwind.symbology.milstd2525.*;
import gov.nasa.worldwind.util.Logging;

/**
 * Tactical symbol implementation to render the echelon modifier as part of a tactical graphic.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class EchelonSymbol extends AbstractTacticalSymbol
{
    /** Identifier for this graphic. */
    protected String echelonId;

    /**
     * Constructs a new symbol with the specified position. The position specifies the latitude, longitude, and altitude
     * where this symbol is drawn on the globe. The position's altitude component is interpreted according to the
     * altitudeMode.
     *
     * @param sidc MIL-STD-2525C sidc code.
     *
     * @throws IllegalArgumentException if {@code sidc} is null, or does not contain a value for the Echelon field.
     */
    public EchelonSymbol(String sidc)
    {
        super();

        if (sidc == null)
        {
            String msg = Logging.getMessage("nullValue.SymbolCodeIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        SymbolCode symbolCode = new SymbolCode(sidc);
        String echelon = symbolCode.getEchelon();
        if (SymbolCode.isFieldEmpty(echelon))
        {
            String msg = Logging.getMessage("Symbology.InvalidSymbolCode", sidc);
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.echelonId = "-" + echelon;

        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        this.setOffset(Offset.fromFraction(0.5, 0));

        // Configure this tactical point graphic's icon retriever and modifier retriever with either the
        // configuration value or the default value (in that order of precedence).
        String iconRetrieverPath = Configuration.getStringValue(AVKey.MIL_STD_2525_ICON_RETRIEVER_PATH,
            MilStd2525Constants.DEFAULT_ICON_RETRIEVER_PATH);
        this.setIconRetriever(new MilStd2525ModifierRetriever(iconRetrieverPath));
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.echelonId;
    }
}
