package cool.scx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ObjectUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();

    public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
        T bean; // 构建对象
        try {
            bean = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (map == null) {
            return bean;
        }
        for (Field field : clazz.getFields()) {
            var fieldType = field.getType();
            var mapValue = map.get(field.getName());
            if (mapValue != null) {
                try {
                    var value = parseSimpleType(mapValue.toString(), fieldType);
                    field.set(bean, value);
                } catch (Throwable ignored) {

                }

            }
        }
        return bean;
    }

    /**
     * 处理字符串，基础类型以及对应的包装类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseSimpleType(String value, Class<T> targetClass) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        var wrapType = Primitives.wrap(targetClass);
        if (Primitives.allWrapperTypes().contains(wrapType)) {
            MethodHandle valueOf = null;
            try {
                valueOf = MethodHandles.lookup().unreflect(wrapType.getMethod("valueOf", String.class));
                return (T) valueOf.invoke(value);
            } catch (Throwable ignored) {

            }
        } else if (targetClass == String.class) {
            return (T) value;
        }

        return null;
    }

    public static Map<String, Object> beanToMap(Object o) {
        if (o == null) {
            return null;
        }
        var clazzFields = o.getClass().getFields(); // 获取所有方法
        var objectMap = new HashMap<String, Object>(1 + (int) (clazzFields.length / 0.75));
        for (Field field : clazzFields) {
            try {
                objectMap.put(field.getName(), field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return objectMap;
    }

    public static Map<String, Object> beanToMapWithIndex(Integer index, Object o) {
        var clazzFields = o.getClass().getFields(); // 获取所有方法
        var objectMap = new HashMap<String, Object>(1 + (int) (clazzFields.length / 0.75));
        for (Field field : clazzFields) {
            var fieldName = field.getName(); // 截取属性名
            try {
                objectMap.put("list" + index + "." + fieldName, field.get(o));
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    @SuppressWarnings("unchecked")
    public static <T> T[] concatArray(T[] arr1, T[] arr2) {
        final T[] result = (T[]) Array.newInstance(arr1.getClass().getComponentType(), arr1.length + arr2.length);
        int index = 0;
        for (T e : arr1) {
            result[index] = e;
            index++;
        }
        for (T e : arr2) {
            result[index] = e;
            index++;
        }
        return result;
    }

}
