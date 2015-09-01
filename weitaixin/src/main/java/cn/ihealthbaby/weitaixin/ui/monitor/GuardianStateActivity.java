package cn.ihealthbaby.weitaixin.ui.monitor;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

public class GuardianStateActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
//
    @Bind(R.id.flGuardianPurpose) FrameLayout flGuardianPurpose;
    @Bind(R.id.flGuardianMood) FrameLayout flGuardianMood;
    @Bind(R.id.ivFooter) TextView ivFooter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardian_state);
        ButterKnife.bind(this);

        title_text.setText("监护状态");
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.flGuardianPurpose)
    public void GuardianPurpose(FrameLayout flGuardianPurpose) {
       MyPoPoWinGuardian myPoPoWinGuardian=new MyPoPoWinGuardian(this);
       myPoPoWinGuardian.showAtLocation(flGuardianPurpose);
    }


    @OnClick(R.id.flGuardianMood)
    public void GuardianMood(FrameLayout flGuardianMood) {
        MyPoPoWinGuardian myPoPoWinGuardian=new MyPoPoWinGuardian(this);
        myPoPoWinGuardian.showAtLocation(flGuardianMood);
    }


    @OnClick(R.id.ivFooter)
    public void Footer(){

    }


}








