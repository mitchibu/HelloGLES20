package jp.co.apcom.hellogles20.scene;

import android.content.Context;
import android.graphics.BitmapFactory;

import jp.co.apcom.hellogles20.R;
import jp.co.apcom.hellogles20.gles.Engine;
import jp.co.apcom.hellogles20.gles.Scene;
import jp.co.apcom.hellogles20.gles.SpriteLayer;
import jp.co.apcom.hellogles20.sprite.ChickenSprite;
import jp.co.apcom.hellogles20.sprite.CrowSprite;

public class MainScene extends Scene {
	SpriteLayer layer;
	private ChickenSprite sprite;
	private CrowSprite crow;

	public MainScene(Context context, Engine engine) {
		super(engine);

		int size = context.getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);
		sprite = new ChickenSprite(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher), size, size);
		crow = new CrowSprite(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher), size, size);
		crow.setX(100);
		crow.setY(100);

		SpriteLayer layer = new SpriteLayer(engine);
		layer.addSprite(sprite);
		layer.addSprite(crow);
		addLayer(layer);
	}

	public void onClick() {
		sprite.jump();
	}
}
