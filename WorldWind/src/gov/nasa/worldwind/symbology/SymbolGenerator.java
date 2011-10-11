/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.avlist.AVList;

import java.awt.image.*;

/**
 * @author ccrick
 * @version $Id: SymbolGenerator.java 90 2011-10-10 23:58:29Z ccrick $
 */
public interface SymbolGenerator
{
    BufferedImage createImage(String symbolIdentifier, AVList params);
}
