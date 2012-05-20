/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * Parses an OGC Web Service Common (OWS) LanguageStringType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/ows19115subset.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSLanguageString extends AbstractXMLEventParser
{
    public OWSLanguageString(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getString()
    {
        return this.getCharacters();
    }

    public String getLanguage()
    {
        return (String) this.getField("lang");
    }
}
