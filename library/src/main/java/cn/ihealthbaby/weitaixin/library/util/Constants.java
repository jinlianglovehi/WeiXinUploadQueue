package cn.ihealthbaby.weitaixin.library.util;

import cn.ihealthbaby.weitaixin.library.BuildConfig;

/**
 * Created by Think on 2015/6/16.
 */
public class Constants {
	public static final String TAI_XIN_YI = "TaiXinYi";
	/**
	 * 应用模式，暂时只区分发布和调试模式
	 */
	public static final boolean MODE_RELEASE = !BuildConfig.DEBUG;
	public static final boolean MODE_DEBUG = BuildConfig.DEBUG;
	public static final boolean MODE = MODE_DEBUG;
	public static final boolean MODE_LOG = MODE;
	public static final boolean MODE_TOAST = MODE;
//	public static final String SERVER_URL = "http://192.168.1.253:8080/port/v1/";
	public static final String SERVER_URL = "http://192.168.1.38:8080/ihealthbaby-port/v1/";
	public static final String MOCK_SERVER_URL = "http://localhost:9800/";
	public static final String MIME_TYPE_WAV = "audio/x-wav";
	public static final String MIME_TYPE_JPEG = "image/jpeg";
}
