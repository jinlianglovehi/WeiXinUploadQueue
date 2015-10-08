package cn.ihealthbaby.weitaixin.ui.pay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.WXPayForm;
import cn.ihealthbaby.client.model.WXPrePay;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.LocalProductData;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.ui.pay.alipay.PayAlipayUtil;
import cn.ihealthbaby.weitaixin.ui.pay.event.PayEvent;
import cn.ihealthbaby.weitaixin.net.sourceforge.simcpux.PayWxUtil;
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
    public int totalfee = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_affirm_payment);

        ButterKnife.bind(this);

        title_text.setText("确认支付");

        EventBus.getDefault().register(this);

        orderId=getIntent().getLongExtra(PayConstant.ORDERID, -1);
        totalfee=getIntent().getIntExtra(PayConstant.TOTALFEE, -1);
        LogUtil.d(this.getClass().getSimpleName(), orderId+":orderId  totalfee:"+totalfee);

//        tvTotalPrice.setText("￥"+((Integer.parseInt(LocalProductData.getLocal().get(LocalProductData.PriceCount)+""))/100)+"");
        tvTotalPrice.setText(PayUtils.showPrice(Integer.parseInt(LocalProductData.getLocal().get(LocalProductData.PriceCount)+"")));
//        tvTotalPrice.setText(PayUtils.showPrice(totalfee));
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

        if (!isWXAppInstalledAndSupported(this, WXAPIFactory.createWXAPI(this, null))) {
            ToastUtil.show(this, "微信客户端未安装，请先下载安装");
            // http://weixin.qq.com/m
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("http://weixin.qq.com/m");
            intent.setData(content_url);
            startActivity(intent);
            return;
        }


        LogUtil.d("WXPrehandleDataPay",orderId+"==orderIdorderIdorderId");
        if (orderId == -1) {
            ToastUtil.show(getApplicationContext(),"订单Id生成有误");
            return;
        }

        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "微支付请求中...");
        dialog.show();

        WXPayForm wxPayForm=new WXPayForm();
        wxPayForm.setOrderId(orderId);
        wxPayForm.setSpbillCreateIp("127.0.0.1");
        ApiManager.getInstance().payApi.getWXPrePay(wxPayForm,
                new DefaultCallback<WXPrePay>(this, new AbstractBusiness<WXPrePay>() {
                    @Override
                    public void handleData(WXPrePay data) {
                        LogUtil.d("WXPrehandleDataPay", orderId + " <==orderId === WXPrehandleDataPay==> " + data);
                        PayConstant.WXPAY_APPID = data.getAppId();
                        PayWxUtil payWxUtil = new PayWxUtil(PayAffirmPaymentActivity.this, data);
                        payWxUtil.sendPayReq(data);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleResult(Result<WXPrePay> result) {
                        super.handleResult(result);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }

    private static boolean isWXAppInstalledAndSupported(Context context, IWXAPI api) {
        boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled() && api.isWXAppSupportAPI();
        return sIsWXAppInstalledAndSupported;
    }


    @OnClick(R.id.llPaymenyAlipay)
    public void PaymenyAlipay() {
        if (orderId == -1) {
            ToastUtil.show(getApplicationContext(),"订单Id生成有误");
            return;
        }

        final CustomDialog customDialog=new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "支付宝支付请求中...");
        dialog.show();
        ApiManager.getInstance().payApi.getAlipayOrderInfo(orderId,
                new DefaultCallback<String>(this, new AbstractBusiness<String>() {
                    @Override
                    public void handleData(String data) {
                        if (!TextUtils.isEmpty(data)) {
                            PayAlipayUtil payAlipayUtil=new PayAlipayUtil(PayAffirmPaymentActivity.this);
                            payAlipayUtil.payAction(data);
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Context context, Exception e) {
                        super.handleClientError(context, e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }



    @OnClick(R.id.llPaymenyUnionPay)
    public void PaymenyUnionPay() {

    }



}


