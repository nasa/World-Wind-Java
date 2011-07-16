/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.formats.vpf;

/**
 * @author dcollins
 * @version $Id$
 */
public enum VPFFeatureType
{
    POINT,
    LINE,
    AREA,
    TEXT,
    COMPLEX,
    LABEL;

    public static VPFFeatureType fromTypeName(String featureType)
    {
        if (featureType.equalsIgnoreCase(VPFConstants.POINT_FEATURE_TYPE))
        {
            return POINT;
        }
        else if (featureType.equalsIgnoreCase(VPFConstants.LINE_FEATURE_TYPE))
        {
            return LINE;
        }
        else if (featureType.equalsIgnoreCase(VPFConstants.AREA_FEATURE_TYPE))
        {
            return AREA;
        }
        else if (featureType.equalsIgnoreCase(VPFConstants.TEXT_FEATURE_TYPE))
        {
            return TEXT;
        }
        else if (featureType.equalsIgnoreCase(VPFConstants.COMPLEX_FEATURE_TYPE))
        {
            return COMPLEX;
        }

        return null;
    }
}
