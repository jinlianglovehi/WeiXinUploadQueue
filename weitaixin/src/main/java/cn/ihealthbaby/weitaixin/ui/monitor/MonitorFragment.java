package cn.ihealthbaby.weitaixin.ui.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.AbstractBusiness;
import cn.ihealthbaby.weitaixin.BuildConfig;
import cn.ihealthbaby.weitaixin.CustomDialog;
import cn.ihealthbaby.weitaixin.DefaultCallback;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
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
import cn.ihealthbaby.weitaixin.library.event.MonitorStartEvent;
import cn.ihealthbaby.weitaixin.library.event.MonitorTerminateEvent;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.DataStorage;
import cn.ihealthbaby.weitaixin.library.util.ExpendableCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.FixedRateCountDownTimer;
import cn.ihealthbaby.weitaixin.library.util.LocalRecordIdUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.library.util.Util;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/8/12 17:52.
 */
public class MonitorFragment extends BaseFragment {
	private final static String TAG = "MonitorFragment";
	public static MonitorFragment instance = new MonitorFragment();
	private static WeakReference<MonitorFragment> monitorFragmentWeakReference = new WeakReference<MonitorFragment>(instance);
	/**
	 * 处理连接状态以及连接失败
	 */
	private static Handler handler;
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
	@Bind(R.id.tv_start)
	TextView tvStart;
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
	private Set<BluetoothDevice> scanedDevices = new HashSet<>();
	private boolean connected;
	private CountDownTimer countDownTimer;
	private boolean started;
	private int autoStartTime = 2 * 60 * 1000;
	private ExpendableCountDownTimer autoStartTimer;
	private int safemin;
	private int safemax;
	private FixedRateCountDownTimer readDataTimer;
	private boolean autoStart;

	public static MonitorFragment getInstance() {
		if (handler == null) {
			handler = new MonitorHandler(monitorFragmentWeakReference);
		}
		return instance;
	}

	@OnClick(R.id.btn_start)
	public void startMonitor() {
		//防止重复点击
		btnStart.setClickable(false);
		//获取配置信息
		getAdviceSetting();
		//生成最新的本地id
		String localRecordId = LocalRecordIdUtil.generateAndSaveId(getActivity());
		initRecord(localRecordId);
		autoStartTimer.cancel();
		countDownTimer.cancel();
		readDataTimer.cancel();
		started = true;
		final MonitorStartEvent event = new MonitorStartEvent();
		event.setLocalRecordId(localRecordId);
		EventBus.getDefault().post(event);
		Intent intent = new Intent(getActivity(), MonitorActivity.class);
		intent.putExtra(Constants.INTENT_LOCAL_RECORD_ID, localRecordId);
		startActivity(intent);
	}

	private void initRecord(String localRecordId) {
		LogUtil.d(TAG, "localRecordId:%s", localRecordId);
		Date recordStartTime = new Date();
		User user = getUser();
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getActivity().getApplicationContext());
		Record record = new Record();
		//必填内容:userId,userName,serialNumber,localRecordId
		record.setUserId(user.getId());
		record.setUserName(user.getName());
		record.setGestationalWeeks(DateTimeTool.getGestationalWeeks(user.getDeliveryTime(), recordStartTime));
		//
		record.setSerialNumber(getDeviceName());
		record.setUploadState(Record.UPLOAD_STATE_LOCAL);
		record.setLocalRecordId(localRecordId);
		record.setRecordStartTime(recordStartTime);
		try {
			recordBusinessDao.insert(record);
			if (Constants.MODE_DEBUG) {
				Record query = recordBusinessDao.query(record);
				LogUtil.d(TAG, query.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.d(TAG, "数据插入失败");
			btnStart.setClickable(true);
			started = false;
			autoStartTimer.cancel();
			SPUtil.clearUUID(getActivity().getApplicationContext());
		}
	}

	@OnClick(R.id.helper)
	public void help() {
		Intent intent = new Intent(getActivity(), MonitorCommonSense.class);
		startActivity(intent);
	}

	@OnClick(R.id.function)
	public void ternimateMonitor() {
		readDataTimer.cancel();
		EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_MANUAL_CANCEL_NOT_START));
	}

	/**
	 * 0.检查服务状态,如果服务开启,检查配置 1.开启蓝牙  2.匹配绑定的设备,连接设备  3.扫描 4.匹配扫描到的设备
	 */
	@OnClick({R.id.round_frontground, R.id.tv_bluetooth})
	void checkConfig() {
		//确认有hid
		//确认获取到配置
		LogUtil.d(TAG, "检查服务状态");
		final ApiManager apiManager = ApiManager.getInstance();
		final CustomDialog customDialog = new CustomDialog(getActivity(), "正在查询配置信息");
		customDialog.show();
		apiManager.userApi.refreshInfo(new DefaultCallback<User>(getActivity().getApplicationContext(), new AbstractBusiness<User>() {
			@Override
			public void handleData(User user) {
				if (user != null) {
					LogUtil.d(TAG, "获取到用户信息并保存");
					SPUtil.saveUser(getActivity().getApplicationContext(), user);
					//如果开通服务,请求配置
					if (user.getHasService()) {
						LogUtil.d(TAG, "已开通服务");
						apiManager.adviceApi.getAdviceSetting(user.getHospitalId(), new DefaultCallback<AdviceSetting>(getActivity(), new AbstractBusiness<AdviceSetting>() {
							@Override
							public void handleData(AdviceSetting adviceSetting) {
								customDialog.dismiss();
								//保存用户信息
								SPUtil.saveAdviceSetting(getActivity().getApplicationContext(), adviceSetting);
								LogUtil.d(TAG, "获取服务信息,并保存");
								//开始开启蓝牙,搜索设备
								startSearch();
							}

							@Override
							public void handleAllFailure(Context context) {
								super.handleAllFailure(context);
								customDialog.dismiss();
								ToastUtil.show(getActivity().getApplicationContext(), "获取用户配置失败");
								reset();
							}
						}), getRequestTag());
					} else {
						customDialog.dismiss();
						LogUtil.d(TAG, "未开通服务");
						ToastUtil.show(getActivity().getApplicationContext(), "请先开通服务");
					}
				}
			}

			@Override
			public void handleAllFailure(Context context) {
				super.handleAllFailure(context);
				customDialog.dismiss();
				ToastUtil.show(getActivity().getApplicationContext(), "获取用户信息失败");
				reset();
			}
		}), getRequestTag());
	}

	private void startSearch() {
		LogUtil.d(TAG, "开始搜索");
		if (!connected) {
			onConnectingUI();
			countDownTimer.start();
			if (!bluetoothScanner.isEnable()) {
				LogUtil.d(TAG, "蓝牙处于未开启状态,正在打开");
				bluetoothScanner.enable();
			} else {
				LogUtil.d(TAG, "蓝牙处于已开启状态");
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

	/**
	 * 初始化数据 主要有三个计时器, 自动开始,搜索倒计时,
	 *
	 * @param view
	 * @param savedInstanceState
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		initView();
		reset();
		readDataTimer = new FixedRateCountDownTimer(100000, 500) {
			@Override
			protected void onExtra(long duration, long extraTime, long stopTime) {
			}

			@Override
			public void onStart(long startTime) {
			}

			@Override
			public void onRestart() {
			}

			@Override
			public void onTick(long millisUntilFinished, FHRPackage fhrPackage) {
				final int fhr = fhrPackage.getFHR1();
				if (tvBluetooth != null) {
					if (fhr >= safemin && fhr <= safemax) {
						tvBluetooth.setTextColor(Color.parseColor("#49DCB8"));
					} else {
						tvBluetooth.setTextColor(Color.parseColor("#FE0058"));
					}
					tvBluetooth.setText(fhr + "");
				}
			}

			@Override
			public void onFinish() {
				start();
			}
		};
		autoStartTimer = new ExpendableCountDownTimer(autoStartTime, 1000) {
			@Override
			public void onStart(long startTime) {
				tvStart.setVisibility(View.GONE);
				hint.setText("");
				if (autoStart) {
					hint.setVisibility(View.VISIBLE);
				} else {
					hint.setVisibility(View.GONE);
				}
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
		countDownTimer = new CountDownTimer(20000, 10000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				if (!connected) {
					ToastUtil.show(getActivity().getApplicationContext(), "未能连接上设备,请重试");
					LogUtil.d(TAG, "时间到,未能连接到设备");
					pseudoBluetoothService.stop();
				}
				if (bluetoothScanner.isDiscovering()) {
					LogUtil.d(TAG, "搜索状态:正在搜索. 开始停止搜索设备");
					bluetoothScanner.cancleDiscovery();
				}
			}
		};
		bluetoothScanner = new DefaultBluetoothScanner(adapter);
		pseudoBluetoothService = PseudoBluetoothService.getInstance(getActivity().getApplicationContext(), handler);
		bluetoothReceiver.register(getActivity().getApplicationContext());
		bluetoothReceiver.setListener(new AbstractBluetoothListener() {
			@Override
			public void onFound(BluetoothDevice remoteDevice, String remoteName, short rssi, BluetoothClass bluetoothClass) {
				connectDevice(remoteDevice, remoteName);
			}

			@Override
			public void onRemoteNameChanged(BluetoothDevice remoteDevice, String remoteName) {
				connectDevice(remoteDevice, remoteName);
			}

			@Override
			public void remoteClassChanged(BluetoothDevice remoteDevice, BluetoothClass bluetoothClass) {
				connectDevice(remoteDevice, remoteDevice.getName());
			}

			private synchronized void connectDevice(BluetoothDevice remoteDevice, String remoteName) {
				if (!scanedDevices.contains(remoteDevice)) {
					if (getDeviceName().equalsIgnoreCase(remoteName)) {
						LogUtil.d(TAG, "发现匹配的设备,正在连接设备:" + remoteName);
						adapter.cancelDiscovery();
						pseudoBluetoothService.connect(remoteDevice, true);
					}
					scanedDevices.add(remoteDevice);
				}
			}

			@Override
			public void onPairingRequest(BluetoothDevice remoteDevice, String remoteName, String pairingKey, int pairingVariant) {
				LogUtil.d(TAG, "remoteDevice:%s,remoteName:%s ,pairingKey:%s ,pairingVariant:%s", remoteDevice, remoteName, pairingKey, pairingVariant);
			}

			@Override
			public void onActionAclDisConnected() {

			}

			@Override
			public void onConnect(BluetoothDevice remoteDevice) {
				// TODO: 15/8/13 此处触发与期望不一致
			}

			@Override
			public void onDisconnect(BluetoothDevice remoteDevice) {
//				reset();
			}

			@Override
			public void onStateOn() {
				connectBondedDeviceOrSearch();
			}

			@Override
			public void onStateOFF() {
				ToastUtil.show(getActivity().getApplicationContext(), "蓝牙被关闭");
				pseudoBluetoothService.stop();
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
			}
		});
	}

	private void initView() {
		loadSrc(roundBackground, R.drawable.round_background_1);
		loadSrc(roundFrontground, R.drawable.round_frontground_1);
		loadSrc(roundScale, R.drawable.round_scale);
		back.setVisibility(View.GONE);
		titleText.setText("胎心监测");
		function.setVisibility(View.GONE);
	}

	/**
	 * 后续去掉
	 *
	 * @param imageView
	 * @param drawableId
	 */
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
			if (split.length == 2) {
				safemin = Integer.parseInt(split[0]);
				safemax = Integer.parseInt(split[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO: 15/9/17 autoBeginAdviceMax = 3,autoBeginAdvice=20??? @小顾
		//{"data":{"autoBeginAdvice":20,"autoAdviceTimeLong":20,"fetalMoveTime":5,"autoBeginAdviceMax":3,"askMinTime":20,"alarmHeartrateLimit":"100-160","hospitalId":3}}
		autoStart = localSetting.isAutoStart();
		if (autoStart) {
			autoStartTime = adviceSetting.getAutoBeginAdvice() * 1000;
		} else {
			autoStartTime = adviceSetting.getAutoBeginAdviceMax() * 1000;
		}
		LogUtil.d(TAG, "safemin:%s,safemax:%s,autoStartTime:%s", safemin, safemax, autoStartTime);
	}

	public void reset() {
		LogUtil.d(TAG, "重置状态");
		tvStart.setVisibility(View.VISIBLE);
		tvBluetooth.setText("");
		tvBluetooth.setClickable(true);
		roundFrontground.setClickable(true);
		btnStart.setClickable(true);
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
		hint.setText("");
		hint.setVisibility(View.GONE);
		bpm.setVisibility(View.GONE);
		function.setVisibility(View.GONE);
		rlStart.setVisibility(View.GONE);
		roundBackground.setImageResource(R.drawable.round_background_1);
		connected = false;
		started = false;
		getAdviceSetting();
		scanedDevices.clear();
	}

	private void onConnectingUI() {
		tvStart.setVisibility(View.GONE);
		tvBluetooth.setText("连接中");
		tvBluetooth.setClickable(false);
		roundFrontground.setClickable(false);
//		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
		hint.setText("请耐心等待");
		hint.setVisibility(View.VISIBLE);
		bpm.setImageResource(R.drawable.bpm_dark);
		bpm.setVisibility(View.VISIBLE);
		loadSrc(roundBackground, R.drawable.round_background_2);
	}

	public void onConnectedUI() {
		connected = true;
		readDataTimer.start();
		autoStartTimer.start();
		LogUtil.d(TAG, "开始倒计时,准备自动开始");
		if (bluetoothScanner.isDiscovering()) {
			bluetoothScanner.cancleDiscovery();
		}
		rlStart.setVisibility(View.VISIBLE);
		function.setText("立即结束");
		function.setVisibility(View.VISIBLE);
		tvBluetooth.setText("--");
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 88);
		bpm.setImageResource(R.drawable.bpm_red);
	}

	public void connectBondedDeviceOrSearch() {
		bondedDevices = adapter.getBondedDevices();
		LogUtil.d(TAG, "已绑定的设备数量" + bondedDevices.size());
		if (bondedDevices != null && bondedDevices.size() > 0) {
			for (BluetoothDevice device : bondedDevices) {
				LogUtil.d(TAG, "已绑定的设备名称: " + device.getName());
				if (getDeviceName().equalsIgnoreCase(device.getName())) {
					LogUtil.d(TAG, "找到匹配的设备,开始连接:" + device.getName());
					ToastUtil.show(getActivity(), "开始连接" + device.getName());
					pseudoBluetoothService.connect(device, false);
					return;
				}
			}
			LogUtil.d(TAG, "直接配对未发现设备");
		} else {
			LogUtil.d(TAG, "无绑定设备");
		}
		LogUtil.d(TAG, "开始搜索设备");
		ToastUtil.show(getActivity().getApplicationContext(), "开始搜索设备");
		//直接配对失败,开始搜索设备
		if (!adapter.isDiscovering()) {
			adapter.startDiscovery();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		bluetoothReceiver.unRegister(getActivity().getApplicationContext());
		ButterKnife.unbind(this);
		EventBus.getDefault().unregister(this);
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
		if (readDataTimer != null) {
			readDataTimer.cancel();
		}
		if (autoStartTimer != null) {
			autoStartTimer.cancel();
		}
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

	public void onEventMainThread(MonitorTerminateEvent event) {
		int reason = event.getEvent();
		pseudoBluetoothService.stop();
		Intent intent = new Intent(getActivity(), GuardianStateActivity.class);
		intent.putExtra(Constants.INTENT_LOCAL_RECORD_ID, LocalRecordIdUtil.getSavedId(getActivity()));
		switch (reason) {
			case MonitorTerminateEvent.EVENT_UNKNOWN:
				LogUtil.d(TAG, "EVENT_UNKNOWN");
				runSave(intent);
				break;
			case MonitorTerminateEvent.EVENT_AUTO:
				LogUtil.d(TAG, "EVENT_AUTO");
				runSave(intent);
				break;
			case MonitorTerminateEvent.EVENT_MANUAL:
				LogUtil.d(TAG, "EVENT_MANUAL");
				runSave(intent);
				break;
			case MonitorTerminateEvent.EVENT_MANUAL_CANCEL_NOT_START:
				if (autoStartTimer != null) {
					autoStartTimer.cancel();
					readDataTimer.cancel();
				}
				break;
			case MonitorTerminateEvent.EVENT_MANUAL_CANCEL_STARTED:
				DataStorage.fhrs.clear();
				DataStorage.fms.clear();
				DataStorage.doctors.clear();
				DataStorage.fhrPackage.recycle();
				break;
			default:
				break;
		}
		reset();
	}

	private void runSave(final Intent intent) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					save();
					handler.post(new Runnable() {
						@Override
						public void run() {
							startActivity(intent);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void save() throws Exception {
		final String localRecordId = LocalRecordIdUtil.getSavedId(getActivity());
		RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(getActivity().getApplicationContext());
		Record record;
		try {
			record = recordBusinessDao.queryByLocalRecordId(localRecordId);
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getActivity().getApplicationContext(), "数据查询异常");
			return;
		}
		Date recordStartTime = record.getRecordStartTime();
		//
		Gson gson = new Gson();
		RecordData recordData = Util.getDefaultRecordData(getActivity().getApplicationContext());
		Data data = new Data();
		data.setInterval(500);
		data.setHeartRate(DataStorage.fhrs);
		data.setFm(Util.position2Time(DataStorage.fms));
		data.setTime(recordStartTime.getTime());
		recordData.setData(data);
		String dataString = gson.toJson(recordData);
		record.setRecordData(dataString);
		record.setPurposeId(Record.PURPOSE_FM_NORMAL);
		record.setPurposeString("日常监护");
		record.setFeelingId(Record.FEELING_NORMAL);
		record.setFeelingString("一般");
		record.setDuration((DataStorage.fhrs.size() / 2));
		record.setSoundPath(FileUtil.getVoiceFile(getActivity(), localRecordId).getPath());
		try {
			recordBusinessDao.update(record);
		} catch (Exception e) {
			e.printStackTrace();
			ToastUtil.show(getActivity().getApplicationContext(), "数据保存异常");
			return;
		}
		//调试用 打印数据库里的该条数据
		if (BuildConfig.DEBUG) {
			try {
				Record query = recordBusinessDao.query(record);
				LogUtil.d(TAG, query.toString());
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtil.show(getActivity().getApplicationContext(), "数据查询异常");
				return;
			}
		}
		DataStorage.fhrs.clear();
		DataStorage.fms.clear();
		DataStorage.fhrPackage.recycle();
	}

	public void stopMonitor() {
		pseudoBluetoothService.stop();
		reset();
	}

	public static class MonitorHandler extends Handler {
		private MonitorFragment monitorFragment;

		public MonitorHandler(WeakReference<MonitorFragment> monitorFragmentWeakReference) {
			this.monitorFragment = monitorFragmentWeakReference.get();
		}

		public MonitorFragment getMonitorFragment() {
			return monitorFragment;
		}

		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
					case Constants.MESSAGE_STATE_CHANGE:
						switch (msg.arg1) {
							//已连接
							case PseudoBluetoothService.STATE_CONNECTED:
								LogUtil.d(TAG, "STATE_CONNECTED");
								getMonitorFragment().onConnectedUI();
								break;
							case PseudoBluetoothService.STATE_CONNECTING:
								LogUtil.d(TAG, "STATE_CONNECTING");
								//未连接,初始状态
								break;
							case PseudoBluetoothService.STATE_NONE:
								LogUtil.d(TAG, "STATE_NONE");
								getMonitorFragment().reset();
								break;
						}
						break;
					case Constants.MESSAGE_STATE_FAIL:
						getMonitorFragment().reset();
						switch (msg.arg1) {
							case Constants.MESSAGE_CANNOT_CONNECT:
								LogUtil.d(TAG, "MESSAGE_CANNOT_CONNECT");
								ToastUtil.show(getMonitorFragment().getActivity().getApplicationContext(), "未能连接上设备,请重试");
								break;
							case Constants.MESSAGE_CONNECTION_LOST:
								LogUtil.d(TAG, "MESSAGE_CONNECTION_LOST");
								ToastUtil.show(getMonitorFragment().getActivity().getApplicationContext(), "断开蓝牙连接");
								// TODO: 15/10/23 丢失连接 通知监测页面结束监测
								break;
							default:
								break;
						}
						break;
					default:
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}



