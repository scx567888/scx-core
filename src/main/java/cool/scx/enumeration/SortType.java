package cool.scx.enumeration;

/**
 * <p>SortType class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public enum SortType {
    ASC(" ASC "),
    DESC(" DESC ");

    private final String sort_str;

    SortType(String code) {
        this.sort_str = code;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.sort_str;
    }
}
