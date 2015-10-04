package jp.co.apcom.hellogles20.gles;

public class Sprite {
	float F = 0;
	float x = 0;
	float y = 0;
	float w = 144;
	float h = 144;
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
