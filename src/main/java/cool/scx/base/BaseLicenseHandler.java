package cool.scx.base;

import cool.scx.annotation.OneAndOnlyOneImpl;

/**
 * 校验 license 的工具类
 * 暂时只采用简单的校验方法
 *
 * @author 司昌旭
 * @version 0.5.0
 */
@OneAndOnlyOneImpl
public interface BaseLicenseHandler {

    /**
     * 初始化 license
     */
    default void checkLicense() {

    }

}
