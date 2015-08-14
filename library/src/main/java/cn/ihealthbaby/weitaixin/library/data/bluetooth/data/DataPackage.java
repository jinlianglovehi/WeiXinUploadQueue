package cn.ihealthbaby.weitaixin.library.data.bluetooth.data;

/**
 * Created by liuhongjian on 15/7/17 12:54.
 */
public class DataPackage implements Cloneable {
	protected String version;
	protected long time;

	public DataPackage() {
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("DataPackage{");
		sb.append("time=").append(time);
		sb.append(", version='").append(version).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
