package cool.scx.enumeration;

/**
 * <p>WhereType class.</p>
 *
 * @author 司昌旭
 * @version 1.2.0
 */
public enum WhereType {

    /**
     * 为空
     */
    IS_NULL(0),

    /**
     * 不为空
     */
    IS_NOT_NULL(0),

    /**
     * 等于
     */
    EQUAL(1),

    /**
     * 不等于
     */
    NOT_EQUAL(1),

    /**
     * 小于
     */
    LESS_THAN(1),

    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUAL(1),

    /**
     * 大于
     */
    GREATER_THAN(1),

    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUAL(1),

    /**
     * 包含
     */
    CONTAIN(1),

    /**
     * Like
     */
    LIKE(1),

    /**
     * not like
     */
    NOT_LIKE(1),

    /**
     * Like 正则表达式
     */
    LIKE_REGEX(1),

    /**
     * Like 正则表达式
     */
    NOT_LIKE_REGEX(1),

    /**
     * IN
     */
    IN(1),

    /**
     * NOT IN
     */
    NOT_IN(1),

    /**
     * NOT
     */
    NOT(1),

    /**
     * 在之间
     */
    BETWEEN(2),

    /**
     * 不在之间
     */
    NOT_BETWEEN(2),

    /**
     * 存在
     */
    EXISTS(1),

    /**
     * 不存在
     */
    NOT_EXISTS(1);

    /**
     * 参数数量 用于校验
     */
    private final int paramSize;

    WhereType(int paramSize) {
        this.paramSize = paramSize;
    }

    /**
     * 获取参数数量
     *
     * @return 参数数量
     */
    public int paramSize() {
        return paramSize;
    }

}
