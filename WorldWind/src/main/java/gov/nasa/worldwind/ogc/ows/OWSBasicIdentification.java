/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.XMLEventParserContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Service Common (OWS) BasicIdentificationType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsDataIdentification.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSBasicIdentification extends OWSDescription
{
    protected List<OWSMetadata> metadata = new ArrayList<OWSMetadata>();

    public OWSBasicIdentification(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof OWSMetadata)
        {
            this.addMetadata((OWSMetadata) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addMetadata(OWSMetadata metadata)
    {
        this.metadata.add(metadata);
    }

    public OWSCodeType getIdentifier()
    {
        return (OWSCodeType) this.getField("Identifier");
    }

    public List<OWSMetadata> getMetadata()
    {
        return this.metadata;
    }
}
