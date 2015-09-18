package cn.ihealthbaby.weitaixinpro;

import android.content.Context;
import android.text.TextUtils;

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
					ToastUtil.show(context, result.getMsgMap()+"1111");
				}
				break;
			/**
			 * 参数验证失败
			 */
			case Result.VALIDATOR:
				Map<String, Object> msgMap = result.getMsgMap();
				// TODO: 15/7/23 提示消息
				ToastUtil.show(context, msgMap.toString()+"2222");
				LogUtil.e(TAG, "call", result.getMsg());

				//
				try {
					business.handleValidator(context, result.getData());
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.show(context, result.getMsgMap() + "22-33");
				}
				break;
			/**
			 * 账号授权错误
			 */
			case Result.ACCOUNT_ERROR:
				Map<String, Object> msgMapERROR = result.getMsgMap();
				if (msgMapERROR != null) {
					String errorStr = msgMapERROR.toString().trim();
					if (TextUtils.isEmpty(errorStr)) {
						ToastUtil.show(context, "请求失效，请重新登录3333");
					} else {
						ToastUtil.show(context, errorStr + "请求失效，请重新登录3333");
					}
				}


//				try {
//					SPUtil.clearUser(context);
//					WeiTaiXinProApplication.getInstance().mAdapter.setAccountToken(null);
//					if (context instanceof Activity) {
//						Intent intent = new Intent(context, LoginActivity.class);
//						//context是Activity类型   appContext有问题
//						context.startActivity(intent);
////						context.finish();
//					}
//					business.handleAccountError(context, result.getData());
//				} catch (Exception e) {
//					e.printStackTrace();
//					ToastUtil.show(context, result.getMsgMap() + "4444");
//				}
				break;
			/**
			 * 服务器错误
			 */
			case Result.ERROR:
				ToastUtil.show(context, result.getMsgMap() + "5555");
				try {
					business.handleError(context, result.getData());
				} catch (Exception e) {
					e.printStackTrace();
					ToastUtil.show(context, result.getMsgMap() + "6666");
				}
				break;

			default:
				ToastUtil.show(context, result.getMsgMap()+"7777");
				break;
		}
	}
}
