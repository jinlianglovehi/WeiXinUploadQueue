package cn.ihealthbaby.weitaixin.library.data.model.data;

/**
 * Created by liuhongjian on 15/9/9 13:31.
 */
public class RecordData {
	private int v;
	private Data data;
	private Device device;
	private HostDevice hostDevice;

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public HostDevice getHostDevice() {
		return hostDevice;
	}

	public void setHostDevice(HostDevice hostDevice) {
		this.hostDevice = hostDevice;
	}
}
