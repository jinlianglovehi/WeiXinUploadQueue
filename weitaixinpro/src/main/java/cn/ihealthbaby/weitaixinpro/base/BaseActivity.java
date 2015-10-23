package cn.ihealthbaby.weitaixinpro.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.squareup.leakcanary.RefWatcher;

import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixinpro.WeiTaiXinProApplication;

/**
 * Created by liuhongjian on 15/7/23 14:58.
 */
public abstract class BaseActivity extends FragmentActivity {
	public String TAG = getClass().getSimpleName();
	protected RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏g
		requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d(TAG, "requestQueue.cancelAll:%s", getRequestTag());
		requestQueue.cancelAll(getRequestTag());
		final RefWatcher refWatcher = ((WeiTaiXinProApplication) getApplication()).getRefWatcher();
		refWatcher.watch(this);
	}

	protected Object getRequestTag() {
		return this;
	}
}
