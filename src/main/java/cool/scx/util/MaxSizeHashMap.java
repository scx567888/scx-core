package cool.scx.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 固定大小的 map 超出指定大小 会将先前的元素移除  一般用来做缓存
 *
 * @param <K> key
 * @param <V> value
 * @author scx56
 * @version $Id: $Id
 */
public class MaxSizeHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    /**
     * <p>Constructor for MaxSizeHashMap.</p>
     *
     * @param maxSize a int.
     */
    public MaxSizeHashMap(int maxSize) {
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
