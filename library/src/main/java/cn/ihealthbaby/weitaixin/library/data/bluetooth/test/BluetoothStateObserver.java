package cn.ihealthbaby.weitaixin.library.data.bluetooth.test;

import java.util.Observable;

/**
 * 暂时先不使用
 * <p>
 * Created by liuhongjian on 15/7/1107:35.
 */
public class BluetoothStateObserver extends Observable {
	/**
	 * Constructs a new {@code Observable} object.
	 */
	public static final int ACTION_TURN_ON_BLUETOOTH = 1;
	public static final int ACTION_TURN_OFF_BLUETOOTH = 1;
	public static final int ACTION_START_SEARCH_BLUETOOTH = 1;
	public static final int ACTION_STOP_SEARCH_BLUETOOTH = 1;
	public static final int STATE_BLUETOOTH_TURNED_ON = 1;
	public static final int STATE_BLUETOOTH_TURNED_OFF = 1;
	public static final int STATE_START_SEARCH = 1;
	public static final int STATE_STOP_SEARCH = 1;
	public static final int STATE_DEVICE_NOT_SUPPORT_BLUETOOTH = -2;
	public static final int STATE_OPEN_BLUETOOTH_FAIL = -1;
	public static final int STATE_OPEN_BLUETOOTH_SUCCESS = 0;
	public static final int STATE_START_TO_SEARCH_DEVICE = 1;
	public static final int STATE_SEARCH_DEVICE_FINISH = 2;
	public static final int STATE_CONNECT_DEVICE_SUCCESS = 3;
	public static final int STATE_CONNECT_DEVICE_FAIL = 4;
	public static final int STATE_START_TO_READ_BLUETOOTH_DATA = 5;
	private int state;

	public BluetoothStateObserver() {
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		setChanged();
		notifyObservers(state);
	}
}
