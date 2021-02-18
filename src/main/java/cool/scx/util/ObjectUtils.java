package cool.scx.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import cool.scx.boot.ScxConfig;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>ObjectUtils class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ObjectUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> mapType = new TypeReference<>() {
    };
    private static final TypeReference<List<Map<String, Object>>> mapListType = new TypeReference<>() {
    };

    static {
        var timeModule = new JavaTimeModule();

        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ScxConfig.dateTimeFormatter));
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ScxConfig.dateTimeFormatter));
        objectMapper.registerModule(timeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * <p>beanToJson.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    public static String beanToJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>JsonToTree.</p>
     *
     * @param json a {@link java.lang.String} object.
     * @return a {@link com.fasterxml.jackson.databind.JsonNode} object.
     */
    public static JsonNode JsonToTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>jsonToBean.</p>
     *
     * @param json  a {@link java.lang.String} object.
     * @param clazz a {@link java.lang.Class} object.
     * @param <T>   a T object.
     * @return a T object.
     */
    public static <T> T jsonToBean(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>jsonNodeToBean.</p>
     *
     * @param jsonNode a {@link com.fasterxml.jackson.databind.JsonNode} object.
     * @param clazz    a {@link java.lang.Class} object.
     * @param <T>      a T object.
     * @return a T object.
     */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>mapToBean.</p>
     *
     * @param map   a {@link java.util.Map} object.
     * @param clazz a {@link java.lang.Class} object.
     * @param <T>   a T object.
     * @return a T object.
     */
    public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }


    /**
     * 处理字符串，基础类型以及对应的包装类型
     *
     * @param value       需要处理的值
     * @param targetClass 需要返回的类型
     * @param <T>         T
     * @return 处理后的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseSimpleType(Object value, Class<T> targetClass) {
        try {
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
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>beanToMap.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, Object> beanToMap(Object o) {
        return objectMapper.convertValue(o, mapType);
    }

    /**
     * <p>beanListToMapList.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Map<String, Object>> beanListToMapList(Object o) {
        return objectMapper.convertValue(o, mapListType);
    }

    /**
     * <p>beanToMapWithIndex.</p>
     *
     * @param index a {@link java.lang.Integer} object.
     * @param o     a {@link java.lang.Object} object.
     * @return a {@link java.util.Map} object.
     */
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
