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
import cn.ihealthbaby.client.form.UserInfoForm;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.data.net.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.UploadUtil;

/**
 * Created by Think on 2015/8/13.
 */
public class UploadFileEngine {

    private Dialog dialog;
    private Context context;
    private UpCompletionHandler upCompletionHandler;
    private UpProgressHandler upProgressHandler;
    private UploadManager uploadManager;
    private UploadOptions options;
    private DefaultCallback<UploadModel> callable4;
    private UserInfoForm form;
    ApiManager instance;

    public UploadFileEngine(Context context, UserInfoForm form,Dialog dialog){
        this.context=context;
        this.dialog=dialog;
        this.form=form;
        initApiManager();
        initHandler();
    }


    private void initHandler() {
        upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
//                System.err.println("errrrrdata完善"+info);
                if (info.statusCode==200) {
                    form.setHeadPic(key);

                    System.err.println("form: "+form);

                    instance.userApi.completeInfo(form, new HttpClientAdapter.Callback<User>() {
                        @Override
                        public void call(Result<User> t) {
                            System.err.println("errrrrdata==Result" + t.isSuccess());
                            if (t.isSuccess()) {
                                System.err.println("errrrrdata完善个人资料成功");
//                            ToastUtil.show(context.getApplicationContext(),"完善个人资料成功");
                            } else {
                                System.err.println("errrrrdata完善个人资料失败");
//                            ToastUtil.show(context.getApplicationContext(),"完善个人资料失败");
                            }
                        }
                    });

/*

                    instance.userApi.completeInfo(form, new DefaultCallback<User>(context.getApplicationContext(), new Business<User>() {
                        @Override
                        public void handleData(User data) throws Exception {
                            System.err.println("errrrrdata==Result" + data);
                            if (data!=null) {
                                System.err.println("errrrrdata完善个人资料成功");
//                            ToastUtil.show(context.getApplicationContext(),"完善个人资料成功");
                            } else {
                                System.err.println("errrrrdata完善个人资料失败");
//                            ToastUtil.show(context.getApplicationContext(),"完善个人资料失败");
                            }
                        }
                    }));
*/


                    System.err.println("errrrrdata头像上次七牛成功");
                    ToastUtil.show(context.getApplicationContext(), "头像上次七牛成功");
                }else{
                    System.err.println("errrrrdata头像上次七牛失败");
                    ToastUtil.show(context.getApplicationContext(), "头像上次七牛失败");
                }
                dialog.dismiss();
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
        callable4 = new DefaultCallback<UploadModel>(context, new Business<UploadModel>() {
            @Override
            public void handleData(UploadModel data) throws Exception {
//                System.err.println("errrrrdata: " + data);
                uploadFile(dataBty, data.getKey(), data.getToken());
            }
        });
        instance.uploadApi.getUploadToken(1, callable4);
    }



    public void initApiManager(){
        RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(context);
        VolleyAdapter adapter = new VolleyAdapter(context, Constants.SERVER_URL, requestQueue);
        adapter.setAccountToken(WeiTaiXinApplication.accountToken);
        ApiManager.init(adapter);
        instance = ApiManager.getInstance();
    }


}



