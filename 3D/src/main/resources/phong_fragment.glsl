#version 130

uniform sampler2D dissolve_texture;
uniform float dissolve_threshold;
in vec3 normal;
in vec3 pos;
in vec2 tex_coord;

void main (void) {
    vec3 l = normalize(vec3(-3, -3, -3) - pos);
    vec4 diffuse = clamp(gl_FrontLightProduct[0].diffuse * max(dot(normal, l), 0.0), 0.0, 1.0);
    vec4 specular = gl_FrontLightProduct[0].specular * pow(max(dot(normalize(-reflect(l, normal)), normalize(-pos)), 0.0), 0.3 * gl_FrontMaterial.shininess);
    gl_FragColor = gl_FrontLightModelProduct.sceneColor + gl_FrontLightProduct[0].ambient + diffuse + clamp(specular, 0.0, 1.0);

    vec3 dissolve = texture(dissolve_texture, tex_coord).rgb;
    if (dissolve[0] + dissolve[1] + dissolve[2] < dissolve_threshold) discard;
    if (dissolve[0] + dissolve[1] + dissolve[2] < dissolve_threshold + 0.1)  gl_FragColor = vec4(1, 0, 0, 0);

    //gl_FragColor = texture2D(dissolve_texture, tex_coord);
}
