package cn.ihealthbaby.weitaixin.ui.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.HttpClientAdapter.Callback;
import cn.ihealthbaby.client.Result;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.WeiTaiXinApplication;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.db.DataDao;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.AudioPlayer;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.DataStorage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.AbstractBluetoothListener;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothReceiver;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothScanner;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.DefaultBluetoothScanner;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.PseudoBluetoothService;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.test.Constants;
import cn.ihealthbaby.weitaixin.library.event.MonitorTerminateEvent;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.FileUtil;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.model.MyAdviceItem;
import cn.ihealthbaby.weitaixin.tools.DateTimeTool;
import cn.ihealthbaby.weitaixin.ui.monitor.data.Data;
import cn.ihealthbaby.weitaixin.ui.monitor.data.RecordData;
import de.greenrobot.event.EventBus;

/**
 * Created by liuhongjian on 15/8/12 17:52.
 */
public class MonitorFragment extends BaseFragment {
	private final static String TAG = "MonitorFragment";
	@Bind(R.id.round_frontground)
	ImageView roundFrontground;
	@Bind(R.id.round_background)
	ImageView roundBackground;
	//	@Bind(R.id.round_progress_mask)
	//	RoundMaskView roundProgressMask;
	@Bind(R.id.hint)
	TextView hint;
	@Bind(R.id.helper)
	ImageView helper;
	@Bind(R.id.tv_record)
	TextView tvRecord;
	@Bind(R.id.btn_start)
	ImageView btn;
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
	private boolean needPlay = true;
	private boolean needRecord;
	private CountDownTimer countDownTimer;
	private boolean started;
	private Date testTime;
	private String uuidString;
	private FileOutputStream fileOutputStream;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
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
							resetUI();
							break;
					}
					break;
				case Constants.MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					break;
				case Constants.MESSAGE_READ_FETAL_DATA:
					FHRPackage fhrPackage = (FHRPackage) msg.obj;
					DataStorage.fhrPackage.setFHRPackage(fhrPackage);
					if (tvBluetooth != null) {
						tvBluetooth.setText(fhrPackage.getFHR1() + "");
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
					resetUI();
					break;
				case Constants.MESSAGE_CONNECTION_LOST:
					LogUtil.d(TAG, "MESSAGE_CONNECTION_LOST");
					ToastUtil.show(getActivity().getApplicationContext(), "断开蓝牙连接");
					resetUI();
					break;
				case Constants.MESSAGE_VOICE:
					byte[] sound = (byte[]) msg.obj;
					if (needPlay) {
						AudioPlayer.getInstance().playAudioTrack(sound, 0, sound.length);
					}
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
		}
	};
	private AdviceSetting adviceSetting;
	private User user;
	private ServiceInfo serviceInfo;

	private String getFileName() {
		return Constants.TEMP_FILE_NAME;
	}

	@OnClick(R.id.helper)
	public void help() {
	}

	@OnClick(R.id.function)
	public void ternimate() {
		EventBus.getDefault().post(new MonitorTerminateEvent(MonitorTerminateEvent.EVENT_MANUAL_NOT_START));
	}

	@OnClick(R.id.btn_start)
	public void startRecord(View view) {
		started = true;
		uuidString = UUID.randomUUID().toString().replace("-", "");
		File file = new File(FileUtil.getVoiceDir(getActivity()), getFileName());
		try {
			fileOutputStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		AdviceForm adviceForm = new AdviceForm();
		testTime = new Date();
		adviceForm.setTestTime(testTime);
		adviceForm.setDeviceType(1);
		Intent intent = new Intent(getActivity(), MonitorActivity.class);
		intent.putExtra(Constants.INTENT_UUID, uuidString);
		needRecord = true;
		needPlay = true;
		startActivity(intent);
	}

	/**
	 * 1.开启蓝牙  2.匹配绑定的设备,连接设备  3.扫描 4.匹配扫描到的设备
	 *
	 * @param view
	 */
	@OnClick(R.id.round_frontground)
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

	private void clear(AdviceForm adviceForm) {
		adviceForm = new AdviceForm();
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
		getConfig();
		countDownTimer = new CountDownTimer(20000, 20000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				if (!connected || bluetoothScanner.isDiscovering()) {
					ToastUtil.show(getActivity().getApplicationContext(), "未能连接上设备,请重试");
					resetUI();
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
				resetUI();
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

	/**
	 * 请求医院配置信息
	 */
	private void getConfig() {
		user = SPUtil.getUser(getActivity().getApplicationContext());
		serviceInfo = SPUtil.getServiceInfo();
		ApiManager.getInstance().adviceApi.getAdviceSetting(serviceInfo.getHospitalId(), new Callback<AdviceSetting>() {
			@Override
			public void call(Result<AdviceSetting> t) {
				adviceSetting = t.getData();
			}
		}, TAG);
	}

	private void loadSrc(ImageView imageView, int drawableId) {
		ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
		Picasso.with(getActivity().getApplicationContext()).load(drawableId).resize(layoutParams.width, layoutParams.height).into(imageView);
	}

	public void resetUI() {
		tvBluetooth.setText("start");
		tvBluetooth.setClickable(true);
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 58);
		hint.setVisibility(View.GONE);
		bpm.setVisibility(View.GONE);
		function.setVisibility(View.GONE);
		rlStart.setVisibility(View.GONE);
		roundBackground.setImageResource(R.drawable.round_background_1);
		connected = false;
		needRecord = false;
		AudioPlayer.getInstance().play();
		scanedDevices.clear();
	}

	private void onConnectingUI() {
		tvBluetooth.setText("连接中");
		tvBluetooth.setClickable(false);
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
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

	public String getDeviceName() {
//		return "IHB2LD1X7CUC"; //IHB2LD1X7CUC   IHB2LC9JUHPB
//		return "IHB2LC9P2UUZ"; //IHB2LD1X7CUC   IHB2LC9JUHPB
		String serialnum = "";
		ServiceInfo serviceInfo = SPUtil.getServiceInfo();
		if (serviceInfo != null) {
			serialnum = serviceInfo.getSerialnum();
		}
		return serialnum;
		//  TODO: 15/9/7 请求网络
	}

	public void onEventMainThread(MonitorTerminateEvent event) {
		int reason = event.getEvent();
		//断开连接 保存音频数据 保存胎心数据
		AudioPlayer.getInstance().release();
		pseudoBluetoothService.stop();
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		resetUI();
		Intent intent = new Intent(getActivity(), GuardianStateActivity.class);
		intent.putExtra(Constants.INTENT_UUID, uuidString);
		switch (reason) {
			case MonitorTerminateEvent.EVENT_AUTO:
				LogUtil.d(TAG, "EVENT_AUTO");
				record();
				startActivity(intent);
				break;
			case MonitorTerminateEvent.EVENT_UNKNOWN:
				LogUtil.d(TAG, "EVENT_UNKNOWN");
				record();
				startActivity(intent);
				break;
			case MonitorTerminateEvent.EVENT_MANUAL:
				LogUtil.d(TAG, "EVENT_MANUAL");
				record();
				startActivity(intent);
				break;
			case MonitorTerminateEvent.EVENT_MANUAL_NOT_START:
				break;
			default:
				break;
		}
	}

	private void record() {
		File tempFile = new File(FileUtil.getVoiceDir(getActivity()), "TEMP");
		File file = new File(FileUtil.getVoiceDir(getActivity()), uuidString);
		if (tempFile.renameTo(file)) {
			tempFile.delete();
		}
		FileUtil.addFileHead(file);
		Gson gson = new Gson();
		String fhrString = gson.toJson(DataStorage.fhrs);
		RecordData recordData = new RecordData();
		Data data = new Data();
		data.setInterval(500);
		data.setHeartRate(fhrString);
		data.setTime(testTime.getTime());
		recordData.setData(data);
		String dataString = gson.toJson(recordData);
		DataDao dao = DataDao.getInstance(getActivity());
		MyAdviceItem adviceItem = new MyAdviceItem();
		AdviceForm adviceForm = WeiTaiXinApplication.getInstance().adviceForm;
		//
		adviceItem.setUserid(user.getId());
		adviceItem.setUploadstate(MyAdviceItem.NATIVE_RECORD);
		adviceItem.setPath(FileUtil.getVoiceDir(getActivity()).getPath());
		adviceItem.setTestTime(testTime);
		// TODO: 15/9/11 实际时间
		adviceItem.setTestTimeLong(DataStorage.fhrs.size() * 500);
		adviceItem.setFeeling(adviceForm.getFeeling());
		adviceItem.setPurpose(adviceForm.getAskPurpose());
		adviceItem.setGestationalWeeks(DateTimeTool.getGestationalWeeks(testTime));
		adviceItem.setJianceid(uuidString);
		adviceItem.setRdata(dataString);
		adviceItem.setSerialnum(user.getServiceInfo().getSerialnum());
		adviceItem.setUploadstate(MyAdviceItem.NATIVE_RECORD);
		//
		dao.add(adviceItem, true);
		MyAdviceItem aNative = dao.findNative(uuidString);
		LogUtil.d(TAG, aNative.toString());
	}
}
