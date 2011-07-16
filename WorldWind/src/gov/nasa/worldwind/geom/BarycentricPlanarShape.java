/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.worldwind.geom;

/**
 * @author tag
 * @version $Id$
 */
public interface BarycentricPlanarShape
{
    double[] getBarycentricCoords(Vec4 p);

    Vec4 getPoint(double[] w);

    @SuppressWarnings({"UnnecessaryLocalVariable"})
    double[] getBilinearCoords(double alpha, double beta);
}
