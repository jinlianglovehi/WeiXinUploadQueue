package cn.ihealthbaby.weitaixin;

import android.app.Application;

import com.android.volley.RequestQueue;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.AbstractHttpClientAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.util.Constants;

/**
 * Created by liuhongjian on 15/7/23 14:09.
 */
public class WeiTaiXinApplication extends Application {
	private AbstractHttpClientAdapter adapter;

	@Override
	public void onCreate() {
		super.onCreate();
		RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
		adapter = new VolleyAdapter(getApplicationContext(), Constants.SERVER_URL, requestQueue);
//		HttpClientAdapter adapter = new XiaoCaoVolleyAdapter(getApplicationContext(), Constants.SERVER_URL);
//		HttpClientAdapter adapter = new LoopjAdapter(getApplicationContext(), Constants.SERVER_URL);
		ApiManager.init(adapter);
	}

	public AbstractHttpClientAdapter getAdapter() {
		return adapter;
	}
}
