package cn.ihealthbaby.weitaixin.ui.pay.event;

public class PayEvent {

	private String mMsg;

	public PayEvent() {
	}

	public PayEvent(String msg) {
		mMsg = msg;
	}

	public String getMsg(){
		return mMsg;
	}

}