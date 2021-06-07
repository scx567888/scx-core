package cool.scx._core._auth;

/**
 * 认证模块配置类
 */
public class AuthModuleOption {

    public final boolean loginUseLicense;
    public final String licenseAdminPassword;

    public AuthModuleOption(boolean loginUseLicense, String licenseAdminPassword) {
        this.loginUseLicense = loginUseLicense;
        this.licenseAdminPassword = licenseAdminPassword;
    }

    public static AuthModuleOption Default() {
        return new AuthModuleOption(true, "123456");
    }

    public static boolean loginUseLicense() {
        return AuthModule.getAuthModuleOption().loginUseLicense;
    }

    public static String licenseAdminPassword() {
        return AuthModule.getAuthModuleOption().licenseAdminPassword;
    }

}
