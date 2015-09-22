package cn.ihealthbaby.weitaixin.library.util;

import android.app.Activity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by liuhongjian on 15/7/23 14:54.
 */
public class DialogUtil {
	public static SweetAlertDialog sweetAlertDialog;

	public static SweetAlertDialog CreateDialog(Activity activity) {
		sweetAlertDialog = new SweetAlertDialog(activity);
		return sweetAlertDialog;
	}

	public static void dismissDialog() {
		if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
			sweetAlertDialog.dismiss();
		}
	}
}
