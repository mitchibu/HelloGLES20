package jp.co.apcom.hellogles20;

import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import jp.co.apcom.hellogles20.gles.*;
import jp.co.apcom.hellogles20.gles.Sprite;

public class MainActivity extends AppCompatActivity {
	private GLSurfaceView view;
	private Engine engine;
	private Sprite sprite = new Sprite();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView view = new GLSurfaceView(this);
		setContentView(view);
//		view.setEGLContextClientVersion(2);
//		final MainRenderer renderer = new MainRenderer(this);
//		view.setRenderer(renderer);
//		view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
//				renderer.sprite.jump();
				sprite.jump();
			}
		});
		engine = Engine.getInstance(view, new Engine.Listener() {
			@Override
			public void onPrepared(Engine engine) {
				Scene scene = new Scene(engine);
				scene.addSprite(sprite);
				scene.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
				engine.setScene(scene);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
//		view.onPause();
		engine.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		view.onResume();
		engine.resume();
	}
}
