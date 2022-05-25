#version 330 core

layout (location = 0) in  vec4 position;
layout (location = 1) in  vec3 tc;

uniform mat4 proj_matrix;

out vec3 uv;

void main()
{
	gl_Position = proj_matrix * position;
	uv = tc;
}
