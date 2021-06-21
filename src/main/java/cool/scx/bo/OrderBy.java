package cool.scx.bo;

import cool.scx.enumeration.SortType;

import java.util.HashMap;
import java.util.Map;

/**
 * 排序
 */
public class OrderBy {

    public Map<String, SortType> orderByList = new HashMap<>();

    public OrderBy() {

    }

    public OrderBy add(String orderByColumn, SortType sortType) {
        orderByList.put(orderByColumn, sortType);
        return this;
    }
}
