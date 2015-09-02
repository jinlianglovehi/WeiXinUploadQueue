package cn.ihealthbaby.weitaixin.ui.pay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;

public class PayAccountActivity extends AppCompatActivity {


    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_account);

        ButterKnife.bind(this);

        title_text.setText("登录");
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


}


