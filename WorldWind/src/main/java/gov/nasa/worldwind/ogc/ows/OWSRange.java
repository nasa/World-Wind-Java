/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * Parses an OGC Web Service Common (OWS) RangeType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsDomainType.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSRange extends AbstractXMLEventParser
{
    public OWSRange(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getMinimumValue()
    {
        return (String) this.getField("MinimumValue");
    }

    public String getMaximumValue()
    {
        return (String) this.getField("MaximumValue");
    }

    public String getSpacing()
    {
        return (String) this.getField("Spacing");
    }

    public String getRangeClosure()
    {
        return (String) this.getField("rangeClosure");
    }
}
