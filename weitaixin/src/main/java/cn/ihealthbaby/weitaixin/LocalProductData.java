package cn.ihealthbaby.weitaixin;

import java.util.HashMap;

/**
 * Created by Think on 2015/9/7.
 */
public class LocalProductData {

    public HashMap<String, Object> localProductDataMap = new HashMap<String, Object>();

    public void put(String key, Object value) {
        localProductDataMap.put(key, value);
    }

    public Object get(String key) {
        return localProductDataMap.get(key);
    }

    public static LocalProductData getLocal(){
        return WeiTaiXinApplication.getInstance().localProductData;
    }

    public static String HospitalId="HospitalId";
    public static String CityId="CityId";
    public static String HospitalName="HospitalName";
    public static String CityName="CityName";
    public static String DoctorId="DoctorId";
    public static String DoctorName="DoctorName";
    public static String Name01="Name01";
    public static String Name02="Name02";
    public static String Name03="Name03";
    public static String Name04="Name04";
    public static String Name05="Name05";
    public static String CountGoods="CountGoods";
    public static String PriceCount="PriceCount";
    public static String HospitalStatus="HospitalStatus";
    public static String HospitalAddress="HospitalAddress";


}
