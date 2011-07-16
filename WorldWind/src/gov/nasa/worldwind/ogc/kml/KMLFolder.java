/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

/**
 * Represents the KML <i>Folder</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLFolder extends KMLAbstractContainer
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLFolder(String namespaceURI)
    {
        super(namespaceURI);
    }
}
