package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Date;

import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.client.model.HClientUser;
import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;

/**
 * SharedPreferences 工具类
 */
public class SPUtil {
	private static final String FILE_NAME = "weitaixin_share_data";
	private static final String FILE_NAME_USER = "user_share_data";
	private static final String FILE_NAME_SERVICEINFO = "serviceinfo_share_data";
	private static final String FILE_NAME_TEMP = "temp_share_data";
	private static final String FILE_NAME_FETALHEART = "fetalheart_share_data";
	private static final String FILE_NAME_FIRST_START_APP = "firststartapp_share_data";
	private static final String FILE_NAME_ADVICE_SETTING = "advice_setting_share_data";
	private static final String FILE_NAME_REMEMBERMOBILE = "remembermobile_share_data";
	private static final String FILE_NAME_PRO = "pro";
	private static final String DEPARTMENT_ID = "departmentId";
	private static final String ID = "id";
	private static final String INDEX_NUMBER = "indexNumber";
	private static final String HOSPITAL_ID = "hospitalId";
	private static final String HOSPITAL_NAME = "hospitalname";
	private static final String SERIALNUM = "serialnum";
	private static final String DEVICE_ID = "deviceId";
	private static final String DEPARTMENT_NAME = "departmentName";
	private static final String STATUS = "status";
	private static final String USE_TYPE = "useType";
	private static final String UPDATE_TIME = "updateTime";
	private static final String CREATE_TIME = "createTime";
	private static final String LOGIN_TOKEN = "loginToken";

	public static String getRememberMobile(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_REMEMBERMOBILE, Context.MODE_PRIVATE);
		return sp.getString("RememberMobile", "");
	}

	public static void saveUser(Context context, User user) {
		SPUtil.clearUser(context);
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		if (user.getAccountToken() != null) {
			editor.putString("AccountToken", user.getAccountToken());
		}
		if (user.getTelephone() != null) {
			editor.putString("Telephone", user.getTelephone());
		}
		editor.putLong("Id", user.getId());
		if (user.getMobile() != null) {
			editor.putString("Mobile", user.getMobile());
			SharedPreferences spMobile = context.getSharedPreferences(FILE_NAME_REMEMBERMOBILE, Context.MODE_PRIVATE);
			SharedPreferences.Editor editorMobile = spMobile.edit();
			editorMobile.putString("RememberMobile", user.getMobile());
			editorMobile.commit();
		}
		if (user.getName() != null) {
			editor.putString("Name", user.getName());
		}
		if (user.getHeadPic() != null) {
			editor.putString("HeadPic", user.getHeadPic());
		}
		if (user.getBirthday() != null) {
			editor.putLong("Birthday", user.getBirthday().getTime());
		}
		editor.putBoolean("HasService", user.getHasService());
		editor.putBoolean("HasRiskscore", user.getHasRiskscore());
		if (user.getDeliveryTime() != null) {
			editor.putLong("DeliveryTime", user.getDeliveryTime().getTime());
		}
		if (user.getCreateTime() != null) {
			editor.putLong("CreateTime", user.getCreateTime().getTime());
		}
		editor.putBoolean("IsInit", user.getIsInit());
		editor.commit();
		saveServiceInfo(context, user);
	}

	public static void saveServiceInfo(Context context, User user) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_SERVICEINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		ServiceInfo serviceInfo = user.getServiceInfo();
		if (serviceInfo != null) {
			editor.putString("AreaInfo", serviceInfo.getAreaInfo());
			editor.putString("Serialnum", serviceInfo.getSerialnum());
			editor.putLong("DoctorId", serviceInfo.getDoctorId());
			editor.putString("DoctorName", serviceInfo.getDoctorName());
			editor.putLong("HospitalId", serviceInfo.getHospitalId());
			editor.putString("HospitalName", serviceInfo.getHospitalName());
		}
		editor.commit();
	}

	public static void setUUID(Context context, String uuid) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_TEMP, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("uuid", uuid);
		editor.commit();
	}

	public static String getUUID(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_TEMP, Context.MODE_PRIVATE);
		return sp.getString("uuid", null);
	}

	public static void clearUUID(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_TEMP, Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}

	public static void setLocalSetting(Context context, LocalSetting localSetting) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("autostart", localSetting.isAutoStart());
		editor.putBoolean("alert", localSetting.isAlert());
		editor.putInt("monitorTime", localSetting.getAlertInterval());
		editor.putBoolean("auto_uploading", localSetting.isAutoUploading());
		editor.commit();
	}

	public static LocalSetting getLocalSetting(Context context) {
		LocalSetting localSetting = new LocalSetting();
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		localSetting.setAutoStart(sp.getBoolean("autostart", true));
		localSetting.setAlert(sp.getBoolean("alert", true));
		localSetting.setAlertInterval(sp.getInt("monitorTime", LocalSetting.DEFAULT_MONITOR_TIME));
		localSetting.setAutoUploading(sp.getBoolean("auto_uploading", true));
		return localSetting;
	}

	public static long getUserID(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		long userId = sp.getLong("Id", -1);
		return userId;
	}

	public static long getDeliveryTime(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		long eliveryTime = sp.getLong("DeliveryTime", -1);
		return eliveryTime;
	}

	public static User getUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		User user = null;
		String accountToken = sp.getString("AccountToken", "");
		if (!TextUtils.isEmpty(accountToken)) {
			user = new User();
		}
		if (user != null) {
			user.setAccountToken(sp.getString("AccountToken", ""));
			user.setTelephone(sp.getString("Telephone", ""));
			user.setId(sp.getLong("Id", -1));
			user.setMobile(sp.getString("Mobile", ""));
			user.setName(sp.getString("Name", ""));
			user.setHeadPic(sp.getString("HeadPic", ""));
			user.setBirthday(new Date(sp.getLong("Birthday", -1)));
			user.setHasService(sp.getBoolean("HasService", false));
			user.setHasRiskscore(sp.getBoolean("HasRiskscore", false));
			user.setDeliveryTime(new Date(sp.getLong("DeliveryTime", -1)));
			user.setCreateTime(new Date(sp.getLong("CreateTime", -1)));
			user.setIsInit(sp.getBoolean("IsInit", false));
			ServiceInfo serviceInfo = SPUtil.getServiceInfo(context);
			user.setServiceInfo(serviceInfo);
		}
		return user;
	}

	public static void clearUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		SharedPreferences sPreferences = context.getSharedPreferences(FILE_NAME_SERVICEINFO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editorService = sPreferences.edit();
		editorService.clear();
		editorService.commit();
	}

	public static void saveAdviceSetting(Context context, AdviceSetting adviceSetting) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("AlarmHeartrateLimit", adviceSetting.getAlarmHeartrateLimit());
		editor.putInt("AskMinTime", adviceSetting.getAskMinTime());
		editor.putInt("AutoAdviceTimeLong", adviceSetting.getAutoAdviceTimeLong());
		editor.putInt("AutoBeginAdvice", adviceSetting.getAutoBeginAdvice());
		editor.putInt("AutoBeginAdviceMax", adviceSetting.getAutoBeginAdviceMax());
		editor.putInt("FetalMoveTime", adviceSetting.getFetalMoveTime());
		editor.putLong("HospitalId", adviceSetting.getHospitalId());
		editor.commit();
	}

	public static AdviceSetting getAdviceSetting(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		AdviceSetting adviceSetting = new AdviceSetting();
		adviceSetting.setAlarmHeartrateLimit(sp.getString("AlarmHeartrateLimit", ""));
		adviceSetting.setAskMinTime(sp.getInt("AskMinTime", -1));
		adviceSetting.setAutoAdviceTimeLong(sp.getInt("AutoAdviceTimeLong", -1));
		adviceSetting.setAutoBeginAdvice(sp.getInt("AutoBeginAdvice", -1));
		adviceSetting.setAutoBeginAdviceMax(sp.getInt("AutoBeginAdviceMax", -1));
		adviceSetting.setFetalMoveTime(sp.getInt("FetalMoveTime", -1));
		adviceSetting.setHospitalId(sp.getLong("HospitalId", -1));
		return adviceSetting;
	}

	public static void saveHeadPic(Context context, String headPic) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("HeadPic", headPic);
		editor.commit();
	}

	public static ServiceInfo getServiceInfo(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_SERVICEINFO, Context.MODE_PRIVATE);
		ServiceInfo serviceInfo = new ServiceInfo();
		serviceInfo.setAreaInfo(sp.getString("AreaInfo", ""));
		serviceInfo.setSerialnum(sp.getString("Serialnum", ""));
		serviceInfo.setDoctorId(sp.getLong("DoctorId", -1));
		serviceInfo.setDoctorName(sp.getString("DoctorName", ""));
		serviceInfo.setHospitalId(sp.getLong("HospitalId", -1));
		serviceInfo.setHospitalName(sp.getString("HospitalName", ""));
		return serviceInfo;
	}

	public static long getHospitalId(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_SERVICEINFO, Context.MODE_PRIVATE);
		long HospitalId = sp.getLong("HospitalId", -1);
		return HospitalId;
	}

	public static boolean isLogin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		String accountToken = sp.getString("AccountToken", "");
		if (TextUtils.isEmpty(accountToken)) {
			return false;
		}
		return true;
	}

	public static boolean isIsInit(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
		return sp.getBoolean("IsInit", false);
	}

	public static void saveHClientUser(Context context, HClientUser user) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_FETALHEART, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(DEPARTMENT_ID, user.getDepartmentId());
		editor.putLong(ID, user.getId());
		editor.putLong(INDEX_NUMBER, user.getIndexNumber());
		editor.putLong(HOSPITAL_ID, user.getHospitalId());
		editor.putString(HOSPITAL_NAME, user.getHospitalName());
		editor.putString(SERIALNUM, user.getSerialnum());
		editor.putString(DEVICE_ID, user.getDeviceId());
		editor.putString(DEPARTMENT_NAME, user.getDepartmentName());
		editor.putInt(STATUS, user.getStatus());
		editor.putInt(USE_TYPE, user.getUseType());
		editor.putLong(UPDATE_TIME, user.getUpdateTime().getTime());
		editor.putLong(CREATE_TIME, user.getCreateTime().getTime());
		editor.putString(LOGIN_TOKEN, user.getLoginToken());
		editor.commit();
	}

	public static HClientUser getHClientUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_FETALHEART, Context.MODE_PRIVATE);
		HClientUser user = new HClientUser();
		user.setHospitalName(sp.getString("hospitalName", null));
		user.setId(sp.getLong("id", 0L));
		user.setDepartmentId(sp.getLong(DEPARTMENT_ID, 0));
		user.setIndexNumber(sp.getLong(INDEX_NUMBER, 0L));
		user.setHospitalId(sp.getLong(HOSPITAL_ID, 0L));
		user.setHospitalName(sp.getString(HOSPITAL_NAME,null));
		user.setSerialnum(sp.getString(SERIALNUM, null));
		user.setDeviceId(sp.getString(DEVICE_ID, null));
		user.setDepartmentName(sp.getString(DEPARTMENT_NAME, null));
		user.setStatus(sp.getInt(STATUS, 0));
		user.setUseType(sp.getInt(USE_TYPE, 0));
		user.setUpdateTime(new Date(sp.getLong(UPDATE_TIME, 0)));
		user.setCreateTime(new Date(sp.getLong(CREATE_TIME, 0)));
		user.setLoginToken(sp.getString(LOGIN_TOKEN, null));
		return user;
	}

	public static boolean isNoFirstStartApp(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_FIRST_START_APP, Context.MODE_PRIVATE);
		return sp.getBoolean("FirstStartApp", false);
	}

	public static void setNoFirstStartApp(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME_FIRST_START_APP, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean("FirstStartApp", true);
		editor.commit();
	}
}




