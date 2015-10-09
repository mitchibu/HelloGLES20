package jp.co.apcom.hellogles20.sprite;

import android.graphics.Bitmap;
import android.opengl.Matrix;

import jp.co.apcom.hellogles20.gles.Engine;
import jp.co.apcom.hellogles20.gles.Sprite;

public class ChickenSprite extends Sprite {
	float F = 0;
	float y_prev = 0;

	public ChickenSprite(Bitmap bm, float w, float h) {
		super(bm, w, h);
	}

	public void jump() {
		y_prev = getY();
		F = getHeight() / 6;
	}

	@Override
	public void matrix(Engine engine, float[] matrix) {
		int screenWidth = engine.getScreenWidth();
		int screenHeight = engine.getScreenHeight();
		float sx = (getX() / (float)screenWidth) * 2.0f - 1.0f;
		float sy = (getY() / (float)screenHeight) * 2.0f - 1.0f;
		float sw = (getWidth() / (float)screenWidth);
		float sh = (getHeight() / (float)screenHeight);

		Matrix.setIdentityM(matrix, 0);
		Matrix.translateM(matrix, 0, sx + sw, (sy + sh), 0.0f);
		Matrix.scaleM(matrix, 0, sw, sh, 1.0f);
		float degrees = (getY() - y_prev) * 2.0f;
		if(degrees > 90.0f) degrees = 90.0f;
		else if(degrees < -90.0f) degrees = -90.0f;

		Matrix.rotateM(matrix, 0, degrees, 0.0f, 0.0f, 1.0f);
	}

	@Override
	public void update() {
		if(F != 0) {
			float y_temp = getY();
			setY(getY() + (getY() - y_prev) + F);
			y_prev = y_temp;
			F = -1;
		}
	}
}
