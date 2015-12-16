package com.id11688025.majorassignment.math;

/**
 * An algebraic matrix with computer graphics mathematics functions.
 */
public class Matrix
{
    /** The number of rows in the matrix */
    public final static int ROWS = 4;

    /** The number of columns in the matrix */
    public final static int COLS = 4;

    /** The 4x4 matrix data */
    public float[][] data = new float[ROWS][COLS];

    /** Create an identity matrix */
    public Matrix()
    {
        this.data = createIdentity().data;
    }

    /** Create a matrix from an array of floating point data.
     * @param data Array of floats that define the 4x4 matrix. Must be 16 elements long.
     */
    public Matrix(float[] data)
    {
        if(data.length != ROWS * COLS)
            throw new RuntimeException(
                    "The 1-D floating point array passed to the matrix constructor must be of size 16.");

        // Convert the 1D array to a 2D array
        for(int i=0; i < data.length; i++)
        {
            int rowIndex = i / COLS;
            int colIndex = i % COLS;

            this.data[rowIndex][colIndex] = data[i];
        }
    }

    /** Create a matrix from a two-dimensional array of floating point data.
     * @param data 2-D array of floating point values that define the 4x4 matrix. Must be 4x4 elements long.
     */
    public Matrix(float[][] data)
    {
        if(data.length != ROWS || data[0].length != COLS)
            throw new RuntimeException(
                    "The 2-D floating point array passed to the matrix constructor must be of size 4x4.");

        this.data = data.clone();
    }

    /**
     * @return This matrix as a one-dimensional floating-point array.
     */
    public float[] as1DArray()
    {
        float[] result = new float[ROWS * COLS];

        for(int i=0; i < ROWS; i++)
            for(int j=0; j < COLS; j++)
                result[i * COLS + j] = data[i][j];

        return result;
    }

    public static Matrix multiply(Matrix lhs, Matrix rhs)
    {
        float[][] matA = lhs.data;
        float[][] matB = rhs.data;
        float[][] result = new float[ROWS][COLS];

        // Compute a standard matrix-matrix product.
        for(int i=0; i < ROWS; i++)
            for(int j=0; j < COLS; j++)
                for(int k=0; k < COLS; k++)
                    result[i][j] = result[i][j] + matA[i][k] * matB[k][j];

        return new Matrix(result);
    }

    public Matrix multiply(Matrix other)
    {
        return multiply(this, other);
    }

    /**
     * Create an identity matrix.
     * @return A new identity matrix.
     */
    public static Matrix createIdentity()
    {
        return new Matrix(new float[][]{
                {1.0f,   0.0f,   0.0f,   0.0f},
                {0.0f,   1.0f,   0.0f,   0.0f},
                {0.0f,   0.0f,   1.0f,   0.0f},
                {0.0f,   0.0f,   0.0f,   1.0f}
        });
    }

    /**
     * Create a matrix that will translate a vector by the amount specified.
     * @param translation The amount of parallel transfer.
     * @return A matrix that performs a parallel transfer.
     */
    public static Matrix createTranslation(Vector3 translation)
    {
        return new Matrix(new float[][]{
                {1.0f,   0.0f,   0.0f,   translation.x},
                {0.0f,   1.0f,   0.0f,   translation.y},
                {0.0f,   0.0f,   1.0f,   translation.z},
                {0.0f,   0.0f,   0.0f,   1.0f}
        });
    }

    /** Create a matrix which performs a scaling transformation.
     * @param scalar The scalar value by which each vertex should be multiplied.
     * @return A matrix which scales the coordinate system.
     */
    public static Matrix createScale(float scalar)
    {
        return new Matrix(new float[][]{
                {scalar, 0.0f,   0.0f,   0.0f},
                {0.0f,   scalar, 0.0f,   0.0f},
                {0.0f,   0.0f,   scalar, 0.0f},
                {0.0f,   0.0f,   0.0f,   1.0f}
        });
    }

    /** Create a matrix which performs a non-parallel scaling transformation.
     * @param scaleX The amount of scale on the X axis.
     * @param scaleY The amount of scale on the Y axis.
     * @param scaleZ The amount of scale on the Z axis.
     * @return A matrix which scales the coordinate system.
     */
    public static Matrix createScale(float scaleX, float scaleY, float scaleZ)
    {
        return new Matrix(new float[][]{
                {scaleX, 0.0f,   0.0f,   0.0f},
                {0.0f,   scaleY, 0.0f,   0.0f},
                {0.0f,   0.0f,   scaleZ, 0.0f},
                {0.0f,   0.0f,   0.0f,   1.0f}
        });
    }

    /** Create a matrix which performs a non-parallel scaling transformation.
     * @param scale The vector which represents the amount of scale on each axis.
     * @return A matrix which scales the coordinate system.
     */
    public static Matrix createScale(Vector3 scale)
    {
        return createScale(scale.x, scale.y, scale.z);
    }

    /** Rotate the coordinate system about the X axis.
     * @param theta The angle (in radians) by which to rotate the coordinate system.
     * @return A matrix which applies a linear rotation to the coordinate system.
     */
    public static Matrix createRotationX(float theta)
    {
        float sinT = (float)Math.sin(theta);
        float cosT = (float)Math.cos(theta);

        return new Matrix(new float[][]{
                {1.0f,   0.0f,   0.0f,   0.0f},
                {0.0f,   cosT,  -sinT,   0.0f},
                {0.0f,   sinT,   cosT,   0.0f},
                {0.0f,   0.0f,   0.0f,   1.0f}
        });
    }

    /** Rotate the coordinate system about the Y axis.
     * @param theta The angle (in radians) by which to rotate the coordinate system.
     * @return A matrix which applies a linear rotation to the coordinate system.
     */
    public static Matrix createRotationY(float theta)
    {
        float sinT = (float)Math.sin(theta);
        float cosT = (float)Math.cos(theta);

        return new Matrix(new float[][]{
                { cosT,   0.0f,   sinT,   0.0f},
                { 0.0f,   1.0f,   0.0f,   0.0f},
                {-sinT,   0.0f,   cosT,   0.0f},
                { 0.0f,   0.0f,   0.0f,   1.0f}
        });
    }

    /** Rotate the coordinate system about the Z axis.
     * @param theta The angle (in radians) by which to rotate the coordinate system.
     * @return A matrix which applies a linear rotation to the coordinate system.
     */
    public static Matrix createRotationZ(float theta)
    {
        float sinT = (float)Math.sin(theta);
        float cosT = (float)Math.cos(theta);

        return new Matrix(new float[][]{
                {cosT,  -sinT,   0.0f,   0.0f},
                {sinT,   cosT,   0.0f,   0.0f},
                {0.0f,   0.0f,   1.0f,   0.0f},
                {0.0f,   0.0f,   0.0f,   1.0f}
        });
    }

    /** Construct a perspective projection matrix from viewport parameters.
     * @param fov The Field of View of the camera (in radians).
     * @param aspect The aspect ratio of the viewport (width / height).
     * @param near The distance of the frustum near plane into Z-space.
     * @param far The distance of the frustum far plane into Z-space.
     * @return A perspective projection matrix.
     */
    public static Matrix createPerspectiveFov(float fov, float aspect, float near, float far)
    {
        // Reciprocate tangent function for cotangent
        float cotfov = 1.f / (float)Math.tan( fov * 0.5f );

        // Values for perspective division
        float zfn = (far + near) / (far - near);
        float wfn = (2.f * far * near) / (far - near);

        // Build the matrix
        return new Matrix(new float[][]{
                {cotfov / aspect,    0.0f,       0.0f,      0.0f},
                {0.0f,               cotfov,     0.0f,      0.0f},
                {0.0f,               0.0f,      -zfn,      -wfn},
                {0.0f,               0.0f,      -1.0f,      1.0f}
        });
    }

    /** Create a look-at view matrix for a camera.
     * The matrix creates a reference frame faces the "target" from the "position".
     * @param position The position, in 3D Cartesian space, of the camera.
     * @param target The position, in 3D Cartesian space, of the target.
     * @param up The camera's up vector (usually (0,1,0)).
     * @return A view matrix which orients the coordinate system to face a target.
     */
    public static Matrix createLookAt(Vector3 position, Vector3 target, Vector3 up)
    {
        // Obtain the direction from the target to the position (target - position)
        Vector3 direction = Vector3.normalize(target.subtract(position));

        // Be sure that the up vector is normalized
        Vector3 upNormalized = up.normalize();

        /* Obtain the vector orthonormal to the direction and up vectors
        to construct a coordinate system. */
        Vector3 tangent = Vector3.cross(direction, upNormalized);

        /* Obtain the vector orthogonal to the direction and tangent
        vectors to complete the Cartesian reference frame. */
        Vector3 bitangent = Vector3.cross(tangent, direction);

        // Construct the matrix that orients the view to face the computed direction.
        // Build the matrix
        Matrix orientation = new Matrix(new float[][]{
                { tangent.x,     tangent.y,    tangent.z,   0.0f},
                { bitangent.x,   bitangent.y,  bitangent.z, 0.0f},
                {-direction.x,  -direction.y, -direction.z, 0.0f},
                { 0.0f,          0.0f,         0.0f,        1.0f}
        });

        // Offset (translate) the reference frame to the camera position.
        return multiply(orientation, createTranslation(position.negate()));
    }
}
