package com.id11688025.majorassignment.graphics;

import android.opengl.GLES20;

import com.id11688025.majorassignment.Camera;
import com.id11688025.majorassignment.ContentManager;
import com.id11688025.majorassignment.R;
import com.id11688025.majorassignment.shaders.Shader;
import com.id11688025.majorassignment.VertexBufferObject;
import com.id11688025.majorassignment.math.Matrix;
import com.id11688025.majorassignment.math.VertexPositionNormalTextureTangent;
import com.id11688025.majorassignment.objparser.OBJModel;

/**
 * A 3D model. Not to be confused with OBJModel,
 * which is a mechanism for loading OBJ files.
 */
public class Model
{
    /** The world transform matrix for this object */
    private Matrix world = Matrix.createIdentity();

    /** The shader that renders the object */
    private Shader shader;

    /** The object's texture */
    private Texture2D texture;

    /** The "Vertex Buffer Object" that stores the vertices for this object. */
    private VertexBufferObject<VertexPositionNormalTextureTangent> vertexBuffer;

    /** The object's vertices */
    private VertexPositionNormalTextureTangent vertices[];

    /** Should the model rotate by itself every update? */
    private boolean rotateAudomatically = false;

    /** Create a model from an OBJ model and a shader */
    public Model(final ContentManager content, final OBJModel model, final Shader shader)
    {
        initialize(content, model, shader);

        // Provide the model's vertex buffer to the existing shader
        shader.provideVertices(vertexBuffer);
    }

    /** Create a model from an OBJ model */
    public Model(final ContentManager content, final OBJModel model)
    {
        // Create a shader
        Shader shader = new Shader(content);

        // Initialize the model's Vertex Buffer
        initialize(content, model, shader);

        // Provide the Vertex Buffer to the shader
        shader.provideVertices(vertexBuffer);
    }

    private void initialize(final ContentManager content, final OBJModel model, final Shader shader)
    {
        // Initialize the vertices from the OBJ model.
        vertices = model.getVertices();

        // Provide the vertex buffer with the object's vertices
        vertexBuffer = new VertexBufferObject<VertexPositionNormalTextureTangent>(vertices);

        texture = content.loadTexture2D(R.drawable.concrete);

        // Use the shader
        this.shader = shader;
    }

    /** Supply a fragment shader to use on the object (from the user).
     * @param fragmentSource The fragment shader source as a String.
     * @return True if compilation was successful.
     */
    public boolean provideUserShader (String fragmentSource)
    {
        return shader.provideUserShader(fragmentSource);
    }

    /** Update the object.
     * @param gameTime The total elapsed game time.
     */
    public void update(float gameTime)
    {
        if(rotateAudomatically)
            autoRotate(gameTime);
    }

    /** Automatically rotate the object's coordinate system */
    private void autoRotate(float gameTime)
    {
        // Rotation animation
        world = Matrix.createRotationY(gameTime * 4f);
        world = world.multiply(Matrix.createRotationZ(gameTime * 6f));
    }

    /** Draw the object to the OpenGL context */
    public void draw(Camera camera, float time)
    {
        // Use the shader program
        shader.setWorld(this.world);
        shader.setView(camera.getView());
        shader.setProjection(camera.getProjection());
        shader.setTexture(texture);
        shader.use(time);

        // Draw the object with triangles
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length);
    }

    /** Set this model's world transformation */
    public void setTransform(Matrix matrix)
    {
        world = matrix;
    }

    /** Transform this model by a matrix */
    public void transform(Matrix matrix)
    {
        world = world.multiply(matrix);
    }

    /** Specify whether the model should rotate automatically,
     * or as a result of touch input.
     * @param condition Automatic rotation if true.
     */
    public void rotateModelAutomatically(final boolean condition)
    {
        rotateAudomatically = condition;
    }

    public Shader getCurrentShader()
    {
        return shader;
    }
}
