package cn.ihealthbaby.weitaixin.ui.login;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import cn.ihealthbaby.client.form.LoginByPasswordForm;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
//
    @Bind(R.id.et_phone_number_login) EditText et_phone_number_login;
    @Bind(R.id.et_password_login) EditText et_password_login;
    @Bind(R.id.tv_login_action) TextView tv_login_action;
    @Bind(R.id.tv_regist_action_login) TextView tv_regist_action_login;
    @Bind(R.id.tv_loginsms_action_login) TextView tv_loginsms_action_login;
    @Bind(R.id.ivShowPassword) CheckBox ivShowPassword;



// 131 6140 1474   密码 123456
// 152 1001 5381   密码 123456


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        title_text.setText("登录");
        ivShowPassword.setTag("0");
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.ivShowPassword)
    public void ivShowPassword() {
        if ("0".equals(ivShowPassword.getTag())) {
            et_password_login.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivShowPassword.setTag("1");
//            ivShowPassword.setSelected(true);
        }else{
            et_password_login.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivShowPassword.setTag("0");
//            ivShowPassword.setSelected(false);
        }
    }



    private LoginByPasswordForm loginForm;
    private ApiManager instance;

    String phone_number_login;
    @OnClick(R.id.tv_login_action)
    public void tvLoginAction() {
            phone_number_login = et_phone_number_login.getText().toString().trim();
            String password_login = et_password_login.getText().toString().trim();
            if (TextUtils.isEmpty(phone_number_login)) {
                ToastUtil.show(getApplicationContext(), "请输入手机号");
                return;
            }
            if (phone_number_login.length()!=11) {
                ToastUtil.show(getApplicationContext(), "手机号必须是11位");
                return;
            }
            if (TextUtils.isEmpty(password_login)) {
                ToastUtil.show(getApplicationContext(), "请输入密码");
                return;
            }
            if (password_login.length()<6||password_login.length()>20) {
                ToastUtil.show(getApplicationContext(), "密码必须是6~20位的数字和字母");
                return;
            }


            final CustomDialog customDialog = new CustomDialog();
            final Dialog dialog=customDialog.createDialog1(this,"登录中...");
            dialog.show();


            loginForm = new LoginByPasswordForm(phone_number_login, password_login, "123456789", 1.0d, 1.0d);
            instance = ApiManager.getInstance();

            instance.accountApi.loginByPassword(loginForm, new HttpClientAdapter.Callback<User>() {
                @Override
                public void call(Result<User> t) {
                    if (customDialog.isNoCancel) {
                        if (t.isSuccess()) {
                            User data=t.getData();
                            if (data!=null&&data.getAccountToken()!=null) {
                                WeiTaiXinApplication.accountToken=data.getAccountToken();
                                WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
                                WeiTaiXinApplication.getInstance().phone_number=phone_number_login;
                                WeiTaiXinApplication.getInstance().saveUser(data);
                                ToastUtil.show(LoginActivity.this.getApplicationContext(), "登录成功");
                                WeiTaiXinApplication.getInstance().isLogin=true;

                                String value = WeiTaiXinApplication.getInstance().getValue("InfoEdit", "");
                                if(data.getIsInit()){
                                    Intent intent=new Intent(getApplicationContext(),InfoEditActivity.class);
                                    startActivity(intent);
                                }

                                LoginActivity.this.finish();
                            }else{
                                ToastUtil.show(LoginActivity.this.getApplicationContext(), t.getMsgMap().get("account").toString()+"");
                            }
                        }else{
                            ToastUtil.show(LoginActivity.this.getApplicationContext(), t.getMsgMap().get("account")+"");
                        }
                    }
                    dialog.dismiss();
                }
            }, getRequestTag());
    }



    @OnClick(R.id.tv_regist_action_login)
    public void tvRegistActionLogin() {
        Intent intent=new Intent(getApplicationContext(), RegistActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.tv_loginsms_action_login)
    public void tvLoginsmsActionLogin() {
        Intent intent=new Intent(getApplicationContext(), LoginSmsAuthCodeActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (WeiTaiXinApplication.getInstance().isLogin) {
            finish();
        }
    }

}








