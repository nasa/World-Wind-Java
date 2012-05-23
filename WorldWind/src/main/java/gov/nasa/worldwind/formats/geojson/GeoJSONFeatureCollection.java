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
public class GeoJSONFeatureCollection extends GeoJSONObject
{
    public GeoJSONFeatureCollection(AVList fields)
    {
        super(fields);
    }

    @Override
    public boolean isFeatureCollection()
    {
        return true;
    }

    public GeoJSONFeature[] getFeatures()
    {
        return (GeoJSONFeature[]) this.getValue(GeoJSONConstants.FIELD_FEATURES);
    }
}
