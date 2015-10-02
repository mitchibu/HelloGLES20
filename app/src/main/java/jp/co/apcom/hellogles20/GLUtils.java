package jp.co.apcom.hellogles20;

import android.opengl.GLES20;

public class GLUtils {
	public static void checkGlError(String op) {
		int error = GLES20.glGetError();
		if(error == GLES20.GL_NO_ERROR) return;
		android.util.Log.e(GLUtils.class.getSimpleName(), op + ": glError " + error);
		throw new RuntimeException(op + ": glError " + error);
	}
}
