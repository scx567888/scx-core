package cool.scx._core.config;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;
import cool.scx.util.Tidy;

import java.io.File;

/**
 * <p>CoreConfig class.</p>
 *
 * @author 司昌旭
 * @version 1.1.2
 */
public class CoreConfig {

    private static CoreEasyToUse coreEasyToUse;

    /**
     * <p>initConfig.</p>
     */
    public static void initConfig() {
        coreEasyToUse = new CoreEasyToUse();
    }

    /**
     * <p>license.</p>
     *
     * @return a {@link String} object.
     */
    public static String license() {
        return coreEasyToUse.license;
    }

    /**
     * <p>uploadFilePath.</p>
     *
     * @return a {@link File} object.
     */
    public static File uploadFilePath() {
        return coreEasyToUse.uploadFilePath;
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

    static class CoreEasyToUse {

        /**
         * 文件上传路径
         */
        final File uploadFilePath;

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

        public CoreEasyToUse() {

            String tempUploadFilePath = ScxConfig.get("core.upload-file-path", "/UploadFile/",
                    s -> Ansi.OUT.green("Y 文件上传目录                         \t -->\t " + FileUtils.getFileByRootModulePath(s)).ln(),
                    f -> Ansi.OUT.red("N 未检测到 core.upload-file-path       \t -->\t 已采用默认值 : " + f).ln());

            uploadFilePath = FileUtils.getFileByRootModulePath(tempUploadFilePath);

            confusionLoginError = ScxConfig.get("core.confusion-login-error", false,
                    s -> Ansi.OUT.green("Y 是否混淆登录错误                     \t -->\t " + (s ? "是" : "否")).ln(),
                    f -> Ansi.OUT.red("N 未检测到 core.confusion-login-error \t -->\t 已采用默认值 : " + f).ln());

            license = ScxConfig.get("core.license", "", Tidy::NoCode,
                    f -> Ansi.OUT.red("N 未检测到 core.license               \t -->\t 请检查 license 是否正确").ln());

            loginErrorLockTimes = ScxConfig.get("core.login-error-lock-times", 999,
                    s -> Ansi.OUT.green("Y 登录错误锁定次数                     \t -->\t " + s + " 次").ln(),
                    f -> Ansi.OUT.red("N 未检测到 core.login-error-lock-times\t -->\t 已采用默认值 : " + f).ln());

            loginErrorLockSecond = ScxConfig.get("core.login-error-lock-second", 10,
                    s -> Ansi.OUT.green("Y 登录错误锁定时间                     \t -->\t " + s + " 秒").ln(),
                    f -> Ansi.OUT.red("N 未检测到 core.login-error-lock-second\t -->\t 已采用默认值 : " + f).ln());
        }
    }

}
