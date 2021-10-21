package com.kfgs.pretrialclassification.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public class TrimeUtil {

    public static Object objectToTrime(Object entity) throws IllegalAccessException {
        Class<?> clzz = entity.getClass();
        Field[] declareFields = clzz.getDeclaredFields();
        for(Field field : declareFields){
            String fieldType = field.getType().getCanonicalName();
            if(StringUtils.equals("java.lang.String",fieldType)){
                field.setAccessible(true);
                //  get​(Object obj) 返回由该 Field表示的字段在指定对象上的值。
                Object o = field.get(entity);
                if(o != null){
                    // set​(Object obj, Object value) 将指定的对象参数中由此 Field对象表示的字段设置为指定的新值。
                    field.set(entity,o.toString().replaceAll(" ","").replaceAll("[\\t\\n\\r]",""));
                }
            }
        }
        return entity;
    }
}
