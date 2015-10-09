package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.data.model.data.RecordData;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import cn.ihealthbaby.weitaixinpro.service.UploadEvent;
import cn.ihealthbaby.weitaixinpro.service.UploadService;
import cn.ihealthbaby.weitaixinpro.tools.CustomDialog;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/9/20 13:13.
 */
public class LocalRecordPlayActivity extends RecordPlayActivity {
	private final static int UPLOADTYPE_DATA = 0;
	private final static int UPLOADTYPE_ALL = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	protected void uploadData() {
		ApiManager.getInstance().hClientAccountApi.uploadData(UPLOADTYPE_DATA, getUploadData(record, null), new DefaultCallback<AdviceItem>(getApplicationContext(), new AbstractBusiness<AdviceItem>() {
			@Override
			public void handleData(AdviceItem data) {
				ToastUtil.show(getApplicationContext(), "上传曲线成功");
				updateUloadState(Record.UPLOAD_STATE_CLOUD);
			}
		}), getRequestTag());
	}

	@Override
	protected void function() {
		Intent uploadService = new Intent(getApplicationContext(), UploadService.class);
		uploadService.putExtra(Constants.INTENT_USER_ID, record.getUserId());
		uploadService.putExtra(Constants.INTENT_LOCAL_RECORD_ID, record.getLocalRecordId());
		CustomDialog customDialog = new CustomDialog();
		dialog = customDialog.createDialog1(this, "正在上传胎音文件...");
		dialog.show();
		startService(uploadService);
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
	 * 记录保存到数据库
	 */
	private void updateUloadState(int result) {
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getApplicationContext());
		try {
			record.setUploadState(result);
			recordBusinessDao.update(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void getData() {
		uuid = getIntent().getStringExtra(Constants.INTENT_LOCAL_RECORD_ID);
		if (uuid == null) {
			ToastUtil.show(getApplicationContext(), "未获取到记录");
			return;
		}
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getApplicationContext());
		try {
			record = recordBusinessDao.queryByLocalRecordId(getIntent().getStringExtra(Constants.INTENT_LOCAL_RECORD_ID));
			path = record.getSoundPath();
			String rData = record.getRecordData();
			Gson gson = new Gson();
			RecordData recordData = gson.fromJson(rData, RecordData.class);
			data = recordData.getData();
			fhrs = data.getHeartRate();
			doctors = Util.time2Position(data.getDoctor());
			fetalMove = Util.time2Position(data.getFm());
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getApplicationContext(), "获取数据失败");
		}
	}

	public void onEventMainThread(UploadEvent event) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		LogUtil.d(TAG, event.toString());
		switch (event.getResult()) {
			case UploadEvent.RESULT_SUCCESS:
				ApiManager.getInstance().hClientAccountApi.uploadData(UPLOADTYPE_ALL, getUploadData(record, event.getKey()), new DefaultCallback<AdviceItem>(getApplicationContext(), new AbstractBusiness<AdviceItem>() {
					@Override
					public void handleData(AdviceItem data) {
						ToastUtil.show(getApplicationContext(), "全部上传成功");
						updateUloadState(Record.UPLOAD_STATE_CLOUD);
					}
				}), getRequestTag());
				break;
			case UploadEvent.RESULT_FAIL:
				ToastUtil.show(getApplicationContext(), "上传失败");
				updateUloadState(Record.UPLOAD_STATE_LOCAL);
				break;
			default:
				break;
		}
		String localRecordId = event.getLocalRecordId();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
