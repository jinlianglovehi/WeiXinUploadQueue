package cn.ihealthbaby.weitaixin.tools;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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


    //

    public static String getGestationalWeeks(Date deliveryTime) {
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
