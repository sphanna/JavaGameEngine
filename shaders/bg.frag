#version 330 core

layout (location = 0) out vec4 color;

in vec3 uv;

uniform sampler2D tex;

void main()
{
	color = texture(tex, vec2(uv.x, uv.y));
}
