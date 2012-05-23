/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>Scale</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLScale extends KMLAbstractObject
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLScale(String namespaceURI)
    {
        super(namespaceURI);
    }

    public Double getX()
    {
        return (Double) this.getField("x");
    }

    public Double getY()
    {
        return (Double) this.getField("y");
    }

    public Double getZ()
    {
        return (Double) this.getField("z");
    }
}
