package jp.co.apcom.hellogles20;

import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import jp.co.apcom.hellogles20.gles.*;
import jp.co.apcom.hellogles20.gles.Sprite;
import jp.co.apcom.hellogles20.scene.MainScene;
import jp.co.apcom.hellogles20.sprite.ChickenSprite;
import jp.co.apcom.hellogles20.sprite.CrowSprite;

public class MainActivity extends AppCompatActivity {
	private Engine engine;
	MainScene scene;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView view = new GLSurfaceView(this);
		setContentView(view);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				scene.onClick();
			}
		});
		engine = Engine.getInstance(view, new Engine.Listener() {
			@Override
			public void onPrepared(Engine engine) {
				engine.setScene(scene);
			}
		});
		scene = new MainScene(this, engine);
	}

	@Override
	protected void onPause() {
		super.onPause();
		engine.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		engine.resume();
	}
}
