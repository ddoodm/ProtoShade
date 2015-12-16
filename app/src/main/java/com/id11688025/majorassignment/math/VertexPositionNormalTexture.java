package com.id11688025.majorassignment.math;

/**
 * A vertex with a 3D position, a 3D normal and a 2D texture coordinate.
 */
public class VertexPositionNormalTexture extends VertexPosition
{
    /** The normal to the surface composed by this vertex. */
    public Vector3 normal;

    /** The texture coordinate at this vertex. */
    public Vector2 texture;

    public VertexPositionNormalTexture (Vector3 position, Vector3 normal, Vector2 texture)
    {
        super(position);

        this.normal = normal;
        this. texture = texture;
    }

    @Override public float[] getArray ()
    {
        // Get arrays of each vertex component
        float[] positionArray = position.getArray();
        float[] normalArray = normal.getArray();
        float[] textureArray = texture.getArray();

        // Create an array with the total length of all components
        float[] result = new float[positionArray.length + normalArray.length + textureArray.length];
        int i=0;

        // Concatenate the arrays into the result array
        for(int j=0; j < positionArray.length; i++, j++)
            result[i] = positionArray[j];
        for(int j=0; j < normalArray.length; i++, j++)
            result[i] = normalArray[j];
        for(int j=0; j < textureArray.length; i++, j++)
            result[i] = textureArray[j];

        return result;
    }

    @Override public int getLengthInBytes()
    {
        return super.getLengthInBytes()
                + normal.getLengthInBytes()
                + texture.getLengthInBytes();
    }
}
