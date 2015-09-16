package cn.ihealthbaby.weitaixin.ui.pay.event;

public class PayChooseAreasEvent {

	private String mMsg;

	public PayChooseAreasEvent() {
	}

	public PayChooseAreasEvent(String msg) {
		mMsg = msg;
	}

	public String getMsg(){
		return mMsg;
	}

}