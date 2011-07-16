/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.ogc.ows.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;

/**
 * @author dcollins
 * @version $Id$
 */
public class WFSParserContext extends OWSParserContext
{
    /**
     * String fields used in WFS schema.
     * <p/>
     * Compatible with WFS </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/wfs/2.0/wfs.xsd.
     */
    protected static final String[] StringFields = new String[]
        {
            "Name", // as xsd:QName
            "DefaultCRS", // as anyURI
            "OtherCRS", // as anyURI
            "NoCRS",
            "Format",
            "about", // as anyURI
            "name",
            "type" // as xsd:QName
        };

    public WFSParserContext(XMLEventReader eventReader, String defaultNamespace)
    {
        super(eventReader, defaultNamespace);
    }

    @Override
    protected void initializeParsers()
    {
        super.initializeParsers();

        this.initializeWFSVersion2dot0Parsers();
    }

    protected void initializeWFSVersion2dot0Parsers()
    {
        this.initializeWFSParsers(WFSConstants.WFS_2dot0_NAMESPACE);
    }

    /**
     * Loads parsers for the WFS schema.
     * <p/>
     * Compatible with WFS version </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/wfs/2.0/wfs.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeWFSParsers(String ns)
    {
        //this.registerParser(new QName(ns, "WFS_Capabilities"), new WFSCapabilities(ns));
        this.registerParser(new QName(ns, "FeatureTypeList"), new WFSFeatureTypeListParser(ns));
        this.registerParser(new QName(ns, "FeatureType"), new WFSFeatureType(ns));
        this.registerParser(new QName(ns, "Title"), new OWSLanguageString(ns));
        this.registerParser(new QName(ns, "Abstract"), new OWSLanguageString(ns));
        this.registerParser(new QName(ns, "OutputFormats"), new WFSOutputFormats(ns));
        this.registerParser(new QName(ns, "MetadataURL"), new WFSMetadataURL(ns));
        this.registerParser(new QName(ns, "ExtendedDescription"), new WFSExtendedDescription(ns));
        this.registerParser(new QName(ns, "Element"), new WFSElement(ns));
        this.registerParser(new QName(ns, "ValueList"), new WFSValueListParser(ns));
        this.registerParser(new QName(ns, "Value"), new WFSValueParser(ns));

        // TODO: WFS_CapabilitiesType WSDL element
        // TODO: fes:Filter_Capabilities and its dependencies

        this.addStringParsers(ns, StringFields);
    }
}
