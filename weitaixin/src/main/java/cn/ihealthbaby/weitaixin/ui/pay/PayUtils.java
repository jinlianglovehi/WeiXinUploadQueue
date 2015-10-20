package cn.ihealthbaby.weitaixin.ui.pay;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by chenweihua on 2015/9/22.
 */
public class PayUtils {

    public static String showPrice(int price) {
        NumberFormat nf = new DecimalFormat("￥##.####");
        String str = nf.format(price);
        return str;
//        return "￥" + (price / 100);
    }

}
