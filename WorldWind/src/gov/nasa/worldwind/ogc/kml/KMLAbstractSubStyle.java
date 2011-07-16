/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>SubStyle</i> element.
 *
 * @author tag
 * @version $Id$
 */
public abstract class KMLAbstractSubStyle extends KMLAbstractObject
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    protected KMLAbstractSubStyle(String namespaceURI)
    {
        super(namespaceURI);
    }
}
