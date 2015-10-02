uniform mat4 vpMatrix;
uniform mat4 wMatrix;
attribute vec3 position;
attribute vec2 a_UV;
varying vec2 v_UV;
void main() {
	gl_Position = vpMatrix * wMatrix * vec4(position, 1.0);
	v_UV=a_UV;
}
