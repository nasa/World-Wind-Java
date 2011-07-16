/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>TimeStamp</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLTimeStamp extends KMLAbstractTimePrimitive
{
    protected String when;

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLTimeStamp(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getWhen()
    {
        return (String) this.getField("when");
    }
}
