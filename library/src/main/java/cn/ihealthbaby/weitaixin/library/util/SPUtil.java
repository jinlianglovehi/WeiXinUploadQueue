package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.content.SharedPreferences;

import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;

/**
 * SharedPreferences 工具类 可以保存String, Integer, Boolean, Float, Long类型的参数 Created by liuhongjian on
 * 15/9/11 20:31.
 */
public class SPUtil {
    private static final String FILE_NAME = "weitaixin_share_data";


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

    }

    public static User getUser() {
        return null;
    }

    public static void saveServiceInfo() {
    }

    public static ServiceInfo getServiceInfo() {
        return null;
    }

}
