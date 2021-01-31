package cool.scx.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public final class ObjectUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> mapType = new TypeReference<>() {
    };

    static {
        var timeModule = new JavaTimeModule();
        var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        objectMapper.registerModule(timeModule);
    }

    public static String beanToJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return null;
        }
    }

    public static JsonNode JsonToTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T jsonToBean(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    /**
     * 处理字符串，基础类型以及对应的包装类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseSimpleType(Object value, Class<T> targetClass) {
        if (value == null || targetClass == value.getClass()) {
            return (T) value;
        }
        if (targetClass == Integer.class || targetClass == int.class) {
            return (T) Integer.valueOf(value.toString());
        }
        if (targetClass == Boolean.class || targetClass == boolean.class) {
            return (T) Boolean.valueOf(value.toString());
        }
        if (targetClass == Byte.class || targetClass == byte.class) {
            return (T) Byte.valueOf(value.toString());
        }
        if (targetClass == Character.class || targetClass == char.class) {
            return (T) value;
        }
        if (targetClass == Double.class || targetClass == double.class) {
            return (T) Double.valueOf(value.toString());
        }
        if (targetClass == Float.class || targetClass == float.class) {
            return (T) Float.valueOf(value.toString());
        }
        if (targetClass == Long.class || targetClass == long.class) {
            return (T) Long.valueOf(value.toString());
        }
        if (targetClass == Short.class || targetClass == short.class) {
            return (T) Short.valueOf(value.toString());
        }

        return null;
    }

    public static Map<String, Object> beanToMap(Object o) {
        return objectMapper.convertValue(o, mapType);
    }

    public static Map<String, Object> beanToMapWithIndex(Integer index, Object o) {
        var clazzFields = o.getClass().getFields(); // 获取所有方法
        var objectMap = new HashMap<String, Object>(1 + (int) (clazzFields.length / 0.75));
        for (var field : clazzFields) {
            objectMap.put("list" + index + "." + field.getName(), getFieldValue(field, o));
        }
        return objectMap;
    }

    /**
     * 获取字段值
     *
     * @param field  字段
     * @param target 字段所属实例对象
     * @return a
     */
    public static Object getFieldValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
