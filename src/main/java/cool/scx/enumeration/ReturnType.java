package cool.scx.enumeration;

/**
 * <p>ReturnType class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public enum ReturnType {
    JSON("JSON"),
    HTML("HTML"),
    FILE("FILE"),
    AUTO("AUTO");

    private final String return_type_str;

    ReturnType(String returnTypeStr) {
        this.return_type_str = returnTypeStr;
    }


    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public String toString() {
        return this.return_type_str;
    }
}
