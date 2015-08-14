package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager;

import android.content.Context;

import com.android.volley.RequestQueue;

public class ConnectionManager {
	private static ConnectionManager instance;
	private RequestQueue mRequestQueue;

	private ConnectionManager() {
	}

	/**
	 * 单例
	 */
	public static ConnectionManager getInstance() {
		if (instance == null) {
			synchronized (ConnectionManager.class) {
				if (instance == null) {
					instance = new ConnectionManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 实现单例请求队列,默认NoCache
	 *
	 * @param context
	 * @return
	 */
	public RequestQueue getRequestQueue(Context context) {
		if (mRequestQueue == null) {
			mRequestQueue = CustomVolley.newRequestQueue(context);
		}
		return mRequestQueue;
	}
}
