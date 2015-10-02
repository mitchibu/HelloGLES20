package jp.co.apcom.hellogles20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Sprite {
	private final static String VERTEX_CODE =
			"uniform mat4 u_MMatrix;"+
					"attribute vec4 a_Position;"+
					"attribute vec2 a_UV;"+
					"varying vec2 v_UV;"+
					"void main(){"+
					"gl_Position=u_MMatrix*a_Position;"+
					"v_UV=a_UV;"+
					"}";

	//フラグメントシェーダのコード
	private final static String FRAGMENT_CODE =
			"precision mediump float;"+
					"varying vec2 v_UV;" +
					"uniform sampler2D u_Tex;"+
					"void main(){"+
					"gl_FragColor=texture2D(u_Tex,v_UV);"+
					"}";

	public static Sprite instance(Context context, int id) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeResource(context.getResources(), id);

			int[] tex = new int[1];
			GLES20.glGenTextures(tex.length, tex, 0);

			//テクスチャへのビットマップ指定
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex[0]);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);

			//テクスチャフィルタの指定
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			return new Sprite(tex[0]);
		} finally {
			if(bm != null) bm.recycle();
		}
	};

	private final int textureID;
	private int programID;
	private FloatBuffer vertexBuffer;
	private FloatBuffer uvBuffer;

	private int mMatrixHandle;
	private int positionHandle;
	private int uvHandle;
	private int texHandle;
	private Shader shader = new Shader();

	public Sprite(int textureID) {
		this.textureID = textureID;

		float[] vertex = {
				-1.0f, 1.0f, 0.0f,
				-1.0f,-1.0f, 0.0f,
				1.0f, 1.0f, 0.0f,
				1.0f, -1.0f, 0.0f,
		};
		vertexBuffer = BufferUtils.create(vertex);

		float[] uv = {
				0.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 0.0f,
				1.0f, 1.0f,
		};
		uvBuffer = BufferUtils.create(uv);

		shader.init(Shader.load(GLES20.GL_VERTEX_SHADER, VERTEX_CODE), Shader.load(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_CODE));
		programID = shader.programID;
//		int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
//		GLES20.glShaderSource(vertexShader, VERTEX_CODE);
//		GLES20.glCompileShader(vertexShader);
//
//		int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
//		GLES20.glShaderSource(fragmentShader, FRAGMENT_CODE);
//		GLES20.glCompileShader(fragmentShader);
//
//		programID = GLES20.glCreateProgram();
//		GLES20.glAttachShader(programID, vertexShader);
//		GLES20.glAttachShader(programID, fragmentShader);
//		GLES20.glLinkProgram(programID);

//		mMatrixHandle = GLES20.glGetUniformLocation(programID, "u_MMatrix");
//		positionHandle = GLES20.glGetAttribLocation(programID, "a_Position");
//		uvHandle = GLES20.glGetAttribLocation(programID, "a_UV");
//		texHandle = GLES20.glGetUniformLocation(programID, "u_Tex");
		mMatrixHandle = shader.getUniformLocation("u_MMatrix");
		positionHandle = shader.getAttributeLocation("a_Position");
		uvHandle = shader.getAttributeLocation("a_UV");
		texHandle = shader.getUniformLocation("u_Tex");
	}

	public void draw(float[] matrix) {
		shader.use();
//		GLES20.glUseProgram(programID);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glEnableVertexAttribArray(uvHandle);

		GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, matrix, 0);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
		GLES20.glUniform1i(texHandle, 0);

		//UVバッファの指定
		GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	}
}
