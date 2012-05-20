/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.formats.geojson;

import gov.nasa.worldwind.formats.json.*;
import org.codehaus.jackson.JsonParser;

import java.io.IOException;

/**
 * @author dcollins
 * @version $Id$
 */
public class GeoJSONDoc extends JSONDoc
{
    public GeoJSONDoc(Object source)
    {
        super(source);
    }

    @Override
    protected JSONEventParserContext createEventParserContext(JsonParser parser) throws IOException
    {
        return new GeoJSONEventParserContext(parser);
    }

    @Override
    protected JSONEventParser createRootObjectParser() throws IOException
    {
        return new GeoJSONEventParser();
    }
}
