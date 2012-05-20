/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Service Common (OWS) AddressType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/ows19115subset.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSAddress extends AbstractXMLEventParser
{
    protected QName DELIVERY_POINT;
    protected QName ELECTRONIC_MAIL_ADDRESS;

    protected List<String> deliveryPoints = new ArrayList<String>();
    protected List<String> emailAddresses = new ArrayList<String>();

    public OWSAddress(String namespaceURI)
    {
        super(namespaceURI);

        this.initialize();
    }

    protected void initialize()
    {
        DELIVERY_POINT = new QName(this.getNamespaceURI(), "DeliveryPoint");
        ELECTRONIC_MAIL_ADDRESS = new QName(this.getNamespaceURI(), "ElectronicMailAddress");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, DELIVERY_POINT))
        {
            this.addDeliveryPoint((String) o);
        }
        else if (ctx.isStartElement(event, ELECTRONIC_MAIL_ADDRESS))
        {
            this.addElectronicMailAddress((String) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addDeliveryPoint(String deliveryPoint)
    {
        this.deliveryPoints.add(deliveryPoint);
    }

    protected void addElectronicMailAddress(String emailAddress)
    {
        this.emailAddresses.add(emailAddress);
    }

    public List<String> getDeliveryPoints()
    {
        return this.deliveryPoints;
    }

    public String getCity()
    {
        return (String) this.getField("City");
    }

    public String getAdministrativeArea()
    {
        return (String) this.getField("AdministrativeArea");
    }

    public String getPostalCode()
    {
        return (String) this.getField("PostalCode");
    }

    public String getCountry()
    {
        return (String) this.getField("Country");
    }

    public List<String> getElectronicMailAddresses()
    {
        return this.emailAddresses;
    }
}
