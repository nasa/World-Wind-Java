/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.ogc.ows.OWSMetadata;
import gov.nasa.worldwind.util.xml.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Feature Service (WFS) ElementType element and provides access to its contents. See
 * http://schemas.opengis.net/wfs/2.0/wfs.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSElement extends AbstractXMLEventParser
{
    protected List<Object> valueList = new ArrayList<Object>();

    public WFSElement(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof WFSValueListParser)
        {
            this.addAllValues((WFSValueListParser) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addAllValues(Iterable<?> iterable)
    {
        for (Object o : iterable)
        {
            this.valueList.add(o);
        }
    }

    public OWSMetadata getMetadata()
    {
        return (OWSMetadata) this.getField("Metadata");
    }

    public List<?> getValueList()
    {
        return this.valueList;
    }

    public String getName()
    {
        return (String) this.getField("name");
    }

    public String getType()
    {
        return (String) this.getField("type");
    }
}
