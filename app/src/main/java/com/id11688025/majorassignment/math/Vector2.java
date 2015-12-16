package com.id11688025.majorassignment.math;

/**
 * A two-dimensional geometric vector.
 */
public class Vector2 implements Vector<Vector2>
{
    /** The number of Cartesian dimensions that this vector can represent */
    private static final int DIMENSIONS = 2;

    /** The X component of this vector in Cartesian space */
    public float x;
    /** The Y component of this vector in Cartesian space */
    public float y;

    /** A pre-made (0,0) vector */
    public static final Vector2 zero = new Vector2(0,0);
    /** A pre-made (1,1) vector */
    public static final Vector2 one = new Vector2(1,1);

    /** Create a new (0,0) vector */
    public Vector2 () { }

    /** Create a vector at (x,y) */
    public Vector2 (float x, float y)
    {
        this.set(x, y);
    }

    /** Create a vector by copying another */
    public Vector2 (Vector2 other)
    {
        this.set(other);
    }

    /** Create a vector with all components of the value provided */
    public Vector2(float values)
    {
        this.set(values);
    }

    @Override
    public Vector2 copy() {
        return new Vector2(this);
    }

    /** Obtain the vector's coordinates as a 2-element floating-point array */
    public float[] getArray()
    {
        return new float[] {x, y};
    }

    /**
     * Useful for efficient computation where actual quantities are not required.
     * @return The vector magnitude without performing square root.
     */
    public static float magnitudeSquared(Vector2 vector)
    {
        return vector.x * vector.x + vector.y * vector.y;
    }

    /** @return The Euclidean length of the supplied vector. */
    public static float magnitude(Vector2 vector)
    {
        // ( sqrt( x^2 + y^2 ) )
        return (float)Math.sqrt(magnitudeSquared(vector));
    }

    @Override
    public float magnitude() {
        return magnitude(this);
    }

    @Override
    public float magnitudeSquared() {
        return magnitudeSquared(this);
    }

    /** @return The vector with its signs reversed. */
    public static Vector2 negate(Vector2 vector)
    {
        return new Vector2(-vector.x, -vector.y);
    }

    @Override
    public Vector2 negate() {
        return negate(this);
    }

    /** Supply this vector with the coordinates provided.
     * @param x The vector's X component.
     * @param y The vector's Y component.
     * @return This vector.
     */
    public Vector2 set(float x, float y)
    {
        this.x = x;
        this.y = y;
        return this;
    }

    /** Set each component of this vector to the value specified.
     * @param values The value to set all components.
     * @return This vector.
     */
    public Vector2 set(float values)
    {
        this.x = this.y = values;
        return this;
    }

    @Override
    public Vector2 set(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    /** Normalize the supplied vector */
    public static Vector2 normalize(Vector2 vector)
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
    public Vector2 normalize() {
        return normalize(this);
    }

    /** Add a vector to another. */
    public static Vector2 add (Vector2 lhs, Vector2 rhs)
    {
        return new Vector2(lhs.x + rhs.x, lhs.y + rhs.y);
    }

    /** Add the vector defined by the components to this vector. */
    public Vector2 add(float x, float y)
    {
        return new Vector2(this.x + x, this.y + y);
    }

    @Override
    public Vector2 add(Vector2 other) {
        return add(this, other);
    }

    /** Subtract a vector from another (lhs - rhs) */
    public static Vector2 subtract(Vector2 lhs, Vector2 rhs)
    {
        return new Vector2(lhs.x - rhs.x, lhs.y - rhs.y);
    }

    /** Subtract the vector defined by the components from this vector. */
    public Vector2 subtract(float x, float y)
    {
        return new Vector2(this.x - x, this.y - y);
    }

    @Override
    public Vector2 subtract(Vector2 other) {
        return subtract(this, other);
    }

    /**
     * Scale a vector by another vector.
     * @param lhs Left-hand-sied vector.
     * @param rhs Right-hand-sied vector.
     * @return The scaled vector.
     */
    public static Vector2 scale(Vector2 lhs, Vector2 rhs)
    {
        return new Vector2(lhs.x * rhs.x, lhs.y * rhs.y);
    }

    @Override
    public Vector2 scale(Vector2 other) {
        return scale(this, other);
    }

    /**
     * Scale a vector by a scalar.
     * @param scalar A scalar value (multiplicand).
     * @param vector A vector to scale.
     * @return The scaled vector.
     */
    public static Vector2 scale(float scalar, Vector2 vector)
    {
        return new Vector2(vector.x * scalar, vector.y * scalar);
    }

    @Override
    public Vector2 scale(float scalar) {
        return scale(scalar, this);
    }

    @Override
    public float distance(Vector2 other) {
        Vector2 delta = other.subtract(this);
        return delta.magnitude();
    }

    @Override
    public int getLengthInBytes() {
        // Number of dimensions * 4-byte float
        return DIMENSIONS * 4;
    }
}
