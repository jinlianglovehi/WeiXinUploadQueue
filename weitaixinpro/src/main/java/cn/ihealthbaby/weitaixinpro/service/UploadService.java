package cn.ihealthbaby.weitaixinpro.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.UploadUtil;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/9/25 21:11.
 */
public class UploadService extends IntentService {
	private final static String TAG = "UploadService";
	private final ApiManager apiManager;
	public UpProgressHandler upProgressHandler;
	public String token;
	public String key;
	public String localRecordId;
	public long userId;
	private UploadManager uploadManager;
	private UpCompletionHandler upCompletionHandler;
	private UploadOptions options;

	public UploadService() {
		super(TAG);
		apiManager = ApiManager.getInstance();
		uploadManager = new UploadManager(UploadUtil.config());
		initHandler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		localRecordId = intent.getStringExtra(Constants.INTENT_LOCAL_RECORD_ID);
		userId = intent.getLongExtra(Constants.INTENT_USER_ID, -1);
		File voiceFile = FileUtil.getVoiceFile(getApplicationContext(), localRecordId);
		upload(getApplicationContext(), userId, voiceFile);
	}

	/**
	 * 请求上传key 和 上传token
	 */
	public void upload(Context context, Long userId, final File file) {
		apiManager.hClientAccountApi.getUploadToken(userId, 1, new DefaultCallback<UploadModel>(context, new AbstractBusiness<UploadModel>() {
			@Override
			public void handleData(UploadModel data) {
				LogUtil.d(TAG, data.toString());
				key = data.getKey();
				token = data.getToken();
				uploadManager.put(file, key, token, upCompletionHandler, options);
			}

			@Override
			public void handleError(Map<String, Object> msgMap) {
				super.handleError(msgMap);
				UploadEvent event = new UploadEvent(UploadEvent.RESULT_FAIL);
				EventBus.getDefault().post(event);
			}

			@Override
			public void handleClientError(Context context, Exception error) {
				super.handleClientError(context, error);
				UploadEvent event = new UploadEvent(UploadEvent.RESULT_FAIL);
				EventBus.getDefault().post(event);
			}

			@Override
			public void handleAccountError(Context context, Map<String, Object> msgMap) {
				super.handleAccountError(context, msgMap);
				UploadEvent event = new UploadEvent(UploadEvent.RESULT_FAIL);
				EventBus.getDefault().post(event);
			}

			@Override
			public void handleValidator(Context context) {
				super.handleValidator(context);
				UploadEvent event = new UploadEvent(UploadEvent.RESULT_FAIL);
				EventBus.getDefault().post(event);
			}
		}), TAG);
	}

	private void initHandler() {
		upCompletionHandler = new UpCompletionHandler() {
			@Override
			public void complete(String key, ResponseInfo info, JSONObject response) {
				if (info.statusCode == 200) {
					String path = info.path;
					if (!TextUtils.isEmpty(path)) {
						Record record = null;
						RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getApplicationContext());
						try {
							record = recordBusinessDao.queryByLocalRecordId(localRecordId);
						} catch (Exception e) {
							e.printStackTrace();
						}
						record.setSoundUrl(path);
						try {
							recordBusinessDao.update(record);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					LogUtil.d(TAG, "Upload Result: key [%s]", key);
					UploadEvent event = new UploadEvent(UploadEvent.RESULT_SUCCESS, localRecordId, key, token);
					EventBus.getDefault().post(event);
				} else {
					UploadEvent event = new UploadEvent(UploadEvent.RESULT_FAIL);
					EventBus.getDefault().post(event);
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
}
