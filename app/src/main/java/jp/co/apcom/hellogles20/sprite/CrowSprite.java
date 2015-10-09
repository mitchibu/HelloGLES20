package jp.co.apcom.hellogles20.sprite;

import android.graphics.Bitmap;
import android.opengl.Matrix;

import jp.co.apcom.hellogles20.gles.Engine;
import jp.co.apcom.hellogles20.gles.Sprite;

public class CrowSprite extends Sprite {
	public CrowSprite(Bitmap bm, float w, float h) {
		super(bm, w, h);
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
	}

	@Override
	public void update() {
	}
}
