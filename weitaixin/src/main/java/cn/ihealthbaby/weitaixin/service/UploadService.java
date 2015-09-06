package cn.ihealthbaby.weitaixin.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qiniu.android.http.ResponseInfo;

import org.json.JSONObject;

import cn.ihealthbaby.weitaixin.tools.AsynUploadEngine;

public class UploadService extends IntentService implements  AsynUploadEngine.FinishedToDoWork{

    public UploadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AsynUploadEngine asynUploadEngine = new AsynUploadEngine(getApplicationContext());
//        asynUploadEngine.init();
    }

    @Override
    public void onFinishedWork(String key, ResponseInfo info, JSONObject response) {

    }

}
