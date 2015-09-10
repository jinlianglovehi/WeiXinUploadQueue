package cn.ihealthbaby.weitaixin.library.event;

/**
 * Created by liuhongjian on 15/9/10 10:36.
 */
public class RecordSoundEvent {
	private boolean record;

	public RecordSoundEvent(boolean record) {
		this.record = record;
	}

	public boolean isRecord() {
		return record;
	}

	public void setRecord(boolean record) {
		this.record = record;
	}
}
