package cool.scx.enumeration;

/**
 * 登录设备
 *
 * @author 司昌旭
 * @version 1.0.2
 */
public enum Device {
    ANDROID("ANDROID"),
    APPLE("APPLE"),
    ADMIN("ADMIN"),
    WEBSITE("WEBSITE"),
    UNKNOWN("UNKNOWN");

    private final String device_str;

    Device(String device_str) {
        this.device_str = device_str;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public String toString() {
        return this.device_str;
    }
}
