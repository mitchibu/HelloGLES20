uniform mat4 vpMatrix;
uniform mat4 wMatrix;
attribute vec3 position;
void main() {
	gl_Position = vpMatrix * wMatrix * vec4(position, 1.0);
}
