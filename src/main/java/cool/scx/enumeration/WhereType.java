package cool.scx.enumeration;

/**
 * WhereType <br>
 * where 查询条件的一些类型 如果无法满足要求请使用 whereSQL
 *
 * @author 司昌旭
 * @version 1.2.0
 */
public enum WhereType {

    /**
     * 为空
     */
    IS_NULL(0, "IS NULL"),

    /**
     * 不为空
     */
    IS_NOT_NULL(0, "IS NOT NULL"),

    /**
     * 等于
     */
    EQUAL(1, "="),

    /**
     * 不等于
     */
    NOT_EQUAL(1, "<>"),

    /**
     * 小于
     */
    LESS_THAN(1, "<"),

    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUAL(1, "<="),

    /**
     * 大于
     */
    GREATER_THAN(1, ">"),

    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUAL(1, ">="),

    /**
     * json 包含 一般用于 数组判断
     */
    JSON_CONTAINS(1, "JSON_CONTAINS"),

    /**
     * Like
     */
    LIKE(1, "LIKE"),

    /**
     * Not Like
     */
    NOT_LIKE(1, "NOT LIKE"),

    /**
     * Like 正则表达式
     */
    LIKE_REGEX(1, "LIKE"),

    /**
     * Like 正则表达式
     */
    NOT_LIKE_REGEX(1, "NOT LIKE"),

    /**
     * IN
     */
    IN(1, "IN"),

    /**
     * NOT IN
     */
    NOT_IN(1, "NOT IN"),

    /**
     * 在之间
     */
    BETWEEN(2, "BETWEEN"),

    /**
     * 不在之间
     */
    NOT_BETWEEN(2, "NOT BETWEEN");

    /**
     * 参数数量 用于校验
     */
    private final int paramSize;

    /**
     * 关键词
     */
    private final String keyWord;

    /**
     * 设置 参数数量 和关键字
     *
     * @param paramSize p
     * @param keyWord   k
     */
    WhereType(int paramSize, String keyWord) {
        this.paramSize = paramSize;
        this.keyWord = keyWord;
    }

    /**
     * 获取参数数量
     *
     * @return 参数数量
     */
    public int paramSize() {
        return paramSize;
    }

    /**
     * 获取关键词
     *
     * @return key
     */
    public String keyWord() {
        return keyWord;
    }

}
