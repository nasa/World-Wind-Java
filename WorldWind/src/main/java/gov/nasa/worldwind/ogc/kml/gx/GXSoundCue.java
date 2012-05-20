/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml.gx;

/**
 * @author tag
 * @version $Id$
 */
public class GXSoundCue extends GXAbstractTourPrimitive
{
    public GXSoundCue(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getHref()
    {
        return (String) this.getField("href");
    }
}
