package cn.ihealthbaby.weitaixin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.ihealthbaby.weitaixin.ui.login.AdviceSettingSP;

public class AdviceSettingService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AdviceSettingSP.saveAdviceSetting(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

}

