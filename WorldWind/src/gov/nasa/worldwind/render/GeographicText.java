/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.geom.Position;

import java.awt.*;

/**
 * @author dcollins
 * @version $Id$
 */
public interface GeographicText
{
    CharSequence getText();

    void setText(CharSequence text);

    Position getPosition();

    void setPosition(Position position);

    Font getFont();

    void setFont(Font font);

    Color getColor();

    void setColor(Color color);

    Color getBackgroundColor();

    void setBackgroundColor(Color background);

    boolean isVisible();

    void setVisible(boolean visible);

    void setPriority(double d);

    double getPriority();
}
