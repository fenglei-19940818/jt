package com.jt.util;

import org.springframework.cglib.beans.BeanMap;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BeanMapUtil {
    /**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(String.valueOf(key), beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    /**
     * 将List<T>转换为List<Map<String, Object>>
     *
     * @param objList
     * @return
     */
    public static <T> List<Map<String, Object>> beansToMaps(List<T> objList) {
        List<Map<String, Object>> list = Collections.emptyList();
        if (objList != null && objList.size() > 0) {
            list = new ArrayList<>(objList.size());
            Map<String, Object> map;
            T bean;
            for (int i = 0, size = objList.size(); i < size; i++) {
                bean = objList.get(i);
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 将List<Map<String,Object>>转换为List<T>
     *
     * @param maps
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> List<T> mapsToBeans(List<Map<String, Object>> maps, Class<T> clazz) {

        List<T> list = Collections.emptyList();
        if (maps != null && maps.size() > 0) {
            list = new ArrayList<>(maps.size());
            Map<String, Object> map;
            T bean;
            try {
                for (int i = 0, size = maps.size(); i < size; i++) {
                    map = maps.get(i);
                    bean = mapToBean(map, clazz.getDeclaredConstructor().newInstance());
                    list.add(bean);
                }
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 使用 Map按key进行排序得到key=value的字符串
     *
     * @param plain
     * @param eqaulsType K与V之间的拼接字符串 = 或者其他...
     * @param spliceType K-V与K-V之间的拼接字符串  & 或者|...
     * @return
     */
    public static Map<String, String> stringToMap(String plain, String eqaulsType,
                                                  String spliceType) {
        if (plain == null || plain.isEmpty()) {
            return null;
        }

        Map<String, String> map = new HashMap<>();

        String[] split = plain.split(spliceType);
        for (String kv : split) {
            if ("|".equals(kv)) {
                continue;
            }
            String[] kvArr = kv.split(eqaulsType);
            if (kvArr.length == 2) {
                map.put(kvArr[0], kvArr[1]);
            } else {
                map.put(kvArr[0], "");
            }
        }

        return map;
    }
}
