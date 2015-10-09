package cn.ihealthbaby.weitaixin.ui.pay;

/**
 * Created by Think on 2015/9/6.
 */
public class PayConstant {
    //0 待付款-未支付,1 待发货,2待收货,3订单结束,4 订单取消
    public static final String[] orderType = new String[]{"待付款", "待发货", "待收货", "交易成功", "订单取消"};
    public static final int notPay = 0;
    public static final int sendingGoods = 1;
    public static final int gettingGoods = 2;
    public static final int orderFinish = 3;
    public static final int orderCancel = 4;
    public static final int orderAll = 5;
    //

    public static final int requestCodeHospitalChoose = 500;
    public static final int resultCodeHospitalChoose = 501;
    public static final int requestCodeCityChoose = 600;
    public static final int resultCodeCityChoose = 601;


    public static String AreasString = "";

    public static final String LeftCityList = "leftCityList";
    public static final String RightCityList = "rightCityList";
    public static final String ORDERID = "orderId";
    public static final String TOTALFEE = "totalfee";
    public static final String NEXTTAP = "nexttap";
    public static String WXPAY_APPID = "wx8da8b08e973e9305";


}
