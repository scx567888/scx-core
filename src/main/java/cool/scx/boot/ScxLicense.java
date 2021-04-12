package cool.scx.boot;

import cool.scx.context.ScxContext;
import cool.scx.core.license.LicenseService;
import cool.scx.util.Ansi;
import cool.scx.util.LogUtils;

/**
 * 校验 license 的工具类
 * 暂时只采用简单的校验方法
 *
 * @author 司昌旭
 * @version 0.5.0
 */
public class ScxLicense {

    private static final LicenseService licenseService;

    static {
        licenseService = ScxContext.getBean(LicenseService.class);
    }

    /**
     * 初始化 license
     */
    public static void checkLicense() {
        Ansi.OUT.brightCyan("校验 license 中 ...").ln();
        var licenseRight = licenseService.passLicense();
        if (!licenseRight) {
            LogUtils.recordLog("license 已失效!!! 请联系服务商...");
        } else {
            LogUtils.recordLog("license 通过校验 ...");
        }
    }
}
