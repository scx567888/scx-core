package cool.scx.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 有限大小的 map 超出指定大小 会将先前的元素移除  一般用来做缓存
 *
 * @param <K> key
 * @param <V> value
 * @author 司昌旭
 * @version 1.0.10
 */
public class LimitedMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    /**
     * 参数为最大容量
     *
     * @param maxSize a int.
     */
    public LimitedMap(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
