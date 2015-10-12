package cn.ihealthbaby.weitaixinpro.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cn.ihealthbaby.weitaixinpro.R;

public class CustomDialog {
	public boolean isNoCancel = true;
	public Dialog dialog;
	private TextView tv_dialog;

	public CustomDialog() {
	}

	public CustomDialog(Context context, String info) {
		createDialog1(context, info);
	}

	public Dialog createDialog1(Context context, String infor) {
		Dialog dialog = new Dialog(context, R.style.myDialogTheme);
		this.isNoCancel = true;
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
//		view.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
		dialog.setContentView(view); //
		tv_dialog = (TextView) view.findViewById(R.id.tv_dialog);
		if (!TextUtils.isEmpty(infor)) {
			tv_dialog.setText(infor);
		}
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				CustomDialog.this.isNoCancel = false;
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		this.dialog = dialog;
		return dialog;
	}

	public void update(String info) {
		tv_dialog.setText(info);
	}

	public void show() {
		dialog.show();
	}

	public void dismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}
}









