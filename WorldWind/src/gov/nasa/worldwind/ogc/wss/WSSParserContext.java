/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wss;

import gov.nasa.worldwind.ogc.wfs.WFSParserContext;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;

/**
 * @author dcollins
 * @version $Id$
 */
public class WSSParserContext extends WFSParserContext
{
    public WSSParserContext(XMLEventReader eventReader, String defaultNamespace)
    {
        super(eventReader, defaultNamespace);
    }

    @Override
    protected void initializeParsers()
    {
        super.initializeParsers();

        this.initializeWSSParsers(XMLConstants.NULL_NS_URI);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Overrides the default WFS FeatureType parser with the WSS FeatureType parser.
     */
    @Override
    protected void initializeWFSParsers(String ns)
    {
        super.initializeWFSParsers(ns);
        this.registerParser(new QName(ns, "FeatureType"), new WSSFeatureType(ns));
    }

    /**
     * Loads parsers for WSS protocol.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeWSSParsers(String ns)
    {
        this.registerParser(new QName(ns, "NamedFeatures"), new WSSNamedFeatures(ns));
    }
}
