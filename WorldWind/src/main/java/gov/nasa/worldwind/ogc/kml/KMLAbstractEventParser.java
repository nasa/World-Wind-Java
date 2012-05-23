/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * Implements {@link AbstractXMLEventParser} for KML elements. Provides the interface and implementation for retrieving
 * support files referred to by elements in the KML document.
 *
 * @author tag
 * @version $Id$
 */
public abstract class KMLAbstractEventParser extends AbstractXMLEventParser
{
    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    protected KMLAbstractEventParser(String namespaceURI)
    {
        super(namespaceURI);
    }
}
