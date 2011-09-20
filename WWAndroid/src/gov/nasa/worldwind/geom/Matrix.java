/*
 * Copyright (C) 2011 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */
package gov.nasa.worldwind.geom;

import gov.nasa.worldwind.util.Logging;

/**
 * @author dcollins
 * @version $Id$
 */
public class Matrix
{
    // Row 1
    public double m11;
    public double m12;
    public double m13;
    public double m14;
    // Row 2
    public double m21;
    public double m22;
    public double m23;
    public double m24;
    // Row 3
    public double m31;
    public double m32;
    public double m33;
    public double m34;
    // Row 4
    public double m41;
    public double m42;
    public double m43;
    public double m44;

    public Matrix(
        double m11, double m12, double m13, double m14,
        double m21, double m22, double m23, double m24,
        double m31, double m32, double m33, double m34,
        double m41, double m42, double m43, double m44)
    {
        // Row 1
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m14 = m14;
        // Row 2
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m24 = m24;
        // Row 3
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        this.m34 = m34;
        // Row 4
        this.m41 = m41;
        this.m42 = m42;
        this.m43 = m43;
        this.m44 = m44;
    }

    public static Matrix fromIdentity()
    {
        return new Matrix(
            1, 0, 0, 0, // Row 1
            0, 1, 0, 0, // Row 2
            0, 0, 1, 0, // Row 3
            0, 0, 0, 1); // Row 4
    }

    public static Matrix fromTranslation(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setTranslation(vec);
    }

    public static Matrix fromTranslation(double x, double y, double z)
    {
        return Matrix.fromIdentity().setTranslation(x, y, z);
    }

    public static Matrix fromScale(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setScale(vec);
    }

    public static Matrix fromScale(double x, double y, double z)
    {
        return Matrix.fromIdentity().setScale(x, y, z);
    }

    public static Matrix fromRotationX(Angle angle)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setRotationX(angle);
    }

    public static Matrix fromRotationY(Angle angle)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setRotationY(angle);
    }

    public static Matrix fromRotationZ(Angle angle)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setRotationZ(angle);
    }

    public static Matrix fromAxisAngle(Angle angle, Vec4 axis)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (axis == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setAxisAngle(angle, axis);
    }

    public static Matrix fromAxisAngle(Angle angle, double x, double y, double z)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setAxisAngle(angle, x, y, z);
    }

    /**
     * Returns a viewing matrix in model coordinates defined by the specified View eye point, reference point indicating
     * the center of the scene, and up vector. The eye point, center point, and up vector are in model coordinates. The
     * returned viewing matrix maps the reference center point to the negative Z axis, and the eye point to the origin,
     * and the up vector to the positive Y axis. When this matrix is used to define an OGL viewing transform along with
     * a typical projection matrix such as {@link #setPerspective(Angle, double, double, double, double)} , this maps
     * the center of the scene to the center of the viewport, and maps the up vector to the viewoport's positive Y axis
     * (the up vector points up in the viewport). The eye point and reference center point must not be coincident, and
     * the up vector must not be parallel to the line of sight (the vector from the eye point to the reference center
     * point).
     *
     * @param eye    the eye point, in model coordinates.
     * @param center the scene's reference center point, in model coordinates.
     * @param up     the direction of the up vector, in model coordinates.
     *
     * @return a viewing matrix in model coordinates defined by the specified eye point, reference center point, and up
     *         vector.
     *
     * @throws IllegalArgumentException if any of the eye point, reference center point, or up vector are null, if the
     *                                  eye point and reference center point are coincident, or if the up vector and the
     *                                  line of sight are parallel.
     */
    public static Matrix fromLookAt(Vec4 eye, Vec4 center, Vec4 up)
    {
        if (eye == null)
        {
            String msg = Logging.getMessage("nullValue.EyeIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (center == null)
        {
            String msg = Logging.getMessage("nullValue.CenterIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (up == null)
        {
            String msg = Logging.getMessage("nullValue.UpIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setLookAt(eye, center, up);
    }

    public static Matrix fromPerspective(Angle horizontalFieldOfView, double viewportWidth, double viewportHeight,
        double near, double far)
    {
        if (horizontalFieldOfView == null)
        {
            String msg = Logging.getMessage("nullValue.FieldOfViewIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (horizontalFieldOfView.degrees <= 0 || horizontalFieldOfView.degrees > 180)
        {
            String msg = Logging.getMessage("generic.FieldOfViewIsInvalid", horizontalFieldOfView);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (viewportWidth < 0)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", viewportWidth);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (viewportHeight < 0)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", viewportHeight);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (near <= 0 || near > far)
        {
            String msg = Logging.getMessage("generic.ClipDistancesAreInvalid", near, far);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setPerspective(horizontalFieldOfView, viewportWidth, viewportHeight, near, far);
    }

    public static Matrix fromPerspective(double left, double right, double bottom, double top, double near, double far)
    {
        if (left > right)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", right - left);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (bottom > top)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", top - bottom);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (near <= 0 || near > far)
        {
            String msg = Logging.getMessage("generic.ClipDistancesAreInvalid", near, far);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setPerspective(left, right, bottom, top, near, far);
    }

    public static Matrix fromOrthographic2D(double viewportWidth, double viewportHeight)
    {
        if (viewportWidth < 0)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", viewportWidth);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (viewportHeight < 0)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", viewportHeight);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setOrthographic2D(viewportWidth, viewportHeight);
    }

    /**
     * Computes a symmetric covariance Matrix from the x, y, z coordinates of the specified points Iterable. This
     * returns null if the points Iterable is empty, or if all of the points are null.
     * <p/>
     * The returned covariance matrix represents the correlation between each pair of x-, y-, and z-coordinates as
     * they're distributed about the point Iterable's arithmetic mean. Its layout is as follows:
     * <p/>
     * <code> C(x, x)  C(x, y)  C(x, z) <br/> C(x, y)  C(y, y)  C(y, z) <br/> C(x, z)  C(y, z)  C(z, z) </code>
     * <p/>
     * C(i, j) is the covariance of coordinates i and j, where i or j are a coordinate's dispersion about its mean
     * value. If any entry is zero, then there's no correlation between the two coordinates defining that entry. If the
     * returned matrix is diagonal, then all three coordinates are uncorrelated, and the specified point Iterable is
     * distributed evenly about its mean point.
     *
     * @param points the Iterable of points for which to compute a Covariance matrix.
     *
     * @return the covariance matrix for the iterable of 3D points.
     *
     * @throws IllegalArgumentException if the points Iterable is null.
     */
    public static Matrix fromCovarianceOfPoints(Iterable<? extends Vec4> points)
    {
        if (points == null)
        {
            String msg = Logging.getMessage("nullValue.PointListIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Matrix.fromIdentity().setCovarianceOfPoints(points);
    }

    /**
     * Computes the eigensystem of the specified symmetric Matrix's upper 3x3 matrix. If the Matrix's upper 3x3 matrix
     * is not symmetric, this throws an IllegalArgumentException. This writes the eigensystem parameters to the
     * specified arrays <code>outEigenValues</code> and <code>outEigenVectors</code>, placing the eigenvalues in the
     * entries of array <code>outEigenValues</code>, and the corresponding eigenvectors in the entires of array
     * <code>outEigenVectors</code>. These arrays must be non-null, and have length three or greater.
     *
     * @param matrix             the symmetric Matrix for which to compute an eigensystem.
     * @param resultEigenvalues  the array which receives the three output eigenvalues.
     * @param resultEigenvectors the array which receives the three output eigenvectors.
     *
     * @throws IllegalArgumentException if the Matrix is null or is not symmetric, if the output eigenvalue array is
     *                                  null or has length less than 3, or if the output eigenvector is null or has
     *                                  length less than 3.
     */
    public static void computeEigensystemFromSymmetricMatrix3(Matrix matrix, double[] resultEigenvalues,
        Vec4[] resultEigenvectors)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (matrix.m12 != matrix.m21 || matrix.m13 != matrix.m31 || matrix.m23 != matrix.m32)
        {
            String msg = Logging.getMessage("Matrix.MatrixIsNotSymmetric", matrix);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (resultEigenvalues == null)
        {
            String msg = Logging.getMessage("nullValue.ResultIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (resultEigenvalues.length < 3)
        {
            String msg = Logging.getMessage("generic.ResultArrayInvalidLength", resultEigenvalues.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (resultEigenvectors == null)
        {
            String msg = Logging.getMessage("nullValue.ResultIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (resultEigenvectors.length < 3)
        {
            String msg = Logging.getMessage("generic.ResultArrayInvalidLength", resultEigenvectors.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Take from "Mathematics for 3D Game Programming and Computer Graphics, Second Edition" by Eric Lengyel,
        // Listing 14.6 (pages 441-444).

        final double EPSILON = 1.0e-10;
        final int MAX_SWEEPS = 32;

        // Since the Matrix is symmetric, m12=m21, m13=m31, and m23=m32. Therefore we can ignore the values m21, m31,
        // and m32.
        double m11 = matrix.m11;
        double m12 = matrix.m12;
        double m13 = matrix.m13;
        double m22 = matrix.m22;
        double m23 = matrix.m23;
        double m33 = matrix.m33;

        double[][] r = new double[3][3];
        r[0][0] = r[1][1] = r[2][2] = 1d;

        for (int a = 0; a < MAX_SWEEPS; a++)
        {
            // Exit if off-diagonal entries small enough
            if ((Math.abs(m12) < EPSILON) && (Math.abs(m13) < EPSILON) && (Math.abs(m23) < EPSILON))
                break;

            // Annihilate (1,2) entry
            if (m12 != 0d)
            {
                double u = (m22 - m11) * 0.5 / m12;
                double u2 = u * u;
                double u2p1 = u2 + 1d;
                double t = (u2p1 != u2) ?
                    ((u < 0d) ? -1d : 1d) * (Math.sqrt(u2p1) - Math.abs(u))
                    : 0.5 / u;
                double c = 1d / Math.sqrt(t * t + 1d);
                double s = c * t;

                m11 -= t * m12;
                m22 += t * m12;
                m12 = 0d;

                double temp = c * m13 - s * m23;
                m23 = s * m13 + c * m23;
                m13 = temp;

                for (int i = 0; i < 3; i++)
                {
                    temp = c * r[i][0] - s * r[i][1];
                    r[i][1] = s * r[i][0] + c * r[i][1];
                    r[i][0] = temp;
                }
            }

            // Annihilate (1,3) entry
            if (m13 != 0d)
            {
                double u = (m33 - m11) * 0.5 / m13;
                double u2 = u * u;
                double u2p1 = u2 + 1d;
                double t = (u2p1 != u2) ?
                    ((u < 0d) ? -1d : 1d) * (Math.sqrt(u2p1) - Math.abs(u))
                    : 0.5 / u;
                double c = 1d / Math.sqrt(t * t + 1d);
                double s = c * t;

                m11 -= t * m13;
                m33 += t * m13;
                m13 = 0d;

                double temp = c * m12 - s * m23;
                m23 = s * m12 + c * m23;
                m12 = temp;

                for (int i = 0; i < 3; i++)
                {
                    temp = c * r[i][0] - s * r[i][2];
                    r[i][2] = s * r[i][0] + c * r[i][2];
                    r[i][0] = temp;
                }
            }

            // Annihilate (2,3) entry
            if (m23 != 0d)
            {
                double u = (m33 - m22) * 0.5 / m23;
                double u2 = u * u;
                double u2p1 = u2 + 1d;
                double t = (u2p1 != u2) ?
                    ((u < 0d) ? -1d : 1d) * (Math.sqrt(u2p1) - Math.abs(u))
                    : 0.5 / u;
                double c = 1d / Math.sqrt(t * t + 1d);
                double s = c * t;

                m22 -= t * m23;
                m33 += t * m23;
                m23 = 0d;

                double temp = c * m12 - s * m13;
                m13 = s * m12 + c * m13;
                m12 = temp;

                for (int i = 0; i < 3; i++)
                {
                    temp = c * r[i][1] - s * r[i][2];
                    r[i][2] = s * r[i][1] + c * r[i][2];
                    r[i][1] = temp;
                }
            }
        }

        resultEigenvalues[0] = m11;
        resultEigenvalues[1] = m22;
        resultEigenvalues[2] = m33;

        resultEigenvectors[0] = new Vec4(r[0][0], r[1][0], r[2][0]);
        resultEigenvectors[1] = new Vec4(r[0][1], r[1][1], r[2][1]);
        resultEigenvectors[2] = new Vec4(r[0][2], r[1][2], r[2][2]);
    }

    public Matrix copy()
    {
        return new Matrix(
            this.m11, this.m12, this.m13, this.m14,  // Row 1
            this.m21, this.m22, this.m23, this.m24,  // Row 2
            this.m31, this.m32, this.m33, this.m34,  // Row 3
            this.m41, this.m42, this.m43, this.m44); // Row 4
    }

    public Matrix set(Matrix matrix)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Row 1
        this.m11 = matrix.m11;
        this.m12 = matrix.m12;
        this.m13 = matrix.m13;
        this.m14 = matrix.m14;
        // Row 2
        this.m21 = matrix.m21;
        this.m22 = matrix.m22;
        this.m23 = matrix.m23;
        this.m24 = matrix.m24;
        // Row 3
        this.m31 = matrix.m31;
        this.m32 = matrix.m32;
        this.m33 = matrix.m33;
        this.m34 = matrix.m34;
        // Row 4
        this.m41 = matrix.m41;
        this.m42 = matrix.m42;
        this.m43 = matrix.m43;
        this.m44 = matrix.m44;

        return this;
    }

    public Matrix set(
        double m11, double m12, double m13, double m14,
        double m21, double m22, double m23, double m24,
        double m31, double m32, double m33, double m34,
        double m41, double m42, double m43, double m44)
    {
        // Row 1
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m14 = m14;
        // Row 2
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m24 = m24;
        // Row 3
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        this.m34 = m34;
        // Row 4
        this.m41 = m41;
        this.m42 = m42;
        this.m43 = m43;
        this.m44 = m44;

        return this;
    }

    public Matrix setIdentity()
    {
        // Row 1
        this.m11 = 1;
        this.m12 = 0;
        this.m13 = 0;
        this.m14 = 0;
        // Row 2
        this.m21 = 0;
        this.m22 = 1;
        this.m23 = 0;
        this.m24 = 0;
        // Row 3
        this.m31 = 0;
        this.m32 = 0;
        this.m33 = 1;
        this.m34 = 0;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    public Matrix setInverse(Matrix matrix)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double cf_11 = matrix.m22 * (matrix.m33 * matrix.m44 - matrix.m43 * matrix.m34)
            - matrix.m23 * (matrix.m32 * matrix.m44 - matrix.m42 * matrix.m34)
            + matrix.m24 * (matrix.m32 * matrix.m43 - matrix.m42 * matrix.m33);
        double cf_12 = -(matrix.m21 * (matrix.m33 * matrix.m44 - matrix.m43 * matrix.m34)
            - matrix.m23 * (matrix.m31 * matrix.m44 - matrix.m41 * matrix.m34)
            + matrix.m24 * (matrix.m31 * matrix.m43 - matrix.m41 * matrix.m33));
        double cf_13 = matrix.m21 * (matrix.m32 * matrix.m44 - matrix.m42 * matrix.m34)
            - matrix.m22 * (matrix.m31 * matrix.m44 - matrix.m41 * matrix.m34)
            + matrix.m24 * (matrix.m31 * matrix.m42 - matrix.m41 * matrix.m32);
        double cf_14 = -(matrix.m21 * (matrix.m32 * matrix.m43 - matrix.m42 - matrix.m33)
            - matrix.m22 * (matrix.m31 * matrix.m43 - matrix.m41 * matrix.m33)
            + matrix.m23 * (matrix.m31 * matrix.m42 - matrix.m41 * matrix.m32));
        double cf_21 = matrix.m12 * (matrix.m33 * matrix.m44 - matrix.m43 - matrix.m34)
            - matrix.m13 * (matrix.m32 * matrix.m44 - matrix.m42 * matrix.m34)
            + matrix.m14 * (matrix.m32 * matrix.m43 - matrix.m42 * matrix.m33);
        double cf_22 = -(matrix.m11 * (matrix.m33 * matrix.m44 - matrix.m43 * matrix.m34)
            - matrix.m13 * (matrix.m31 * matrix.m44 - matrix.m41 * matrix.m34)
            + matrix.m14 * (matrix.m31 * matrix.m43 - matrix.m41 * matrix.m33));
        double cf_23 = matrix.m11 * (matrix.m32 * matrix.m44 - matrix.m42 * matrix.m34)
            - matrix.m12 * (matrix.m31 * matrix.m44 - matrix.m41 * matrix.m34)
            + matrix.m14 * (matrix.m31 * matrix.m42 - matrix.m41 * matrix.m32);
        double cf_24 = -(matrix.m11 * (matrix.m32 * matrix.m43 - matrix.m42 * matrix.m33)
            - matrix.m12 * (matrix.m31 * matrix.m43 - matrix.m41 * matrix.m33)
            + matrix.m13 * (matrix.m31 * matrix.m42 - matrix.m41 * matrix.m32));
        double cf_31 = matrix.m12 * (matrix.m23 * matrix.m44 - matrix.m43 * matrix.m24)
            - matrix.m13 * (matrix.m22 * matrix.m44 - matrix.m42 * matrix.m24)
            + matrix.m14 * (matrix.m22 * matrix.m43 - matrix.m42 * matrix.m23);
        double cf_32 = -(matrix.m11 * (matrix.m23 * matrix.m44 - matrix.m43 * matrix.m24)
            - matrix.m13 * (matrix.m21 * matrix.m44 - matrix.m41 * matrix.m24)
            + matrix.m14 * (matrix.m24 * matrix.m43 - matrix.m41 * matrix.m23));
        double cf_33 = matrix.m11 * (matrix.m22 * matrix.m44 - matrix.m42 * matrix.m24)
            - matrix.m12 * (matrix.m21 * matrix.m44 - matrix.m41 * matrix.m24)
            + matrix.m14 * (matrix.m21 * matrix.m42 - matrix.m41 * matrix.m22);
        double cf_34 = -(matrix.m11 * (matrix.m22 * matrix.m33 - matrix.m32 * matrix.m23)
            - matrix.m12 * (matrix.m21 * matrix.m33 - matrix.m31 * matrix.m23)
            + matrix.m13 * (matrix.m21 * matrix.m32 - matrix.m31 * matrix.m22));
        double cf_41 = matrix.m12 * (matrix.m23 * matrix.m34 - matrix.m33 * matrix.m24)
            - matrix.m13 * (matrix.m22 * matrix.m34 - matrix.m32 * matrix.m24)
            + matrix.m14 * (matrix.m22 * matrix.m33 - matrix.m32 * matrix.m23);
        double cf_42 = -(matrix.m11 * (matrix.m23 * matrix.m34 - matrix.m33 * matrix.m24)
            - matrix.m13 * (matrix.m21 * matrix.m34 - matrix.m31 * matrix.m24)
            + matrix.m14 * (matrix.m21 * matrix.m33 - matrix.m31 * matrix.m23));
        double cf_43 = matrix.m11 * (matrix.m22 * matrix.m34 - matrix.m32 * matrix.m24)
            - matrix.m12 * (matrix.m21 * matrix.m34 - matrix.m31 * matrix.m24)
            + matrix.m14 * (matrix.m21 * matrix.m32 - matrix.m31 * matrix.m22);
        double cf_44 = -(matrix.m11 * (matrix.m22 * matrix.m33 - matrix.m32 * matrix.m23)
            - matrix.m12 * (matrix.m21 * matrix.m33 - matrix.m31 * matrix.m23)
            + matrix.m13 * (matrix.m21 * matrix.m32 - matrix.m31 * matrix.m22));

        double det = (matrix.m11 * cf_11) + (matrix.m12 * cf_12) + (matrix.m13 * cf_13) + (matrix.m14 * cf_14);
        if (det == 0)
        {
            String msg = Logging.getMessage("Matrix.DeterminantIsZero");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Row 1
        this.m11 = cf_11 / det;
        this.m12 = cf_21 / det;
        this.m13 = cf_31 / det;
        this.m14 = cf_41 / det;
        // Row 2
        this.m21 = cf_12 / det;
        this.m22 = cf_22 / det;
        this.m23 = cf_32 / det;
        this.m24 = cf_42 / det;
        // Row 3
        this.m31 = cf_13 / det;
        this.m32 = cf_23 / det;
        this.m33 = cf_33 / det;
        this.m34 = cf_43 / det;
        // Row 4
        this.m41 = cf_14 / det;
        this.m42 = cf_24 / det;
        this.m43 = cf_34 / det;
        this.m44 = cf_44 / det;

        return this;
    }

    public Matrix setInverseTransformMatrix(Matrix matrix)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // The specified matrix is assumed to represent an orthonormal transform matrix. The specified matrix's
        // upper-3x3 is transposed, then its fourth column is transformed by its inverted-upper-3x3 and negated.

        double tx = -(matrix.m11 * matrix.m14) - (matrix.m21 * matrix.m24) - (matrix.m31 * matrix.m34);
        double ty = -(matrix.m12 * matrix.m14) - (matrix.m22 * matrix.m24) - (matrix.m32 * matrix.m34);
        double tz = -(matrix.m13 * matrix.m14) - (matrix.m23 * matrix.m24) - (matrix.m33 * matrix.m34);

        // Row 1
        this.m11 = matrix.m11;
        this.m12 = matrix.m21;
        this.m13 = matrix.m31;
        this.m14 = tx;
        // Row 2
        this.m21 = matrix.m12;
        this.m22 = matrix.m22;
        this.m23 = matrix.m32;
        this.m24 = ty;
        // Row 3
        this.m31 = matrix.m13;
        this.m32 = matrix.m23;
        this.m33 = matrix.m33;
        this.m34 = tz;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    public Matrix setTranslation(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.setTranslation(vec.x, vec.y, vec.z);
    }

    public Matrix setTranslation(double x, double y, double z)
    {
        // Row 1
        this.m11 = 1;
        this.m12 = 0;
        this.m13 = 0;
        this.m14 = x;
        // Row 2
        this.m21 = 0;
        this.m22 = 1;
        this.m23 = 0;
        this.m24 = y;
        // Row 3
        this.m31 = 0;
        this.m32 = 0;
        this.m33 = 1;
        this.m34 = z;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    public Matrix setScale(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.setScale(vec.x, vec.y, vec.z);
    }

    public Matrix setScale(double x, double y, double z)
    {
        // Row 1
        this.m11 = x;
        this.m12 = 0;
        this.m13 = 0;
        this.m14 = 0;
        // Row 2
        this.m21 = 0;
        this.m22 = y;
        this.m23 = 0;
        this.m24 = 0;
        // Row 3
        this.m31 = 0;
        this.m32 = 0;
        this.m33 = z;
        this.m34 = 0;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    public Matrix setRotationX(Angle angle)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double c = angle.cos();
        double s = angle.sin();

        // Row 1
        this.m11 = 1;
        this.m12 = 0;
        this.m13 = 0;
        this.m14 = 0;
        // Row 2
        this.m21 = 0;
        this.m22 = c;
        this.m23 = -s;
        this.m24 = 0;
        // Row 3
        this.m31 = 0;
        this.m32 = s;
        this.m33 = c;
        this.m34 = 0;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    public Matrix setRotationY(Angle angle)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double c = angle.cos();
        double s = angle.sin();

        // Row 1
        this.m11 = c;
        this.m12 = 0;
        this.m13 = s;
        this.m14 = 0;
        // Row 2
        this.m21 = 0;
        this.m22 = 1;
        this.m23 = 0;
        this.m24 = 0;
        // Row 3
        this.m31 = -s;
        this.m32 = 0;
        this.m33 = c;
        this.m34 = 0;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    public Matrix setRotationZ(Angle angle)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double c = angle.cos();
        double s = angle.sin();

        // Row 1
        this.m11 = c;
        this.m12 = -s;
        this.m13 = 0;
        this.m14 = 0;
        // Row 2
        this.m21 = s;
        this.m22 = c;
        this.m23 = 0;
        this.m24 = 0;
        // Row 3
        this.m31 = 0;
        this.m32 = 0;
        this.m33 = 1;
        this.m34 = 0;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    public Matrix setAxisAngle(Angle angle, Vec4 axis)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (axis == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.setAxisAngle(angle, axis.x, axis.y, axis.z);
    }

    public Matrix setAxisAngle(Angle angle, double x, double y, double z)
    {
        if (angle == null)
        {
            String msg = Logging.getMessage("nullValue.AngleIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double c = angle.cos();
        double s = angle.sin();
        double one_minus_c = 1.0 - c;

        // Row 1
        this.m11 = c + (one_minus_c * x * x);
        this.m12 = (one_minus_c * x * y) - (s * z);
        this.m13 = (one_minus_c * x * z) + (s * y);
        this.m14 = 0;
        // Row 2
        this.m21 = (one_minus_c * x * y) + (s * z);
        this.m22 = c + (one_minus_c * y * y);
        this.m23 = (one_minus_c * y * z) - (s * x);
        this.m24 = 0;
        // Row 3
        this.m31 = (one_minus_c * x * z) - (s * y);
        this.m32 = (one_minus_c * y * z) + (s * x);
        this.m33 = c + (one_minus_c * z * z);
        this.m34 = 0;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    /**
     * Returns a viewing matrix in model coordinates defined by the specified View eye point, reference point indicating
     * the center of the scene, and up vector. The eye point, center point, and up vector are in model coordinates. The
     * returned viewing matrix maps the reference center point to the negative Z axis, and the eye point to the origin,
     * and the up vector to the positive Y axis. When this matrix is used to define an OGL viewing transform along with
     * a typical projection matrix such as {@link #setPerspective(Angle, double, double, double, double)} , this maps
     * the center of the scene to the center of the viewport, and maps the up vector to the viewoport's positive Y axis
     * (the up vector points up in the viewport). The eye point and reference center point must not be coincident, and
     * the up vector must not be parallel to the line of sight (the vector from the eye point to the reference center
     * point).
     *
     * @param eye    the eye point, in model coordinates.
     * @param center the scene's reference center point, in model coordinates.
     * @param up     the direction of the up vector, in model coordinates.
     *
     * @return a viewing matrix in model coordinates defined by the specified eye point, reference center point, and up
     *         vector.
     *
     * @throws IllegalArgumentException if any of the eye point, reference center point, or up vector are null, if the
     *                                  eye point and reference center point are coincident, or if the up vector and the
     *                                  line of sight are parallel.
     */
    public Matrix setLookAt(Vec4 eye, Vec4 center, Vec4 up)
    {
        if (eye == null)
        {
            String msg = Logging.getMessage("nullValue.EyeIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (center == null)
        {
            String msg = Logging.getMessage("nullValue.CenterIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (up == null)
        {
            String msg = Logging.getMessage("nullValue.UpIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Compute orthogonal forward, side, and up vectors from the specified eye, center, and up points. The computed
        // forward vector always points from the eye to the center, the side is the always orthogonal to the computed
        // forward and specified up vector, and the computed up vector is always orthogonal to the computed side and the
        // computed up. The computed up vector is not equivalent to the specified up vector if the specified up vector
        // is not orthogonal to the computed forward vector.

        Vec4 f = center.subtract3(eye);
        f.normalize3AndSet();

        Vec4 s = f.subtract3(up);
        s.normalize3AndSet();

        Vec4 u = s.cross3(f);
        u.normalize3AndSet();

        // Set this matrix to translate model coordinates into the eye's coordinate space. The eye's coordinate space
        // places the eye point at the origin, looking down the negative Z axis with the Y axis pointing up. This is
        // equivalent to:
        //
        // Matrix m = new Matrix(s.x, s.y, s.z, 0, u.x, u.y, u.z, 0, -f.x, -f.y, -f.z, 0, 0, 0, 0, 1);
        // Matrix eye = Matrix.fromIdentity().setTranslation(-eye.x, -eye.y, -eye.z);
        // Matrix lookAt = Matrix.fromIdentity();
        // this.multiplyAndSet(m, eye);
        //
        // We use the shorthand version below to avoid two matrix allocations and one matrix multiplication.

        // Row 1
        this.m11 = s.x;
        this.m12 = s.y;
        this.m13 = s.z;
        this.m14 = -s.x * eye.x - s.y * eye.y - s.z * eye.z;
        // Row 2
        this.m21 = u.x;
        this.m22 = u.y;
        this.m23 = u.z;
        this.m24 = -u.x * eye.x - u.y * eye.y - u.z * eye.z;
        // Row 3
        this.m31 = -f.x;
        this.m32 = -f.y;
        this.m33 = -f.z;
        this.m34 = f.x * eye.x + f.y * eye.y + f.z * eye.z;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    public Matrix setPerspective(Angle horizontalFieldOfView, double viewportWidth, double viewportHeight, double near,
        double far)
    {
        if (horizontalFieldOfView == null)
        {
            String msg = Logging.getMessage("nullValue.FieldOfViewIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (horizontalFieldOfView.degrees <= 0 || horizontalFieldOfView.degrees > 180)
        {
            String msg = Logging.getMessage("generic.FieldOfViewIsInvalid", horizontalFieldOfView);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (viewportWidth < 0)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", viewportWidth);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (viewportHeight < 0)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", viewportHeight);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (near <= 0 || near > far)
        {
            String msg = Logging.getMessage("generic.ClipDistancesAreInvalid", near, far);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Based on http://www.opengl.org/resources/faq/technical/transformations.htm#tran0085.
        // This method uses horizontal field-of-view here to describe the perspective viewing angle. This results in a
        // different set of clip plane distances than documented in sources using vertical field-of-view.

        if (viewportWidth == 0)
            viewportWidth = 1;

        if (viewportHeight == 0)
            viewportHeight = 1;

        if (near == far)
            far = near + 1;

        double right = near * horizontalFieldOfView.tanHalfAngle();
        double left = -right;
        double top = right * viewportHeight / viewportWidth;
        double bottom = -top;

        this.setPerspective(left, right, bottom, top, near, far);

        return this;
    }

    public Matrix setPerspective(double left, double right, double bottom, double top, double near, double far)
    {
        if (left > right)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", right - left);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (bottom > top)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", top - bottom);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (near <= 0 || near > far)
        {
            String msg = Logging.getMessage("generic.ClipDistancesAreInvalid", near, far);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Taken from "Mathematics for 3D Game Programming and Computer Graphics", page 130.

        if (left == right)
            right = left + 1;

        if (bottom == top)
            top = bottom + 1;

        if (near == far)
            far = near + 1;

        // Row 1
        this.m11 = 2 * near / (right - left);
        this.m12 = 0;
        this.m13 = (right + left) / (right - left);
        this.m14 = 0;
        // Row 2
        this.m21 = 0;
        this.m22 = 2 * near / (top - bottom);
        this.m23 = (top + bottom) / (top - bottom);
        this.m24 = 0;
        // Row 3
        this.m31 = 0;
        this.m32 = 0;
        this.m33 = -(far + near) / (far - near);
        this.m34 = -2 * near * far / (far - near);
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = -1;
        this.m44 = 0;

        return this;
    }

    public Matrix setOrthographic2D(double viewportWidth, double viewportHeight)
    {
        if (viewportWidth < 0)
        {
            String msg = Logging.getMessage("generic.WidthIsInvalid", viewportWidth);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (viewportHeight < 0)
        {
            String msg = Logging.getMessage("generic.HeightIsInvalid", viewportHeight);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (viewportWidth == 0)
            viewportWidth = 1;

        if (viewportHeight == 0)
            viewportHeight = 1;

        // Row 1
        this.m11 = 2 / viewportWidth;
        this.m12 = 0;
        this.m13 = 0;
        this.m14 = 0;
        // Row 2
        this.m21 = 0;
        this.m22 = 2 / viewportHeight;
        this.m23 = 0;
        this.m24 = 0;
        // Row 3
        this.m31 = 0;
        this.m32 = 0;
        this.m33 = -1;
        this.m34 = 0;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 1;

        return this;
    }

    /**
     * Computes a symmetric covariance Matrix from the x, y, z coordinates of the specified points Iterable. This
     * returns null if the points Iterable is empty, or if all of the points are null.
     * <p/>
     * The returned covariance matrix represents the correlation between each pair of x-, y-, and z-coordinates as
     * they're distributed about the point Iterable's arithmetic mean. Its layout is as follows:
     * <p/>
     * <code> C(x, x)  C(x, y)  C(x, z) <br/> C(x, y)  C(y, y)  C(y, z) <br/> C(x, z)  C(y, z)  C(z, z) </code>
     * <p/>
     * C(i, j) is the covariance of coordinates i and j, where i or j are a coordinate's dispersion about its mean
     * value. If any entry is zero, then there's no correlation between the two coordinates defining that entry. If the
     * returned matrix is diagonal, then all three coordinates are uncorrelated, and the specified point Iterable is
     * distributed evenly about its mean point.
     *
     * @param points the Iterable of points for which to compute a Covariance matrix.
     *
     * @return the covariance matrix for the iterable of 3D points.
     *
     * @throws IllegalArgumentException if the points Iterable is null.
     */
    public Matrix setCovarianceOfPoints(Iterable<? extends Vec4> points)
    {
        if (points == null)
        {
            String msg = Logging.getMessage("nullValue.PointListIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        Vec4 mean = Vec4.computeAverage3(points);
        if (mean == null)
            return null;

        int count = 0;
        double c11 = 0d;
        double c22 = 0d;
        double c33 = 0d;
        double c12 = 0d;
        double c13 = 0d;
        double c23 = 0d;

        for (Vec4 vec : points)
        {
            if (vec == null)
                continue;

            count++;
            c11 += (vec.x - mean.x) * (vec.x - mean.x);
            c22 += (vec.y - mean.y) * (vec.y - mean.y);
            c33 += (vec.z - mean.z) * (vec.z - mean.z);
            c12 += (vec.x - mean.x) * (vec.y - mean.y); // c12 = c21
            c13 += (vec.x - mean.x) * (vec.z - mean.z); // c13 = c31
            c23 += (vec.y - mean.y) * (vec.z - mean.z); // c23 = c32
        }

        if (count == 0)
            return null;

        // Row 1
        this.m11 = c11 / (double) count;
        this.m12 = c12 / (double) count;
        this.m13 = c13 / (double) count;
        this.m14 = 0;
        // Row 2
        this.m21 = c12 / (double) count;
        this.m22 = c22 / (double) count;
        this.m23 = c23 / (double) count;
        this.m24 = 0;
        // Row 3
        this.m31 = c13 / (double) count;
        this.m32 = c23 / (double) count;
        this.m33 = c33 / (double) count;
        this.m34 = 0;
        // Row 4
        this.m41 = 0;
        this.m42 = 0;
        this.m43 = 0;
        this.m44 = 0;

        return this;
    }

    // Cartesian Android Model

    public final Angle getCAMRotationX()    // assumes the order of rotations is YXZ, positive CW
    {
        double xRadians = Math.asin(-this.m23);
        if (Double.isNaN(xRadians))
            return null;

        return Angle.fromRadians(-xRadians);    // negate to make angle CW
    }

    public final Angle getCAMRotationY()    // assumes the order of rotations is YXZ, positive CW
    {
        double xRadians = Math.asin(-this.m23);
        if (Double.isNaN(xRadians))
            return null;

        double yRadians;
        if (xRadians < Math.PI / 2)
        {
            if (xRadians > -Math.PI / 2)
            {
                yRadians = Math.atan2(this.m13, this.m33);
            }
            else
            {
                yRadians = -Math.atan2(-this.m12, this.m11);
            }
        }
        else
        {
            yRadians = Math.atan2(-this.m12, this.m11);
        }

        if (Double.isNaN(yRadians))
            return null;

        return Angle.fromRadians(-yRadians);    // negate angle to make it CW
    }

    public final Angle getCAMRotationZ()    //  assumes the order of rotations is YXZ, positive CW
    {
        double xRadians = Math.asin(-this.m23);
        if (Double.isNaN(xRadians))
            return null;

        double zRadians;
        if (xRadians < Math.PI / 2 && xRadians > -Math.PI / 2)
        {
            zRadians = Math.atan2(this.m21, this.m22);
        }
        else
        {
            zRadians = 0;
        }

        if (Double.isNaN(zRadians))
            return null;

        return Angle.fromRadians(-zRadians);    // negate angle to make it CW
    }

    public Matrix invert()
    {
        return this.copy().setInverse(this);
    }

    public Matrix invertTransformMatrix()
    {
        // This is assumed to represent an orthonormal transform matirx. This matrix's upper-3x3 is transposed, then its
        // fourth column is transformed by by inverted-upper-3x3 and negated.
        return this.copy().setInverseTransformMatrix(this);
    }

    public Matrix transpose()
    {
        // Swap m12 and m21.
        double tmp = this.m12;
        this.m12 = this.m21;
        this.m21 = tmp;

        // Swap m13 and m31.
        tmp = this.m13;
        this.m13 = this.m31;
        this.m31 = tmp;

        // Swap m14 and m41.
        tmp = this.m14;
        this.m14 = this.m41;
        this.m41 = tmp;

        // Swap m23 and m32.
        tmp = this.m23;
        this.m23 = this.m32;
        this.m32 = tmp;

        // Swap m24 and m42.
        tmp = this.m24;
        this.m24 = this.m42;
        this.m42 = tmp;

        // Swap m34 and m43.
        tmp = this.m34;
        this.m34 = this.m43;
        this.m43 = tmp;

        return this;
    }

    public Matrix transpose(Matrix matrix)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Row 1
        this.m11 = matrix.m11;
        this.m12 = matrix.m21;
        this.m13 = matrix.m31;
        this.m14 = matrix.m41;
        // Row 1
        this.m21 = matrix.m12;
        this.m22 = matrix.m22;
        this.m23 = matrix.m32;
        this.m24 = matrix.m42;
        // Row 1
        this.m31 = matrix.m13;
        this.m32 = matrix.m23;
        this.m33 = matrix.m33;
        this.m34 = matrix.m43;
        // Row 1
        this.m41 = matrix.m14;
        this.m42 = matrix.m24;
        this.m43 = matrix.m34;
        this.m44 = matrix.m44;

        return this;
    }

    public Matrix multiply(Matrix matrix)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.copy().multiplyAndSet(matrix);
    }

    public Matrix multiply(
        double m11, double m12, double m13, double m14,
        double m21, double m22, double m23, double m24,
        double m31, double m32, double m33, double m34,
        double m41, double m42, double m43, double m44)
    {
        return this.copy().multiplyAndSet(
            m11, m12, m13, m14, // Row 1
            m21, m22, m23, m24, // Row 2
            m31, m32, m33, m34, // Row 3
            m41, m42, m43, m44); // Row 4
    }

    public Matrix multiplyAndSet(Matrix matrix)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Row 1
        double a = this.m11;
        double b = this.m12;
        double c = this.m13;
        double d = this.m14;
        this.m11 = (a * matrix.m11) + (b * matrix.m21) + (c * matrix.m31) + (d * matrix.m41);
        this.m12 = (a * matrix.m12) + (b * matrix.m22) + (c * matrix.m32) + (d * matrix.m42);
        this.m13 = (a * matrix.m13) + (b * matrix.m23) + (c * matrix.m33) + (d * matrix.m43);
        this.m14 = (a * matrix.m14) + (b * matrix.m24) + (c * matrix.m34) + (d * matrix.m44);

        // Row 2
        a = this.m21;
        b = this.m22;
        c = this.m23;
        d = this.m24;
        this.m21 = (a * matrix.m11) + (b * matrix.m21) + (c * matrix.m31) + (d * matrix.m41);
        this.m22 = (a * matrix.m12) + (b * matrix.m22) + (c * matrix.m32) + (d * matrix.m42);
        this.m23 = (a * matrix.m13) + (b * matrix.m23) + (c * matrix.m33) + (d * matrix.m43);
        this.m24 = (a * matrix.m14) + (b * matrix.m24) + (c * matrix.m34) + (d * matrix.m44);

        // Row 3
        a = this.m31;
        b = this.m32;
        c = this.m33;
        d = this.m34;
        this.m31 = (a * matrix.m11) + (b * matrix.m21) + (c * matrix.m31) + (d * matrix.m41);
        this.m32 = (a * matrix.m12) + (b * matrix.m22) + (c * matrix.m32) + (d * matrix.m42);
        this.m33 = (a * matrix.m13) + (b * matrix.m23) + (c * matrix.m33) + (d * matrix.m43);
        this.m34 = (a * matrix.m14) + (b * matrix.m24) + (c * matrix.m34) + (d * matrix.m44);

        // Row 4
        a = this.m41;
        b = this.m42;
        c = this.m43;
        d = this.m44;
        this.m41 = (a * matrix.m11) + (b * matrix.m21) + (c * matrix.m31) + (d * matrix.m41);
        this.m42 = (a * matrix.m12) + (b * matrix.m22) + (c * matrix.m32) + (d * matrix.m42);
        this.m43 = (a * matrix.m13) + (b * matrix.m23) + (c * matrix.m33) + (d * matrix.m43);
        this.m44 = (a * matrix.m14) + (b * matrix.m24) + (c * matrix.m34) + (d * matrix.m44);

        return this;
    }

    public Matrix multiplyAndSet(
        double m11, double m12, double m13, double m14,
        double m21, double m22, double m23, double m24,
        double m31, double m32, double m33, double m34,
        double m41, double m42, double m43, double m44)
    {
        // Row 1
        double a = this.m11;
        double b = this.m12;
        double c = this.m13;
        double d = this.m14;
        this.m11 = (a * m11) + (b * m21) + (c * m31) + (d * m41);
        this.m12 = (a * m12) + (b * m22) + (c * m32) + (d * m42);
        this.m13 = (a * m13) + (b * m23) + (c * m33) + (d * m43);
        this.m14 = (a * m14) + (b * m24) + (c * m34) + (d * m44);

        // Row 2
        a = this.m21;
        b = this.m22;
        c = this.m23;
        d = this.m24;
        this.m21 = (a * m11) + (b * m21) + (c * m31) + (d * m41);
        this.m22 = (a * m12) + (b * m22) + (c * m32) + (d * m42);
        this.m23 = (a * m13) + (b * m23) + (c * m33) + (d * m43);
        this.m24 = (a * m14) + (b * m24) + (c * m34) + (d * m44);

        // Row 3
        a = this.m31;
        b = this.m32;
        c = this.m33;
        d = this.m34;
        this.m31 = (a * m11) + (b * m21) + (c * m31) + (d * m41);
        this.m32 = (a * m12) + (b * m22) + (c * m32) + (d * m42);
        this.m33 = (a * m13) + (b * m23) + (c * m33) + (d * m43);
        this.m34 = (a * m14) + (b * m24) + (c * m34) + (d * m44);

        // Row 4
        a = this.m41;
        b = this.m42;
        c = this.m43;
        d = this.m44;
        this.m41 = (a * m11) + (b * m21) + (c * m31) + (d * m41);
        this.m42 = (a * m12) + (b * m22) + (c * m32) + (d * m42);
        this.m43 = (a * m13) + (b * m23) + (c * m33) + (d * m43);
        this.m44 = (a * m14) + (b * m24) + (c * m34) + (d * m44);

        return this;
    }

    public Matrix multiplyAndSet(Matrix lhs, Matrix rhs)
    {
        if (lhs == null)
        {
            String msg = Logging.getMessage("nullValue.LhsIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (rhs == null)
        {
            String msg = Logging.getMessage("nullValue.RhsIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        // Row 1
        this.m11 = (lhs.m11 * rhs.m11) + (lhs.m12 * rhs.m21) + (lhs.m13 * rhs.m31) + (lhs.m14 * rhs.m41);
        this.m12 = (lhs.m11 * rhs.m12) + (lhs.m12 * rhs.m22) + (lhs.m13 * rhs.m32) + (lhs.m14 * rhs.m42);
        this.m13 = (lhs.m11 * rhs.m13) + (lhs.m12 * rhs.m23) + (lhs.m13 * rhs.m33) + (lhs.m14 * rhs.m43);
        this.m14 = (lhs.m11 * rhs.m14) + (lhs.m12 * rhs.m24) + (lhs.m13 * rhs.m34) + (lhs.m14 * rhs.m44);
        // Row 2
        this.m21 = (lhs.m21 * rhs.m11) + (lhs.m22 * rhs.m21) + (lhs.m23 * rhs.m31) + (lhs.m24 * rhs.m41);
        this.m22 = (lhs.m21 * rhs.m12) + (lhs.m22 * rhs.m22) + (lhs.m23 * rhs.m32) + (lhs.m24 * rhs.m42);
        this.m23 = (lhs.m21 * rhs.m13) + (lhs.m22 * rhs.m23) + (lhs.m23 * rhs.m33) + (lhs.m24 * rhs.m43);
        this.m24 = (lhs.m21 * rhs.m14) + (lhs.m22 * rhs.m24) + (lhs.m23 * rhs.m34) + (lhs.m24 * rhs.m44);
        // Row 3
        this.m31 = (lhs.m31 * rhs.m11) + (lhs.m32 * rhs.m21) + (lhs.m33 * rhs.m31) + (lhs.m34 * rhs.m41);
        this.m32 = (lhs.m31 * rhs.m12) + (lhs.m32 * rhs.m22) + (lhs.m33 * rhs.m32) + (lhs.m34 * rhs.m42);
        this.m33 = (lhs.m31 * rhs.m13) + (lhs.m32 * rhs.m23) + (lhs.m33 * rhs.m33) + (lhs.m34 * rhs.m43);
        this.m34 = (lhs.m31 * rhs.m14) + (lhs.m32 * rhs.m24) + (lhs.m33 * rhs.m34) + (lhs.m34 * rhs.m44);
        // Row 4
        this.m41 = (lhs.m41 * rhs.m11) + (lhs.m42 * rhs.m21) + (lhs.m43 * rhs.m31) + (lhs.m44 * rhs.m41);
        this.m42 = (lhs.m41 * rhs.m12) + (lhs.m42 * rhs.m22) + (lhs.m43 * rhs.m32) + (lhs.m44 * rhs.m42);
        this.m43 = (lhs.m41 * rhs.m13) + (lhs.m42 * rhs.m23) + (lhs.m43 * rhs.m33) + (lhs.m44 * rhs.m43);
        this.m44 = (lhs.m41 * rhs.m14) + (lhs.m42 * rhs.m24) + (lhs.m43 * rhs.m34) + (lhs.m44 * rhs.m44);

        return this;
    }

    // TODO: remove this
    public final float[] toArray(float[] compArray, int offset, boolean rowMajor)
    {
        if (compArray == null)
        {
            String msg = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if ((compArray.length - offset) < 16)         // 16 elements in a 4x4 matrix
        {
            String msg = Logging.getMessage("generic.ArrayInvalidLength", compArray.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (rowMajor)
        {
            // Row 1
            //noinspection PointlessArithmeticExpression
            compArray[0 + offset] = (float) this.m11;
            compArray[1 + offset] = (float) this.m12;
            compArray[2 + offset] = (float) this.m13;
            compArray[3 + offset] = (float) this.m14;
            // Row 2
            compArray[4 + offset] = (float) this.m21;
            compArray[5 + offset] = (float) this.m22;
            compArray[6 + offset] = (float) this.m23;
            compArray[7 + offset] = (float) this.m24;
            // Row 3
            compArray[8 + offset] = (float) this.m31;
            compArray[9 + offset] = (float) this.m32;
            compArray[10 + offset] = (float) this.m33;
            compArray[11 + offset] = (float) this.m34;
            // Row 4
            compArray[12 + offset] = (float) this.m41;
            compArray[13 + offset] = (float) this.m42;
            compArray[14 + offset] = (float) this.m43;
            compArray[15 + offset] = (float) this.m44;
        }
        else
        {
            // Row 1
            //noinspection PointlessArithmeticExpression
            compArray[0 + offset] = (float) this.m11;
            compArray[4 + offset] = (float) this.m12;
            compArray[8 + offset] = (float) this.m13;
            compArray[12 + offset] = (float) this.m14;
            // Row 2
            compArray[1 + offset] = (float) this.m21;
            compArray[5 + offset] = (float) this.m22;
            compArray[9 + offset] = (float) this.m23;
            compArray[13 + offset] = (float) this.m24;
            // Row 3
            compArray[2 + offset] = (float) this.m31;
            compArray[6 + offset] = (float) this.m32;
            compArray[10 + offset] = (float) this.m33;
            compArray[14 + offset] = (float) this.m34;
            // Row 4
            compArray[3 + offset] = (float) this.m41;
            compArray[7 + offset] = (float) this.m42;
            compArray[11 + offset] = (float) this.m43;
            compArray[15 + offset] = (float) this.m44;
        }

        return compArray;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Matrix that = (Matrix) o;
        return (this.m11 == that.m11) && (this.m12 == that.m12) && (this.m13 == that.m13) && (this.m14 == that.m14)
            && (this.m21 == that.m21) && (this.m22 == that.m22) && (this.m23 == that.m23) && (this.m24 == that.m24)
            && (this.m31 == that.m31) && (this.m32 == that.m32) && (this.m33 == that.m33) && (this.m34 == that.m34)
            && (this.m41 == that.m41) && (this.m42 == that.m42) && (this.m43 == that.m43) && (this.m44 == that.m44);
    }

    @Override
    public int hashCode()
    {
        int result;
        long tmp;
        tmp = Double.doubleToLongBits(this.m11);
        result = (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m12);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m13);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m14);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m21);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m22);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m23);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m24);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m31);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m32);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m33);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m34);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m41);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m42);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m43);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.m44);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        // Row 1
        sb.append(this.m11).append(", ");
        sb.append(this.m12).append(", ");
        sb.append(this.m13).append(", ");
        sb.append(this.m14).append(", ");
        // Row 2
        sb.append(this.m21).append(", ");
        sb.append(this.m22).append(", ");
        sb.append(this.m23).append(", ");
        sb.append(this.m24).append(", ");
        // Row 3
        sb.append(this.m31).append(", ");
        sb.append(this.m32).append(", ");
        sb.append(this.m33).append(", ");
        sb.append(this.m34).append(", ");
        // Row 4
        sb.append(this.m41).append(", ");
        sb.append(this.m42).append(", ");
        sb.append(this.m43).append(", ");
        sb.append(this.m44);
        sb.append(")");
        return sb.toString();
    }
}
