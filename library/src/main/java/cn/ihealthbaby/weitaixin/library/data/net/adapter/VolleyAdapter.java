package cn.ihealthbaby.weitaixin.library.data.net.adapter;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.DefaultErrorListener;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.error.UnsupportRequestMethod;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.request.AbstractReqeust;
import cn.ihealthbaby.weitaixin.library.data.net.paser.Parser;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * TRACE 请求现在使用兼容模式
 * @author liuhongjian on 15/7/23 10:39.
 * @author zuoge85 修改支持patch 请求，使用兼容模式
 */
public class VolleyAdapter extends AbstractHttpClientAdapter {
	private final static String TAG = "VolleyAdapter";
	private static final String CHAR_SET = "UTF-8";
	public static final String PATCH = "PATCH";

	private final Context context;
	private RequestQueue requestQueue;

	public VolleyAdapter(Context context, String serverUrl, RequestQueue requestQueue) {
		super(serverUrl);
		this.context = context;
		this.requestQueue = requestQueue;
	}

	@Override
	public <T> void requestAsync(final RequestParam<T> requestParam) {
		Object tag = requestParam.getTag();
		LogUtil.v(TAG, "requestAsync::%s,", requestParam);
		Callback<T> callable = requestParam.getCallable();
		final List<Map.Entry<String, Object>> form = requestParam.getForm();
		int method = translate(requestParam.getMethod());


		final boolean isPostBody = (method == Request.Method.POST) || (method == Request.Method.PUT) || (method == Request.Method.PATCH);
		String url = getUrl(method, requestParam.getUri(), form);
		DefaultErrorListener defaultErrorListener = new DefaultErrorListener(context, TAG);


		AbstractReqeust<T> request
				= new AbstractReqeust<T>(
						                        method,
						                        url,
						                        defaultErrorListener,
						                        callable) {
			@Override
			protected Response<Result<T>> parseNetworkResponse(NetworkResponse networkResponse) {
				LogUtil.v(TAG, "parseNetworkResponse::%s", networkResponse);
				Response<Result<T>> response = null;
				String json = getString(networkResponse);
				LogUtil.v(TAG, "parseNetworkResponse::%s", json);
				try {
					Result<T> result = Parser.getInstance().parse(json, requestParam.getType());
					LogUtil.v(TAG, "parseNetworkResponse::%s", result);
					response = Response.success(result, HttpHeaderParser.parseCacheHeaders(networkResponse));
				} catch (JsonParseException e) {
					response = Response.error(new ParseError(networkResponse));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return response;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> map = new HashMap<>();
				if (requestParam.isAccount()) {
					map.put("accountToken", accountToken);
				}
				map.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
				return map;
			}

			@Override
			public byte[] getBody() throws AuthFailureError {
				if(requestParam.getMethod().equalsIgnoreCase(PATCH)){
//					_method
					List<Map.Entry<String, Object>> varForm = form;
					if (varForm == null) {
						varForm = new ArrayList<>();
					}
					varForm.add(new AbstractMap.SimpleImmutableEntry<String, Object>("_method",PATCH));
					try {
						return encodeParameters(varForm, getParamsEncoding(), new StringBuilder()).toString().getBytes(getParamsEncoding());
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), e);
					}
				} else{
					if (isPostBody && form != null && form.size() > 0) {
						try {
							return encodeParameters(form, getParamsEncoding(), new StringBuilder()).toString().getBytes(getParamsEncoding());
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), e);
						}
					} else {
						return null;
					}
				}
			}
		};
		request.setTag(tag);
		HttpClientAdapter.RequestCallback<T> requestCallback = requestParam.getRequestCallback();
		if (requestCallback != null) {
			requestCallback.call(request);
		}
		requestQueue.add(request);
	}

	public String getUrl(int method, String uri, List<Map.Entry<String, Object>> form) {
		final boolean isPostBody = (method == Request.Method.POST) || (method == Request.Method.PUT) || (method == Request.Method.PATCH);
		String url;
		if ((!isPostBody) && form != null && form.size() > 0) {
			StringBuilder sb = new StringBuilder(serverUrl).append(uri).append("?");
			encodeParameters(form, CHAR_SET, sb);
			url = sb.toString();
		} else {
			url = serverUrl + uri;
		}
		return url;
	}

	private StringBuilder encodeParameters(List<Map.Entry<String, Object>> form, String paramsEncoding, StringBuilder encodedParams) {
		try {
			for (Map.Entry<String, Object> entry : form) {
				encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
				encodedParams.append('=');
				encodedParams.append(URLEncoder.encode(toString(entry.getValue()), paramsEncoding));
				encodedParams.append('&');
			}
			return encodedParams;
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
		}
	}

	protected int translate(String methodString) {
		int method = Request.Method.DEPRECATED_GET_OR_POST;
		switch (methodString.toUpperCase()) {
			case "GET":
				method = Request.Method.GET;
				break;
			case "POST":
				method = Request.Method.POST;
				break;
			case "PUT":
				method = Request.Method.PUT;
				break;
			case PATCH:
				method = Request.Method.POST;
				break;
			case "DELETE":
				method = Request.Method.DELETE;
				break;
			/**
			 * 以下信息暂时不用
			 */
			case "HEAD":
				method = Request.Method.HEAD;
				break;
			case "OPTIONS":
				method = Request.Method.OPTIONS;
				break;
			case "TRACE":
				method = Request.Method.TRACE;
				break;
			default:
				try {
					throw new VolleyError(new UnsupportRequestMethod("不支持该类型"));
				} catch (VolleyError volleyError) {
					LogUtil.e(TAG, "translate", volleyError.getMessage());
					volleyError.printStackTrace();
				}
		}
		return method;
	}

	public void cancleAllRequest(Object tag) {
		requestQueue.cancelAll(tag);
	}
}
