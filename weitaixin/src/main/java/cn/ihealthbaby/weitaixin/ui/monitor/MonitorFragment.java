package cn.ihealthbaby.weitaixin.ui.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.AudioPlayer;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.AbstractBluetoothListener;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothReceiver;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothScanner;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.DefaultBluetoothScanner;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.PseudoBluetoothService;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;
import cn.ihealthbaby.weitaixin.library.data.model.data.Data;
import cn.ihealthbaby.weitaixin.library.data.model.data.RecordData;
import cn.ihealthbaby.weitaixin.library.event.MonitorTerminateEvent;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/8/12 17:52.
 */
public class MonitorFragment extends BaseFragment {
	private final static String TAG = "MonitorFragment";
	public SoundPool alertSound;
	@Bind(R.id.round_frontground)
	ImageView roundFrontground;
	@Bind(R.id.round_background)
	ImageView roundBackground;
	@Bind(R.id.hint)
	TextView hint;
	@Bind(R.id.helper)
	ImageView helper;
	@Bind(R.id.tv_record)
	TextView tvRecord;
	@Bind(R.id.btn_start)
	ImageView btnStart;
	@Bind(R.id.tv_bluetooth)
	TextView tvBluetooth;
	@Bind(R.id.rl_start)
	RelativeLayout rlStart;
	@Bind(R.id.back)
	RelativeLayout back;
	@Bind(R.id.title_text)
	TextView titleText;
	@Bind(R.id.function)
	TextView function;
	@Bind(R.id.roundScale)
	ImageView roundScale;
	@Bind(R.id.bpm)
	ImageView bpm;
	@Bind(R.id.hint_animation)
	TextView hintAnimation;
	private BluetoothReceiver bluetoothReceiver = new BluetoothReceiver();
	private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothScanner bluetoothScanner;
	private Set<BluetoothDevice> bondedDevices;
	private PseudoBluetoothService pseudoBluetoothService;
	private ArrayList<BluetoothDevice> scanedDevices = new ArrayList<>();
	private boolean connected;
	private boolean needPlay;
	private boolean needRecord;
	private CountDownTimer countDownTimer;
	private boolean started;
	private FileOutputStream fileOutputStream;
	private AudioTrack audioTrack = AudioPlayer.getInstance().getmAudioTrack();
	private int autoStartTime = 2 * 60 * 1000;
	private ExpendableCountDownTimer autoStartTimer;
	private int safemin;
	private int safemax;
	private long lastAlert;
	private boolean alert;
	private int alertInterval;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
					case Constants.MESSAGE_STATE_CHANGE:
						switch (msg.arg1) {
							case PseudoBluetoothService.STATE_CONNECTED:
								LogUtil.d(TAG, "STATE_CONNECTED");
								connected = true;
								bluetoothScanner.cancleDiscovery();
								onConnectedUI();
								break;
							case PseudoBluetoothService.STATE_CONNECTING:
								LogUtil.d(TAG, "STATE_CONNECTING");
								break;
							case PseudoBluetoothService.STATE_LISTEN:
								LogUtil.d(TAG, "STATE_LISTEN");
								break;
							case PseudoBluetoothService.STATE_NONE:
								LogUtil.d(TAG, "STATE_NONE");
								reset();
								break;
						}
						break;
					case Constants.MESSAGE_WRITE:
						byte[] writeBuf = (byte[]) msg.obj;
						break;
					case Constants.MESSAGE_READ_FETAL_DATA:
						FHRPackage fhrPackage = (FHRPackage) msg.obj;
						int fhr1 = fhrPackage.getFHR1();
						DataStorage.fhrPackage.setFHRPackage(fhrPackage);
						if (tvBluetooth != null) {
							if (fhr1 >= safemin && fhr1 <= safemax) {
								tvBluetooth.setTextColor(Color.parseColor("#49DCB8"));
							} else {
								tvBluetooth.setTextColor(Color.parseColor("#FE0058"));
								if (alert && connected && started) {
									long currentTimeMillis = System.currentTimeMillis();
									if (currentTimeMillis - lastAlert >= alertInterval * 1000)
										alertSound.play(1, 1, 1, 0, 0, 1);
									lastAlert = currentTimeMillis;
								}
							}
							tvBluetooth.setText(fhr1 + "");
						}
						break;
					case Constants.MESSAGE_DEVICE_NAME:
						// save the connected device's name
						String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
						LogUtil.d(TAG, "connecting " + deviceName);
						break;
					case Constants.MESSAGE_CANNOT_CONNECT:
						LogUtil.d(TAG, "MESSAGE_CANNOT_CONNECT");
						ToastUtil.show(getActivity().getApplicationContext(), "未能连接上设备,请重试");
						started = false;
						reset();
						break;
					case Constants.MESSAGE_CONNECTION_LOST:
						LogUtil.d(TAG, "MESSAGE_CONNECTION_LOST");
						ToastUtil.show(getActivity().getApplicationContext(), "断开蓝牙连接");
						reset();
						break;
					case Constants.MESSAGE_VOICE:
						byte[] sound = (byte[]) msg.obj;
						audioTrack.write(sound, 0, sound.length);
						if (needRecord) {
							if (fileOutputStream != null) {
								try {
									fileOutputStream.write(sound);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private String getTempFileName() {
		return Constants.TEMP_FILE_NAME;
	}

	private File getTempFile() {
		return new File(FileUtil.getVoiceDir(getActivity()), getTempFileName());
	}

	private File getRecordFile() {
		return new File(FileUtil.getVoiceDir(getActivity()), getLocalRecordId());
	}

	@OnClick(R.id.helper)
	public void help() {
		Intent intent = new Intent(getActivity(), MonitorCommonSense.class);
		startActivity(intent);
	}

	@OnClick(R.id.function)
	public void ternimateMonitor() {
		EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_MANUAL_NOT_START));
	}

	@OnClick(R.id.btn_start)
	public void startMonitor(View view) {
		view.setClickable(false);
		getAdviceSetting();
		autoStartTimer.cancel();
		started = true;
		String localRecordId = getLocalRecordId();
		LogUtil.d(TAG, "localRecordId:%s", localRecordId);
		File file = getTempFile();
		try {
			if (file.createNewFile()) {
				fileOutputStream = new FileOutputStream(file);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Date recordStartTime = new Date();
		needRecord = true;
		needPlay = true;
		User user = getUser();
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getActivity().getApplicationContext());
		Record record = new Record();
		//必填内容:userId,userName,serialNumber,localRecordId
		record.setUserId(user.getId());
		record.setSerialNumber(getDeviceName());
		record.setUploadState(Record.UPLOAD_STATE_LOCAL);
		record.setUserName(user.getName());
		record.setLocalRecordId(localRecordId);
		record.setRecordStartTime(recordStartTime);
		record.setGestationalWeeks(DateTimeTool.getGestationalWeeks(user.getDeliveryTime(), recordStartTime));
		try {
			Record queryExist = recordBusinessDao.queryByLocalRecordId(record.getLocalRecordId());
			if (queryExist != null) {
				record.setLocalRecordId(getLocalRecordId());
			}
		} catch (Exception e) {
		}
		try {
			recordBusinessDao.insert(record);
			Record query = recordBusinessDao.query(record);
			LogUtil.d(TAG, query.toString());
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d(TAG, "数据插入失败");
			view.setClickable(true);
			started = false;
			needRecord = false;
			needPlay = false;
			autoStartTimer.cancel();
			SPUtil.clearUUID(getActivity().getApplicationContext());
			return;
		}
		Intent intent = new Intent(getActivity(), MonitorActivity.class);
		intent.putExtra(Constants.INTENT_LOCAL_RECORD_ID, localRecordId);
		startActivity(intent);
	}

	/**
	 * 1.开启蓝牙  2.匹配绑定的设备,连接设备  3.扫描 4.匹配扫描到的设备
	 *
	 * @param view
	 */
	@OnClick({R.id.round_frontground, R.id.tv_bluetooth})
	void startSearch(View view) {
		if (!connected) {
			onConnectingUI();
			if (!bluetoothScanner.isEnable()) {
				bluetoothScanner.enable();
			} else {
				LogUtil.d("bluetoothScanner", "bluetoothScanner");
				connectBondedDeviceOrSearch();
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor, null);
		ButterKnife.bind(this, view);
		EventBus.getDefault().register(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		loadSrc(roundBackground, R.drawable.round_background_1);
		loadSrc(roundFrontground, R.drawable.round_frontground_1);
		loadSrc(roundScale, R.drawable.round_scale);
		back.setVisibility(View.GONE);
		titleText.setText("胎心监测");
		function.setVisibility(View.GONE);
		getAdviceSetting();
		alertSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		alertSound.load(getActivity().getApplicationContext(), R.raw.didi, 1);
		autoStartTimer = new ExpendableCountDownTimer(autoStartTime, 1000) {
			@Override
			public void onStart(long startTime) {
				hint.setText("");
				hint.setVisibility(View.VISIBLE);
			}

			@Override
			public void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onTick(long millisUntilFinished) {
				hint.setText(millisUntilFinished / 1000 + "秒后倾听宝宝心跳");
			}

			@Override
			public void onFinish() {
				hint.setText("");
				hint.setVisibility(View.GONE);
				btnStart.performClick();
			}

			@Override
			public void onRestart() {
			}
		};
		reset();
		countDownTimer = new CountDownTimer(10000, 10000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				if (!connected || bluetoothScanner.isDiscovering()) {
					ToastUtil.show(getActivity().getApplicationContext(), "未能连接上设备,请重试");
					reset();
					pseudoBluetoothService.stop();
				}
				if (bluetoothScanner.isDiscovering()) {
					bluetoothScanner.cancleDiscovery();
				}
			}
		};
		bluetoothScanner = new DefaultBluetoothScanner(adapter);
		pseudoBluetoothService = new PseudoBluetoothService(getActivity().getApplicationContext(), handler);
		bluetoothReceiver.register(getActivity().getApplicationContext());
		bluetoothReceiver.setListener(new AbstractBluetoothListener() {
			@Override
			public void onFound(BluetoothDevice remoteDevice, String remoteName, short rssi, BluetoothClass bluetoothClass) {
				if (!scanedDevices.contains(remoteDevice)) {
					if (getDeviceName().equalsIgnoreCase(remoteName)) {
						pseudoBluetoothService.connect(remoteDevice, false);
					}
					scanedDevices.add(remoteDevice);
				}
			}

			@Override
			public void onConnect(BluetoothDevice remoteDevice) {
				// TODO: 15/8/13 此处触发与期望不一致
			}

			@Override
			public void onDisconnect(BluetoothDevice remoteDevice) {
				reset();
			}

			@Override
			public void onStateOn() {
				connectBondedDeviceOrSearch();
			}

			@Override
			public void onStateOFF() {
				ToastUtil.warn(getActivity().getApplicationContext(), "蓝牙被关闭");
			}

			@Override
			public void onRequestBluetoothEnable() {
				scanedDevices.clear();
			}

			@Override
			public void onDiscoveryStarted() {
				scanedDevices.clear();
			}

			@Override
			public void onDiscoveryFinished() {
				scanedDevices.clear();
			}
		});
	}

	private void loadSrc(ImageView imageView, int drawableId) {
		ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
		Picasso.with(getActivity().getApplicationContext()).load(drawableId).resize(layoutParams.width, layoutParams.height).into(imageView);
	}

	/**
	 * 获取开始前的配置
	 */
	private void getAdviceSetting() {
		User user = SPUtil.getUser(getActivity().getApplicationContext());
		ServiceInfo serviceInfo = SPUtil.getServiceInfo(getActivity().getApplicationContext());
		LocalSetting localSetting = SPUtil.getLocalSetting(getActivity().getApplicationContext());
		AdviceSetting adviceSetting = SPUtil.getAdviceSetting(getActivity().getApplicationContext());
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
		// TODO: 15/9/17 autoBeginAdviceMax = 3,autoBeginAdvice=20??? @小顾
		//{"data":{"autoBeginAdvice":20,"autoAdviceTimeLong":20,"fetalMoveTime":5,"autoBeginAdviceMax":3,"askMinTime":20,"alarmHeartrateLimit":"100-160","hospitalId":3}}
		alert = localSetting.isAlert();
		if (alert) {
			autoStartTime = adviceSetting.getAutoBeginAdvice() * 1000;
		} else {
			autoStartTime = adviceSetting.getAutoBeginAdviceMax() * 1000;
		}
		alertInterval = localSetting.getAlertInterval();
		LogUtil.d(TAG, "safemin:%s,safemax:%s,autoStartTime:%s", safemin, safemax, autoStartTime);
	}

	public void reset() {
		tvBluetooth.setTextColor(getResources().getColor(R.color.green0));
		tvBluetooth.setText("start");
		tvBluetooth.setClickable(true);
		btnStart.setClickable(true);
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 58);
		hint.setText("");
		hint.setVisibility(View.GONE);
		bpm.setVisibility(View.GONE);
		function.setVisibility(View.GONE);
		rlStart.setVisibility(View.GONE);
		roundBackground.setImageResource(R.drawable.round_background_1);
		connected = false;
		needRecord = false;
		needPlay = true;
		if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
			audioTrack.flush();
			audioTrack.play();
		}
		// TODO: 15/9/13
		scanedDevices.clear();
	}

	private void onConnectingUI() {
		tvBluetooth.setText("连接中");
		tvBluetooth.setClickable(false);
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
		hint.setText("请耐心等待");
		hint.setVisibility(View.VISIBLE);
		bpm.setImageResource(R.drawable.bpm_dark);
		bpm.setVisibility(View.VISIBLE);
		loadSrc(roundBackground, R.drawable.round_background_2);
	}

	public void onConnectedUI() {
		rlStart.setVisibility(View.VISIBLE);
		function.setText("立即结束");
		function.setVisibility(View.VISIBLE);
		tvBluetooth.setText("--");
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 88);
		bpm.setImageResource(R.drawable.bpm_red);
		hint.setVisibility(View.GONE);
		autoStartTimer.start();
	}

	public void connectBondedDeviceOrSearch() {
		bondedDevices = adapter.getBondedDevices();
		LogUtil.e("bluetoothScanner", "" + bondedDevices.size());
		if (bondedDevices != null && bondedDevices.size() > 0) {
			for (BluetoothDevice device : bondedDevices) {
				LogUtil.e("bluetoothScanner", "devicegetName: " + device.getName());
				if (getDeviceName().equalsIgnoreCase(device.getName())) {
					LogUtil.e("bluetoothScanner", "" + "pseudoBluetoothService.connect()");
					pseudoBluetoothService.connect(device, false);
					return;
				}
			}
		}
		if (!bluetoothScanner.isDiscovering()) {
			bluetoothScanner.discovery();
		}
		countDownTimer.start();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		handler = null;
		bluetoothReceiver.unRegister(getActivity().getApplicationContext());
		ButterKnife.unbind(this);
		EventBus.getDefault().unregister(this);
	}

	private String getLocalRecordId() {
		String uuid = SPUtil.getUUID(getActivity().getApplicationContext());
		if (TextUtils.isEmpty(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
			SPUtil.setUUID(getActivity().getApplicationContext(), uuid);
		}
		return uuid;
	}

	private String getDeviceName() {
		String serialnum = null;
		ServiceInfo serviceInfo = getServiceInfo();
		if (serviceInfo != null) {
			serialnum = serviceInfo.getSerialnum();
		}
		LogUtil.d(TAG, "serialNumber:" + serialnum);
		return serialnum == null ? "" : serialnum;
	}

	private ServiceInfo getServiceInfo() {
		return SPUtil.getServiceInfo(getActivity().getApplicationContext());
	}

	private User getUser() {
		return SPUtil.getUser(getActivity().getApplicationContext());
	}

	private File getPath() {
		return new File(getTempFileName());
	}

	public void onEventMainThread(MonitorTerminateEvent event) {
		int reason = event.getEvent();
		new Thread() {
			@Override
			public void run() {
				super.run();
				pseudoBluetoothService.stop();
			}
		}.start();
		needPlay = false;
		if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			audioTrack.pause();
			audioTrack.flush();
		}
		if (fileOutputStream != null) {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		reset();
		Intent intent = new Intent(getActivity(), GuardianStateActivity.class);
		intent.putExtra(Constants.INTENT_LOCAL_RECORD_ID, getLocalRecordId());
		switch (reason) {
			case MonitorTerminateEvent.EVENT_AUTO:
				LogUtil.d(TAG, "EVENT_AUTO");
				runSave();
				startActivity(intent);
				break;
			case MonitorTerminateEvent.EVENT_UNKNOWN:
				LogUtil.d(TAG, "EVENT_UNKNOWN");
				runSave();
				startActivity(intent);
				break;
			case MonitorTerminateEvent.EVENT_MANUAL:
				LogUtil.d(TAG, "EVENT_MANUAL");
				runSave();
				startActivity(intent);
				break;
			case MonitorTerminateEvent.EVENT_MANUAL_NOT_START:
				if (autoStartTimer != null) {
					autoStartTimer.cancel();
				}
				break;
			default:
				break;
		}
	}

	private void runSave() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void save() throws Exception {
		File tempFile = getTempFile();
		File file = getRecordFile();
		if (tempFile.renameTo(file)) {
			tempFile.delete();
		} else {
			ToastUtil.show(getActivity().getApplicationContext(), "胎音文件错误");
			return;
		}
		FileUtil.addFileHead(file);
		//
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getActivity().getApplicationContext());
		Record record = null;
		try {
			record = recordBusinessDao.queryByLocalRecordId(getLocalRecordId());
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getActivity().getApplicationContext(), "数据查询异常");
			return;
		}
		Date recordStartTime = record.getRecordStartTime();
		//
		Gson gson = new Gson();
		RecordData recordData = new RecordData();
		Data data = new Data();
		data.setInterval(500);
		data.setHeartRate(DataStorage.fhrs);
		data.setFm(Util.position2Time(DataStorage.fms));
		data.setTime(recordStartTime.getTime());
		recordData.setData(data);
		String dataString = gson.toJson(recordData);
		record.setRecordData(dataString);
		record.setDuration((DataStorage.fhrs.size() / 2));
		record.setSoundPath(getRecordFile().getPath());
		try {
			recordBusinessDao.update(record);
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getActivity().getApplicationContext(), "数据保存异常");
			return;
		}
		try {
			Record query = recordBusinessDao.query(record);
			LogUtil.d(TAG, query.toString());
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getActivity().getApplicationContext(), "数据查询异常");
			return;
		}
		SPUtil.clearUUID(getActivity().getApplicationContext());
		DataStorage.fhrs.clear();
		DataStorage.fms.clear();
		DataStorage.fhrPackage.recycle();
	}
}



