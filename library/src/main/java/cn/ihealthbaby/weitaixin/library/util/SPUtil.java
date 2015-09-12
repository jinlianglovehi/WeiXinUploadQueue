package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import cn.ihealthbaby.client.model.ServiceInfo;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.model.LocalSetting;

/**
 * SharedPreferences 工具类 可以保存String, Integer, Boolean, Float, Long类型的参数 Created by liuhongjian on
 * 15/9/11 20:31.
 */
public class SPUtil {
    private static final String FILE_NAME = "weitaixin_share_data";

    public static void setData(Context context, String key, Object value) {
        String type = value.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if ("String".equals(type)) {
            editor.putString(key, (String) value);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) value);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) value);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) value);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }

    public static Object getData(Context context, String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if ("String".equals(type)) {
            return sp.getString(key, (String) defValue);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defValue);
        }
        return null;
    }

    public static void setObject(Context context, Object o) {
        Gson gson = new Gson();
        String str = gson.toJson(o);
        Toast.makeText(context, o.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
    }

    public static void setLocalSetting(Context context, LocalSetting localSetting) {
        setData(context, "autostart", localSetting.isAutostart());
        setData(context, "policeset", localSetting.isPoliceset());
    }

    public static LocalSetting getLocalSetting(Context context) {
        LocalSetting localSetting = new LocalSetting();
        localSetting.setAutostart((Boolean) getData(context, "autostart", true));
        localSetting.setPoliceset((Boolean) getData(context, "policeset", true));
        return localSetting;
    }


    public static void saveUser(Context context, User user) {
        setObject(context, user);
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
