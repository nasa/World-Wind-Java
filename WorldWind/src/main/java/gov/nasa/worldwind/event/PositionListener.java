/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.event;

import java.util.EventListener;

/**
 * @author tag
 * @version $Id$
 */
public interface PositionListener extends EventListener
{
    public void moved(PositionEvent event);
}
