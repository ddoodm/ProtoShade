package com.id11688025.majorassignment.math;

/**
 * A three-dimensional geometric vector.
 */
public class Vector3 implements Vector<Vector3>
{
    /** The number of Cartesian dimensions that this vector can represent */
    private static final int DIMENSIONS = 3;

    /** The X component of this vector in Cartesian space */
    public float x;
    /** The Y component of this vector in Cartesian space */
    public float y;
    /** The Z component of this vector in Cartesian space */
    public float z;

    /** A pre-made (0,0,0) vector */
    public static final Vector3 zero = new Vector3(0,0,0);
    /** A pre-made (1,1,1) vector */
    public static final Vector3 one = new Vector3(1,1,1);
    /** A pre-made Y-up (0,1,0) vector */
    public static final Vector3 up = new Vector3(0,1,0);

    /** Creates the vector (0,0,0) */
    public Vector3() {}

    /** Create a vector with the components supplied */
    public Vector3(final float x, final float y, final float z)
    {
        this.set(x,y,z);
    }

    /** Create a vector by copying another */
    public Vector3(Vector3 other)
    {
        this.set(other);
    }

    /** Create a vector with all components of the value provided */
    public Vector3(float values)
    {
        this.set(values);
    }

    /** Create a vector from a 3-element array of floats */
    public Vector3(float[] values)
    {
        this.set(values[0], values[1], values[2]);
    }

    @Override
    public Vector3 copy() {
        return new Vector3(this);
    }

    /** Obtain the vector's coordinates as a 3-element floating-point array */
    public float[] getArray()
    {
        return new float[] {x, y, z};
    }

    /**
     * Useful for efficient computation where actual quantities are not required.
     * @return The vector magnitude without performing square root.
     */
    public static float magnitudeSquared(Vector3 vector)
    {
        return vector.x * vector.x + vector.y * vector.y + vector.z * vector.z;
    }

    @Override
    public float magnitudeSquared() {
        return magnitudeSquared(this);
    }

    /** @return The Euclidean length of the supplied vector. */
    public static float magnitude(Vector3 vector)
    {
        // ( sqrt( x^2 + y^2 + z^2 ) )
        return (float)Math.sqrt(magnitudeSquared(vector));
    }

    @Override
    public float magnitude() {
        return magnitude(this);
    }

    /** @return The vector with its signs reversed. */
    public static Vector3 negate(Vector3 vector)
    {
        return new Vector3(-vector.x, -vector.y, -vector.z);
    }

    @Override
    public Vector3 negate() {
        return negate(this);
    }

    /** Supply this vector with the coordinates provided.
     * @param x The vector's X component.
     * @param y The vector's Y component.
     * @param z The vector's Z component.
     * @return This vector.
     */
    public Vector3 set(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /** Set each component of this vector to the value specified.
     * @param values The value to set all components.
     * @return This vector.
     */
    public Vector3 set(float values)
    {
        this.x = this.y = this.z = values;
        return this;
    }

    @Override
    public Vector3 set(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    /** Normalize the supplied vector */
    public static Vector3 normalize(Vector3 vector)
    {
        float magnitude = magnitude(vector);

        /*  If the length of the vector is already unit scaled (1),
            or zero (0), the vector is already normalized. */
        if(magnitude == 0 || magnitude == 1)
            return vector;

        // Otherwise, divide the vector by the magnitude
        return scale(1f / magnitude, vector);
    }

    @Override
    public Vector3 normalize() {
        return normalize(this);
    }

    /** Add a vector to another. */
    public static Vector3 add (Vector3 lhs, Vector3 rhs)
    {
        return new Vector3(lhs.x + rhs.x, lhs.y + rhs.y, lhs.z + rhs.z);
    }

    /** Add the vector defined by the components to this vector. */
    public Vector3 add(float x, float y, float z)
    {
        return new Vector3(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public Vector3 add(Vector3 other) {
        return add(this, other);
    }

    /** Subtract a vector from another (lhs - rhs) */
    public static Vector3 subtract(Vector3 lhs, Vector3 rhs)
    {
        return new Vector3(lhs.x - rhs.x, lhs.y - rhs.y, lhs.z - rhs.z);
    }

    /** Subtract the vector defined by the components from this vector. */
    public Vector3 subtract(float x, float y, float z)
    {
        return new Vector3(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public Vector3 subtract(Vector3 other) {
        return subtract(this, other);
    }

    /**
     * Scale a vector by another vector.
     * @param lhs Left-hand-sied vector.
     * @param rhs Right-hand-sied vector.
     * @return The scaled vector.
     */
    public static Vector3 scale(Vector3 lhs, Vector3 rhs)
    {
        return new Vector3(lhs.x * rhs.x, lhs.y * rhs.y, lhs.z * rhs.z);
    }

    @Override
    public Vector3 scale(Vector3 other) {
        return scale(this, other);
    }

    /**
     * Scale a vector by a scalar.
     * @param scalar A scalar value (multiplicand).
     * @param vector A vector to scale.
     * @return The scaled vector.
     */
    public static Vector3 scale(float scalar, Vector3 vector)
    {
        return new Vector3(vector.x * scalar, vector.y * scalar, vector.z * scalar);
    }

    @Override
    public Vector3 scale(float scalar) {
        return scale(scalar, this);
    }

    /** Vector cross product between two vectors.
     * @param lhs The left-hand-side operand.
     * @param rhs The right-hand-side operand.
     * @return The vector perpendicular to the two vectors */
    public static Vector3 cross(Vector3 lhs, Vector3 rhs)
    {
        // Though seemingly random, the pattern is
        // akin to the matrix determinant operation.
        return new Vector3(
                lhs.y * rhs.z - lhs.z * rhs.y,
                lhs.z * rhs.x - lhs.x * rhs.z,
                lhs.x * rhs.y - lhs.y * rhs.x
        );
    }

    /** Vector cross product between this vector and the other.
     * @param other The vector to compute the vector product between.
     * @return The vector perpendicular to the two vectors.
     */
    public Vector3 cross(Vector3 other)
    {
        return cross(this, other);
    }

    @Override
    public float distance(Vector3 other) {
        Vector3 delta = other.subtract(this);
        return delta.magnitude();
    }

    @Override
    public int getLengthInBytes() {
        // 4 is sizeof(float)
        return DIMENSIONS * 4;
    }
}
