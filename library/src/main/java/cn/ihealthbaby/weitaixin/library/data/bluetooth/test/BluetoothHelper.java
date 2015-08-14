package cn.ihealthbaby.weitaixin.library.data.bluetooth.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothHelper extends BroadcastReceiver {
	private static final int SIGNAL_STATE_DISCOVERY_FINISHED = -1;
	private static final int SIGNAL_STOP_DISCOVERY = 1;
	private static final int SIGNAL_START_DISCOVERY = 1;
	private static final int SIGNAL_FOUND = 1;
	private static final int SIGNAL_STATE_DISCONNECT = 1;
	private static final int SIGNAL_CONNECTED = 1;
	private static final int SIGNAL_CONNECT_FAILED = 1;
	private static final int SIGNAL_DATA = 1;
	private static final int SIGNAL_DISCONNECT_BY_HAND = 1;
	private static final String UUID_STRING = "";
	private final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	private int signal;
	private Context context;
	private Handler handler;
	private List<String> devices = new ArrayList<>();
	private BluetoothSocket socket;
	private ConnectThread connectThread;

	/**
	 * @param context
	 * @param handler
	 */
	public BluetoothHelper(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	public boolean supportBluetooth() {
		return adapter == null;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		switch (action) {
			case BluetoothDevice.ACTION_FOUND:
				BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME);
				devices.add(device.getName());
				setSignal(SIGNAL_FOUND);
				break;
			case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
				setSignal(SIGNAL_STATE_DISCOVERY_FINISHED);
				break;
			//状态改变时获取之前的状态
			case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
				int previousConnectionState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, -1);
				switch (previousConnectionState) {
					//连接
					case BluetoothAdapter.STATE_CONNECTED:
						//交给业务判断如何做后续处理.比如是否需要重连.
						setSignal(SIGNAL_STATE_DISCONNECT);
						break;
					//正在连接中
					case BluetoothAdapter.STATE_CONNECTING:
						break;
					//未连接
					case BluetoothAdapter.STATE_DISCONNECTED:
						break;
					//正在断开连接
					case BluetoothAdapter.STATE_DISCONNECTING:
						break;
					default:
						break;
				}
				int connectionState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
				switch (connectionState) {
				}
			default:
				break;
		}
	}

	public synchronized int getSignal() {
		return signal;
	}

	public synchronized void setSignal(int signal) {
		this.signal = signal;
		handler.obtainMessage(signal).sendToTarget();
	}

	public void turnOn() {
		if (!isOn()) {
			adapter.enable();
		}
	}

	/**
	 * 开启为异步操作,需要在业务中轮询开启状态
	 *
	 * @return
	 */
	public boolean isOn() {
		return supportBluetooth() && !adapter.isEnabled();
	}

	public void startDiscovery() {
		stopDiscovery();
		adapter.startDiscovery();
		setSignal(SIGNAL_START_DISCOVERY);
	}

	private boolean isDiscovering() {
		if (supportBluetooth()) {
			return adapter.isDiscovering();
		} else {
			return false;
		}
	}

	private void stopDiscovery() {
		if (isDiscovering()) {
			adapter.cancelDiscovery();
			setSignal(SIGNAL_STOP_DISCOVERY);
		}
	}

	public void stopConnection(BluetoothDevice device) {
		if (isConnected() && socket != null) {
			try {
				socket.close();
//                主动断开连接
				setSignal(SIGNAL_DISCONNECT_BY_HAND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isConnected() {
		if (supportBluetooth()) {
			return SIGNAL_CONNECTED == getSignal();
		}
		return false;
	}

	public BluetoothSocket startConnection(BluetoothDevice device) {
		stopDiscovery();
		if (connectThread == null) {
			connectThread = new ConnectThread(device);
		}
		connectThread.start();
		return socket;
	}

	private void readData() {
//        if (!isConnected()) {
//            return;
//        }
//        try {
//            InputStream inputStream = socket.getInputStream();
//            byte[] buffer = new byte[1024];
//            IParser packageParser = new PackageParser();
//
//            while (inputStream.read(buffer) != -1) {
//                PackageEntity packageEntity = packageParser.parse(buffer);
//                //业务处理解析出来的packageEntity
//                Message message = handler.obtainMessage(SIGNAL_DATA);
//                message.obj = packageEntity;
//                message.sendToTarget();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
	}

	class ConnectThread extends Thread {
		private final BluetoothDevice device;

		public ConnectThread(BluetoothDevice device) {
			this.device = device;
		}

		@Override
		public void run() {
			String uuidString = UUID_STRING;
			UUID uuid = UUID.fromString(uuidString);
			try {
				socket = device.createRfcommSocketToServiceRecord(uuid);
				socket.connect();
				setSignal(SIGNAL_CONNECTED);
			} catch (IOException e) {
				e.printStackTrace();
				setSignal(SIGNAL_CONNECT_FAILED);
			}
		}
	}
}

