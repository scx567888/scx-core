package cool.scx.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import cool.scx.dao.BaseModel;
import cool.scx.config.ScxConfig;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
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

    //普通的object mapper
    private static final ObjectMapper objectMapper = new ObjectMapper();
    //忽略注解 的 mapper
    private static final ObjectMapper unUseAnnotationsObjectMapper = new ObjectMapper();
    private static final TypeFactory typeFactory;
    private static final TypeReference<Map<String, Object>> mapType = new TypeReference<>() {
    };

    static {
        //初始化正常的 objectMap
        objectMapper.registerModule(getJavaTimeModule());
        setObjectMapperConfig(objectMapper);
        objectMapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer());
        typeFactory = objectMapper.getTypeFactory();
        setNullOnError(objectMapper);

        //初始化 忽略注解 的 mapper
        unUseAnnotationsObjectMapper.registerModule(getJavaTimeModule());
        setObjectMapperConfig(unUseAnnotationsObjectMapper);
        unUseAnnotationsObjectMapper.getSerializerProvider().setNullKeySerializer(new NullKeySerializer());
        setNullOnError(unUseAnnotationsObjectMapper);
        unUseAnnotationsObjectMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
    }

    /**
     * todo 此处会对前台发送的不合法数据进行置空处理 若影响到业务开发 请注释掉此段代码
     * 例子
     * json   {"username": "test","password": [1,2,3,4,5,6,7,8,8,9]}
     * class  public class User  { public String username; public String password; }
     * 有此代码 -- username=test;  password=null;
     * 无此代码 --   com.fasterxml.jackson.databind.exc.MismatchedInputException
     *
     * @param o a {@link com.fasterxml.jackson.databind.ObjectMapper} object.
     */
    public static void setNullOnError(ObjectMapper o) {
        o.addHandler(new DeserializationProblemHandler() {
            @Override
            public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType, JsonToken t, JsonParser p, String failureMsg) throws IOException {
                return null;
            }
        });
    }

    private static JavaTimeModule getJavaTimeModule() {
        var timeModule = new JavaTimeModule();
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ScxConfig.dateTimeFormatter()));
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ScxConfig.dateTimeFormatter()));
        return timeModule;
    }

    private static void setObjectMapperConfig(ObjectMapper o) {
        o.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        o.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
            return "";
        }
    }

    /**
     * <p>beanToByteArray.</p>
     *
     * @param o a {@link java.lang.Object} object.
     * @return an array of {@link byte} objects.
     */
    public static byte[] beanToByteArray(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
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
            return objectMapper.readTree(json);
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
            return objectMapper.readValue(json, mapType);
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
     */
    public static <T> T jsonNodeToBean(JsonNode jsonNode, Type type) {
        var reader = objectMapper.readerFor(typeFactory.constructType(type));
        try {
            return reader.readValue(jsonNode);
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
        try {
            return objectMapper.convertValue(map, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>mapToBeanUnUseAnnotations.</p>
     *
     * @param map   a {@link java.util.Map} object.
     * @param clazz a {@link java.lang.Class} object.
     * @param <T>   a T object.
     * @return a T object.
     */
    public static <T> T mapToBeanUnUseAnnotations(Map<String, ?> map, Class<T> clazz) {
        try {
            return unUseAnnotationsObjectMapper.convertValue(map, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * <p>mapToBeanNotNull.</p>
     *
     * @param map   a {@link java.util.Map} object.
     * @param clazz a {@link java.lang.Class} object.
     * @param <T>   a T object.
     * @return a T object.
     */
    public static <T> T mapToBeanNotNull(Map<String, ?> map, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.convertValue(map, clazz);

        } catch (Exception ignored) {

        }
        if (t == null) {
            try {
                t = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ignored) {

            }
        }
        return t;
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

    // 将对象转成字符串

    /**
     * <p>objectToMapDeep.</p>
     *
     * @param obj a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    public static Object objectToMapDeep(Object obj) {
        Object ooo = null;
        try {
            String s = objectMapper.writeValueAsString(obj);
            if (obj instanceof List) {
                ooo = objectMapper.readValue(s, List.class);
            } else if (obj instanceof BaseModel || obj instanceof Map) {
                ooo = objectMapper.readValue(s, Map.class);
            } else {
                ooo = obj;
            }
        } catch (Exception ignored) {

        }
        return ooo;
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

    private static class NullKeySerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeFieldName("");
        }
    }
}
