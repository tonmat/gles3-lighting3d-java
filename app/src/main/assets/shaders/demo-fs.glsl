#version 310 es
precision highp float;

in vec3 v_position;
in vec3 v_normal;

uniform vec3 u_light;

out vec4 f_color;

const vec3 ambientColor = vec3(0.01, 0.02, 0.01);
const vec3 diffuseColor = vec3(0.1, 0.4, 0.0);
const vec3 specularColor = vec3(0.8, 0.8, 0.8);

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
        specular = pow(specular, 64.0);
    }

    f_color = vec4(max(ambientColor + (diffuse * diffuseColor + specular * specularColor) / distance, 0.0), 1.0);
    f_color.rgb = pow(f_color.rgb, vec3(1.0 / 2.2));
}

