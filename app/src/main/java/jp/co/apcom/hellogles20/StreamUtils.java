package jp.co.apcom.hellogles20;

import android.content.Context;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

public class StreamUtils {
	public static String loadString(Context context, int id) throws IOException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(id)));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = in.readLine()) != null) sb.append(line);
			return sb.toString();
		} finally {
			close(in);
		}
	}

	public static void close(Closeable c) {
		if(c != null) try { c.close(); } catch(Exception e) { e.printStackTrace(); }
	}
}
