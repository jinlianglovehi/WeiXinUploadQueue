package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.WXPayForm;
import cn.ihealthbaby.client.model.WXPrePay;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.data.model.LocalProductData;
import cn.ihealthbaby.weitaixin.library.tools.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.pay.alipay.PayAlipayUtil;

public class PayAffirmPaymentActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //

    @Bind(R.id.tvTotalPrice) TextView tvTotalPrice;
    @Bind(R.id.llPaymenyWeixin) LinearLayout llPaymenyWeixin;
    @Bind(R.id.llPaymenyAlipay) LinearLayout llPaymenyAlipay;
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
        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "微支付请求中...");
        dialog.show();

        WXPayForm wxPayForm=new WXPayForm();
        ApiManager.getInstance().payApi.getWXPrePay(wxPayForm, new HttpClientAdapter.Callback<WXPrePay>() {
            @Override
            public void call(Result<WXPrePay> t) {
                if (t.isSuccess()) {
                    WXPrePay data = t.getData();
//                    if (!TextUtils.isEmpty(data)) {
//                        PayAlipayUtil payAlipayUtil=new PayAlipayUtil(PayAffirmPaymentActivity.this);
//                        payAlipayUtil.payAction(data);
//                    } else {
//                        ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
//                    }
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        },getRequestTag());
    }

    @OnClick(R.id.llPaymenyAlipay)
    public void PaymenyAlipay() {
        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "支付宝支付请求中...");
        dialog.show();
        ApiManager.getInstance().payApi.getAlipayOrderInfo(0, new HttpClientAdapter.Callback<String>() {
            @Override
            public void call(Result<String> t) {
                if (t.isSuccess()) {
                    String data = t.getData();
                    if (!TextUtils.isEmpty(data)) {
                        PayAlipayUtil payAlipayUtil=new PayAlipayUtil(PayAffirmPaymentActivity.this);
                        payAlipayUtil.payAction(data);
                    } else {
                        ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }



    @OnClick(R.id.llPaymenyUnionPay)
    public void PaymenyUnionPay() {

    }



}


