package cn.ihealthbaby.weitaixin.ui.mine;

import android.app.Dialog;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import cn.ihealthbaby.weitaixin.tools.CustomDialog;

/**
 * @author by kang on 2015/8/29.
 */
public class FeatureActiviy extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout mBack;
    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.function)
    TextView mFunction;
    @Bind(R.id.wv_feathure)
    WebView mWvWelcome;

    private WebSettings mWebSettings;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature);
        ButterKnife.bind(this);
        mTitleText.setText(getString(R.string.feature_title));

        mWebSettings = mWvWelcome.getSettings();
        mWebSettings.setSavePassword(false);
        mWebSettings.setSaveFormData(false);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(false);
        mWvWelcome.setWebChromeClient(new WebChromeClient());


        customDialog = new CustomDialog();
        Dialog dialog = customDialog.createDialog1(this, "加载中...");
        dialog.show();

        ApiManager.getInstance().urlApi.getPrivacyAgreementUrl(new HttpClientAdapter.Callback<String>() {
            @Override
            public void call(Result<String> t) {
                if (t.isSuccess()) {
                    mWvWelcome.loadUrl(t.getData());
                }
                customDialog.dismiss();
            }
        }, getRequestTag());
    }

    @OnClick(R.id.back)
    public void backOnclick(){
        finish();
    }
}