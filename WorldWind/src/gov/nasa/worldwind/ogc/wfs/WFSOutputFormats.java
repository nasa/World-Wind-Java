/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.util.xml.StringSetXMLEventParser;

import javax.xml.namespace.QName;

/**
 * Parses the OGC Web Feature Service (WFS) OutputFormats and Format elements and provides access to their contents as a
 * set of format strings. See http://schemas.opengis.net/wfs/2.0/wfs.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSOutputFormats extends StringSetXMLEventParser
{
    public WFSOutputFormats(String namespaceURI)
    {
        super(namespaceURI, new QName(namespaceURI, "Format"));
    }
}
