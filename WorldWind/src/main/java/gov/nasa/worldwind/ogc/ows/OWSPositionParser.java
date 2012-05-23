/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.util.xml.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

/**
 * Parses an OGC Web Service Common (OWS) PositionType element and converts it to an array of doubles. See
 * http://schemas.opengis.net/ows/2.0/owsCommon.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSPositionParser extends AbstractXMLEventParser
{
    protected List<Double> values = new ArrayList<Double>();

    public OWSPositionParser(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    public Object parse(XMLEventParserContext ctx, XMLEvent inputEvent, Object... args) throws XMLStreamException
    {
        String s = ctx.getStringParser().parseString(ctx, inputEvent);
        if (WWUtil.isEmpty(s))
            return null;

        Scanner scanner = new Scanner(s);
        try
        {
            while (scanner.hasNext())
            {
                this.values.add(scanner.nextDouble());
            }
        }
        finally
        {
            scanner.close();
        }

        return this.makePosition();
    }

    protected Object makePosition()
    {
        if (this.values.isEmpty())
            return null;

        double[] doubleValues = new double[values.size()];

        for (int i = 0; i < this.values.size(); i++)
        {
            doubleValues[i] = this.values.get(i);
        }

        return doubleValues;
    }
}
