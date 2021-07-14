package cool.scx._ext.organization;

import cool.scx.BaseModule;

/**
 * 拓展模块 (组织机构)
 *
 * @author scx567888
 * @version 1.1.11
 */
public class OrganizationModule implements BaseModule {

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        OrganizationConfig.initConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appKey() {
        return "H8QS91GcuNGP9735";
    }

}
