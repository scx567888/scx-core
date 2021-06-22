package cool.scx._core._auth;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.Tidy;

/**
 * 核心模块配置文件
 *
 * @author 司昌旭
 * @version 1.1.2
 */
public class AuthConfig {

    private static AuthEasyToUse coreEasyToUse;

    /**
     * 初始化方法
     */
    public static void initConfig() {
        Ansi.OUT.magenta("AuthConfig 初始化中...").ln();
        coreEasyToUse = new AuthEasyToUse();
        Ansi.OUT.magenta("AuthConfig 初始化完成...").ln();
    }

    /**
     * <p>license.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String license() {
        return coreEasyToUse.license;
    }

    /**
     * <p>loginErrorLockTimes.</p>
     *
     * @return a int.
     */
    public static int loginErrorLockTimes() {
        return coreEasyToUse.loginErrorLockTimes;
    }

    /**
     * <p>loginErrorLockSecond.</p>
     *
     * @return a int.
     */
    public static int loginErrorLockSecond() {
        return coreEasyToUse.loginErrorLockSecond;
    }

    /**
     * <p>confusionLoginError.</p>
     *
     * @return a boolean.
     */
    public static boolean confusionLoginError() {
        return coreEasyToUse.confusionLoginError;
    }

    static class AuthEasyToUse {

        /**
         * 混淆登录
         */
        final boolean confusionLoginError;

        /**
         * license
         */
        final String license;

        /**
         * 登录错误锁定次数
         */
        final int loginErrorLockTimes;

        /**
         * 登录错误锁定时间
         */
        final int loginErrorLockSecond;

        AuthEasyToUse() {

            confusionLoginError = ScxConfig.get("core.auth.confusion-login-error", false,
                    s -> Ansi.OUT.magenta("Y 是否混淆登录错误                     \t -->\t " + (s ? "是" : "否")).ln(),
                    f -> Ansi.OUT.red("N 未检测到 core.auth.confusion-login-error \t -->\t 已采用默认值 : " + f).ln());

            license = ScxConfig.get("core.auth.license", "", Tidy::NoCode,
                    f -> Ansi.OUT.red("N 未检测到 core.auth.license               \t -->\t 请检查 license 是否正确").ln());

            loginErrorLockTimes = ScxConfig.get("core.auth.login-error-lock-times", 999,
                    s -> Ansi.OUT.magenta("Y 登录错误锁定次数                     \t -->\t " + s + " 次").ln(),
                    f -> Ansi.OUT.red("N 未检测到 core.auth.login-error-lock-times\t -->\t 已采用默认值 : " + f).ln());

            loginErrorLockSecond = ScxConfig.get("core.auth.login-error-lock-second", 10,
                    s -> Ansi.OUT.magenta("Y 登录错误锁定时间                     \t -->\t " + s + " 秒").ln(),
                    f -> Ansi.OUT.red("N 未检测到 core.auth.login-error-lock-second\t -->\t 已采用默认值 : " + f).ln());
        }
    }

}
