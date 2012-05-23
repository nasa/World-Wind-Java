/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.formats.geojson;

import gov.nasa.worldwind.avlist.AVList;

/**
 * @author dcollins
 * @version $Id$
 */
public class GeoJSONMultiLineString extends GeoJSONGeometry
{
    public GeoJSONMultiLineString(AVList fields)
    {
        super(fields);
    }

    @Override
    public boolean isMultiLineString()
    {
        return true;
    }

    public int getLineStringCount()
    {
        GeoJSONPositionArray array = this.getCoordinates(0);
        return array != null ? array.length() : 0;
    }

    public GeoJSONPositionArray[] getCoordinates()
    {
        return (GeoJSONPositionArray[]) this.getValue(GeoJSONConstants.FIELD_COORDINATES);
    }

    public GeoJSONPositionArray getCoordinates(int lineString)
    {
        GeoJSONPositionArray[] array = this.getCoordinates();
        return array != null ? array[lineString] : null;
    }
}
