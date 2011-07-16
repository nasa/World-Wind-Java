/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wfs;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * Parses an OGC Web Feature Service (WFS) MetadataURLType element and provides access to its contents. See
 * http://schemas.opengis.net/wfs/2.0/wfs.xsd, and http://schemas.opengis.net/xlink/1.0.0/xlinks.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class WFSMetadataURL extends AbstractXMLEventParser
{
    public WFSMetadataURL(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getType()
    {
        return (String) this.getField("type");
    }

    public String getHref()
    {
        return (String) this.getField("href");
    }

    public String getRole()
    {
        return (String) this.getField("role");
    }

    public String getArcRole()
    {
        return (String) this.getField("arcrole");
    }

    public String getTitle()
    {
        return (String) this.getField("title");
    }

    public String getShow()
    {
        return (String) this.getField("show");
    }

    public String getActuate()
    {
        return (String) this.getField("actuate");
    }

    public String getAbout()
    {
        return (String) this.getField("about");
    }
}
