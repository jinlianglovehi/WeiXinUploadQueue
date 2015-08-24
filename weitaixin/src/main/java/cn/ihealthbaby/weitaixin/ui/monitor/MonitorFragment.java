package cn.ihealthbaby.weitaixin.ui.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ihealthbaby.weitaixin.R;
import cn.ihealthbaby.weitaixin.base.BaseFragment;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.data.FHRPackage;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothReceiver;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothScanner;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.DefaultBluetoothScanner;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.PseudoBluetoothService;
import cn.ihealthbaby.weitaixin.library.data.bluetooth.test.Constants;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.ToastUtil;
import cn.ihealthbaby.weitaixin.ui.AbstractBluetoothListener;

/**
 * Created by liuhongjian on 15/8/12 17:52.
 */
public class MonitorFragment extends BaseFragment {
	@Bind(R.id.round_frontground)
	ImageView roundFrontground;
	@Bind(R.id.round_background)
	ImageView roundBackground;
	//	@Bind(R.id.round_progress_mask)
	//	RoundMaskView roundProgressMask;
	@Bind(R.id.bpm)
	ImageView bpm;
	@Bind(R.id.hint)
	TextView hint;
	@Bind(R.id.tv_record)
	TextView tvRecord;
	@Bind(R.id.btn_start)
	ImageView btn;
	@Bind(R.id.tv_bluetooth)
	TextView tvBluetooth;
	@Bind(R.id.rl_start)
	RelativeLayout rlStart;
	private BluetoothReceiver bluetoothReceiver;
	private BluetoothAdapter adapter;
	private BluetoothScanner bluetoothScanner;
	private Set<BluetoothDevice> bondedDevices;
	private PseudoBluetoothService pseudoBluetoothService;
	private ArrayList<BluetoothDevice> scanedDevices;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constants.MESSAGE_STATE_CHANGE:
					switch (msg.arg1) {
						case PseudoBluetoothService.STATE_CONNECTED:
							onConnectedUI();
//							startActivity(new Intent(getActivity().getApplicationContext(), MonitorActivity.class));
							break;
						case PseudoBluetoothService.STATE_CONNECTING:
							break;
						case PseudoBluetoothService.STATE_LISTEN:
							break;
						case PseudoBluetoothService.STATE_NONE:
							break;
					}
					break;
				case Constants.MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					break;
				case Constants.MESSAGE_READ:
					FHRPackage fhrPackage = (FHRPackage) msg.obj;
					tvBluetooth.setText(String.valueOf(fhrPackage.getFHR1()));
					break;
				case Constants.MESSAGE_DEVICE_NAME:
					// save the connected device's name
					String deviceName = msg.getData().getString(Constants.DEVICE_NAME);
					break;
				case Constants.MESSAGE_TOAST:
					break;
			}
		}
	};
	private String deviceName;

	public void onConnectedUI() {
		rlStart.setVisibility(View.VISIBLE);
		tvBluetooth.setText("--");
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 88);
		bpm.setImageResource(R.drawable.bpm_red);
		hint.setVisibility(View.GONE);
	}

	/**
	 * 1.开启蓝牙  2.匹配绑定的设备,连接设备  3.扫描 4.匹配扫描到的设备
	 *
	 * @param view
	 */
	@OnClick(R.id.round_frontground)
	void startSearch(View view) {
		onConnectingUI();
		if (!bluetoothScanner.isEnable()) {
			bluetoothScanner.enable();
		} else {
			LogUtil.e("bluetoothScanner","bluetoothScanner");
			connectBondedDeviceOrSearch();
		}
	}

	private void onConnectingUI() {
		tvBluetooth.setText("连接中");
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
//		AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.)
		hint.setVisibility(View.VISIBLE);
		bpm.setImageResource(R.drawable.bpm_dark);
		bpm.setVisibility(View.VISIBLE);
		roundBackground.setImageResource(R.drawable.round_background_2);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_monitor, null);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		adapter = BluetoothAdapter.getDefaultAdapter();
		bluetoothScanner = new DefaultBluetoothScanner(adapter);
		scanedDevices = new ArrayList<>();
		pseudoBluetoothService = new PseudoBluetoothService(getActivity().getApplicationContext(), handler);
		bluetoothReceiver = new BluetoothReceiver();
		bluetoothReceiver.register(getActivity().getApplicationContext());
		bluetoothReceiver.setListener(new AbstractBluetoothListener() {
			@Override
			public void onFound(BluetoothDevice remoteDevice, String remoteName, short rssi, BluetoothClass bluetoothClass) {
				if (!scanedDevices.contains(remoteDevice)) {
					if (getDeviceName().equalsIgnoreCase(remoteDevice.getName())) {
						pseudoBluetoothService.connect(remoteDevice, false);
						bluetoothScanner.cancleDiscovery();
					}
					scanedDevices.add(remoteDevice);
				}
			}

			@Override
			public void onConnect(BluetoothDevice remoteDevice) {
				// TODO: 15/8/13 此处触发与期望不一致
//				startActivity(new Intent(getActivity().getApplicationContext(), MonitorActivity.class));
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

	public void resetUI() {
		tvBluetooth.setText("start");
		tvBluetooth.setTextSize(TypedValue.COMPLEX_UNIT_SP, 58);
		hint.setVisibility(View.GONE);
		bpm.setVisibility(View.GONE);
		roundBackground.setImageResource(R.drawable.round_background_1);
	}

	public void connectBondedDeviceOrSearch() {
		bondedDevices = adapter.getBondedDevices();
		LogUtil.e("bluetoothScanner",""+bondedDevices.size());
		if (bondedDevices != null && bondedDevices.size() > 0) {
			for (BluetoothDevice device : bondedDevices) {
				LogUtil.e("bluetoothScanner","devicegetName: "+device.getName());
				if (getDeviceName().equalsIgnoreCase(device.getName())) {
					LogUtil.e("bluetoothScanner",""+"pseudoBluetoothService.connect()");
					pseudoBluetoothService.connect(device, false);
				}
			}
		} else if (!bluetoothScanner.isDiscovering()) {
			bluetoothScanner.discovery();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		bluetoothReceiver.unRegister(getActivity().getApplicationContext());
		ButterKnife.unbind(this);
	}

	public String getDeviceName() {
		// TODO: 15/8/14 登录之后需要保存用户绑定的设备信息
		return "IHB2LD1X7CUC"; //IHB2LD1X7CUC   IHB2LC9JUHPB
	}


}



