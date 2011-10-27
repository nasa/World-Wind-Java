/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd1477;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.symbology.AbstractIconRetriever;
import gov.nasa.worldwind.util.Logging;

import java.awt.image.*;
import java.util.MissingResourceException;

/**
 * @author ccrick
 * @version $Id: MilStd1477IconRetriever.java 90 2011-25-10 23:58:29Z ccrick $
 */
public class MilStd1477IconRetriever extends AbstractIconRetriever
{
    // TODO: add more error checking

    public MilStd1477IconRetriever(String URL)
    {
        super(URL);
    }

    public BufferedImage createIcon(String symbolIdentifier)
    {
        AVListImpl params = new AVListImpl();

        return createIcon(symbolIdentifier, params);
    }

    public BufferedImage createIcon(String symbolIdentifier, AVList params)
    {
        // retrieve desired symbol and convert to bufferedImage

        // SymbolCode symbolCode = new SymbolCode(symbolIdentifier);

        BufferedImage img = null;
        String filename = getFilename(symbolIdentifier);

        img = retrieveImageFromURL(filename, img);

        if (img == null)
        {
            String msg = Logging.getMessage("Symbology.SymbolIconNotFound", symbolIdentifier);
            Logging.logger().severe(msg);
            throw new MissingResourceException(msg, BufferedImage.class.getName(), filename);
        }

        return img;
    }

    protected static String getFilename(String code)
    {
        return code.toLowerCase() + ".png";
    }
}