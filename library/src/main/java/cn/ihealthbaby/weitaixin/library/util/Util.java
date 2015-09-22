package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cn.ihealthbaby.weitaixin.library.data.model.data.Device;
import cn.ihealthbaby.weitaixin.library.data.model.data.HostDevice;
import cn.ihealthbaby.weitaixin.library.data.model.data.RecordData;

public class Util {
	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static float getDensityDpi(Context context) {
		return context.getResources().getDisplayMetrics().densityDpi;
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dipToPixels(Context context, int dip) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
		return (int) px;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static float dip2pxF(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return dipValue * scale + 0.5f;
	}

	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	public static int getStatusBarHeight(Context context) {
		// Rect frame = new Rect();
		// activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// return frame.top;
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	/**
	 * 获取手机IP地址
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
//            LogUtil.log("getLocalIpAddress", "获取IP地址失败");
		}
		return null;
	}

	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = getPackageInfo(context).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = getPackageInfo(context).versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	public static PackageInfo getPackageInfo(Context context) throws Exception {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		return packInfo;
	}

	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String device_id = tm.getDeviceId();
			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);
			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}
			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			}
			json.put("device_id", device_id);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getDeviceMac(Context context) {
		try {
			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			String mac = wifi.getConnectionInfo().getMacAddress();
			if (!TextUtils.isEmpty(mac)) {
				return mac;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "null";
	}

	public static void verifyMobile(String mobileNumber) {
	}

	public static List<Long> position2Time(List<Integer> positions) {
		ArrayList<Long> times = new ArrayList<>();
		if (positions != null && positions.size() != 0) {
			for (int position : positions) {
				times.add((long) (position * 500));
			}
		}
		return times;
	}

	public static List<Integer> time2Position(List<Long> times) {
		ArrayList<Integer> positions = new ArrayList<>();
		if (times != null && times.size() != 0) {
			for (long time : times) {
				positions.add((int) (time / 500));
			}
		}
		return positions;
	}

	/**
	 * { "v": 1,//(必选)数据结构版本 "data":{//(必选) "heartRate":[158,123],  //(必选)胎儿心率, 按照 interval 间隔
	 * "fm":[1500,3500],       //胎动 fetalMovement , 监测开始后的第xxx 毫秒 "afm":[1500,3500],      //自动胎动
	 * autoFetalMovement, 监测开始后的第xxx 毫秒 "doctor":[1500,3500],   //医生干预 , 监测开始后的第xxx 毫秒 "interval":
	 * 500,        //(必选)心率时间间隔,毫秒 "time":1400000000       //(必选)开始时间,UTC 毫秒时间戳 }, "device": {
	 * //(必选)探头 "sn":"",                //(必选)设备id "type":0,              //(必选)设备型号 "version":1
	 * //(必选)设备版本号 }, "hostDevice":{            //宿主设备（允许app的设备） "deviceId":"", //设备id "type":"",
	 * //类型 "os":"",                //os 版本信息字符串 "imei":"", //设备imei 可选 "softVersion":""
	 * //我们的软件版本字符串格式 } }
	 *
	 * @return
	 */
	public static RecordData getDefaultRecordData(Context context) {
		RecordData recordData = new RecordData();
		recordData.setV(1);
		Device device = new Device();
		device.setSn(SPUtil.getServiceInfo(context).getSerialnum());
		device.setType(0);
		device.setVersion(1);
		HostDevice hostDevice = new HostDevice();
		hostDevice.setDeviceId(getDeviceId(context));
		hostDevice.setOs("Android" + Build.VERSION.CODENAME + Build.DEVICE);
		hostDevice.setType(android.os.Build.MODEL);
		hostDevice.setImei(getDeviceId(context));
		hostDevice.setSoftVersion("1");
		recordData.setDevice(device);
		recordData.setHostDevice(hostDevice);
		return recordData;
	}

	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		return deviceId;
	}
}
