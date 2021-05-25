package cool.scx.util;

import java.util.HashMap;
import java.util.Map;

/**
 * map 工具类
 *
 * @author 司昌旭
 * @version 1.1.2
 */
public class MapUtils {

    /**
     * 将嵌套的 map 扁平化
     *
     * @param source    源 map
     * @param parentKey a {@link java.lang.String} object.
     * @return 扁平化后的 map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> flatMap(Map<String, Object> source, String parentKey) {
        var result = new HashMap<String, Object>();
        var prefix = parentKey == null ? "" : parentKey + ".";
        source.forEach((key, value) -> {
            String newKey = prefix + key;
            if (value instanceof Map) {
                result.putAll(flatMap((Map) value, newKey));
            } else {
                result.put(newKey, value);
            }
        });
        return result;
    }
}
