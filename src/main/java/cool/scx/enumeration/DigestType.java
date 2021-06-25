package cool.scx.enumeration;

/**
 * 摘要算法的类型枚举
 *
 * @author 司昌旭
 * @version 1.2.1
 */
public enum DigestType {

    MD5("MD5"),
    SHA_1("SHA-1"),
    SHA_256("SHA-256"),
    SHA_384("SHA-384"),
    SHA_512("SHA-512");

    /**
     * 算法名称
     */
    private final String algorithmsName;

    /**
     * 设置 算法名称
     *
     * @param algorithmsName 算法名称
     */
    DigestType(String algorithmsName) {
        this.algorithmsName = algorithmsName;
    }

    /**
     * 获取算法名称
     *
     * @return 算法名称
     */
    public String algorithmsName() {
        return this.algorithmsName;
    }

}
