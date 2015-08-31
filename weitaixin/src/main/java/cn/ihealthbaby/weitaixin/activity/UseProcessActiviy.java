package cn.ihealthbaby.weitaixin.activity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;

/**
 * @author by kang on 2015/8/29.
 */
public class UseProcessActiviy extends BaseActivity {

    @Bind(R.id.back)
    RelativeLayout mBack;
    @Bind(R.id.title_text)
    TextView mTitleText;
    @Bind(R.id.function)
    TextView mFunction;
    @Bind(R.id.wv_use_process)
    WebView mWvWelcome;

    private WebSettings mWebSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_process);
        ButterKnife.bind(this);
        mTitleText.setText(getString(R.string.use_process_title));

        mWebSettings = mWvWelcome.getSettings();
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWvWelcome.loadUrl("http://baidu.com");
    }

    @OnClick(R.id.back)
    public void backOnclick(){
        finish();
    }
}