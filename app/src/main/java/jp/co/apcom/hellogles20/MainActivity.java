package jp.co.apcom.hellogles20;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {
	private GLSurfaceView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(view = new GLSurfaceView(this));
		view.setEGLContextClientVersion(2);
		final MainRenderer renderer = new MainRenderer(this);
		view.setRenderer(renderer);
		view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				renderer.sprite.jump();
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}
}
