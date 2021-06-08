package cool.scx.cms;

import cool.scx.annotation.ScxTemplateDirective;
import cool.scx.base.BaseTemplateDirective;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.Ansi;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.Version;

import java.io.IOException;

/**
 * ScxCms
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxCms {

    /**
     * Freemarker 默认引擎版本
     */
    private static final Version VERSION = Configuration.VERSION_2_3_31;

    /**
     * Constant <code>freemarkerConfig</code>
     */
    private static Configuration freemarkerConfig;


    private static Configuration initFreemarkerConfig() {
        // freemarker 配置文件版本
        var configuration = new Configuration(VERSION);
        var wrapperBuilder = new DefaultObjectWrapperBuilder(VERSION);
        //暴露 实体类的 fields 因为 此项目中的实体类没有 get set
        wrapperBuilder.setExposeFields(true);
        configuration.setObjectWrapper(wrapperBuilder.build());
        try {
            configuration.setDirectoryForTemplateLoading(ScxConfig.cmsRoot());
        } catch (Exception e) {
            Ansi.OUT.brightRed(ScxConfig.cmsRoot().getPath()).ln();
            Ansi.OUT.brightRed("Cms 模板目录不存在!!! Cms 功能将不可用!!!").ln();
        }

        //设置 字符集
        configuration.setDefaultEncoding("UTF-8");
        //设置 语法 为自动检测
        configuration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
        //自定义的指令就在这里添加

        ScxModuleHandler.iterateClass(clazz -> {
            if (clazz.isAnnotationPresent(ScxTemplateDirective.class) && BaseTemplateDirective.class.isAssignableFrom(clazz)) {
                try {
                    var myDirective = (BaseTemplateDirective) ScxContext.getBean(clazz);
                    Ansi.OUT.blue("已加载自定义 Freemarker 标签 [" + myDirective._DirectiveName() + "] Class -> " + clazz.getName()).ln();
                    configuration.setSharedVariable(myDirective._DirectiveName(), myDirective);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        });
        return configuration;
    }


    /**
     * 初始化 cms 配置文件
     */
    public static void initCms() {
        Ansi.OUT.blue("ScxCms 初始化中...").ln();
        freemarkerConfig = initFreemarkerConfig();
        Ansi.OUT.blue("ScxCms 初始化完成...").ln();
    }

    /**
     * <p>getTemplateByPath.</p>
     *
     * @param pagePath a {@link java.lang.String} object.
     * @return a {@link freemarker.template.Template} object.
     */
    public static Template getTemplateByPath(String pagePath) {
        try {
            return freemarkerConfig.getTemplate(pagePath + ScxConfig.cmsTemplateSuffix());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
