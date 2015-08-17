package cn.ihealthbaby.weitaixin.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.ChangePasswordForm;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.data.net.DefaultCallback;
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

    public Handler mHandler=new Handler();
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ButterKnife.bind(this);

        title_text.setText("修改密码");
//      back.setVisibility(View.INVISIBLE);
        tv_reset_password_action_reset.setEnabled(false);

    }

    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }


    public int countTime=10;
    public boolean isSend=true;
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (countTime>0){
                            countTime--;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (countTime >= 1) {
                                        tv_mark_num_text_reset.setText(countTime + "秒之后重发");
                                        isSend = false;
                                    } else {
                                        tv_mark_num_text_reset.setText("发送验证码");
                                        isSend = true;
                                        countTime = 10;
                                        dialog.dismiss();
                                    }
                                }
                            });
                            if(countTime>0)
                                SystemClock.sleep(1000);
                        }
//                    isSend=true;
//                    countTime=10;
                    }
                }).start();;
            }catch (Exception e){
                e.printStackTrace();
                tv_mark_num_text_reset.setText("发送验证码");
                isSend = true;
                countTime = 10;
                dialog.dismiss();
            }
        }
    }


    //0 注册验证码 1 登录验证码 2 修改密码验证码.
    public void getAuthCode(){
        ApiManager.getInstance().accountApi.getAuthCode(WeiTaiXinApplication.getInstance().phone_number, 2, new HttpClientAdapter.Callback<Boolean>() {
            @Override
            public void call(Result<Boolean> t) {
                if (t.isSuccess()) {
                    Boolean data=t.getData();
                    if (data){
                        tv_reset_password_action_reset.setEnabled(true);
                    }else{
                        tv_reset_password_action_reset.setEnabled(false);
                        ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg()+"重新获取验证码");
                    }
                    ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg());
                }else{
                    ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg());
                }
                dialog.dismiss();
            }
        });
    }


    @OnClick(R.id.tv_reset_password_action_reset)
    public void tv_reset_password_action_reset() {
        System.err.println("dsaddadad");
            tvRegistAction2();
    }


    public String newPassword;
//    public String confirmPassword;
    public String mark_number;
    public void tvRegistAction2() {
        newPassword = et_password_reset.getText().toString().trim();
//        confirmPassword = et_password_reset.getText().toString().trim();
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


        dialog=new CustomDialog().createDialog1(this,"密码修改中...");
        dialog.show();


        ChangePasswordForm changePasswordForm=new ChangePasswordForm(Integer.parseInt(mark_number),newPassword);

        ApiManager.getInstance().accountApi.changePassword(changePasswordForm, new HttpClientAdapter.Callback<Boolean>() {
            @Override
            public void call(Result<Boolean> t) {
                if (t.isSuccess()) {
                    Boolean data= t.getData();
                    if (data) {
                        ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), "修改密码成功"+t.getMsg());
                        finish();
                    }else{
                        ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg());
                    }
                }else {
                    ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), t.getMsg());
                }
                dialog.dismiss();
            }
        });
    }


}
