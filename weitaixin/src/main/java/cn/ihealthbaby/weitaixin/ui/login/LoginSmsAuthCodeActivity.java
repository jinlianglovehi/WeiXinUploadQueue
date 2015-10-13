package cn.ihealthbaby.weitaixin.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.form.LoginByAuthForm;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.service.AdviceSettingService;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.MeMainFragmentActivity;
import cn.ihealthbaby.weitaixin.ui.mine.GradedActivity;

public class LoginSmsAuthCodeActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //

    @Bind(R.id.et_phone_number_smsauthcode)
    EditText et_phone_number_smsauthcode;
    @Bind(R.id.et_mark_number_smsauthcode)
    EditText et_mark_number_smsauthcode;
    @Bind(R.id.tv_mark_number_text_smsauthcode)
    TextView tv_mark_number_text_smsauthcode;
    @Bind(R.id.tv_login_action_smsauthcode)
    TextView tv_login_action_smsauthcode;
    @Bind(R.id.tv_ruleregister)
    TextView tvRuleRegister;


    public Handler mHandler = new Handler();
    @Bind(R.id.iv_agree_register)
    ImageView mIvAgreeRegister;
    private Dialog dialog;

    private boolean isChecked = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sms_auth_code);

        ButterKnife.bind(this);

        title_text.setText("短信验证码登录");
//      back.setVisibility(View.INVISIBLE);
//        tv_login_action_smsauthcode.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.tv_ruleregister)
    public void tvRuleLogin() {
        Intent intent = new Intent(this, ProtocolActivity.class);
        startActivity(intent);
    }


    public boolean isSend = true;
    public CountDownTimer countDownTimer;
    public boolean isHasAuthCode = false;


    @OnClick(R.id.iv_agree_register)
    public void iv_agree_registerOnclick() {
        isChecked = !isChecked;
        if (isChecked) {
            mIvAgreeRegister.setImageResource(R.drawable.pitch);
        } else {
            mIvAgreeRegister.setImageResource(R.drawable.pitch_un);
        }
    }

    @OnClick(R.id.tv_mark_number_text_smsauthcode)
    public void tv_mark_number_text_smsauthcode() {
        if (isSend) {
            phone_number = et_phone_number_smsauthcode.getText().toString().trim();
            if (TextUtils.isEmpty(phone_number)) {
                ToastUtil.show(getApplicationContext(), "请输入手机号码");
                return;
            }
            if (phone_number.length() != 11) {
                ToastUtil.show(getApplicationContext(), "请输入手机号码必须是11位的数字和字母");
                return;
            }


            getAuthCode();

        }
    }


    public void cancel(CustomDialog customDialog) {
        tv_mark_number_text_smsauthcode.setText("发送验证码");
        isSend = true;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        customDialog.dismiss();
    }


    //0 注册验证码 1 登录验证码 2 修改密码验证码.
    public void getAuthCode() {

        final CustomDialog customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "验证码发送中...");
        dialog.show();


        isSend = false;


        ApiManager.getInstance().accountApi.getAuthCode(phone_number, 1,
                new DefaultCallback<Boolean>(this, new AbstractBusiness<Boolean>() {
                    @Override
                    public void handleData(Boolean data) {
                        if (data) {

                            tv_mark_number_text_smsauthcode.setBackgroundResource(R.color.gray1);
                            tv_mark_number_text_smsauthcode.setTextColor(getResources().getColor(R.color.gray2));


                            isHasAuthCode = true;

                            try {
                                countDownTimer = new CountDownTimer(60000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        tv_mark_number_text_smsauthcode.setText(millisUntilFinished / 1000 + "秒之后重发");
                                        isSend = false;
                                    }

                                    @Override
                                    public void onFinish() {
                                        tv_mark_number_text_smsauthcode.setText("发送验证码");
                                        tv_mark_number_text_smsauthcode.setBackgroundResource(R.drawable.shape_send_verifycode);
                                        tv_mark_number_text_smsauthcode.setTextColor(getResources().getColor(R.color.black0));
                                        isSend = true;
                                        customDialog.dismiss();
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
                            ToastUtil.show(LoginSmsAuthCodeActivity.this.getApplicationContext(), "重新获取短信验证码");
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        isHasAuthCode = false;
                        cancel(customDialog);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        isHasAuthCode = false;
                        cancel(customDialog);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }


    @OnClick(R.id.tv_login_action_smsauthcode)
    public void tvLoginActionSmsAuthCode() {
        if (isHasAuthCode) {
            if (isChecked) {
                tvLogieActionSmsAuthCode();
            } else {
                ToastUtil.show(getApplicationContext(), "不接受，不能登录哦");
            }
        } else {
            ToastUtil.show(getApplicationContext(), "先获取验证码");
        }
    }


    public String phone_number;
    public String mark_number;

    public void tvLogieActionSmsAuthCode() {
        phone_number = et_phone_number_smsauthcode.getText().toString().trim();
        mark_number = et_mark_number_smsauthcode.getText().toString().trim();
        if (TextUtils.isEmpty(phone_number)) {
            ToastUtil.show(getApplicationContext(), "请输入手机号码");
            return;
        }
        if (phone_number.length() != 11) {
            ToastUtil.show(getApplicationContext(), "请输入手机号码必须是11位的数字和字母");
            return;
        }
        if (TextUtils.isEmpty(mark_number)) {
            ToastUtil.show(getApplicationContext(), "请输入短信验证码");
            return;
        }
        if (mark_number.length() != 6) {
            ToastUtil.show(getApplicationContext(), "短信验证码必须是6位的数字");
            return;
        }


        final CustomDialog customDialog = new CustomDialog();
        final Dialog dialog = customDialog.createDialog1(this, "登录中...");
        dialog.show();


        LoginByAuthForm loginByAuthForm = new LoginByAuthForm(phone_number, Integer.parseInt(mark_number), "123456789", 1.0d, 1.0d);
        ApiManager.getInstance().accountApi.loginByAuthCode(loginByAuthForm,
                new DefaultCallback<User>(this, new AbstractBusiness<User>() {
                    @Override
                    public void handleData(User data) {
                        if (data != null && data.getAccountToken() != null) {
                            WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(data.getAccountToken());
                            SPUtil.saveUser(LoginSmsAuthCodeActivity.this, data);
                            ToastUtil.show(LoginSmsAuthCodeActivity.this.getApplicationContext(), "登录成功");


                            Intent intent = new Intent(getApplicationContext(), AdviceSettingService.class);
                            startService(intent);

                            if (data.getIsInit()) {
                                customDialog.dismiss();
                                LoginSmsAuthCodeActivity.this.finish();
                                Intent intentIsInit = new Intent(LoginSmsAuthCodeActivity.this, InfoEditActivity.class);
                                startActivity(intentIsInit);
                                return;
                            }

                            if (!data.getHasRiskscore() && SPUtil.getHospitalId(LoginSmsAuthCodeActivity.this) != -1) {
                                customDialog.dismiss();
                                Intent intentHasRiskscore = new Intent(LoginSmsAuthCodeActivity.this, GradedActivity.class);
                                startActivity(intentHasRiskscore);
                                LoginSmsAuthCodeActivity.this.finish();
                                return;
                            }


                            Intent intentMain = new Intent(getApplicationContext(), MeMainFragmentActivity.class);
                            startActivity(intentMain);
                            LoginSmsAuthCodeActivity.this.finish();
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }


}








