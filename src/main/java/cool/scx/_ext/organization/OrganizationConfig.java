package cool.scx._ext.organization;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class OrganizationConfig {

    private static boolean confusionLoginError;

    /**
     * 初始化方法
     */
    public static void initConfig() {
        confusionLoginError = ScxConfig.get("core.auth.confusion-login-error", false,
                s -> Ansi.out().magenta("Y 是否混淆登录错误                     \t -->\t " + (s ? "是" : "否")).ln(),
                f -> Ansi.out().red("N 未检测到 core.auth.confusion-login-error \t -->\t 已采用默认值 : " + f).ln());
    }

    /**
     * 混淆登录错误
     *
     * @return b
     */
    public static boolean confusionLoginError() {
        return confusionLoginError;
    }

}
