package cn.ihealthbaby.weitaixin;

import android.content.Context;

import java.util.Map;

import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

/**
 * Created by liuhongjian on 15/7/22 22:56.
 */
public class DefaultCallback<T> implements HttpClientAdapter.Callback<T> {
	private static final String TAG = "DefaultCallback";
	private Context context;
	private Business<T> business;

	public DefaultCallback(Context context, Business<T> business) {
		this.context = context;
		this.business = business;
	}

	@Override
	public void call(Result<T> result) {
		business.handleResult(result);
		if (result == null) {
			return;
		}
		int status = result.getStatus();
		switch (status) {
			/**
			 * 正常响应业务数据
			 */
			case Result.SUCCESS:
				T data = result.getData();
				if (data != null) {
					LogUtil.d(TAG, "Result.SUCCESS" + data.toString());
				}
				/**
				 * 处理业务
				 */
				try {
					business.handleData(data);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.d(TAG, "Result.SUCCESS.Exception" + e.toString());
					business.handleException(e);
				}
				break;
			/**
			 * 参数验证失败
			 */
			case Result.VALIDATOR:
				String msgMapString = map2String(result.getMsgMap());
				if (msgMapString != null) {
					ToastUtil.show(context, msgMapString);
					LogUtil.e(TAG, "Result.VALIDATOR", msgMapString);
				}
				//
				try {
					business.handleValidator(context);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.d(TAG, "Result.VALIDATOR.Exception==> " + result.getMsgMap() + "Result.VALIDATORException" + e.toString());
					business.handleException(e);
				}
				break;
			/**
			 * 账号授权错误
			 */
			case Result.ACCOUNT_ERROR:
				String map2String = map2String(result.getMsgMap());
				if (map2String != null) {
					ToastUtil.show(context, map2String);
					LogUtil.e(TAG, "Result.ACCOUNT_ERROR" + map2String);
				}
				try {
					business.handleAccountError(context, result.getMsgMap());
				} catch (Exception e) {
					e.printStackTrace();
					business.handleException(e);
				}
				break;
			/**
			 *
			 */
			case Result.CLIENT_ERROR:
				Exception exception = result.getException();
				LogUtil.e(TAG, "CLIENT_ERROR" + exception);
				business.handleClientError(exception);
				break;
			/**
			 * 服务器错误
			 */
			case Result.ERROR:
				String map2String1 = map2String(result.getMsgMap());
				if (map2String1 != null) {
					ToastUtil.show(context, "Result.ERROR" + map2String1);
					LogUtil.e(TAG, "Result.ERROR" + map2String1);
				}
				try {
					business.handleError(result.getMsgMap());
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.e(TAG, "Result.ERROR" + map2String1 + e.toString());
					business.handleException(e);
				}
				break;
			default:
				break;
		}
	}

	public String map2String(Map map) {
		if (map != null && map.size() > 0) {
			StringBuilder stringBuilder = new StringBuilder();
			for (Object o : map.values()) {
				stringBuilder.append(o.toString());
				stringBuilder.append("\r\n");
			}
			return stringBuilder.toString();
		} else {
			return null;
		}
	}
}
