package cn.ihealthbaby.weitaixin.library.data.bluetooth.exception;

/**
 * 错误的版本
 * Created by liuhongjian on 15/7/20 11:50.
 */
public class ParseVersionException extends ParseException {
    public ParseVersionException() {
    }

    public ParseVersionException(String detailMessage) {
        super(detailMessage);
    }

    public ParseVersionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ParseVersionException(Throwable throwable) {
        super(throwable);
    }
}
