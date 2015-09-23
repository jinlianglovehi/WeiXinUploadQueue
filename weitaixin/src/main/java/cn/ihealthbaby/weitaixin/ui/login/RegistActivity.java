package cn.ihealthbaby.weitaixin.ui.login;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.LoginByPasswordForm;
import cn.ihealthbaby.client.form.RegForm;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.service.AdviceSettingService;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
import cn.ihealthbaby.weitaixin.ui.mine.GradedActivity;

public class RegistActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
//

    @Bind(R.id.et_phone_number)
    EditText et_phone_number;
    @Bind(R.id.etPassword)
    EditText etPassword;
    @Bind(R.id.et_mark_number)
    EditText et_mark_number;
    @Bind(R.id.iv_agree_register)
    ImageView ivAgreeRegister;
    @Bind(R.id.tvRuleRegister)
    TextView tvRuleRegister;
    @Bind(R.id.tv_regist_action)
    TextView tv_regist_action;
    @Bind(R.id.tv_mark_num_text)
    TextView tv_mark_num_text;
    @Bind(R.id.ivShowPassword)
    CheckBox ivShowPassword;

    public Handler mHandler = new Handler();
    private boolean mChecked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);

        title_text.setText("手机号注册");


//      tv_regist_action.setEnabled(false);
        ivShowPassword.setTag("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.ivShowPassword)
    public void ivShowPassword() {
        if ("0".equals(ivShowPassword.getTag())) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivShowPassword.setTag("1");
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivShowPassword.setTag("0");
        }
    }

    @OnClick(R.id.back)
    public void onBack(RelativeLayout view) {
        this.finish();
    }

    @OnClick(R.id.tvRuleRegister)
    public void tvRuleRegister() {
        Intent intent = new Intent(this, ProtocolActivity.class);
        startActivity(intent);
    }


    public boolean isSend = true;

    public CountDownTimer countDownTimer;

    @OnClick(R.id.tv_mark_num_text)
    public void tvMarkNumText() {
        if (isSend) {
            phone_number = et_phone_number.getText().toString().trim();
            if (TextUtils.isEmpty(phone_number)) {
                ToastUtil.show(getApplicationContext(), "请输入手机号");
                return;
            }
            if (phone_number.length() != 11) {
                ToastUtil.show(getApplicationContext(), "手机号必须是11位");
                return;
            }
            getAuthCode();
        }
    }


    public boolean isHasAuthCode = false;

    //0 注册验证码 1 登录验证码 2 修改密码验证码.
    public void getAuthCode() {
        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "验证码发送中...");
        dialog.show();


        isSend = false;

        ApiManager.getInstance().accountApi.getAuthCode(phone_number, 0, new HttpClientAdapter.Callback<Boolean>() {
            @Override
            public void call(Result<Boolean> t) {
                if (t.getStatus() == Result.SUCCESS) {
                    Boolean data = t.getData();
                    if (data) {
                        tv_mark_num_text.setBackgroundResource(R.color.gray1);
                        tv_mark_num_text.setTextColor(getResources().getColor(R.color.gray2));
                        isHasAuthCode = true;
//                      tv_regist_action.setEnabled(true);
                        try {
                            countDownTimer = new CountDownTimer(60000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    tv_mark_num_text.setText(millisUntilFinished / 1000 + "秒之后重发");
                                    isSend = false;
                                }

                                @Override
                                public void onFinish() {
                                    tv_mark_num_text.setText("发送验证码");
                                    isSend = true;
                                    customDialog.dismiss();
                                    tv_mark_num_text.setBackgroundResource(R.drawable.shape_send_verifycode);
                                    tv_mark_num_text.setTextColor(getResources().getColor(R.color.black0));
                                }
                            };
                            countDownTimer.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            cancel(customDialog);
                        }
                    } else {
                        isHasAuthCode = false;
                        cancel(customDialog);
//                      tv_regist_action.setEnabled(false);
                        ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsgMap()+ ",请重新获取验证码");
                    }
                } else {
                    ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsgMap()+ "");
                    isHasAuthCode = false;
                    cancel(customDialog);
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }

    public void cancel(CustomDialog customDialog) {
        tv_mark_num_text.setText("发送验证码");
        isSend = true;
        countDownTimer.cancel();
        customDialog.dismiss();
    }

    private RegForm regForm;

    @OnClick(R.id.tv_regist_action)
    public void tvRegistAction() {
        if (isHasAuthCode) {
            if (mChecked) {
                tvRegistAction2();
            } else {
                ToastUtil.show(getApplicationContext(), "不接受协议，不能注册哦");
            }
        } else {
            ToastUtil.show(getApplicationContext(), "请先获取验证码");
        }
    }

    public String phone_number;
    public String password;
    public String mark_number;

    public void tvRegistAction2() {
        phone_number = et_phone_number.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        mark_number = et_mark_number.getText().toString().trim();
        if (TextUtils.isEmpty(phone_number)) {
            ToastUtil.show(getApplicationContext(), "请输入手机号");
            return;
        }
        if (phone_number.length() != 11) {
            ToastUtil.show(getApplicationContext(), "手机号必须是11位");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(getApplicationContext(), "请输入密码");
            return;
        }
        if (password.length() < 6 && password.length() > 20) {
            ToastUtil.show(getApplicationContext(), "密码必须是6~20位的数字和字母");
            return;
        }
        if (TextUtils.isEmpty(mark_number)) {
            ToastUtil.show(getApplicationContext(), "请输入验证码");
            return;
        }
        if (mark_number.length() != 6) {
            ToastUtil.show(getApplicationContext(), "验证码必须是6位的数字");
            return;
        }


        regForm = new RegForm(phone_number, password, Integer.parseInt(mark_number), "123456789", 1.0d, 1.0d);
        final CustomDialog customDialog = new CustomDialog();
        final Dialog dialog = customDialog.createDialog1(this, "注册中...");
        dialog.show();
        ApiManager.getInstance().accountApi.register(regForm,
                new DefaultCallback<User>(this, new AbstractBusiness<User>() {
                    @Override
                    public void handleData(User data) {
                        ToastUtil.show(RegistActivity.this.getApplicationContext(), "注册成功");
                        loginActionOfReg();
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        customDialog.dismiss();
                        Intent intent=new Intent(RegistActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                        Intent intent=new Intent(RegistActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }), getRequestTag());
    }


    private LoginByPasswordForm loginForm;

    public void loginActionOfReg() {
        loginForm = new LoginByPasswordForm(phone_number, password, "123456789", 1.0d, 1.0d);

        ApiManager.getInstance().accountApi.loginByPassword(loginForm, new HttpClientAdapter.Callback<User>() {
            @Override
            public void call(Result<User> t) {
                if (t.getStatus() == Result.SUCCESS) {
                    User data = t.getData();
                    if (data.getAccountToken() != null) {
                        WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
                        SPUtil.saveUser(RegistActivity.this,data);

                        Intent intentService = new Intent(getApplicationContext(), AdviceSettingService.class);
                        startService(intentService);

                        if(data.getIsInit()){
                            Intent intentIsInit=new Intent(RegistActivity.this, InfoEditActivity.class);
                            startActivity(intentIsInit);
                            RegistActivity.this.finish();
                            return;
                        }


                        if(!data.getHasRiskscore()&& SPUtil.getHospitalId(RegistActivity.this) != -1){
                            Intent intentHasRiskscore=new Intent(RegistActivity.this, GradedActivity.class);
                            startActivity(intentHasRiskscore);
                            RegistActivity.this.finish();
                            return;
                        }


                        Intent intentMain=new Intent(getApplicationContext(), MeMainFragmentActivity.class);
                        startActivity(intentMain);
                        RegistActivity.this.finish();
                    } else {
                        ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsgMap()+ "");
                    }
                } else {
                    ToastUtil.show(RegistActivity.this.getApplicationContext(), t.getMsgMap() + "");
                }
            }
        }, getRequestTag());
    }


    @OnClick(R.id.iv_agree_register)
    public void agreeRegisterOnclick() {
        mChecked = !mChecked;
        if (mChecked) {
            ivAgreeRegister.setImageResource(R.drawable.pitch);
        } else {
            ivAgreeRegister.setImageResource(R.drawable.pitch_un);
        }
    }


}

