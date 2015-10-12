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

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.data.model.data.Data;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorDetialView;

public abstract class RecordPlayActivity extends BaseActivity {
	private final static String TAG = "LocalRecordPlayActivity";
	public String path;
	public Record record;
	public String uuid;
	protected Data data;
	protected List<Integer> fhrs;
	protected List<Integer> fetalMove;
	protected Dialog dialog;
	@Bind(R.id.curve_play)
	CurveMonitorDetialView curvePlay;
	@Bind(R.id.chs)
	CurveHorizontalScrollView chs;
	@Bind(R.id.play)
	ImageView play;
	@Bind(R.id.replay)
	ImageView replay;
	@Bind(R.id.tv_business)
	TextView tvBusiness;
	@Bind(R.id.btn_business)
	ImageView btnBusiness;
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
	@Bind(R.id.tv_start_time)
	TextView tvStartTime;
	@Bind(R.id.tv_consum_time)
	TextView tvConsumTime;
	private int width;
	private ExpendableCountDownTimer countDownTimer;
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

	@OnClick(R.id.btn_business)
	public void function(View view) {
		function();
	}

	protected abstract void function();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_play);
		ButterKnife.bind(this);
		titleText.setText("胎心监测");
		final DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		getData();
		getAdviceSetting();
		configCurve();
		mediaPlayer = new MediaPlayer();
		btnBusiness.setImageResource(R.drawable.button_upload);
		tvBusiness.setText("上传监测图");
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
				tvStartTime.setText("开始时间 " + DateTimeTool.million2hhmmss(record.getRecordStartTime().getTime()));
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
					tvConsumTime.setText(DateTimeTool.million2mmss(getConsumedTime()));
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
	 * 从网络或者本地数据库获取数据 protected Data data; protected List<Integer> fhrs; protected List<Integer>
	 * fetalMove;
	 */
	protected abstract void getData();

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
	 * 获取监测的配置 AdviceSetting [autoBeginAdvice=20,autoAdviceTimeLong=20,fetalMoveTime=5,autoBeginAdviceMax=3,askMinTime=20,alarmHeartrateLimit=100-160,hospitalId=3,]
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
			safemax = 160;
			safemin = 110;
			ToastUtil.show(getApplicationContext(), "解析错误,设置为默认值");
		}
		LogUtil.d(TAG, "safemin:%s,safemax:%s", safemin, safemax);
	}
}
