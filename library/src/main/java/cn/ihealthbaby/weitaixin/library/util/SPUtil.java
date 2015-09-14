package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Date;

import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;

/**
 * SharedPreferences 工具类 可以保存String, Integer, Boolean, Float, Long类型的参数 Created by liuhongjian on
 * 15/9/11 20:31.
 */
public class SPUtil {
    private static final String FILE_NAME = "weitaixin_share_data";
    private static final String FILE_NAME_USER = "user_share_data";
    private static final String FILE_NAME_SERVICEINFO = "serviceinfo_share_data";
    private static final String FILE_NAME_TEMP = "temp_share_data";
    private static final String FILE_NAME_ADVICE_SETTING = "advice_setting_share_data";

    public static void setUUID(Context context, String uuid) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_TEMP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("uuid", uuid);
        editor.apply();
    }

    public static String getUUID(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_TEMP, Context.MODE_PRIVATE);
        return sp.getString("uuid", "");
    }

    public static void clearUUID(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_TEMP, Context.MODE_PRIVATE);
        sp.edit().clear();
    }


    public static void setLocalSetting(Context context, LocalSetting localSetting) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("autostart", localSetting.isAutostart());
        editor.putBoolean("alert", localSetting.isAlert());
        editor.putInt("monitorTime", localSetting.getMonitorTime());
        editor.putBoolean("auto_uploading", localSetting.isAuto_uploading());
        editor.commit();
    }

    public static LocalSetting getLocalSetting(Context context) {
        LocalSetting localSetting = new LocalSetting();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        localSetting.setAutostart(sp.getBoolean("autostart", true));
        localSetting.setAlert(sp.getBoolean("alert", true));
        localSetting.setMonitorTime(sp.getInt("monitorTime", LocalSetting.DEFAULT_MONITOR_TIME));
        localSetting.setAuto_uploading(sp.getBoolean("auto_uploading", true));
        return localSetting;
    }


    public static void saveUser(Context context, User user) {
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

        editor.putInt("TypeId", user.getTypeId());
        editor.putBoolean("HasService", user.getHasService());

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


    public static User getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        User user = new User();
        user.setAccountToken(sp.getString("AccountToken", ""));
        user.setTelephone(sp.getString("Telephone", ""));
        user.setId(sp.getLong("Id", -1));
        user.setMobile(sp.getString("Mobile", ""));
        user.setName(sp.getString("Name", ""));
        user.setHeadPic(sp.getString("HeadPic", ""));
        user.setBirthday(new Date(sp.getLong("Birthday", -1)));
        user.setTypeId(sp.getInt("TypeId", -1));
        user.setHasService(sp.getBoolean("HasService", false));
        user.setDeliveryTime(new Date(sp.getLong("DeliveryTime", -1)));
        user.setCreateTime(new Date(sp.getLong("CreateTime", -1)));
        user.setIsInit(sp.getBoolean("IsInit", false));
        ServiceInfo serviceInfo = SPUtil.getServiceInfo(context);
        user.setServiceInfo(serviceInfo);
        return user;
    }


    public static void clearUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
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
        AdviceSetting adviceSetting=new AdviceSetting();
        adviceSetting.setAlarmHeartrateLimit(sp.getString("AlarmHeartrateLimit", ""));
        adviceSetting.setAskMinTime(sp.getInt("AskMinTime", -1));
        adviceSetting.setAutoBeginAdvice(sp.getInt("AutoAdviceTimeLong", -1));
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


    public static boolean isLogin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        String accountToken = sp.getString("AccountToken", "");
        if (TextUtils.isEmpty(accountToken)) {
            return false;
        }
        return true;
    }


}
