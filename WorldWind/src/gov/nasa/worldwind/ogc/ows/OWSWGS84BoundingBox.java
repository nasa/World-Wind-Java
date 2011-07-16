/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.ogc.ows;

import gov.nasa.worldwind.geom.Sector;

/**
 * Parses an OGC Web Service Common (OWS) WGS84BoundingBoxType element and provides access to its contents. See
 * http://schemas.opengis.net/ows/2.0/owsCommon.xsd.
 *
 * @author dcollins
 * @version $Id$
 */
public class OWSWGS84BoundingBox extends OWSBoundingBox
{
    public OWSWGS84BoundingBox(String namespaceURI)
    {
        super(namespaceURI);
    }

    public Sector getSector()
    {
        double[] ll = this.getLowerCorner();
        double[] ur = this.getUpperCorner();

        if (ll == null || ur == null || ll.length != 2 || ur.length != 2)
            return null;

        return Sector.fromDegrees(ll[1], ur[1], ll[0], ur[0]);
    }
}
