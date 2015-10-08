package cn.ihealthbaby.weitaixin.ui.mine.event;

public class LogoutEvent {

	private String mMsg;

	public LogoutEvent() {
	}

	public LogoutEvent(String msg) {
		mMsg = msg;
	}

	public String getMsg(){
		return mMsg;
	}

}