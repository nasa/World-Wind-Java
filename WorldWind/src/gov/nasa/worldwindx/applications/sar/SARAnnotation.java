/*
Copyright (C) 2001, 2007 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwindx.applications.sar;

import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.geom.Position;

/**
 * @author tag
 * @version $Id$
 */
public class SARAnnotation extends GlobeAnnotation
{
    private SARTrack owner;
    private String id;

    public SARAnnotation(String text, Position position)
    {
        super(text, position);
    }

    public SARTrack getOwner()
    {
        return this.owner;
    }

    public void setOwner(SARTrack owner)
    {
        this.owner = owner;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
