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
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.pay.alipay.PayAlipayUtil;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayEvent;
import de.greenrobot.event.EventBus;

public class PayAffirmPaymentActivity extends BaseActivity {

    @Bind(R.id.back) RelativeLayout back;
    @Bind(R.id.title_text) TextView title_text;
    @Bind(R.id.function) TextView function;

    //

    @Bind(R.id.tvTotalPrice) TextView tvTotalPrice;
    @Bind(R.id.llPaymenyWeixin) LinearLayout llPaymenyWeixin;
    @Bind(R.id.llPaymenyAlipay) LinearLayout llPaymenyAlipay;
    @Bind(R.id.llPaymenyUnionPay) LinearLayout llPaymenyUnionPay;

    public long orderId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_affirm_payment);

        ButterKnife.bind(this);

        title_text.setText("确认支付");

        EventBus.getDefault().register(this);

        orderId=getIntent().getLongExtra("OrderId", -1);

        tvTotalPrice.setText("￥"+LocalProductData.getLocal().get(LocalProductData.PriceCount)+"");
    }


    public void onEventMainThread(PayEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @OnClick(R.id.back)
    public void onBack() {
        this.finish();
    }


    @OnClick(R.id.llPaymenyWeixin)
    public void PaymenyWeixin() {
        if (orderId == -1) {
            ToastUtil.show(getApplicationContext(),"订单id生成有误");
            return;
        }

        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "微支付请求中...");
        dialog.show();

        WXPayForm wxPayForm=new WXPayForm();
        wxPayForm.setOrderId(orderId);
//        wxPayForm.setSpbillCreateIp();
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
        if (orderId == -1) {
            ToastUtil.show(getApplicationContext(),"订单id生成有误");
            return;
        }

        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "支付宝支付请求中...");
        dialog.show();
        ApiManager.getInstance().payApi.getAlipayOrderInfo(orderId, new HttpClientAdapter.Callback<String>() {
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


