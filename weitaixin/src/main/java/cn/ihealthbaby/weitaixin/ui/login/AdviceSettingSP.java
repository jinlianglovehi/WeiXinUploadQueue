package cn.ihealthbaby.weitaixin.ui.login;

import android.content.Context;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;

/**
 * Created by chenweihua on 2015/9/14.
 */
public class AdviceSettingSP {

    public static void saveAdviceSetting(final Context context) {
        User user = SPUtil.getUser(context);
        if (user != null) {
            ServiceInfo serviceInfo = user.getServiceInfo();
            if (serviceInfo != null) {
                long hid = serviceInfo.getHospitalId();
                ApiManager.getInstance().adviceApi.getAdviceSetting(hid, new HttpClientAdapter.Callback<AdviceSetting>() {
                    @Override
                    public void call(Result<AdviceSetting> t) {
                        if (t.isSuccess()) {
                            AdviceSetting data = t.getData();
                            if (data != null) {
                                SPUtil.saveAdviceSetting(context, data);
                            } else {
                                ToastUtil.show(context, t.getMsgMap() + "");
                            }
                        } else {
                            ToastUtil.show(context, t.getMsgMap() + "");
                        }
                    }
                }, "");
            }
        }
    }


}
