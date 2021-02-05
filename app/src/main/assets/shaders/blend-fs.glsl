#version 310 es
precision highp float;

in vec2 v_texCoords;

uniform sampler2D u_color;
uniform sampler2D u_bloom;

out vec4 f_color;

void main() {
    vec3 color = texture(u_color, v_texCoords).rgb;
    color += texture(u_bloom, v_texCoords).rgb;
    color = vec3(1.0) - exp(-color * 4.0);
    f_color = vec4(pow(color, vec3(1.0 / 2.2)), 1.0);
//    f_color = texture(u_bloom, v_texCoords);
}
