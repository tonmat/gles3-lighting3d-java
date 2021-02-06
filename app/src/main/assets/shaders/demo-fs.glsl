#version 310 es
precision highp float;

in vec3 v_position;
in vec3 v_normal;

uniform vec3 u_light;
uniform vec3 u_color;

layout (location = 0) out vec4 f_color;
layout (location = 1) out vec4 f_bloom;

const vec3 ambientColor = vec3(0.01, 0.01, 0.01);
const vec3 specularColor = vec3(1.0, 1.0, 1.0);

void main() {
    vec3 L = u_light - v_position;
    float distance = L.x * L.x + L.y * L.y + L.z * L.z;
    L = normalize(L);
    vec3 N = normalize(v_normal);

    float diffuse = max(dot(L, N), 0.0);

    float specular = 0.0;
    if (diffuse > 0.0)
    {
        vec3 V = normalize(-v_position);
        vec3 H = normalize(L + V);
        specular = max(dot(H, N), 0.0);
        specular = pow(specular, 16.0);
    }

    f_color = vec4(max(ambientColor + (diffuse * u_color + specular * specularColor) / distance, 0.0), 1.0);

    float brightness = dot(f_color.rgb, vec3(0.2125, 0.7154, 0.0721));
    if (brightness > 1.0) {
        f_bloom = f_color;
    } else {
        f_bloom = vec4(0.0, 0.0, 0.0, 1.0);
    }
}

