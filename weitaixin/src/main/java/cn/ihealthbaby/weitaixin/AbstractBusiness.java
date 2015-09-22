package cn.ihealthbaby.weitaixin;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;

/**
 * Created by liuhongjian on 15/7/28 17:55.
 */
public abstract class AbstractBusiness<T> implements Business<T> {
	@Override
	public void handleData(T data) {
	}

	@Override
	public void handleValidator(Context context) {
	}

	@Override
	public void handleAccountError(Context context, Map<String, Object> msgMap) {
		SPUtil.clearUser(context);
		WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(null);
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void handleError(Map<String, Object> msgMap) {
	}

	@Override
	public void handleException(Exception e) {
	}

	@Override
	public void handleClientError(Exception e) {
	}
}
