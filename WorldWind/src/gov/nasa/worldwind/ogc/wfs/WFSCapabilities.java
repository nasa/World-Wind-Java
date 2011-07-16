/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.ogc.ows.OWSCapabilitiesBase;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.util.xml.XMLEventParserContext;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Feature Service (WFS) WFS_CapabilitiesType element and provides access to its contents. See
 * http://schemas.opengis.net/wfs/2.0/wfs.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSCapabilities extends OWSCapabilitiesBase
{
    // TODO: parse WSDL element.
    // TODO: parse Filter_Capabilities element.

    protected List<WFSFeatureType> featureTypes = new ArrayList<WFSFeatureType>();
    /**
     * The event reader used to parse the document's XML. This is <code>null<code> if the exception report is not
     * created from an XML document source.
     */
    protected XMLEventReader eventReader;
    /**
     * The parser context for the document. This is <code>null<code> if the exception report is not created from an XML
     * document source.
     */
    protected XMLEventParserContext parserContext;

    public WFSCapabilities()
    {
        super(WFSConstants.WFS_2dot0_NAMESPACE);
    }

    public WFSCapabilities(Object docSource)
    {
        super(WFSConstants.WFS_2dot0_NAMESPACE);

        if (docSource == null)
        {
            String message = Logging.getMessage("nullValue.DocumentSourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.eventReader = this.createReader(docSource);
        if (this.eventReader == null)
            throw new WWRuntimeException(Logging.getMessage("XML.UnableToOpenDocument", docSource));

        this.initializeParser();
    }

    /**
     * Called from the constructor that takes an XML document source, just before it returns. If overriding this method
     * be sure to call <code>super.initialize()</code>.
     *
     * @see #WFSCapabilities(Object)
     */
    protected void initializeParser()
    {
        this.parserContext = this.createParserContext(this.eventReader);
    }

    /**
     * Creates the exception report's XML event reader. Called from the constructor that takes an XML document source.
     *
     * @param docSource the document source to create a reader for. The type can be any of those supported by {@link
     *                  gov.nasa.worldwind.util.WWXML#openEventReader(Object)}.
     *
     * @return a new event reader, or null if the source type cannot be determined.
     *
     * @see #WFSCapabilities(Object)
     */
    protected XMLEventReader createReader(Object docSource)
    {
        return WWXML.openEventReader(docSource);
    }

    /**
     * Called during <code>{@link #initializeParser()}</code> to create the parser context.
     *
     * @param reader the reader to associate with the parser context.
     *
     * @return a new parser context.
     */
    protected XMLEventParserContext createParserContext(XMLEventReader reader)
    {
        return new WFSParserContext(reader, this.getNamespaceURI());
    }

    /**
     * Indicates the XML parser context created during <code>{@link #initializeParser()}</code>. Returns
     * <code>null</code> if the exception report was not created form an XML document source.
     *
     * @return the XML parser context, or <code>null</code> the exception report does not have a parser context.
     */
    protected XMLEventParserContext getParserContext()
    {
        return this.parserContext;
    }

    /**
     * Starts document parsing. This method initiates parsing of the WFS capabilities document and returns when the full
     * exception report document has been parsed. If this capabilities document was not created from an XML document
     * source, this does nothing and returns <code>null</code>.
     *
     * @param args optional arguments to pass to parsers of sub-elements.
     *
     * @return <code>this</code> if parsing is successful, otherwise <code>null</code>.
     *
     * @throws XMLStreamException if an exception occurs while attempting to read the XML document event stream.
     * @see #WFSCapabilities() (Object)
     */
    public WFSCapabilities parse(Object... args) throws XMLStreamException
    {
        XMLEventParserContext ctx = this.getParserContext();
        if (ctx == null)
            return null;

        QName rootElementName = new QName(this.getNamespaceURI(), "WFS_Capabilities");

        for (XMLEvent event = ctx.nextEvent(); ctx.hasNext(); event = ctx.nextEvent())
        {
            if (event == null)
                continue;

            if (ctx.isStartElement(event, rootElementName))
            {
                super.parse(ctx, event, args);
                return this;
            }
        }

        return null;
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof WFSFeatureTypeListParser)
        {
            this.addAllFeatureTypes((WFSFeatureTypeListParser) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addAllFeatureTypes(Iterable<WFSFeatureType> iterable)
    {
        for (WFSFeatureType featureType : iterable)
        {
            this.featureTypes.add(featureType);
        }
    }

    public List<WFSFeatureType> getFeatureTypes()
    {
        return this.featureTypes;
    }
}
