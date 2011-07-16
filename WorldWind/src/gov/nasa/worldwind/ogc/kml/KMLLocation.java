/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>Location</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLLocation extends KMLAbstractObject
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLLocation(String namespaceURI)
    {
        super(namespaceURI);
    }

    public Double getLongitude()
    {
        return (Double) this.getField("longitude");
    }

    public Double getLatitude()
    {
        return (Double) this.getField("latitude");
    }

    public Double getAltitude()
    {
        return (Double) this.getField("altitude");
    }
}
