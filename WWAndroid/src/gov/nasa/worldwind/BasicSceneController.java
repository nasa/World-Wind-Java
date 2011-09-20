/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind;

import gov.nasa.worldwind.render.DrawContext;

/**
 * @author dcollins
 * @version $Id$
 */
public class BasicSceneController extends AbstractSceneController
{
    public BasicSceneController()
    {
    }

    /** {@inheritDoc} */
    @Override
    protected void doDrawFrame(DrawContext dc)
    {
        this.initializeFrame(dc);
        try
        {
            this.applyView(dc);
            this.createTerrain(dc);
            this.clearFrame(dc);
            this.draw(dc);
        }
        finally
        {
            this.finalizeFrame(dc);
        }
    }
}
