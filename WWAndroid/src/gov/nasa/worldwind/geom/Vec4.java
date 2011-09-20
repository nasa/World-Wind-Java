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
public class Vec4
{
    public double x;
    public double y;
    public double z;
    public double w;

    public Vec4()
    {
        this.w = 1;
    }

    public Vec4(double x, double y)
    {
        this.x = x;
        this.y = y;
        this.w = 1;
    }

    public Vec4(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public Vec4(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Vec4 fromLine3(Vec4 origin, double t, Vec4 direction)
    {
        if (origin == null || direction == null)
        {
            String msg = Logging.getMessage("nullValue.Vec4IsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new Vec4(
            origin.x + (direction.x * t),
            origin.y + (direction.y * t),
            origin.z + (direction.z * t));
    }

    public Vec4 copy()
    {
        return new Vec4(this.x, this.y, this.z, this.w);
    }

    public Vec4 set(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;

        return this;
    }

    public Vec4 set(double x, double y)
    {
        this.x = x;
        this.y = y;
        this.z = 0;
        this.w = 1;

        return this;
    }

    public Vec4 set(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;

        return this;
    }

    public Vec4 set(double x, double y, double z, double w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;

        return this;
    }

    public double getLength3()
    {
        return Math.sqrt(this.getLengthSquared3());
    }

    public double getLengthSquared3()
    {
        return this.x * this.x
            + this.y * this.y
            + this.z * this.z;
    }

    public Vec4 normalize3()
    {
        double length = this.getLength3();
        if (length == 0)
            return this; // Vector has zero length.

        return new Vec4(
            this.x / length,
            this.y / length,
            this.z / length);
    }

    public Vec4 normalize3AndSet()
    {
        double length = this.getLength3();
        if (length == 0)
            return this; // Vector has zero length.

        this.x /= length;
        this.y /= length;
        this.z /= length;

        return this;
    }

    public Vec4 normalize3AndSet(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double length = vec.getLength3();
        if (length == 0)
        {
            this.x = vec.x;
            this.y = vec.y;
            this.z = vec.z;
        }
        else
        {
            this.x = vec.x / length;
            this.y = vec.y / length;
            this.z = vec.z / length;
        }

        return this;
    }

    public Vec4 invert3()
    {
        return new Vec4(
            -this.x,
            -this.y,
            -this.z);
    }

    public Vec4 invert3AndSet()
    {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;

        return this;
    }

    public Vec4 abs3()
    {
        return new Vec4(
            Math.abs(this.x),
            Math.abs(this.y),
            Math.abs(this.z));
    }

    public Vec4 abs3AndSet()
    {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);

        return this;
    }

    public double dot3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.x * vec.x
            + this.y * vec.y
            + this.z * vec.z;
    }

    public double dot4(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return this.x * vec.x
            + this.y * vec.y
            + this.z * vec.z
            + this.w * vec.w;
    }

    public double dotSelf3()
    {
        return this.x * this.x
            + this.y * this.y
            + this.z * this.z;
    }

    public double dotSelf4()
    {
        return this.x * this.x
            + this.y * this.y
            + this.z * this.z
            + this.w * this.w;
    }

    public double distanceTo3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return Math.sqrt(this.distanceToSquared3(vec));
    }

    public double distanceToSquared3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double tmp;
        double result = 0.0;
        tmp = this.x - vec.x;
        result += tmp * tmp;
        tmp = this.y - vec.y;
        result += tmp * tmp;
        tmp = this.z - vec.z;
        result += tmp * tmp;
        return result;
    }

    public Angle angleBetween3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double a_dot_b = this.dot3(vec);
        // Compute the sum of magnitudes.
        double length = this.getLength3() * vec.getLength3();
        // Normalize the dot product, if necessary.
        if (!(length == 0) && (length != 1.0))
            a_dot_b /= length;

        // The normalized dot product should be in the range [-1, 1]. Otherwise the result is an error from floating
        // point roundoff. So if a_dot_b is less than -1 or greater than +1, we treat it as -1 and +1 respectively.
        if (a_dot_b < -1.0)
            a_dot_b = -1.0;
        else if (a_dot_b > 1.0)
            a_dot_b = 1.0;

        // Angle is arc-cosine of normalized dot product.
        return Angle.fromRadians(Math.acos(a_dot_b));
    }

    public Vec4 add3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new Vec4(
            this.x + vec.x,
            this.y + vec.y,
            this.z + vec.z);
    }

    public Vec4 add3(double x, double y, double z)
    {
        return new Vec4(
            this.x + x,
            this.y + y,
            this.z + z);
    }

    public Vec4 add3AndSet(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;

        return this;
    }

    public Vec4 add3AndSet(double x, double y, double z)
    {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public Vec4 add3AndSet(Vec4 lhs, Vec4 rhs)
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

        this.x = lhs.x + rhs.x;
        this.y = lhs.y + rhs.y;
        this.z = lhs.z + rhs.z;

        return this;
    }

    public Vec4 subtract3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new Vec4(
            this.x - vec.x,
            this.y - vec.y,
            this.z - vec.z);
    }

    public Vec4 subtract3(double x, double y, double z)
    {
        return new Vec4(
            this.x - x,
            this.y - y,
            this.z - z);
    }

    public Vec4 subtract3AndSet(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;

        return this;
    }

    public Vec4 subtract3AndSet(double x, double y, double z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;

        return this;
    }

    public Vec4 subtract3AndSet(Vec4 lhs, Vec4 rhs)
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

        this.x = lhs.x - rhs.x;
        this.y = lhs.y - rhs.y;
        this.z = lhs.z - rhs.z;

        return this;
    }

    public Vec4 multiply3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new Vec4(
            this.x * vec.x,
            this.y * vec.y,
            this.z * vec.z);
    }

    public Vec4 multiply3(double value)
    {
        return new Vec4(
            this.x * value,
            this.y * value,
            this.z * value);
    }

    public Vec4 multiply3AndSet(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;

        return this;
    }

    public Vec4 multiply3AndSet(double value)
    {
        this.x *= value;
        this.y *= value;
        this.z *= value;

        return this;
    }

    public Vec4 multiply3AndSet(Vec4 lhs, Vec4 rhs)
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

        this.x = lhs.x * rhs.x;
        this.y = lhs.y * rhs.y;
        this.z = lhs.z * rhs.z;

        return this;
    }

    public Vec4 divide3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new Vec4(
            this.x / vec.x,
            this.y / vec.y,
            this.z / vec.z);
    }

    public Vec4 divide3(double value)
    {
        if (value == 0)
        {
            String msg = Logging.getMessage("generic.DivideByZero");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new Vec4(
            this.x / value,
            this.y / value,
            this.z / value);
    }

    public Vec4 divide3AndSet(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.x /= vec.x;
        this.y /= vec.y;
        this.z /= vec.z;

        return this;
    }

    public Vec4 divide3AndSet(double value)
    {
        this.x /= value;
        this.y /= value;
        this.z /= value;

        return this;
    }

    public Vec4 divide3AndSet(Vec4 lhs, Vec4 rhs)
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

        this.x = lhs.x / rhs.x;
        this.y = lhs.y / rhs.y;
        this.z = lhs.z / rhs.z;

        return this;
    }

    /**
     * Returns the arithmetic mean of the x, y, z coordinates of the specified points Iterable. This returns null if the
     * Iterable contains no points, or if all of the points are null.
     *
     * @param points the Iterable of points which define the returned arithmetic mean.
     *
     * @return the arithmetic mean point of the specified points Iterable, or null if the Iterable is empty or contains
     *         only null points.
     *
     * @throws IllegalArgumentException if the Iterable is null.
     */
    public static Vec4 computeAverage3(Iterable<? extends Vec4> points)
    {
        if (points == null)
        {
            String msg = Logging.getMessage("nullValue.PointListIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        int count = 0;
        double x = 0;
        double y = 0;
        double z = 0;
        double w = 0;

        for (Vec4 vec : points)
        {
            if (vec == null)
                continue;

            count++;
            x += vec.x;
            y += vec.y;
            z += vec.z;
            w += vec.w;
        }

        if (count == 0)
            return null;

        return new Vec4(
            x / (double) count,
            y / (double) count,
            z / (double) count,
            w / (double) count);
    }

    /**
     * Computes the arithmetic mean of the x, y, z coordinates of the specified points Iterable and sets this vector to
     * the computed result. This does nothing if the Iterable contains no points, or if all of the points are null.
     *
     * @param points the Iterable of points which define the returned arithmetic mean.
     *
     * @return a reference to this vector.
     *
     * @throws IllegalArgumentException if the Iterable is null.
     */
    public Vec4 computeAverage3AndSet(Iterable<? extends Vec4> points)
    {
        if (points == null)
        {
            String msg = Logging.getMessage("nullValue.PointListIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        int count = 0;
        double x = 0;
        double y = 0;
        double z = 0;

        for (Vec4 vec : points)
        {
            if (vec == null)
                continue;

            count++;
            x += vec.x;
            y += vec.y;
            z += vec.z;
        }

        if (count > 0)
            return this;

        this.x = x / (double) count;
        this.y = y / (double) count;
        this.z = z / (double) count;

        return this;
    }

    public Vec4 cross3(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new Vec4(
            (this.y * vec.z) - (this.z * vec.y),
            (this.z * vec.x) - (this.x * vec.z),
            (this.x * vec.y) - (this.y * vec.x));
    }

    public Vec4 cross3AndSet(Vec4 vec)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.LhsIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double x = this.x;
        double y = this.y;
        double z = this.z;

        this.x = (y * vec.z) - (z * vec.y);
        this.y = (z * vec.x) - (x * vec.z);
        this.z = (x * vec.y) - (y * vec.x);

        return this;
    }

    public Vec4 cross3AndSet(Vec4 lhs, Vec4 rhs)
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

        this.x = (lhs.y * rhs.z) - (lhs.z * rhs.y);
        this.y = (lhs.z * rhs.x) - (lhs.x * rhs.z);
        this.z = (lhs.x * rhs.y) - (lhs.y * rhs.x);

        return this;
    }

    public Vec4 transformBy4(Matrix matrix)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return new Vec4(
            (matrix.m11 * this.x) + (matrix.m12 * this.y) + (matrix.m13 * this.z) + (matrix.m14 * this.w),
            (matrix.m21 * this.x) + (matrix.m22 * this.y) + (matrix.m23 * this.z) + (matrix.m24 * this.w),
            (matrix.m31 * this.x) + (matrix.m32 * this.y) + (matrix.m33 * this.z) + (matrix.m34 * this.w),
            (matrix.m41 * this.x) + (matrix.m42 * this.y) + (matrix.m43 * this.z) + (matrix.m44 * this.w));
    }

    public Vec4 transformBy4AndSet(Matrix matrix)
    {
        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double x = this.x;
        double y = this.y;
        double z = this.z;
        double w = this.w;

        this.x = (matrix.m11 * x) + (matrix.m12 * y) + (matrix.m13 * z) + (matrix.m14 * w);
        this.y = (matrix.m21 * x) + (matrix.m22 * y) + (matrix.m23 * z) + (matrix.m24 * w);
        this.z = (matrix.m31 * x) + (matrix.m32 * y) + (matrix.m33 * z) + (matrix.m34 * w);
        this.w = (matrix.m41 * x) + (matrix.m42 * y) + (matrix.m43 * z) + (matrix.m44 * w);

        return this;
    }

    public Vec4 transformBy4AndSet(Vec4 vec, Matrix matrix)
    {
        if (vec == null)
        {
            String msg = Logging.getMessage("nullValue.VectorIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (matrix == null)
        {
            String msg = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        this.x = (matrix.m11 * vec.x) + (matrix.m12 * vec.y) + (matrix.m13 * vec.z) + (matrix.m14 * vec.w);
        this.y = (matrix.m21 * vec.x) + (matrix.m22 * vec.y) + (matrix.m23 * vec.z) + (matrix.m24 * vec.w);
        this.z = (matrix.m31 * vec.x) + (matrix.m32 * vec.y) + (matrix.m33 * vec.z) + (matrix.m34 * vec.w);
        this.w = (matrix.m41 * vec.x) + (matrix.m42 * vec.y) + (matrix.m43 * vec.z) + (matrix.m44 * vec.w);

        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;

        Vec4 that = (Vec4) o;
        return this.x == that.x
            && this.y == that.y
            && this.z == that.z
            && this.w == that.w;
    }

    @Override
    public int hashCode()
    {
        int result;
        long tmp;
        tmp = Double.doubleToLongBits(this.x);
        result = (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.y);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.z);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        tmp = Double.doubleToLongBits(this.w);
        result = 29 * result + (int) (tmp ^ (tmp >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.x).append(", ");
        sb.append(this.y).append(", ");
        sb.append(this.z).append(", ");
        sb.append(this.w);
        sb.append(")");
        return sb.toString();
    }

    public void toArray3(float[] array, int offset)
    {
        if (array == null)
        {
            String msg = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (array.length < 3)
        {
            String msg = Logging.getMessage("generic.ArrayInvalidLength", array.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (offset < 0 || offset + 3 > array.length)
        {
            String msg = Logging.getMessage("generic.OffsetIsInvalid", offset);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        array[offset] = (float) this.x;
        array[offset + 1] = (float) this.y;
        array[offset + 2] = (float) this.z;
    }

    /**
     * Constructs a new Vec4 with coordinate values read from the specified float array. The specified offset must be 0
     * or greater, the specified length must be 1 or greater, and the array must have capacity equal to or greater than
     * <code>offset + length</code>. Coordinates are assigned as follows:<p><code>x = array[offset]</code><br/> <code>y
     * = array[offset + 1]</code> if <code>length > 1</code>, otherwise <code>y=0</code><br/><code>z = array[offset +
     * 2]</code> if <code>length > 2</code>, otherwise <code>z=0</code><br/><code>w = array[offset + 3]</code> if
     * <code>length > 3</code>, otherwise <code>w=1</code></p>
     *
     * @param array  the float array from which to read coordinate data.
     * @param offset the array starting index.
     * @param length the number of coordinates to read.
     *
     * @return a new Vec4 with coordinate values read from the specified double array.
     *
     * @throws IllegalArgumentException if the array is null, if offset is negative, if length is less than 1, or if the
     *                                  array's capacity is less than <code>offset + length</code>.
     */
    public static Vec4 fromFloatArray(float[] array, int offset, int length)
    {
        if (array == null)
        {
            String msg = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (offset < 0)
        {
            String msg = Logging.getMessage("generic.OffsetIsInvalid", offset);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (length < 1)
        {
            String msg = Logging.getMessage("generic.LengthIsInvalid", length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (array.length < offset + length)
        {
            String msg = Logging.getMessage("generic.ArrayInvalidLength", array.length);
            Logging.error(msg);
            throw new IllegalArgumentException(msg);
        }

        if (length == 2)
            return new Vec4(array[offset], array[offset + 1], 0d);

        if (length == 3)
            return new Vec4(array[offset], array[offset + 1], array[offset + 2]);

        return new Vec4(array[offset], array[offset + 1], array[offset + 2], array[offset + 3]);
    }
}
