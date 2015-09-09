package cn.ihealthbaby.weitaixin.ui.monitor;

/**
 * Created by liuhongjian on 15/9/9 13:31.
 */
public class DataStructor {
	private String v;
	private Data data;
	private Device device;
	private HostDevice hostDevice;

	public String getV() {
		return v;
	}

	public void setV(String v) {
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

	private class Data {
		private String heartRate;
		private String fm;
		private String afm;
		private String doctor;
		private String interval;
		private String time;

		public String getHeartRate() {
			return heartRate;
		}

		public void setHeartRate(String heartRate) {
			this.heartRate = heartRate;
		}

		public String getAfm() {
			return afm;
		}

		public void setAfm(String afm) {
			this.afm = afm;
		}

		public String getDoctor() {
			return doctor;
		}

		public void setDoctor(String doctor) {
			this.doctor = doctor;
		}

		public String getFm() {
			return fm;
		}

		public void setFm(String fm) {
			this.fm = fm;
		}

		public String getInterval() {
			return interval;
		}

		public void setInterval(String interval) {
			this.interval = interval;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}
	}

	private class Device {
		private String sn;
		private String type;
		private String version;

		public String getSn() {
			return sn;
		}

		public void setSn(String sn) {
			this.sn = sn;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}

	private class HostDevice {
		private String deviceId;
		private String type;
		private String os;
		private String imei;
		private String softVersion;

		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}

		public String getImei() {
			return imei;
		}

		public void setImei(String imei) {
			this.imei = imei;
		}

		public String getOs() {
			return os;
		}

		public void setOs(String os) {
			this.os = os;
		}

		public String getSoftVersion() {
			return softVersion;
		}

		public void setSoftVersion(String softVersion) {
			this.softVersion = softVersion;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
}
