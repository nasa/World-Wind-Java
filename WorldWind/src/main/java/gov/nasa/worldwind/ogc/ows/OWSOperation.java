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
 * Parses an OGC Web Service Common (OWS) Operation element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsOperationsMetadata.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSOperation extends AbstractXMLEventParser
{
    protected QName PARAMETER;
    protected QName CONSTRAINT;

    protected List<OWSDCPType> dcpTypes = new ArrayList<OWSDCPType>();
    protected List<OWSDomain> parameters = new ArrayList<OWSDomain>();
    protected List<OWSDomain> constraints = new ArrayList<OWSDomain>();
    protected List<OWSMetadata> metadata = new ArrayList<OWSMetadata>();

    public OWSOperation(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        PARAMETER = new QName(this.getNamespaceURI(), "Parameter");
        CONSTRAINT = new QName(this.getNamespaceURI(), "Constraint");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, PARAMETER))
        {
            this.addParameter((OWSDomain) o);
        }
        else if (ctx.isStartElement(event, CONSTRAINT))
        {
            this.addConstraint((OWSDomain) o);
        }
        else if (o instanceof OWSDCPType)
        {
            this.addDCPType((OWSDCPType) o);
        }
        else if (o instanceof OWSMetadata)
        {
            this.addMetadata((OWSMetadata) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addDCPType(OWSDCPType dcpType)
    {
        this.dcpTypes.add(dcpType);
    }

    protected void addParameter(OWSDomain parameter)
    {
        this.parameters.add(parameter);
    }

    protected void addConstraint(OWSDomain constraint)
    {
        this.constraints.add(constraint);
    }

    protected void addMetadata(OWSMetadata metadata)
    {
        this.metadata.add(metadata);
    }

    public List<OWSDCPType> getDCPTypes()
    {
        return this.dcpTypes;
    }

    public List<OWSDomain> getParameters()
    {
        return this.parameters;
    }

    public List<OWSDomain> getConstraints()
    {
        return this.constraints;
    }

    public List<OWSMetadata> getMetadata()
    {
        return this.metadata;
    }

    public String getName()
    {
        return (String) this.getField("name");
    }
}
