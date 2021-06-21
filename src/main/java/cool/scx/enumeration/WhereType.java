package cool.scx.enumeration;

public enum WhereType {
    /**
     * 等于
     */
    EQUAL,
    /**
     * 不等于
     */
    NOT_EQUAL,
    /**
     * 小于
     */
    LESS_THAN,
    /**
     * 小于等于
     */
    EQUAL_OR_LESS_THAN,
    /**
     * 大于
     */
    GREATER_THAN,
    /**
     * 大于等于
     */
    EQUAL_OR_GREATER_THAN,
    /**
     * 包含
     */
    CONTAIN,
    /**
     * Like todo 此处需要针对匹配模式进行处理
     */
    LIKE,
    /**
     * IN
     */
    IN,
}
