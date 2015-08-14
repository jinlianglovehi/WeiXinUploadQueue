package cn.ihealthbaby.weitaixin.library.data.net.adapter;

import android.content.Context;

import com.google.gson.JsonParseException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.List;
import java.util.Map;

import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.loopj.ClientManager;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.loopj.SimpleResponseHandler;
import cn.ihealthbaby.weitaixin.library.data.net.paser.Parser;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

/**
 * Created by liuhongjian on 15/7/21 20:54.
 */
public class LoopjAdapter implements HttpClientAdapter {
	private final static String TAG = "LoopjAdapter";
	private final AsyncHttpClient client;
	private Context context;
	private String host;

	public LoopjAdapter(Context context, String serverUrl) {
		this.context = context;
		this.host = serverUrl;
		client = ClientManager.getInstance().getClient();
	}

	@Override
	public <T> void requestAsync(final RequestParam<T> requestParam) {
		SimpleResponseHandler<T> responseHandler = new SimpleResponseHandler<T>(context, requestParam.getCallable()) {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String response = new String(responseBody);
						LogUtil.v(TAG, response);
						Result<T> result = Parser.getInstance().parse(response, requestParam.getType());
						LogUtil.v(TAG, result.toString());
						call(result);
					} catch (JsonParseException jsonParseException) {
						ToastUtil.warn(context, "JsonParseException");
						jsonParseException.printStackTrace();
					} catch (NullPointerException nullPointerException) {
						ToastUtil.warn(context, "NullPointerException");
						nullPointerException.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		requestParam.setCallable(responseHandler);
		String method = requestParam.getMethod().toUpperCase();
		List<Map.Entry<String, Object>> form = requestParam.getForm();
		RequestParams requestParams = new RequestParams();
		for (Map.Entry<String, Object> entry : form) {
			requestParams.put(entry.getKey(), entry.getValue());
		}
		String url = getUrl(requestParam.getUri());
		switch (method) {
			case "GET":
				client.get(url, responseHandler);
				break;
			case "POST":
				client.post(url, requestParams, responseHandler);
				break;
			case "PUT":
				client.put(url, requestParams, responseHandler);
				break;
			case "DELETE":
				client.delete(url, responseHandler);
				break;
			case "PATCH":
				client.patch(url, requestParams, responseHandler);
				break;
			default:
				break;
		}
	}

	private String getUrl(String uri) {
		return host + uri;
	}

	public void cancleAllRequest(Object tag) {
		if (tag instanceof Context) {
			client.cancelRequests(context, true);
		}
	}
}
