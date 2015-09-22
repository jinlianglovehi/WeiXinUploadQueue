package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.request;

import android.support.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;

/**
 * Created by Think on 2015/6/25.
 */
public abstract class AbstractReqeust<T> extends Request<Result<T>> {
	private final static String TAG = "AbstractReqeust";
	//默认超时20s
	private static final int DEFAULT_TIMEOUT_MS = 20 * 1000;
	private HttpClientAdapter.Callback callback;

	public AbstractReqeust(int method, String url, Response.ErrorListener listener, HttpClientAdapter.Callback callback) {
		super(method, url, listener);
		this.callback = callback;
		//设置默认超时
		setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, 0, 1f));
	}

	@Override
	protected void deliverResponse(Result<T> response) {

		if (callback != null) {
			callback.call(response);
		}
	}

	@NonNull
	protected String getString(NetworkResponse response) {
		String parsed = "";
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return parsed;
	}
}
