package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.mine.event.LogoutEvent;
import cn.ihealthbaby.weitaixin.ui.mine.event.WelcomeEvent;
import cn.ihealthbaby.weitaixin.ui.widget.PayDialog;
import de.greenrobot.event.EventBus;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_system);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        title_text.setText("系统设置");
    }

    public void onEventMainThread(WelcomeEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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


    public void logout() {
        PayDialog payDialog=new PayDialog(this,new String[]{"确定退出？", "取消", "确定"});
        payDialog.show();
        payDialog.setOperationAction(new PayDialog.OperationAction() {
            @Override
            public void payYes(Object... obj) {

                final CustomDialog customDialog = new CustomDialog();
                Dialog dialog = customDialog.createDialog1(SetSystemActivity.this, "退出中...");
                dialog.show();

                ApiManager.getInstance().accountApi.logout(new DefaultCallback<Void>(SetSystemActivity.this, new AbstractBusiness<Void>() {
                    @Override
                    public void handleData(Void data) {
                        SPUtil.clearUser(SetSystemActivity.this);
                        WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(null);
                        customDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        EventBus.getDefault().post(new LogoutEvent());
                    }

                    @Override
                    public void handleValidator(Context context) {
                        super.handleValidator(context);
                        customDialog.dismiss();
                        LogUtil.d("handleValidator super", "handleValidator super");
                    }

                    @Override
                    public void handleAccountError(Context context, Map<String, Object> msgMap) {
                        super.handleAccountError(context, msgMap);
                        customDialog.dismiss();
                        finish();
                        LogUtil.d("handleAccountError super", "handleAccountError super");
                    }

                    @Override
                    public void handleError(Map<String, Object> msgMap) {
                        super.handleError(msgMap);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleResult(Result<Void> result) {
                        super.handleResult(result);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
            }

            @Override
            public void payNo(Object... obj) {

            }
        });

    }


}


