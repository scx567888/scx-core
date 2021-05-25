package cool.scx._core;

import cool.scx._core.config.CoreConfig;
import cool.scx.base.BaseModule;
import cool.scx.boot.ScxApp;

import java.util.Map;

/**
 * 核心模块启动类
 * 提供功能 : [ 基本认证逻辑, 通用 crud , 测试 website ,基本license 校验 , 基本文件上传]
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class CoreModule implements BaseModule {

    /**
     * 核心启动方法
     *
     * @param args 外部参数
     */
    public static void main(String[] args) {
        ScxApp.run(new CoreModule(), args);
    }

    @Override
    public void onStart(Map<String, Object> configMap) {
        CoreConfig.initConfig(configMap);
    }
}
