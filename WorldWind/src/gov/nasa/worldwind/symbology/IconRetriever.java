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
 * @version $Id: IconRetriever.java 90 2011-17-10 23:58:29Z ccrick $
 */
public interface IconRetriever
{
    BufferedImage createIcon(String symbolIdentifier, AVList params);
}