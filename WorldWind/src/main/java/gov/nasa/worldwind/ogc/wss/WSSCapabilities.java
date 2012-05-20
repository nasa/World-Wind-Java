/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.wss;

import gov.nasa.worldwind.ogc.wfs.WFSCapabilities;
import gov.nasa.worldwind.util.xml.XMLEventParserContext;

import javax.xml.stream.XMLEventReader;

/**
 * Parses a World Wind Web Shape Service (WSS) WFS_CapabilitiesType element and provides access to its contents.
 *
 * @author dcollins
 * @version $Id$
 */
public class WSSCapabilities extends WFSCapabilities
{
    public WSSCapabilities()
    {
    }

    public WSSCapabilities(Object docSource)
    {
        super(docSource);
    }

    /** {@inheritDoc} */
    protected XMLEventParserContext createParserContext(XMLEventReader reader)
    {
        return new WSSParserContext(reader, this.getNamespaceURI());
    }
}
