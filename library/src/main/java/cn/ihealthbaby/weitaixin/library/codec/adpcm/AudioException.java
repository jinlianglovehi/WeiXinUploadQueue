package cn.ihealthbaby.weitaixin.library.codec.adpcm;

/**
 * Created by liuhongjian on 15/8/13 14:30.
 */
public class AudioException extends Exception {
	public AudioException() {
	}

	public AudioException(String detailMessage) {
		super(detailMessage);
	}

	public AudioException(Throwable throwable) {
		super(throwable);
	}
}
