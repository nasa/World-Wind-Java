/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.util;

import gov.nasa.worldwind.geom.*;

import java.util.*;

/**
 * A collection of useful math methods, all static.
 *
 * @author dcollins
 * @version $Id$
 */
public class WWMath
{
    /**
     * Returns an array of normalized vectors defining the three principal axes of the x-, y-, and z-coordinates from
     * the specified points Iterable, sorted from the most prominent axis to the least prominent. This returns null if
     * the points Iterable is empty, or if all of the points are null. The returned array contains three normalized
     * orthogonal vectors defining a coordinate system which best fits the distribution of the points Iterable about its
     * arithmetic mean.
     *
     * @param points the Iterable of points for which to compute the principal axes.
     *
     * @return the normalized principal axes of the points Iterable, sorted from the most prominent axis to the least
     *         prominent.
     *
     * @throws IllegalArgumentException if the points Iterable is null.
     */
    public static Vec4[] computePrincipalAxes(Iterable<? extends Vec4> points)
    {
        if (points == null)
        {
            String msg = Logging.getMessage("nullValue.PointListIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Compute the covariance matrix of the specified points Iterable. Note that Matrix.fromCovarianceOfVertices
        // returns null if the points Iterable is empty, or if all of the points are null.
        Matrix covariance = Matrix.fromCovarianceOfPoints(points);
        if (covariance == null)
            return null;

        // Compute the eigenvalues and eigenvectors of the covariance matrix. Since the covariance matrix is symmetric
        // by definition, we can safely use the method Matrix.computeEigensystemFromSymmetricMatrix3().
        final double[] eigenValues = new double[3];
        final Vec4[] eigenVectors = new Vec4[3];
        Matrix.computeEigensystemFromSymmetricMatrix3(covariance, eigenValues, eigenVectors);

        // Compute an index array who's entries define the order in which the eigenValues array can be sorted in
        // ascending order.
        Integer[] indexArray = {0, 1, 2};
        Arrays.sort(indexArray, new Comparator<Integer>()
        {
            public int compare(Integer a, Integer b)
            {
                return Double.compare(eigenValues[a], eigenValues[b]);
            }
        });

        // Return the normalized eigenvectors in order of decreasing eigenvalue. This has the effect of returning three
        // normalized orthognal vectors defining a coordinate system, which are sorted from the most prominent axis to
        // the least prominent.
        return new Vec4[]
            {
                eigenVectors[indexArray[2]].normalize3(),
                eigenVectors[indexArray[1]].normalize3(),
                eigenVectors[indexArray[0]].normalize3()
            };
    }
}
