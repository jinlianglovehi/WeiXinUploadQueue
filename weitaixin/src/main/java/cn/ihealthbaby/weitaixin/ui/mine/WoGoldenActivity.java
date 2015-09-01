package cn.ihealthbaby.weitaixin.ui.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;


public class WoGoldenActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_golden);

        ButterKnife.bind(this);

        title_text.setText("我的金库");
        back.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    @OnClick(R.id.back)
    public void onBack( ) {
        this.finish();
    }


}
