/* Copyright (C) 2001, 2009 United States Government as represented by
   the Administrator of the National Aeronautics and Space Administration.
   All Rights Reserved.
 */
package gov.nasa.worldwind.servers.tools.utm;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.globes.Earth;

import gov.nasa.worldwind.servers.tools.utm.UTMCoords;
/**
 * @author garakl
 * @version $Id$
 */
class Utm2LatLon extends UTMConverter
{
    public static LatLon convert(UTMCoords.Hemisphere hemisphere, short lonZone, double easting, double northing)
    {
        double e = 0.081819191d;
        double e1sq = 0.006739497d;
        double k0 = 0.9996d;

        // UTMCoords.Hemisphere hemisphere = UTMConverter.getHemisphere( latBand );
        if(hemisphere == UTMCoords.Hemisphere.SOUTH )
            northing = 10000000 - northing;

        double arc = northing / k0;

        double mu = arc / (Earth.WGS84_EQUATORIAL_RADIUS * (1 - POW(e, 2)/ 4.0 - 3 * POW(e, 4) / 64.0 - 5 * POW(e, 6) / 256.0));

        double ei = (1 - POW((1 - e * e), (1 / 2.0))) / (1 + POW((1 - e * e), (1 / 2.0)));

        double ca = 3 * ei / 2 - 27 * POW(ei, 3) / 32.0;

        double cb = 21 * POW(ei, 2) / 16 - 55 * POW(ei, 4) / 32;

        double cc = 151 * POW(ei, 3) / 96;

        double cd = 1097 * POW(ei, 4) / 512;

        double phi1 = mu + ca * SIN(2 * mu) + cb * SIN(4 * mu) + cc * SIN(6 * mu) + cd * SIN(8 * mu);

        double n0 = Earth.WGS84_EQUATORIAL_RADIUS / POW((1 - POW((e * SIN(phi1)), 2)), (1 / 2.0));

        double r0 = Earth.WGS84_EQUATORIAL_RADIUS * (1 - e * e) / POW((1 - POW((e * SIN(phi1)), 2)), (3 / 2.0));

        double fact1 = n0 * TAN(phi1) / r0;

        double _a1 = 500000 - easting;

        double dd0 = _a1 / (n0 * k0);

        double fact2 = dd0 * dd0 / 2;

        double t0 = POW(TAN(phi1), 2);

        double Q0 = e1sq * POW(COS(phi1), 2);

        double fact3 = (5 + 3 * t0 + 10 * Q0 - 4 * Q0 * Q0 - 9 * e1sq) * POW(dd0, 4)/ 24;

        double fact4 = (61 + 90 * t0 + 298 * Q0 + 45 * t0 * t0 - 252 * e1sq - 3 * Q0 * Q0) * POW(dd0, 6) / 720;

        double lof1 = _a1 / (n0 * k0);

        double lof2 = (1 + 2 * t0 + Q0) * POW(dd0, 3) / 6.0;

        double lof3 = (5 - 2 * Q0 + 28 * t0 - 3 * POW(Q0, 2) + 8 * e1sq + 24 * POW(t0, 2)) * POW(dd0, 5) / 120;

        double _a2 = (lof1 - lof2 + lof3) / COS(phi1);

        double _a3 = _a2 * 180 / Math.PI;

        double latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

        double zoneCM = (lonZone > 0) ? (6 * lonZone - 183.0) : 3.0;

        double longitude = zoneCM - _a3;

        if( hemisphere == UTMCoords.Hemisphere.SOUTH)
            latitude = -latitude;

        return new LatLon( Angle.fromDegreesLatitude(latitude), Angle.fromDegreesLongitude(longitude));
    }
}
