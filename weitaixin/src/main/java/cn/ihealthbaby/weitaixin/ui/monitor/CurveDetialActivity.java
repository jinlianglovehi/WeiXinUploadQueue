package cn.ihealthbaby.weitaixin.ui.monitor;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorDetialView;

public class CurveDetialActivity extends BaseActivity {
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
	private long consumedtime;
	private long duration;
	private long interval;
	private int width;
	private boolean needReset = true;
	private ExpendableCountDownTimer countDownTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_detial);
		ButterKnife.bind(this);
		titleText.setText("胎心监测");
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		consumedtime = getIntent().getLongExtra("CONSUMEDTIME", 0);
		duration = getIntent().getLongExtra("DURATION", 0);
		interval = getIntent().getLongExtra("INTERVAL", 0);
		configCurve();
		countDownTimer = new ExpendableCountDownTimer(duration, interval) {
			@Override
			public void onStart(long startTime) {
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished) {
//				DataStorage.fhrPackage
//				int position = 0;
//				if (needReset) {
//					curve.resetPoints();
//					needReset = !needReset;
//				} else {
//					curve.addPoint(position);
//				}
				curve.resetPoints();
				curve.postInvalidate();
				int fhr1 = DataStorage.fhrs.get(DataStorage.fhrs.size() - 1);
				bpm.setText(String.valueOf(fhr1));
				if (!chs.isTouching()) {
					chs.smoothScrollTo((int) (curve.getCurrentPositionX() - width / 2), 0);
				}
			}

			@Override
			public void onFinish() {
			}
		};
		countDownTimer.startAt(consumedtime);
	}

	private void configCurve() {
		curve.setFhrs(DataStorage.fhrs);
		curve.setCellWidth(Util.dip2px(getApplicationContext(), 10));
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
