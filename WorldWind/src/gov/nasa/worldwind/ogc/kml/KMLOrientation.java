/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>Orientation</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLOrientation extends KMLAbstractObject
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLOrientation(String namespaceURI)
    {
        super(namespaceURI);
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
}
