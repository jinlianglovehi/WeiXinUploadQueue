package cn.ihealthbaby.weitaixin.library.data.net.adapter.loopj;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by liuhongjian on 15/7/910:52.
 */
public class ClientManager {
	/**
	 * 重新设置默认超时为30s
	 */
	private static ClientManager instance;
	private final AsyncHttpClient client;

	private ClientManager() {
		client = new AsyncHttpClient();
	}

	public static ClientManager getInstance() {
		if (instance == null) {
			synchronized (ClientManager.class) {
				if (instance == null) {
					instance = new ClientManager();
				}
			}
		}
		return instance;
	}

	public AsyncHttpClient getClient() {
		return client;
	}
}
