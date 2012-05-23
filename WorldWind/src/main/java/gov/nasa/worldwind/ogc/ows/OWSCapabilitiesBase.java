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
 * Parses an OGC Web Service Common (OWS) CapabilitiesBaseType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsGetCapabilities.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSCapabilitiesBase extends AbstractXMLEventParser
{
    protected QName LANGUAGES;

    protected Set<String> languages = new HashSet<String>();

    public OWSCapabilitiesBase(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        LANGUAGES = new QName(this.getNamespaceURI(), "Languages");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, LANGUAGES))
        {
            this.addAllLanguages((StringSetXMLEventParser) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addAllLanguages(Iterable<String> iterable)
    {
        for (String s : iterable)
        {
            this.languages.add(s);
        }
    }

    public OWSServiceIdentification getServiceIdentification()
    {
        return (OWSServiceIdentification) this.getField("ServiceIdentification");
    }

    public OWSServiceProvider getServiceProvider()
    {
        return (OWSServiceProvider) this.getField("ServiceProvider");
    }

    public OWSOperationsMetadata getOperationsMetadata()
    {
        return (OWSOperationsMetadata) this.getField("OperationsMetadata");
    }

    /**
     * Returns the set of languages this service is able to fully support. If one of the listed languages is requested
     * using the "AcceptsLanguages" parameter in future requests, all text strings contained in the response are
     * guaranteed to be in that language.
     * <p/>
     * The Languages element is included in services supporting OWS version <code>2.0.0</code>.
     *
     * @return the set of languages supported by this service.
     */
    public Set<String> getLanguages()
    {
        return this.languages;
    }

    public String getVersion()
    {
        return (String) this.getField("version");
    }

    public String getUpdateSequence()
    {
        return (String) this.getField("updateSequence");
    }
}
