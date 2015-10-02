precision mediump float;
varying vec2 v_UV;
uniform sampler2D u_Tex;
void main() {
	gl_FragColor=texture2D(u_Tex, v_UV);
}
