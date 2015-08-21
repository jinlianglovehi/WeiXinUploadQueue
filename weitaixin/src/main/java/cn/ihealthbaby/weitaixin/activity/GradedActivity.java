package cn.ihealthbaby.weitaixin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.LoginByPasswordForm;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class GradedActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
//
//    @Bind(R.id.tv_login_action) TextView tv_login_action;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graded);
        ButterKnife.bind(this);

        title_text.setText("高危评分");

        showDialogView().show();
    }

    private Dialog showDialogView() {
        Dialog dialog = new Dialog(this, R.style.myDialogTheme2);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_graded, null);
//		view.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setContentView(view);
        TextView gradedText = (TextView) view.findViewById(R.id.tvGradedText);
        TextView gradedYes = (TextView) view.findViewById(R.id.tvGradedYes);
        TextView gradedNo = (TextView) view.findViewById(R.id.tvGradedNo);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        gradedYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        gradedNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return dialog;
    }

    @OnClick(R.id.back)
    public void onBack(RelativeLayout view) {
        this.finish();
    }

    private ApiManager instance;

    public void tvLoginAction() {
//            final CustomDialog customDialog = new CustomDialog();
//            final Dialog dialog=customDialog.createDialog1(this,"登录中...");
//            dialog.show();
//



//
//            loginForm = new LoginByPasswordForm(phone_number_login, password_login, "123456789", 1.0d, 1.0d);
//            instance = ApiManager.getInstance();
//
//            instance.accountApi.loginByPassword(loginForm, new HttpClientAdapter.Callback<User>() {
//                @Override
//                public void call(Result<User> t) {
//                    if (customDialog.isNoCancel) {
//                        if (t.isSuccess()) {
//                            User data=t.getData();
//                            if (data!=null&&data.getAccountToken()!=null) {
//                                WeiTaiXinApplication.accountToken=data.getAccountToken();
//                                WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
//                                WeiTaiXinApplication.getInstance().phone_number=phone_number_login;
//                                WeiTaiXinApplication.user=data;
//                                ToastUtil.show(GradedActivity.this.getApplicationContext(), "登录成功");
//                                WeiTaiXinApplication.getInstance().isLogin=true;
//                                GradedActivity.this.finish();
//                            }else{
//                                ToastUtil.show(GradedActivity.this.getApplicationContext(), t.getMsg());
//                            }
//                        }else{
//                            ToastUtil.show(GradedActivity.this.getApplicationContext(), t.getMsg());
//                        }
//                    }
//                    dialog.dismiss();
//                }
//            });
    }



}








