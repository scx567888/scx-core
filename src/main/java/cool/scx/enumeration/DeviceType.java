package cool.scx.enumeration;

/**
 * 登录设备类型
 *
 * @author 司昌旭
 * @version 1.0.2
 */
public enum DeviceType {

    /**
     * 安卓设备
     */
    ANDROID("ANDROID"),

    /**
     * 苹果设备
     */
    APPLE("APPLE"),

    /**
     * 后台管理
     */
    ADMIN("ADMIN"),

    /**
     * 网页
     */
    WEBSITE("WEBSITE"),

    /**
     * 未知
     */
    UNKNOWN("UNKNOWN");

    private final String value;

    DeviceType(String value) {
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
