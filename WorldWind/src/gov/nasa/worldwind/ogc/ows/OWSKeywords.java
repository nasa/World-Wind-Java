/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Service Common (OWS) KeywordsType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/ows19115subset.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSKeywords extends AbstractXMLEventParser
{
    protected QName KEYWORD;

    protected List<OWSLanguageString> keywords = new ArrayList<OWSLanguageString>();

    public OWSKeywords(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        KEYWORD = new QName(this.getNamespaceURI(), "Keyword");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, KEYWORD))
        {
            this.addKeyword((OWSLanguageString) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addKeyword(OWSLanguageString keyword)
    {
        this.keywords.add(keyword);
    }

    public List<OWSLanguageString> getKeywords()
    {
        return this.keywords;
    }

    public OWSCodeType getType()
    {
        return (OWSCodeType) this.getField("Type");
    }
}
