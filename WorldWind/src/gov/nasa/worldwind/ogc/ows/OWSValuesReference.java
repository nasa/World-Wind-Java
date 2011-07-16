/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

/**
 * Parses an OGC Web Service Common (OWS) ValuesReference element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsDomainType.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSValuesReference extends OWSPossibleValues
{
    public OWSValuesReference(String namespaceURI)
    {
        super(namespaceURI);
    }

    public String getText()
    {
        return this.getCharacters();
    }

    public String getReference()
    {
        return (String) this.getField("reference");
    }

    @Override
    public boolean isValuesReference()
    {
        return true;
    }

    @Override
    public OWSValuesReference asValuesReference()
    {
        return this;
    }
}
