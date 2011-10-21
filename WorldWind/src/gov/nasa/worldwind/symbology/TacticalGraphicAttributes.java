/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.symbology;

import gov.nasa.worldwind.render.*;

import java.awt.*;

/**
 * @author pabercrombie
 * @version $Id$
 */
public interface TacticalGraphicAttributes extends ShapeAttributes
{
    Offset getLabelOffset();

    void setLabelOffset(Offset offset);

    Font getTextModifierFont();

    void setTextModifierFont(Font font);

    Color getTextModifierColor();

    void setTextModifierColor(Color color);
}
