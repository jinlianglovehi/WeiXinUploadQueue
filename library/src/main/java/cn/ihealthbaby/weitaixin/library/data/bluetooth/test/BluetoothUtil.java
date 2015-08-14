package cn.ihealthbaby.weitaixin.library.data.bluetooth.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;

import java.util.ArrayList;

/**
 * 蓝牙工具类，实现蓝牙通信需要四个步骤1.打开蓝牙，2.搜索蓝牙（配对），3.连接，4.传输数据。
 * 
 * @author liuxiaolong
 * 
 */
@SuppressLint("NewApi")
public 	abstract  class BluetoothUtil extends BroadcastReceiver {
	private static BluetoothUtil searchBluetooth;
	private Activity context;
	 private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private BluetoothStateListener bluetoothStateListener;
	private ArrayList<BluetoothDevice> searchDevices = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> bondedDevices = new ArrayList<BluetoothDevice>();
	private boolean dataTransmissionOff = true;
	private BluetoothSocket bluetoothSocket;
//	private ByteEntity byteEntity;
//	private int i;
//	private DataQueue dataQueue;
//	AudioPlayer myAudioTrack;
//	private String fileName = "test";
//	private InputStream connectInputStream;

//	private BluetoothUtil(Activity context) {
//		this.context = context;
//		dataQueue = DataQueue.getInstance();
//		myAudioTrack = AudioPlayer.getInstance();
//	}
//
//	public static BluetoothUtil getInstance(Activity context) {
//		if (searchBluetooth == null) {
//			searchBluetooth = new BluetoothUtil(context);
//		}
//		return searchBluetooth;
//	}
//
//	/**
//	 * 设置蓝牙状态回调。
//	 *
//	 * @param bluetoothStateListener
//	 */
//	public void setBluetoothStateListener(
//			BluetoothStateListener bluetoothStateListener) {
//		this.bluetoothStateListener = bluetoothStateListener;
//	}
//
//	public void setfileName(String fileName) {
//		this.fileName = fileName;
//	}
//
//	public String getfileName() {
//		return fileName;
//	}
//
//	/**
//	 * 打开蓝牙。
//	 */
//	public void openBluetooth() {
//		if (mBluetoothAdapter == null) {
//			if (bluetoothStateListener != null) {
//				bluetoothStateListener.connectState(BluetoothStateListener.STATE_DEVICE_NOT_SUPPORT_BLUETOOTH);
//			}
//		} else {
//			if (!mBluetoothAdapter.isEnabled()) {
//				// Intent enableBtIntent = new Intent(
//				// BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				// context.startActivityForResult(enableBtIntent, 3);
//				mBluetoothAdapter.enable();
//				observationBluetoothOff();
//			} else {
//				if (bluetoothStateListener != null) {
//					bluetoothStateListener.connectState(BluetoothStateListener.STATE_OPEN_BLUETOOTH_SUCCESS);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 关闭蓝牙。
//	 */
//	public void closeBluetooth() {
//		mBluetoothAdapter.disable();
//	}
//
//	/**
//	 * 开始搜索蓝牙设备。
//	 */
//	public void startSearch() {
//		searchCanPairDevices();
//		bondedDevices.addAll(mBluetoothAdapter.getBondedDevices());
//	}
//
//	/**
//	 * 结束搜索蓝牙设备。
//	 */
//	public void stopSearch() {
//		context.unregisterReceiver(this);
//		mBluetoothAdapter.cancelDiscovery();
//		bondedDevices.clear();
//	}
//
//	/**
//	 * 开始连接胎心设备
//	 *
//	 * @param deviceName
//	 * @return
//	 */
//	public boolean startBluetoothSocket(String deviceName) {
//		boolean result = false;
//		//更改AudioTrack.init()，在构造时内部调用
////		myAudioTrack.init();
//		//修复空指针异常
//		if (bondedDevices != null && bondedDevices.size() > 0) {
//			for (BluetoothDevice device : bondedDevices) {
//				//			lvxin connect device with device addr
//				//			if (device.getAddress().equalsIgnoreCase(deviceName)) {
//				if (device.getName().equalsIgnoreCase(deviceName)) {
//					openDataTransmission();
//					result = true;
//					connectJudge(device);
//					return result;
//				}
//			}
//		}
//		//修复空指针异常
//		if (searchBluetooth != null && searchDevices.size() > 0) {
//			for (BluetoothDevice device : searchDevices) {
//				//lvxin connect device with device addr
//				//			if (device.getAddress().equalsIgnoreCase(deviceName)) {
//
//				//TODO 可能存在问题 device可能为空
//				if (device.getName().equalsIgnoreCase(deviceName)) {
//					openDataTransmission();
//					result = true;
//					connectJudge(device);
//					return result;
//				}
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * 关闭蓝牙数据传输连接。
//	 */
//	public void stopBluetoothSocket() {
//		closeDataTransmission();
//		if (bluetoothSocket != null) {
//			if (bluetoothSocket.isConnected()) {
//				try {
//					mBluetoothAdapter.disable();
//					bluetoothSocket.close();
//					myAudioTrack.release();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		// TODO Auto-generated method stub
//		String action = intent.getAction();
//		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//			BluetoothDevice device = intent
//					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//			searchDevices.add(device);
//			if (bluetoothStateListener != null) {
//				bluetoothStateListener.searchDeviceName(device.getName());
////				bluetoothStateListener.searchDeviceName(device.getAddress());
//			}
//		} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//			Log.i("lxl...log...onReceive", "无连接1");
//			if (bluetoothStateListener != null) {
//				bluetoothStateListener.connectState(BluetoothStateListener.STATE_SEARCH_DEVICE_FINISH);
//			}
//		} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
//			Log.i("lxl...log...onReceive", "无连接2");
//
//		} else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
//			Log.i("lxl...log...onReceive", "无连接3");
//
//		} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
//			Log.i("lxl...log...onReceive", "无连接4");
//		}else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
//			Log.i("lxl...log...onReceive", "连接状态改变");
//			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//			int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, -1);
//			switch (previousState) {
//				case BluetoothAdapter.STATE_CONNECTED:
//					Log.i("lxl...log...onReceive", "连接被断开");
//					break;
//				case BluetoothAdapter.STATE_CONNECTING:
//				case BluetoothAdapter.STATE_DISCONNECTED:
//				case BluetoothAdapter.STATE_DISCONNECTING:
//					break;
//				default:
//					break;
//			}
////
//		}
//		//注释掉无用的重复逻辑
////		else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
////			Log.i("lxl...log...onReceive", "无连接5");
////		}
//	}
//
//	/**
//	 * 轮询蓝牙是否打开。
//	 */
//	private void observationBluetoothOff() {
//		final Timer timer = new Timer();
//		TimerTask task = new TimerTask() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				Log.i("lxl...log...openBluetooth", "打开蓝牙中...");
//				if (mBluetoothAdapter.isEnabled()) {
//					Log.i("lxl...log...openBluetooth", "打开蓝牙成功");
//					if (bluetoothStateListener != null) {
//						bluetoothStateListener.connectState(BluetoothStateListener.STATE_OPEN_BLUETOOTH_SUCCESS);
//					}
//					timer.cancel();
//				}
//			}
//		};
//		timer.schedule(task, 0, 500);
//	}
//
//	/**
//	 * 关闭数据传输。
//	 */
//	private void closeDataTransmission() {
//		dataTransmissionOff = false;
//		if (connectInputStream != null) {
//			try {
//				connectInputStream.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * 打开数据传输。
//	 */
//	private void openDataTransmission() {
//		dataTransmissionOff = true;
//	}
//
//	/**
//	 * 搜索附近可以配对的设备。
//	 */
//	private void searchCanPairDevices() {
//		searchDevices.clear();
//		// 设置广播信息过滤
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
//		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//		intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
//		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//		// 注册广播接收器，接收并处理搜索结果
//		context.registerReceiver(this, intentFilter);
//		// 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
//		mBluetoothAdapter.startDiscovery();
//	}
//
//	/**
//	 * 连接特定的硬件。
//	 *
//	 * @param device
//	 */
//	private void connectJudge(BluetoothDevice device) {
//		if (device != null) {
//			switch (device.getBondState()) {
//			// 未配对
//			case BluetoothDevice.BOND_NONE:
//				// 配对
//				//test by lvxin
//				try {
//					// 连接
//					dataTransmission(device);
//				} catch (Exception e) {
//					e.printStackTrace();
//					closeDataTransmission();
//					if (bluetoothStateListener != null) {
//						bluetoothStateListener.connectState(BluetoothStateListener.STATE_OPEN_BLUETOOTH_SUCCESS);
//					}
//				}
//
//
////				try {
////					Method createBondMethod = BluetoothDevice.class
////							.getMethod("createBond");
////					createBondMethod.invoke(device);
////				} catch (Exception e) {
////					e.printStackTrace();
////					if (bluetoothStateListener != null) {
////						bluetoothStateListener.connectState(0);
////					}
////				}
//				break;
//			// 已配对
//			case BluetoothDevice.BOND_BONDED:
//				try {
//					// 连接
//					dataTransmission(device);
//				} catch (Exception e) {
//					e.printStackTrace();
//					closeDataTransmission();
//					if (bluetoothStateListener != null) {
//						bluetoothStateListener.connectState(BluetoothStateListener.STATE_OPEN_BLUETOOTH_SUCCESS);
//					}
//				}
//				break;
//				//add by lvxin at 08/22/2014
//			case BluetoothDevice.BOND_BONDING:
//				try {
//					// 连接
//					dataTransmission(device);
//				} catch (Exception e) {
//					e.printStackTrace();
//					closeDataTransmission();
//					if (bluetoothStateListener != null) {
//						bluetoothStateListener.connectState(BluetoothStateListener.STATE_OPEN_BLUETOOTH_SUCCESS);
//					}
//				}
//				break;
//			}
//		}
//	}
//
//	/**
//	 * 连接设备。
//	 *
//	 * @param device
//	 * @return
//	 * @throws Exception
//	 */
//	private BluetoothSocket connect(BluetoothDevice device) throws Exception {
//
//		// 固定的UUID
//		final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
//		UUID uuid = UUID.fromString(SPP_UUID);
//		//fix by lvxin
//		//createInsecureRfcommSocketToServiceRecord
//		BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
////		BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
//
//		if (socket != null) {
//			Log.i("lxl...log...BluetoothSocket", "连接设备成功");
//		} else {
//			Log.i("lxl...log...BluetoothSocket", "连接设备失败");
//		}
//		socket.connect();
//		return socket;
//	}
//
//	/**
//	 * 设备间数据传输。
//	 *
//	 * @throws Exception
//	 */
//	private void dataTransmission(final BluetoothDevice device) {
//		new Thread(new Runnable() {
//
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
////					Log.i("lvxin test bluetooth device -- ", device.getAddress());
////					Log.i("lvxin test bluetooth device -- ", device.getName());
////					Log.i("lvxin test bluetooth device -- ", new String());
//					bluetoothSocket = connect(device);
//					connectInputStream = bluetoothSocket.getInputStream();
//					if (bluetoothStateListener != null) {
//						bluetoothStateListener.connectState(BluetoothStateListener.STATE_START_TO_READ_BLUETOOTH_DATA);
//						receiveByte(connectInputStream);
//					}
////					receiveByte(connectInputStream);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					if (bluetoothStateListener != null) {
//						bluetoothStateListener.connectState(BluetoothStateListener.STATE_CONNECT_DEVICE_FAILED);
//					}
//				}
//			}
//		}).start();
//	}
//
//	/**
//	 * 接收胎心仪数据。
//	 *
//	 * @param tmpIn
//	 * @throws IOException
//	 */
//	private void receiveByte(InputStream tmpIn) throws IOException {
//		byte[] buffer = new byte[1];
//		byte[] buffer1 = new byte[1];
//		byte[] buffer2 = new byte[321];
////		while (dataTransmissionOff) {
//		//meizu mx3 zte u930hd coolpad5951
//		while (dataTransmissionOff) {
//			int a = tmpIn.read(buffer);
////			Log.i("lvxin test FHR new protocol ----- ", byte2HexStr(buffer));
//			packageJudge(tmpIn, buffer, buffer1, buffer2);
//		}
//	}
//
//	public static String byte2HexStr(byte[] b)
//	{
//	    String stmp="";
//	    StringBuilder sb = new StringBuilder("");
//	    for (int n=0;n<b.length;n++)
//	    {
//	        stmp = Integer.toHexString(b[n] & 0xFF);
//	        sb.append((stmp.length()==1)? "0"+stmp : stmp);
//	        sb.append(" ");
//	    }
//	    return sb.toString().toUpperCase().trim();
//	}
//
//
//	/**
//	 * 判断是心率包还是胎心声音包。
//	 *
//	 * @param tmpIn
//	 * @param buffer
//	 * @param buffer1
//	 * @param buffer2
//	 * @throws IOException
//	 */
//	private void packageJudge(InputStream tmpIn, byte[] buffer, byte[] buffer1,
//			byte[] buffer2) throws IOException {
//		if (CheckBluetoothPackage.CheckHeartRatePackage(buffer[0]) == ChackPackageState.CHECK_OK) {
//			taixinlvRead(tmpIn);
//		}else if(CheckBluetoothPackage.CheckHeartRateAdPackage(buffer[0]) == ChackPackageState.CHECK_OK){
//			//新通讯协议
//			taixinlvReadAd(tmpIn);
//		}else if(CheckBluetoothPackage.CheckHeartVoicePackage(buffer[0]) == ChackPackageState.CHECK_OK){
//			int[] a = getVoice(tmpIn);
//			if (ByteUtil.soundFormatCheck(a)) {
//				byte[] s = intForByte(ByteUtil.analysePackage(a));
//				myAudioTrack.playAudioTrack(s, 0, s.length);
//				if (!fileName.equals("")) {
//					JsonOrFileUtil.generateFile(RAMStorage.dataPath,
//							fileName + ".WAV", s);
//				}
//			}
//		}else{
//			if (CheckBluetoothPackage.CheckHeartVoiceAdPackage(buffer[0]) == ChackPackageState.CHECK_OK) {
//				int[] a = getVoiceAd(tmpIn);
//				byte[] s = intForByte(ByteUtil.anylyseData(a,1));
//				myAudioTrack.playAudioTrack(s, 0, s.length);
//				if (!fileName.equals("")) {
//					JsonOrFileUtil.generateFile(RAMStorage.dataPath,
//							fileName + ".WAV", s);
//				}
//			}
//		}
//	}
//
//	/**
//	 * int数组转成byte数组。
//	 *
//	 * @param ints
//	 * @return
//	 */
//	private byte[] intForByte(int[] ints) {
//		int size = ints.length;
//		byte[] shorts = new byte[size];
//		for (int i = 0; i < size; i++) {
//			shorts[i] = (byte) ints[i];
//		}
//		return shorts;
//	}
//
//	/**
//	 * 获取一个原始的胎声包。
//	 *
//	 * @param inputStream
//	 * @return
//	 */
//	private int[] getVoice(InputStream inputStream) {
//		int[] ints = new int[321];
//		for (int i = 0; i < 321; i++) {
//			try {
//				ints[i] = inputStream.read();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return ints;
//	}
//
//	private int[] getVoiceAd(InputStream inputStream) {
//		int[] ints = new int[101];
//		for (int i = 0; i < 101; i++) {
//			try {
//				ints[i] = inputStream.read();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return ints;
//	}
//
//	/**
//	 * 对每秒胎心率数据截取2个字节。
//	 *
//	 * @param tmpIn
//	 * @throws IOException
//	 */
//	private void taixinlvRead(InputStream tmpIn) throws IOException {
//		int[] is = new int[4];
//		for (int i = 0; i < 4; i++) {
//			is[i] = tmpIn.read();
//		}
////		String a = Integer.toHexString(is[0]) + Integer.toHexString(is[1]) + Integer.toHexString(is[2]);
////		Log.i("lvxin test FHR new protocol --- FHR", Integer.toHexString(is[0]));
////		Log.i("lvxin test FHR new protocol --- AFM", Integer.toHexString(is[1]));
////		Log.i("lvxin test FHR new protocol --- ST2", Integer.toHexString(is[2]));
//
//		Log.i("lvxin test FHR new protocol --- FHR Decimal--", Integer.toString(is[0]) + " Hexadecimal--" + Integer.toHexString(is[0]));
//		Log.i("lvxin test FHR new protocol --- AFM Decimal--", Integer.toString(is[1]) + " Hexadecimal--" + Integer.toHexString(is[1]));
//		Log.i("lvxin test FHR new protocol --- ST2 Decimal--", Integer.toString(is[2]) + " Hexadecimal--" + Integer.toHexString(is[2]));
//		Log.i("lvxin test FHR new protocol --- SUM Decimal--", Integer.toString(is[3]) + " Hexadecimal--" + Integer.toHexString(is[3]));
//
//		int count;
//		count = is[0] + is[1] + is[2];
//
//		if (count == is[3]) {
//			ByteEntity byteEntity = new ByteEntity();
//			byteEntity.setByte1(is[0]);
//			byteEntity.setTime(System.currentTimeMillis());
//
//			int taidong_auto = is[2]/4;
//			if (taidong_auto == 1)
//				byteEntity.setTaidong_auto(1);
//
//			RAMStorage.signal_strength = is[2]%4;
//
//			if (getByteEntity(byteEntity))
//			{
//				dataQueue.addHeartRatePackage(byteEntity);
//			}
//		}
//		else
//		{
//			return;
//		}
//
////		int b = verifyData(tmpIn);
////		if (b == -1) {
////			return;
////		} else {
////			ByteEntity byteEntity = new ByteEntity();
////			byteEntity.setByte1(b);
////			byteEntity.setTime(System.currentTimeMillis());
////			if (getByteEntity(byteEntity)) {
////				dataQueue.addHeartRatePackage(byteEntity);
////			}
////		}
//	}
//
//	private void taixinlvReadAd(InputStream tmpIn) throws IOException {
//		int count;
//		int[] is = new int[7];
//		for (int i = 0; i < 7; i++) {
//			is[i] = tmpIn.read();
//		}
//		Log.i("lvxin test FHR new protocol --- FHR", Integer.toHexString(is[0]));
//
//		/*
//		 * is0 FHR : the fetal heart rate . is1 AFM: fetal movement data is2
//		 * status2: BIT 1 ~ BIT 0：01表示信号质量差，10表示信号质量一般，11表示信号质量好 BIT2 → 1 means
//		 * there isa fetal movement manually( the marker is pressed onetime).
//		 * BIT 7 ~ BIT3 ：0
//		 */
//
//		ByteEntity byteEntity = new ByteEntity();
//		byteEntity.setByte1(is[0]);
//		byteEntity.setTime(System.currentTimeMillis());
//
//		int taidong = is[4] / 4;
//		if (taidong == 1){
//			byteEntity.setTaidong_auto(1);
//			Log.i("胎动--", "发生了一次自动胎动");
//		}
//
//		RAMStorage.signal_strength = is[4] % 4;
//
//		if (getByteEntity(byteEntity)) {
//			dataQueue.addHeartRatePackage(byteEntity);
//		}
//
//		return;
//
//	}
//
//	private int verifyData(InputStream tmpIn) throws IOException {
//		int count;
//		int[] is = new int[4];
//		for (int i = 0; i < 4; i++) {
//			is[i] = tmpIn.read();
//		}
//		count = is[0] + is[1] + is[2];
////		Log.i("lvxin test FHR new protocol --- FHR", Integer.toHexString(is[0]));
////		Log.i("lvxin test FHR new protocol --- AFM", Integer.toHexString(is[1]));
////		Log.i("lvxin test FHR new protocol --- STS", Integer.toHexString(is[2]));
//		if (count == is[3]) {
//			return is[0];
//		} else {
//			return -1;
//		}
//	}
//
//	/**
//	 * 判断字节是不是在同一秒传输了来的。
//	 *
//	 * @param by
//	 * @return
//	 */
//	private boolean getByteEntity(ByteEntity by) {
//		if (byteEntity == null) {
//			byteEntity = by;
//			return false;
//		} else {
//			if (JsonOrFileUtil.times(byteEntity.getTime()).equals(
//					JsonOrFileUtil.times(by.getTime()))) {
//				i++;
//				if (i == 1 || i == 2) {
//					return true;
//				} else {
//					return false;
//				}
//			} else {
//				i = 0;
//				byteEntity = by;
//				return false;
//			}
//		}
//	}

	public interface BluetoothStateListener {
		// -2设备不支持蓝牙功能，-1蓝牙打开失败，0蓝牙打开成功，
		// 1开始搜索设备，2完成搜索，3连接设备成功，
		// 4连接失败,5开始传输胎心数据。
		public static final int STATE_DEVICE_NOT_SUPPORT_BLUETOOTH = -2;
		public static final int STATE_OPEN_BLUETOOTH_FAILED = -1;
		public static final int STATE_OPEN_BLUETOOTH_SUCCESS = 0;
		public static final int STATE_START_TO_SEARCH_DEVICE = 1;
		public static final int STATE_SEARCH_DEVICE_FINISH = 2;
		public static final int STATE_CONNECT_DEVICE_SUCCESS = 3;
		public static final int STATE_CONNECT_DEVICE_FAILED =4;
		public static final int STATE_START_TO_READ_BLUETOOTH_DATA = 5;

		public void connectState(int state);

		// 搜索到的设备名字。
		public void searchDeviceName(String name);

	}

}