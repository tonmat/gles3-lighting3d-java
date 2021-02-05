#version 310 es

layout (location = 0) in vec3 a_position;
layout (location = 1) in vec3 a_normal;

uniform mat4 u_modelview;
uniform mat4 u_projection;
uniform mat3 u_normal;

out vec3 v_position;
out vec3 v_normal;

void main() {
    gl_Position = u_projection * u_modelview * vec4(a_position, 1);
    v_position = vec3(u_modelview * vec4(a_position, 1));
    v_normal = u_normal * a_normal;
}
