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
 * Parses an OGC Web Service Common (OWS) UnNamedDomainType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsDomainType.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSUnNamedDomain extends AbstractXMLEventParser
{
    protected OWSPossibleValues possibleValues;
    protected List<OWSMetadata> metadata = new ArrayList<OWSMetadata>();

    public OWSUnNamedDomain(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof OWSPossibleValues)
        {
            this.setPossibleValues((OWSPossibleValues) o);
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

    protected void setPossibleValues(OWSPossibleValues possibleValues)
    {
        this.possibleValues = possibleValues;
    }

    protected void addMetadata(OWSMetadata metadata)
    {
        this.metadata.add(metadata);
    }

    public OWSPossibleValues getPossibleValues()
    {
        return this.possibleValues;
    }

    public String getDefaultValue()
    {
        return (String) this.getField("DefaultValue");
    }

    public OWSDomainMetadata getMeaning()
    {
        return (OWSDomainMetadata) this.getField("Meaning");
    }

    public OWSDomainMetadata getDataType()
    {
        return (OWSDomainMetadata) this.getField("DataType");
    }

    public OWSDomainMetadata getValuesUnit()
    {
        return (OWSDomainMetadata) this.getField("ValuesUnit");
    }

    public List<OWSMetadata> getMetadata()
    {
        return this.metadata;
    }
}
