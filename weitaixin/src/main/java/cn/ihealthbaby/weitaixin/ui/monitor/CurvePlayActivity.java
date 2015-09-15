package cn.ihealthbaby.weitaixin.ui.monitor;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qiniu.android.http.ResponseInfo;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseActivity;
import cn.ihealthbaby.weitaixin.db.DataDao;
import cn.ihealthbaby.weitaixin.library.data.model.MyAdviceItem;
import cn.ihealthbaby.weitaixin.library.data.model.data.Data;
import cn.ihealthbaby.weitaixin.library.data.model.data.RecordData;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.AsynUploadEngine;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import cn.ihealthbaby.weitaixin.ui.widget.CurveHorizontalScrollView;
import cn.ihealthbaby.weitaixin.ui.widget.CurveMonitorDetialView;

public class CurvePlayActivity extends BaseActivity {
	private final static String TAG = "CurvePlayActivity";
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
	private Data data;
	private ArrayList<Integer> fhrs;
	private Dialog dialog;
	private String uuid;
	private MyAdviceItem myAdviceItem;
	private ArrayList<Integer> fetalMove;
	private MediaPlayer mediaPlayer;

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
	public void upload(View view) {
		AsynUploadEngine asynUploadEngine = new AsynUploadEngine(getApplicationContext());
		String uuid = getIntent().getStringExtra(Constants.INTENT_UUID);
		if (uuid == null) {
			ToastUtil.show(getApplicationContext(), "未获取到本地记录");
			return;
		}
		dialog.show();
		asynUploadEngine.init(new File(FileUtil.getVoiceDir(getApplicationContext()), uuid));
		asynUploadEngine.setOnFinishActivity(new AsynUploadEngine.FinishedToDoWork() {
			@Override
			public void onFinishedWork(String key, ResponseInfo info, JSONObject response) {
				ApiManager.getInstance().adviceApi.uploadData(getUploadData(myAdviceItem), new HttpClientAdapter.Callback<Long>() {
					@Override
					public void call(Result<Long> t) {
						dialog.dismiss();
						if (t.isSuccess()) {
							ToastUtil.show(getApplicationContext(), "上传成功");
							Long data = t.getData();
							saveDataToDatabase(data);
						} else {
							ToastUtil.show(getApplicationContext(), t.getMsg());
						}
					}
				}, getRequestTag());
			}
		});
	}

	private AdviceForm getUploadData(MyAdviceItem item) {
		AdviceForm adviceForm = new AdviceForm();
		adviceForm.setClientId(item.getJianceid());
		adviceForm.setDataType(1);
		adviceForm.setDeviceType(1);
		adviceForm.setFeeling(item.getFeeling());
		adviceForm.setAskPurpose(item.getPurpose());
		adviceForm.setData(item.getRdata());
		adviceForm.setTestTime(item.getTestTime());
		adviceForm.setTestTimeLong(item.getTestTimeLong());
		adviceForm.setClientId(uuid);
		return adviceForm;
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
		getData();
		configCurve();
		mediaPlayer = new MediaPlayer();
		String path = myAdviceItem.getPath();
		try {
			mediaPlayer.setDataSource(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		CustomDialog customDialog = new CustomDialog();
		dialog = customDialog.createDialog1(this, "正在上传胎音文件...");
		countDownTimer = new ExpendableCountDownTimer(fhrs.size() * data.getInterval(), 500) {
			public int fmposition;
			public int position;

			@Override
			public void onStart(long startTime) {
				try {
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
					if (fmposition < fetalMove.size() && fetalMove.get(fmposition) / getInterval() == position) {
						curvePlay.addRedHeart(position);
						fmposition++;
					}
					curvePlay.postInvalidate();
					// TODO: 15/9/9   颜色根据数值变化
					bpm.setText(fhr + "");
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
	 * 从网络或者本地数据库获取数据
	 */
	private void getData() {
		DataDao dao = DataDao.getInstance(getApplicationContext());
		uuid = getIntent().getStringExtra(Constants.INTENT_UUID);
		myAdviceItem = dao.findNative(uuid);
		if (myAdviceItem != null) {
			LogUtil.d(TAG, myAdviceItem.toString());
			String rdata = myAdviceItem.getRdata();
			Gson gson = new Gson();
			RecordData recordData = gson.fromJson(rdata, RecordData.class);
			data = recordData.getData();
			fhrs = gson.fromJson(data.getHeartRate(), new TypeToken<ArrayList<Integer>>() {
			}.getType());
			fetalMove = gson.fromJson(data.getFm(), new TypeToken<ArrayList<Integer>>() {
			}.getType());
		} else {
			ToastUtil.show(getApplicationContext(), "获取数据失败");
		}
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
}
