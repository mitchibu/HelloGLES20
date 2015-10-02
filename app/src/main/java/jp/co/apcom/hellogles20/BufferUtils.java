package jp.co.apcom.hellogles20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BufferUtils {
	public static ByteBuffer create(int size) {
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}

	public static FloatBuffer create(float[] data) {
		FloatBuffer fb = create(data.length * 4).asFloatBuffer().put(data);
		fb.position(0);
		return fb;
	}

	public static ShortBuffer create(short[] data) {
		ShortBuffer sb = create(data.length * 2).asShortBuffer().put(data);
		sb.position(0);
		return sb;
	}
}
