package com.id11688025.majorassignment.math;

/**
 * A vertex with a: 3D position, 3D normal, 3D bitangent
 * and tangent, and a 2D texture coordinate.
 */
public class VertexPositionNormalTextureTangent extends VertexPositionNormalTexture
{
    /** The u-aligned direction that defines the Tangent Reference Frame */
    public Vector3 tangent;

    /** The perpendicular direction to the tangent and surface normal */
    public Vector3 bitangent;

    public VertexPositionNormalTextureTangent(Vector3 position, Vector3 normal, Vector2 texture, Vector3 tangent, Vector3 bitangent) {
        super(position, normal, texture);

        this.tangent = tangent;
        this.bitangent = bitangent;
    }

    @Override public float[] getArray ()
    {
        // Get arrays for each vertex component
        float[] superArray = super.getArray();
        float[] tangentArray = tangent.getArray();
        float[] bitangentArray = bitangent.getArray();

        // Create an array with the total length of all components
        float[] result = new float[superArray.length + tangentArray.length + bitangentArray.length];
        int i=0;

        // Concatenate the arrays into the result array
        for(int j=0; j < superArray.length; i++, j++)
            result[i] = superArray[j];
        for(int j=0; j < tangentArray.length; i++, j++)
            result[i] = tangentArray[j];
        for(int j=0; j < bitangentArray.length; i++, j++)
            result[i] = bitangentArray[j];

        return result;
    }

    @Override public int getLengthInBytes()
    {
        return super.getLengthInBytes()
                + tangent.getLengthInBytes()
                + bitangent.getLengthInBytes();
    }
}
