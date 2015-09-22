package cn.ihealthbaby.weitaixin.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.login.InfoEditActivity;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;
import cn.ihealthbaby.weitaixin.ui.mine.GradedActivity;

/**
 * Created by chenweihua on 2015/9/21.
 */
public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.tv_enter) TextView tv_enter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SPUtil.isNoFirstStartApp(this)) {
            if (SPUtil.getUser(this) != null) {
                final CustomDialog customDialog = new CustomDialog();
                Dialog dialog = customDialog.createDialog1(this, "刷新用户数据...");
                dialog.show();

                ApiManager.getInstance().userApi.refreshInfo(new DefaultCallback<User>(this, new AbstractBusiness<User>() {
                    @Override
                    public void handleData(User data)   {
                        SPUtil.saveUser(WelcomeActivity.this, data);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        customDialog.dismiss();
                        Intent intentHasRiskscore = new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intentHasRiskscore);
                        finish();
                    }
                }), getRequestTag());

                if (SPUtil.isLogin(this)) {
                    if (SPUtil.getUser(this).getIsInit()) {
                        Intent intentIsInit = new Intent(this, InfoEditActivity.class);
                        startActivity(intentIsInit);
                        return;
                    }

                    if (!SPUtil.getUser(this).getHasRiskscore()) {
                        if (SPUtil.getHospitalId(this) != -1) {
                            Intent intentHasRiskscore = new Intent(this, GradedActivity.class);
                            startActivity(intentHasRiskscore);
                            return;
                        }
                    }

                    Intent intent = new Intent(this, MeMainFragmentActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            return;
        }


        setContentView(R.layout.activity_main_welcome);
        ButterKnife.bind(this);

        if (!SPUtil.isNoFirstStartApp(this)) {
            SPUtil.setNoFirstStartApp(this);
        }

    }


    @OnClick(R.id.tv_enter)
    public void tv_enter() {
        if (SPUtil.getUser(this) != null) {
            final CustomDialog customDialog = new CustomDialog();
            Dialog dialog = customDialog.createDialog1(this, "刷新用户数据...");
            dialog.show();

            ApiManager.getInstance().userApi.refreshInfo(new DefaultCallback<User>(this, new AbstractBusiness<User>() {
                @Override
                public void handleData(User data)   {
                    SPUtil.saveUser(WelcomeActivity.this, data);
                    customDialog.dismiss();
                }

                @Override
                public void handleException(Exception e) {
                    customDialog.dismiss();
                    Intent intentHasRiskscore = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intentHasRiskscore);
                    finish();
                }
            }), getRequestTag());

            if (SPUtil.isLogin(this)) {
                if (SPUtil.getUser(this).getIsInit()) {
                    Intent intentIsInit = new Intent(this, InfoEditActivity.class);
                    startActivity(intentIsInit);
                    return;
                }

                if (!SPUtil.getUser(this).getHasRiskscore()) {
                    if (SPUtil.getHospitalId(this) != -1) {
                        Intent intentHasRiskscore = new Intent(this, GradedActivity.class);
                        startActivity(intentHasRiskscore);
                        return;
                    }
                }

                Intent intent = new Intent(this, MeMainFragmentActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    public void nextAction() {

    }


}



