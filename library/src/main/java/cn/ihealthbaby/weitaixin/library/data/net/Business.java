package cn.ihealthbaby.weitaixin.library.data.net;

import android.content.Context;

import java.util.Map;

import cn.ihealthbaby.client.Result;

/**
 * Created by liuhongjian on 15/7/22 22:48.
 */
public interface Business<T> {
	/**
	 * 处理业务数据
	 *
	 * @param data
	 */
	void handleData(T data);

	/**
	 * 参数验证失败
	 *
	 * @param context
	 */
	void handleValidator(Context context);

	/**
	 * 账号错误
	 *
	 * @param context
	 * @param msgMap
	 */
	void handleAccountError(Context context, Map<String, Object> msgMap);

	/**
	 * 服务器错误
	 *
	 * @param msgMap
	 */
	void handleError(Map<String, Object> msgMap);

	/**
	 * 业务异常处理
	 *
	 * @param e
	 */
	void handleException(Exception e);

	/**
	 * 客户端错误
	 *
	 * @param e
	 */
	void handleClientError(Exception e);

	/**
	 * 收到响应
	 *
	 * @param result
	 */
	void handleResult(Result<T> result);
}
