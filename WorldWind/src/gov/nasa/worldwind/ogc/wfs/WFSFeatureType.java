/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.ogc.ows.*;
import gov.nasa.worldwind.util.xml.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Feature Service (WFS) FeatureTypeType element and provides access to its contents. See
 * http://schemas.opengis.net/wfs/2.0/wfs.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSFeatureType extends AbstractXMLEventParser
{
    protected QName TITLE;
    protected QName ABSTRACT;
    protected QName OUTPUT_FORMATS;
    protected QName DEFAULT_CRS;
    protected QName OTHER_CRS;
    protected QName NO_CRS;

    protected List<OWSLanguageString> titles = new ArrayList<OWSLanguageString>();
    protected List<OWSLanguageString> abstracts = new ArrayList<OWSLanguageString>();
    protected List<OWSKeywords> keywordLists = new ArrayList<OWSKeywords>();
    protected String defaultCRS;
    protected Set<String> otherCRS = new HashSet<String>();
    protected boolean noCRS; // False unless the NoCRS element is present.
    protected Set<String> outputFormats = new HashSet<String>();
    protected List<OWSBoundingBox> boundingBoxes = new ArrayList<OWSBoundingBox>();
    protected List<WFSMetadataURL> metadataURLs = new ArrayList<WFSMetadataURL>();

    public WFSFeatureType(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        TITLE = new QName(this.getNamespaceURI(), "Title");
        ABSTRACT = new QName(this.getNamespaceURI(), "Abstract");
        OUTPUT_FORMATS = new QName(this.getNamespaceURI(), "OutputFormats");
        DEFAULT_CRS = new QName(this.getNamespaceURI(), "DefaultCRS");
        OTHER_CRS = new QName(this.getNamespaceURI(), "OtherCRS");
        NO_CRS = new QName(this.getNamespaceURI(), "NoCRS");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, TITLE))
        {
            this.addTitle((OWSLanguageString) o);
        }
        else if (ctx.isStartElement(event, ABSTRACT))
        {
            this.addAbstract((OWSLanguageString) o);
        }
        else if (ctx.isStartElement(event, OUTPUT_FORMATS))
        {
            this.addAllOutputFormats((StringSetXMLEventParser) o);
        }
        else if (ctx.isStartElement(event, DEFAULT_CRS))
        {
            this.setDefaultCRS((String) o);
        }
        else if (ctx.isStartElement(event, OTHER_CRS))
        {
            this.addOtherCRS((String) o);
        }
        else if (ctx.isStartElement(event, NO_CRS))
        {
            this.setNoCRS(true);
        }
        else if (o instanceof OWSKeywords)
        {
            this.addKeywordList((OWSKeywords) o);
        }
        else if (o instanceof OWSBoundingBox)
        {
            this.addBoundingBox((OWSBoundingBox) o);
        }
        else if (o instanceof WFSMetadataURL)
        {
            this.addMetadataURL((WFSMetadataURL) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addTitle(OWSLanguageString title)
    {
        this.titles.add(title);
    }

    protected void addAbstract(OWSLanguageString abstractText)
    {
        this.abstracts.add(abstractText);
    }

    protected void addKeywordList(OWSKeywords keywords)
    {
        this.keywordLists.add(keywords);
    }

    protected void setDefaultCRS(String crs)
    {
        this.defaultCRS = crs;
    }

    protected void addOtherCRS(String crs)
    {
        this.otherCRS.add(crs);
    }

    protected void setNoCRS(boolean tf)
    {
        this.noCRS = tf;
    }

    protected void addAllOutputFormats(Iterable<String> iterable)
    {
        for (String s : iterable)
        {
            this.outputFormats.add(s);
        }
    }

    protected void addBoundingBox(OWSBoundingBox boundingBox)
    {
        this.boundingBoxes.add(boundingBox);
    }

    protected void addMetadataURL(WFSMetadataURL metadataURL)
    {
        this.metadataURLs.add(metadataURL);
    }

    public String getName()
    {
        return (String) this.getField("Name");
    }

    public List<OWSLanguageString> getTitles()
    {
        return this.titles;
    }

    public List<OWSLanguageString> getAbstracts()
    {
        return this.abstracts;
    }

    public List<OWSKeywords> getKeywordLists()
    {
        return this.keywordLists;
    }

    public String getDefaultCRS()
    {
        return this.defaultCRS;
    }

    public Set<String> getOtherCRS()
    {
        return this.otherCRS;
    }

    public boolean isNoCRS()
    {
        return this.noCRS;
    }

    public Set<String> getOutputFormats()
    {
        return this.outputFormats;
    }

    public List<OWSBoundingBox> getBoundingBoxes()
    {
        return this.boundingBoxes;
    }

    public List<WFSMetadataURL> getMetadataURLs()
    {
        return this.metadataURLs;
    }

    public WFSExtendedDescription getExtendedDescription()
    {
        return (WFSExtendedDescription) this.getField("ExtendedDescription");
    }
}
