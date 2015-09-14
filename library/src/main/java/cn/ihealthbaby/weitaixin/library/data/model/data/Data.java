package cn.ihealthbaby.weitaixin.library.data.model.data;

/**
 * Created by liuhongjian on 15/9/9 21:42.
 */
public class Data {
	private String heartRate;
	private String fm;
	private String afm;
	private String doctor;
	private int interval;
	private long time;

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

	public int getInterval() {
		return interval;

	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public long getTime() {
		return time;
	}
}
