package com.id11688025.majorassignment.graphics;

import android.opengl.GLES20;

import com.id11688025.majorassignment.Camera;
import com.id11688025.majorassignment.ContentManager;
import com.id11688025.majorassignment.R;
import com.id11688025.majorassignment.VertexBufferObject;
import com.id11688025.majorassignment.math.Matrix;
import com.id11688025.majorassignment.math.Vector2;
import com.id11688025.majorassignment.math.Vector3;
import com.id11688025.majorassignment.math.VertexPositionNormalTexture;
import com.id11688025.majorassignment.shaders.Shader;

/**
 * A basic geometric triangle.
 */
public class Triangle
{
    /** The world transform matrix for this primitive */
    private Matrix world = Matrix.createIdentity();

    /** The shader that renders the primitive */
    private Shader shader;

    /** The object's texture */
    private Texture2D texture;

    /** The "Vertex Buffer Object" that stores the vertices for this primitive. */
    private VertexBufferObject<VertexPositionNormalTexture> vertexBuffer;

    /** The coordinates that compose a triangle. */
    private VertexPositionNormalTexture vertices[] =
    {
        new VertexPositionNormalTexture(new Vector3(-1.0f,-1.0f, 0.0f), new Vector3(0,0,1), new Vector2(0,0)),
        new VertexPositionNormalTexture(new Vector3( 0.0f, 1.0f, 0.0f), new Vector3(0,0,1), new Vector2(0.5f,1)),
        new VertexPositionNormalTexture(new Vector3( 1.0f,-1.0f, 0.0f), new Vector3(0,0,1), new Vector2(1,0)),
    };

    public Triangle(ContentManager content)
    {
        // Create an initialize the vertex buffer to store the triangle coordinates.
        vertexBuffer = new VertexBufferObject<VertexPositionNormalTexture>(vertices);

        // Initialize the texture
        texture = content.loadTexture2D(R.drawable.utsshot);

        // Create an initialize the shader
        shader = new Shader(content);
        shader.provideVertices(vertexBuffer);
        shader.handleLog();
    }

    /** Update the primitive
     * @param gameTime The total elapsed game time in fractional seconds.
     */
    public void update(double gameTime)
    {
        // Rotation animation
        world = Matrix.createRotationY((float) gameTime);
        world = world.multiply(Matrix.createRotationZ((float)gameTime/2f));
    }

    /** Draw the primitive to the OpenGL context */
    public void draw(Camera camera, float time)
    {
        // Use the shader program
        shader.setWorld(this.world);
        shader.setView(camera.getView());
        shader.setProjection(camera.getProjection());
        shader.setTexture(texture);
        shader.use(time);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length);
    }
}
