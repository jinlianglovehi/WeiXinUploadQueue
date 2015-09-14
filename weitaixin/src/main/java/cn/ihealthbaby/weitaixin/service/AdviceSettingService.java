package cn.ihealthbaby.weitaixin.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qiniu.android.http.ResponseInfo;

import org.json.JSONObject;

import java.security.Provider;

import cn.ihealthbaby.weitaixin.tools.AsynUploadEngine;
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

