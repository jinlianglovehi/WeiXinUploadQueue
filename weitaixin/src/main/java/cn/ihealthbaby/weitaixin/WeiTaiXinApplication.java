package cn.ihealthbaby.weitaixin;

import android.app.Application;

import com.android.volley.RequestQueue;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.AbstractHttpClientAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.util.Constants;

/**
 * Created by liuhongjian on 15/7/23 14:09.
 */
public class WeiTaiXinApplication extends Application {
	private AbstractHttpClientAdapter adapter;


	public static WeiTaiXinApplication app;
	public boolean isLogin=false;
	public static String accountToken;
	public static String phone_number;
	public static User user;



	@Override
	public void onCreate() {
		super.onCreate();

		app=this;

		initApiManager();

//		RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
//		adapter = new VolleyAdapter(getApplicationContext(), Constants.SERVER_URL, requestQueue);
////		HttpClientAdapter adapter = new XiaoCaoVolleyAdapter(getApplicationContext(), Constants.SERVER_URL);
////		HttpClientAdapter adapter = new LoopjAdapter(getApplicationContext(), Constants.SERVER_URL);
//		ApiManager.init(adapter);


	}

	public AbstractHttpClientAdapter getAdapter() {
		return adapter;
	}


	public static WeiTaiXinApplication  getInstance(){
		return app;
	}

	public VolleyAdapter mAdapter;
	public void initApiManager(){
		RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
		mAdapter = new VolleyAdapter(getApplicationContext(), Constants.SERVER_URL, requestQueue);
//		mAdapter.setAccountToken(WeiTaiXinApplication.accountToken);
		ApiManager.init(mAdapter);
//		ApiManager.getInstance();
	}



}
