/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525.graphics;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525Constants;
import gov.nasa.worldwind.util.Logging;

import java.awt.image.*;

/**
 * Tactical symbol implementation to render the echelon modifier as part of a tactical graphic.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class EchelonSymbol extends AbstractTacticalSymbol
{
    protected static final String PATH_PREFIX = "modifiers/";
    protected static final String PATH_SUFFIX = ".png";

    /** Icon retriever to retrieve echelon icons. */
    protected static class EchelonIconRetriever extends AbstractIconRetriever
    {
        /**
         * Create a new icon retriever.
         *
         * @param url Base URL for symbol graphics.
         */
        public EchelonIconRetriever(String url)
        {
            super(url);
        }

        public BufferedImage createIcon(String symbolId, AVList params)
        {
            if (symbolId == null)
            {
                String msg = Logging.getMessage("nullValue.SymbolCodeIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            // Compose a path from the modifier code and value.
            String path = this.composePath(symbolId);
            if (path == null)
            {
                String msg = Logging.getMessage("Symbology.SymbolIconNotFound", symbolId);
                Logging.logger().severe(msg);
                throw new WWRuntimeException(msg);
            }

            BufferedImage image = this.readImage(path);
            if (image == null)
            {
                String msg = Logging.getMessage("Symbology.SymbolIconNotFound", symbolId);
                Logging.logger().severe(msg);
                throw new WWRuntimeException(msg);
            }

            return image;
        }

        protected String composePath(String echelon)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(PATH_PREFIX);
            sb.append('-');
            sb.append(echelon.toLowerCase());
            sb.append(PATH_SUFFIX);
            return sb.toString();
        }
    }

    /** Identifier for this graphic. */
    protected String echelon;

    /**
     * Constructs a new symbol with the specified position. The position specifies the latitude, longitude, and altitude
     * where this symbol is drawn on the globe. The position's altitude component is interpreted according to the
     * altitudeMode.
     *
     * @param echelon MIL-STD-2525C echelon code.
     */
    public EchelonSymbol(String echelon)
    {
        super();
        this.echelon = echelon;

        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        this.setOffset(Offset.fromFraction(0.5, 0));

        // Configure this tactical point graphic's icon retriever and modifier retriever with either the
        // configuration value or the default value (in that order of precedence).
        String iconRetrieverPath = Configuration.getStringValue(AVKey.MIL_STD_2525_ICON_RETRIEVER_PATH,
            MilStd2525Constants.DEFAULT_ICON_RETRIEVER_PATH);
        this.setIconRetriever(new EchelonIconRetriever(iconRetrieverPath));
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return this.echelon;
    }
}
