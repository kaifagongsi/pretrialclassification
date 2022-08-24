package com.kfgs.pretrialclassification.common.utils;

public class SimpleDateFormateUtils {

    public  static String getFormat(String fenleiTime){
        return fenleiTime.substring(0,4) + "年" + fenleiTime.substring(4,6) + "月" + fenleiTime.substring(6,8)+"日 "
                + fenleiTime.substring(8,10 )+ "时" + fenleiTime.substring(10,12)+"分"+fenleiTime.substring(12,14)+"秒";
    }
}
