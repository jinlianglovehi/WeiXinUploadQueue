package cn.ihealthbaby.weitaixin.ui.pay;

/**
 * Created by chenweihua on 2015/9/22.
 */
public class PayUtils {

    public static String showPrice(int price) {
        return "￥" + (price / 100);
    }

}
