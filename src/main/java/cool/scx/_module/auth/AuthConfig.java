package cool.scx._module.auth;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class AuthConfig {

    private static boolean confusionLoginError;

    /**
     * 初始化方法
     */
    public static void initConfig() {
        Ansi.OUT.magenta("AuthConfig 初始化中...").ln();

        confusionLoginError = ScxConfig.get("core.auth.confusion-login-error", false,
                s -> Ansi.OUT.magenta("Y 是否混淆登录错误                     \t -->\t " + (s ? "是" : "否")).ln(),
                f -> Ansi.OUT.red("N 未检测到 core.auth.confusion-login-error \t -->\t 已采用默认值 : " + f).ln());

        Ansi.OUT.magenta("AuthConfig 初始化完成...").ln();
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
