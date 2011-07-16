/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.xml.AbstractXMLEventParser;

/**
 * Parses an OGC Web Service Common (OWS) PossibleValues group element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsDomainType.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public abstract class OWSPossibleValues extends AbstractXMLEventParser
{
    public OWSPossibleValues(String namespaceURI)
    {
        super(namespaceURI);
    }

    public boolean isAllowedValues()
    {
        return false;
    }

    public boolean isAnyValue()
    {
        return false;
    }

    public boolean isNoValues()
    {
        return false;
    }

    public boolean isValuesReference()
    {
        return false;
    }

    public OWSAllowedValues asAllowedValues()
    {
        return null;
    }

    public OWSAnyValue asAnyValue()
    {
        return null;
    }

    public OWSNoValues asNoValues()
    {
        return null;
    }

    public OWSValuesReference asValuesReference()
    {
        return null;
    }
}
