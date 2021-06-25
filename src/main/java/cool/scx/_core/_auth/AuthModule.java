package cool.scx._core._auth;

import cool.scx.ScxModule;

/**
 * 拓展模块 (认证模块)
 * 提供功能 : [ 基本认证逻辑 , license 认证]
 *
 * @author 司昌旭
 * @version 1.1.11
 */
public class AuthModule implements ScxModule {

    private static AuthModuleOption AUTH_MODULE_OPTION;

    /**
     * <p>Constructor for AuthModule.</p>
     */
    public AuthModule() {
        AUTH_MODULE_OPTION = AuthModuleOption.Default();
    }

    /**
     * <p>Constructor for AuthModule.</p>
     *
     * @param authModuleOption a {@link cool.scx._core._auth.AuthModuleOption} object
     */
    public AuthModule(AuthModuleOption authModuleOption) {
        AUTH_MODULE_OPTION = authModuleOption;
    }

    /**
     * <p>getAuthModuleOption.</p>
     *
     * @return a {@link cool.scx._core._auth.AuthModuleOption} object
     */
    public static AuthModuleOption getAuthModuleOption() {
        return AUTH_MODULE_OPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        AuthConfig.initConfig();
    }

    @Override
    public String appKey() {
        return "H8QS91GcuNGP9735";
    }

}
