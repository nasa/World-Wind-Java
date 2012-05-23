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
 * Parses an OGC Web Service Common (OWS) OperationsMetadata element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsOperationsMetadata.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSOperationsMetadata extends AbstractXMLEventParser
{
    protected QName PARAMETER;
    protected QName CONSTRAINT;

    protected List<OWSOperation> operations = new ArrayList<OWSOperation>();
    protected List<OWSDomain> parameters = new ArrayList<OWSDomain>();
    protected List<OWSDomain> constraints = new ArrayList<OWSDomain>();

    public OWSOperationsMetadata(String namespaceURI)
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
        else if (o instanceof OWSOperation)
        {
            this.addOperation((OWSOperation) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addOperation(OWSOperation operation)
    {
        this.operations.add(operation);
    }

    protected void addParameter(OWSDomain parameter)
    {
        this.parameters.add(parameter);
    }

    protected void addConstraint(OWSDomain constraint)
    {
        this.constraints.add(constraint);
    }

    public List<OWSOperation> getOperations()
    {
        return this.operations;
    }

    public List<OWSDomain> getParameters()
    {
        return this.parameters;
    }

    public List<OWSDomain> getConstraints()
    {
        return this.constraints;
    }

    public Object getExtendedCapabilities()
    {
        return this.getField("ExtendedCapabilities");
    }
}
