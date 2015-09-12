package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.tools.CustomDialog;


public class SetSystemActivity extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function)
    TextView function;
    //
    @Bind(R.id.ll_set_system_01)
    RelativeLayout ll_set_system_01;
    @Bind(R.id.ll_set_system_02)
    RelativeLayout ll_set_system_02;
    @Bind(R.id.ll_set_system_03)
    RelativeLayout ll_set_system_03;
    @Bind(R.id.ll_set_system_04)
    RelativeLayout ll_set_system_04;
    @Bind(R.id.ll_set_system_05)
    RelativeLayout ll_set_system_05;
    @Bind(R.id.ll_set_system_06)
    RelativeLayout ll_set_system_06;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system);

        ButterKnife.bind(this);

        title_text.setText("系统设置");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.ll_set_system_01)
    public void ll_set_system_01() {
        Intent intent = new Intent(getApplicationContext(), SetSystemGuardianActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_set_system_02)
    public void ll_set_system_02() {
        Intent intent = new Intent(getApplicationContext(), SetSystemResetPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_set_system_03)
    public void ll_set_system_03() {
        Intent intent = new Intent(getApplicationContext(), SetSystemSuggestionActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_set_system_04)
    public void ll_set_system_04() {
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_set_system_05)
    public void ll_set_system_05() {
        Intent intent = new Intent(getApplicationContext(), SetSystemUploadActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_set_system_06)
    public void ll_set_system_06() {
        logout();
    }

    ApiManager apiManager;

    public void logout() {
        apiManager = ApiManager.getInstance();

        dialog = new CustomDialog().createDialog1(this, "退出中...");
        dialog.show();

        apiManager.accountApi.logout(new HttpClientAdapter.Callback<Void>() {
            @Override
            public void call(Result<Void> t) {
                if (t.isSuccess()) {
                    WeiTaiXinApplication.getInstance().isLogin = false;
//                  ToastUtil.show(getApplicationContext(),"退出登录");
                    WeiTaiXinApplication.accountToken = null;
                    WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(null);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsg());
                }
                dialog.dismiss();
            }
        }, getRequestTag());
    }

}



