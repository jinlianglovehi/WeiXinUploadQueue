package cn.ihealthbaby.weitaixinpro.ui.monitor;

import com.google.gson.Gson;

import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.data.model.data.RecordData;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixinpro.service.UploadUtil;

/**
 * Created by liuhongjian on 15/9/20 13:13.
 */
public class LocalRecordPlayActivity extends RecordPlayActivity {
	private final static int UPLOADTYPE_DATA = 0;
	private final static int UPLOADTYPE_ALL = 1;
	public UploadUtil uploadUtil;

	@Override
	protected void uploadData() {
		uploadUtil.uploadData();
	}

	@Override
	protected void function() {
		uploadUtil.uploadAll();
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
			uploadUtil = new UploadUtil(getApplicationContext(), record);
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
