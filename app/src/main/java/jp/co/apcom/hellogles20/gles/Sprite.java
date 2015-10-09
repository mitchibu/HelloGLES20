package jp.co.apcom.hellogles20.gles;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class Sprite {
	private final Bitmap bm;

	private int textureID;
//	float F = 0;
	float x = 0;
	float y = 0;
	float w = 144;
	float h = 144;
//	float y_prev = 0;

	public Sprite(Bitmap bm, float w, float h) {
		this.bm = bm;
		this.w = w;
		this.h = h;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getX() {
		return x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
		return h;
	}

	public void setup() {
		int[] args = new int[1];
		GLES20.glGenTextures(args.length, args, 0);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, args[0]);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bm, 0);
		bm.recycle();
		textureID = args[0];
	}

	public int getTextureID() {
		return textureID;
	}

	public void matrix(Engine engine, float[] matrix) {
	}

	public void update() {
	}
}
