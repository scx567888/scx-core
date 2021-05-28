package cool.scx.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 有限大小的 map 超出指定大小 会将先前的元素移除  一般用来做固定量的内存缓存防止内存占用过大
 *
 * @param <K> key
 * @param <V> value
 * @author 司昌旭
 * @version 1.0.10
 */
public class LimitedMap<K, V> extends LinkedHashMap<K, V> {
    /**
     * 最大容量
     */
    private final int maxSize;

    /**
     * 初始化一个定量的 map 容器
     *
     * @param maxSize 最大容量
     */
    public LimitedMap(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * removeEldestEntry
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
