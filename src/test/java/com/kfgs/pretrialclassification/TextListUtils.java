package com.kfgs.pretrialclassification;


import com.ibm.icu.text.Collator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TextListUtils {

    @Test
    public void textListUtilsSortObject() {
        /*List<FenleiBaohuUserinfo> list = new ArrayList<FenleiBaohuUserinfo>();
        FenleiBaohuUserinfo i1 = new FenleiBaohuUserinfo();
        i1.setName("三部");
        list.add(i1);
        FenleiBaohuUserinfo i2 = new FenleiBaohuUserinfo();
        i2.setName("一部");
        list.add(i2);
        FenleiBaohuUserinfo i3 = new FenleiBaohuUserinfo();
        i3.setName("业务研究与发展部");
        list.add(i3);
        FenleiBaohuUserinfo i4 = new FenleiBaohuUserinfo();
        i4.setName("四部");
        list.add(i4);
        FenleiBaohuUserinfo i5 = new FenleiBaohuUserinfo();
        i5.setName("系统建设与运维部");
        list.add(i5);
        FenleiBaohuUserinfo i6 = new FenleiBaohuUserinfo();
        i6.setName("二部");
        list.add(i6);
        ListUtils.sort(list,"name","desc");
        list.forEach( s -> {
            System.out.println(s);
        });*/

       /* List<String> list = new ArrayList<String>();
        list.add("一部");
        list.add("三部");
        list.add("业务研究与发展部");
        list.add("四部");
        list.add("系统建设与运维部");
        list.add("二部");

        //升序   注意：是根据的汉字的拼音的字母排序的，而不是根据汉字一般的排序方法
        Collections.sort(list, Collator.getInstance(Locale.CHINA));
        for (String string : list) {
            System.out.print(string);
        }
        System.out.println();
        //降序
        Collections.reverse(list);
        for (String string : list) {
            System.out.print(string);
        }*/
        List<String> list = new ArrayList<String>();
        list.add("一部");
        list.add("三部");
        list.add("业务研究与发展部");
        list.add("四部");
        list.add("系统建设与运维部");
        list.add("二部");
      /*  Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                return com.compare(o1, o2);

            }
        });*/

        for (String temp : list) {
            if("四部".equals(temp)){
                list.remove(temp);
                break;
            }
            System.out.println(temp);
        }
        for (String temp : list) {
            System.out.println(temp);
        }

    }
}
