package cool.scx.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 固定大小的 map 超出指定大小 会将先前的元素移除  一般用来做缓存
 *
 * @param <K>
 * @param <V>
 */
public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public MaxSizeHashMap(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}