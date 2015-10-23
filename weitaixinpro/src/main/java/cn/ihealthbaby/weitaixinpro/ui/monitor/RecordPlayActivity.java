package cn.ihealthbaby.weitaixinpro.ui.monitor;

import android.app.Dialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.data.model.data.Data;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixinpro.R;
import cn.ihealthbaby.weitaixinpro.base.BaseActivity;
import cn.ihealthbaby.weitaixinpro.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixinpro.ui.widget.CurveMonitorPlayView;

public abstract class RecordPlayActivity extends BaseActivity {
	private final static String TAG = "LocalRecordPlayActivity";
	public String path;
	public Record record;
	public String uuid;
	public RelativeLayout.LayoutParams layoutParams;
	protected Data data;
	protected List<Integer> fhrs;
	protected List<Integer> fms;
	protected List<Integer> doctors;
	protected Dialog dialog;
	@Bind(R.id.curve_play)
	CurveMonitorPlayView curvePlay;
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
	@Bind(R.id.tv_data)
	TextView tvData;
	@Bind(R.id.btn_data)
	ImageView btnData;
	@Bind(R.id.rl_data)
	RelativeLayout rlData;
	@Bind(R.id.vertical_line)
	ImageView verticalLine;
	private int width;
	private ExpendableCountDownTimer countDownTimer;
	private MediaPlayer mediaPlayer;
	private int safemin;
	private int safemax;
	private long pausedTime;
	private float diffTime;
	private long newOffset;
	private boolean playing;

	@OnClick(R.id.back)
	public void back() {
		finish();
	}

	@OnClick({R.id.play, R.id.play_wrapper})
	public void play() {
		if (playing) {
			pausedTime = countDownTimer.getConsumedTime();
			countDownTimer.cancel();
			mediaPlayer.pause();
			play.setImageResource(R.drawable.button_play);
		} else {
			countDownTimer.startAt(pausedTime);
//			play.setImageResource(R.drawable.button_pause);
		}
		playing = !playing;
	}

	@OnClick({R.id.replay, R.id.replay_wrapper})
	public void replay() {
		countDownTimer.restart();
	}

	@OnClick(R.id.btn_business)
	public void function(View view) {
		function();
	}

	@OnClick(R.id.btn_data)
	public void data() {
		uploadData();
	}

	protected abstract void uploadData();

	protected abstract void function();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_play);
		ButterKnife.bind(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		titleText.setText("胎心监测");
		final DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		getData();
		getAdviceSetting();
		configCurve();
		tvStartTime.setText("开始时间 " + DateTimeTool.million2hhmmss(record.getRecordStartTime().getTime()));
		mediaPlayer = new MediaPlayer();
		final int duration = fhrs.size() * data.getInterval();
		chs.setOnTouchListener(new View.OnTouchListener() {
			public int scrollX1;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction();
				switch (action) {
					case MotionEvent.ACTION_DOWN:
						playing = false;
						scrollX1 = chs.getScrollX();
						pausedTime = countDownTimer.getConsumedTime();
						countDownTimer.cancel();
						mediaPlayer.pause();
						play.setImageResource(R.drawable.button_play);
						LogUtil.d(TAG, "pausedTime:[%s]", pausedTime);
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
						playing = true;
						final int scrollX2 = chs.getScrollX();
						LogUtil.d(TAG, "scrollX2:[%s]", scrollX2);
						diffTime = curvePlay.reconvertXDiff(scrollX2 - scrollX1);
						newOffset = pausedTime + (long) (diffTime) * 1000;
						LogUtil.d(TAG, "newOffset:[%s]", newOffset);
						countDownTimer.cancel();
						countDownTimer.startAt(newOffset);
						break;
					default:
						break;
				}
				return false;
			}
		});
		layoutParams = (RelativeLayout.LayoutParams) verticalLine.getLayoutParams();
		layoutParams.setMargins(curvePlay.getPaddingLeft(), 0, 0, 0);
		countDownTimer = new ExpendableCountDownTimer(duration, 500) {
			public int position;

			@Override
			public void onStart(long startTime) {
				curvePlay.reset();
				play.setImageResource(R.drawable.button_pause);
				position = (int) (getOffset() / getInterval());
				LogUtil.d(TAG, "position:[%s]", position);
				LogUtil.d(TAG, "getOffset():[%s]", getOffset());
				layoutParams.setMargins(curvePlay.getPaddingLeft(), 0, 0, 0);
				try {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(path);
					mediaPlayer.prepare();
					mediaPlayer.seekTo((int) getOffset());
					mediaPlayer.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				curvePlay.draw2Position(position);
				final float currentPositionX = curvePlay.convertPositionX(position);
				final float diff = currentPositionX - width / 2;
				if (diff <= 0) {
					layoutParams.setMargins((int) currentPositionX, 0, 0, 0);
				} else {
					layoutParams.setMargins(width / 2, 0, 0, 0);
				}
				if (!chs.isTouching()) {
					chs.smoothScrollTo((int) diff, 0);
				}
				tvStartTime.setText("开始时间 " + DateTimeTool.million2hhmmss(record.getRecordStartTime().getTime()));
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished) {
				tvConsumTime.setText(DateTimeTool.million2mmss(getConsumedTime()));
				int fhr = fhrs.get(position);
				curvePlay.add2Position(position);
				curvePlay.postInvalidate();
				position++;
				if (bpm != null) {
					if (fhr >= safemin && fhr <= safemax) {
						bpm.setTextColor(Color.parseColor("#49DCB8"));
					} else {
						bpm.setTextColor(Color.parseColor("#FE0058"));
					}
					bpm.setText(fhr + "");
				}
				final float currentPositionX = curvePlay.convertPositionX(position);
				final float diff = currentPositionX - width / 2;
//				LogUtil.d(TAG, "currentPositionX:[%s], diff:[%s]", currentPositionX, diff);
				if (diff <= 0) {
					layoutParams.setMargins((int) currentPositionX, 0, 0, 0);
				} else {
					layoutParams.setMargins(width / 2, 0, 0, 0);
				}
				if (!chs.isTouching()) {
					chs.smoothScrollTo((int) diff, 0);
				}
			}

			@Override
			public void onFinish() {
				playing = false;
				pausedTime = 0;
				play.setImageResource(R.drawable.button_play);
				LogUtil.d(TAG, "finish");
				ToastUtil.show(getApplicationContext(), "播放结束");
				mediaPlayer.stop();
				mediaPlayer.reset();
			}

			@Override
			public void onRestart() {
				position = 0;
				curvePlay.reset();
				chs.smoothScrollTo(0, 0);
			}
		};
	}

	/**
	 * 从网络或者本地数据库获取数据 protected Data data; protected List<Integer> fhrs; protected List<Integer>
	 * fms;
	 */
	protected abstract void getData();

	private void configCurve() {
		// TODO: 15/9/9  设置数据源
		int duration = record.getDuration();
		int xMax = duration / 60 * 60 + (duration % 60 == 0 ? 0 : 1) * 60;
		curvePlay.setxMax(xMax);
		curvePlay.setCellWidth(Util.dip2px(getApplicationContext(), 10));
		curvePlay.setCurveStrokeWidth(2);
		curvePlay.setFhrs(fhrs);
		curvePlay.setDoctors(doctors);
		curvePlay.setHearts(fms);
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
