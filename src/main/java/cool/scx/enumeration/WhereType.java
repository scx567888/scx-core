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
    IS_NULL("IS NULL", 0),

    /**
     * 不为空
     */
    IS_NOT_NULL("IS NOT NULL", 0),

    /**
     * 等于
     */
    EQUAL("=", 1),

    /**
     * 不等于
     */
    NOT_EQUAL("<>", 1),

    /**
     * 小于
     */
    LESS_THAN("<", 1),

    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUAL("<=", 1),

    /**
     * 大于
     */
    GREATER_THAN(">", 1),

    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUAL(">=", 1),

    /**
     * 包含
     */
    CONTAIN("CONTAINS", 1),

    /**
     * Like
     */
    LIKE("LIKE", 1),

    /**
     * not like
     */
    NOT_LIKE("NOT LIKE", 1),

    /**
     * Like 正则表达式
     */
    LIKE_REGEX("LIKE", 1),

    /**
     * Like 正则表达式
     */
    NOT_LIKE_REGEX("NOT LIKE", 1),

    /**
     * IN
     */
    IN("IN", 1),

    /**
     * NOT IN
     */
    NOT_IN("NOT IN", 1),

    /**
     * NOT
     */
    NOT("NOT", 1),

    /**
     * 在之间
     */
    BETWEEN("BETWEEN", 2),

    /**
     * 不在之间
     */
    NOT_BETWEEN("NOT BETWEEN", 2),

    /**
     * 存在
     */
    EXISTS("EXISTS", 1),

    /**
     * 不存在
     */
    NOT_EXISTS("NOT EXISTS", 1);

    /**
     * sql 语句
     */
    private final String value;

    /**
     * 参数数量 用于校验
     */
    private final int paramSize;

    WhereType(String value, int paramSize) {
        this.value = value;
        this.paramSize = paramSize;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public String toString() {
        return this.value;
    }

    /**
     * <p>paramSize.</p>
     *
     * @return a int
     */
    public int paramSize() {
        return paramSize;
    }
}
