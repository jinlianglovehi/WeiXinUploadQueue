package cn.ihealthbaby.weitaixin.ui.monitor;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.event.FetalMovementEvent;
import cn.ihealthbaby.weitaixin.library.event.MonitorTerminateEvent;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.FixedRateCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorDetialView;
import de.greenrobot.event.EventBus;

public class MonitorDetialActivity extends BaseActivity {
	private final static String TAG = "MonitorDetialActivity";
	@Bind(R.id.chs)
	CurveHorizontalScrollView chs;
	@Bind(R.id.bpm)
	TextView bpm;
	@Bind(R.id.red_heart)
	ImageView redHeart;
	@Bind(R.id.back)
	RelativeLayout back;
	@Bind(R.id.title_text)
	TextView titleText;
	@Bind(R.id.function)
	TextView function;
	@Bind(R.id.curve)
	CurveMonitorDetialView curve;
	@Bind(R.id.tv_record)
	TextView tvRecord;
	@Bind(R.id.btn_start)
	ImageView btnStart;
	@Bind(R.id.rl_function)
	RelativeLayout rlMovement;
	@Bind(R.id.tv_consum_time)
	TextView tvConsumTime;
	@Bind(R.id.ivDelectAction)
	ImageView ivDelectAction;
	@Bind(R.id.tvDelectAction)
	TextView tvDelectAction;
	@Bind(R.id.flDelAction)
	FrameLayout flDelAction;
	@Bind(R.id.tv_start_time)
	TextView tvStartTime;
	private long consumedtime;
	private long duration;
	private long interval;
	private int width;
	private boolean needReset = true;
	private FixedRateCountDownTimer countDownTimer;
	private long lastFMTime;
	private boolean terminate;
	private int safemin;
	private int safemax;

	@OnClick(value = {R.id.tv_record, R.id.btn_start})
	public void fetalMovement() {
//		long consumedTime = countDownTimer.getConsumedTime();
//		int position = (int) (consumedTime / countDownTimer.getInterval());
//		if (lastFMTime == 0 || consumedTime - lastFMTime >= 3 * 1000) {
//			savePosition(position);
//			lastFMTime = consumedTime;
//		}
		EventBus.getDefault().post(new FetalMovementEvent());
	}

	@OnClick(R.id.function)
	public void terminate() {
		EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_MANUAL));
		terminate = true;
		finish();
	}

	private void savePosition(int position) {
		curve.getHearts().add(position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_detial);
		ButterKnife.bind(this);
		titleText.setText("胎心监测");
		function.setText("立即结束");
		function.setVisibility(View.VISIBLE);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		consumedtime = getIntent().getLongExtra(Constants.INTENT_CONSUMED_TIME, 0);
		duration = getIntent().getLongExtra(Constants.INTENT_DURATION, 0);
		interval = getIntent().getLongExtra(Constants.INTENT_INTERVAL, 0);
		getAdviceSetting();
		configCurve();
		countDownTimer = new FixedRateCountDownTimer(duration, 1000) {
			public boolean reset;
			public long lastStart;

			@Override
			public void onStart(long startTime) {
				terminate = false;
				tvStartTime.setText("开始时间 " + DateTimeTool.million2hhmmss(System.currentTimeMillis()));
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished,FHRPackage fhrPackage) {
				long start = System.currentTimeMillis();
				if (DataStorage.fhrs.size() > 0 || !terminate) {
					curve.resetPoints();
					curve.postInvalidate();
					tvConsumTime.setText(DateTimeTool.million2mmss(getConsumedTime()));
					int fhr1 = DataStorage.fhrs.get(DataStorage.fhrs.size() - 1);
					if (fhr1 >= safemin && fhr1 <= safemax) {
						bpm.setTextColor(Color.parseColor("#49DCB8"));
					} else {
						bpm.setTextColor(Color.parseColor("#FE0058"));
					}
					bpm.setText(fhr1 + "");
				}
				if (!chs.isTouching()) {
					chs.smoothScrollTo((int) (curve.getCurrentPositionX() - width / 2), 0);
				}
				long stop = System.currentTimeMillis();
				LogUtil.d("MonitorDetialActivity", "duration:[%s] , interval:[%s] ,consumedTime:[%s]s ,listSize:[%s]", (stop - start), start - lastStart, getConsumedTime() / 1000, curve.getFhrs().size());
				lastStart = start;
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onRestart() {
			}
		};
		countDownTimer.startAt(consumedtime);
	}

	private void configCurve() {
		ViewGroup.LayoutParams hsLayoutParams = chs.getLayoutParams();
		hsLayoutParams.width = width;
		chs.setLayoutParams(hsLayoutParams);
		curve.setFhrs(DataStorage.fhrs);
		curve.setCellWidth(Util.dip2px(getApplicationContext(), 10));
		curve.setHearts(DataStorage.fms);
		curve.setCurveStrokeWidth(Util.dip2px(getApplicationContext(), 2));
		curve.setSafeMax(safemax);
		curve.setSafeMin(safemin);
		ViewGroup.LayoutParams layoutParams = curve.getLayoutParams();
		layoutParams.width = curve.getMinWidth();
		layoutParams.height = curve.getMinHeight() + Util.dip2px(getApplicationContext(), 16);
		curve.setLayoutParams(layoutParams);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (countDownTimer != null) {
			countDownTimer.cancel();
			countDownTimer = null;
		}
	}

	/**
	 * 获取监测的配置
	 */
	private void getAdviceSetting() {
		LocalSetting localSetting = SPUtil.getLocalSetting(getApplicationContext());
		AdviceSetting adviceSetting = SPUtil.getAdviceSetting(getApplicationContext());
		String alarmHeartrateLimit = adviceSetting.getAlarmHeartrateLimit();
		String[] split = alarmHeartrateLimit.split("-");
		try {
			if (split != null && split.length == 2) {
				int safemin = Integer.parseInt(split[0]);
				if (safemin > 0) {
					this.safemin = safemin;
				}
				int safemax = Integer.parseInt(split[1]);
				if (safemax > 0) {
					this.safemax = safemax;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtil.d(TAG, "safemin:%s,safemax:%s", safemin, safemax);
	}
}
