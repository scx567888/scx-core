package cool.scx._ext.upload;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;

import java.io.File;

/**
 * 核心模块配置文件
 *
 * @author scx567888
 * @version 1.1.2
 */
public class UploadConfig {

    private static BaseEasyToUse baseEasyToUse;

    /**
     * <p>initConfig.</p>
     */
    public static void initConfig() {
        baseEasyToUse = new BaseEasyToUse();
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

        BaseEasyToUse() {

            String tempUploadFilePath = ScxConfig.get("core.base.upload-file-path", "AppRoot:/ScxUploadFiles/",
                    s -> Ansi.out().magenta("Y 文件上传目录                         \t -->\t " + FileUtils.getFileByAppRoot(s)).ln(),
                    f -> Ansi.out().red("N 未检测到 core.base.upload-file-path  \t -->\t 已采用默认值 : " + FileUtils.getFileByAppRoot(f)).ln());

            uploadFilePath = FileUtils.getFileByAppRoot(tempUploadFilePath);

        }
    }

}
