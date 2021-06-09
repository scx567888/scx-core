package cool.scx._core._base;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;

import java.io.File;

/**
 * 核心模块配置文件
 *
 * @author 司昌旭
 * @version 1.1.2
 */
public class BaseConfig {

    private static BaseEasyToUse baseEasyToUse;

    /**
     * <p>initConfig.</p>
     */
    public static void initConfig() {
        Ansi.OUT.magenta("BaseConfig 初始化中...").ln();
        baseEasyToUse = new BaseEasyToUse();
        Ansi.OUT.magenta("BaseConfig 初始化完成...").ln();
    }

    /**
     * <p>uploadFilePath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File uploadFilePath() {
        return baseEasyToUse.uploadFilePath;
    }

    static class BaseEasyToUse {

        /**
         * 文件上传路径
         */
        final File uploadFilePath;

        public BaseEasyToUse() {

            String tempUploadFilePath = ScxConfig.get("core.base.upload-file-path", "AppRoot:/ScxUploadFiles/",
                    s -> Ansi.OUT.magenta("Y 文件上传目录                         \t -->\t " + FileUtils.getFileByAppRoot(s)).ln(),
                    f -> Ansi.OUT.red("N 未检测到 core.base.upload-file-path  \t -->\t 已采用默认值 : " + FileUtils.getFileByAppRoot(f)).ln());

            uploadFilePath = FileUtils.getFileByAppRoot(tempUploadFilePath);

        }
    }

}
