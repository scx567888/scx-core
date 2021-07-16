package cool.scx._ext.upload;

import cool.scx.BaseModule;

/**
 * 提供基本的文件上传及下载 (展示)的功能
 *
 * @author scx567888
 * @version 1.0.10
 */
public class UploadModule implements BaseModule {

    /**
     * {@inheritDoc}
     * <p>
     * start
     */
    @Override
    public void start() {
        UploadConfig.initConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String appKey() {
        return "H8QS91GcuNGP9735";
    }

}
