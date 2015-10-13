package cn.ihealthbaby.weitaixinpro.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.AdviceItem;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.AbstractBusiness;
import cn.ihealthbaby.weitaixinpro.DefaultCallback;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.service.UploadDataUtil;
import de.greenrobot.event.EventBus;

public class ChooseUploadContentPopupWindow extends PopupWindow {
	private final static String TAG = "ChooseUploadContentPopupWindow";
	private final static int UPLOADED_STATE_NONE = 1;
	private final static int UPLOADED_STATE_DATA = 2;
	private final static int UPLOADED_STATE_ALL = 3;
	private static final int BUTTON_ALL = 1;
	private static final int BUTTON_DATA = 2;
	private static final String TONE_FORMAT = "audio/x-wav";
	private static final int TONE_VERSION = 1;
	private final Context context;
	private Record record;
	private int position;
	private Dialog dialog;
	private int uploadedState;

	/**
	 * 点击按钮,先检查资源状态,根据状态提示用户,
	 *
	 * @param context
	 * @param record
	 * @param position
	 */
	public ChooseUploadContentPopupWindow(Context context, Record record, int position) {
		this.context = context;
		this.record = record;
		this.position = position;
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
				checkUploadStatus(BUTTON_ALL);
			}
		});
		//上传曲线
		uploadData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkUploadStatus(BUTTON_DATA);
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

	private void checkUploadStatus(final int button) {
		ApiManager.getInstance().hClientAccountApi.checkUpload(record.getLocalRecordId(), new DefaultCallback<AdviceItem>(context, new AbstractBusiness<AdviceItem>() {
			@Override
			public void handleAllFailure(Context context) {
				super.handleAllFailure(context);
				ToastUtil.show(context, "查询上传状态失败");
			}

			@Override
			public void handleData(AdviceItem adviceItem) {
				UploadDataUtil uploadDataUtil = new UploadDataUtil(context, record);
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
						ToastUtil.show(context, "已上传曲线");
						LogUtil.d(TAG, "已上传过,无胎音");
					} else {
						ToastUtil.show(context, "已上传全部数据");
						LogUtil.d(TAG, "已上传过,有胎音");
					}
				}
			}
		}), this);
	}
}


