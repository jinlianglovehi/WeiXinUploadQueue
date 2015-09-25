package cn.ihealthbaby.weitaixin.ui.login;

import android.content.Context;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;

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
                ApiManager.getInstance().adviceApi.getAdviceSetting(hid,
                        new DefaultCallback<AdviceSetting>(context, new AbstractBusiness<AdviceSetting>() {
                            @Override
                            public void handleData(AdviceSetting data) {
                                    SPUtil.saveAdviceSetting(context, data);
                            }

                            @Override
                            public void handleClientError(Context context, Exception e) {
                                super.handleClientError(context, e);
                            }

                            @Override
                            public void handleException(Exception e) {
                                super.handleException(e);
                            }
                        }), "");
            }
        }
    }


}
