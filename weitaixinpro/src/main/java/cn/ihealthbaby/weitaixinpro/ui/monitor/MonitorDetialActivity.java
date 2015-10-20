package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
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
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.event.MonitorTerminateEvent;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.FixedRateCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixinpro.ui.widget.CurveMonitorDetialView;
import cn.ihealthbaby.weitaixinpro.ui.widget.MonitorDialog;
import de.greenrobot.event.EventBus;

public class MonitorDetialActivity extends BaseActivity {
	public int askMinTime;
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
	@Bind(R.id.tv_start_time)
	TextView tvStartTime;
	@Bind(R.id.btn_extra_time)
	ImageView btnExtraTime;
	@Bind(R.id.tv_sum_time)
	TextView tvSumTime;
	@Bind(R.id.btn_doctor_interrupt)
	ImageView btnDoctorInterrupt;
	@Bind(R.id.tv_doctor_interrupt)
	TextView tvDoctorInterrupt;
	@Bind(R.id.vertical_line)
	ImageView verticalLine;
	private long consumedtime;
	private long duration;
	private long interval;
	private int width;
	private boolean needReset = true;
	private FixedRateCountDownTimer countDownTimer;
	private long lastFMTime;
	private int safemin = 110;
	private int safemax = 160;
	private int limitMax = 200;
	private int limitMin = 60;
	private boolean alert;
	private int alertInterval;
	private long lastAlert;
	private SoundPool alertSound;

	@OnClick(R.id.back)
	public void back() {
		final MonitorDialog monitorDialog = new MonitorDialog(this, new String[]{"希望取消监测么", "继续监测", "取消监测"});
		monitorDialog.setOperationAction(new MonitorDialog.OperationAction() {
			@Override
			public void left(Object... obj) {
				monitorDialog.dismiss();
			}

			@Override
			public void right(Object... obj) {
				monitorDialog.dismiss();
				EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_MANUAL_CANCEL_STARTED));
				finish();
			}
		});
		monitorDialog.show();
	}

	@OnClick(value = {R.id.tv_record, R.id.btn_start})
	public void fetalMovement() {
		long consumedTime = countDownTimer.getConsumedTime();
		int position = (int) (consumedTime / countDownTimer.getInterval());
		if (lastFMTime == 0 || consumedTime - lastFMTime >= 3 * 1000) {
			saveFetalMovementPosition(position);
			lastFMTime = consumedTime;
		}
	}

	@OnClick(R.id.btn_extra_time)
	public void extra() {
		countDownTimer.extra(5 * 60 * 1000);
	}

	@OnClick(R.id.btn_doctor_interrupt)
	public void doctor() {
		long consumedTime = countDownTimer.getConsumedTime();
		int position = (int) (consumedTime / countDownTimer.getInterval());
		saveDoctorPosition(position);
	}

	private void saveDoctorPosition(int position) {
		curve.getDoctors().add(position);
	}

	@OnClick(R.id.function)
	public void terminate() {
		final MonitorDialog monitorDialog;
		if (countDownTimer.getConsumedTime() / 1000 < askMinTime * 60) {
			monitorDialog = new MonitorDialog(this, new String[]{"满" + askMinTime + "分钟的监测才是有效数据\n是否继续监测", "继续监测", "立即完成"});
		} else {
			monitorDialog = new MonitorDialog(this, new String[]{"是否立即完成", "继续监测", "立即完成"});
		}
		monitorDialog.setOperationAction(new MonitorDialog.OperationAction() {
			@Override
			public void left(Object... obj) {
				monitorDialog.dismiss();
			}

			@Override
			public void right(Object... obj) {
				monitorDialog.dismiss();
				EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_MANUAL));
				finish();
			}
		});
		monitorDialog.show();
	}

	private void saveFetalMovementPosition(int position) {
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
		getAdviceSetting();
		configCurve();
		alertSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		alertSound.load(getApplicationContext(), R.raw.didi, 1);
		countDownTimer = new FixedRateCountDownTimer(duration, 500) {
			public RelativeLayout.LayoutParams layoutParams;
			public long lastTime;

			@Override
			public void onStart(long startTime) {
				tvStartTime.setText("开始时间 " + DateTimeTool.million2hhmmss(System.currentTimeMillis()));
				tvSumTime.setText("共" + getDuration() / 1000 / 60 + "分钟");
				DataStorage.fhrs.clear();
				DataStorage.fms.clear();
				DataStorage.doctors.clear();
				layoutParams = (RelativeLayout.LayoutParams) verticalLine.getLayoutParams();
			}

			@Override
			public void onExtra(long newDuration, long extraTime, long stopTime) {
				tvSumTime.setText("共" + newDuration / 1000 / 60 + "分钟");
				curve.setxMax(((int) (newDuration / 1000)));
			}

			@Override
			public void onTick(long millisUntilFinished, FHRPackage fhrPackage) {
				tick(fhrPackage);
			}

			@Override
			public void onFinish() {
				function.performClick();
			}

			@Override
			public void onRestart() {
			}

			private void tick(FHRPackage fhrPackage) {
				//获取当前心率值
				int fhr = fhrPackage.getFHR1();
				if (alert) {
					long currentTimeMillis = System.currentTimeMillis();
					if (currentTimeMillis - lastAlert >= alertInterval * 1000)
						alertSound.play(1, 1, 1, 0, 0, 1);
					lastAlert = currentTimeMillis;
				}
				long time = fhrPackage.getTime();
				//防止重复
				if (lastTime == time) {
					fhr = 0;
				}
				lastTime = time;
				//如果越界,则值设置为0
				if ((fhr > limitMax || fhr < limitMin)) {
					fhr = 0;
				}
				if (fhr >= safemin && fhr <= safemax) {
					bpm.setTextColor(Color.parseColor("#49DCB8"));
				} else {
					bpm.setTextColor(Color.parseColor("#FE0058"));
				}
				bpm.setText(fhr + "");
				//加入到图中
				curve.addPoint(fhr);
				curve.postInvalidate();
				tvConsumTime.setText("已记录" + DateTimeTool.million2mmss(getConsumedTime()));
				int size = curve.getHearts().size();
				final float currentPositionX = curve.getCurrentPositionX();
				final float diff = currentPositionX - width / 2;
				LogUtil.d(TAG, "currentPositionX:[%s], diff:[%s]", currentPositionX, diff);
				if (diff <= 0) {
					layoutParams.setMargins((int) currentPositionX, 0, 0, 0);
				} else {
					layoutParams.setMargins(width / 2, 0, 0, 0);
				}
				if (!chs.isTouching()) {
					chs.smoothScrollTo((int) diff, 0);
				}
			}
		};
		countDownTimer.start();
	}

	private void configCurve() {
		ViewGroup.LayoutParams hsLayoutParams = chs.getLayoutParams();
		hsLayoutParams.width = width;
		chs.setLayoutParams(hsLayoutParams);
		curve.setFhrs(DataStorage.fhrs);
		curve.setxMax((int) (duration / 1000));
		curve.setCellWidth(Util.dip2px(getApplicationContext(), 10));
		curve.setHearts(DataStorage.fms);
		curve.setDoctors(DataStorage.doctors);
		curve.setCurveStrokeWidth(2);
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
	 * 获取监测的配置  AdviceSetting [autoBeginAdvice=20,autoAdviceTimeLong=20,fetalMoveTime=5,autoBeginAdviceMax=3,askMinTime=20,alarmHeartrateLimit=100-160,hospitalId=3,
	 * ]
	 */
	private void getAdviceSetting() {
		LocalSetting localSetting = SPUtil.getLocalSetting(getApplicationContext());
		AdviceSetting adviceSetting = SPUtil.getAdviceSetting(getApplicationContext());
		askMinTime = adviceSetting.getAskMinTime();
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
		int autoAdviceTimeLong = adviceSetting.getAutoAdviceTimeLong();
		if (autoAdviceTimeLong > 0) {
			duration = autoAdviceTimeLong * 60 * 1000;
		}
		alert = localSetting.isAlert();
		alertInterval = localSetting.getAlertInterval();
		LogUtil.d(TAG, "safemin:%s,safemax:%s,alertSound:%s,alertInterval:%s,duration:%s", safemin, safemax, alert, alertInterval, duration);
	}

	@Override
	public void onBackPressed() {
		back.performClick();
	}
}
