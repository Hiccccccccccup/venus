package com.jozz.venus.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonUtils {
    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * @param jsonData json数据
     * @param beanType 目标类型
     * @param <T>
     * @return 将json数据转换成java类型
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param jsonData json数据
     * @param beanType 目标类型
     * @param <T>
     * @return 将json数据转换成集合pojo
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param obj 对象
     * @return 数据转换成Json格式的String
     */
    public static String toJson(Object obj){
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
