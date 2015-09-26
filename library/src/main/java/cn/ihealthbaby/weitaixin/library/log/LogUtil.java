package cn.ihealthbaby.weitaixin.library.log;

import android.util.Log;

import cn.ihealthbaby.weitaixin.library.util.Constants;

/**
 * Created by Think on 2015/6/16.
 */
public class LogUtil {
	/**
	 * MODE_DEGUB: true 时开启log， false时，关闭log
	 */
	private static final boolean MODE_DEGUB = Constants.MODE_LOG;
	private static final int LEVEL_THRESHOLD = Log.VERBOSE;

	private static void println(String tag, int level, String msg, Object... object) {
		if (!MODE_DEGUB && level >= LEVEL_THRESHOLD) {
			System.err.println("errtlrLEVEL_THRESHOLDerr"+object);
			String string = String.format(msg, object);
			print(tag, level, string);
		}
	}

	private static void print(String tag, int level, String string) {
		switch (level) {
			case Log.VERBOSE:
				Log.v(tag, string);
				break;
			case Log.DEBUG:
				Log.d(tag, string);
				break;
			case Log.INFO:
				Log.i(tag, string);
				break;
			case Log.WARN:
				Log.w(tag, string);
				break;
			case Log.ERROR:
				Log.e(tag, string);
				break;
			case Log.ASSERT:
				Log.wtf(tag, string);
				break;
			default:
				break;
		}
	}

	public static void v(String tag, String msg, Object... object) {
		println(tag, Log.VERBOSE, msg, object);
	}

	public static void d(String tag, String msg, Object... object) {
		println(tag, Log.DEBUG, msg, object);
	}

	public static void i(String tag, String msg, Object... object) {
		println(tag, Log.INFO, msg, object);
	}

	public static void w(String tag, String msg, Object... object) {
		println(tag, Log.WARN, msg, object);
	}

	public static void e(String tag, String msg, Object... object) {
		println(tag, Log.ERROR, msg, object);
	}
}
