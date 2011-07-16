/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.xml.xal;

import gov.nasa.worldwind.util.xml.XMLEventParserContext;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import java.util.*;

/**
 * @author tag
 * @version $Id$
 */
public class XALAddressLines extends XALAbstractObject
{
    protected List<XALAddressLine> addressLines;

    public XALAddressLines(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof XALAddressLine)
            this.addAddressLine((XALAddressLine) o);
    }

    public List<XALAddressLine> getAddressLines()
    {
        return this.addressLines;
    }

    protected void addAddressLine(XALAddressLine o)
    {
        if (this.addressLines == null)
            this.addressLines = new ArrayList<XALAddressLine>();

        this.addressLines.add(o);
    }
}
