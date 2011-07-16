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
 * Represents the KML <i>ListStyle</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id$
 */
public class KMLListStyle extends KMLAbstractSubStyle
{
    protected List<KMLItemIcon> itemIcons = new ArrayList<KMLItemIcon>();

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLListStyle(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof KMLItemIcon)
            this.addItemIcon((KMLItemIcon) o);
        else
            super.doAddEventContent(o, ctx, event, args);
    }

    public String getListItemType()
    {
        return (String) this.getField("listItemType");
    }

    public String getBgColor()
    {
        return (String) this.getField("bgColor");
    }

    protected void addItemIcon(KMLItemIcon o)
    {
        this.itemIcons.add(o);
    }

    public List<KMLItemIcon> getItemIcons()
    {
        return this.itemIcons;
    }

    public Integer getMaxSnippetLines()
    {
        return (Integer) this.getField("maxSnippetLines");
    }
}
