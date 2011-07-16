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
 * Parses an OGC Web Service Common (OWS) ServiceIdentification element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsServiceIdentification.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSServiceIdentification extends OWSDescription
{
    protected QName SERVICE_TYPE_VERSION;
    protected QName PROFILE;
    protected QName ACCESS_CONSTRAINTS;

    protected List<String> serviceTypeVersions = new ArrayList<String>();
    protected List<String> profiles = new ArrayList<String>();
    protected List<String> accessConstraints = new ArrayList<String>();

    public OWSServiceIdentification(String namespaceURI)
    {
        super(namespaceURI);
    }

    protected void initialize()
    {
        super.initialize();

        SERVICE_TYPE_VERSION = new QName(this.getNamespaceURI(), "ServiceTypeVersion");
        PROFILE = new QName(this.getNamespaceURI(), "Profile");
        ACCESS_CONSTRAINTS = new QName(this.getNamespaceURI(), "AccessConstraints");
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
        throws XMLStreamException
    {
        if (ctx.isStartElement(event, SERVICE_TYPE_VERSION))
        {
            this.addServiceTypeVersion((String) o);
        }
        else if (ctx.isStartElement(event, PROFILE))
        {
            this.addProfile((String) o);
        }
        else if (ctx.isStartElement(event, ACCESS_CONSTRAINTS))
        {
            this.addAccessConstraints((String) o);
        }
        else
        {
            super.doAddEventContent(o, ctx, event, args);
        }
    }

    protected void addServiceTypeVersion(String version)
    {
        this.serviceTypeVersions.add(version);
    }

    protected void addProfile(String profile)
    {
        this.profiles.add(profile);
    }

    protected void addAccessConstraints(String accessConstraints)
    {
        this.accessConstraints.add(accessConstraints);
    }

    public OWSCodeType getServiceType()
    {
        return (OWSCodeType) this.getField("ServiceType");
    }

    public List<String> getServiceTypeVersions()
    {
        return this.serviceTypeVersions;
    }

    public List<String> getProfiles()
    {
        return this.profiles;
    }

    public String getFees()
    {
        return (String) this.getField("Fees");
    }

    public List<String> getAccessConstraints()
    {
        return this.accessConstraints;
    }
}
