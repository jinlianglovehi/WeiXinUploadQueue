package cn.ihealthbaby.weitaixin.ui;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

import cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp.BluetoothListener;

/**
 * Created by liuhongjian on 15/8/12 19:12.
 */
public abstract class AbstractBluetoothListener implements BluetoothListener {
	@Override
	public void onLocalNameChanged(String localName) {
	}

	@Override
	public void onRemoteNameChanged(BluetoothDevice remoteDevice, String remoteName) {
	}

	@Override
	public void remoteClassChanged(BluetoothDevice remoteDevice, BluetoothClass bluetoothClass) {
	}
}
