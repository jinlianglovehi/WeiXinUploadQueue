package cn.ihealthbaby.weitaixinpro.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.volley.RequestQueue;

import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * Created by liuhongjian on 15/7/23 14:58.
 */
public abstract class BaseActivity extends FragmentActivity {
	protected RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.d(this.getClass().getName(), "requestQueue.cancelAll:%s", getRequestTag());
		requestQueue.cancelAll(getRequestTag());
	}

	protected Object getRequestTag() {
		return this;
	}
}
