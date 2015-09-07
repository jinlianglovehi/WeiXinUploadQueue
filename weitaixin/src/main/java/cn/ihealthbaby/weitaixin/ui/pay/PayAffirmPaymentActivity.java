package cn.ihealthbaby.weitaixin.ui.pay;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

public class PayAffirmPaymentActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_affirm_payment);

        ButterKnife.bind(this);

        title_text.setText("确认支付");
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


}


