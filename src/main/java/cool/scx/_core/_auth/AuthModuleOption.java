package cool.scx._core._auth;

/**
 * 认证模块配置类
 *
 * @author 司昌旭
 * @version 1.1.11
 */
public class AuthModuleOption {

    public final boolean loginUseLicense;
    public final String licenseAdminPassword;

    /**
     * <p>Constructor for AuthModuleOption.</p>
     *
     * @param loginUseLicense      a boolean
     * @param licenseAdminPassword a {@link java.lang.String} object
     */
    public AuthModuleOption(boolean loginUseLicense, String licenseAdminPassword) {
        this.loginUseLicense = loginUseLicense;
        this.licenseAdminPassword = licenseAdminPassword;
    }

    /**
     * <p>Default.</p>
     *
     * @return a {@link cool.scx._core._auth.AuthModuleOption} object
     */
    public static AuthModuleOption Default() {
        return new AuthModuleOption(true, "123456");
    }

    /**
     * <p>loginUseLicense.</p>
     *
     * @return a boolean
     */
    public static boolean loginUseLicense() {
        return AuthModule.getAuthModuleOption().loginUseLicense;
    }

    /**
     * <p>licenseAdminPassword.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public static String licenseAdminPassword() {
        return AuthModule.getAuthModuleOption().licenseAdminPassword;
    }

}
