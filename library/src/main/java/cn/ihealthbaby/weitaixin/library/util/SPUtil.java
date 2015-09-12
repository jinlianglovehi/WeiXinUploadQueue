package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Date;

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


    public static void setLocalSetting(Context context, LocalSetting localSetting) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("autostart", localSetting.isAutostart());
        editor.putBoolean("alertInterval", localSetting.isAlertInterval());
        editor.putInt("selectPosition", localSetting.getSelectPosition());
        editor.putInt("monitorTime", localSetting.getMonitorTime());
        editor.putBoolean("auto_uploading", localSetting.isAuto_uploading());
        editor.commit();
    }

    public static LocalSetting getLocalSetting(Context context) {
        LocalSetting localSetting = new LocalSetting();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        localSetting.setAutostart(sp.getBoolean("autostart", true));
        localSetting.setSelectPosition(sp.getInt("selectPosition", LocalSetting.DEFAULT_SELECT_POSITION));
        localSetting.setAlertInterval(sp.getBoolean("alertInterval", true));
        localSetting.setMonitorTime(sp.getInt("monitorTime", LocalSetting.DEFAULT_MONITOR_TIME));
        localSetting.setAuto_uploading(sp.getBoolean("auto_uploading", true));
        return localSetting;
    }


    public static void saveUser(Context context, User user) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("AccountToken", user.getAccountToken());
        editor.putString("Telephone", user.getTelephone());
        editor.putLong("Id", user.getId());
        editor.putString("Mobile", user.getMobile());
        editor.putString("Name", user.getName());
        editor.putString("HeadPic", user.getHeadPic());
        editor.putLong("Birthday", user.getBirthday().getTime());
        editor.putInt("TypeId", user.getTypeId());
        editor.putBoolean("HasService", user.getHasService());
        editor.putLong("DeliveryTime", user.getDeliveryTime().getTime());
        editor.putLong("CreateTime", user.getCreateTime().getTime());
        editor.putBoolean("IsInit", user.getIsInit());
        editor.commit();
    }


    public static User getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        User user =new User();
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
        return user;
    }


    public static void clearUser(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }


    public static void saveHeadPic(Context context,String headPic) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("HeadPic", headPic);
        editor.commit();
    }


    public static void saveServiceInfo(Context context, User user) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_SERVICEINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        ServiceInfo serviceInfo = user.getServiceInfo();
        editor.putString("AreaInfo", serviceInfo.getAreaInfo());
        editor.putString("Serialnum", serviceInfo.getSerialnum());
        editor.putLong("DoctorId", serviceInfo.getDoctorId());
        editor.putString("DoctorName", serviceInfo.getDoctorName());
        editor.putLong("HospitalId", serviceInfo.getHospitalId());
        editor.putString("HospitalName", serviceInfo.getHospitalName());
        editor.commit();
    }


    public static ServiceInfo getServiceInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_SERVICEINFO, Context.MODE_PRIVATE);
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setAreaInfo(sp.getString("AreaInfo", ""));
        serviceInfo.setAreaInfo(sp.getString("Serialnum", ""));
        serviceInfo.setDoctorId(sp.getLong("DoctorId", -1));
        serviceInfo.setAreaInfo(sp.getString("DoctorName", ""));
        serviceInfo.setHospitalId(sp.getLong("HospitalId", -1));
        serviceInfo.setHospitalName(sp.getString("HospitalName", ""));
        return serviceInfo;
    }


    public static boolean isLogin(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME_USER, Context.MODE_PRIVATE);
        String accountToken = sp.getString("AccountToken","");
        if (TextUtils.isEmpty(accountToken)) {
            return false;
        }
        return true;
    }


}
