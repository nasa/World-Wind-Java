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
 * @author pabercrombie
 * @version $Id$
 */
public class MilStd2525PointGraphicRetriever extends AbstractIconRetriever
{
    /** Subdirectory for graphics in the Tactical Graphics scheme. */
    protected static final String DIR_TACTICAL_GRAPHICS = "tacgrp";
    /** Subdirectory for graphics in the Meteorological and Oceanographic scheme. */
    protected static final String DIR_METOC = "metoc";

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

    protected String getFilename(SymbolCode code)
    {
        String scheme = code.getScheme();

        if (SymbologyConstants.SCHEME_TACTICAL_GRAPHICS.equals(scheme))
            return this.getFilenameTacticalGraphic(code);
        else if (SymbologyConstants.SCHEME_METOC.equals(scheme))
            return this.getFilenameMetoc(code);

        return null;
    }

    protected String getFilenameTacticalGraphic(SymbolCode code)
    {
        String scheme = code.getScheme();
        String category = code.getCategory();
        String functionId = code.getFunctionId();

        // MIL-STD-2525C includes the order of battle in the symbol identifier, but X is the only valid
        // value. Just pass X so retrieval will still work even if the symbol code is specified incorrectly.
        char orderOfBattle = 'x';

        if (functionId == null)
            functionId = "------";

        StringBuilder sb = new StringBuilder();
        sb.append(DIR_TACTICAL_GRAPHICS).append("/")
            .append(scheme.toLowerCase())
            .append('-') // Standard identity
            .append(category.toLowerCase())
            .append('p') // TODO need to handle "Anticipated status differently
            .append(functionId.toLowerCase())
            .append("----") // Echelon, Country Code
            .append(orderOfBattle) // Order of Battle
            .append(".png");

        return sb.toString();
    }

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
            .append(".png");

        return sb.toString().toLowerCase();
    }
}
