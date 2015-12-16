package com.id11688025.majorassignment.math;

/**
 * A vertex with a 3D position.
 */
public class VertexPosition
{
    /** The 3D position in Cartesian space */
    public Vector3 position;

    public VertexPosition(Vector3 position)
    {
        this.position = position;
    }

    /** Return the vertex as an array of floating-point data */
    public float[] getArray ()
    {
        return position.getArray();
    }

    /** Returns the length (in bytes) of this data structure. */
    public int getLengthInBytes()
    {
        return position.getLengthInBytes();
    }
}
