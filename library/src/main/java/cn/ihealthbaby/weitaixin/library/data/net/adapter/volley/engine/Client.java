package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.engine;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import cn.ihealthbaby.client.HttpClientAdapter;

/**
 * Created by liuhongjian on 15/7/822:31.
 */
public class Client implements HttpClientAdapter {
	private static Client instance;
	private RequestQueue queue;
	private Context context;

	private Client(Context context) {
		this.context = context;
		queue = Volley.newRequestQueue(context);
		queue.start();
	}

	public static Client getInstance(Context context) {
		if (instance == null) {
			synchronized (Client.class) {
				if (instance == null) {
					instance = new Client(context);
				}
			}
		}
		return instance;
	}

	public void sendRequest(Request request) {
		queue.add(request);
	}

	@Override
	public <T> void requestAsync(RequestParam<T> requestParam) {
//        AbstractReqeust<T> reqeust = new AbstractReqeust<T>(requestParam, new SimpleErrorListener(context));
//        return reqeust;
	}
}
