/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * Parse a string from an XML event.
 *
 * @author tag
 * @version $Id$
 */
public class StringXMLEventParser extends AbstractXMLEventParser
{
    public StringXMLEventParser()
    {
    }

    public StringXMLEventParser(String namespaceUri)
    {
        super(namespaceUri);
    }

    public Object parse(XMLEventParserContext ctx, XMLEvent stringEvent, Object... args) throws XMLStreamException
    {
        String s = this.parseCharacterContent(ctx, stringEvent, args);
        return s != null ? s.trim() : null;
    }

    public String parseString(XMLEventParserContext ctx, XMLEvent stringEvent, Object... args) throws XMLStreamException
    {
        return (String) this.parse(ctx, stringEvent, args);
    }
}
