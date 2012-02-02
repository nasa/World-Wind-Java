/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.util.Logging;

import java.awt.image.*;
import java.util.MissingResourceException;

/**
 * Retriever to fetch icons for MIL-STD-2525C point graphics. The retriever can fetch images from either local or remote
 * locations.
 * <p/>
 * The retriever base URL must identify a location on a local or remote file system (including zip and jar files) that
 * holds the icon files in an expected directory structure. Each icon URL is constructed from three parts:
 * [base]/[scheme]/[sidc].png. Parts of the SIDC that do not identify a type of graphic (such as echelon, status,
 * standard identity, etc.) are replaced with hyphens. For example, the Underwater Datum graphic (2.X.2.1.1.1.1.1) will
 * be retrieved from this URL: [base]/tacgrp/g-g-gpuud-----x.png
 * <p/>
 * Most applications should not use this class directly. See <a href="http://goworldwind.org/developers-guide/symbology/tactical-symbols/#offline-use">Offline
 * Use</a> for information on how to set the icon retrieval location.
 *
 * @author pabercrombie
 * @version $Id$
 */
public class MilStd2525PointGraphicRetriever extends AbstractIconRetriever
{
    /** Suffix added to file names to indicate the file type. */
    protected static final String PATH_SUFFIX = ".png";

    /** Subdirectory for graphics in the Tactical Graphics scheme. */
    protected static final String DIR_TACTICAL_GRAPHICS = "tacgrp";
    /** Subdirectory for graphics in the Meteorological and Oceanographic scheme. */
    protected static final String DIR_METOC = "metoc";

    /**
     * Create a new icon retriever.
     *
     * @param url Base URL for symbol graphics.
     */
    public MilStd2525PointGraphicRetriever(String url)
    {
        super(url);
    }

    public BufferedImage createIcon(String symbolIdentifier, AVList params)
    {
        if (symbolIdentifier == null)
        {
            String msg = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        // Retrieve desired symbol and convert to BufferedImage
        SymbolCode symbolCode = new SymbolCode(symbolIdentifier);

        BufferedImage img = null;
        String filename = getFilename(symbolCode);

        img = retrieveImageFromURL(filename, img);

        if (img == null)
        {
            String msg = Logging.getMessage("Symbology.SymbolIconNotFound", symbolCode);
            Logging.logger().severe(msg);
            throw new MissingResourceException(msg, BufferedImage.class.getName(), filename);
        }

        return img;
    }

    /**
     * Indicates the filename of the icon for a graphic.
     *
     * @param code Code that identifies a graphic in MIL-STD-2525C.
     *
     * @return The file name of the image file that corresponds to the specified graphic, or null if the graphic's
     *         scheme is not recognized.
     */
    protected String getFilename(SymbolCode code)
    {
        String scheme = code.getScheme();

        if (SymbologyConstants.SCHEME_TACTICAL_GRAPHICS.equals(scheme))
            return this.getFilenameTacticalGraphic(code);
        else if (SymbologyConstants.SCHEME_METOC.equals(scheme))
            return this.getFilenameMetoc(code);

        return null;
    }

    /**
     * Indicates the filename of a graphic in the Tactical Graphics scheme (MIL-STD-2525C Appendix B).
     *
     * @param code Code that identifies a graphic in the Tactical Graphics scheme.
     *
     * @return The filename of the icon for the specified graphic.
     */
    protected String getFilenameTacticalGraphic(SymbolCode code)
    {
        String scheme = code.getScheme();
        String category = code.getCategory();
        String functionId = code.getFunctionId();

        // Two images are provided for each graphic: one for Present status and one for all other statuses.
        // MIL-STD-2525C section 5.5.1.2 (pg. 37) states that graphics must draw using solid lines when Present, and
        // dashed lines in other states.
        char status = SymbologyConstants.STATUS_PRESENT.equals(code.getStatus()) ? 'p' : 'a';

        // MIL-STD-2525C Tactical Graphics includes the order of battle in the symbol identifier, but X is the only valid
        // value. Just pass X so retrieval will still work even if the symbol code is specified incorrectly.
        char orderOfBattle = 'x';

        if (functionId == null)
            functionId = "------";

        StringBuilder sb = new StringBuilder();
        sb.append(DIR_TACTICAL_GRAPHICS).append("/")
            .append(scheme.toLowerCase())
            .append('-') // Standard identity
            .append(category.toLowerCase())
            .append(status)
            .append(functionId.toLowerCase())
            .append("----") // Echelon, Country Code
            .append(orderOfBattle) // Order of Battle
            .append(PATH_SUFFIX);

        return sb.toString();
    }

    /**
     * Indicates the filename of a graphic in the Meteorological and Oceanographic scheme (MIL-STD-2525C Appendix C).
     *
     * @param code Code that identifies a graphic in the Metoc scheme.
     *
     * @return The filename of the icon for the specified graphic.
     */
    protected String getFilenameMetoc(SymbolCode code)
    {
        String scheme = code.getScheme();
        String category = code.getCategory();
        String staticDynamic = code.getStaticDynamic();
        String functionId = code.getFunctionId();
        String graphicType = code.getGraphicType();

        if (functionId == null)
            functionId = "------";

        StringBuilder sb = new StringBuilder();
        sb.append(DIR_METOC).append("/")
            .append(scheme)
            .append(category)
            .append(staticDynamic)
            .append(functionId)
            .append(graphicType)
            .append("--") // Positions 14, 15 unused
            .append(PATH_SUFFIX);

        return sb.toString().toLowerCase();
    }
}
