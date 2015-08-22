package cn.ihealthbaby.weitaixin.tools;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Think on 2015/8/14.
 */
public class DateTimeTool {

    public static String date2Str(Date date) {
        if (date==null) {
            return "";
        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return  sdf.format(date);
    }

    public static String date2StrAndTime(Date date) {
        if (date==null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd hh:mm");
        return  sdf.format(date);
    }

    public static Date str2Date(String dateStr)  {
        if (TextUtils.isEmpty(dateStr)) {
            return new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Date d = sdf.parse("2000-11-11 14:23:20");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTime3(int mss){
        mss=mss*1000;
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + "日" + hours + "小时" + minutes + "分"+ seconds + "秒";
    }

    public static String getTime2(int time){
        SimpleDateFormat formatter = new SimpleDateFormat("mm分ss秒");
        String hms = formatter.format(time*1000);
        return hms;
    }


    public static String getGestationalWeeks(Date deliveryTime) {
        if (deliveryTime==null) {
            return "";
        }
        int getGestationalDay = (int) ((new Date().getTime() / 1000
                - deliveryTime.getTime() / 1000 + 280 * 24 * 3600) / 3600 / 24);
        int weeks = getGestationalDay / 7;
        int days = getGestationalDay % 7;
        return "孕" + weeks + "周+" + days + "天";
    }

    public static void main(String[] args) {
        String s = getGestationalWeeks(new Date(new Date().getTime() + 1000
                * 3600 * 24 * 6));
        System.out.println(s);
    }


}



