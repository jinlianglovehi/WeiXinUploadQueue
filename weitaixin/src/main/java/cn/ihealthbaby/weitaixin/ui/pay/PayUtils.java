package cn.ihealthbaby.weitaixin.ui.pay;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * Created by chenweihua on 2015/9/22.
 */
public class PayUtils {

    public static String showPrice(int price) {
        int a = price / 100;
        int b = price % 100;
        String s = a + "." + b;
        DecimalFormat nf = new DecimalFormat("￥##0.00");
        String str = nf.format(Double.valueOf(s));
        LogUtil.d("showPrice", "showPrice==> " + str);
        return str;
    }

}
