package cn.ihealthbaby.weitaixin.ui.monitor;

import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;

import org.json.JSONObject;

import java.io.File;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.data.model.data.RecordData;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.AsynUploadEngine;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.record.AskDoctorActivity;
import cn.ihealthbaby.weitaixin.ui.widget.MonitorDialog;

/**
 * Created by liuhongjian on 15/9/20 13:13.
 */
public class LocalRecordPlayActivity extends RecordPlayActivity {
	private final static String TAG = "LocalRecordPlayActivity";
	public CustomDialog customDialog;
	private String key;

	@Override
	protected void function() {
		if (!Util.isWifi(getApplicationContext())) {
			final MonitorDialog monitorDialog = new MonitorDialog(this, new String[]{"建议在wifi环境下上传", "立即上传", "取消上传"});
			monitorDialog.setOperationAction(new MonitorDialog.OperationAction() {
				@Override
				public void left(Object... obj) {
					monitorDialog.dismiss();
					upload();
				}

				@Override
				public void right(Object... obj) {
					monitorDialog.dismiss();
				}
			});
			monitorDialog.show();
		} else {
			upload();
		}
	}

	private void upload() {
		AsynUploadEngine asynUploadEngine = new AsynUploadEngine(getApplicationContext());
		final File file = new File(FileUtil.getVoiceDir(getApplicationContext()), uuid);
		if (!file.exists()) {
			ToastUtil.show(getApplicationContext(), "未找到胎音文件");
			return;
		}
		dialog.show();
		asynUploadEngine.init(file);
		asynUploadEngine.setOnFinishActivity(new AsynUploadEngine.FinishedToDoWork() {
			@Override
			public void onFinishedWork(final String key, final ResponseInfo info, JSONObject response) {
				ApiManager.getInstance().adviceApi.uploadData(getUploadData(record, key),
						                                             new DefaultCallback<AdviceItem>(getApplicationContext(), new AbstractBusiness<AdviceItem>() {
							                                             @Override
							                                             public void handleResult(Result<AdviceItem> result) {
								                                             super.handleResult(result);
								                                             customDialog.dismiss();
							                                             }

							                                             @Override
							                                             public void handleData(final AdviceItem data) {
								                                             ToastUtil.show(getApplicationContext(), "上传成功");
								                                             btnBusiness.setImageResource(R.drawable.button_ask_doctor);
								                                             try {
									                                             final RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getApplicationContext());
									                                             record = recordBusinessDao.queryByLocalRecordId(data.getClientId());
									                                             record.setUploadState(Record.UPLOAD_STATE_CLOUD);
									                                             recordBusinessDao.update(record);
								                                             } catch (Exception e) {
									                                             e.printStackTrace();
									                                             LogUtil.d(TAG, "更新本地数据状态失败");
								                                             }
								                                             tvBusiness.setText("问医生");
								                                             btnBusiness.setOnClickListener(new View.OnClickListener() {
									                                             @Override
									                                             public void onClick(View v) {
										                                             Intent intent = new Intent(getApplicationContext(), AskDoctorActivity.class);
										                                             intent.putExtra(Constants.INTENT_ID, data.getId());
										                                             if (LocalRecordPlayActivity.this.record != null) {
											                                             if (LocalRecordPlayActivity.this.record.getPurposeString() != null) {
												                                             intent.putExtra(Constants.INTENT_FEELING, record.getPurposeString());
											                                             }
											                                             if (LocalRecordPlayActivity.this.record.getFeelingString() != null) {
												                                             intent.putExtra(Constants.INTENT_PURPOSE, record.getFeelingString());
											                                             }
										                                             }
										                                             startActivity(intent);
									                                             }
								                                             });
								                                             final MonitorDialog monitorDialog = new MonitorDialog(LocalRecordPlayActivity.this, new String[]{"上传完成,问问医生吗", "去问医生", "暂不询问"});
								                                             monitorDialog.setOperationAction(new MonitorDialog.OperationAction() {
									                                             @Override
									                                             public void left(Object... obj) {
										                                             monitorDialog.dismiss();
										                                             btnBusiness.performClick();
									                                             }

									                                             @Override
									                                             public void right(Object... obj) {
										                                             monitorDialog.dismiss();
									                                             }
								                                             });
								                                             monitorDialog.show();
								                                             saveDataToDatabase(data.getId());
							                                             }
						                                             }), getRequestTag());
			}
		});
	}

	private AdviceForm getUploadData(Record record, String key) {
		AdviceForm adviceForm = new AdviceForm();
		adviceForm.setClientId(record.getLocalRecordId());
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
	private void saveDataToDatabase(Long data) {
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getApplicationContext());
		try {
			record.setUploadState(Record.UPLOAD_STATE_CLOUD);
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
			fms = Util.time2Position(data.getFm());
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getApplicationContext(), "获取数据失败");
		}
		// TODO: 15/9/20  临时放置
		customDialog = new CustomDialog();
		dialog = customDialog.createDialog1(this, "正在上传胎音文件...");
	}
}
