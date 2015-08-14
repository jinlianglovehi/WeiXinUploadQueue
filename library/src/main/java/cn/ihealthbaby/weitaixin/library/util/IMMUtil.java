package cn.ihealthbaby.weitaixin.library.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 *
 */
public class IMMUtil {
    private View mView;

    public static void hide(Activity context) {
        if (null == context) {
            return;
        }

        if (null == context.getCurrentFocus()) {
            return;
        }

        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setView(View view) {
        mView = view;
    }

    private IMMBroadcastReceiver mIMMBroadcastReceiver;

    public class IMMBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_INPUT_METHOD_CHANGED)) {
                InputMethodManager m = (InputMethodManager) mView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                //m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                if (m.isActive()) {

                } else {

                }
            }
        }
    }
}
