/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.util.xml.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Parses an OGC Web Feature Service (WFS) Value element and provides access to its contents. See
 * http://schemas.opengis.net/wfs/2.0/wfs.xsd. The WFS Value element is a placeholder for any element. This this default
 * parser reads the Value element as simple string content and returns the resultant string..
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSValueParser extends AbstractXMLEventParser
{
    public WFSValueParser(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    public Object parse(XMLEventParserContext ctx, XMLEvent inputEvent, Object... args) throws XMLStreamException
    {
        return ctx.getStringParser().parse(ctx, inputEvent, args);
    }
}
