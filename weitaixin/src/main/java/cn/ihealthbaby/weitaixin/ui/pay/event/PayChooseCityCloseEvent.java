package cn.ihealthbaby.weitaixin.ui.pay.event;

public class PayChooseCityCloseEvent {

	private String mMsg;

	public PayChooseCityCloseEvent() {
	}

	public PayChooseCityCloseEvent(String msg) {
		mMsg = msg;
	}

	public String getMsg(){
		return mMsg;
	}

}