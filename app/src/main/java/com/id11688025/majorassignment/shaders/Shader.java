package com.id11688025.majorassignment.shaders;

import android.opengl.GLES20;
import android.util.Log;

import com.id11688025.majorassignment.ContentManager;
import com.id11688025.majorassignment.VertexBufferObject;
import com.id11688025.majorassignment.graphics.Texture2D;
import com.id11688025.majorassignment.math.Matrix;

/**
 * A basic GLSL shader wrapper that provides flat shading.
 *
 * This class should be extended in order to implement more
 * advanced lighting effects.
 */
public class Shader
{
    /** The attribute name of the vertex position. */
    private static final String VPOSITION = "vPosition";

    /** The attribute name of the normal vector. */
    private static final String VNORMAL = "vNormal";

    /** The attribute name of the texture coordinate. */
    private static final String VTEXTURE = "vTexture";

    /** The uniform name of the model-view matrix. */
    private static final String MV_MATRIX = "mv_matrix";

    /** The uniform name of the projection matrix. */
    private static final String PROJECTION_MATRIX = "projection";

    /** The uniform name of the time uniform. */
    private static final String TIME_UNIFORM = "time";

    private static final String
        TAG_VERTEX_SHADER = "VERTEX_SHADER_LOG",
        TAG_FRAGMENT_SHADER = "VERTEX_SHADER_LOG";

    /** The model world transform */
    private Matrix world = Matrix.createIdentity();

    /** The camera view transform */
    private Matrix view = Matrix.createIdentity();

    /** The projective transform */
    private Matrix projection = Matrix.createIdentity();

    /** The current texture to use */
    private Texture2D texture;

    /** The name (ID) of the vertex shader */
    private int vertexShaderName;
    /** The name (ID) of the fragment shader */
    private int fragmentShaderName;
    /** The name (ID) of the shader program */
    private int programName;

    /** The vertex shader source code */
    private String vertexSource;
    /** The fragment shader source code */
    private String fragmentSource;

    /** The vertex buffer that currently stores the object's vertices */
    private VertexBufferObject vertexBuffer;

    /** The location of the "vPosition" attribute */
    private int vPositionLocation;
    /** The location of the "vNormal" attribute */
    private int vNormalLocation;
    /** The location of the "vTexture" attribute */
    private int vTextureLocation;
    /** The location of the "model-view" matrix uniform */
    private int modelViewLocation;
    /** The location of the "projection" matrix uniform */
    private int projectionLocation;
    /** The location of the "time" uniform */
    private int timeLocation;

    /** The compilation log of the fragment shader */
    private String fragmentShaderLog;

    public Shader(ContentManager content, String fragmentSource)
    {
        this.vertexSource = readShaderFile(content, "shaders/phong_basic.vs.glsl");
        this.fragmentSource = fragmentSource;

        compile(vertexSource, fragmentSource);
    }

    public Shader(ContentManager content)
    {
        // Read the "basic" shader source code
        vertexSource = readShaderFile(content, "shaders/phong_basic.vs.glsl");
        fragmentSource = readShaderFile(content, "shaders/phong_basic.fs.glsl");

        compile(vertexSource, fragmentSource);
    }

    /** Supply a fragment shader to use on the object (from the user).
     * @param fragmentSource The fragment shader source as a String.
     * @return True if compilation was successful.
     */
    public boolean provideUserShader (String fragmentSource)
    {
        // Update source code
        this.fragmentSource = fragmentSource;

        // Delete old program and fragment shader
        GLES20.glDeleteProgram(programName);
        GLES20.glDeleteShader(fragmentShaderName);

        // Re-compile and re-link the shader
        boolean compiled = compile(vertexSource, fragmentSource);

        // Re-supply shader attribute / uniform data
        provideVertices(vertexBuffer);

        // Return compiler status
        return compiled;
    }

    private boolean compile(String vertexSource, String fragmentSource)
    {
        // Compile the vertex and fragment shaders
        vertexShaderName = compile(GLES20.GL_VERTEX_SHADER, vertexSource);
        fragmentShaderName = compile(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        // Link the two shaders into a shader program
        int newProgramName = GLES20.glCreateProgram();
        GLES20.glAttachShader(newProgramName, vertexShaderName);
        GLES20.glAttachShader(newProgramName, fragmentShaderName);
        GLES20.glLinkProgram(newProgramName);

        // Print a debug log in case of compilation errors, and keep the fragment log.
        handleLog();

        // Check compile status:
        // '0' indicate that OpenGL could not link the shader.
        int compileStatus[] = new int[1];
        GLES20.glGetShaderiv(fragmentShaderName, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the shader compiled, start using it.
        boolean compiled = compileStatus[0] > 0;
        if(compiled)
            this.programName = newProgramName;

        return compiled;
    }

    /** Read in shader source code from a file.
     * @param path The asset path to the shader.
     * @return The shader source code.
     */
    protected static String readShaderFile(ContentManager content, String path)
    {
        // Get the string that contains the file
        return content.fileAsString(path);
    }

    /** Compile a shader source.
     * @param type The type of shader module. (GLES20.GL_VERTEX_SHADER or GLES20.GL_FRAGMENT_SHADER)
     * @param source The shader source code
     * @return The name (ID) of the shader module.
     */
    private int compile(int type, String source)
    {
        int name = GLES20.glCreateShader(type);

        // Provide source code and compile
        GLES20.glShaderSource(name, source);
        GLES20.glCompileShader(name);

        // Return shader name (ID)
        return name;
    }

    /** Initialize the shader's attributes.
     * @param vertexBuffer The vertex buffer that stores vertex data. */
    public void provideVertices(VertexBufferObject vertexBuffer)
    {
        // Store the vertex buffer
        this.vertexBuffer = vertexBuffer;

        // Give the state machine the shader program to use
        GLES20.glUseProgram(programName);

        // The location of the "vPosition" attribute
        vPositionLocation = GLES20.glGetAttribLocation(programName, VPOSITION);
        // The location of the "vNormal" attribute
        vNormalLocation = GLES20.glGetAttribLocation(programName, VNORMAL);
        // The location of the "vTexture" attribute
        vTextureLocation = GLES20.glGetAttribLocation(programName, VTEXTURE);
        // The location of the "model-view" matrix uniform
        modelViewLocation = GLES20.glGetUniformLocation(programName, MV_MATRIX);
        // The location of the "projection" uniform
        projectionLocation = GLES20.glGetUniformLocation(programName, PROJECTION_MATRIX);
        // The location of the "time" uniform
        timeLocation = GLES20.glGetUniformLocation(programName, TIME_UNIFORM);

        // Create a buffer that will store the vertex data (coordinates, normal, texture)
        int[] bufferName = new int[1];
        GLES20.glGenBuffers(1, bufferName, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferName[0]);
        GLES20.glBufferData(
                GLES20.GL_ARRAY_BUFFER,
                vertexBuffer.sizeInBytes(),
                vertexBuffer.getFloatBuffer(),
                GLES20.GL_STATIC_DRAW);

        // The amount (in bytes) to stride over the buffer to reach the next vertex
        int stride = vertexBuffer.vertexSizeInBytes();

        // Enable a vertex array attribute on the attribute named "vPosition"
        GLES20.glEnableVertexAttribArray(vPositionLocation);
        // Pass the vertex buffer data to the vPosition vertex attribute array
        GLES20.glVertexAttribPointer(
                vPositionLocation, // Attribute name
                3,                 // Attribute size
                GLES20.GL_FLOAT,   // Data type (float)
                false,             // Do not normalize
                stride,            // Stride across 14 4-byte floats
                0);                // Offset into vertex array

        // Enable a vertex array attribute on the attribute named "vNormal"
        GLES20.glEnableVertexAttribArray(vNormalLocation);
        // Pass the vertex buffer data to the vNormal vertex attribute array
        GLES20.glVertexAttribPointer(
                vNormalLocation,   // Attribute name
                3,                 // Attribute size
                GLES20.GL_FLOAT,   // Data type (float)
                false,             // Do not normalize
                stride,            // Stride across 14 4-byte floats
                4*3);              // Offset into vertex array

        // Enable a vertex array attribute on the attribute named "vTexture"
        GLES20.glEnableVertexAttribArray(vTextureLocation);
        // Pass the vertex buffer data to the vTexture vertex attribute array
        GLES20.glVertexAttribPointer(
                vTextureLocation,  // Attribute name
                2,                 // Attribute size
                GLES20.GL_FLOAT,   // Data type (float)
                false,             // Do not normalize
                stride,            // Stride across 14 4-byte floats
                4*6);              // Offset into vertex array
    }

    /** Set the shader's texture */
    public void setTexture(Texture2D texture)
    {
        this.texture = texture;
    }

    /** Set the OpenGL state machine to use this shader program, and update the shader.
     * @param time The time, in fractional seconds, since the program started. */
    public void use(float time)
    {
        // Compute the model-view matrix
        Matrix modelView = Matrix.multiply(view, world);

        // Enable the shader program
        GLES20.glUseProgram(programName);

        // If the texture has been set, use it.
        if(texture != null)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getTextureName());

        // Provide matrix uniforms
        GLES20.glUniformMatrix4fv(
                modelViewLocation,       // Uniform name
                1,                       // Matrix count
                true,                    // Transpose the matrix
                modelView.as1DArray(),   // The matrix as an array
                0                        // Offset into the array
        );
        GLES20.glUniformMatrix4fv(
                projectionLocation,      // Uniform name
                1,                       // Matrix count
                true,                    // Transpose the matrix
                projection.as1DArray(),  // The matrix as an array
                0                        // Offset into the array
        );
        GLES20.glUniform1f(timeLocation, time);
    }

    /** Print the shader info log to the debugging stream. */
    public void handleLog()
    {
        Log.d(TAG_VERTEX_SHADER, GLES20.glGetShaderInfoLog(vertexShaderName));
        Log.d(TAG_FRAGMENT_SHADER, fragmentShaderLog = GLES20.glGetShaderInfoLog(fragmentShaderName));
    }

    /** Set the world (model) matrix transform */
    public void setWorld(Matrix world) {
        this.world = world;
    }

    /** The world view transform (camera view matrix) */
    public void setView(Matrix view) {
        this.view = view;
    }

    /** The projective transform (camera projection matrix) */
    public void setProjection(Matrix projection) {
        this.projection = projection;
    }

    /** @return The fragment shader source code */
    public String getFragmentSource() {
        return fragmentSource;
    }

    /** @return The fragment shader compiler log */
    public String getFragmentShaderLog() {
        return fragmentShaderLog;
    }
}
