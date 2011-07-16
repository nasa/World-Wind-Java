/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>TimeSpan</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLTimeSpan extends KMLAbstractTimePrimitive
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLTimeSpan(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getBegin()
    {
        return (String) this.getField("begin");
    }

    public String getEnd()
    {
        return (String) this.getField("end");
    }
}
