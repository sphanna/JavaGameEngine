#version 330 core

layout (location = 0) in  vec4 position;

uniform mat4 proj_matrix;

void main()
{
	gl_Position = proj_matrix * position;
}
