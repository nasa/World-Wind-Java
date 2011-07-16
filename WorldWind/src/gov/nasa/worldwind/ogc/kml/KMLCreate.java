/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

import gov.nasa.worldwind.util.xml.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Represents the KML <i>Create</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLCreate extends AbstractXMLEventParser
{
    protected List<KMLAbstractContainer> containers = new ArrayList<KMLAbstractContainer>();

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLCreate(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof KMLAbstractContainer)
            this.addContainer((KMLAbstractContainer) o);
        else
            super.doAddEventContent(o, ctx, event, args);
    }

    protected void addContainer(KMLAbstractContainer o)
    {
        this.containers.add(o);
    }

    public List<KMLAbstractContainer> getContainers()
    {
        return this.containers;
    }
}
