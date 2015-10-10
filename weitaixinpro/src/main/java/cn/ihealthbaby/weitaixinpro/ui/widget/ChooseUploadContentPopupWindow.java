package cn.ihealthbaby.weitaixinpro.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.service.UploadEvent;
import cn.ihealthbaby.weitaixinpro.service.UploadService;
import cn.ihealthbaby.weitaixinpro.tools.CustomDialog;
import de.greenrobot.event.EventBus;

public class ChooseUploadContentPopupWindow extends PopupWindow {
	private final static String TAG = "ChooseUploadContentPopupWindow";
	private final static int UPLOADTYPE_DATA = 0;
	private final static int UPLOADTYPE_ALL = 1;
	private final Context context;
	private Record record;
	private int position;
	private Dialog dialog;
	private String key;

	public ChooseUploadContentPopupWindow(Context context, Record record, int position) {
		this.context = context;
		this.record = record;
		this.position = position;
		EventBus.getDefault().register(this);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View popupWindows = inflater.inflate(R.layout.popupwindow_choose_upload_content, null);
		setContentView(popupWindows);
		setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
		setAnimationStyle(R.style.anim_popowin_dir);
		setTouchable(true);
		setOutsideTouchable(true);
		setFocusable(true);
		View uploadAll = popupWindows.findViewById(R.id.upload_all);
		final View uploadData = popupWindows.findViewById(R.id.upload_data);
		View cancel = popupWindows.findViewById(R.id.cancel);
		//上传所有数据
		uploadAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				upload();
			}
		});
		//上传曲线
		uploadData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadData();
			}
		});
		//取消
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		//消失时的处理
		setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				EventBus.getDefault().unregister(ChooseUploadContentPopupWindow.this);
			}
		});
	}

	protected void upload() {
		Intent uploadService = new Intent(context, UploadService.class);
		uploadService.putExtra(Constants.INTENT_USER_ID, record.getUserId());
		uploadService.putExtra(Constants.INTENT_LOCAL_RECORD_ID, record.getLocalRecordId());
		CustomDialog customDialog = new CustomDialog();
		dialog = customDialog.createDialog1(context, "正在上传胎音文件...");
		dialog.show();
		context.startService(uploadService);
	}

	public void onEventMainThread(UploadEvent event) {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		LogUtil.d(TAG, event.toString());
		switch (event.getResult()) {
			case UploadEvent.RESULT_SUCCESS:
//				ApiManager.getInstance().hClientAccountApi.uploadData(UPLOADTYPE_ALL, getUploadData(record, event.getKey()), new DefaultCallback<AdviceItem>(context, new AbstractBusiness<AdviceItem>() {
//					@Override
//					public void handleData(AdviceItem data) {
//						ToastUtil.show(context, "全部上传成功");
//						updateUloadState(Record.UPLOAD_STATE_CLOUD);
//						EventBus.getDefault().post(new UploadedEvent(position));
//					}
//				}), ChooseUploadContentPopupWindow.this);
				break;
			case UploadEvent.RESULT_FAIL:
				ToastUtil.show(context, "上传失败");
				updateUloadState(Record.UPLOAD_STATE_LOCAL);
				break;
			default:
				break;
		}
	}

	/**
	 * 记录保存到数据库
	 */
	private void updateUloadState(int result) {
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(context);
		try {
			record.setUploadState(result);
			recordBusinessDao.update(record);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	protected void uploadData() {
//		ApiManager.getInstance().hClientAccountApi.uploadData(UPLOADTYPE_DATA, getUploadData(record, null), new DefaultCallback<AdviceItem>(context, new AbstractBusiness<AdviceItem>() {
//			@Override
//			public void handleData(AdviceItem data) {
//				ToastUtil.show(context, "上传曲线成功");
//				updateUloadState(Record.UPLOAD_STATE_CLOUD);
//			}
//		}), ChooseUploadContentPopupWindow.this);
	}
}


