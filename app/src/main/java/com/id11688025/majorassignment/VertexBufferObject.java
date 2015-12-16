package com.id11688025.majorassignment;

import com.id11688025.majorassignment.math.VertexPosition;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * An abstraction of a FloatBuffer that is used to store
 * an array of Vector3f vectors.
 */
public class VertexBufferObject <VertexType extends VertexPosition>
{
    /** The vertices stored by this VBO */
    private FloatBuffer vertexBuffer;

    /** The vertices in their original form */
    private VertexType[] vertices;

    /** Create a Vertex Buffer Object from an array of Vector3f vectors */
    public VertexBufferObject(VertexType[] vertices)
    {
        this.vertices = vertices;

        // Allocate memory for (number of vertices) * (vertex dimensions) * (4-byte float)
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(
                vertices.length * vertices[0].getLengthInBytes()
        );

        // Use the native byte order of the device hardware
        byteBuffer.order(ByteOrder.nativeOrder());

        // Create a FloatBuffer from the ByteBuffer
        vertexBuffer = byteBuffer.asFloatBuffer();

        // Append the vectors to the vertex buffer
        putVertices(vertices);

        // Reset the buffer cursor
        vertexBuffer.position(0);
    }

    /** Append an array of Vector3 vertices to the vertex buffer */
    private void putVertices(VertexType[] vertices)
    {
        for(VertexType vertex : vertices)
        {
            // Obtain the current vertex as an array of floats
            float[] vertexFloats = vertex.getArray();

            // Store the vertex into the buffer
            vertexBuffer.put(vertexFloats);
        }
    }

    /** @return a float buffer that stores the vertex data */
    public FloatBuffer getFloatBuffer()
    {
        return vertexBuffer;
    }

    /** @return The size of each vertex that the buffer stores. */
    public int vertexSizeInBytes()
    {
        if(vertices.length == 0)
            return 0;
        else
            return vertices[0].getLengthInBytes();
    }

    /** @return the size of the buffer in bytes */
    public int sizeInBytes()
    {
        if(vertices.length == 0)
            return 0;
        else
            return vertices.length * vertexSizeInBytes();
    }
}
