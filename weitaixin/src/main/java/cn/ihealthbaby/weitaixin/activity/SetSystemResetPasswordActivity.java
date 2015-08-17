package cn.ihealthbaby.weitaixin.activity;

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
import cn.ihealthbaby.client.form.ChangePasswordForm;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.data.net.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;


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
            }
        }
    }


    private DefaultCallback<Boolean> callableAuthCode;
    //0 注册验证码 1 登录验证码 2 修改密码验证码.
    public void getAuthCode(){
        callableAuthCode = new DefaultCallback<Boolean>(getApplicationContext(), new Business<Boolean>() {
            @Override
            public void handleData(Boolean data) throws Exception {
                if (data){
                    tv_reset_password_action_reset.setEnabled(true);
                }else{
                    tv_reset_password_action_reset.setEnabled(false);
                    Toast.makeText(SetSystemResetPasswordActivity.this.getApplicationContext(), "重新获取验证码 " + data, Toast.LENGTH_LONG).show();
                }
                System.out.println("data: "+data);
                Toast.makeText(SetSystemResetPasswordActivity.this.getApplicationContext(), "data: " + data, Toast.LENGTH_LONG).show();
            }
        });

        ApiManager.getInstance().accountApi.getAuthCode(WeiTaiXinApplication.getInstance().phone_number, 2, callableAuthCode);
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



        ChangePasswordForm changePasswordForm=new ChangePasswordForm(Integer.parseInt(mark_number),newPassword);


        ApiManager.getInstance().accountApi.changePassword(changePasswordForm, new DefaultCallback<Boolean>(getApplicationContext(), new Business<Boolean>() {
            @Override
            public void handleData(Boolean data) throws Exception {
                ToastUtil.show(SetSystemResetPasswordActivity.this.getApplicationContext(), "修改密码成功");
                finish();
            }
        }));
    }


}
