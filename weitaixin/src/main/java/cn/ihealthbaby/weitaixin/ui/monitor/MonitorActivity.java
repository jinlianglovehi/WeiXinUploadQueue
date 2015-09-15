package cn.ihealthbaby.weitaixin.ui.monitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.event.MonitorTerminateEvent;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorSimpleView;
import cn.ihealthbaby.weitaixin.ui.widget.RoundMaskView;
import de.greenrobot.event.EventBus;

public class MonitorActivity extends BaseActivity {
	private final static String TAG = "MonitorActivity";
	public int alertInterval;
	public boolean alert;
	public int duration = 20 * 60 * 1000;
	@Bind(R.id.back)
	RelativeLayout back;
	@Bind(R.id.title_text)
	TextView titleText;
	@Bind(R.id.function)
	TextView function;
	@Bind(R.id.round_progress_mask)
	RoundMaskView roundProgressMask;
	@Bind(R.id.tv_bluetooth)
	TextView tvBluetooth;
	@Bind(R.id.bpm)
	ImageView bpm;
	@Bind(R.id.hint)
	TextView hint;
	@Bind(R.id.rl_round)
	RelativeLayout rlRound;
	@Bind(R.id.tv_record)
	TextView tvRecord;
	@Bind(R.id.btn_start)
	ImageView btnStart;
	@Bind(R.id.rl_function)
	RelativeLayout rlMovement;
	@Bind(R.id.hs)
	CurveHorizontalScrollView hs;
	@Bind(R.id.round_frontground)
	ImageView roundFrontground;
	@Bind(R.id.curve_simple)
	CurveMonitorSimpleView curveSimple;
	@Bind(R.id.tv_start_time)
	TextView tvStartTime;
	@Bind(R.id.fm_count)
	TextView fmCount;
	private ExpendableCountDownTimer countDownTimer;
	private long lastFetalMoveTime;
	private int width;
	private int safemin = 110;
	private int safemax = 160;

	@OnClick(R.id.back)
	public void back() {
		finish();
	}

	@OnClick(R.id.function)
	public void terminate() {
		EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_MANUAL));
	}

	@OnClick(R.id.curve_simple)
	public void curveDetial() {
		Intent intent = new Intent(getApplicationContext(), CurveDetialActivity.class);
		long consumedTime = countDownTimer.getConsumedTime();
		long duration = countDownTimer.getDuration();
		long interval = countDownTimer.getInterval();
		intent.putExtra(Constants.INTENT_CONSUMED_TIME, consumedTime);
		intent.putExtra(Constants.INTENT_DURATION, duration);
		intent.putExtra(Constants.INTENT_INTERVAL, interval);
		startActivity(intent);
	}

	@OnClick(value = {R.id.tv_record, R.id.btn_start})
	public void fetalMovement() {
		long consumedTime = countDownTimer.getConsumedTime();
		int position = (int) (consumedTime / countDownTimer.getInterval());
		if (lastFetalMoveTime == 0 || consumedTime - lastFetalMoveTime >= 3 * 1000) {
			savePosition(position);
			lastFetalMoveTime = consumedTime;
		}
	}

	private void savePosition(int position) {
		curveSimple.getHearts().add(position);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		ButterKnife.bind(this);
		EventBus.getDefault().register(this);
		back.setVisibility(View.VISIBLE);
		titleText.setText("胎心监测");
		function.setText("立即结束");
		function.setVisibility(View.VISIBLE);
		//
		//
		getAdviceSetting();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		//
		roundProgressMask.setBackgroundResource(R.drawable.round_background_3);
		configCurveSimple();
		final long interval = 500;
		countDownTimer = new ExpendableCountDownTimer(duration, interval) {
			private int fhr;
			private long lasttime;

			@Override
			public void onStart(long startTime) {
				tvStartTime.setText(DateTimeTool.second2hhmmss(startTime) + "开始记录");
				hint.setText("已记录" + DateTimeTool.second2mmss(getConsumedTime()));
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished) {
				tick();
			}

			@Override
			public void onFinish() {
				tick();
				//
				EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_AUTO));
				//
			}

			@Override
			public void onRestart() {
			}

			private void tick() {
				roundProgressMask.setAngel((float) (360 * getConsumedTime() / getDuration()));
				roundProgressMask.postInvalidate();
				long time = DataStorage.fhrPackage.getTime();
				hint.setText("已记录" + DateTimeTool.second2mmss(getConsumedTime()));
				if (lasttime < time) {
					fhr = DataStorage.fhrPackage.getFHR1();
					if (tvBluetooth != null) {
						if (fhr >= safemin && fhr <= safemax) {
							tvBluetooth.setTextColor(Color.parseColor("#49DCB8"));
						} else {
							tvBluetooth.setTextColor(Color.parseColor("#FE0058"));
						}
						tvBluetooth.setText(fhr + "");
					}
					lasttime = time;
				} else {
					fhr = 0;
				}
				curveSimple.addPoint(fhr);
				curveSimple.postInvalidate();
				if (!hs.isTouching()) {
					hs.smoothScrollTo((int) (curveSimple.getCurrentPositionX() - width / 2), 0);
				}
			}
		};
		reset();
		countDownTimer.start();
	}

	private void configCurveSimple() {
		ViewGroup.LayoutParams hsLayoutParams = hs.getLayoutParams();
		hsLayoutParams.width = width;
		hs.setLayoutParams(hsLayoutParams);
		curveSimple.setCellWidth(Util.dip2px(getApplicationContext(), 4));
//		curveSimple.setCellWidth(Util.dip2px(getApplicationContext(), 8));
		curveSimple.setyMax(210);
		curveSimple.setSafeMax(safemax);
		curveSimple.setSafeMin(safemin);
		curveSimple.setHearts(DataStorage.hearts);
		//TODO 根据配置设置
//		WeiTaiXinApplication.user.getServiceInfo();
//		curveSimple.setSafeMax();
		curveSimple.setFhrs(DataStorage.fhrs);
		int minWidth = curveSimple.getMinWidth();
		int minHeight = curveSimple.getMinHeight();
		ViewGroup.LayoutParams layoutParams = curveSimple.getLayoutParams();
		layoutParams.width = minWidth;
		layoutParams.height = minHeight;
		curveSimple.setLayoutParams(layoutParams);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ButterKnife.unbind(this);
		EventBus.getDefault().unregister(this);
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
	}

	private void reset() {
		lastFetalMoveTime = 0;
		DataStorage.fhrs.clear();
		DataStorage.hearts.clear();
	}

	public void onEventMainThread(MonitorTerminateEvent event) {
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
		int reason = event.getEvent();
		switch (reason) {
			case MonitorTerminateEvent.EVENT_AUTO:
				LogUtil.d(TAG, "EVENT_AUTO");
				break;
			case MonitorTerminateEvent.EVENT_UNKNOWN:
				LogUtil.d(TAG, "EVENT_UNKNOWN");
				break;
			case MonitorTerminateEvent.EVENT_MANUAL:
				LogUtil.d(TAG, "EVENT_MANUAL");
				break;
		}
		finish();
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
				safemin = Integer.parseInt(split[0]);
				safemax = Integer.parseInt(split[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		int autoAdviceTimeLong = adviceSetting.getAutoAdviceTimeLong();
		if (autoAdviceTimeLong > 0) {
			duration = autoAdviceTimeLong * 60 * 1000;
		}
		LogUtil.d(TAG, "safemin:%s,safemax:%s,alertSound:%s,alertInterval:%s,duration:%s", safemin, safemax, alert, alertInterval, duration);
	}
}
