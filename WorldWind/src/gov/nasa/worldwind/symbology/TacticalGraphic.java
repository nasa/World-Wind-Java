/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.render.Renderable;

/**
 * @author pabercrombie
 * @version $Id$
 */
public interface TacticalGraphic extends Renderable, AVList
{
    /**
     * Indicates a string identifier for this graphic. The format of the identifier depends on the symbol set to which
     * the graphic belongs.
     *
     * @return An identifier for this graphic.
     */
    String getIdentifier();

    /**
     * Indicates a string of descriptive text for this graphic.
     *
     * @return Descriptive text for this graphic.
     */
    String getText();

    /**
     * Specifies a string of descriptive text for this graphic.
     *
     * @param text Descriptive text for this graphic.
     */
    void setText(String text);
}
