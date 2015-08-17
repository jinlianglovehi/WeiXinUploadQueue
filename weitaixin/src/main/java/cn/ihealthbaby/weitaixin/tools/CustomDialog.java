package cn.ihealthbaby.weitaixin.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import cn.ihealthbaby.weitaixin.R;


public class CustomDialog {

	public CustomDialog(){

	}

	public boolean isNoCancel=true;
	public Dialog dialog;

	public Dialog createDialog1(Context context, String infor) {
		Dialog dialog = new Dialog(context, R.style.myDialogTheme);
		this.isNoCancel=true;
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
//		view.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
		dialog.setContentView(view); //
		TextView tv_dialog = (TextView) view.findViewById(R.id.tv_dialog);
		if (!TextUtils.isEmpty(infor)) {
			tv_dialog.setText(infor);
		}
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				CustomDialog.this.isNoCancel=false;
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		this.dialog=dialog;
		return dialog;
	}

	public void dismiss(){
		if (dialog!=null) {
			dialog.dismiss();
			dialog=null;
		}
	}

}









