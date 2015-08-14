package cn.ihealthbaby.weitaixin.library.data.net;

import android.content.Context;

import java.util.Map;

import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
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
					LogUtil.v(TAG, data.toString());
				}
				/**
				 * 处理业务
				 */
				try {
					business.handleData(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			/**
			 * 参数验证失败
			 */
			case Result.VALIDATOR:
				Map<String, Object> msgMap = result.getMsgMap();
				// TODO: 15/7/23 提示消息
				ToastUtil.show(context, msgMap.toString());
				LogUtil.e(TAG, "call", result.getMsg());
				break;
			/**
			 * 账号授权错误
			 */
			case Result.ACCOUNT_ERROR:
//				business.handleAccountError(result.);
				break;
			/**
			 * 服务器错误
			 */
			case Result.ERROR:
//				business.handleError(result.);
				ToastUtil.show(context, result.getMsg());
				LogUtil.e(TAG, "call", result.getMsg());
				break;
			default:
				break;
		}
	}
}
