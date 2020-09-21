attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform vec4 u_topColor;
uniform vec4 u_bottomColor;
uniform mat3 u_rgbToLms;

varying vec2 v_texCoords;
varying vec3 v_lmsCompressedColor;

void main()
{
   vec3 color = mix(u_topColor.rgb, u_bottomColor.rgb, a_texCoord0.y);
   color = u_rgbToLms * pow(color, vec3(2.2));
   v_lmsCompressedColor = pow(abs(color), vec3(0.43)) * sign(color);
   v_texCoords = a_texCoord0;
   gl_Position = u_projTrans * a_position;
}
