package cool.scx.boot;

import cool.scx.business.license.LicenseService;
import cool.scx.context.ScxContext;
import cool.scx.util.Ansi;
import cool.scx.util.LogUtils;
import cool.scx.web.ScxServer;

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
     * <p>init.</p>
     */
    public static void init() {
        Ansi.OUT.brightCyan("校验 license 中 ...").ln();
        var licenseRight = licenseService.passLicense();
        if (!licenseRight) {
            ScxServer.stopServer();
            LogUtils.recordLog("license 已失效!!! 请联系服务商...");
            System.exit(0);
        } else {
            LogUtils.recordLog("license 通过校验 ...");
        }
    }
}
