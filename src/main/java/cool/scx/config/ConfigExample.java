package cool.scx.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.util.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 配置文件实例
 *
 * @author scx567888
 * @version 1.3.0
 */
public class ConfigExample {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Object> configMapping = new HashMap<>();

    /**
     * 向其中添加一个配置
     *
     * @param keyPath keyPath
     * @param value   value
     * @return 当前实例方便链式调用
     */
    public ConfigExample add(String keyPath, Object value) {
        configMapping.put(keyPath, value);
        return this;
    }

    /**
     * 向其中添加一个配置
     *
     * @param map a {@link java.util.Map} object
     * @return 当前实例方便链式调用
     */
    public ConfigExample add(Map<String, Object> map) {
        configMapping.putAll(MapUtils.flatMap(map, null));
        return this;
    }

    /**
     * 向其中添加一个配置
     *
     * @param keyPath keyPath
     * @param type    a {@link java.lang.Class} object
     * @param <T>     a T class
     * @return 当前实例方便链式调用
     */
    public <T> T get(String keyPath, Class<T> type) {
        Object value = configMapping.get(keyPath);
        if (value != null) {
            if (isArrayOrList(type) && !isArrayOrList(value.getClass())) {
                value = new Object[]{value};
            }
            return objectMapper.convertValue(value, type);
        } else {
            return null;
        }
    }

    /**
     * 向其中添加一个配置
     *
     * @param keyPath keyPath
     * @return 当前实例方便链式调用
     */
    public Object get(String keyPath) {
        return configMapping.get(keyPath);
    }

    /**
     * <p>clear.</p>
     */
    public void clear() {
        configMapping.clear();
    }

    private static boolean isArrayOrList(Class<?> type) {
        return List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type) || type.isArray();
    }

}
