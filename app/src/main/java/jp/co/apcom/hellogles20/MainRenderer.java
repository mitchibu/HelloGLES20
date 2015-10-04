package jp.co.apcom.hellogles20;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainRenderer implements GLSurfaceView.Renderer {
	protected static final String DEFAULT_VERTEX_SHADER =
			"attribute vec4 aPosition;" +
					"attribute vec4 aTextureCoord;" +
					"varying highp vec2 vTextureCoord;" +
					"void main() {" +
					"gl_Position = aPosition;" +
					"vTextureCoord = aTextureCoord.xy;" +
					"}";

	/**
	 * デフォルトの色描画用のピクセル/フラグメントシェーダのソースコードです。
	 */
	protected static final String DEFAULT_FRAGMENT_SHADER =
			"precision mediump float;" +	// 演算精度を指定します。
					"varying highp vec2 vTextureCoord;" +
					"uniform lowp sampler2D sTexture;" +
					"void main() {" +
					"gl_FragColor = texture2D(sTexture, vTextureCoord);" +
					"}";

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

	private final MainActivity activity;
	private final float[] matrix = new float[16];

	private static final int FLOAT_SIZE_BYTES = 4;
	protected static final int VERTICES_DATA_POS_SIZE = 3;
	protected static final int VERTICES_DATA_UV_SIZE = 2;
	protected static final int VERTICES_DATA_STRIDE_BYTES = (VERTICES_DATA_POS_SIZE + VERTICES_DATA_UV_SIZE) * FLOAT_SIZE_BYTES;
	protected static final int VERTICES_DATA_POS_OFFSET = 0 * FLOAT_SIZE_BYTES;
	protected static final int VERTICES_DATA_UV_OFFSET = VERTICES_DATA_POS_OFFSET + VERTICES_DATA_POS_SIZE * FLOAT_SIZE_BYTES;
	private int fboProgramID;
	private FloatBuffer fboBuffer = BufferUtils.create(new float[] {
			// X, Y, Z, U, V
			-1.0f, 1.0f, 0.0f, 0.0f, 1.0f,	// 左上
			1.0f, 1.0f, 0.0f, 1.0f, 1.0f,	// 右上
			-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,	// 左下
			1.0f, -1.0f, 0.0f, 1.0f, 0.0f	// 右下
	});
	private int mVertexBufferName;
	int aPosition;
	int aTextureCoord;
	int sTexture;

	private int programID;
	private int mMatrixHandle;
	private int positionHandle;
	private int uvHandle;
	private int texHandle;
	private int textureID;
	private FloatBuffer spriteBuffer = BufferUtils.create(new float[] {
			// X, Y, Z, U, V
			-1.0f, 1.0f, 0.0f, 0.0f, 0.0f,	// 左上
			1.0f, 1.0f, 0.0f, 1.0f, 0.0f,	// 右上
			-1.0f, -1.0f, 0.0f, 0.0f, 1.0f,	// 左下
			1.0f, -1.0f, 0.0f, 1.0f, 1.0f	// 右下
	});
	private int mSpriteBufferName;

	private int screenWidth;
	private int screenHeight;
	private int mFramebufferName;
	private int mRenderbufferName;
	private int mTexName;

	private long tick;

	public MainRenderer(MainActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		{
			final int[] args = new int[1];
			GLES20.glGenBuffers(args.length, args, 0);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, args[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fboBuffer.capacity() * FLOAT_SIZE_BYTES, fboBuffer, GLES20.GL_STATIC_DRAW);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			mVertexBufferName = args[0];
			// GLES20.glDeleteBuffers(1, new int[]{ mVertexBufferName }, 0);
		}
		{
			final int[] args = new int[1];
			GLES20.glGenBuffers(args.length, args, 0);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, args[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, spriteBuffer.capacity() * FLOAT_SIZE_BYTES, spriteBuffer, GLES20.GL_STATIC_DRAW);
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			mSpriteBufferName = args[0];
		}

		{
			int[] shader = new int[2];
			shader[0] = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
			GLES20.glShaderSource(shader[0], DEFAULT_VERTEX_SHADER);
			GLES20.glCompileShader(shader[0]);

			shader[1] = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
			GLES20.glShaderSource(shader[1], DEFAULT_FRAGMENT_SHADER);
			GLES20.glCompileShader(shader[1]);

			fboProgramID = GLES20.glCreateProgram();
			for(int i : shader) {
				GLES20.glAttachShader(fboProgramID, i);
				GLES20.glDeleteShader(i);
			}
			GLES20.glLinkProgram(fboProgramID);

			aPosition = GLES20.glGetAttribLocation(fboProgramID, "aPosition");
			aTextureCoord = GLES20.glGetAttribLocation(fboProgramID, "aTextureCoord");
			sTexture = GLES20.glGetUniformLocation(fboProgramID, "sTexture");
		}

		{
			int[] shader = new int[2];
			shader[0] = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
			GLES20.glShaderSource(shader[0], VERTEX_CODE);
			GLES20.glCompileShader(shader[0]);

			shader[1] = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
			GLES20.glShaderSource(shader[1], FRAGMENT_CODE);
			GLES20.glCompileShader(shader[1]);

			programID = GLES20.glCreateProgram();
			for(int i : shader) {
				GLES20.glAttachShader(programID, i);
				GLES20.glDeleteShader(i);
			}
			GLES20.glLinkProgram(programID);

			mMatrixHandle = GLES20.glGetUniformLocation(programID, "u_MMatrix");
			positionHandle = GLES20.glGetAttribLocation(programID, "a_Position");
			uvHandle = GLES20.glGetAttribLocation(programID, "a_UV");
			texHandle = GLES20.glGetUniformLocation(programID, "u_Tex");
		}

		{
			int[] args = new int[1];
			GLES20.glGenTextures(args.length, args, 0);
//			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, args[0]);

//			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
			sprite.w = bm.getWidth();
			sprite.h = bm.getHeight();
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm, 0);
			bm.recycle();
			textureID = args[0];
		}

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		screenWidth = width;
		screenHeight = height;

		{
			final int[] args = new int[1];
			GLES20.glGenFramebuffers(args.length, args, 0);
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, args[0]);
			mFramebufferName = args[0];

			GLES20.glGenRenderbuffers(args.length, args, 0);
			GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, args[0]);
			GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
			GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, args[0]);
			mRenderbufferName = args[0];

			GLES20.glGenTextures(args.length, args, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, args[0]);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, args[0], 0);
			mTexName = args[0];

			final int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
			if(status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
				throw new RuntimeException("Failed to initialize framebuffer object " + status);
			}
		}
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebufferName);
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		sprite.update();

		//ウィンドウ座標を正規化デバイス座標に変換(1)
		float sx = (sprite.x / (float)screenWidth) * 2.0f - 1.0f;
		float sy = (sprite.y / (float)screenHeight) * 2.0f - 1.0f;
		float sw = (sprite.w / (float)screenWidth);
		float sh = (sprite.h / (float)screenHeight);

		//モデルビュー行列の移動・拡縮
		Matrix.setIdentityM(matrix, 0);
		Matrix.translateM(matrix, 0, sx + sw, (sy + sh), 0.0f);
		Matrix.scaleM(matrix, 0, sw, sh, 1.0f);
		float degrees = (sprite.y - sprite.y_prev) * 2.0f;
		if(degrees > 90.0f) degrees = 90.0f;
		else if(degrees < -90.0f) degrees = -90.0f;
		Matrix.rotateM(matrix, 0, degrees, 0.0f, 0.0f, 1.0f);

		GLES20.glUseProgram(programID);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mSpriteBufferName);

		GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, matrix, 0);

		GLES20.glEnableVertexAttribArray(uvHandle);
		GLES20.glVertexAttribPointer(uvHandle, VERTICES_DATA_UV_SIZE, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET);

		GLES20.glEnableVertexAttribArray(positionHandle);
		GLES20.glVertexAttribPointer(positionHandle, VERTICES_DATA_POS_SIZE, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
		GLES20.glUniform1i(texHandle, 0);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		GLES20.glDisableVertexAttribArray(positionHandle);
		GLES20.glDisableVertexAttribArray(uvHandle);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		////////////////////////////////////////////////////////////////
		GLES20.glUseProgram(fboProgramID);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferName);

		GLES20.glEnableVertexAttribArray(aPosition);
		GLES20.glVertexAttribPointer(aPosition, VERTICES_DATA_POS_SIZE, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);

		GLES20.glEnableVertexAttribArray(aTextureCoord);
		GLES20.glVertexAttribPointer(aTextureCoord, VERTICES_DATA_UV_SIZE, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName);
		GLES20.glUniform1i(sTexture, 0);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		GLES20.glDisableVertexAttribArray(aPosition);
		GLES20.glDisableVertexAttribArray(aTextureCoord);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		long now = System.currentTimeMillis();
		if(tick != 0) {
			long diff = now - tick;
			// 30fps 1/30spf f:1000/30ms
			float m = 1000.0f / 30.0f;
			long ms = (long)(m - diff);
			android.util.Log.v("test", "ms: " + ms);
			if(ms > 0) {
				try {
					Thread.sleep(ms);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		tick = now;
 	}

	static class Sprite {
		float F = 0;
		float x = 0;
		float y = 0;
		float w = 300;
		float h = 300;
		float y_prev = 0;

		public void jump() {
			y_prev = y;
			F = 30;
		}

		public void update() {
			if(F != 0) {
				float y_temp = y;
				y += (y - y_prev) + F;
				y_prev = y_temp;
				F = -1;
			}
		}
	}
	public final Sprite sprite = new Sprite();
}
