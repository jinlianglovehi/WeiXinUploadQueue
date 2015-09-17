package cn.ihealthbaby.weitaixin.ui.monitor;

import android.app.Dialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.data.model.data.Data;
import cn.ihealthbaby.weitaixin.library.data.model.data.RecordData;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.AsynUploadEngine;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorDetialView;

public class CurvePlayActivity extends BaseActivity {
	private final static String TAG = "CurvePlayActivity";
	public String path;
	public Record record;
	@Bind(R.id.curve_play)
	CurveMonitorDetialView curvePlay;
	@Bind(R.id.chs)
	CurveHorizontalScrollView chs;
	@Bind(R.id.play)
	ImageView play;
	@Bind(R.id.replay)
	ImageView replay;
	@Bind(R.id.tv_record)
	TextView tvRecord;
	@Bind(R.id.btn_start)
	ImageView btnStart;
	@Bind(R.id.rl_function)
	RelativeLayout rlFunction;
	@Bind(R.id.back)
	RelativeLayout back;
	@Bind(R.id.title_text)
	TextView titleText;
	@Bind(R.id.function)
	TextView function;
	@Bind(R.id.bpm)
	TextView bpm;
	@Bind(R.id.red_heart)
	ImageView redHeart;
	private int width;
	private ExpendableCountDownTimer countDownTimer;
	private Data data;
	private List<Integer> fhrs;
	private Dialog dialog;
	private List<Integer> fetalMove;
	private MediaPlayer mediaPlayer;
	private int safemin;
	private int safemax;

	@OnClick(R.id.back)
	public void back() {
		finish();
	}

	@OnClick(R.id.play)
	public void play() {
		countDownTimer.restart();
	}

	@OnClick(R.id.replay)
	public void replay() {
		countDownTimer.restart();
	}

	@OnClick(R.id.btn_start)
	public void upload(View view) {
		AsynUploadEngine asynUploadEngine = new AsynUploadEngine(getApplicationContext());
		String uuid = getIntent().getStringExtra(Constants.INTENT_UUID);
		if (uuid == null) {
			ToastUtil.show(getApplicationContext(), "未获取到本地记录");
			return;
		}
		dialog.show();
		asynUploadEngine.init(new File(FileUtil.getVoiceDir(getApplicationContext()), uuid));
		asynUploadEngine.setOnFinishActivity(new AsynUploadEngine.FinishedToDoWork() {
			@Override
			public void onFinishedWork(String key, ResponseInfo info, JSONObject response) {
				ApiManager.getInstance().adviceApi.uploadData(getUploadData(record), new HttpClientAdapter.Callback<Long>() {
					@Override
					public void call(Result<Long> t) {
						dialog.dismiss();
						if (t.isSuccess()) {
							ToastUtil.show(getApplicationContext(), "上传成功");
							Long data = t.getData();
							saveDataToDatabase(data);
						} else {
							ToastUtil.show(getApplicationContext(), t.getMsg());
						}
					}
				}, getRequestTag());
			}
		});
	}

	private AdviceForm getUploadData(Record record) {
		AdviceForm adviceForm = new AdviceForm();
		adviceForm.setClientId(record.getLocalRecordId());
		adviceForm.setDataType(1);
		adviceForm.setDeviceType(1);
		adviceForm.setFeeling(record.getFeeling() + "");
		adviceForm.setAskPurpose(record.getPurpose() + "");
		adviceForm.setData(record.getRecordData());
		adviceForm.setTestTime(record.getRecordStartTime());
		adviceForm.setTestTimeLong((int) (record.getDuration() / 1000));
		return adviceForm;
	}

	//记录保存到数据库
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curve_play);
		ButterKnife.bind(this);
		titleText.setText("胎心监测");
		final DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		getData();
		getAdviceSetting();
		configCurve();
		mediaPlayer = new MediaPlayer();
		CustomDialog customDialog = new CustomDialog();
		dialog = customDialog.createDialog1(this, "正在上传胎音文件...");
		countDownTimer = new ExpendableCountDownTimer(fhrs.size() * data.getInterval(), 500) {
			public int fmposition;
			public int position;

			@Override
			public void onStart(long startTime) {
				try {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(path);
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished) {
				int size = fhrs.size();
				if (position < size) {
					int fhr = fhrs.get(position);
					curvePlay.addPoint(fhr);
					if (fmposition < fetalMove.size() && fetalMove.get(fmposition) == position) {
						curvePlay.addRedHeart(position);
						fmposition++;
					}
					curvePlay.postInvalidate();
					if (bpm != null) {
						if (fhr >= safemin && fhr <= safemax) {
							bpm.setTextColor(Color.parseColor("#49DCB8"));
						} else {
							bpm.setTextColor(Color.parseColor("#FE0058"));
						}
						bpm.setText(fhr + "");
					}
					if (!chs.isTouching()) {
						chs.smoothScrollTo((int) (curvePlay.getCurrentPositionX() - width / 2), 0);
					}
				}
				position++;
			}

			@Override
			public void onFinish() {
				ToastUtil.show(getApplicationContext(), "播放结束");
				mediaPlayer.stop();
				mediaPlayer.reset();
			}

			@Override
			public void onRestart() {
				position = 0;
				fmposition = 0;
				curvePlay.reset();
				chs.smoothScrollTo(0, 0);
			}
		};
	}

	/**
	 * 从网络或者本地数据库获取数据
	 */
	private void getData() {
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getApplicationContext());
		try {
			record = recordBusinessDao.queryByLocalRecordId(getIntent().getStringExtra(Constants.INTENT_UUID));
			path = record.getSoundPath();
			String rData = record.getRecordData();
			Gson gson = new Gson();
			RecordData recordData = gson.fromJson(rData, RecordData.class);
			data = recordData.getData();
			fhrs = data.getHeartRate();
			fetalMove = Util.time2Position(data.getFm());
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getApplicationContext(), "获取数据失败");
		}
	}

	private void configCurve() {
		// TODO: 15/9/9  设置数据源
		curvePlay.setxMax(20 * 60);
		curvePlay.setCellWidth(Util.dip2px(getApplicationContext(), 10));
		curvePlay.setCurveStrokeWidth(Util.dip2px(getApplicationContext(), 2));
		ViewGroup.LayoutParams layoutParams = curvePlay.getLayoutParams();
		layoutParams.width = curvePlay.getMinWidth();
		layoutParams.height = curvePlay.getMinHeight() + Util.dip2px(getApplicationContext(), 16);
		curvePlay.setLayoutParams(layoutParams);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
	}

	/**
	 * 获取监测的配置  AdviceSetting [autoBeginAdvice=20,autoAdviceTimeLong=20,fetalMoveTime=5,autoBeginAdviceMax=3,askMinTime=20,alarmHeartrateLimit=100-160,hospitalId=3,
	 * ]
	 */
	private void getAdviceSetting() {
		LocalSetting localSetting = SPUtil.getLocalSetting(getApplicationContext());
		AdviceSetting adviceSetting = SPUtil.getAdviceSetting(getApplicationContext());
		String alarmHeartrateLimit = adviceSetting.getAlarmHeartrateLimit();
		String[] split = alarmHeartrateLimit.split("-");
		try {
			if (split != null && split.length == 2) {
				safemin = Integer.parseInt(split[0]);
				safemax = Integer.parseInt(split[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getApplicationContext(), "解析错误");
		}
		LogUtil.d(TAG, "safemin:%s,safemax:%s", safemin, safemax);
	}
}
