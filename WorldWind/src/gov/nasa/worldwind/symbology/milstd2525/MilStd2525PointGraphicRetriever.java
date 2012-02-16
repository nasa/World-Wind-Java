/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.symbology.*;
import gov.nasa.worldwind.symbology.milstd2525.graphics.TacGrpSidc;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;
import java.awt.image.*;
import java.util.MissingResourceException;

/**
 * Retriever to fetch icons for MIL-STD-2525C point graphics. The retriever can fetch images from either local or remote
 * locations.
 * <p/>
 * The retriever base URL must identify a location on a local or remote file system (including zip and jar files) that
 * holds the icon files in an expected directory structure. Each icon URL is constructed from three parts:
 * [base]/icons/[scheme]/[sidc].png. Parts of the SIDC that do not identify a type of graphic (such as echelon, status,
 * standard identity, etc.) are replaced with hyphens. For example, the Underwater Datum graphic (2.X.2.1.1.1.1.1) will
 * be retrieved from this URL: [base]/icons/tacgrp/g-g-gpuud-----x.png
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
    protected static final String DIR_ICON_TACGRP = "icons/tacgrp";
    /** Subdirectory for graphics in the Meteorological and Oceanographic scheme. */
    protected static final String DIR_ICON_METOC = "icons/metoc";
    /** Subdirectory for fill graphics. */
    protected static final String DIR_FILL_TACGRP = "fills/tacgrp";

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

        String filename = composeFilename(symbolCode);

        BufferedImage srcImg = retrieveImageFromURL(filename, null);

        if (srcImg == null)
        {
            String msg = Logging.getMessage("Symbology.SymbolIconNotFound", symbolCode);
            Logging.logger().severe(msg);
            throw new MissingResourceException(msg, BufferedImage.class.getName(), filename);
        }

        int width = srcImg.getWidth();
        int height = srcImg.getHeight();

        BufferedImage destImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

        this.drawImage(srcImg, destImg);

        Color color = this.getColorFromParams(params);
        if (color == null)
            color = this.getColorForStandardIdentity(symbolCode);

        this.multiply(destImg, color);

        if (this.mustDrawFill(symbolCode))
        {
            destImg = this.composeFilledImage(destImg, symbolCode);
        }

        return destImg;
    }

    /**
     * Create an image by drawing over a fill image.
     *
     * @param srcImg     Image to draw over fill.
     * @param symbolCode Symbol code that identifies the graphic.
     *
     * @return A new image with the {@code srcImg} drawn over the appropriate fill.
     */
    protected BufferedImage composeFilledImage(BufferedImage srcImg, SymbolCode symbolCode)
    {
        String fillPath = this.composeFillPath(symbolCode);
        BufferedImage fill = retrieveImageFromURL(fillPath, null);

        if (fill == null)
        {
            String msg = Logging.getMessage("Symbology.SymbolIconNotFound", symbolCode);
            Logging.logger().severe(msg);
            throw new MissingResourceException(msg, BufferedImage.class.getName(), fillPath);
        }

        int width = srcImg.getWidth();
        int height = srcImg.getHeight();

        BufferedImage filledImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

        this.drawImage(fill, filledImg);
        this.drawImage(srcImg, filledImg);

        return filledImg;
    }

    /**
     * Indicates whether or not a fill must be drawn for a graphic.
     *
     * @param code Symbol code of a point graphic.
     *
     * @return True if the graphic has a fill image. False if not. Only three graphics in MIL-STD-2525C Appendix B use a
     *         fill pattern: Nuclear Detonation Ground Zero (2.X.3.4.2), Biological Release Event (2.X.3.4.7.1), and
     *         Chemical Release Event (2.X.3.4.7.2).
     */
    protected boolean mustDrawFill(SymbolCode code)
    {
        String masked = code.toMaskedString();

        return TacGrpSidc.MOBSU_CBRN_NDGZ.equals(masked)
            || TacGrpSidc.MOBSU_CBRN_REEVNT_BIO.equals(masked)
            || TacGrpSidc.MOBSU_CBRN_REEVNT_CML.equals(masked);
    }

    /**
     * Retrieves the value of the AVKey.COLOR parameter.
     *
     * @param params Parameter list.
     *
     * @return The value of the AVKey.COLOR parameter, if such a parameter exists and is of type java.awt.Color. Returns
     *         null if the parameter list is null, if there is no value for key AVKey.COLOR, or if the value is not a
     *         Color.
     */
    protected Color getColorFromParams(AVList params)
    {
        if (params == null)
            return null;

        Object o = params.getValue(AVKey.COLOR);
        return (o instanceof Color) ? (Color) o : null;
    }

    /**
     * Indicates the color to apply to a graphic based on the graphic's standard identity.
     *
     * @param code Symbol code that identifies the graphic.
     *
     * @return Color to apply based on the standard identity. (Red for hostile entities, black for others.)
     */
    protected Color getColorForStandardIdentity(SymbolCode code)
    {
        if (SymbologyConstants.STANDARD_IDENTITY_HOSTILE.equals(code.getStandardIdentity()))
            return MilStd2525TacticalGraphic.MATERIAL_HOSTILE.getDiffuse();
        else
            return MilStd2525TacticalGraphic.MATERIAL_FRIEND.getDiffuse();
    }

    /**
     * Compose a file path to the fill icon for a graphic.
     *
     * @param code Code the identifies the graphic.
     *
     * @return Path to the appropriate fill image.
     */
    protected String composeFillPath(SymbolCode code)
    {
        // Note: Metoc symbols currently do not use fill, so only handle tactical graphics here.
        return this.composeFilenameTacticalGraphic(code, DIR_FILL_TACGRP);
    }

    /**
     * Indicates the filename of the icon for a graphic.
     *
     * @param code Code that identifies a graphic in MIL-STD-2525C.
     *
     * @return The file name of the image file that corresponds to the specified graphic, or null if the graphic's
     *         scheme is not recognized.
     */
    protected String composeFilename(SymbolCode code)
    {
        String scheme = code.getScheme();

        if (SymbologyConstants.SCHEME_TACTICAL_GRAPHICS.equals(scheme))
            return this.composeFilenameTacticalGraphic(code, DIR_ICON_TACGRP);
        else if (SymbologyConstants.SCHEME_METOC.equals(scheme))
            return this.composeFilenameMetoc(code);

        return null;
    }

    /**
     * Indicates the filename of a graphic in the Tactical Graphics scheme (MIL-STD-2525C Appendix B).
     *
     * @param code Code that identifies a graphic in the Tactical Graphics scheme.
     * @param dir  Directory to prepend to file name.
     *
     * @return The filename of the icon for the specified graphic.
     */
    protected String composeFilenameTacticalGraphic(SymbolCode code, String dir)
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
        sb.append(dir).append("/")
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
    protected String composeFilenameMetoc(SymbolCode code)
    {
        String scheme = code.getScheme();
        String category = code.getCategory();
        String staticDynamic = code.getStaticDynamic();
        String functionId = code.getFunctionId();
        String graphicType = code.getGraphicType();

        if (functionId == null)
            functionId = "------";

        StringBuilder sb = new StringBuilder();
        sb.append(DIR_ICON_METOC).append("/")
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
