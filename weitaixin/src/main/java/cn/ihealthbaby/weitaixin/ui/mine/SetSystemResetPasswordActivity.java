package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.ChangePasswordForm;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;


public class SetSystemResetPasswordActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

//    @Bind(R.id.et_phone_number_reset) EditText et_phone_number_reset;
    @Bind(R.id.et_password_reset) EditText et_password_reset;
    @Bind(R.id.et_mark_number_reset) EditText et_mark_number_reset;
    @Bind(R.id.tv_mark_num_text_reset) TextView tv_mark_num_text_reset;
    @Bind(R.id.tv_reset_password_action_reset) TextView tv_reset_password_action_reset;
    @Bind(R.id.ivShowPassword)
    CheckBox ivShowPassword;


    public Handler mHandler=new Handler();
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);

        title_text.setText("修改密码");
//      back.setVisibility(View.INVISIBLE);
        ivShowPassword.setTag("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }


    @OnClick(R.id.ivShowPassword)
    public void ivShowPassword() {
        if ("0".equals(ivShowPassword.getTag())) {
            et_password_reset.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivShowPassword.setTag("1");
        }else{
            et_password_reset.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivShowPassword.setTag("0");
        }
    }



    public int countTime=10;
    public boolean isSend=true;
    public CountDownTimer countDownTimer ;
    public boolean isHasAuthCode=false;


    @OnClick(R.id.tv_mark_num_text_reset)
    public void tv_mark_num_text_reset() {
        if (isSend) {
            newPassword = et_password_reset.getText().toString().trim();
            if (TextUtils.isEmpty(newPassword)) {
                ToastUtil.show(getApplicationContext(), "请输入密码");
                return;
            }
            if (newPassword.length()<6&&newPassword.length()>20) {
                ToastUtil.show(getApplicationContext(), "密码必须是6~20位的数字和字母");
                return;
            }

            try{
                dialog=new CustomDialog().createDialog1(this,"验证码发送中...");
                dialog.show();
                getAuthCode();


                countDownTimer=new CountDownTimer(10000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tv_mark_num_text_reset.setText(millisUntilFinished/1000+"秒之后重发");
                        isSend = false;
                    }

                    @Override
                    public void onFinish() {
                        tv_mark_num_text_reset.setText("发送验证码");
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
        tv_mark_num_text_reset.setText("发送验证码");
        isSend = true;
        countDownTimer.cancel();
        dialog.dismiss();
    }


    //0 注册验证码 1 登录验证码 2 修改密码验证码.
    public void getAuthCode(){
        ApiManager.getInstance().accountApi.getAuthCode(WeiTaiXinApplication.getInstance().phone_number, 2, new HttpClientAdapter.Callback<Boolean>() {
            @Override
            public void call(Result<Boolean> t) {
                if (t.isSuccess()) {
                    Boolean data=t.getData();
                    if (data){
                        isHasAuthCode=true;
                    }else{
                        isHasAuthCode=false;
                        cancel();
                        ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg()+"重新获取验证码");
                    }
                }else{
                    ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg());
                    isHasAuthCode=false;
                    cancel();
                }
                dialog.dismiss();
            }
        }, getRequestTag());
    }


    @OnClick(R.id.tv_reset_password_action_reset)
    public void tvResetPasswordActionReset() {
        if (isHasAuthCode) {
            tvRegistAction2();
        }else{
            ToastUtil.show(getApplicationContext(), "先获取验证码~~");
        }

    }


    public String newPassword;
    public String mark_number;
    public void tvRegistAction2() {
        newPassword = et_password_reset.getText().toString().trim();
        mark_number= et_mark_number_reset.getText().toString().trim();
        if (TextUtils.isEmpty(newPassword)) {
            ToastUtil.show(getApplicationContext(), "请输入密码");
            return;
        }
        if (newPassword.length()<6&&newPassword.length()>20) {
            ToastUtil.show(getApplicationContext(), "密码必须是6~20位的数字和字母");
            return;
        }
        if (TextUtils.isEmpty(mark_number)) {
            ToastUtil.show(getApplicationContext(), "请输入验证码");
            return;
        }


        final CustomDialog customDialog= new CustomDialog();
        final Dialog dialog=customDialog.createDialog1(this,"登录中...");
        dialog.show();


        ChangePasswordForm changePasswordForm=new ChangePasswordForm(Integer.parseInt(mark_number),newPassword);

        ApiManager.getInstance().accountApi.changePassword(changePasswordForm, new HttpClientAdapter.Callback<Boolean>() {
            @Override
            public void call(Result<Boolean> t) {
                if (customDialog.isNoCancel) {
                    if (t.isSuccess()) {
                        Boolean data= t.getData();
                        if (data) {
                            ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), "修改密码成功");
                            finish();
                        }else{
                            ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg());
                        }
                    }else {
                        ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg());
                    }
                }
                dialog.dismiss();
            }
        }, getRequestTag());
    }


}
