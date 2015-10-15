package cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * Created by liuhongjian on 15/8/5 19:14.
 */
public interface BluetoothListener {
	/**
	 * 发现新设备
	 *
	 * @param bluetoothClass
	 * @param remoteDevice
	 * @param remoteName
	 * @param rssi
	 */
	void onFound(BluetoothDevice remoteDevice, String remoteName, short rssi, BluetoothClass bluetoothClass);

	/**
	 * 设备建立连接
	 *
	 * @param remoteDevice
	 */
	void onConnect(BluetoothDevice remoteDevice);

	/**
	 * 设备断开连接
	 *
	 * @param remoteDevice
	 */
	void onDisconnect(BluetoothDevice remoteDevice);

	/**
	 * 蓝牙开启
	 */
	void onStateOn();

	/**
	 * 蓝牙关闭
	 */
	void onStateOFF();

	/**
	 * 请求开启蓝牙
	 */
	void onRequestBluetoothEnable();

	/**
	 * 搜索开始
	 */
	void onDiscoveryStarted();

	/**
	 * 搜索结束
	 */
	void onDiscoveryFinished();

	/**
	 * 本地名称更改
	 *
	 * @param localName
	 */
	void onLocalNameChanged(String localName);

	/**
	 * 远程设备名称更改
	 *
	 * @param remoteDevice
	 * @param remoteName
	 */
	void onRemoteNameChanged(BluetoothDevice remoteDevice, String remoteName);

	/**
	 * 远程设备等级改变
	 *
	 * @param remoteDevice
	 * @param bluetoothClass
	 */
	void remoteClassChanged(BluetoothDevice remoteDevice, BluetoothClass bluetoothClass);

	/**
	 * 手机显示配对请求,用于用户选择
	 *
	 * @param remoteDevice
	 * @param remoteName
	 * @param pairingKey
	 * @param pairingVariant
	 */
	void onPairingRequest(BluetoothDevice remoteDevice, String remoteName, String pairingKey, int pairingVariant);
}
