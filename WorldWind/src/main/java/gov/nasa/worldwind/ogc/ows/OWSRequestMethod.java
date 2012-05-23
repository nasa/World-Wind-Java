/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.XMLEventParserContext;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Service Common (OWS) RequestMethodType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsOperationsMetadata.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSRequestMethod extends OWSOnlineResource
{
    protected QName CONSTRAINT;

    protected String requestType;
    protected List<OWSDomain> constraints = new ArrayList<OWSDomain>();

    public OWSRequestMethod(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        CONSTRAINT = new QName(this.getNamespaceURI(), "Constraint");
    }

    @Override
    public Object parse(XMLEventParserContext ctx, XMLEvent inputEvent, Object... args) throws XMLStreamException
    {
        this.requestType = inputEvent.asStartElement().getName().getLocalPart();

        return super.parse(ctx, inputEvent, args);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, CONSTRAINT))
        {
            this.addConstraint((OWSDomain) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void setRequestType(String type)
    {
        this.requestType = type;
    }

    protected void addConstraint(OWSDomain constraint)
    {
        this.constraints.add(constraint);
    }

    public String getRequestType()
    {
        return this.requestType;
    }

    public List<OWSDomain> getConstraints()
    {
        return this.constraints;
    }
}
