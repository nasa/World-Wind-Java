/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.util.xml.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Feature Service (WFS) ValueListType element and provides access to its contents. See
 * http://schemas.opengis.net/wfs/2.0/wfs.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSValueListParser extends AbstractXMLEventParser implements Iterable<Object>
{
    protected QName VALUE;

    protected List<Object> values = new ArrayList<Object>();

    public WFSValueListParser(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        VALUE = new QName(this.getNamespaceURI(), "Value");
    }

    @Override
    public Object parse(XMLEventParserContext ctx, XMLEvent inputEvent, Object... args) throws XMLStreamException
    {
        this.values.clear();

        return super.parse(ctx, inputEvent, args);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, VALUE))
        {
            this.addValue(o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addValue(Object o)
    {
        this.values.add(o);
    }

    public List<Object> getValues()
    {
        return this.values;
    }

    public Iterator<Object> iterator()
    {
        return this.values.iterator();
    }
}