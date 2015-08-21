package cn.ihealthbaby.weitaixin.tools;

import android.app.Dialog;
import android.content.Context;

import com.android.volley.RequestQueue;
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
import cn.ihealthbaby.client.form.UpdateHeadPicForm;
import cn.ihealthbaby.client.form.UserInfoForm;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.data.net.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.UploadUtil;

/**
 * Created by Think on 2015/8/13.
 */
public class UploadFileEngine {

    public CustomDialog customDialog;
    private Context context;
    private UpCompletionHandler upCompletionHandler;
    private UpProgressHandler upProgressHandler;
    private UploadManager uploadManager;
    private UploadOptions options;
    private DefaultCallback<UploadModel> callable4;
    private UserInfoForm form;
    private String key;
    ApiManager instance;

    public UploadFileEngine(Context context, UserInfoForm form,CustomDialog customDialog){
        this.context=context;
        this.customDialog=customDialog;
        this.form=form;
        instance = ApiManager.getInstance();
        initHandler();
    }


    private void initHandler() {
        upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.statusCode==200) {
                    UploadFileEngine.this.key=key;
                    LogUtil.e("errrrrdata", "errrrrdata头像上次七牛成功");
                    ToastUtil.show(context.getApplicationContext(), "头像上次七牛成功");
                }else{
                    LogUtil.e("errrrrdata","errrrrdata头像上次七牛失败");
                    ToastUtil.show(context.getApplicationContext(), "头像上次七牛失败");
                }
                customDialog.dismiss();
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


    public void init(final File file){
        /**
         * 请求上传key 和 上传token
         */
        callable4 = new DefaultCallback<UploadModel>(context, new Business<UploadModel>() {
            @Override
            public void handleData(UploadModel data) throws Exception {
                uploadFile(file, data.getKey(), data.getToken());
            }
        });

        instance.uploadApi.getUploadToken(1, callable4);
    }

    public void init(final byte[] dataBty){
        /**
         * 请求上传key 和 上传token
         */
        instance.uploadApi.getUploadToken(0, new HttpClientAdapter.Callback<UploadModel>() {
            @Override
            public void call(Result<UploadModel> t) {
                if (t.isSuccess()) {
                    UploadModel data = t.getData();
                    uploadFile(dataBty, data.getKey(), data.getToken());
                } else {
                    ToastUtil.show(context, t.getMsg());
                }
                customDialog.dismiss();
            }
        });
    }


    public void completeInfoAction(){
        form.setHeadPic(key);
        instance.userApi.completeInfo(form, new HttpClientAdapter.Callback<User>() {
            @Override
            public void call(Result<User> t) {
                if (t.isSuccess()) {
                    LogUtil.e("errrrrdata", "errrrrdata完善个人资料成功");
//                            ToastUtil.show(context.getApplicationContext(),"完善个人资料成功");
                } else {
                    LogUtil.e("errrrrdata", "errrrrdata完善个人资料失败");
//                            ToastUtil.show(context.getApplicationContext(),"完善个人资料失败");
                }
                customDialog.dismiss();
            }
        });
    }


    public void updateHeadPicAction(){
        UpdateHeadPicForm updateHeadPicForm=new UpdateHeadPicForm();
        updateHeadPicForm.setHeadPicPath(key);
        instance.userApi.updateHeadPic(updateHeadPicForm, new HttpClientAdapter.Callback<Void>() {
            @Override
            public void call(Result<Void> t) {
                if (t.isSuccess()) {
                    ToastUtil.show(context.getApplicationContext(), "头像上传成功");
                } else {
                    ToastUtil.show(context.getApplicationContext(), "头像上传失败");
                }
                customDialog.dismiss();
            }
        });
    }


}



