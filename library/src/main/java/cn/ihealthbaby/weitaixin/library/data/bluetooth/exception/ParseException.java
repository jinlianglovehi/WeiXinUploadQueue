package cn.ihealthbaby.weitaixin.library.data.bluetooth.exception;

/**
 * Created by liuhongjian on 15/8/11 20:23.
 */
public class ParseException extends Exception {
	public ParseException() {
	}

	public ParseException(String detailMessage) {
		super(detailMessage);
	}

	public ParseException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ParseException(Throwable throwable) {
		super(throwable);
	}
}
