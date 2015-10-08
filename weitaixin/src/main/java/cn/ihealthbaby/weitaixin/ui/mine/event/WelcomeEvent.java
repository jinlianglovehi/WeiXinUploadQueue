package cn.ihealthbaby.weitaixin.ui.mine.event;

public class WelcomeEvent {

	private String mMsg;

	public WelcomeEvent() {
	}

	public WelcomeEvent(String msg) {
		mMsg = msg;
	}

	public String getMsg(){
		return mMsg;
	}

}