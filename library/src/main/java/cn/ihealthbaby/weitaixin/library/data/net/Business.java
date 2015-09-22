package cn.ihealthbaby.weitaixin.library.data.net;

import android.content.Context;

/**
 * Created by liuhongjian on 15/7/22 22:48.
 */
public interface Business<T> {
	void handleData(T data) throws Exception;

	void handleValidator(Context context) throws Exception;

	void handleAccountError(Context context, T data) throws Exception;

	void handleError(Context context, T data) throws Exception;

	void handleException();

	void handleDefault();
}
