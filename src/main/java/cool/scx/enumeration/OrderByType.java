package cool.scx.enumeration;

/**
 * 排序类型
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public enum OrderByType {

    /**
     * 正序 : 也就是从小到大 (1,2,3,4,5,6)
     */
    ASC("ASC"),

    /**
     * 倒序 : 也就是从大到小 (6,5,4,3,2,1)
     */
    DESC("DESC");

    private final String value;

    OrderByType(String value) {
        this.value = value;
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

}
