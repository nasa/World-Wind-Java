/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.util.xml.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Feature Service (WFS) ExtendedDescriptionType element and provides access to its contents. See
 * http://schemas.opengis.net/wfs/2.0/wfs.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSExtendedDescription extends AbstractXMLEventParser
{
    protected List<WFSElement> elements = new ArrayList<WFSElement>();

    public WFSExtendedDescription(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof WFSElement)
        {
            this.addElement((WFSElement) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addElement(WFSElement element)
    {
        this.elements.add(element);
    }

    public List<WFSElement> getElements()
    {
        return this.elements;
    }
}
