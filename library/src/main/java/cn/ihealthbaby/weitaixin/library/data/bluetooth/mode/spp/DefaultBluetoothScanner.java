package cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by liuhongjian on 15/8/5 19:59.
 */
public class DefaultBluetoothScanner implements BluetoothScanner {
	private BluetoothAdapter adapter;

	public DefaultBluetoothScanner(BluetoothAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void enable() {
		adapter.enable();
	}

	@Override
	public boolean isEnable() {
		return adapter.isEnabled();
	}

	@Override
	public void discovery() {
		adapter.startDiscovery();
	}

	@Override
	public void cancleDiscovery() {
		adapter.cancelDiscovery();
	}

	@Override
	public void disable() {
		adapter.disable();
	}

	@Override
	public boolean isDiscovering() {
		return adapter.isDiscovering();
	}
}
