package cn.ihealthbaby.weitaixin.library.data.net.adapter;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.android.volley.RequestQueue;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.form.LoginByPasswordForm;
import cn.ihealthbaby.client.model.Questions;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.net.Business;
import cn.ihealthbaby.weitaixin.library.data.net.DefaultCallback;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.UploadUtil;

/**
 * Created by liuhongjian on 15/7/24 23:16.
 */
public class VolleyAdapterTest extends InstrumentationTestCase {
	private final static String TAG = "VolleyAdapterTest";
	private ApiManager instance;
	private Context context;
	private LoginByPasswordForm loginByPasswordForm;
	private VolleyAdapter adapter;
	private File file;
	private CountDownLatch signal;
	private DefaultCallback<User> callable0;
	private DefaultCallback callable4;
	private DefaultCallback callable3;
	private DefaultCallback callable2;
	private DefaultCallback callable1;
	private UpCompletionHandler upCompletionHandler;
	private UpProgressHandler upProgressHandler;
	private UploadManager uploadManager;
	private UploadOptions options;

	@Override
	protected void setUp() throws Exception {
		context = getInstrumentation().getContext();
		signal = new CountDownLatch(1);
		RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(context);
		adapter = new VolleyAdapter(context, Constants.SERVER_URL, requestQueue);
		loginByPasswordForm = new LoginByPasswordForm("13161401474", "123456", "123456789", 1.0d, 1.0d);
		ApiManager.init(adapter);
		instance = ApiManager.getInstance();
		initCallback();
		initHandler();
	}

	@Override
	protected void runTest() throws Throwable {
		super.runTest();
	}

	public void testFile() {
		assertTrue(getFile().exists());
	}

	public void testRequest() throws Exception {
		LogUtil.v(TAG, "testRequest::%s", "testRequest");
		instance.accountApi.loginByPassword(loginByPasswordForm, callable0);
		signal.await();
	}

	private void initHandler() {
		upCompletionHandler = new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject response) {
				LogUtil.v(TAG, "complete::%s::%s", info, response);
				assertTrue(info.isOK());
				signal.countDown();
			}
		};
		upProgressHandler = new UpProgressHandler() {
			@Override
			public void progress(String key, double percent) {
				LogUtil.v(TAG, "progress", percent);
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
				assertTrue(data);
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
//				UpdateHeadPicForm updateHeadPicForm = new UpdateHeadPicForm("headpic/1/402881fe4e6165af014e6165b38f0066");
//				instance.userApi.updateHeadPic(updateHeadPicForm, new HttpClientAdapter.Callback<Void>() {
//					@Override
//					public void call(Result<Void> t) {
//					}
//				});
			}
		});
	}

	public void testDelete() {
		instance.testApi.delete("0", callable3);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public File getFile() {
		String path = context.getCacheDir().getPath();
		return new File(path, "1436261524075.WAV");
	}

	public void setFile(File file) {
		this.file = file;
	}
}
