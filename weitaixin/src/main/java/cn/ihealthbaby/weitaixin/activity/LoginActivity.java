package cn.ihealthbaby.weitaixin.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;

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
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.data.net.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.util.Constants;
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
    @Bind(R.id.tv_regist_mark_action_login) TextView tv_regist_mark_action_login;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        title_text.setText("登录");
    }

    @OnClick(R.id.back)
    public void onBack(RelativeLayout view) {
        this.finish();
    }

    private VolleyAdapter adapter;
    private LoginByPasswordForm loginForm;
    private ApiManager instance;
//    private DefaultCallback<User> callable0;
//    private DefaultCallback<UploadModel> callable4;

    String phone_number_login;
    private boolean isLogin=true;
    @OnClick(R.id.tv_login_action)
    public void tv_login_action() {
        if (isLogin) {
            isLogin=false;
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


            RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(this);
            adapter = new VolleyAdapter(this, Constants.SERVER_URL, requestQueue);
            loginForm = new LoginByPasswordForm(phone_number_login, password_login, "123456789", 1.0d, 1.0d);
            ApiManager.init(adapter);
            instance = ApiManager.getInstance();


            /**
             * 登录请求 登录token
             */
            /*callable0 = new DefaultCallback<User>(getApplicationContext(), new Business<User>() {
                @Override
                public void handleData(User data) throws Exception {
                    //LogUtil.v(TAG, "handleData::%s", data);
                    //adapter.setAccountToken(data.getAccountToken());
                    System.err.println("登录AccountToken： " + data.getAccountToken());
                    if (data.getAccountToken()!=null) {

                        adapter.setAccountToken(data.getAccountToken());
                        WeiTaiXinApplication.accountToken=data.getAccountToken();
                        WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
                        WeiTaiXinApplication.getInstance().phone_number=phone_number_login;
                        WeiTaiXinApplication.user=data;
                        System.err.println("User: "+data);
                        ToastUtil.show(LoginActivity.this.getApplicationContext(), "登录成功");
                        WeiTaiXinApplication.getInstance().isLogin=true;
                        LoginActivity.this.finish();
                    }else{
                        ToastUtil.show(LoginActivity.this.getApplicationContext(), "登录失败");
                    }
                    isLogin=true;
                }
            });*/


            dialog=new CustomDialog().createDialog1(this,"登录中...");
            dialog.show();

//            instance.accountApi.loginByPassword(loginForm, callable0);
            instance.accountApi.loginByPassword(loginForm, new HttpClientAdapter.Callback<User>() {
                @Override
                public void call(Result<User> t) {
                    //LogUtil.v(TAG, "handleData::%s", data);
                    //adapter.setAccountToken(data.getAccountToken());
                    User data=t.getData();
//                    System.err.println("登录AccountToken： " + data.getAccountToken());
                    if (t.isSuccess()) {
                        if (data.getAccountToken()!=null) {
                            adapter.setAccountToken(data.getAccountToken());
                            WeiTaiXinApplication.accountToken=data.getAccountToken();
                            WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
                            WeiTaiXinApplication.getInstance().phone_number=phone_number_login;
                            WeiTaiXinApplication.user=data;
                            System.err.println("User: "+data);
                            ToastUtil.show(LoginActivity.this.getApplicationContext(), "登录成功");
                            WeiTaiXinApplication.getInstance().isLogin=true;
                            LoginActivity.this.finish();
                        }else{
                            ToastUtil.show(LoginActivity.this.getApplicationContext(), t.getMsg());
                        }
                    }else{
                        ToastUtil.show(LoginActivity.this.getApplicationContext(), t.getMsg());
                    }
                    isLogin=true;
                    dialog.dismiss();
                }
            });
        }
    }



    @OnClick(R.id.tv_regist_action_login)
    public void tv_regist_action_login() {
        Intent intent=new Intent(getApplicationContext(), RegistActivity.class);
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








