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
 * Parses an OGC Web Feature Service (WFS) FeatureTypeListType element and provides access to its contents. See
 * http://schemas.opengis.net/wfs/2.0/wfs.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSFeatureTypeListParser extends AbstractXMLEventParser implements Iterable<WFSFeatureType>
{
    protected List<WFSFeatureType> featureTypes = new ArrayList<WFSFeatureType>();

    public WFSFeatureTypeListParser(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    public Object parse(XMLEventParserContext ctx, XMLEvent inputEvent, Object... args) throws XMLStreamException
    {
        this.featureTypes.clear();

        return super.parse(ctx, inputEvent, args);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (o instanceof WFSFeatureType)
        {
            this.addFeatureType((WFSFeatureType) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addFeatureType(WFSFeatureType featureType)
    {
        this.featureTypes.add(featureType);
    }

    public List<WFSFeatureType> getFeatureTypes()
    {
        return this.featureTypes;
    }

    public Iterator<WFSFeatureType> iterator()
    {
        return this.featureTypes.iterator();
    }
}
