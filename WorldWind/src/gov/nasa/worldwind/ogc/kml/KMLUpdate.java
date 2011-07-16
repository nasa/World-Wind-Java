/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.ogc.kml;

import gov.nasa.worldwind.util.xml.XMLEventParserContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Represents the KML <i>Update</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLUpdate extends KMLAbstractObject
{
    protected List<KMLChange> changes;
    protected List<KMLCreate> creates;
    protected List<KMLDelete> deletes;

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLUpdate(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof KMLChange)
            this.addChange((KMLChange) o);
        else if (o instanceof KMLCreate)
            this.addCreate((KMLCreate) o);
        else if (o instanceof KMLDelete)
            this.addDelete((KMLDelete) o);
        else
            super.doAddEventContent(o, ctx, event, args);
    }

    public String getTargetHref()
    {
        return (String) this.getField("targetHref");
    }

    protected void addChange(KMLChange o)
    {
        if (this.changes == null)
            this.changes = new ArrayList<KMLChange>();

        this.changes.add(o);
    }

    protected void addCreate(KMLCreate o)
    {
        if (this.creates == null)
            this.creates = new ArrayList<KMLCreate>();

        this.creates.add(o);
    }

    protected void addDelete(KMLDelete o)
    {
        if (this.deletes == null)
            this.deletes = new ArrayList<KMLDelete>();

        this.deletes.add(o);
    }
}
