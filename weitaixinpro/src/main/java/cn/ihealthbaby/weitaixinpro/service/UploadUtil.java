package cn.ihealthbaby.weitaixinpro.service;

import android.content.Context;
import android.text.TextUtils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.Zone;

import org.json.JSONObject;

import java.io.File;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.form.ToneForm;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.client.model.UploadModel;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import cn.ihealthbaby.weitaixinpro.tools.CustomDialog;

/**
 * Created by liuhongjian on 15/10/12 19:56.
 */
public class UploadUtil {
	private final static String TAG = "UploadUtil";
	private static final String TONE_FORMAT = Constants.MIME_TYPE_WAV;
	private static final int TONE_VERSION = 1;
	public final String localRecordId;
	public final long userId;
	private final File file;
	private CustomDialog customDialog;
	private ApiManager apiManager;
	private UploadManager uploadManager;
	private UpProgressHandler upProgressHandler;
	private Context context;
	private Record record;
	private UploadOptions options;
	private String key;
	private String token;
	private UpCompletionHandler upCompletionHandler;
	private AdviceItem adviceItem;

	public UploadUtil(Context context, Record record) {
		this.context = context;
		this.record = record;
		userId = record.getUserId();
		localRecordId = record.getLocalRecordId();
		file = FileUtil.getVoiceFile(context, localRecordId);
		init();
	}

	/**
	 *
	 *
	 */
	public void uploadAll() {
		customDialog.show();
		uploadRecordFirst();
	}

	/**
	 *
	 *
	 */
	public void uploadData() {
		customDialog.show();
		uploadOnlyRecord();
	}

	/**
	 *
	 *
	 */
	public void updateTone(AdviceItem adviceItem) {
		this.adviceItem = adviceItem;
		customDialog.show();
		requestForKeyAndToken();
	}

	private Configuration getUploadConfig() {
		return new Configuration.Builder()
				       .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认 256K
				       .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认 512K
				       .connectTimeout(10) // 链接超时。默认 10秒
				       .responseTimeout(60) // 服务器响应超时。默认 60秒
//				       .recorder(recorder)  // recorder 分片上传时，已上传片记录器。默认 null
//				       .recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
				       .zone(Zone.zone0) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。默认 Zone.zone0
				       .build();
	}

	private void init() {
		customDialog = new CustomDialog(context, "正在上传胎音");
		apiManager = ApiManager.getInstance();
		uploadManager = new UploadManager(cn.ihealthbaby.weitaixin.library.util.UploadUtil.config());
		upCompletionHandler = new UpCompletionHandler() {
			/**
			 * 上传胎音文件结束
			 *
			 * @param key
			 * @param info
			 * @param response
			 */
			@Override
			public void complete(String key, ResponseInfo info, JSONObject response) {
				if (info.statusCode == 200) {
					String path = info.path;
					if (!TextUtils.isEmpty(path)) {
						Record record = null;
						RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(context);
						try {
							record = recordBusinessDao.queryByLocalRecordId(localRecordId);
						} catch (Exception e) {
							e.printStackTrace();
							ToastUtil.show(context, "数据库查询错误");
						}
						record.setSoundUrl(path);
						try {
							recordBusinessDao.update(record);
						} catch (Exception e) {
							e.printStackTrace();
							ToastUtil.show(context, "数据库更新错误");
						}
					}
					LogUtil.d(TAG, "Upload Result: key [%s]", key);
					updateSoundFile(adviceItem, key);
				} else {
					/**
					 * 上传失败
					 */
					customDialog.dismiss();
					ToastUtil.show(context, "上传胎音文件失败");
				}
			}
		};
		upProgressHandler = new UpProgressHandler() {
			/**
			 * 发布上传进度
			 * @param key
			 * @param percent
			 */
			@Override
			public void progress(String key, double percent) {
			}
		};
		uploadManager = new UploadManager(getUploadConfig());
		UpCancellationSignal UpCancellationSignal = new UpCancellationSignal() {
			@Override
			public boolean isCancelled() {
				return false;
			}
		};
		options = new UploadOptions(null, Constants.MIME_TYPE_WAV, true, upProgressHandler, UpCancellationSignal);
	}

	private void updateSoundFile(AdviceItem adviceItem, String key) {
		final ToneForm toneForm = new ToneForm();
		toneForm.setFetalToneVersion(TONE_VERSION);
		toneForm.setFetalToneFormat(TONE_FORMAT);
		toneForm.setFetalTonePath(key);
		apiManager.hClientAccountApi.uploadTone(adviceItem.getId(), toneForm, new DefaultCallback<AdviceItem>(context, new AbstractBusiness<AdviceItem>() {
			@Override
			public void handleData(AdviceItem data) {
				customDialog.dismiss();
				ToastUtil.show(context, "上传成功");
				updateUploadState(Record.UPLOAD_STATE_CLOUD);
			}

			@Override
			public void handleAllFailure(Context context) {
				super.handleAllFailure(context);
				customDialog.dismiss();
				ToastUtil.show(context, "上传失败");
			}
		}), TAG);
	}

	/**
	 * 2.向服务器请求 上传key 和 上传token
	 */
	private void requestForKeyAndToken() {
		apiManager.hClientAccountApi.getUploadToken(userId, 1, new DefaultCallback<UploadModel>(context, new AbstractBusiness<UploadModel>() {
			@Override
			public void handleData(UploadModel data) {
				LogUtil.d(TAG, data.toString());
				key = data.getKey();
				token = data.getToken();
				/**
				 * 向七牛上传文件
				 */
				uploadManager.put(file, key, token, upCompletionHandler, options);
			}

			@Override
			public void handleAllFailure(Context context) {
				super.handleAllFailure(context);
				/**
				 * 上传失败
				 */
				customDialog.dismiss();
				ToastUtil.show(context, "获取服务器上传授权失败");
			}
		}), TAG);
	}

	private AdviceForm getUploadData(Record record, String key) {
		AdviceForm adviceForm = new AdviceForm();
		adviceForm.setClientId(record.getLocalRecordId());
		adviceForm.setServiceId(record.getServiceId());
		adviceForm.setDataType(1);
		adviceForm.setDeviceType(1);
		adviceForm.setFeeling(record.getFeelingString());
		adviceForm.setAskPurpose(record.getPurposeString());
		adviceForm.setData(record.getRecordData());
		adviceForm.setTestTime(record.getRecordStartTime());
		adviceForm.setTestTimeLong(record.getDuration());
		adviceForm.setFetalTonePath(key);
		return adviceForm;
	}

	/**
	 * 1.上传记录
	 */
	private void uploadOnlyRecord() {
		ApiManager.getInstance().hClientAccountApi.uploadData(getUploadData(record, null), new DefaultCallback<AdviceItem>(context, new AbstractBusiness<AdviceItem>() {
			@Override
			public void handleData(AdviceItem adviceItem) {
				UploadUtil.this.adviceItem = adviceItem;
				customDialog.dismiss();
				ToastUtil.show(context, "上传曲线成功");
			}

			@Override
			public void handleAllFailure(Context context) {
				super.handleAllFailure(context);
				customDialog.dismiss();
				ToastUtil.show(context, "上传曲线失败");
			}
		}), this);
	}

	private void uploadRecordFirst() {
		ApiManager.getInstance().hClientAccountApi.uploadData(getUploadData(record, null), new DefaultCallback<AdviceItem>(context, new AbstractBusiness<AdviceItem>() {
			@Override
			public void handleData(AdviceItem adviceItem) {
				UploadUtil.this.adviceItem = adviceItem;
				requestForKeyAndToken();
			}

			@Override
			public void handleAllFailure(Context context) {
				super.handleAllFailure(context);
				customDialog.dismiss();
				ToastUtil.show(context, "上传曲线失败");
			}
		}), this);
	}

	/**
	 * 记录保存到数据库
	 */
	private void updateUploadState(int result) {
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(context);
		try {
			record.setUploadState(result);
			recordBusinessDao.update(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
