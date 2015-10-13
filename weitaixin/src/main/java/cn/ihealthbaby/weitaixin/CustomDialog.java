package cn.ihealthbaby.weitaixin;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class CustomDialog {


    public Dialog dialog;

    public CustomDialog() {

    } public CustomDialog(Context context,String info) {
        createDialog1(context, info);
    }

    public Dialog createDialog1(Context context, String infor) {
        Dialog dialog = new Dialog(context, R.style.myDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
        dialog.setContentView(view);
        TextView tv_dialog = (TextView) view.findViewById(R.id.tv_dialog);
        if (!TextUtils.isEmpty(infor)) {
            tv_dialog.setText(infor);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        this.dialog = dialog;
        return dialog;
    }

    public void dismiss() {
        if (dialog != null&& dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }


}




