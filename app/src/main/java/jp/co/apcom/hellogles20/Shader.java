package jp.co.apcom.hellogles20;

import android.opengl.GLES20;

public class Shader {
	public int programID = -1;

	private int loadShader(int type, String shaderCode){
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		int[] compiled = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if(compiled[0] == 0) {//もしコンパイルに失敗していたら
			android.util.Log.e("TAG", GLES20.glGetShaderInfoLog(shader));//何行目がどんなふうに間違ってるか吐き出す。
			GLES20.glDeleteShader(shader);
			return 0;
		}
		return shader;
	}

	public static int load(int type, String code) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, code);
		GLES20.glCompileShader(shader);
		return shader;
	}

	public void init(int... shader) {
		programID = GLES20.glCreateProgram();
		for(int i : shader) {
			GLES20.glAttachShader(programID, i);
			GLES20.glDeleteShader(i);
		}
		GLES20.glLinkProgram(programID);
	}

	public void use() {
		GLES20.glUseProgram(programID);
	}

	public int getUniformLocation(String name) {
		return GLES20.glGetUniformLocation(programID, name);
	}

	public int getAttributeLocation(String name) {
		return GLES20.glGetAttribLocation(programID, name);
	}
}
