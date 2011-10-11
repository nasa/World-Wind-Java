/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA. User: Camberley Date: 10/11/11 Time: 4:01 PM To change this template use File | Settings |
 * File Templates.
 */
public abstract class AbstractSymbolGenerator implements SymbolGenerator
{
    public final static String SOURCE_TYPE = "sourceType";
    public final static String SOURCE_PATH = "sourcePath";

    public BufferedImage retrieveImageFromFile(String filename, String path, BufferedImage img)
    {
        try {
            File file = new File(path + filename);
            img = ImageIO.read(file);
        } catch (Exception e) {
            // TODO: error handling
            return null;
        }

        return img;
    }

    public BufferedImage retrieveImageFromURL(String filename, String url, BufferedImage img)
    {
        // TODO

        return null;
    }
}
