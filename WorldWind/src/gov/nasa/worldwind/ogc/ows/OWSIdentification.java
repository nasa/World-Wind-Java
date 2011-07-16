/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.XMLEventParserContext;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Service Common (OWS) IdentificationType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsDataIdentification.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSIdentification extends OWSBasicIdentification
{
    protected QName OUTPUT_FORMAT;
    protected QName AVAILABLE_CRS;

    protected List<OWSBoundingBox> boundingBoxes = new ArrayList<OWSBoundingBox>();
    protected Set<String> outputFormats = new HashSet<String>();
    protected Set<String> availableCRS = new HashSet<String>();

    public OWSIdentification(String namespaceURI)
    {
        super(namespaceURI);
    }

    protected void initialize()
    {
        super.initialize();

        OUTPUT_FORMAT = new QName(this.getNamespaceURI(), "OutputFormat");
        AVAILABLE_CRS = new QName(this.getNamespaceURI(), "AvailableCRS");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, OUTPUT_FORMAT))
        {
            this.addOutputFormat((String) o);
        }
        else if (ctx.isStartElement(event, AVAILABLE_CRS))
        {
            this.addAvailableCRS((String) o);
        }
        else if (o instanceof OWSBoundingBox)
        {
            this.addBoundingBox((OWSBoundingBox) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addBoundingBox(OWSBoundingBox boundingBox)
    {
        this.boundingBoxes.add(boundingBox);
    }

    protected void addOutputFormat(String outputFormat)
    {
        this.outputFormats.add(outputFormat);
    }

    protected void addAvailableCRS(String crs)
    {
        this.availableCRS.add(crs);
    }

    public List<OWSBoundingBox> getBoundingBoxes()
    {
        return this.boundingBoxes;
    }

    public Set<String> getOutputFormats()
    {
        return this.outputFormats;
    }

    public Set<String> getAvailableCRS()
    {
        return this.availableCRS;
    }
}
