package cn.ihealthbaby.weitaixin.library.data.net.adapter.loopj;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

/**
 * Created by liuhongjian on 15/7/920:29.
 */
public abstract class SimpleResponseHandler<T> extends AsyncHttpResponseHandler implements HttpClientAdapter.Callback<T> {
	private static final String TAG = "SimpleResponseHandler";
	private Context context;
	private HttpClientAdapter.Callback<T> callback;

	public SimpleResponseHandler(Context context, HttpClientAdapter.Callback<T> callback) {
		this.context = context;
		this.callback = callback;
	}

	@Override
	public void onStart() {
		super.onStart();
		LogUtil.v(TAG, "URL::", getRequestURI());
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
		ToastUtil.show(context, "请求失败");
	}

	@Override
	public void onCancel() {
		super.onCancel();
		LogUtil.w(TAG, "onCancel", getRequestURI());
	}

	@Override
	public void onFinish() {
		super.onFinish();
	}

	@Override
	public void onUserException(Throwable error) {
		super.onUserException(error);
	}

	@Override
	public void onProgress(long bytesWritten, long totalSize) {
		super.onProgress(bytesWritten, totalSize);
	}

	@Override
	public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
		super.onPreProcessResponse(instance, response);
	}

	@Override
	public void onRetry(int retryNo) {
		super.onRetry(retryNo);
		LogUtil.v(TAG, "onRetry 重试次数：%s 链接：%s  ", retryNo, getRequestURI());
	}

	@Override
	public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
		super.onPostProcessResponse(instance, response);
	}

	@Override
	public void call(Result<T> result) {
		callback.call(result);
	}
}

