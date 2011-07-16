/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>IconStyle</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLIconStyle extends KMLAbstractColorStyle
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLIconStyle(String namespaceURI)
    {
        super(namespaceURI);
    }

    public Double getScale()
    {
        return (Double) this.getField("scale");
    }

    public Double getHeading()
    {
        return (Double) this.getField("heading");
    }

    public KMLVec2 getHotSpot()
    {
        return (KMLVec2) this.getField("hotSpot");
    }

    public KMLIcon getIcon()
    {
        return (KMLIcon) this.getField("Icon");
    }
}