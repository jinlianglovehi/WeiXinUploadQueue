package cn.ihealthbaby.weitaixin.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.ChangePasswordForm;
import cn.ihealthbaby.client.form.LoginByAuthForm;
import cn.ihealthbaby.client.form.LoginByPasswordForm;
import cn.ihealthbaby.client.model.SysMsg;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class LoginSmsAuthCodeActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Bind(R.id.et_phone_number_smsauthcode) EditText et_phone_number_smsauthcode;
    @Bind(R.id.et_mark_number_smsauthcode) EditText et_mark_number_smsauthcode;
    @Bind(R.id.tv_mark_number_text_smsauthcode) TextView tv_mark_number_text_smsauthcode;
    @Bind(R.id.tv_login_action_smsauthcode) TextView tv_login_action_smsauthcode;
    @Bind(R.id.cbPitch) CheckBox cbPitch;
    @Bind(R.id.tvRuleLogin) TextView tvRuleLogin;

    public Handler mHandler=new Handler();
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sms_auth_code);

        ButterKnife.bind(this);

        title_text.setText("短信验证码登录");
//      back.setVisibility(View.INVISIBLE);
//        tv_login_action_smsauthcode.setEnabled(false);
    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }

    @OnClick(R.id.tvRuleLogin)
    public void tvRuleLogin() {
        Intent intent = new Intent(this, ProtocolActivity.class);
        startActivity(intent);
    }



    public boolean isSend=true;
    public CountDownTimer countDownTimer ;
    public boolean isHasAuthCode=false;


    @OnClick(R.id.tv_mark_number_text_smsauthcode)
    public void tv_mark_number_text_smsauthcode() {
        if (isSend) {
            phone_number = et_phone_number_smsauthcode.getText().toString().trim();
            if (TextUtils.isEmpty(phone_number)) {
                ToastUtil.show(getApplicationContext(), "请输入手机号码");
                return;
            }
            if (phone_number.length()!=11) {
                ToastUtil.show(getApplicationContext(), "请输入手机号码必须是11位的数字和字母");
                return;
            }

            try{
                dialog=new CustomDialog().createDialog1(this,"短信验证码发送中...");
                dialog.show();
                getAuthCode();



                countDownTimer=new CountDownTimer(10000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tv_mark_number_text_smsauthcode.setText(millisUntilFinished/1000+"秒之后重发");
                        isSend = false;
                    }

                    @Override
                    public void onFinish() {
                        tv_mark_number_text_smsauthcode.setText("发送验证码");
                        isSend = true;
                        dialog.dismiss();
                    }
                };
                countDownTimer.start();

            }catch (Exception e){
                e.printStackTrace();
                cancel();
            }
        }
    }


    public void cancel(){
        tv_mark_number_text_smsauthcode.setText("发送验证码");
        isSend = true;
        countDownTimer.cancel();
        dialog.dismiss();
    }


    //0 注册验证码 1 登录验证码 2 修改密码验证码.
    public void getAuthCode(){
        ApiManager.getInstance().accountApi.getAuthCode(phone_number, 1, new HttpClientAdapter.Callback<Boolean>() {
            @Override
            public void call(Result<Boolean> t) {
                if (t.isSuccess()) {
                    Boolean data=t.getData();
                    if (data){
                        isHasAuthCode=true;
                    }else{
                        isHasAuthCode=false;
                        cancel();
                        ToastUtil.show(LoginSmsAuthCodeActivity.this.getApplicationContext(), t.getMsg()+"重新获取短信验证码");
                    }
                }else{
                    ToastUtil.show(LoginSmsAuthCodeActivity.this.getApplicationContext(), t.getMsgMap().get("mobile")+"");
                    isHasAuthCode=false;
                    cancel();
                }
                dialog.dismiss();
            }
        }, getRequestTag());
    }


    @OnClick(R.id.tv_login_action_smsauthcode)
    public void tvLoginActionSmsAuthCode() {
        if (isHasAuthCode) {
            if(cbPitch.isChecked()){
                tvLogieActionSmsAuthCode();
            }else{
                ToastUtil.show(getApplicationContext(), "不接受，不能登录哦~~");
            }
        }else{
            ToastUtil.show(getApplicationContext(), "先获取验证码~~");
        }
    }


    public String phone_number;
    public String mark_number;

    public void tvLogieActionSmsAuthCode() {
            phone_number = et_phone_number_smsauthcode.getText().toString().trim();
            mark_number= et_mark_number_smsauthcode.getText().toString().trim();
            if (TextUtils.isEmpty(phone_number)) {
                ToastUtil.show(getApplicationContext(), "请输入手机号码");
                return;
            }
            if (phone_number.length()!=11) {
                ToastUtil.show(getApplicationContext(), "请输入手机号码必须是11位的数字和字母");
                return;
            }
            if (TextUtils.isEmpty(mark_number)) {
                ToastUtil.show(getApplicationContext(), "请输入短信验证码");
                return;
            }
            if (mark_number.length()!=6) {
                ToastUtil.show(getApplicationContext(), "短信验证码必须是6位的数字");
                return;
            }


            final CustomDialog customDialog= new CustomDialog();
            final Dialog dialog=customDialog.createDialog1(this,"登录中...");
            dialog.show();


            LoginByAuthForm loginByAuthForm=new LoginByAuthForm(phone_number,Integer.parseInt(mark_number),"123456789", 1.0d, 1.0d);
            ApiManager.getInstance().accountApi.loginByAuthCode(loginByAuthForm, new HttpClientAdapter.Callback<User>() {
                @Override
                public void call(Result<User> t) {
                    if (customDialog.isNoCancel) {
                        if (t.isSuccess()) {
                            User data = t.getData();
                            if (data != null && data.getAccountToken() != null) {
                                WeiTaiXinApplication.accountToken = data.getAccountToken();
                                WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
                                WeiTaiXinApplication.getInstance().phone_number = phone_number;
                                WeiTaiXinApplication.user = data;
                                ToastUtil.show(LoginSmsAuthCodeActivity.this.getApplicationContext(), "登录成功");
                                WeiTaiXinApplication.getInstance().isLogin = true;
                                LoginSmsAuthCodeActivity.this.finish();
                            } else {
                                ToastUtil.show(LoginSmsAuthCodeActivity.this.getApplicationContext(), t.getMsgMap().get("mobile")+"");
                            }
                        } else {
                            ToastUtil.show(LoginSmsAuthCodeActivity.this.getApplicationContext(), t.getMsgMap().get("mobile")+"");
                        }
                    }
                    dialog.dismiss();
                }
            }, getRequestTag());
    }



}








