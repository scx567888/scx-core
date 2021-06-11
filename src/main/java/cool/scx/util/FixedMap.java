package cool.scx.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 固定大小的 map
 * <p>
 * 超出指定大小 会将先前的元素移除  一般用来做缓存防止内存占用过高
 *
 * @param <K> key
 * @param <V> value
 * @author 司昌旭
 * @version 1.0.10
 */
public class FixedMap<K, V> extends LinkedHashMap<K, V> {

    /**
     * 最大容量
     */
    private final int maxSize;

    /**
     * 初始化一个定量的 map 容器
     *
     * @param maxSize 最大容量
     */
    public FixedMap(int maxSize) {
        if (maxSize < 1) {
            throw new IllegalArgumentException("maxSize must be >= 1");
        }
        this.maxSize = maxSize;
    }

    /**
     * {@inheritDoc}
     * <p>
     * removeEldestEntry
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
