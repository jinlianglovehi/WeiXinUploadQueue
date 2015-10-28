package cn.ihealthbaby.weitaixin.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.AskPurposeType;
import cn.ihealthbaby.client.model.CommonConfig;
import cn.ihealthbaby.client.model.FeelingType;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.ui.login.AdviceSettingSP;

public class CommonConfigService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pullData();
        return super.onStartCommand(intent, flags, startId);
    }

    public void pullData() {
        if (SPUtil.isLogin(this)) {
            ApiManager.getInstance().commonApi.getCommonConfig(
                    new DefaultCallback<CommonConfig>(this, new AbstractBusiness<CommonConfig>() {
                        @Override
                        public void handleData(CommonConfig data) {
                            if (data != null) {
                                List<AskPurposeType> askPurposetypes = data.getAskPurposetypes();
                                List<FeelingType> feelingTypes = data.getFeelingTypes();
                                SPUtil.saveCommonConfig(CommonConfigService.this, new List[]{askPurposetypes, feelingTypes});
                            }
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

