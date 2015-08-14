package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author Hongjian.Liu 5:20:43 PM Mar 19, 2014
 */
public class ToastUtil {
	private static Toast toast;

	/**
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void showToast(@NonNull Context context, @NonNull String text, int duration) {
		if (toast == null) {
			toast = Toast.makeText(context, text, duration);
		} else {
			toast.setText(text);
			toast.setDuration(duration);
		}
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 150);
		toast.show();
	}

	public static void show(Context context, String text) {
		showToast(context, text, Toast.LENGTH_LONG);
	}

	public static void show(Context context, int id) {
		showToast(context, context.getString(id), Toast.LENGTH_LONG);
	}

	public static void show(Context context, int id, int duration) {
		showToast(context, context.getString(id), duration);
	}

	public static void show(Context context, String text, int duration) {
		showToast(context, text, duration);
	}

	public static void warn(Context context, int id) {
		if (Constants.MODE_TOAST) {
			showToast(context, context.getString(id), Toast.LENGTH_LONG);
		}
	}

	public static void warn(Context context, int id, int duration) {
		if (Constants.MODE_TOAST) {
			showToast(context, context.getString(id), duration);
		}
	}

	public static void warn(Context context, String text) {
		if (Constants.MODE_TOAST) {
			showToast(context, text, Toast.LENGTH_LONG);
		}
	}

	public static void warn(Context context, String text, int duration) {
		if (Constants.MODE_TOAST) {
			showToast(context, text, duration);
		}
	}
}
