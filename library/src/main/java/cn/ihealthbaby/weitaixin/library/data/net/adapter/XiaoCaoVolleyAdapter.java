package cn.ihealthbaby.weitaixin.library.data.net.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.weitaixin.library.data.net.paser.Parser;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.DefaultErrorListener;

/**
 * @author zuoge85 on 15/6/15.
 */
public class XiaoCaoVolleyAdapter extends AbstractHttpClientAdapter {
	public static final String CHAR_SET = "UTF-8";
	private final static String TAG = "XiaoCaoVolleyAdapter";
	private RequestQueue requestQueue;
	private Context context;

	public XiaoCaoVolleyAdapter(Context context, String serverUrl) {
		super(serverUrl);
		this.context = context;
		requestQueue = Volley.newRequestQueue(context);
		requestQueue.start();
	}

	@Override
	public <T> void requestAsync(final HttpClientAdapter.RequestParam<T> requestParam) {
		final int methodStatus = getMethod(requestParam.getMethod());
		final boolean isPostBody = (methodStatus == Request.Method.POST) || (methodStatus == Request.Method.PUT) || (methodStatus == Request.Method.PATCH);
		final String url;
		final List<Map.Entry<String, Object>> form = requestParam.getForm();
		if ((!isPostBody) && form != null && form.size() > 0) {
			StringBuilder sb = new StringBuilder(serverUrl).append(requestParam.getUri()).append("?");
			encodeParameters(form, CHAR_SET, sb);
			url = sb.toString();
		} else {
			url = serverUrl + requestParam.getUri();
		}
		Request<Result<T>> request = new Request<Result<T>>(methodStatus, url, new DefaultErrorListener(context, TAG)
//				                                                   new Response.ErrorListener() {
//					                                                   @Override
//					                                                   public void onErrorResponse(VolleyError error) {
////                        log.error("请求错误,url:{},msg:{}", url, ex.getMessage(), ex);
//						                                                   String parsed = getString(error.networkResponse);
//						                                                   requestParam.getCallable().call(Result.<T>createError(error, error.getMessage() + parsed));
//					                                                   }
//				                                                   }
		) {
			public byte[] getBody() throws AuthFailureError {
				if (isPostBody && form != null && form.size() > 0) {
					try {
						return encodeParameters(form, getParamsEncoding(), new StringBuilder()).toString().getBytes(getParamsEncoding());
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), e);
					}
				}
				return null;
			}

			@Override
			protected void deliverResponse(Result<T> result) {
				requestParam.getCallable().call(result);
			}

			@Override
			protected Response<Result<T>> parseNetworkResponse(NetworkResponse response) {
				String parsed = getString(response);
				Result<T> result = null;
				try {
					result = Parser.getInstance().parse(parsed, requestParam.getType());
				} catch (JsonParseException e) {
					e.printStackTrace();
					return Response.error(new ParseError());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
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
		};
		HttpClientAdapter.RequestCallback<T> requestCallback = requestParam.getRequestCallback();
		if (requestCallback != null) {
			requestCallback.call(request);
		}
		requestQueue.add(request);
	}

	@NonNull
	private String getString(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf8"));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return parsed;
	}

	private int getMethod(String method) {
		switch (method) {
			case "POST":
				return Request.Method.POST;
			case "GET":
				return Request.Method.GET;
			case "PUT":
				return Request.Method.PUT;
			case "DELETE":
				return Request.Method.DELETE;
			case "PATCH":
				return Request.Method.PATCH;
			default:
				throw new RuntimeException("不支持的类型" + method);
		}
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
}
