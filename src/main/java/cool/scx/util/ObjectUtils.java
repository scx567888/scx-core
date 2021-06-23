package cool.scx.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import cool.scx.config.ScxConfig;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理对象的工具类<br>
 * 本质上就是对 ObjectMapper 进行了一些简单的封装
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ObjectUtils {

    /**
     * 忽略 Jackson 注解的 objectMapper  用于前台向后台发送数据和 后台数据序列化
     */
    private static final ObjectMapper OBJECT_MAPPER = getObjectMapper();

    /**
     * 使用 Jackson 注解的 objectMapper 用于向前台发送数据
     */
    private static final ObjectMapper OBJECT_MAPPER_USE_ANNOTATIONS = getObjectMapper();

    /**
     * 类型工厂
     */
    private static final TypeFactory TYPE_FACTORY = OBJECT_MAPPER.getTypeFactory();

    /**
     * map 类
     */
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    static {
        //设置 OBJECT_MAPPER 使用 Jackson 注解
        OBJECT_MAPPER_USE_ANNOTATIONS.configure(MapperFeature.USE_ANNOTATIONS, true);
    }

    /**
     * 获取 objectMapper 对象
     */
    private static ObjectMapper getObjectMapper() {
        var timeModule = new JavaTimeModule();
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ScxConfig.DATETIME_FORMATTER));
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ScxConfig.DATETIME_FORMATTER));
        var o = new ObjectMapper();
        //初始化正常的 objectMap
        o.registerModule(timeModule);
        o.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        o.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        o.getSerializerProvider().setNullKeySerializer(new JsonSerializer<>() {
            @Override
            public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeFieldName("");
            }
        });
        o.configure(MapperFeature.USE_ANNOTATIONS, false);
        return o;
    }

    /**
     * 对象转 json 使用 注解如 @JsonIgnore
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    public static String beanToJsonUseAnnotations(Object o) {
        try {
            return OBJECT_MAPPER_USE_ANNOTATIONS.writeValueAsString(o);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 对象转 json 不使用 注解如 @JsonIgnore
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link java.lang.String} object.
     */
    public static String beanToJson(Object o) {
        try {
            return OBJECT_MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 对象转 json 使用 注解如 @JsonIgnore
     *
     * @param o a {@link java.lang.Object} object.
     * @return an array of {@link byte} objects.
     */
    public static byte[] beanToByteArrayUseAnnotations(Object o) {
        try {
            return OBJECT_MAPPER_USE_ANNOTATIONS.writeValueAsBytes(o);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
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
            return OBJECT_MAPPER.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>JsonToTree.</p>
     *
     * @param json a {@link java.lang.String} object.
     * @param type a {@link java.lang.Class} object.
     * @param <T>  a T object.
     * @return a {@link com.fasterxml.jackson.databind.JsonNode} object.
     */
    public static <T> T JsonToBean(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>JsonToBean.</p>
     *
     * @param json a {@link java.lang.String} object
     * @param type a {@link java.lang.reflect.Type} object
     * @param <T>  a T class
     * @return a T object
     */
    public static <T> T JsonToBean(String json, Type type) {
        try {
            return OBJECT_MAPPER.readValue(json, TYPE_FACTORY.constructType(type));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>jsonToMap.</p>
     *
     * @param json a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, Object> jsonToMap(String json) {
        try {
            return OBJECT_MAPPER.readValue(json, MAP_TYPE);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * <p>jsonNodeToBean.</p>
     *
     * @param jsonNode a {@link com.fasterxml.jackson.databind.JsonNode} object.
     * @param type     a {@link java.lang.Class} object.
     * @param <T>      a T object.
     * @return a T object.
     * @throws java.io.IOException if any.
     */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Type type) throws IOException {
        var reader = OBJECT_MAPPER.readerFor(TYPE_FACTORY.constructType(type));
        return reader.readValue(jsonNode);
    }

    /**
     * <p>mapToBeanUnUseAnnotations.</p>
     *
     * @param map   a {@link java.util.Map} object.
     * @param clazz a {@link java.lang.Class} object.
     * @param <T>   a T object.
     * @return a T object.
     */
    public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(map, clazz);
    }

    /**
     * <p>beanToMap.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return a {@link java.util.Map} object.
     */
    public static Map<String, Object> beanToMap(Object o) {
        return OBJECT_MAPPER.convertValue(o, MAP_TYPE);
    }

    /**
     * 处理字符串，基础类型以及对应的包装类型
     *
     * @param value       需要处理的值
     * @param targetClass 需要返回的类型
     * @param <T>         T
     * @return 处理后的值
     */
    public static <T> T parseSimpleType(Object value, Class<T> targetClass) {
        if (value == null) {
            throw new NullPointerException();
        } else {
            return OBJECT_MAPPER.convertValue(value, targetClass);
        }
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
