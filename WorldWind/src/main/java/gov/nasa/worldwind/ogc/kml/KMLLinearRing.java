/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

import gov.nasa.worldwind.geom.Position;

/**
 * Represents the KML <i>LinearRing</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLLinearRing extends KMLLineString
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLLinearRing(String namespaceURI)
    {
        super(namespaceURI);
    }
}
