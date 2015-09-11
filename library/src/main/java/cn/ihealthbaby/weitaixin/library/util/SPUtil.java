package cn.ihealthbaby.weitaixin.library.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类 可以保存String, Integer, Boolean, Float, Long类型的参数
 * Created by liuhongjian on 15/9/11 20:31.
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

}
