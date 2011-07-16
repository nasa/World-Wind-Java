/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.BasicXMLEventParserContext;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;

/**
 * @author dcollins
 * @version $Id$
 */
public class OWSParserContext extends BasicXMLEventParserContext
{
    /**
     * String fields from ISO 19115 used by the OWS schemas.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/ows19115subset.xsd.
     */
    protected static final String[] ISO_19115_StringFields = new String[]
        {
            "codeSpace",
            "IndividualName",
            "OrganisationName",
            "PositionName",
            "HoursOfService",
            "ContactInstructions",
            "Voice",
            "Facsimile",
            "DeliveryPoint",
            "City",
            "AdministrativeArea",
            "PostalCode",
            "Country",
            "ElectronicMailAddress"
        };

    /**
     * String fields used in OWS common schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsCommon.xsd.
     */
    protected static final String[] OWSCommonStringFields = new String[]
        {
            // Note: the OWS common schema declares MimeType VersionType and strings, but does not declare any elements
            // that implement this type.

            "about", // as anyURI
            "crs" // as anyURI
        };

    /**
     * Integer fields used in OWS common schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsCommon.xsd.
     */
    protected static final String[] OWSCommonIntegerFields = new String[]
        {
            "dimensions", // as positiveInteger
        };

    /**
     * String fields from the OWS data identification schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsDataIdentification.xsd.
     */
    protected static final String[] OWSDataIdentificationStringFields = new String[]
        {
            "OutputFormat", // as MimeType
            "AvailableCRS", // as anyURI
            "SupportedCRS", // as anyURI
            "AccessConstraints",
            "Fees",
            "Language" // as language
        };

    /**
     * String fields from the OWS domain type schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsDomainType.xsd.
     */
    protected static final String[] OWSDomainTypeStringFields = new String[]
        {
            "name",
            "reference", // as anyURI
            "Value", // as ValueType
            "DefaultValue", // as ValueType
            "MinimumValue", // as ValueType
            "MaximumValue", // as ValueType
            "Spacing", // as ValueType
            "rangeClosure" // as enumeration: closed, open, open-closed, closed-open (default=closed)
        };

    /**
     * String fields from the OWS exception report schema.
     * <p/>
     * Compatible with OWS version <code>1.0.0</code>, <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsExceptionReport.xsd.
     */
    protected static final String[] OWSExceptionReportStringFields = new String[]
        {
            "version",
            "exceptionCode",
            "locator",
            "ExceptionText"
        };

    /**
     * String fields from the OWS get capabilities schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsGetCapabilities.xsd.
     */
    protected static final String[] OWSGetCapabilitiesStringFields = new String[]
        {
            // Note: The OWS get capabilities schema defines the string type "ServiceType", but does not declare any
            // elements or attributes that use it.
            "UpdateSequence" // as UpdateSequenceType
        };

    /**
     * String fields from the OWS operations metadata schema.
     * <p/>
     * Compatible with OWS version <code>1.0.0</code>, <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsOperationsMetadata.xsd.
     */
    protected static final String[] OWSOperationsMetadataStringFields = new String[]
        {
            // Note: The OWS operations metadata schema defines the string type "name". Since this is already defined in
            // the OWS domain type schema, we omit it here. This blank array is left as a placeholder for future
            // entries.
        };

    /**
     * String fields from the OWS service identification schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsServiceIdentification.xsd.
     */
    protected static final String[] OWSServiceIdentificationStringFields = new String[]
        {
            "ServiceTypeVersion", // as VersionType
            "Profile" // as anyURI
        };

    /**
     * String fields from the OWS service provider schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsServiceProvider.xsd.
     */
    protected static final String[] OWSServiceProviderStringFields = new String[]
        {
            "ProviderName"
        };

    /**
     * String fields from the XLink schema used by the OWS schemas.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/xlink/1.0.0/xlinks.xsd.
     */
    protected static final String[] XLinkStringFields = new String[]
        {
            "type",
            "href", // as anyURI
            "role", // as anyURI
            "arcrole", // as anyURI
            "title",
            "show", // as enumeration: new, replace, embed, other, none
            "actuate" // as enumeration: onLoad, onRequest, other, none
        };

    /**
     * String fields from the XML standard used by the OWS schemas.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://www.w3.org/2001/xml.xsd.
     */
    protected static final String[] XMLStringFields = new String[]
        {
            "lang"
        };

    public OWSParserContext(XMLEventReader eventReader, String defaultNamespace)
    {
        super(eventReader, defaultNamespace);
    }

    /**
     * Loads the parser map with the parser to use for each element type. The parser may be changed by calling {@link
     * #registerParser(javax.xml.namespace.QName, gov.nasa.worldwind.util.xml.XMLEventParser)}.
     */
    protected void initializeParsers()
    {
        super.initializeParsers();

        this.initializeOWSVersion1dot1Parsers();
    }

    protected void initializeOWSVersion1dot1Parsers()
    {
        this.initializeOWSParsers(OWSConstants.OWS_1dot1_NAMESPACE);
        this.initializeXLinkParsers();
        this.initailizeXMLStandardParsers();
    }

    protected void initializeOWSParsers(String ns)
    {
        this.initializeISO19115Parsers(ns);
        this.initializeOWSCommonParsers(ns);
        this.initializeOWSDataIdentificationParsers(ns);
        this.initializeOWSDomainTypeParsers(ns);
        this.initializeOWSExceptionReportParsers(ns);
        this.initializeOWSGetCapabilitiesParsers(ns);
        this.initializeOWSOperationsMetadataParsers(ns);
        this.initializeOWSServiceIdentificationParsers(ns);
        this.initializeOWSServiceProviderParsers(ns);
    }

    /**
     * Loads parsers for ISO 19115 element used by the OWS schemas.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/ows19115subset.xsd.
     *
     * @param ns the XML namespace used to construct the element parsers. This namespace is passed as the sole parameter
     *           to each XML element parser's constructor.
     */
    protected void initializeISO19115Parsers(String ns)
    {
        this.registerParser(new QName(ns, "Title"), new OWSLanguageString(ns));
        this.registerParser(new QName(ns, "Abstract"), new OWSLanguageString(ns));
        this.registerParser(new QName(ns, "Keywords"), new OWSKeywords(ns));
        this.registerParser(new QName(ns, "Keyword"), new OWSLanguageString(ns));
        this.registerParser(new QName(ns, "Type"), new OWSCodeType(ns));
        this.registerParser(new QName(ns, "PointOfContact"), new OWSResponsibleParty(ns));
        this.registerParser(new QName(ns, "ContactInfo"), new OWSContactInformation(ns));
        this.registerParser(new QName(ns, "Role"), new OWSCodeType(ns));
        this.registerParser(new QName(ns, "Phone"), new OWSTelephone(ns));
        this.registerParser(new QName(ns, "Address"), new OWSAddress(ns));
        this.registerParser(new QName(ns, "OnlineResource"), new OWSOnlineResource(ns));

        this.addStringParsers(ns, ISO_19115_StringFields);
    }

    /**
     * Loads parsers for the OWS common schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsCommon.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeOWSCommonParsers(String ns)
    {
        this.registerParser(new QName(ns, "Metadata"), new OWSMetadata(ns));
        this.registerParser(new QName(ns, "AbstractMetaData"), new OWSAbstractMetaData(ns));
        this.registerParser(new QName(ns, "BoundingBox"), new OWSBoundingBox(ns));
        this.registerParser(new QName(ns, "WGS84BoundingBox"), new OWSWGS84BoundingBox(ns));
        this.registerParser(new QName(ns, "LowerCorner"), new OWSPositionParser(ns));
        this.registerParser(new QName(ns, "UpperCorner"), new OWSPositionParser(ns));

        this.addStringParsers(ns, OWSCommonStringFields);
        this.addIntegerParsers(ns, OWSCommonIntegerFields);
    }

    /**
     * Loads parsers for the OWS data identification schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsDataIdentification.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeOWSDataIdentificationParsers(String ns)
    {
        this.registerParser(new QName(ns, "Identifier"), new OWSCodeType(ns));

        this.addStringParsers(ns, OWSDataIdentificationStringFields);
    }

    /**
     * Loads parsers for the OWS domain type schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsDomainType.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeOWSDomainTypeParsers(String ns)
    {
        this.registerParser(new QName(ns, "AllowedValues"), new OWSAllowedValues(ns));
        this.registerParser(new QName(ns, "AnyValue"), new OWSAnyValue(ns));
        this.registerParser(new QName(ns, "NoValues"), new OWSNoValues(ns));
        this.registerParser(new QName(ns, "ValuesReference"), new OWSValuesReference(ns));
        this.registerParser(new QName(ns, "Meaning"), new OWSDomainMetadata(ns));
        this.registerParser(new QName(ns, "DataType"), new OWSDomainMetadata(ns));
        this.registerParser(new QName(ns, "UOM"), new OWSDomainMetadata(ns));
        this.registerParser(new QName(ns, "ReferenceSystem"), new OWSDomainMetadata(ns));
        this.registerParser(new QName(ns, "Range"), new OWSRange(ns));

        this.addStringParsers(ns, OWSDomainTypeStringFields);
    }

    /**
     * Loads parsers for the OWS exception report schema.
     * <p/>
     * Compatible with OWS version <code>1.0.0</code>, <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsExceptionReport.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeOWSExceptionReportParsers(String ns)
    {
        //this.registerParser(new QName(ns, "ExceptionReport"), new OWSExceptionReport(ns));
        this.registerParser(new QName(ns, "Exception"), new OWSException(ns));

        this.addStringParsers(ns, OWSExceptionReportStringFields);
    }

    /**
     * Loads parsers for the OWS get capabilities schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>. The Languages element is included in OWS
     * version <code>2.0.0</code> and newer.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsGetCapabilities.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeOWSGetCapabilitiesParsers(String ns)
    {
        this.registerParser(new QName(ns, "Languages"), new OWSLanguages(ns));

        this.addStringParsers(ns, OWSGetCapabilitiesStringFields);
    }

    /**
     * Loads parsers for the OWS operations metadata schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsOperationsMetadata.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeOWSOperationsMetadataParsers(String ns)
    {
        this.registerParser(new QName(ns, "OperationsMetadata"), new OWSOperationsMetadata(ns));
        this.registerParser(new QName(ns, "Operation"), new OWSOperation(ns));
        this.registerParser(new QName(ns, "Parameter"), new OWSDomain(ns));
        this.registerParser(new QName(ns, "Constraint"), new OWSDomain(ns));
        this.registerParser(new QName(ns, "DCP"), new OWSDCPType(ns));
        this.registerParser(new QName(ns, "HTTP"), new OWSProtocol(ns));
        this.registerParser(new QName(ns, "Get"), new OWSRequestMethod(ns));
        this.registerParser(new QName(ns, "Post"), new OWSRequestMethod(ns));

        this.addStringParsers(ns, OWSOperationsMetadataStringFields);
    }

    /**
     * Loads parsers for the OWS service identification schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsServiceIdentification.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeOWSServiceIdentificationParsers(String ns)
    {
        this.registerParser(new QName(ns, "ServiceIdentification"), new OWSServiceIdentification(ns));
        this.registerParser(new QName(ns, "ServiceType"), new OWSCodeType(ns));

        this.addStringParsers(ns, OWSServiceIdentificationStringFields);
    }

    /**
     * Loads parsers for the OWS service provider schema.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/ows/2.0/owsServiceProvider.xsd.
     *
     * @param ns ns the XML namespace used to construct the element parsers. This namespace is passed as the sole
     *           parameter to each XML element parser's constructor.
     */
    protected void initializeOWSServiceProviderParsers(String ns)
    {
        this.registerParser(new QName(ns, "ServiceProvider"), new OWSServiceProvider(ns));
        this.registerParser(new QName(ns, "ProviderSite"), new OWSOnlineResource(ns));
        this.registerParser(new QName(ns, "ServiceContact"), new OWSResponsiblePartySubset(ns));

        this.addStringParsers(ns, OWSServiceProviderStringFields);
    }

    /**
     * Loads parsers for XLink elements used by the OWS schemas.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://schemas.opengis.net/xlink/1.0.0/xlinks.xsd.
     */
    protected void initializeXLinkParsers()
    {
        this.addStringParsers("http://www.w3.org/1999/xlink", XLinkStringFields);
    }

    /**
     * Loads parsers for XML standard elements used by the OWS schemas.
     * <p/>
     * Compatible with OWS version <code>1.1.0</code> and </code>2.0.0</code>.
     * <p/>
     * See http://www.w3.org/2001/xml.xsd.
     */
    protected void initailizeXMLStandardParsers()
    {
        this.addStringParsers(XMLConstants.XML_NS_URI, XMLStringFields);
    }
}
