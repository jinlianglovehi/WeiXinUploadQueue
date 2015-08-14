package cn.ihealthbaby.weitaixin.library.util;

/**
 * Created by liuhongjian on 15/7/27 16:04.
 */
public class TokenUtil {
	// TODO: 15/7/27 增加同步,确保线程安全 
	private static String accountToken;

	public static synchronized void saveToken(String accountToken) {
		TokenUtil.accountToken = accountToken;
	}

	public static synchronized String getAccountToken() {
		return accountToken;
	}
}
