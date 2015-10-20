package cn.ihealthbaby.weitaixinpro;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;

import java.util.Map;

import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.ui.RebindEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/7/28 17:55.
 */
public abstract class AbstractBusiness<T> implements Business<T> {
	@Override
	public void handleValidator(Context context) {
	}

	@Override
	public void handleResult(Result<T> result) {
	}

	@Override
	public void handleAccountError(Context context, Map<String, Object> msgMap) {
		SPUtil.clearHClientUser(context);
		EventBus.getDefault().post(new RebindEvent());
	}

	@Override
	public void handleError(Map<String, Object> msgMap) {
	}

	@Override
	public void handleException(Exception e) {
	}

	@Override
	public void handleClientError(Context context, Exception error) {
		String tag = "handleClientError";
		if (error instanceof NoConnectionError) {
			ToastUtil.show(context, "网络连接失败");
			LogUtil.e(tag, "no connection" + error.getMessage());
		} else if (error instanceof NetworkError) {
			ToastUtil.show(context, "网络连接异常");
			LogUtil.e(tag, "network error" + error.getMessage());
		} else if (error instanceof ParseError) {
			ToastUtil.show(context, "解析错误");
			LogUtil.e(tag, "parse error" + error.getMessage());
		} else if (error instanceof ServerError) {
			ToastUtil.show(context, "服务器错误");
			LogUtil.e(tag, "server error" + error.getMessage());
		} else if (error instanceof TimeoutError) {
			ToastUtil.show(context, "网络连接超时");
			LogUtil.e(tag, "timeout" + error.getMessage());
		} else if (error instanceof AuthFailureError) {
			ToastUtil.show(context, "授权错误");
			LogUtil.e(tag, "authfailure error" + error.getMessage());
		}
	}

	@Override
	public void handleAllFailure(Context context) {
	}
}
