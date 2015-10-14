package cn.ihealthbaby.weitaixin.library.event;

/**
 * Created by liuhongjian on 15/10/12 23:06.
 */
public class MonitorStartEvent {
	private String localRecordId;

	public MonitorStartEvent() {
	}

	public String getLocalRecordId() {
		return localRecordId;
	}

	public void setLocalRecordId(String localRecordId) {
		this.localRecordId = localRecordId;
	}
}
