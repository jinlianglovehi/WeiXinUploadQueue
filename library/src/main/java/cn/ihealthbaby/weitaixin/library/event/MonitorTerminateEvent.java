package cn.ihealthbaby.weitaixin.library.event;

/**
 * Created by liuhongjian on 15/9/10 09:52.
 */
public class MonitorTerminateEvent {
	public static final int EVENT_AUTO = 1;
	public static final int EVENT_UNKNOWN = 2;
	public static final int EVENT_MANUAL = 3;
	public static final int EVENT_MANUAL_NOT_START = 4;
	public static final int EVENT_MANUAL_CANCEL = 5;
	private int event;

	public MonitorTerminateEvent() {
	}

	public MonitorTerminateEvent(int event) {
		this.event = event;
	}

	public int getEvent() {
		return event;
	}

	public void setEvent(int event) {
		this.event = event;
	}
}
