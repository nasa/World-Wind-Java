/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml.gx;

import gov.nasa.worldwind.ogc.kml.KMLAbstractObject;
import gov.nasa.worldwind.util.xml.XMLEventParserContext;

import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.XMLStreamException;
import java.util.*;

/**
 * @author tag
 * @version $Id$
 */
public class GXPlaylist extends KMLAbstractObject
{
    protected List<GXAbstractTourPrimitive> tourPrimitives = new ArrayList<GXAbstractTourPrimitive>();

    public GXPlaylist(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof GXAbstractTourPrimitive)
            this.addTourPrimitive((GXAbstractTourPrimitive) o);
        else
            super.doAddEventContent(o, ctx, event, args);
    }

    protected void addTourPrimitive(GXAbstractTourPrimitive o)
    {
        this.tourPrimitives.add(o);
    }

    public List<GXAbstractTourPrimitive> getTourPrimitives()
    {
        return this.tourPrimitives;
    }
}
