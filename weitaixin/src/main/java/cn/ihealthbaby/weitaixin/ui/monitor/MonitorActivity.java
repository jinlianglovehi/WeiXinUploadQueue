package cn.ihealthbaby.weitaixin.ui.monitor;

import android.content.Intent;
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
import cn.ihealthbaby.weitaixin.library.data.bluetooth.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorSimpleView;
import cn.ihealthbaby.weitaixin.ui.widget.RoundMaskView;

public class MonitorActivity extends BaseActivity {
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
	@Bind(R.id.rl_movement)
	RelativeLayout rlMovement;
	@Bind(R.id.hs)
	CurveHorizontalScrollView hs;
	@Bind(R.id.round_frontground)
	ImageView roundFrontground;
	@Bind(R.id.curve_simple)
	CurveMonitorSimpleView curveSimple;
	private ExpendableCountDownTimer countDownTimer;
	private int lastFMPosition;
	private int width;

	@OnClick(R.id.back)
	void back() {
		finish();
	}

	@OnClick(R.id.curve_simple)
	public void curveDetial() {
		Intent intent = new Intent(getApplicationContext(), CurveDetialActivity.class);
		long consumedTime = countDownTimer.getConsumedTime();
		long duration = countDownTimer.getDuration();
		long interval = countDownTimer.getInterval();
		intent.putExtra("CONSUMEDTIME", consumedTime);
		intent.putExtra("DURATION", duration);
		intent.putExtra("INTERVAL", interval);
		startActivity(intent);
	}

	@OnClick(R.id.rl_movement)
	public void fetalMovement() {
//		int position = (int) (countDownTimer.getConsumedTime() / countDownTimer.getInterval());
		int position = curveSimple.getFhrs().size();
		if (lastFMPosition == 0) {
			lastFMPosition = position;
		} else if (lastFMPosition - position > 5 * 60 / countDownTimer.getInterval()) {
			savePosition(position);
		}
	}

	private void savePosition(int position) {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		ButterKnife.bind(this);
		back.setVisibility(View.VISIBLE);
		titleText.setText("胎心监测");
		//
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		//
		roundProgressMask.setBackgroundResource(R.drawable.round_background_3);
		configCurveSimple();
		long duration = 20 * 60 * 1000;
		final long interval = 500;
		countDownTimer = new ExpendableCountDownTimer(duration, interval) {
			private int fhr;
			private long lasttime;

			@Override
			public void onStart(long startTime) {
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished) {
				roundProgressMask.setAngel((float) (360 * getConsumedTime() / getDuration()));
				roundProgressMask.postInvalidate();
				if (lasttime < DataStorage.fhrPackage.getTime()) {
					fhr = DataStorage.fhrPackage.getFHR1();
					lasttime = DataStorage.fhrPackage.getTime();
				} else {
					fhr = 0;
				}
				curveSimple.addPoint(fhr);
				curveSimple.postInvalidate();
				tvBluetooth.setText(String.valueOf(fhr));
				if (!hs.isTouching()) {
					hs.smoothScrollTo((int) (curveSimple.getCurrentPositionX() - width / 2), 0);
				}
			}

			@Override
			public void onFinish() {
				String parsed = ResultHandler.listToArrayString(DataStorage.fhrs);
			}
		};
//		DataStorage.fhrPackages.clear();
		countDownTimer.start();
	}

	private void configCurveSimple() {
//		curveSimple.setCellWidth(Util.dip2px(getApplicationContext(), 4));
		curveSimple.setCellWidth(Util.dip2px(getApplicationContext(), 8));
		curveSimple.setyMax(210);
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
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
	}
}
