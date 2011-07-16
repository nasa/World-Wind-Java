/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.util.xml;

import gov.nasa.worldwind.ogc.kml.KMLAbstractObject;

/**
 * Holds the content of unrecognized elements. There are no field-specific accessors because the field names are
 * unknown, but all fields can be accessed via the inherited {@link gov.nasa.worldwind.util.xml.AbstractXMLEventParser#getField(javax.xml.namespace.QName)}
 * and {@link gov.nasa.worldwind.util.xml.AbstractXMLEventParser#getFields()}.
 *
 * @author tag
 * @version $Id$
 */
public class UnrecognizedXMLEventParser extends KMLAbstractObject
{
    public UnrecognizedXMLEventParser()
    {
    }

    public UnrecognizedXMLEventParser(String namespaceURI)
    {
        super(namespaceURI);
    }
}
