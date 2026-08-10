package com.example.mall.util;

import com.alibaba.fastjson.JSONObject;

public class CamelCastUtils {
    /**
     * 字段名转换成属性名
     * @param field
     * @return
     */
    public static String UnderlineToHump(String field){
        String[] parts = field.split("_");
        StringBuilder hump = new StringBuilder(parts[0]);
        for(int i = 1; i < parts.length; i++){
            // 首字母转换成大写
            String capitalizedString = Character.toString(parts[i].charAt(0)).toUpperCase() + parts[i].substring(1);
            hump.append(capitalizedString);
        }
        return hump.toString();
    }
    /**
     * JSONObject转换成实体类
     * @param json
     *  @param clazz
     * @return
     */
    public  static<T> T jsonToObject(JSONObject json, Class<T> clazz) {
        JSONObject  jsonObject = new JSONObject();
        json.entrySet().stream().forEach(entry -> {
            String key = entry.getKey();
            Object value = entry.getValue();
            String fieldName = UnderlineToHump(key);
            jsonObject.put(fieldName, value);
        });
        T obj = JSONObject.parseObject(jsonObject.toJSONString(), clazz);
        return obj;
    }
}
