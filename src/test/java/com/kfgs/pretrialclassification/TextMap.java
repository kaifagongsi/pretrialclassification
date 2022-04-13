package com.kfgs.pretrialclassification;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class TextMap {

    public static void main(String[] args) {
        /*String finishTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        System.out.println(finishTime);*/

           /* HashMap<Integer, Integer> map = new HashMap<>(8);
            for (int i = 1; i <= 7; i++) {
                int sevenSlot = i * 8  +7 ;
                map.put(sevenSlot, sevenSlot);
            }*/

        /*String cca = "";
        cca =  "cca1,cca" + "," +cca;

        System.out.println(cca.substring(0,cca.lastIndexOf(",")));*/

        String cci = "A01B63/06,A01B63/102,A01B63/006,CPCONLY,G06F2211/1007";
        // ipcmi.

        System.out.println(cci.replaceAll("CPCONLY","").replaceAll(",,",","));

    }


}
