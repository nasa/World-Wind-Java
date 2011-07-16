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
 * Parses an OGC Web Service Common (OWS) TelephoneType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/ows19115subset.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSTelephone extends AbstractXMLEventParser
{
    protected QName VOICE;
    protected QName FACSIMILE;

    protected List<String> voiceNumbers = new ArrayList<String>();
    protected List<String> facsimileNumbers = new ArrayList<String>();

    public OWSTelephone(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        VOICE = new QName(this.getNamespaceURI(), "Voice");
        FACSIMILE = new QName(this.getNamespaceURI(), "Facsimile");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, VOICE))
        {
            this.addVoiceNumber((String) o);
        }
        else if (ctx.isStartElement(event, FACSIMILE))
        {
            this.addFacsimileNumber((String) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addVoiceNumber(String voiceNumber)
    {
        this.voiceNumbers.add(voiceNumber);
    }

    protected void addFacsimileNumber(String facsimileNumber)
    {
        this.facsimileNumbers.add(facsimileNumber);
    }

    public List<String> getVoiceNumbers()
    {
        return this.voiceNumbers;
    }

    public List<String> getFacsimileNumbers()
    {
        return this.facsimileNumbers;
    }
}
