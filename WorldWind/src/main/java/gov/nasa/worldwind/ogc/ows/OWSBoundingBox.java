/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * Parses an OGC Web Service Common (OWS) BoundingBoxType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsCommon.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSBoundingBox extends AbstractXMLEventParser
{
    public OWSBoundingBox(String namespaceURI)
    {
        super(namespaceURI);
    }

    public double[] getLowerCorner()
    {
        return (double[]) this.getField("LowerCorner");
    }

    public double[] getUpperCorner()
    {
        return (double[]) this.getField("UpperCorner");
    }

    public String getCRS()
    {
        return (String) this.getField("crs");
    }

    public Integer getDimensions()
    {
        return (Integer) this.getField("dimensions");
    }
}
