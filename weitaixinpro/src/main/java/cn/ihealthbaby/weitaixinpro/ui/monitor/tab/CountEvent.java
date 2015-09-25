package cn.ihealthbaby.weitaixinpro.ui.monitor.tab;

/**
 * Created by liuhongjian on 15/9/25 20:33.
 */
public class CountEvent {
	public static final int TYPE_MONITORING = 2;
	public static final int TYPE_UNMONITOR = 1;
	private int type;
	private int count;

	CountEvent(int type, int count) {
		this.type = type;
		this.count = count;
	}

	public long getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
