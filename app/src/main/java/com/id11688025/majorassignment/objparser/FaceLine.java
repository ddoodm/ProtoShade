package com.id11688025.majorassignment.objparser;

import com.id11688025.majorassignment.math.Vector2;
import com.id11688025.majorassignment.math.Vector3;
import com.id11688025.majorassignment.math.VertexPositionNormalTextureTangent;

/**
 * An OBJ model line that links vertices, normals and texture coordinates into a face.
 */
public class FaceLine extends OBJLine
{
    /** The number of vertices that are allocated to a face */
    public static final int VERTS_PER_FACE = 3;

    /** The three positions that compose a face */
    private Vector3[] positions = new Vector3[VERTS_PER_FACE];

    /** The three normals that compose a face */
    private Vector3[] normals = new Vector3[VERTS_PER_FACE];

    /** The three texture coordinates that compose a face */
    private Vector3[] texCoords = new Vector3[VERTS_PER_FACE];

    public FaceLine(final LineType type, final String lineString, final OBJModel model) {
        super(type, lineString);
        this.parseVectors(model);
    }

    /**
     * Parse this line as a face that links vertices into faces.
     * @param model The OBJ model that already holds the vertex position, normal and texture data.
     */
    private void parseVectors(OBJModel model)
    {
        // Split the line into the three vertex strings
        String[] vertStrings = lineString.split("\\s+");

        // For each vertex tuple
        for(int i=0; i<vertStrings.length; i++)
        {
            // Each tuple component index (position, normal, texture) as a string.
            String[] componentIndexNames = vertStrings[i].split("/");

            // Each tuple component index as an integer
            int[] componentIndexes = new int[componentIndexNames.length];
            for(int j=0; j<componentIndexes.length; j++)
                // IMPORTANT: '1' is subtracted because OBJ vertices are 1-indexed.
                componentIndexes[j] = Integer.parseInt(componentIndexNames[j]) - 1;

            // Map indices to their values
            positions[i] = model.getPosition(componentIndexes[0]);
            texCoords[i] = model.getTexture(componentIndexes[1]);
            normals[i] = model.getNormal(componentIndexes[2]);
        }
    }

    /** Get the vertex at the face's index 'index'. */
    public VertexPositionNormalTextureTangent getVertex(int index)
    {
        // TODO: Implement tangent and bitangent vectors.
        return new VertexPositionNormalTextureTangent(
                positions[index],
                normals[index],
                new Vector2(texCoords[index].x, texCoords[index].y),
                Vector3.zero,
                Vector3.zero
        );
    }
}
