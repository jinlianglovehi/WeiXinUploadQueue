package cn.ihealthbaby.weitaixin.library.data.net;

import android.content.Context;

/**
 * Created by liuhongjian on 15/7/28 17:55.
 */
public abstract class AbstractBusiness<T> implements Business<T> {


	@Override
	public void handleValidator(Context context, T data) throws Exception {

	}

	@Override
	public void handleAccountError(Context context, T data) throws Exception {

	}

	@Override
	public void handleError(Context context, T data) throws Exception {

	}

	@Override
	public void handleException() {

	}

	@Override
	public void handleDefault() {

	}


}
