package cn.ihealthbaby.weitaixin.ui.monitor;

import android.app.Dialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.Advice;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.data.model.data.Data;
import cn.ihealthbaby.weitaixin.library.data.net.AbstractBusiness;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorDetialView;

public abstract class CloudRecordPlayActivity extends BaseActivity {
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
	@Bind(R.id.ivDelectAction)
	ImageView ivDelectAction;
	@Bind(R.id.tvDelectAction)
	TextView tvDelectAction;
	@Bind(R.id.flDelAction)
	FrameLayout flDelAction;
	@Bind(R.id.tv_start_time)
	TextView tvStartTime;
	@Bind(R.id.tv_consum_time)
	TextView tvConsumTime;
	private int width;
	private ExpendableCountDownTimer countDownTimer;
	private MediaPlayer mediaPlayer;
	private int safemin;
	private int safemax;
	private Advice advice;
	private FileOutputStream fileOutputStream;

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
				tvStartTime.setText("开始时间 " + DateTimeTool.million2hhmmss(System.currentTimeMillis()));
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
	 * 从网络获取数据
	 */
	protected void getData() {
		long id = getIntent().getLongExtra(Constants.INTENT_ID, 0);
		String url = getIntent().getStringExtra(Constants.INTENT_URL);
		String localRecordId = getIntent().getStringExtra(Constants.INTENT_LOCAL_RECORD_ID);
		final File file = new File(FileUtil.getVoiceDir(getApplicationContext()), localRecordId);
		if (file.exists()) {
			return;
		}
		if (id == 0) {
			ToastUtil.show(getApplicationContext(), "获取数据失败");
			return;
		}
		CustomDialog customDialog = new CustomDialog();
		dialog = customDialog.createDialog1(this, "正在下载胎音数据...");
		dialog.show();
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		asyncHttpClient.get(url, new FileAsyncHttpResponseHandler(file) {
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
				ToastUtil.show(getApplicationContext(), "未获取到音频数据");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, File file) {
				if (statusCode == Constants.CODE_200_OK) {
					try {
						mediaPlayer.setDataSource(file.getPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		ApiManager.getInstance().adviceApi.getAdviceDetail(id, new DefaultCallback<Advice>(getApplicationContext(), new AbstractBusiness<Advice>() {
			@Override
			public void handleData(Advice data) throws Exception {
				advice = data;
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}), getRequestTag());
	}

	private void configCurve() {
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
