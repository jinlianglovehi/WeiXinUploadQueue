package cn.ihealthbaby.weitaixin.library.data.bluetooth.mode.spp;

/**
 * Created by liuhongjian on 15/8/12 17:33.
 */
public class BluetoothManager {
	private static BluetoothManager instance = new BluetoothManager();

	private BluetoothManager() {
	}

	public static BluetoothManager getInstance() {
		return instance;
	}


}
