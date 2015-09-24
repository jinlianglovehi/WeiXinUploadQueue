package cn.ihealthbaby.weitaixin.ui.login;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;


public class ProtocolActivity extends BaseActivity {

    private Handler mHandler = new Handler();

    @Bind(R.id.back)
    RelativeLayout back;
    @Bind(R.id.title_text)
    TextView title_text;
    @Bind(R.id.function) TextView function;
    //
    @Bind(R.id.mWebView) WebView mWebView;

    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        ButterKnife.bind(this);

        title_text.setText("协议");

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.back)
    public void onBack() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            finish();
        }
    }


    private void initView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);


        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebChromeClient(new MyWebChromeClient());

//     mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "androidJS");
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new HelloWebViewClient());

        customDialog = new CustomDialog();
        Dialog dialog=customDialog.createDialog1(this,"加载中...");
        dialog.show();

        ApiManager.getInstance().urlApi.getPrivacyAgreementUrl(
                new DefaultCallback<String>(this, new AbstractBusiness<String>() {
                    @Override
                    public void handleData(String data) {
                        mWebView.loadUrl(data);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }
                }), getRequestTag());

    }



    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {

        }

        public void clickOnAndroid() {
            mHandler.post(new Runnable() {
                public void run() {
//	                mWebView.loadUrl("javascript:wave()");
                    finish();
                }
            });

        }
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return true;
        }
    }


}


