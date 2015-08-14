package cn.ihealthbaby.weitaixin.library.data.net;

/**
 * Created by liuhongjian on 15/7/22 22:48.
 */
public interface Business<T> {
	void handleData(T data) throws Exception;
}
