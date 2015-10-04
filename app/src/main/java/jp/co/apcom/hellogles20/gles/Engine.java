package jp.co.apcom.hellogles20.gles;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Engine {
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

	private static Engine engine;

	public static Engine getInstance(GLSurfaceView view, Listener listener) {
		if(engine == null) engine = new Engine(view, listener);
		return engine;
	}

	public static int createBuffer(float[] data) {
		FloatBuffer fb = BufferUtils.create(data);
		final int[] args = new int[1];
		GLES20.glGenBuffers(args.length, args, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, args[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fb.capacity() * 4, fb, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		return args[0];
	}

	public static int createShader(String vertexCode, String fragmentCode) {
		int[] shader = new int[2];
		shader[0] = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(shader[0], vertexCode);
		GLES20.glCompileShader(shader[0]);

		shader[1] = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(shader[1], fragmentCode);
		GLES20.glCompileShader(shader[1]);

		int id = GLES20.glCreateProgram();
		for(int i : shader) {
			GLES20.glAttachShader(id, i);
			GLES20.glDeleteShader(i);
		}
		GLES20.glLinkProgram(id);
		return id;
	}

	private final GLSurfaceView surfaceView;
	private final Listener listener;

	private int screenWidth;
	private int screenHeight;
	private int programID;
	private int aPosition;
	private int aTextureCoord;
	private int sTexture;
	private int bufferID;
	private int frameBufferID;
	private int textureID;
	private Scene scene = null;

	private Engine(GLSurfaceView view, Listener listener) {
		this.listener = listener;
		surfaceView = view;
		view.setEGLContextClientVersion(2);
		view.setRenderer(new GLSurfaceView.Renderer() {
			@Override
			public void onSurfaceCreated(GL10 gl, EGLConfig config) {
				setup();
			}

			@Override
			public void onSurfaceChanged(GL10 gl, int width, int height) {
				configure(width, height);
			}

			@Override
			public void onDrawFrame(GL10 gl) {
				draw();
			}
		});
		view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	public void resume() {
		surfaceView.onResume();
	}

	public void pause() {
		surfaceView.onPause();
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScene(final Scene scene) {
		surfaceView.queueEvent(new Runnable() {
			@Override
			public void run() {
				scene.setup();
				Engine.this.scene = scene;
			}
		});
	}

	private void setup() {
		bufferID = createBuffer(new float[] {
				// X, Y, Z, U, V
				-1.0f, 1.0f, 0.0f, 0.0f, 1.0f,	// 左上
				1.0f, 1.0f, 0.0f, 1.0f, 1.0f,	// 右上
				-1.0f, -1.0f, 0.0f, 0.0f, 0.0f,	// 左下
				1.0f, -1.0f, 0.0f, 1.0f, 0.0f	// 右下
		});
		programID = createShader(DEFAULT_VERTEX_SHADER, DEFAULT_FRAGMENT_SHADER);
		aPosition = GLES20.glGetAttribLocation(programID, "aPosition");
		aTextureCoord = GLES20.glGetAttribLocation(programID, "aTextureCoord");
		sTexture = GLES20.glGetUniformLocation(programID, "sTexture");

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private void configure(int width, int height) {
		screenWidth = width;
		screenHeight = height;

		final int[] args = new int[1];
		GLES20.glGenFramebuffers(args.length, args, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, args[0]);
		frameBufferID = args[0];

		GLES20.glGenRenderbuffers(args.length, args, 0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, args[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, args[0]);
//		mRenderbufferName = args[0];

		GLES20.glGenTextures(args.length, args, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, args[0]);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, args[0], 0);
		textureID = args[0];

		final int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if(status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Failed to initialize framebuffer object " + status);
		}
		if(listener != null) listener.onPrepared(this);
	}

	private void draw() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferID);
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		if(scene != null) scene.draw();

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		GLES20.glUseProgram(programID);
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferID);

		GLES20.glEnableVertexAttribArray(aPosition);
		GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 20, 0);

		GLES20.glEnableVertexAttribArray(aTextureCoord);
		GLES20.glVertexAttribPointer(aTextureCoord, 2, GLES20.GL_FLOAT, false, 20, 12);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
		GLES20.glUniform1i(sTexture, 0);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		GLES20.glDisableVertexAttribArray(aPosition);
		GLES20.glDisableVertexAttribArray(aTextureCoord);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

	public interface Listener {
		void onPrepared(Engine engine);
	}
}
