/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wss;

import gov.nasa.worldwind.ogc.wfs.WFSFeatureType;
import gov.nasa.worldwind.util.xml.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses a World Wind Web Shape Service (WSS) FeatureTypeType element and provides access to its contents.
 *
 * @author dcollins
 * @version $Id$
 */
public class WSSFeatureType extends WFSFeatureType
{
    protected QName NAMED_FEATURES;

    protected Set<String> namedFeatures = new HashSet<String>();

    public WSSFeatureType(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        super.initialize();

        NAMED_FEATURES = new QName(this.getNamespaceURI(), "NamedFeatures");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, NAMED_FEATURES))
        {
            this.addAllNamedFeatures((StringSetXMLEventParser) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addAllNamedFeatures(Iterable<String> iterable)
    {
        for (String s : iterable)
        {
            this.namedFeatures.add(s);
        }
    }

    public Set<String> getNamedFeatures()
    {
        return this.namedFeatures;
    }
}
