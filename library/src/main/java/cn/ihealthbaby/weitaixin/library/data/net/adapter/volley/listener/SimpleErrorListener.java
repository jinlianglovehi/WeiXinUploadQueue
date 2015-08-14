package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.listener;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

/**
 * Created by Think on 2015/6/26.
 */
public class SimpleErrorListener implements Response.ErrorListener {
	private Context context;

	public SimpleErrorListener(Context context) {
		this.context = context;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (error instanceof ServerError) {
			// #Link｛BasicNetwork｝Only throw ServerError for 5xx status codes.
			ToastUtil.show(context, "服务器错误");
		}
		if (error instanceof TimeoutError) {
			ToastUtil.show(context, "请求超时");
		}
		if (error instanceof ParseError) {
			ToastUtil.show(context, "响应数据解析错误");
		}
		if (error instanceof AuthFailureError) {
			ToastUtil.show(context, "请求授权错误");
		}
		if (error instanceof NetworkError) {
			ToastUtil.show(context, "网络错误");
		}
	}
}
