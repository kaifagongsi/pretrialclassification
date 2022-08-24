package com.kfgs.pretrialclassification.common.utils;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.ComparatorUtils;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

public class ListUtils {


    /**
     * List  按照实体类中的某个属性排序 中文（根据的汉字的拼音的字母排序的）
     * @param list  需要排序数组
     * @param key  需要排序的属性
     * @param desc  排序方式
     * @param <E>
     */
    @SuppressWarnings("unchecked")
    public static <E> void sort(List<?> list, String key, String desc) {
        try {
            Comparator<?> comparator = new Comparator<E>() {
                @Override
                public int compare(E obj1, E obj2) {
                    // 定义中文语境
                    Collator instance = Collator.getInstance(Locale.CHINA);
                    return instance.compare(obj1, obj2);
                }

            };
            comparator = ComparatorUtils.nullLowComparator(comparator);
            if ("desc".equals(desc)) {
                comparator = ComparatorUtils.reversedComparator(comparator);
            }
            //Collections.sort(list,new BeanComparator<String>(comparator));
            Collections.sort(list, new BeanComparator(key, comparator));
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    /**
     * 两个list去重
     */
    public static List<String> sortListTwo(List<String> first, List<String> second){
        Set set = new HashSet();
        set.addAll(first);
        set.addAll(second);
        return new ArrayList<String>(set);
    }

    /**
     * 1. 按指定大小，分隔集合，将集合按规定个数分为n个部分
     * @param list 需要分割的list
     * @param len  没分list有多少个
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }
        List<List<T>> result = new ArrayList<List<T>>();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

    /**
     * 2.将一个list均分成n个list
     * @param list 需要分割的list
     * @param n  需要分割为多少份
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> list,int n){
        List<List<T>> result=new ArrayList<List<T>>();
        int remaider=list.size()%n;  //(先计算出余数)
        int number=list.size()/n;  //然后是商
        int offset=0;//偏移量
        for(int i=0;i<n;i++){
            List<T> value=null;
            if(remaider>0){
                value=list.subList(i*number+offset, (i+1)*number+offset+1);
                remaider--;
                offset++;
            }else{
                value=list.subList(i*number+offset, (i+1)*number+offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * list 去重
     * @param list 待去重的list
     * @return 返回新的list列表
     */
    public static List<String> delRepeatReturnList(List<String> list){
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     *  list 去重
     * @param list 待去重的list
     * @return 返回String字符串，以逗号分隔
     */
    public static String delRepeatReturnString(List<String> list){
        List<String> collect = list.stream().distinct().collect(Collectors.toList());
        return collect.stream().collect(Collectors.joining(","));
    }


}
