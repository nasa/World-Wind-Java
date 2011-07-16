/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * Parses an OGC Web Service Common (OWS) ServiceProvider element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsServiceProvider.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSServiceProvider extends AbstractXMLEventParser
{
    public OWSServiceProvider(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getProviderName()
    {
        return (String) this.getField("ProviderName");
    }

    public OWSOnlineResource getProviderSite()
    {
        return (OWSOnlineResource) this.getField("ProviderSite");
    }

    public OWSResponsiblePartySubset getServiceContact()
    {
        return (OWSResponsiblePartySubset) this.getField("ServiceContact");
    }
}
