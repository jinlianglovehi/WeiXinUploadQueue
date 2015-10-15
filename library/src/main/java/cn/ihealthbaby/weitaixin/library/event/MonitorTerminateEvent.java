package cn.ihealthbaby.weitaixin.library.event;

/**
 * Created by liuhongjian on 15/9/10 09:52.
 */
public class MonitorTerminateEvent {
	/**
	 * 未知原因
	 */
	public static final int EVENT_UNKNOWN = -1;
	/**
	 * 自动结束
	 */
	public static final int EVENT_AUTO = 1;
	/**
	 * 手动结束
	 */
	public static final int EVENT_MANUAL = 2;
	/**
	 * 用户取消,未开始监测
	 */
	public static final int EVENT_MANUAL_CANCEL_NOT_START = 3;
	/**
	 * 用户取消,已开始监测
	 */
	public static final int EVENT_MANUAL_CANCEL_STARTED = 4;
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
