#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP 
#endif

uniform sampler2D u_texture;
uniform mat3 u_lmsToRgb;

varying vec2 v_texCoords;
varying vec3 v_lmsCompressedColor;

void main()
{
	vec3 color = pow(abs(v_lmsCompressedColor), vec3(1.0 / 0.43)) * sign(v_lmsCompressedColor);
	color = pow(u_lmsToRgb * color, vec3(1.0 / 2.2));
	gl_FragColor = texture2D(u_texture, v_texCoords) * vec4(color, 1.0);
}