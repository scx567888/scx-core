package cool.scx._module.auth;

import cool.scx.ScxModule;

/**
 * 拓展模块 (认证模块)
 * 提供功能 : [ 基本认证逻辑 ]
 *
 * @author 司昌旭
 * @version 1.1.11
 */
public class AuthModule implements ScxModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        AuthConfig.initConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appKey() {
        return "H8QS91GcuNGP9735";
    }

}
