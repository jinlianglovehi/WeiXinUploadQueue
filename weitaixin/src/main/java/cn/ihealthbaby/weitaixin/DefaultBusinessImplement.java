package cn.ihealthbaby.weitaixin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.ihealthbaby.weitaixin.library.data.net.DefaultBusiness;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.login.LoginActivity;

/**
 * Created by chenweihua on 2015/9/18.
 */
public class DefaultBusinessImplement extends DefaultBusiness {

    @Override
    public void handleData(Object data) throws Exception {
        super.handleData(data);
        LogUtil.d("handleData","handleData");
    }

    @Override
    public void handleValidator(Context context, Object data) throws Exception {
        super.handleValidator(context, data);
        LogUtil.d("handleValidator","handleValidator");
    }

    @Override
    public void handleAccountError(Context context, Object data) throws Exception {
        super.handleAccountError(context, data);
        SPUtil.clearUser(context);
        WeiTaiXinApplication.getInstance().mAdapter.setAccountToken(null);
        if (context instanceof Activity) {
            Intent intent = new Intent(context, LoginActivity.class);
            //context是Activity类型   appContext有问题
            context.startActivity(intent);
            //context.finish();
        }
        LogUtil.d("handleAccountError", "handleAccountError");
    }

    @Override
    public void handleError(Context context, Object data) throws Exception {
        super.handleError(context, data);
        LogUtil.d("handleError", "handleError");
    }

}
