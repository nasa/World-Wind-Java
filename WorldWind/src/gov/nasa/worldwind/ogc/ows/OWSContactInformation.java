/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * Parses an OGC Web Service Common (OWS) ContactType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/ows19115subset.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSContactInformation extends AbstractXMLEventParser
{
    public OWSContactInformation(String namespaceURI)
    {
        super(namespaceURI);
    }

    public OWSTelephone getPhone()
    {
        return (OWSTelephone) this.getField("Phone");
    }

    public OWSAddress getAddress()
    {
        return (OWSAddress) this.getField("Address");
    }

    public OWSOnlineResource getOnlineResource()
    {
        return (OWSOnlineResource) this.getField("OnlineResource");
    }

    public String getHoursOfService()
    {
        return (String) this.getField("HoursOfService");
    }

    public String getContactInstructions()
    {
        return (String) this.getField("ContactInstructions");
    }
}
