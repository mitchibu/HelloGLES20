package jp.co.apcom.hellogles20.gles;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

public class SpriteLayer extends Layer {
	private final static String VERTEX_CODE =
			"uniform mat4 u_MMatrix;"+
					"attribute vec4 a_Position;"+
					"attribute vec2 a_UV;"+
					"varying highp vec2 v_UV;"+
					"void main() {"+
					"gl_Position = u_MMatrix * a_Position;"+
					"v_UV = a_UV;"+
					"}";

	//フラグメントシェーダのコード
	private final static String FRAGMENT_CODE =
			"precision mediump float;"+
					"varying highp vec2 v_UV;" +
					"uniform lowp sampler2D u_Tex;"+
					"void main() {"+
					"gl_FragColor = texture2D(u_Tex, v_UV);"+
					"}";

	private final Engine engine;
	private final float[] matrix = new float[16];
	private final List<Sprite> sprites = new ArrayList<>();

	private int programID;
	private int mMatrixHandle;
	private int positionHandle;
	private int uvHandle;
	private int texHandle;
//	private int textureID;
	private int bufferID;
//	private Bitmap bm;

	public SpriteLayer(Engine engine) {
		this.engine = engine;
	}

	public void addSprite(Sprite sprite) {
		sprites.add(sprite);
	}

//	public void setBitmap(Bitmap bm) {
//		this.bm = bm;
//	}

	@Override
	public void setup() {
		bufferID = Engine.createBuffer(new float[]{
				// X, Y, Z, U, V
				-1.0f, 1.0f, 0.0f, 0.0f, 0.0f,    // 左上
				1.0f, 1.0f, 0.0f, 1.0f, 0.0f,    // 右上
				-1.0f, -1.0f, 0.0f, 0.0f, 1.0f,    // 左下
				1.0f, -1.0f, 0.0f, 1.0f, 1.0f    // 右下
		});
		programID = Engine.createShader(VERTEX_CODE, FRAGMENT_CODE);
		mMatrixHandle = GLES20.glGetUniformLocation(programID, "u_MMatrix");
		positionHandle = GLES20.glGetAttribLocation(programID, "a_Position");
		uvHandle = GLES20.glGetAttribLocation(programID, "a_UV");
		texHandle = GLES20.glGetUniformLocation(programID, "u_Tex");

//		int[] args = new int[1];
//		GLES20.glGenTextures(args.length, args, 0);
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, args[0]);
//
//		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
//		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
//		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//
//		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bm, 0);
//		bm.recycle();
//		textureID = args[0];

		for(Sprite sprite : sprites) {
			sprite.setup();
		}
	}

	@Override
	public void draw() {
		GLES20.glUseProgram(programID);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferID);
		GLES20.glEnableVertexAttribArray(uvHandle);
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		for(Sprite sprite : sprites) {
			sprite.matrix(engine, matrix);

			GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, matrix, 0);

			GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 20, 12);

			GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 20, 0);

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sprite.getTextureID());
			GLES20.glUniform1i(texHandle, 0);

			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

			sprite.update();
		}

		GLES20.glDisableVertexAttribArray(positionHandle);
		GLES20.glDisableVertexAttribArray(uvHandle);
		GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
}
