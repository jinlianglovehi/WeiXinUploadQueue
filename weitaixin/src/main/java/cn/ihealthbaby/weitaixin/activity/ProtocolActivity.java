package cn.ihealthbaby.weitaixin.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);

        ButterKnife.bind(this);

        title_text.setText("协议");

        initView();
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


        ApiManager.getInstance().urlApi.getPrivacyAgreementUrl(new HttpClientAdapter.Callback<String>() {
            @Override
            public void call(Result<String> t) {
                if (t.isSuccess()) {
                    mWebView.loadUrl(t.getData());
                }
            }
        }, getRequestTag());

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
                    System.out.println("-----clickOnAndroid------");
//	                    mWebView.loadUrl("javascript:wave()");
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


