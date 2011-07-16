/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>LatLonBox</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public abstract class KMLAbstractLatLonBoxType extends KMLAbstractObject
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    protected KMLAbstractLatLonBoxType(String namespaceURI)
    {
        super(namespaceURI);
    }

    public Double getNorth()
    {
        return (Double) this.getField("north");
    }

    public Double getSouth()
    {
        return (Double) this.getField("south");
    }

    public Double getEast()
    {
        return (Double) this.getField("east");
    }

    public Double getWest()
    {
        return (Double) this.getField("west");
    }
}
