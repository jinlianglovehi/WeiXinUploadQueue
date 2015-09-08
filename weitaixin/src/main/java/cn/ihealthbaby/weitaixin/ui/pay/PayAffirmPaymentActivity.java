package cn.ihealthbaby.weitaixin.ui.pay;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.model.LocalProductData;

public class PayAffirmPaymentActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //

    @Bind(R.id.tvTotalPrice) TextView tvTotalPrice;
    @Bind(R.id.llPaymenyWeixin) LinearLayout llPaymenyWeixin;
    @Bind(R.id.llPaymenyTaoBao) LinearLayout llPaymenyTaoBao;
    @Bind(R.id.llPaymenyUnionPay) LinearLayout llPaymenyUnionPay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_affirm_payment);

        ButterKnife.bind(this);

        title_text.setText("确认支付");

        tvTotalPrice.setText("￥"+LocalProductData.getLocal().get(LocalProductData.PriceCount)+"");
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }

    @OnClick(R.id.llPaymenyWeixin)
    public void PaymenyWeixin() {
    }

    @OnClick(R.id.llPaymenyTaoBao)
    public void PaymenyTaoBao() {
    }

    @OnClick(R.id.llPaymenyUnionPay)
    public void PaymenyUnionPay() {
    }

}


