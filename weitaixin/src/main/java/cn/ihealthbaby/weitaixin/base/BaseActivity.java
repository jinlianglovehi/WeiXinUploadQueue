package cn.ihealthbaby.weitaixin.base;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.RequestQueue;

import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;

/**
 * Created by liuhongjian on 15/7/23 14:58.
 */
public abstract class BaseActivity extends Activity {
	protected RequestQueue requestQueue;
	protected String TAG = getClass().getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		requestQueue.cancelAll(TAG);
	}
}
