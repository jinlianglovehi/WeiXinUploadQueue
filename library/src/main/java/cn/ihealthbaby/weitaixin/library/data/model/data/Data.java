package cn.ihealthbaby.weitaixin.library.data.model.data;

import java.util.List;

/**
 * Created by liuhongjian on 15/9/9 21:42.
 */
public class Data {
	private List<Integer> heartRate;
	private List<Long> fm;
	private List<Long> afm;
	private List<Long> doctor;
	private int interval;
	private long time;

	public List<Integer> getHeartRate() {
		return heartRate;
	}

	public List<Long> getAfm() {
		return afm;
	}

	public void setAfm(List<Long> afm) {
		this.afm = afm;
	}

	public List<Long> getDoctor() {
		return doctor;
	}

	public void setDoctor(List<Long> doctor) {
		this.doctor = doctor;
	}

	public List<Long> getFm() {
		return fm;
	}

	public void setFm(List<Long> fm) {
		this.fm = fm;
	}

	public void setHeartRate(List<Integer> heartRate) {
		this.heartRate = heartRate;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
