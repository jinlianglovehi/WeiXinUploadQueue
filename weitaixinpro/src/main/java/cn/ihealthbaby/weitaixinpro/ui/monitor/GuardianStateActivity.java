package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.model.AskPurposeType;
import cn.ihealthbaby.client.model.CommonConfig;
import cn.ihealthbaby.client.model.FeelingType;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.tools.CustomDialog;

public class GuardianStateActivity extends BaseActivity {
	private final static String TAG = "GuardianStateActivity";
	public List<AskPurposeType> askPurposetypes;
	public List<FeelingType> feelingTypes;
	@Bind(R.id.back)
	RelativeLayout back;
	@Bind(R.id.title_text)
	TextView title_text;
	@Bind(R.id.function)
	TextView function;
	//
	@Bind(R.id.flGuardianPurpose)
	FrameLayout flGuardianPurpose;
	@Bind(R.id.flGuardianMood)
	FrameLayout flGuardianMood;
	@Bind(R.id.ivFooter)
	TextView ivFooter;
	@Bind(R.id.tvGuardianPurposeText)
	TextView tvGuardianPurposeText;
	@Bind(R.id.tvGuardianMoodText)
	TextView tvGuardianMoodText;
	private MyPoPoWinGuardian myPoPoWinGuardian;
	private MyPoPoWinGuardian myPoPoWinGuardian1;
	private String purposeText;
	private String moodText;
	private int guardianPurposeIndexPosition = 0;
	private int guardianMoodIndexPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guardian_state);
		ButterKnife.bind(this);
		title_text.setText("监护状态");
		back.setVisibility(View.GONE);
		final CustomDialog customDialog = new CustomDialog();
		Dialog dialog = customDialog.createDialog1(this, "加载中...");
		dialog.show();
		ApiManager.getInstance().commonApi.getCommonConfig(new HttpClientAdapter.Callback<CommonConfig>() {
			@Override
			public void call(Result<CommonConfig> t) {
				if (t.isSuccess()) {
					CommonConfig data = t.getData();
					askPurposetypes = data.getAskPurposetypes();
					feelingTypes = data.getFeelingTypes();
				} else {
					ToastUtil.show(getApplicationContext(), t.getMsgMap() + "");
				}
				customDialog.dismiss();
			}
		}, getRequestTag());
	}

	@OnClick(R.id.flGuardianPurpose)
	public void GuardianPurpose(FrameLayout flGuardianPurpose) {
		if (askPurposetypes == null) {
			ToastUtil.show(getApplicationContext(), "没获取到数据");
			return;
		}
		myPoPoWinGuardian = new MyPoPoWinGuardian(this);
		myPoPoWinGuardian.initPurposetData(askPurposetypes, guardianPurposeIndexPosition);
		myPoPoWinGuardian.showAtLocation(flGuardianPurpose);
		myPoPoWinGuardian.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
//				if (myPoPoWinGuardian.getIndexPosition() == -1) {
//					purposeText = null;
//				} else {
				guardianPurposeIndexPosition = myPoPoWinGuardian.getGuardianPurposeIndexPosition();
				purposeText = askPurposetypes.get(myPoPoWinGuardian.getGuardianPurposeIndexPosition()).getValue();
				tvGuardianPurposeText.setText(purposeText + "");
//				}
			}
		});
	}

	@OnClick(R.id.flGuardianMood)
	public void GuardianMood(FrameLayout flGuardianMood) {
		if (feelingTypes == null) {
			ToastUtil.show(getApplicationContext(), "没获取到数据");
			return;
		}
		myPoPoWinGuardian1 = new MyPoPoWinGuardian(this);
		myPoPoWinGuardian1.initFeelingTypeData(feelingTypes, guardianMoodIndexPosition);
		myPoPoWinGuardian1.showAtLocation(flGuardianMood);
		myPoPoWinGuardian1.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
//				if (myPoPoWinGuardian1.getIndexPosition() == -1) {
//					moodText = null;
//				} else {
				guardianMoodIndexPosition = myPoPoWinGuardian1.getGuardianMoodIndexPosition();
				moodText = feelingTypes.get(myPoPoWinGuardian1.getGuardianMoodIndexPosition()).getValue();
				tvGuardianMoodText.setText(moodText + "");
//				}
			}
		});
	}

	@OnClick(R.id.ivFooter)
	public void Footer() {
		if (TextUtils.isEmpty(purposeText) && TextUtils.isEmpty(moodText)) {
			ToastUtil.show(getApplicationContext(), "请选择监护心情和监护目的");
			return;
		}
		if (TextUtils.isEmpty(purposeText)) {
			ToastUtil.show(getApplicationContext(), "请选择监护目的");
			return;
		}
		if (TextUtils.isEmpty(moodText)) {
			ToastUtil.show(getApplicationContext(), "请选择监护心情");
			return;
		}
		Intent intent = getIntent();
		intent.setClass(getApplicationContext(), LocalRecordPlayActivity.class);
		int purposeId = askPurposetypes.get(myPoPoWinGuardian.getGuardianPurposeIndexPosition()).getId();
		String purposeString = askPurposetypes.get(myPoPoWinGuardian.getGuardianPurposeIndexPosition()).getValue();
		int feelingId = feelingTypes.get(myPoPoWinGuardian1.getGuardianMoodIndexPosition()).getId();
		String feelingString = feelingTypes.get(myPoPoWinGuardian1.getGuardianMoodIndexPosition()).getValue();
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getApplicationContext());
		try {
			String localRecordId = getIntent().getStringExtra(Constants.INTENT_LOCAL_RECORD_ID);
			LogUtil.d(TAG, "localRecordId:%s", localRecordId);
			Record query = recordBusinessDao.queryByLocalRecordId(localRecordId);
			LogUtil.d(TAG, query.toString());
			query.setFeelingId(feelingId);
			query.setFeelingString(feelingString);
			query.setPurposeString(purposeString);
			query.setPurposeId(purposeId);
			recordBusinessDao.update(query);
			Record query1 = recordBusinessDao.query(query);
			LogUtil.d(TAG, query1.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		ToastUtil.show(getApplicationContext(), "请选择监护心情和监护目的");
	}
}








