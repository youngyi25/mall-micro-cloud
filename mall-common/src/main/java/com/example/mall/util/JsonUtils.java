package com.example.mall.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import java.util.List;

public class JsonUtils {
    private JsonUtils(){}

    /**
     * 对象 → JSON
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * JSON → 对象
     * @param json
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T toObj(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * JSON → 集合
     * @param json
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * JSON → 复杂泛型
     * @param json
     * @param type
     * @return
     * @param <T>
     */
    public static <T> T toType(String json, TypeReference<T> type) {
        return JSON.parseObject(json, type);
    }

    /**
     * 对象 → 对象
     * @param obj
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T toObj(Object obj, Class<T> clazz) {
        return JSON.parseObject(JSON.toJSONString(obj), clazz);
    }
    /**
     * 集合 → 集合
     * @param list
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> List<T> toList(List<?> list, Class<T> clazz) {
        return JSON.parseArray(JSON.toJSONString(list), clazz);
    }
}
