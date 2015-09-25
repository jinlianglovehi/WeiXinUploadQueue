package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.Urls;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.tools.CustomDialog;

/**
 * @author by kang on 2015/9/10.
 */
public class MonitorCommonSense extends BaseActivity {
	@Bind(R.id.back)
	RelativeLayout mBack;
	@Bind(R.id.title_text)
	TextView mTitleText;
	@Bind(R.id.function)
	TextView mFunction;
	@Bind(R.id.wv_monitor_common_sence)
	WebView mWvMonitorCommonSence;
	private WebSettings mWebSettings;
	private CustomDialog customDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_common_sence);
		ButterKnife.bind(this);
		mTitleText.setText("监护常识");
		mWebSettings = mWvMonitorCommonSence.getSettings();
		mWebSettings.setSavePassword(false);
		mWebSettings.setSaveFormData(false);
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setSupportZoom(false);
		mWvMonitorCommonSence.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		customDialog = new CustomDialog();
		Dialog dialog = customDialog.createDialog1(this, "加载中...");
		dialog.show();
		ApiManager.getInstance().urlApi.getUrls(new DefaultCallback<Urls>(getApplicationContext(), new AbstractBusiness<Urls>() {
			@Override
			public void handleData(Urls data) {
				mWvMonitorCommonSence.loadUrl(data.getKnowledge());
				customDialog.dismiss();
			}
		}), getRequestTag());
	}

	@OnClick(R.id.back)
	public void backOnclick() {
		if (mWvMonitorCommonSence.canGoBack()) mWvMonitorCommonSence.goBack();
		else super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWvMonitorCommonSence.canGoBack())
				mWvMonitorCommonSence.goBack();
			else super.onBackPressed();
		}
		return true;
	}
}
