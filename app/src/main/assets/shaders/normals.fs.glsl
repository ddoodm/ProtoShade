// ==== FRAGMENT SHADER ==== \\
precision mediump float;

// == Uniform inputs ==
uniform sampler2D texture;
uniform float time;

// == Inputs from vertex shader ==
varying vec4 fPosition;
varying vec3 fNormal;
varying vec2 fTexture;

void main()
{
    gl_FragColor = vec4(fNormal, 1.0);
}