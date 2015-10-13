package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.data.model.data.RecordData;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import cn.ihealthbaby.weitaixinpro.service.UploadDataUtil;

/**
 * Created by liuhongjian on 15/9/20 13:13.
 */
public class LocalRecordPlayActivity extends RecordPlayActivity {
	private final static int UPLOADTYPE_DATA = 0;
	private final static int UPLOADTYPE_ALL = 1;
	private static final int BUTTON_ALL = 1;
	private static final int BUTTON_DATA = 2;
	public UploadDataUtil uploadDataUtil;

	@Override
	protected void uploadData() {
		checkUploadStatus(BUTTON_DATA);
	}

	@Override
	protected void function() {
		checkUploadStatus(BUTTON_ALL);
	}

	private void checkUploadStatus(final int button) {
		ApiManager.getInstance().hClientAccountApi.checkUpload(record.getLocalRecordId(), new DefaultCallback<AdviceItem>(getApplicationContext(), new AbstractBusiness<AdviceItem>() {
			@Override
			public void handleAllFailure(Context context) {
				super.handleAllFailure(context);
				ToastUtil.show(context, "查询上传状态失败");
			}

			@Override
			public void handleData(AdviceItem adviceItem) {
				UploadDataUtil uploadDataUtil = new UploadDataUtil(LocalRecordPlayActivity.this, record);
				if (adviceItem == null) {
					LogUtil.d(TAG, "未上传过,可以上传");
					if (button == BUTTON_ALL) {
						uploadDataUtil.uploadAll();
					} else if (button == BUTTON_DATA) {
						uploadDataUtil.uploadData();
					}
				} else {
					final String fetalTonePath = adviceItem.getFetalTonePath();
					if (TextUtils.isEmpty(fetalTonePath)) {
						if (button == BUTTON_ALL) {
							uploadDataUtil.updateTone(adviceItem);
						}
						ToastUtil.show(getApplicationContext(), "已上传曲线");
						LogUtil.d(TAG, "已上传过,无胎音");
					} else {
						ToastUtil.show(getApplicationContext(), "已上传全部数据");
						LogUtil.d(TAG, "已上传过,有胎音");
					}
				}
			}
		}), this);
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
			uploadDataUtil = new UploadDataUtil(this, record);
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
}
