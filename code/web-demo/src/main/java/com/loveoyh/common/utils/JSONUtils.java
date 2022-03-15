package com.loveoyh.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);

    private static ObjectMapper defaultMapper = new ObjectMapper();


    /**
     * 将对象转换为JSON
     */
    public static String toJSON(Object object) {
        return JSON.toJSONString(object);
    }


    /**
     * 将JSON转换为Map
     */
    public static <K, V> Map<K, V> parse2Map(String json, Class<K> keyClass, Class<V> valueClass) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return defaultMapper.readValue(json, TypeFactory.defaultInstance().constructMapLikeType(Map.class, keyClass, valueClass));
        } catch (Exception e) {
            LOGGER.error("JSONUtils#parse2Map error json:{}", json, e);
        }
        return new HashMap<K, V>(2);
    }


    /**
     * 将JSON转换为对象
     */
    public static <T> T parse2Object(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            LOGGER.error("JSONUtils#parse2Object error json:{}", json, e);
            return null;
        }
    }

    public static <T> List<T> parse2List(String json, Class<T> clazz) {
        List<T> empty = new ArrayList<T>();
        if (StringUtils.isEmpty(json)) {
            return empty;
        }
        return JSONArray.parseArray(json, clazz);

    }

    public static Object getValueFromJson(String extra, String key){
        if (!StringUtils.isEmpty(extra)){
            Map<String, Object> map = parse2Map(extra, String.class, Object.class);
            if(map!=null){
                if (map.containsKey(key)){
                    return map.get(key);
                }
            }
        }
        return null;
    }

}
