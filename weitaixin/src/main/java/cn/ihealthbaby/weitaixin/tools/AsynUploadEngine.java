package cn.ihealthbaby.weitaixin.tools;

import android.content.Context;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.UploadUtil;

/**
 * Created by Think on 2015/8/13.
 */
public class AsynUploadEngine {

    private Context context;
    private UpCompletionHandler upCompletionHandler;
    private UpProgressHandler upProgressHandler;
    private UploadManager uploadManager;
    private UploadOptions options;
    ApiManager instance  ;

    public AsynUploadEngine(Context context){
        this.context=context;
        instance = ApiManager.getInstance();
        initHandler();
    }


    private void initHandler() {
        upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.statusCode==200) {
                    finishedToDoWork.onFinishedWork(key, info, response);
                }else {
                    ToastUtil.show(context, info.error+"");
                }
            }
        };
        upProgressHandler = new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
            }
        };
        uploadManager = new UploadManager(UploadUtil.config());
        UpCancellationSignal UpCancellationSignal = new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        };
        options = new UploadOptions(null, Constants.MIME_TYPE_WAV, true, upProgressHandler, UpCancellationSignal);
    }


    public void uploadFile(File file, String key, String token) {
        uploadManager.put(file, key, token, upCompletionHandler, options);
    }


    public void uploadFile(byte[] data, String key, String token) {
		uploadManager.put(data, key, token, upCompletionHandler, options);
    }



    /**
     * 请求上传key 和 上传token
     */
    public void init(final File file){
        instance.uploadApi.getUploadToken(1, new HttpClientAdapter.Callback<UploadModel>() {
            @Override
            public void call(Result<UploadModel> t) {
                if (t.isSuccess()) {
                    UploadModel data = t.getData();
                    uploadFile(file, data.getKey(), data.getToken());
                } else {
                    ToastUtil.show(context, t.getMsgMap()+"");
                }
            }
        }, getRequestTag());
    }


    /**
     * 请求上传key 和 上传token
     */
    public void init(final byte[] dataBty) {
        instance.uploadApi.getUploadToken(0, new HttpClientAdapter.Callback<UploadModel>() {
            @Override
            public void call(Result<UploadModel> t) {
                if (t.isSuccess()) {
                    UploadModel data = t.getData();
                    uploadFile(dataBty, data.getKey(), data.getToken());
                } else {
                    ToastUtil.show(context, t.getMsgMap()+"");
                }
            }
        }, getRequestTag());
    }

    private Object getRequestTag() {
        return this;
    }

    public FinishedToDoWork finishedToDoWork;
    public interface FinishedToDoWork{
        void onFinishedWork(String key, ResponseInfo info, JSONObject response);
    }

    public void setOnFinishActivity(FinishedToDoWork finishedToDoWork){
        this.finishedToDoWork=finishedToDoWork;
    }



}



