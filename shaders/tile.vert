#version 330 core

layout (location = 0) in  vec4 position;
layout (location = 1) in  vec3 tc;

uniform mat4 proj_matrix;
uniform mat4 view_matrix = mat4(1.0);

out vec3 uvl;

void main()
{
	gl_Position = proj_matrix * view_matrix * position;
	uvl = tc;
}
