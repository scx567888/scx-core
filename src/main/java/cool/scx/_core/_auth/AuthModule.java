package cool.scx._core._auth;

import cool.scx.ScxModule;

/**
 * 拓展模块 (认证模块)
 * 提供功能 : [ 基本认证逻辑 , license 认证]
 */
public class AuthModule implements ScxModule {

    private static AuthModuleOption AUTH_MODULE_OPTION;

    public AuthModule() {
        AUTH_MODULE_OPTION = AuthModuleOption.Default();
    }

    public AuthModule(AuthModuleOption authModuleOption) {
        AUTH_MODULE_OPTION = authModuleOption;
    }

    public static AuthModuleOption getAuthModuleOption() {
        return AUTH_MODULE_OPTION;
    }

    @Override
    public void init() {
        AuthConfig.initConfig();
    }
}
