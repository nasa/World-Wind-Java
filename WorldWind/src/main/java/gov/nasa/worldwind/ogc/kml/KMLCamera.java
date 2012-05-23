/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>Camera</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLCamera extends KMLAbstractView
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLCamera(String namespaceURI)
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

    public Double getHeading()
    {
        return (Double) this.getField("heading");
    }

    public Double getTilt()
    {
        return (Double) this.getField("tilt");
    }

    public Double getRoll()
    {
        return (Double) this.getField("roll");
    }

    public String getAltitudeMode()
    {
        return (String) this.getField("altitudeMode");
    }
}
