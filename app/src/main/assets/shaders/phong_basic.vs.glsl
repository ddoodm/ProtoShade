// ==== VERTEX SHADER ==== \\

// == Array inputs ==
attribute vec4 vPosition;
attribute vec3 vNormal;
attribute vec2 vTexture;

// == Uniform inputs ==
uniform mat4 mv_matrix;     // Modelview matrix
uniform mat4 projection;    // Projection matrix

// == Fragment shader outputs ==
varying vec4 fPosition;
varying vec3 fNormal;
varying vec2 fTexture;

void main()
{
    // Transform the vertex position and normal into view-space
    fPosition = mv_matrix * vPosition;

    // Do not translate the normal
    fNormal = mat3(mv_matrix) * vNormal;

    // Pass the texture coordinate
    fTexture = vTexture;

    // Project the position into clip-space to obtain the final normalized position
    gl_Position = projection * fPosition;
}