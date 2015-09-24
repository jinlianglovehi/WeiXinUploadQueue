package cn.ihealthbaby.weitaixin;

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
import cn.ihealthbaby.client.form.UpdateHeadPicForm;
import cn.ihealthbaby.client.form.UserInfoForm;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
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

    public UploadFileEngine(Context context, UserInfoForm form,CustomDialog customDialog){
        this.context=context;
        this.customDialog=customDialog;
        this.form=form;
        initHandler();
    }

    public boolean isUpdateHeadPic=false;
    public boolean isUpdateInfo=false;

    private void initHandler() {
        upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.statusCode==200) {
                    UploadFileEngine.this.key=key;
                    LogUtil.e("errdata", "errdata头像上次七牛成功: " + key);
//                    ToastUtil.show(context.getApplicationContext(), "七牛成功");
//                    if(isUpdateInfo){
//                        completeInfoAction();
//                    }
                    if(isUpdateHeadPic){
                        updateHeadPicAction();
                    }
                }else{
                    LogUtil.e("errdata","errdata头像上次七牛失败 "+info.error);
                    ToastUtil.show(context.getApplicationContext(), info.error+"");
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
        options = new UploadOptions(null, Constants.MIME_TYPE_JPEG, true, upProgressHandler, UpCancellationSignal);
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
//        callable4 = new DefaultCallback<UploadModel>(context, new Business<UploadModel>() {
//            @Override
//            public void handleData(UploadModel data) throws Exception {
//                uploadFile(file, data.getKey(), data.getToken());
//            }
//        });
        ApiManager.getInstance().uploadApi.getUploadToken(0,
                new DefaultCallback<UploadModel>(context, new AbstractBusiness<UploadModel>() {
                    @Override
                    public void handleData(UploadModel data) {
                        uploadFile(file, data.getKey(), data.getToken());
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }



    public void init(final byte[] dataBty){
        /**
         * 请求上传key 和 上传token
         */
        ApiManager.getInstance().uploadApi.getUploadToken(0,
                new DefaultCallback<UploadModel>(context, new AbstractBusiness<UploadModel>() {
                    @Override
                    public void handleData(UploadModel data) {
                        uploadFile(dataBty, data.getKey(), data.getToken());
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }



    private Object getRequestTag() {
        return this;
    }


    public void completeInfoAction(){
        form.setHeadPic(key);
        ApiManager.getInstance().userApi.completeInfo(form,
                new DefaultCallback<User>(context, new AbstractBusiness<User>() {
                    @Override
                    public void handleData(User data) {
                        if (data != null) {
                            SPUtil.saveUser(context,data);
                            if (finishActivity != null) {
                                WeiTaiXinApplication.getInstance().putValue("InfoEdit","true");
                                finishActivity.onFinishActivity(true);
                            }
                        } else {
//                            WeiTaiXinApplication.getInstance().putValue("InfoEdit","");
                            ToastUtil.show(context, "完善个人资料失败");
                        }
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        if (finishActivity != null) {
                            finishActivity.onFinishActivity(false);
                        }
//                        WeiTaiXinApplication.getInstance().putValue("InfoEdit","");
                        ToastUtil.show(context, "完善个人资料失败");
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        if (finishActivity != null) {
                            finishActivity.onFinishActivity(false);
                        }
//                        WeiTaiXinApplication.getInstance().putValue("InfoEdit","");
                        ToastUtil.show(context, "完善个人资料失败");
                        customDialog.dismiss();
                    }
                }), getRequestTag());
    }


    public void updateHeadPicAction(){
        UpdateHeadPicForm updateHeadPicForm=new UpdateHeadPicForm();
        updateHeadPicForm.setHeadPicPath(key);
        ApiManager.getInstance().userApi.updateHeadPic(updateHeadPicForm,
                new DefaultCallback<String>(context, new AbstractBusiness<String>() {
                    @Override
                    public void handleData(String data) {
                        SPUtil.saveHeadPic(context,data);
                        LogUtil.e("errdata", "errdata服务器头像上传: " + data);
                        ToastUtil.show(context.getApplicationContext(), "头像成功上传到服务器上");
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleClientError(Exception e) {
                        super.handleClientError(e);
                        ToastUtil.show(context.getApplicationContext(), "头像上传到服务器失败");
                        customDialog.dismiss();
                    }

                    @Override
                    public void handleException(Exception e) {
                        super.handleException(e);
                        ToastUtil.show(context.getApplicationContext(), "头像上传到服务器失败");
                        customDialog.dismiss();
                    }
                }),getRequestTag());
    }


    public FinishActivity finishActivity;
    public interface FinishActivity{
        void onFinishActivity(boolean isFinish);
    }

    public void setOnFinishActivity(FinishActivity fa){
        this.finishActivity=fa;
    }



}



