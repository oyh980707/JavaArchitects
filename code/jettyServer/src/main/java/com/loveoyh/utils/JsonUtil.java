package com.loveoyh.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static ObjectMapper mapper = null;

    static {
        if (mapper == null) {
            SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapper = new ObjectMapper()
//                  null暂时参与序列化  .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
                    .setDateFormat(smt).setTimeZone(TimeZone.getTimeZone("GMT+8"));
        }
    }

    private static ObjectMapper mapperFE = null;

    static {
        if (mapperFE == null) {
            SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mapperFE = new ObjectMapper()
//                  null暂时参与序列化  .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false)
                    .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                    .setDateFormat(smt).setTimeZone(TimeZone.getTimeZone("GMT+8"));
            mapperFE.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
                @Override
                public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                    String fieldName = jsonGenerator.getOutputContext().getCurrentName();
                    try {
                        Field field = jsonGenerator.getCurrentValue().getClass().getDeclaredField(fieldName);
                        if (field.getType().equals(String.class)) {
                            jsonGenerator.writeString("");
                        } else {
                            jsonGenerator.writeNull();
                        }
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException("No field for " + fieldName);
                    }

                }
            });
        }
    }

    public static ObjectMapper getInstance() {
        return mapper;
    }


    /**
     * 将对象转成json格式
     *
     * @param object
     * @return String
     */
    public static String getJsonString(Object object) {
        if(object == null){
            return null;
        }
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象转成json格式
     *
     * @param object
     * @return String
     */
    public static String getJsonStringForFE(Object object) {
        if(object == null){
            return null;
        }
        try {
            return mapperFE.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将json转成特定的cls的对象
     *
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> T jsonToBean(String jsonString, Class<T> cls) {
        try {
            if (jsonString == null || StringUtils.isEmpty(jsonString)) {
                return null;
            }
            if (cls == String.class) {
                return (T) jsonString;
            } else if (cls == Map.class || cls == HashMap.class) {
                return (T) jsonToMap(jsonString);
            }
            return mapper.readValue(jsonString, cls);
        } catch (IOException e) {
            logger.warn("json to bean error, {}", jsonString);
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> jsonToList(String jsonString, final Class<T> cls) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, cls);
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> List<T> jsonToMapList(String jsonString) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructCollectionLikeType(List.class, Map.class);
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Map<String, Object> jsonToMap(String jsonString) {
        try {
            return mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> jsonToStrMap(String jsonString) {
        try {
            return mapper.readValue(jsonString, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz) || clazz.isArray();
    }
}
