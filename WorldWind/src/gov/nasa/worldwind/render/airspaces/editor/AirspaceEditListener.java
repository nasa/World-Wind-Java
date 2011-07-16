/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.render.airspaces.editor;

import java.util.EventListener;

/**
 * @author dcollins
 * @version $Id$
 */
public interface AirspaceEditListener extends EventListener
{
    void airspaceMoved(AirspaceEditEvent e);

    void airspaceResized(AirspaceEditEvent e);

    void controlPointAdded(AirspaceEditEvent e);

    void controlPointRemoved(AirspaceEditEvent e);

    void controlPointChanged(AirspaceEditEvent e);
}
