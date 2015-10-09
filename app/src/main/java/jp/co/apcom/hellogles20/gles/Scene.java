package jp.co.apcom.hellogles20.gles;

import java.util.ArrayList;
import java.util.List;

public class Scene {
	private final Engine engine;
	private final List<Layer> layers = new ArrayList<>();

	public Scene(Engine engine) {
		this.engine = engine;
	}

	public void addLayer(Layer layer) {
		layers.add(layer);
	}

	public void setup() {
		for(Layer layer : layers) {
			layer.setup();
		}
	}

	public void draw() {
		for(Layer layer : layers) {
			layer.draw();
		}
	}
}
