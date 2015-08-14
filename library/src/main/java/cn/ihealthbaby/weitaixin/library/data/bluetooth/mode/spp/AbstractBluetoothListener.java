package cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * Created by liuhongjian on 15/8/5 21:14.
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
