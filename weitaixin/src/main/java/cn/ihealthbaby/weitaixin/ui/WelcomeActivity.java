package cn.ihealthbaby.weitaixin.ui;

import android.content.Intent;
import android.os.Bundle;

import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;

/**
 * Created by chenweihua on 2015/9/21.
 */
public class WelcomeActivity extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (SPUtil.isNoFirstStartApp(this)) {
            if (SPUtil.isLogin(this)) {
                Intent intent = new Intent(this, MeMainFragmentActivity.class);
                startActivity(intent);
                finish();
                return;
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        }


        setContentView(R.layout.activity_welcome);


        if (!SPUtil.isNoFirstStartApp(this)) {
            SPUtil.setNoFirstStartApp(this);
        }

    }


}



