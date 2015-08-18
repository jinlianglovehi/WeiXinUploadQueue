package cn.ihealthbaby.weitaixin.library.data.bluetooth.exception;

/**
 * 过期版本
 */
public class ParseExpireVersionException extends ParseVersionException {
    public ParseExpireVersionException() {
    }

    public ParseExpireVersionException(String detailMessage) {
        super(detailMessage);
    }

    public ParseExpireVersionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ParseExpireVersionException(Throwable throwable) {
        super(throwable);
    }

    public ParseExpireVersionException(int version, int type) {
        super(String.format("过期的版本号 version:%d,type %d", version, type));
    }
}
