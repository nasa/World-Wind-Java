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
public abstract class GeoJSONGeometry extends GeoJSONObject
{
    protected GeoJSONGeometry(AVList fields)
    {
        super(fields);
    }

    @Override
    public boolean isGeometry()
    {
        return true;
    }
}
