package jp.co.apcom.hellogles20;

import android.opengl.GLES20;

//シェーダ操作
public class GLES {
	//頂点シェーダのコード
	private final static String VERTEX_CODE=
			"uniform mat4 u_MMatrix;"+
					"attribute vec4 a_Position;"+
					"attribute vec2 a_UV;"+
					"varying vec2 v_UV;"+
					"void main(){"+
					"gl_Position=u_MMatrix*a_Position;"+
					"v_UV=a_UV;"+
					"}";

	//フラグメントシェーダのコード
	private final static String FRAGMENT_CODE=
			"precision mediump float;"+
					"varying vec2 v_UV;" +
					"uniform sampler2D u_Tex;"+
					"void main(){"+
					"gl_FragColor=texture2D(u_Tex,v_UV);"+
					"}";

	//システム
	private static int program;//プログラムオブジェクト

	//ハンドル
	public static int mMatrixHandle; //モデルビュー行列ハンドル
	public static int positionHandle;//位置ハンドル
	public static int uvHandle;      //UVハンドル
	public static int texHandle;     //テクスチャハンドル

	//プログラムの生成
	public static void makeProgram() {
		//シェーダーオブジェクトの生成
		int vertexShader=loadShader(GLES20.GL_VERTEX_SHADER,VERTEX_CODE);
		int fragmentShader=loadShader(GLES20.GL_FRAGMENT_SHADER,FRAGMENT_CODE);

		//プログラムオブジェクトの生成
		program=GLES20.glCreateProgram();
		GLES20.glAttachShader(program,vertexShader);
		GLES20.glAttachShader(program,fragmentShader);
		GLES20.glLinkProgram(program);

		//ハンドルの取得
		mMatrixHandle=GLES20.glGetUniformLocation(program,"u_MMatrix");
		positionHandle=GLES20.glGetAttribLocation(program,"a_Position");
		uvHandle=GLES20.glGetAttribLocation(program,"a_UV");
		texHandle=GLES20.glGetUniformLocation(program,"u_Tex");

		//プログラムオブジェクトの利用開始
		GLES20.glUseProgram(program);
	}

	//シェーダーオブジェクトの生成
	private static int loadShader(int type,String shaderCode) {
		int shader=GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader,shaderCode);
		GLES20.glCompileShader(shader);
		return shader;
	}
}