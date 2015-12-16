package com.id11688025.majorassignment.math;

/**
 * A generic linear algebraic vector for 2D and 3D geometric computations.
 * {@link Vector2} and {@link Vector3} implement this interface.
 */
public interface Vector<T extends Vector<T>>
{
    /** @return A copy of this vector */
    T copy();

    /** @return The Euclidean magnitude (length) of this vector */
    float magnitude();

    /**
     * Useful for efficient computation where actual quantities are not required.
     * @return The vector magnitude without performing square root.
     */
    float magnitudeSquared();

    /** Reverse the signs of the vector in order to reverse it. */
    T negate();

    /** Set this vector to the content of another.
     * @param other The vector with the new data.
     * @return This vector.
     */
    T set(T other);

    /** Normalizes this vector.
     * @return The normalized vector.
     */
    T normalize();

    /** Add another vector to this vector. */
    T add(T other);

    /** Subtract another vector from this vector. */
    T subtract(T other);

    /** @return Multiply each vector component by each component of the vector supplied. */
    T scale(T other);

    /** Multiply this vector by a scalar */
    T scale(float scalar);

    /** @return The distance between this vector and the one supplied */
    float distance(T other);

    /** @return The length (in bytes) of this data structure */
    int getLengthInBytes();
}
