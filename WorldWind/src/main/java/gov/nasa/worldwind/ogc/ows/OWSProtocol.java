/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Service Common (OWS) HTTP element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsOperationsMetadata.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSProtocol extends AbstractXMLEventParser
{
    protected String protocolType;
    protected List<OWSRequestMethod> requestMethods = new ArrayList<OWSRequestMethod>();

    public OWSProtocol(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    public Object parse(XMLEventParserContext ctx, XMLEvent inputEvent, Object... args) throws XMLStreamException
    {
        this.protocolType = inputEvent.asStartElement().getName().getLocalPart();

        return super.parse(ctx, inputEvent, args);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof OWSRequestMethod)
        {
            this.addRequestMethod((OWSRequestMethod) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void setProtocolType(String type)
    {
        this.protocolType = type;
    }

    protected void addRequestMethod(OWSRequestMethod requestMethod)
    {
        this.requestMethods.add(requestMethod);
    }

    public String getProtocolType()
    {
        return this.protocolType;
    }

    public List<OWSRequestMethod> getRequestMethods()
    {
        return this.requestMethods;
    }
}
