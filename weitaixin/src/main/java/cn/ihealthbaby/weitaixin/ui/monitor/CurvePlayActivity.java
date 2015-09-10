package cn.ihealthbaby.weitaixin.ui.monitor;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;

import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.tools.AsynUploadEngine;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorDetialView;

public class CurvePlayActivity extends BaseActivity {
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

	@OnClick(R.id.back)
	public void back() {
		finish();
	}

	@OnClick(R.id.play)
	public void play() {
		countDownTimer.start();
	}

	@OnClick(R.id.replay)
	public void replay() {
		countDownTimer.restart();
	}

	@OnClick(R.id.btn_start)
	public void upload(View view) {
		AsynUploadEngine asynUploadEngine = new AsynUploadEngine(getApplicationContext());
		String filename = null;
		asynUploadEngine.init(new File(getCacheDir(), filename));
		asynUploadEngine.setOnFinishActivity(new AsynUploadEngine.FinishedToDoWork() {
			@Override
			public void onFinishedWork(String key, ResponseInfo info, JSONObject response) {
				ApiManager.getInstance().adviceApi.uploadData(WeiTaiXinApplication.getInstance().adviceForm, new HttpClientAdapter.Callback<Long>() {
					@Override
					public void call(Result<Long> t) {
						if (t.isSuccess()) {
							Long data = t.getData();
							saveDataToDatabase(data);
						}
					}
				}, getRequestTag());
			}
		});
	}

	//记录保存到数据库
	private void saveDataToDatabase(Long data) {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curve_play);
		ButterKnife.bind(this);
		titleText.setText("胎心监测");
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels;
		Intent intent = getIntent();
		getData();
		configCurve();
		countDownTimer = new ExpendableCountDownTimer(20 * 60 * 1000, 500) {
			public int position;

			@Override
			public void onStart(long startTime) {
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished) {
				int fhr = DataStorage.fhrs.get(position);
				curvePlay.addPoint(fhr);
				curvePlay.postInvalidate();
				int fhr1 = DataStorage.fhrs.get(DataStorage.fhrs.size() - 1);
				// TODO: 15/9/9   颜色根据数值变化
				bpm.setText(fhr1 + "");
				if (!chs.isTouching()) {
					chs.smoothScrollTo((int) (curvePlay.getCurrentPositionX() - width / 2), 0);
				}
				position++;
			}

			@Override
			public void onFinish() {
				ToastUtil.show(getApplicationContext(), "播放结束");
			}
		};
	}

	/**
	 * 从网络或者本地数据库获取数据
	 */
	private void getData() {
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
}
