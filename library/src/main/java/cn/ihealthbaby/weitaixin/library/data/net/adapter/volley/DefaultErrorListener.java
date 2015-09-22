package cn.ihealthbaby.weitaixin.library.data.net.adapter.volley;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.DialogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

/**
 * Created by liuhongjian on 15/7/23 09:58.
 */
public class DefaultErrorListener implements Response.ErrorListener {
	private final Context context;
	private String tag;

	public DefaultErrorListener(Context context, String tag) {
		this.context = context;
		this.tag = tag;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		DialogUtil.dismissDialog(context);
		if (error instanceof NoConnectionError) {
			ToastUtil.warn(context, "网络连接失败，请检查网络状态");
			LogUtil.e(tag, "no connection" + error.getMessage());
		} else if (error instanceof NetworkError) {
			ToastUtil.warn(context, "网络连接异常");
			LogUtil.e(tag, "network error" + error.getMessage());
		} else if (error instanceof ParseError) {
			ToastUtil.warn(context, "解析错误");
			LogUtil.e(tag, "parse error" + error.getMessage());
		} else if (error instanceof ServerError) {
			ToastUtil.warn(context, "服务器错误");
			LogUtil.e(tag, "server error" + error.getMessage());
		} else if (error instanceof TimeoutError) {
			ToastUtil.show(context, "网络连接超时");
			LogUtil.e(tag, "timeout" + error.getMessage());
		} else if (error instanceof AuthFailureError) {
			ToastUtil.warn(context, "授权错误");
			LogUtil.e(tag, "authfailure error" + error.getMessage());
		}
	}
}
