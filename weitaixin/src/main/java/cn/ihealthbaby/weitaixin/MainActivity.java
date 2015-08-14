package cn.ihealthbaby.weitaixin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.form.LoginByPasswordForm;
import cn.ihealthbaby.client.model.Questions;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.data.net.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.UploadUtil;

public class MainActivity extends BaseActivity {
	private final static String TAG = "VolleyAdapterTest";
	private ApiManager instance;
	private Context context;
	private LoginByPasswordForm loginByPasswordForm;
	private VolleyAdapter adapter;
	private File file;
	//	private CountDownLatch signal;
	private DefaultCallback<User> callable0;
	private DefaultCallback callable4;
	private DefaultCallback callable3;
	private DefaultCallback callable2;
	private DefaultCallback callable1;
	private UpCompletionHandler upCompletionHandler;
	private UpProgressHandler upProgressHandler;
	private UploadManager uploadManager;
	private UploadOptions options;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button button = new Button(this);
		setContentView(button);
		setUp();
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					testRequest();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setUp() {
		context = getApplicationContext();
//		signal = new CountDownLatch(1);
		RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(context);
		adapter = new VolleyAdapter(context, Constants.SERVER_URL, requestQueue);
		loginByPasswordForm = new LoginByPasswordForm("13161401474", "123456", "123456789", 1.0d, 1.0d);
		ApiManager.init(adapter);
		instance = ApiManager.getInstance();
		initCallback();
		initHandler();
	}

	public void testRequest() throws Exception {
		LogUtil.v(TAG, "testRequest::%s", "testRequest");
		instance.accountApi.loginByPassword(loginByPasswordForm, callable0);
//		signal.await();
	}

	private void initHandler() {
		upCompletionHandler = new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject response) {
				LogUtil.v(TAG, "complete::%s::%s", info, response);
//				signal.countDown();
			}
		};
		upProgressHandler = new UpProgressHandler() {
			@Override
			public void progress(String key, double percent) {
				LogUtil.v(TAG, "progress", percent);
			}
		};
		uploadManager = new UploadManager(UploadUtil.config());
		options = null;
	}

	public void uploadFile(String key, String token) {
//		uploadManager.put("hello world".getBytes(), key, token, upCompletionHandler, options);
		uploadManager.put(getFile(), key, token, upCompletionHandler, options);
	}

	private void initCallback() {
		/**
		 * 问题
		 */
		callable1 = new DefaultCallback<Questions>(context, new Business<Questions>() {
			@Override
			public void handleData(Questions data) throws Exception {
				LogUtil.v(TAG, "handleData::%s", data);
			}
		});
		/**
		 *
		 */
		callable2 = new DefaultCallback<Void>(context, new Business<Void>() {
			@Override
			public void handleData(Void data) throws Exception {
			}
		});
		/**
		 *
		 */
		callable3 = new DefaultCallback<Boolean>(context, new Business<Boolean>() {
			@Override
			public void handleData(Boolean data) throws Exception {
			}
		});
		/**
		 * 请求上传key 和 上传token
		 */
		callable4 = new DefaultCallback<UploadModel>(context, new Business<UploadModel>() {
			@Override
			public void handleData(UploadModel data) throws Exception {
				LogUtil.v(TAG, "handleData::key:%s,token:%s", data.getKey(), data.getToken());
				uploadFile(data.getKey(), data.getToken());
			}
		});
		/**
		 * 登录请求 登录token
		 */
		callable0 = new DefaultCallback<>(context, new Business<User>() {
			@Override
			public void handleData(User data) throws Exception {
				LogUtil.v(TAG, "handleData::%s", data);
				adapter.setAccountToken(data.getAccountToken());
				instance.uploadApi.getUploadToken(1, callable4);
			}
		});
	}

	public void testDelete() {
		instance.testApi.delete("0", callable3);
	}

	public File getFile() {
		String path = getCacheDir().getPath();
		return new File(path, "1436267027166.WAV");
	}
}
