package cool.scx.config;

import cool.scx.base.service.BaseTemplateDirective;
import cool.scx.enumeration.Color;
import cool.scx.enumeration.ScanPackageVisitResult;
import cool.scx.util.PackageUtils;
import cool.scx.util.StringUtils;
import freemarker.template.Configuration;

/**
 * <p>ScxCmsConfig class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxCmsConfig {
    /**
     * Constant <code>freemarkerConfig</code>
     */
    public static final Configuration freemarkerConfig;

    static {
        freemarkerConfig = initFreemarkerConfig();
    }

    private static Configuration initFreemarkerConfig() {
        // freemarker 配置文件版本
        var configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        try {
            configuration.setDirectoryForTemplateLoading(ScxConfig.cmsRoot);
        } catch (Exception e) {
            System.err.println(ScxConfig.cmsRoot.getPath());
            System.err.println("Cms 模板目录不存在!!! Cms 功能将不可用!!!");
        }

        //设置 字符集
        configuration.setDefaultEncoding("UTF-8");
        //设置 语法 为自动检测
        configuration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
        //自定义的指令就在这里添加

        PackageUtils.scanPackageIncludePlugins(clazz -> {
            if (!clazz.isInterface() && BaseTemplateDirective.class.isAssignableFrom(clazz)) {
                try {
                    var myDirective = (BaseTemplateDirective) clazz.getDeclaredConstructor().newInstance();
                    StringUtils.println("已加载自定义 Freemarker 标签 [" + myDirective.getVariable() + "] Class -> " + clazz.getName(), Color.BLUE);
                    configuration.setSharedVariable(myDirective.getVariable(), myDirective);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ScanPackageVisitResult.CONTINUE;
        });
        return configuration;
    }

    /**
     * <p>init.</p>
     */
    public static void init() {

    }
}
