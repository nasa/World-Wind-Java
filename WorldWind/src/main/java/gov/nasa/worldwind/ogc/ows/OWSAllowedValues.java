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
 * Parses an OGC Web Service Common (OWS) AllowedValues element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsDomainType.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSAllowedValues extends OWSPossibleValues
{
    protected QName VALUE;

    protected List<String> values = new ArrayList<String>();
    protected List<OWSRange> ranges = new ArrayList<OWSRange>();

    public OWSAllowedValues(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        VALUE = new QName(this.getNamespaceURI(), "Value");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, VALUE))
        {
            this.addValue((String) o);
        }
        else if (o instanceof OWSRange)
        {
            this.addRange((OWSRange) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addValue(String value)
    {
        this.values.add(value);
    }

    protected void addRange(OWSRange range)
    {
        this.ranges.add(range);
    }

    @Override
    public boolean isAllowedValues()
    {
        return true;
    }

    @Override
    public OWSAllowedValues asAllowedValues()
    {
        return this;
    }

    public List<String> getValues()
    {
        return this.values;
    }

    public List<OWSRange> getRanges()
    {
        return this.ranges;
    }
}
