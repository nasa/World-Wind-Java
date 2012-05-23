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
 * Parses an OGC Web Service Common (OWS) DescriptionType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsDataIdentification.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSDescription extends AbstractXMLEventParser
{
    protected QName TITLE;
    protected QName ABSTRACT;

    protected List<OWSLanguageString> titles = new ArrayList<OWSLanguageString>();
    protected List<OWSLanguageString> abstracts = new ArrayList<OWSLanguageString>();
    protected List<OWSKeywords> keywordLists = new ArrayList<OWSKeywords>();

    public OWSDescription(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        TITLE = new QName(this.getNamespaceURI(), "Title");
        ABSTRACT = new QName(this.getNamespaceURI(), "Abstract");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, TITLE))
        {
            this.addTitle((OWSLanguageString) o);
        }
        else if (ctx.isStartElement(event, ABSTRACT))
        {
            this.addAbstract((OWSLanguageString) o);
        }
        else if (o instanceof OWSKeywords)
        {
            this.addKeywordList((OWSKeywords) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addTitle(OWSLanguageString title)
    {
        this.titles.add(title);
    }

    protected void addAbstract(OWSLanguageString abstractText)
    {
        this.abstracts.add(abstractText);
    }

    protected void addKeywordList(OWSKeywords keywords)
    {
        this.keywordLists.add(keywords);
    }

    public List<OWSLanguageString> getTitles()
    {
        return this.titles;
    }

    public List<OWSLanguageString> getAbstracts()
    {
        return this.abstracts;
    }

    public List<OWSKeywords> getKeywordLists()
    {
        return this.keywordLists;
    }
}
