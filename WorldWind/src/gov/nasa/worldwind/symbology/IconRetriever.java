/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.avlist.AVList;

import java.awt.image.*;

/**
 * An IconRetriever loads icons for symbols in a symbol set. Typically, an icon retriever will be implemented for a
 * specific symbol set. For example, the {@link gov.nasa.worldwind.symbology.milstd2525.MilStd2525IconRetriever}
 * retrieves icons for symbols in the MIL-STD-2525 symbology set.
 *
 * @author ccrick
 * @version $Id: IconRetriever.java 90 2011-17-10 23:58:29Z ccrick $
 */
public interface IconRetriever
{
    /**
     * Create an icon to represent a symbol in a symbol set.
     *
     * @param symbolId Identifier for the symbol. The format of this identifier depends on the symbology set.
     * @param params   Parameters that affect icon retrieval.
     *
     * @return A BufferedImage containing the requested icon, or null if the icon cannot be retrieved.
     */
    BufferedImage createIcon(String symbolId, AVList params);
}