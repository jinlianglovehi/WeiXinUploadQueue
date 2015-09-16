package cn.ihealthbaby.weitaixin.ui.pay;

/**
 * Created by Think on 2015/9/6.
 */
public class PayConstant {
    //    0 未支付, 1待发货, 2待收货, 3订单结束, 4订单取消  5全部
    public static final String[] orderType=new String[]{"未支付","待发货","待收货","订单结束","订单取消","全部"};
    public static final int notPay=0;
    public static final int sendingGoods=1;
    public static final int gettingGoods=2;
    public static final int orderFinish=3;
    public static final int orderCancel=4;
    public static final int orderAll=5;
    //

    public static final int requestCodeHospitalChoose =500;
    public static final int resultCodeHospitalChoose =501;
    public static final int requestCodeCityChoose =600;
    public static final int resultCodeCityChoose =601;


    public static final String LeftCityList ="leftCityList";
    public static final String RightCityList ="rightCityList";
    public static final String ORDERID ="orderId";


}
