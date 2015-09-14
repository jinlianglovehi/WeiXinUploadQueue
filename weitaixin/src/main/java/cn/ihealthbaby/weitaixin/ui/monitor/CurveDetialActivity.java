package cn.ihealthbaby.weitaixin.ui.monitor;

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
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.util.DataStorage;
import cn.ihealthbaby.weitaixin.library.event.MonitorTerminateEvent;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorDetialView;
import de.greenrobot.event.EventBus;

public class CurveDetialActivity extends BaseActivity {
	private final static String TAG = "CurveDetialActivity";
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
	private long consumedtime;
	private long duration;
	private long interval;
	private int width;
	private boolean needReset = true;
	private ExpendableCountDownTimer countDownTimer;
	private long lastFMTime;
	private boolean terminate;

	@OnClick(value = {R.id.tv_record, R.id.btn_start})
	public void fetalMovement() {
		long consumedTime = countDownTimer.getConsumedTime();
		int position = (int) (consumedTime / countDownTimer.getInterval());
		if (lastFMTime == 0 || consumedTime - lastFMTime >= 3 * 1000) {
			savePosition(position);
			lastFMTime = consumedTime;
		}
	}

	@OnClick(R.id.function)
	public void terminate() {
		EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_MANUAL));
		terminate = true;
//		finish();
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
		configCurve();
		countDownTimer = new ExpendableCountDownTimer(duration, interval) {
			@Override
			public void onStart(long startTime) {
				terminate = false;
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished) {
				if (DataStorage.fhrs.size() > 0 || !terminate) {
					curve.resetPoints();
					curve.postInvalidate();
					int fhr1 = DataStorage.fhrs.get(DataStorage.fhrs.size() - 1);
					// TODO: 15/9/9   颜色根据数值变化
					bpm.setText(fhr1 + "");
				}
				if (!chs.isTouching()) {
					chs.smoothScrollTo((int) (curve.getCurrentPositionX() - width / 2), 0);
				}
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
		curve.setFhrs(DataStorage.fhrs);
		curve.setCellWidth(Util.dip2px(getApplicationContext(), 10));
		curve.setHearts(DataStorage.hearts);
		curve.setCurveStrokeWidth(Util.dip2px(getApplicationContext(), 2));
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
}
