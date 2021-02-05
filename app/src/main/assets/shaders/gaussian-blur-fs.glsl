#version 310 es
precision highp float;

in vec2 v_texCoords;

uniform sampler2D u_texture;
uniform bool u_horizontal;

out vec4 f_color;

const float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
    vec2 tc_offset = 1.0 / vec2(textureSize(u_texture, 0));
    vec3 result = texture(u_texture, v_texCoords).rgb * weight[0];

    if (u_horizontal) {
        for (int i = 1; i < 5; i++) {
            result += texture(u_texture, v_texCoords + vec2(tc_offset.x * float(i), 0.0)).rgb * weight[i];
            result += texture(u_texture, v_texCoords - vec2(tc_offset.x * float(i), 0.0)).rgb * weight[i];
        }
    } else {
        for (int i = 1; i < 5; i++) {
            result += texture(u_texture, v_texCoords + vec2(0.0, tc_offset.y * float(i))).rgb * weight[i];
            result += texture(u_texture, v_texCoords - vec2(0.0, tc_offset.y * float(i))).rgb * weight[i];
        }
    }

    f_color = vec4(result, 1.0);
}
