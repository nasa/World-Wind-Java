/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology.milstd2525;


import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.symbology.AbstractSymbolGenerator;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;

/**
 * @author ccrick
 * @version $Id: MilStd2525SymbolGenerator.java 90 2011-10-10 23:58:29Z ccrick $
 */
public class MilStd2525SymbolGenerator extends AbstractSymbolGenerator
{
    // TODO: add more error checking

    public BufferedImage createImage(String symbolIdentifier, AVList params)
    {
        if( symbolIdentifier.length() != 15 ) {
            return null;
        }

        SymbolCode symbolCode = new SymbolCode(symbolIdentifier);

        // retrieve desired symbol and convert to bufferedImage
        BufferedImage img = null;

        if (params.getValue(AbstractSymbolGenerator.SOURCE_TYPE).equals("file"))
        {
            String path = (String) params.getValue(AbstractSymbolGenerator.SOURCE_PATH);
            String filename = getFilename(symbolCode);

            img = retrieveImageFromFile(filename, path, img);
        }
        else if (params.getValue(AbstractSymbolGenerator.SOURCE_TYPE).equals("url"))
        {
            // TODO: retrieve image from URL
        }

        // TODO: modify image with given params

        return img;
    }

    private static String getFilename(SymbolCode code) {

        String standardID = (String) code.getValue(SymbolCode.STANDARD_IDENTITY);
        standardID = standardID.toLowerCase();

        int prefix = 0;
        char stdid = 'u';
        // See Table I and TABLE II, p.15 of MIL-STD-2525C for standard identities that use similar shapes
        switch(standardID.charAt(0))
        {
            case 'p' :      // PENDING
            case 'u' :      // UNKNOWN
            case 'g' :      // EXERCISE PENDING
            case 'w' :      // EXERCISE UNKNOWN
                prefix = 0;
                stdid = 'u';
                break;
            case 'f' :      // FRIEND
            case 'a' :      // ASSUMED FRIEND
            case 'd' :      // EXERCISE FRIEND
            case 'm' :      // EXERCISE ASSUMED FRIEND
            case 'j' :      // JOKER
            case 'k' :      // FAKER
                prefix = 1;
                stdid = 'f';
            case 'n' :      // NEUTRAL
            case 'l' :      // EXERCISE NEUTRAL
                prefix = 2;
                stdid = 'n';
                break;
            case 'h' :      // HOSTILE
            case 's' :      // SUSPECT
                prefix = 3;
                stdid = 'h';
        }

        String padding = "-----";

        String result = Integer.toString(prefix) + '.' + code.getValue(SymbolCode.SCHEME).toString().toLowerCase()
                        + stdid + code.getValue(SymbolCode.BATTLE_DIMENSION).toString().toLowerCase()
                        + 'p' + code.getValue(SymbolCode.FUNCTION_ID).toString().toLowerCase()
                        //+ code.getValue(SymbolCode.SYMBOL_MODIFIER).toString().toLowerCase()
                        + padding + ".png";
        return result;
    }
}
