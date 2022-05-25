#version 330 core

layout (location = 0) out vec4 color;

uniform sampler2DArray texArray;

in vec3 uvl;

void main()
{
	color = texture(texArray, vec3(uvl.x,uvl.y,uvl.z));
}
