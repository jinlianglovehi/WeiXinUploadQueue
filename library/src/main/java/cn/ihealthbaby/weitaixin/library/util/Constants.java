package cn.ihealthbaby.weitaixin.library.util;

import java.util.UUID;

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
	//内网
//	public static final String SERVER_URL = "http://192.168.1.253:8080/port/v1/";
	//外网
	public static final String SERVER_URL = "http://dev.ihealthbaby.cn:8280/v1/";
//	public static final String SERVER_URL = "http://192.168.1.38:8080/ihealthbaby-port/v1/";
	public static final String MOCK_SERVER_URL = "http://localhost:9800/";
	public static final String MIME_TYPE_WAV = "audio/x-wav";
	public static final String MIME_TYPE_JPEG = "image/jpeg";
	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ_FETAL_DATA = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_CANNOT_CONNECT = 5;
	public static final int MESSAGE_CONNECTION_LOST = 6;
	public static final int MESSAGE_VOICE = 7;
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final UUID COMMON_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final String RECORD_PATH = "";
	public static final String TEMP_FILE_NAME = "TEMP";
	public static final String EXTENTION_NAME = ".WAV";
	public static final String INTENT_UUID = "UUID";
	public static final String INTENT_CONSUMED_TIME = "CONSUMEDTIME";
	public static final String INTENT_DURATION = "DURATION";
	public static final String INTENT_INTERVAL = "INTERVAL";
}
