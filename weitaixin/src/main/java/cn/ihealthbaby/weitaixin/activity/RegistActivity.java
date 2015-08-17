package cn.ihealthbaby.weitaixin.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.LoginByPasswordForm;
import cn.ihealthbaby.client.form.RegForm;
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

public class RegistActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
//

    @Bind(R.id.et_phone_number) EditText et_phone_number;
    @Bind(R.id.et_password) EditText et_password;
    @Bind(R.id.et_mark_number) EditText et_mark_number;
    @Bind(R.id.iv_agree_register) ImageView iv_agree_register;
    @Bind(R.id.tv_rule_register) TextView tv_rule_register;
    @Bind(R.id.tv_regist_action) TextView tv_regist_action;
    @Bind(R.id.tv_mark_num_text) TextView tv_mark_num_text;

    public Handler mHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);

        title_text.setText("手机号注册");

        RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(this);
        adapter = new VolleyAdapter(this, Constants.SERVER_URL, requestQueue);
        ApiManager.init(adapter);
        instance = ApiManager.getInstance();

        tv_regist_action.setEnabled(false);
    }



    @OnClick(R.id.back)
    public void onBack(RelativeLayout view) {
        this.finish();
    }

    public Dialog dialog;
    public int countTime=10;
    public boolean isSend=true;
    @OnClick(R.id.tv_mark_num_text)
    public void tv_mark_num_text() {
        if (isSend) {
            phone_number = et_phone_number.getText().toString().trim();
            if (TextUtils.isEmpty(phone_number)) {
                ToastUtil.show(getApplicationContext(), "请输入手机号");
                return;
            }
            if (phone_number.length()!=11) {
                ToastUtil.show(getApplicationContext(), "手机号必须是11位");
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
                                        tv_mark_num_text.setText(countTime + "秒之后重发");
                                        isSend = false;
                                    } else {
                                        tv_mark_num_text.setText("发送验证码");
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
                tv_mark_num_text.setText("发送验证码");
                isSend = true;
                countTime = 10;
                dialog.dismiss();
            }
        }
    }


//    private DefaultCallback<Boolean> callableAuthCode;

    //0 注册验证码 1 登录验证码 2 修改密码验证码.
    public void getAuthCode(){
/*
        callableAuthCode = new DefaultCallback<Boolean>(getApplicationContext(), new Business<Boolean>() {
            @Override
            public void handleData(Boolean data) throws Exception {
                if (data){
                    tv_regist_action.setEnabled(true);
                }else{
                    tv_regist_action.setEnabled(false);
                    Toast.makeText(RegistActivity.this.getApplicationContext(), "重新获取验证码 " + data, Toast.LENGTH_LONG).show();
                }
                System.out.println("data: "+data);
                Toast.makeText(RegistActivity.this.getApplicationContext(), "data: "+data ,Toast.LENGTH_LONG).show();
            }
        });
*/

//        instance.accountApi.getAuthCode(phone_number,0,callableAuthCode);
        instance.accountApi.getAuthCode(phone_number, 0, new HttpClientAdapter.Callback<Boolean>() {
            @Override
            public void call(Result<Boolean> t) {
                if (t.isSuccess()) {
                   Boolean data= t.getData();
                    if (data){
                        tv_regist_action.setEnabled(true);
                    }else{
                        tv_regist_action.setEnabled(false);
                        ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsg()+",请重新获取验证码");
                    }
                }else{
                    ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsg());
                }
               dialog.dismiss();
            }
        });
    }


    private VolleyAdapter adapter;
    private RegForm regForm;
    private ApiManager instance;
//    private DefaultCallback<User> callable0;

    @OnClick(R.id.tv_regist_action)
    public void tvRegistAction() {
        System.err.println("dsaddadad");
            tvRegistAction2();
    }

    public String phone_number;
    public String password;
    public String mark_number;
    public void tvRegistAction2() {
         phone_number = et_phone_number.getText().toString().trim();
         password = et_password.getText().toString().trim();
        mark_number= et_mark_number.getText().toString().trim();
        if (TextUtils.isEmpty(phone_number)) {
            ToastUtil.show(getApplicationContext(), "请输入手机号");
            return;
        }
        if (phone_number.length()!=11) {
            ToastUtil.show(getApplicationContext(), "手机号必须是11位");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(getApplicationContext(), "请输入密码");
            return;
        }
        if (password.length()<6&&password.length()>20) {
            ToastUtil.show(getApplicationContext(), "密码必须是6~20位的数字和字母");
            return;
        }
        if (TextUtils.isEmpty(mark_number)) {
            ToastUtil.show(getApplicationContext(), "请输入验证码");
            return;
        }



        regForm = new RegForm(phone_number, password, Integer.parseInt(mark_number), "123456789", 1.0d, 1.0d);

//        callable0 = new DefaultCallback<User>(getApplicationContext(), new Business<User>() {
//            @Override
//            public void handleData(User data) throws Exception {
//                //LogUtil.v(TAG, "handleData::%s", data);
//                //adapter.setAccountToken(data.getAccountToken());
//                System.err.println("注册AccountToken： " + data.getAccountToken());
//                if (data.getAccountToken()!=null) {
//                    ToastUtil.show(RegistActivity.this.getApplicationContext(), "注册成功");
//                    loginActionOfReg();
//                }else{
//                    ToastUtil.show(RegistActivity.this.getApplicationContext(), "注册失败");
//                }
//            }
//        });

        dialog=new CustomDialog().createDialog1(this,"注册中...");
        dialog.show();
//        instance.accountApi.register(regForm, callable0);
        instance.accountApi.register(regForm, new HttpClientAdapter.Callback<User>() {
            @Override
            public void call(Result<User> t) {
                if (t.isSuccess()) {
                    User data= t.getData();
                    System.err.println("注册AccountToken： " + data.getAccountToken());
                    if (data.getAccountToken()!=null) {
                        ToastUtil.show(RegistActivity.this.getApplicationContext(), "注册成功");
                        loginActionOfReg();
                    }else{
                        ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsg());
                    }
                }else {
                    ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsg());
                }
                dialog.dismiss();
            }
        });
    }


    private LoginByPasswordForm loginForm;
    public void loginActionOfReg(){
        RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(this);
        adapter = new VolleyAdapter(this, Constants.SERVER_URL, requestQueue);
        loginForm = new LoginByPasswordForm(phone_number, password, "123456789", 1.0d, 1.0d);
        ApiManager.init(adapter);
        instance = ApiManager.getInstance();

/*
        callable0 = new DefaultCallback<User>(getApplicationContext(), new Business<User>() {
            @Override
            public void handleData(User data) throws Exception {
                //LogUtil.v(TAG, "handleData::%s", data);
                //adapter.setAccountToken(data.getAccountToken());
                System.err.println("登录AccountToken： " + data.getAccountToken());
                if (data.getAccountToken()!=null) {
//                    ToastUtil.show(RegistActivity.this.getApplicationContext(),"登录成功");
                    WeiTaiXinApplication.getInstance().isLogin=true;
                    adapter.setAccountToken(data.getAccountToken());
                    WeiTaiXinApplication.accountToken=data.getAccountToken();
                    WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
                    WeiTaiXinApplication.getInstance().phone_number=phone_number;
                    WeiTaiXinApplication.user=data;

                    Intent intent=new Intent(RegistActivity.this.getApplicationContext(), InfoEditActivity.class);
                    startActivity(intent);
                    RegistActivity.this.finish();
                }else{
                    ToastUtil.show(RegistActivity.this.getApplicationContext(), "登录失败");
                }
            }
        });
*/


//        instance.accountApi.loginByPassword(loginForm, callable0);
        instance.accountApi.loginByPassword(loginForm, new HttpClientAdapter.Callback<User>() {
            @Override
            public void call(Result<User> t) {
                if (t.isSuccess()) {
                    User data=t.getData();
                    System.err.println("登录AccountToken： " + data.getAccountToken());
                    if (data.getAccountToken()!=null) {
//                    ToastUtil.show(RegistActivity.this.getApplicationContext(),"登录成功");
                        WeiTaiXinApplication.getInstance().isLogin=true;
                        adapter.setAccountToken(data.getAccountToken());
                        WeiTaiXinApplication.accountToken=data.getAccountToken();
                        WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
                        WeiTaiXinApplication.getInstance().phone_number=phone_number;
                        WeiTaiXinApplication.user=data;

                        Intent intent=new Intent(RegistActivity.this.getApplicationContext(), InfoEditActivity.class);
                        startActivity(intent);
                        RegistActivity.this.finish();
                    }else{
                        ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsg());
                    }
                }else {
                    ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsg());
                }
            }
        });
    }


}
