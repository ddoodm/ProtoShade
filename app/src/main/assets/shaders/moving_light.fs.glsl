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
    // Lighting constants
    const vec4 ambient =
        vec4(0.1, 0.1, 0.2, 1.0);
    vec3 light_pos =
        vec3(100.0, 200.*sin (20.*time), 100.0);
    const vec4 diffuse_albedo =
        vec4(0.9, 0.7, 0.7, 1.0);
    const vec4 specular_albedo =
        vec4(0.9, 0.7, 0.7, 1.0);
    const float specular_power =
        128.0;
    const vec4 rim_albedo =
        vec4(0.);
    const float rim_power =
        1.25;

    // Interpolated vertex coordinate
    // in view-space
    vec3 P = vec3(fPosition);

    // Interpolated vertex normal
    // in view-space
    vec3 N = normalize(fNormal);

    // The viewing vector points to
    // the origin from the vertex
    vec3 V = normalize(-P);

    // Transform the light vector
    // into a direction in view space
    vec3 L = normalize(light_pos - P);

    // Reflect the light about the plane
    // defined by the normal (N)
    vec3 R = reflect(-L, N);

    // Compute the diffuse contribution
    vec4 diffuse =
        max(0.0, dot(N, L)) *
        diffuse_albedo *
        texture2D(texture, fTexture);

    // Traditional Phong specular
    // contribution
    // ( no Blinn-Phong optimisation )
    vec4 specular =
        pow( max(0.0, dot(R, V)),
            specular_power ) *
        specular_albedo;

    // Artificial rim colour contributes
    // when normal is at a grazing
    // angle to the view
    vec4 rim = pow(
          smoothstep(0.0, 1.0, 1.0 - dot(N, V)),
          rim_power) * rim_albedo;

    // Final fragment colour is the
    // sum of contributions
    gl_FragColor =
      ambient + diffuse + specular + rim;
}
